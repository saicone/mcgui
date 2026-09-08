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
package com.saicone.mcgui.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class InventoryCreator {

    private static final boolean ADVENTURE_SUPPORT;
    static {
        boolean adventureSupport = false;
        try {
            Bukkit.class.getDeclaredMethod("createInventory", InventoryHolder.class, int.class, Component.class);
            adventureSupport = true;
        } catch (Throwable ignored) { }
        ADVENTURE_SUPPORT = adventureSupport;
    }

    InventoryCreator() {
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public static Inventory create(@Nullable InventoryHolder owner, int size, @Nullable Component title) {
        if (title == null) {
            return Bukkit.createInventory(owner, size);
        } else if (ADVENTURE_SUPPORT) {
            return Bukkit.createInventory(owner, size, title);
        } else {
            return Bukkit.createInventory(owner, size, LegacyComponentSerializer.legacySection().serialize(title));
        }
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public static Inventory create(@Nullable InventoryHolder owner, @NotNull InventoryType type, @Nullable Component title) {
        if (title == null) {
            return Bukkit.createInventory(owner, type);
        } else if (ADVENTURE_SUPPORT) {
            return Bukkit.createInventory(owner, type, title);
        } else {
            return Bukkit.createInventory(owner, type, LegacyComponentSerializer.legacySection().serialize(title));
        }
    }
}
