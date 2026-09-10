/*
 * MIT License.
 *
 * Copyright (c) 2025-2026 Rubenicos
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

import com.saicone.mcgui.gui.AbstractGui;
import com.saicone.mcgui.gui.Gui;
import com.saicone.mcgui.gui.LayoutGui;
import com.saicone.mcgui.util.Audiences;
import com.saicone.mcgui.util.PAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Consumer;

public interface GuiSession extends InventoryHolder {

    @NotNull
    Player viewer();

    @Nullable
    Player agent();

    @NotNull
    default Player player() {
        final Player agent = agent();
        if (agent != null) {
            return agent;
        }
        return viewer();
    }

    @NotNull
    default Audience audience() {
        return Audiences.player(viewer());
    }

    @NotNull
    default Audience pointer() {
        return Audiences.player(player());
    }

    @NotNull
    GuiView view();

    @NotNull
    GuiHolder holder();

    @NotNull
    default <T extends Gui> T gui() {
        return holder().gui();
    }

    @NotNull
    default <T extends Gui.Metadata> T meta() {
        return holder().meta();
    }

    @Override
    @NotNull
    @ApiStatus.Internal
    default Inventory getInventory() {
        return holder().inventory();
    }

    @NotNull
    default String parse(@NotNull String str) {
        String result = str;
        if (PAPI.get().isPresent() && str.contains("%")) {
            result = PlaceholderAPI.setPlaceholders(player(), str);
        }
        return result;
    }

    @UnknownNullability
    @Contract("!null -> !null")
    default Component parse(@Nullable Component component) {
        if (component == null) {
            return null;
        }

        if (component instanceof TextComponent) {
            final String content = ((TextComponent) component).content();
            final String parsed = parse(content);
            if (!content.equals(parsed)) {
                component = ((TextComponent) component).content(parsed);
            }
        }

        if (component.children().isEmpty()) {
            return component;
        }

        return component.children(component.children().stream().map(this::parse).toList());
    }

    void push(@NotNull Gui gui);

    default void openCurrentInventory() {
        holder().open(viewer());
    }

    default void close() {
        close(true);
    }

    default void close(boolean send) {
        if (send) {
            viewer().closeInventory();
        } else if (view().getTopInventory().getHolder() == this) {
            if (holder().gui() instanceof AbstractGui gui) {
                gui.execute(this, new InventoryCloseEvent(viewer().getOpenInventory()));
            }
        }
    }

    default void silentClose() {
        silentClose(true);
    }

    default void silentClose(boolean send) {
        holder().meta().setSilentClose(true);
        close(send);
    }

    default void update() {
        holder().gui().update(this);
    }

    default void updateTitle() {
        holder().gui().updateTitle(this);
    }

    default void updateSlots(int... slots) {
        holder().gui().updateSlots(this, slots);
    }

    default void updateItems(char... ids) {
        if (holder().gui() instanceof LayoutGui gui) {
            gui.updateItems(this, ids);
        }
    }

    void listenPlainChat(@NotNull Consumer<String> consumer);

    void listenDecoratedChat(@NotNull Consumer<Component> consumer);

    boolean consumePlainText(@NotNull String input);

    boolean consumePlainText(@NotNull Component input);

    boolean consumeDecoratedText(@NotNull String input);

    boolean consumeDecoratedText(@NotNull Component input);
}
