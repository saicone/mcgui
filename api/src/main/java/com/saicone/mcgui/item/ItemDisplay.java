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

package com.saicone.mcgui.item;

import com.saicone.mcgui.item.impl.ItemDisplayBuilderImpl;
import com.saicone.mcgui.session.GuiSession;
import com.saicone.mcgui.text.TextDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ItemDisplay {

    @NotNull
    static Builder builder(@NotNull Material material) {
        return builder(new ItemStack(material));
    }

    @NotNull
    static Builder builder(@NotNull ItemStack item) {
        return new ItemDisplayBuilderImpl(item);
    }

    @NotNull
    ItemStack get(@NotNull GuiSession session);

    @NotNull
    ItemStack get(@NotNull GuiSession session, @NotNull ItemStack item);

    @NotNull
    Builder toBuilder();

    @NotNull
    default GuiItem toGuiItem() {
        return (session, slot) -> get(session);
    }

    @NotNull
    default GuiItem toGuiItem(@NotNull BiConsumer<GuiSession, InventoryClickEvent> action) {
        return new GuiItem() {
            @Override
            public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
                return get(session);
            }

            @Override
            public void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
                action.accept(session, event);
            }
        };
    }

    interface Builder {

        @NotNull
        Builder amount(@Nullable Integer amount);

        @NotNull
        default Builder text(@NotNull String text) {
            return text(TextDisplay.mini(text));
        }

        @NotNull
        default Builder text(@NotNull String... text) {
            return text(TextDisplay.mini(text));
        }

        @NotNull
        default Builder text(@NotNull Component text) {
            return text(TextDisplay.miniComponent(text));
        }

        @NotNull
        default Builder text(@NotNull Component... text) {
            return text(TextDisplay.miniComponent(text));
        }

        @NotNull
        Builder text(@Nullable TextDisplay text);

        @NotNull
        default Builder name(@NotNull String name) {
            return name(TextDisplay.mini(name));
        }

        @NotNull
        default Builder name(@NotNull Component name) {
            return name(TextDisplay.miniComponent(name));
        }

        @NotNull
        Builder name(@Nullable TextDisplay name);

        @NotNull
        default Builder lore(@NotNull String... lore) {
            return lore(TextDisplay.mini(lore));
        }

        @NotNull
        default Builder lore(@NotNull Component... lore) {
            return lore(TextDisplay.miniComponent(lore));
        }

        @NotNull
        Builder lore(@Nullable TextDisplay lore);

        @NotNull
        Builder glowing(@Nullable Boolean glowing);

        @NotNull
        Builder model(@Nullable Model model);

        @NotNull
        Builder model(@NotNull Consumer<Model> consumer);

        @NotNull
        Builder tooltip(@Nullable Tooltip tooltip);

        @NotNull
        Builder tooltip(@NotNull Consumer<Tooltip> consumer);

        @NotNull
        ItemDisplay build();
    }

    class Model {

        private Integer data;

        private Key model;

        private List<Float> floats;
        private List<Boolean> flags;
        private List<String> strings;
        private List<Color> colors;

        public boolean containsCustomModelData() {
            return data != null || floats != null || flags != null || strings != null || colors != null;
        }

        @UnknownNullability
        public Integer data() {
            return data;
        }

        @UnknownNullability
        public Key model() {
            return model;
        }

        @UnknownNullability
        public List<Float> floats() {
            return floats;
        }

        @UnknownNullability
        public List<Boolean> flags() {
            return flags;
        }

        @UnknownNullability
        public List<String> strings() {
            return strings;
        }

        @UnknownNullability
        public List<Color> colors() {
            return colors;
        }

        @NotNull
        @Contract("_ -> this")
        public Model data(@Nullable Integer data) {
            this.data = data;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Model model(@Nullable Key model) {
            this.model = model;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Model floats(@Nullable List<Float> floats) {
            this.floats = floats;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Model flags(@Nullable List<Boolean> flags) {
            this.flags = flags;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Model strings(@Nullable List<String> strings) {
            this.strings = strings;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Model colors(@Nullable List<Color> colors) {
            this.colors = colors;
            return this;
        }

        @Override
        public @NotNull Model clone() {
            try {
                final Model clone = (Model) super.clone();

                // non-shallow copy fix
                if (floats != null) {
                    clone.floats = new ArrayList<>(floats);
                }
                if (flags != null) {
                    clone.flags = new ArrayList<>(flags);
                }
                if (strings != null) {
                    clone.strings = new ArrayList<>(strings);
                }
                if (colors != null) {
                    clone.colors = new ArrayList<>(colors);
                }

                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Tooltip {

        private List<ItemFlag> flags;

        private Key style;

        private Boolean hide;
        private List<Key> hidden;

        @UnknownNullability
        public List<ItemFlag> flags() {
            return flags;
        }

        @UnknownNullability
        public Key style() {
            return style;
        }

        @UnknownNullability
        public Boolean hide() {
            return hide;
        }

        @UnknownNullability
        public List<Key> hidden() {
            return hidden;
        }

        @NotNull
        @Contract("_ -> this")
        public Tooltip flags(@Nullable List<ItemFlag> flags) {
            this.flags = flags;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Tooltip style(@Nullable Key style) {
            this.style = style;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Tooltip hide(@Nullable Boolean hide) {
            this.hide = hide;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Tooltip hidden(@Nullable List<Key> hidden) {
            this.hidden = hidden;
            return this;
        }

        @Override
        public @NotNull Tooltip clone() {
            try {
                final Tooltip clone = (Tooltip) super.clone();

                // non-shallow copy fix
                if (flags != null) {
                    clone.flags = new ArrayList<>(flags);
                }
                if (hidden != null) {
                    clone.hidden = new ArrayList<>(hidden);
                }

                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
