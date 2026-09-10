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
import com.saicone.mcgui.session.GuiSession;
import com.saicone.mcgui.text.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractItemDisplay implements ItemDisplay {

    protected final ItemStack item;
    protected final Integer amount;
    protected final TextDisplay text;
    protected final TextDisplay name;
    protected final TextDisplay lore;
    protected final Boolean glowing;
    protected final ItemDisplay.Model model;
    protected final ItemDisplay.Tooltip tooltip;

    public AbstractItemDisplay(
            @NotNull ItemStack item,
            @Nullable Integer amount,
            @Nullable TextDisplay text,
            @Nullable TextDisplay name,
            @Nullable TextDisplay lore,
            @Nullable Boolean glowing,
            @Nullable Model model,
            @Nullable Tooltip tooltip) {
        this.item = item;
        this.amount = amount;
        this.text = text;
        this.name = name;
        this.lore = lore;
        this.glowing = glowing;
        this.model = model;
        this.tooltip = tooltip;
    }

    @Override
    public @NotNull ItemStack get(@NotNull GuiSession session) {
        return get(session, this.item);
    }

    @Override
    public @NotNull Builder toBuilder() {
        return ItemDisplay.builder(item)
                .amount(amount)
                .text(text)
                .name(name)
                .lore(lore)
                .glowing(glowing)
                .model(model != null ? model.clone() : null)
                .tooltip(tooltip != null ? tooltip.clone() : null);
    }
}
