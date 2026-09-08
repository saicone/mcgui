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
package com.saicone.mcgui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PageableItem<E> implements GuiItem {

    private final char id;
    private final boolean independent;
    private final PageGrid grid;

    public PageableItem(char id) {
        this(id, false);
    }

    public PageableItem(char id, boolean independent) {
        this(id, independent, PageGrid.DEFAULT);
    }

    public PageableItem(char id, @NotNull PageGrid grid) {
        this(id, false, grid);
    }

    public PageableItem(char id, boolean independent, @NotNull PageGrid grid) {
        this.id = id;
        this.independent = independent;
        this.grid = grid;
    }

    public boolean isIndependent() {
        return independent;
    }

    public char getId() {
        return id;
    }

    @NotNull
    public PageGrid getGrid() {
        return grid;
    }

    @NotNull
    public abstract List<E> createList(@NotNull GuiSession session);

    @NotNull
    public GuiItem createNextPage(@NotNull GuiItem item) {
        return createNextPage(item, item);
    }

    @NotNull
    public GuiItem createNextPage(@NotNull GuiItem active, @NotNull GuiItem inactive) {
        final GuiItem itemA = active.executes((session, event) -> {
            session.<PageableGui.Metadata>getMeta().setPage(this, page -> page + 1);
            session.update();
        });
        return VariantItem.valueOf(PageableItem.this::hasNext, itemA, inactive);
    }

    @NotNull
    public GuiItem createPreviousPage(@NotNull GuiItem item) {
        return createPreviousPage(item, item);
    }

    @NotNull
    public GuiItem createPreviousPage(@NotNull GuiItem active, @NotNull GuiItem inactive) {
        final GuiItem itemA = active.executes((session, event) -> {
            session.<PageableGui.Metadata>getMeta().setPage(this, page -> page - 1);
            session.update();
        });
        return VariantItem.valueOf(session -> session.<PageableGui.Metadata>getMeta().getPage(this) > 0, itemA, inactive);
    }

    public boolean hasNext(@NotNull GuiSession session) {
        final PageableGui.Metadata metadata = session.getMeta();

        return metadata.getItemList(this).size() > metadata.getAmount(getId()) * (metadata.getPage(this) + 1);
    }

    @Override
    public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
        final E element = getGrid().element(this, session, slot);
        if (element == null) {
            return Static.EMPTY_ITEM;
        }
        return display(session, slot, element);
    }

    @NotNull
    public abstract ItemStack display(@NotNull GuiSession session, int slot, @NotNull E element);

    @Override
    public void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
        final E element = getGrid().element(this, session, event.getSlot());
        if (element == null) {
            return;
        }
        onClick(session, event, element);
    }

    public void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event, @NotNull E element) {
        // empty default method
    }

    @NotNull
    public List<E> currentList(@NotNull GuiSession session) {
        return getGrid().subList(this, session);
    }
}
