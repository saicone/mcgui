/*
 * MIT License.
 *
 * Copyright (c) 2026 Rubenicos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.saicone.mcgui.session;

import com.saicone.mcgui.gui.LayoutGui;
import com.saicone.mcgui.gui.AbstractGui;
import com.saicone.mcgui.gui.Gui;
import com.saicone.mcgui.util.PluginSource;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class AbstractGuiSession implements GuiSession {

    public static final BiFunction<com.saicone.mcgui.session.GuiSession, String, Component> PARSER = (session, string) -> {
        return MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(session.getAgent(), string));
    };

    private static void run(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(PluginSource.unchecked(), runnable);
        }
    }

    private final Player agent;

    private volatile GuiView view;

    private Gui last;
    private Gui gui;
    private Gui.Metadata meta;
    private Inventory inventory;

    private final AtomicReference<Consumer<String>> plainTextConsumer = new AtomicReference<>();
    private final AtomicReference<Consumer<Component>> decoratedTextConsumer = new AtomicReference<>();

    public AbstractGuiSession(@NotNull Player agent) {
        this.agent = agent;
    }

    @Override
    public @NonNull Player getAgent() {
        return agent;
    }

    @Override
    public @NonNull GuiView getView() {
        if (view == null) {
            synchronized (this) {
                if (view == null) {
                    view = new GuiView(agent.getOpenInventory());
                }
            }
        }
        return view;
    }

    @Override
    public @NotNull Optional<Gui> getLast() {
        return Optional.ofNullable(last);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T extends Gui> T getGui() {
        return (T) gui;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T extends Gui.Metadata> T getMeta() {
        return (T) meta;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    @Contract("!null -> !null")
    public @Nullable Component parse(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return PARSER.apply(this, string);
    }

    @Override
    public @NotNull List<Component> parse(@NotNull List<String> list) {
        final List<Component> result = new ArrayList<>();
        for (String element : list) {
            result.add(parse(element));
        }
        return result;
    }

    @Override
    public void rotate(@NotNull Gui gui) {
        this.last = this.gui;
        this.gui = gui;
        this.meta = gui.createMetadata(this);
        this.inventory = gui.createInventory(this);
    }

    @Override
    public void open() {
        if (this.inventory != null) {
            run(() -> this.agent.openInventory(this.inventory));
        }
    }

    @Override
    public void close() {
        close(true);
    }

    @Override
    public void close(boolean send) {
        if (send) {
            agent.closeInventory();
        } else if (getView().getTopInventory().getHolder() == this) {
            if (gui instanceof AbstractGui) {
                ((AbstractGui) gui).execute(this, new InventoryCloseEvent(agent.getOpenInventory()));
            }
        }
    }

    @Override
    public void silentClose() {
        meta.setSilentClose(true);
        agent.closeInventory();
    }

    @Override
    public void update() {
        gui.update(this);
    }

    @Override
    public void updateTitle() {
        gui.updateTitle(this);
    }

    @Override
    public void updateInventory(@NotNull Inventory inventory) {
        run(() -> {
            final Inventory previous = this.inventory;
            this.inventory = inventory;
            for (HumanEntity viewer : new ArrayList<>(previous.getViewers())) {
                viewer.openInventory(this.inventory);
            }
        });
    }

    @Override
    public void updateSlots(int... slots) {
        gui.updateSlots(this, slots);
    }

    @Override
    public void updateItems(char... ids) {
        if (gui instanceof LayoutGui) {
            ((LayoutGui) gui).updateItems(this, ids);
        }
    }

    @Override
    public void execute(@NotNull InventoryCloseEvent event) {
        if (meta.isSilentClose()) {
            meta.setSilentClose(false);
            return;
        }
        if (gui instanceof AbstractGui) {
            ((AbstractGui) gui).execute(this, event);
        }
    }

    @Override
    public void execute(@NotNull InventoryClickEvent event) {
        if (gui instanceof AbstractGui) {
            ((AbstractGui) gui).execute(this, event);
        }
    }

    @Override
    public void execute(@NotNull InventoryDragEvent event) {
        if (gui instanceof AbstractGui) {
            ((AbstractGui) gui).execute(this, event);
        }
    }

    @Override
    public void listenPlainChat(@NotNull Consumer<String> consumer) {
        this.plainTextConsumer.set(consumer);
        silentClose();
    }

    @Override
    public void listenDecoratedChat(@NotNull Consumer<Component> consumer) {
        this.decoratedTextConsumer.set(consumer);
        silentClose();
    }

    @Override
    public boolean consumePlainText(@NotNull String input) {
        final Consumer<String> consumer = plainTextConsumer.getAndSet(null);
        if (consumer != null) {
            consumer.accept(input);
            return true;
        }
        return false;
    }

    @Override
    public boolean consumePlainText(@NotNull Component input) {
        final Consumer<String> consumer = plainTextConsumer.getAndSet(null);
        if (consumer != null) {
            consumer.accept(PlainTextComponentSerializer.plainText().serialize(input));
            return true;
        }
        return false;
    }

    @Override
    public boolean consumeDecoratedText(@NotNull String input) {
        final Consumer<Component> consumer = decoratedTextConsumer.getAndSet(null);
        if (consumer != null) {
            consumer.accept(LegacyComponentSerializer.legacySection().deserialize(input));
            return true;
        }
        return false;
    }

    @Override
    public boolean consumeDecoratedText(@NotNull Component input) {
        final Consumer<Component> consumer = decoratedTextConsumer.getAndSet(null);
        if (consumer != null) {
            consumer.accept(input);
            return true;
        }
        return false;
    }
}
