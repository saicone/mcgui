/*
 *  MIT License.
 *
 *  Copyright (c) 2025-2026 Rubenicos
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.saicone.bukkit.module.gui;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.jar.JarFile;

public class GuiSession implements InventoryHolder {

    public static Plugin PLUGIN;
    private static boolean INIT;
    private static final BiFunction<GuiSession, String, Component> PARSER = (session, string) -> {
        return MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(session.agent, string));
    };

    @NotNull
    private static Plugin plugin() {
        if (!INIT && PLUGIN == null) {
            INIT = true;
            try {
                final CodeSource codeSource = GuiSession.class.getProtectionDomain().getCodeSource();
                final File file = new File(codeSource.getLocation().toURI());
                try (JarFile jar = new JarFile(file)) {
                    final InputStream input = jar.getInputStream(jar.stream().filter(entry -> entry.getName().equals("plugin.yml")).findFirst().orElseThrow());
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                        final String pluginName = reader.lines()
                                .filter(line -> line.startsWith("name:"))
                                .map(line -> line.substring("name:".length()).split("#", 2)[0].trim())
                                .findFirst()
                                .orElseThrow();
                        PLUGIN = Bukkit.getPluginManager().getPlugin(pluginName);
                    }
                }
            } catch (Throwable ignored) { }
        }
        if (PLUGIN == null) {
            throw new IllegalStateException("GuiSession doesn't have a declared plugin, make sure to use 'GuiSession.PLUGIN = this' on your plugin initialization");
        }
        return PLUGIN;
    }

    private static void run(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(plugin(), runnable);
        }
    }

    private final Player agent;

    private volatile GuiView view;

    private Gui last;
    private Gui gui;
    private Gui.Metadata meta;
    private Inventory inventory;

    public GuiSession(@NotNull Player agent) {
        this.agent = agent;
    }

    @NotNull
    public Player getAgent() {
        return agent;
    }

    @NotNull
    public GuiView getView() {
        if (view == null) {
            synchronized (this) {
                if (view == null) {
                    view = new GuiView(agent.getOpenInventory());
                }
            }
        }
        return view;
    }

    @NotNull
    public Optional<Gui> getLast() {
        return Optional.ofNullable(last);
    }

    @NotNull
    public Gui getGui() {
        return gui;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends Gui.Metadata> T getMeta() {
        return (T) meta;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    @Nullable
    @Contract("!null -> !null")
    public Component parse(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return PARSER.apply(this, string);
    }

    @NotNull
    public List<Component> parse(@NotNull List<String> list) {
        final List<Component> result = new ArrayList<>();
        for (String element : list) {
            result.add(parse(element));
        }
        return result;
    }

    public void rotate(@NotNull Gui gui) {
        this.last = this.gui;
        this.gui = gui;
        this.meta = gui.createMetadata(this);
        this.inventory = gui.createInventory(this);
    }

    public void open() {
        if (this.inventory != null) {
            run(() -> this.agent.openInventory(this.inventory));
        }
    }

    public void close() {
        close(true);
    }

    public void close(boolean send) {
        if (send) {
            agent.closeInventory();
        } else if (getView().getTopInventory().getHolder() == this) {
            if (gui instanceof AbstractGui) {
                ((AbstractGui) gui).execute(this, new InventoryCloseEvent(agent.getOpenInventory()));
            }
        }
    }

    public void silentClose() {
        meta.setSilentClose(true);
        agent.closeInventory();
    }

    public void update() {
        gui.update(this);
    }

    public void updateTitle() {
        gui.updateTitle(this);
    }

    public void updateInventory(@NotNull Inventory inventory) {
        run(() -> {
            final Inventory previous = this.inventory;
            this.inventory = inventory;
            for (HumanEntity viewer : new ArrayList<>(previous.getViewers())) {
                viewer.openInventory(this.inventory);
            }
        });
    }

    public void updateSlots(int... slots) {
        gui.updateSlots(this, slots);
    }

    public void updateItems(char... ids) {
        if (gui instanceof LayoutGui) {
            ((LayoutGui) gui).updateItems(this, ids);
        }
    }

    public void execute(@NotNull InventoryCloseEvent event) {
        if (meta.isSilentClose()) {
            meta.setSilentClose(false);
            return;
        }
        if (gui instanceof AbstractGui) {
            ((AbstractGui) gui).execute(this, event);
        }
    }

    public void execute(@NotNull InventoryClickEvent event) {
        if (gui instanceof AbstractGui) {
            ((AbstractGui) gui).execute(this, event);
        }
    }

    public void execute(@NotNull InventoryDragEvent event) {
        if (gui instanceof AbstractGui) {
            ((AbstractGui) gui).execute(this, event);
        }
    }
}
