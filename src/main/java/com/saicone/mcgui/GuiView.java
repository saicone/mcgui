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
package com.saicone.mcgui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

@SuppressWarnings("all")
public class GuiView {

    private final InventoryView view;

    public GuiView(@NotNull InventoryView view) {
        this.view = view;
    }

    private static final MethodHandle getTopInventory = method(Inventory.class, "getTopInventory");

    /**
     * {@link InventoryView#getTopInventory()}
     */
    @NotNull
    public Inventory getTopInventory() {
        try {
            return (Inventory) getTopInventory.invokeExact(this.view);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getBottomInventory = method(Inventory.class, "getBottomInventory");

    /**
     * {@link InventoryView#getBottomInventory()}
     */
    @NotNull
    public Inventory getBottomInventory() {
        try {
            return (Inventory) getBottomInventory.invokeExact(this.view);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getPlayer = method(HumanEntity.class, "getPlayer");

    /**
     * {@link InventoryView#getPlayer()}
     */
    @NotNull
    public HumanEntity getPlayer() {
        try {
            return (HumanEntity) getPlayer.invokeExact(this.view);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getType = method(InventoryType.class, "getType");

    /**
     * {@link InventoryView#getType()}
     */
    @NotNull
    public InventoryType getType() {
        try {
            return (InventoryType) getType.invokeExact(this.view);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle setItem = method(void.class, "setItem", int.class, ItemStack.class);

    /**
     * {@link InventoryView#setItem(int, ItemStack)}
     */
    public void setItem(int slot, @Nullable ItemStack item) {
        try {
            setItem.invokeExact(this.view, slot, item);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getItem = method(ItemStack.class, "getItem", int.class);

    /**
     * {@link InventoryView#getItem(int)}
     */
    @Nullable
    public ItemStack getItem(int slot) {
        try {
            return (ItemStack) getItem.invokeExact(this.view, slot);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle setCursor = method(void.class, "setCursor", ItemStack.class);

    /**
     * {@link InventoryView#setCursor(ItemStack)}
     */
    public void setCursor(@Nullable ItemStack item) {
        try {
            setCursor.invokeExact(this.view, item);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getCursor = method(ItemStack.class, "getCursor");

    /**
     * {@link InventoryView#getCursor()}
     */
    @Nullable
    public ItemStack getCursor() {
        try {
            return (ItemStack) getCursor.invokeExact(this.view);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getInventory = method(Inventory.class, "getInventory", int.class);

    /**
     * {@link InventoryView#getInventory(int)}
     */
    @Nullable
    public Inventory getInventory(int rawSlot) {
        try {
            return (Inventory) getInventory.invokeExact(this.view, rawSlot);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle convertSlot = method(int.class, "convertSlot", int.class);

    /**
     * {@link InventoryView#convertSlot(int)}
     */
    public int convertSlot(int rawSlot) {
        try {
            return (int) convertSlot.invokeExact(this.view, rawSlot);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getSlotType = method(InventoryType.SlotType.class, "getSlotType", int.class);

    /**
     * {@link InventoryView#getSlotType(int)}
     */
    @NotNull
    public InventoryType.SlotType getSlotType(int slot) {
        try {
            return (InventoryType.SlotType) getSlotType.invokeExact(this.view, slot);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle open = method(void.class, "open");

    /**
     * {@link InventoryView#open()}
     */
    public void open() {
        try {
            open.invokeExact(this.view);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle close = method(void.class, "close");

    /**
     * {@link InventoryView#close()}
     */
    public void close() {
        try {
            close.invokeExact(this.view);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle countSlots = method(int.class, "countSlots");

    /**
     * {@link InventoryView#countSlots()}
     */
    public int countSlots() {
        try {
            return (int) countSlots.invokeExact(this.view);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle getProperty = method(int.class, "getProperty", InventoryView.Property.class);

    /**
     * {@link InventoryView#getProperty(InventoryView.Property)}
     */
    public boolean setProperty(@NotNull InventoryView.Property prop, int value) {
        try {
            return (boolean) getProperty.invokeExact(this.view, prop, value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static final MethodHandle title = method(Component.class, "title");

    /**
     * {@link InventoryView#title()}
     */
    @NotNull
    public Component title() {
        if (title != null) {
            try {
                return (Component) title.invokeExact(this.view);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            return LegacyComponentSerializer.legacySection().deserialize(this.getTitle());
        }
    }

    private static final MethodHandle getTitle = method(String.class, "getTitle");

    /**
     * {@link InventoryView#getTitle()}
     */
    @NotNull
    public String getTitle() {
        try {
            return (String) getTitle.invokeExact(this.view);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private static MethodHandle method(@NotNull Class<?> returnType, @NotNull String name, @NotNull Class<?>... parameterTypes) {
        final MethodType methodType;
        switch (parameterTypes.length) {
            case 0:
                methodType = MethodType.methodType(returnType);
                break;
            case 1:
                methodType = MethodType.methodType(returnType, parameterTypes[0]);
                break;
            default:
                methodType = MethodType.methodType(returnType, parameterTypes[0], Arrays.copyOfRange(parameterTypes, 1, parameterTypes.length));
                break;
        }
        try {
            return MethodHandles.lookup().findVirtual(InventoryView.class, name, methodType);
        } catch (Throwable ignored) { }
        return null;
    }
}
