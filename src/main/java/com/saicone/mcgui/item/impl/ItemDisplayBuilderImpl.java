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

package com.saicone.mcgui.item.impl;

import com.saicone.mcgui.item.ItemDisplay;
import com.saicone.mcgui.text.TextDisplay;
import com.saicone.mcgui.util.Lazy;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemDisplayBuilderImpl implements ItemDisplay.Builder {

    private static final Lazy<Boolean> USE_COMPONENT_API = Lazy.init(() -> {
        try {
            Class.forName("io.papermc.paper.datacomponent.DataComponentTypes");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    });

    protected final ItemStack item;
    protected Integer amount;
    protected TextDisplay text;
    protected TextDisplay name;
    protected TextDisplay lore;
    protected Boolean glowing;
    protected ItemDisplay.Model model;
    protected ItemDisplay.Tooltip tooltip;

    public ItemDisplayBuilderImpl(@NotNull ItemStack item) {
        this.item = item;
    }

    @Override
    public @NotNull ItemDisplay.Builder amount(@Nullable Integer amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public @NotNull ItemDisplay.Builder text(@Nullable TextDisplay text) {
        this.text = text;
        return this;
    }

    @Override
    public @NotNull ItemDisplay.Builder name(@Nullable TextDisplay name) {
        this.name = name;
        return this;
    }

    @Override
    public @NotNull ItemDisplay.Builder lore(@Nullable TextDisplay lore) {
        this.lore = lore;
        return this;
    }

    @Override
    public @NotNull ItemDisplay.Builder glowing(@Nullable Boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    @Override
    public @NotNull ItemDisplay.Builder model(ItemDisplay.@Nullable Model model) {
        this.model = model;
        return this;
    }

    @Override
    public @NotNull ItemDisplay.Builder model(@NotNull Consumer<ItemDisplay.Model> consumer) {
        if (model == null) {
            model = new ItemDisplay.Model();
        }
        consumer.accept(model);
        return this;
    }

    @Override
    public @NotNull ItemDisplay.Builder tooltip(ItemDisplay.@Nullable Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public @NotNull ItemDisplay.Builder tooltip(@NotNull Consumer<ItemDisplay.Tooltip> consumer) {
        if (tooltip == null) {
            tooltip = new ItemDisplay.Tooltip();
        }
        consumer.accept(tooltip);
        return this;
    }

    @Override
    public @NotNull ItemDisplay build() {
        if (USE_COMPONENT_API.get()) {
            return new ComponentItemDisplay(item, amount, text, name, lore, glowing, model != null ? model.clone() : null, tooltip != null ? tooltip.clone() : null);
        } else {
            return new MetaItemDisplay(item, amount, text, name, lore, glowing, model != null ? model.clone() : null, tooltip != null ? tooltip.clone() : null);
        }
    }
}
