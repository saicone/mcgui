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
package com.saicone.mcgui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface GuiItem {

    @NotNull
    static GuiItem empty() {
        return Static.EMPTY;
    }

    @NotNull
    static GuiItem valueOf(@NotNull Material display) {
        return valueOf(new ItemStack(display));
    }

    @NotNull
    static GuiItem valueOf(@NotNull Material display, @NotNull Component name) {
        return valueOf(display, name, List.of());
    }

    @NotNull
    static GuiItem valueOf(@NotNull ItemStack item, @NotNull Component name) {
        return valueOf(item, name, List.of());
    }

    @NotNull
    static GuiItem valueOf(@NotNull Material display, @NotNull Component name, @NotNull List<Component> lore) {
        return valueOf(new ItemStack(display), name, lore);
    }

    @NotNull
    static GuiItem valueOf(@NotNull ItemStack item, @NotNull Component name, @NotNull List<Component> lore) {
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(name));
        if (!lore.isEmpty()) {
            final List<String> list = new ArrayList<>();
            for (Component line : lore) {
                list.add(LegacyComponentSerializer.legacySection().serialize(line));
            }
            meta.setLore(list);
        }

        item.setItemMeta(meta);
        return valueOf(item);
    }

    @NotNull
    static GuiItem valueOf(@Nullable ItemStack display) {
        if (display == null || display.getType() == Material.AIR) {
            return empty();
        }
        return new GuiItem() {
            @Override
            public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
                return display;
            }
        };
    }

    @NotNull
    static GuiItem valueOf(@NotNull Function<GuiSession, ItemStack> display) {
        return new GuiItem() {
            @Override
            public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
                return display.apply(session);
            }
        };
    }

    @NotNull
    static GuiItem valueOf(@NotNull BiFunction<GuiSession, Integer, ItemStack> display) {
        return new GuiItem() {
            @Override
            public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
                return display.apply(session, slot);
            }
        };
    }

    @NotNull
    static GuiItem valueOf(@NotNull Material display, @NotNull BiConsumer<GuiSession, InventoryClickEvent> action) {
        return valueOf(new ItemStack(display), action);
    }

    @NotNull
    static GuiItem valueOf(@Nullable ItemStack display, @NotNull BiConsumer<GuiSession, InventoryClickEvent> action) {
        return new GuiItem() {
            @Override
            public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
                return display == null ? Static.EMPTY_ITEM : display;
            }

            @Override
            public void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
                action.accept(session, event);
            }
        };
    }

    @NotNull
    static GuiItem valueOf(@NotNull Function<GuiSession, ItemStack> display, @NotNull BiConsumer<GuiSession, InventoryClickEvent> action) {
        return new GuiItem() {
            @Override
            public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
                return display.apply(session);
            }

            @Override
            public void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
                action.accept(session, event);
            }
        };
    }

    @NotNull
    static GuiItem valueOf(@NotNull BiFunction<GuiSession, Integer, ItemStack> display, @NotNull BiConsumer<GuiSession, InventoryClickEvent> action) {
        return new GuiItem() {
            @Override
            public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
                return display.apply(session, slot);
            }

            @Override
            public void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
                action.accept(session, event);
            }
        };
    }

    @NotNull
    ItemStack display(@NotNull GuiSession session, int slot);

    default void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
        // empty default method
    }

    @NotNull
    default GuiItem executes(@NotNull BiConsumer<GuiSession, InventoryClickEvent> action) {
        return new GuiItem() {
            @Override
            public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
                return GuiItem.this.display(session, slot);
            }

            @Override
            public void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
                action.accept(session, event);
            }
        };
    }

    class Static {

        static final ItemStack EMPTY_ITEM = new ItemStack(Material.AIR);
        private static final GuiItem EMPTY = new GuiItem() {
            @Override
            public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
                return EMPTY_ITEM;
            }
        };
    }
}
