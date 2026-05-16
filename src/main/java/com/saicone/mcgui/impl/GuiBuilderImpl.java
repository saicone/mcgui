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
package com.saicone.mcgui.impl;

import com.saicone.mcgui.Gui;
import com.saicone.mcgui.GuiItem;
import com.saicone.mcgui.GuiSession;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class GuiBuilderImpl implements Gui.Builder<GuiBuilderImpl> {

    private final InventoryType type;

    private Function<GuiSession, Component> title;
    // Layout gui
    private Function<GuiSession, String[]> layout;
    // Simple gui
    private Function<GuiSession, Integer> size;

    // Interactions
    private final Map<Integer, GuiItem> items = new HashMap<>();
    private Consumer<GuiSession> onPreOpen = null;
    private Consumer<GuiSession> onOpen = null;
    private BiConsumer<GuiSession, InventoryCloseEvent> onClose = null;
    private BiConsumer<GuiSession, InventoryClickEvent> onPreClick = null;
    private BiConsumer<GuiSession, InventoryClickEvent> onClick = null;

    // Options
    private final Set<Gui.Flag> flags = new HashSet<>();

    public GuiBuilderImpl() {
        this(InventoryType.CHEST);
    }

    public GuiBuilderImpl(@NotNull InventoryType type) {
        this.type = type;
    }

    @Override
    public @NotNull GuiBuilderImpl title(@NotNull Function<GuiSession, Component> title) {
        this.title = title;
        return this;
    }

    @Override
    public @NotNull GuiBuilderImpl layout(@NotNull Function<GuiSession, String[]> layout) {
        this.layout = layout;
        return this;
    }

    @Override
    public @NotNull GuiBuilderImpl size(@NotNull Function<GuiSession, Integer> size) {
        if (this.type != InventoryType.CHEST) {
            throw new IllegalStateException("Cannot change size of a non-chest inventory");
        }
        this.size = size;
        return this;
    }

    @Override
    public @NotNull GuiBuilderImpl item(int slot, @NotNull GuiItem item) {
        this.items.put(slot, item);
        return this;
    }

    @Override
    public @NotNull GuiBuilderImpl onPreOpen(@NotNull Consumer<GuiSession> onPreOpen) {
        this.onPreOpen = onPreOpen;
        return this;
    }

    @Override
    public @NotNull GuiBuilderImpl onOpen(@NotNull Consumer<GuiSession> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    @Override
    public @NotNull GuiBuilderImpl onClose(@NotNull BiConsumer<GuiSession, InventoryCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }

    @Override
    public @NotNull GuiBuilderImpl onPreClick(@NotNull BiConsumer<GuiSession, InventoryClickEvent> onPreClick) {
        this.onPreClick = onPreClick;
        return this;
    }

    @Override
    public @NotNull GuiBuilderImpl onClick(@NotNull BiConsumer<GuiSession, InventoryClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    public @NotNull GuiBuilderImpl flags(@NotNull Gui.Flag... flags) {
        Collections.addAll(this.flags, flags);
        return this;
    }

    @Override
    public @NotNull Gui build() {
        if (this.layout != null) {
            return new LayoutGuiImpl(this.type, this.title, this.layout, this.items, this.onPreOpen, this.onOpen, this.onClose, this.onPreClick, this.onClick, this.flags);
        } else if (this.size != null) {
            return new SimpleGuiImpl(this.title, this.size, this.items, this.onPreOpen, this.onOpen, this.onClose, this.onPreClick, this.onClick, this.flags);
        } else {
            return new GuiImpl(this.type, this.title, this.items, this.onPreOpen, this.onOpen, this.onClose, this.onPreClick, this.onClick, this.flags);
        }
    }
}