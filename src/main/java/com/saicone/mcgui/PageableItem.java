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

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PageableItem<E> implements GuiItem {

    private final char id;

    protected PageableItem(char id) {
        this.id = id;
    }

    public char getId() {
        return id;
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
            session.<PageableGui.Metadata>getMeta().setPage(page -> page + 1);
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
            session.<PageableGui.Metadata>getMeta().setPage(page -> page - 1);
            session.update();
        });
        return VariantItem.valueOf(session -> session.<PageableGui.Metadata>getMeta().getPage() > 0, itemA, inactive);
    }

    public boolean hasNext(@NotNull GuiSession session) {
        final PageableGui.Metadata metadata = session.getMeta();

        return metadata.getItemList(this).size() > metadata.getAmount(getId()) * (metadata.getPage() + 1);
    }

    @Override
    public @NotNull ItemStack display(@NotNull GuiSession session, int slot) {
        final PageableGui.Metadata metadata = session.getMeta();

        int index = metadata.getIndex(slot);
        if (index < 0) {
            return Static.EMPTY_ITEM;
        }

        index += metadata.getAmount(getId()) * metadata.getPage();
        final List<E> list = metadata.getItemList(this);
        if (index >= list.size()) {
            return Static.EMPTY_ITEM;
        }

        final E element = list.get(index);
        return display(session, slot, element);
    }

    @NotNull
    public abstract ItemStack display(@NotNull GuiSession session, int slot, @NotNull E element);

    @Override
    public void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
        final PageableGui.Metadata metadata = session.getMeta();

        int index = metadata.getIndex(event.getSlot());
        if (index < 0) {
            return;
        }

        index += metadata.getAmount(getId()) * metadata.getPage();
        final List<E> list = metadata.getItemList(this);
        if (index >= list.size()) {
            return;
        }

        final E element = list.get(index);
        onClick(session, event, element);
    }

    public void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event, @NotNull E element) {
        // empty default method
    }

    @NotNull
    public List<E> currentList(@NotNull GuiSession session) {
        final PageableGui.Metadata metadata = session.getMeta();

        final int amount = metadata.getAmount(getId());
        final int fromIndex = amount * metadata.getPage();

        final List<E> list = metadata.getItemList(this);
        if (fromIndex >= list.size()) {
            return List.of();
        }

        final int toIndex = Math.min(list.size(), fromIndex + amount);

        return list.subList(fromIndex, toIndex);
    }
}
