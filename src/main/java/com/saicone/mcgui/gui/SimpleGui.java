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
package com.saicone.mcgui.gui;

import com.saicone.mcgui.session.GuiSession;
import com.saicone.mcgui.item.GuiItem;
import com.saicone.mcgui.util.InventoryCreator;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public abstract class SimpleGui extends AbstractGui {

    public SimpleGui() {
        super(InventoryType.CHEST);
    }

    public SimpleGui(@NotNull Map<Integer, GuiItem> items, @NotNull Set<Flag> flags) {
        super(InventoryType.CHEST, items, flags);
    }

    public abstract int getSize(@NotNull GuiSession session);

    @Override
    public @NotNull Inventory createInventory(@NotNull GuiSession session, @Nullable Component title) {
        final int rows = (getSize(session) - 1) / 9 + 1;
        return InventoryCreator.create(session, Math.min(rows * 9, 6 * 9), title);
    }
}
