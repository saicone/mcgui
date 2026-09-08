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
package com.saicone.mcgui.impl;

import com.saicone.mcgui.AbstractGui;
import com.saicone.mcgui.GuiItem;
import com.saicone.mcgui.GuiSession;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class GuiImpl extends AbstractGui {

    private final Function<GuiSession, Component> title;

    private final Consumer<GuiSession> onPreOpen ;
    private final Consumer<GuiSession> onOpen;
    private final BiConsumer<GuiSession, InventoryCloseEvent> onClose;
    private final BiConsumer<GuiSession, InventoryClickEvent> onPreClick;
    private final BiConsumer<GuiSession, InventoryClickEvent> onClick;

    public GuiImpl(
            @NotNull InventoryType type,
            @NotNull Function<GuiSession, Component> title,
            @NotNull Map<Integer, GuiItem> items,
            @Nullable Consumer<GuiSession> onPreOpen,
            @Nullable Consumer<GuiSession> onOpen,
            @Nullable BiConsumer<GuiSession, InventoryCloseEvent> onClose,
            @Nullable BiConsumer<GuiSession, InventoryClickEvent> onPreClick,
            @Nullable BiConsumer<GuiSession, InventoryClickEvent> onClick,
            @NotNull Set<Flag> flags) {
        super(type, items, flags);
        this.title = title;
        this.onPreOpen = onPreOpen;
        this.onOpen = onOpen;
        this.onClose = onClose;
        this.onPreClick = onPreClick;
        this.onClick = onClick;
    }

    @Override
    public @NotNull Component createTitle(@NotNull GuiSession session) {
        return this.title.apply(session);
    }

    @Override
    protected void onPreOpen(@NotNull GuiSession session) {
        if (this.onPreOpen != null) {
            this.onPreOpen.accept(session);
        }
    }

    @Override
    protected void onOpen(@NotNull GuiSession session) {
        if (this.onOpen != null) {
            this.onOpen.accept(session);
        }
    }

    @Override
    protected void onClose(@NotNull GuiSession session, @NotNull InventoryCloseEvent event) {
        if (this.onClose != null) {
            this.onClose.accept(session, event);
        }
    }

    @Override
    protected void onPreClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
        if (this.onPreClick != null) {
            this.onPreClick.accept(session, event);
        }
    }

    @Override
    protected void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
        if (this.onClick != null) {
            this.onClick.accept(session, event);
        }
    }
}
