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

import com.saicone.mcgui.item.GuiItem;
import com.saicone.mcgui.item.PageableGuiItem;
import com.saicone.mcgui.item.VariantGuiItem;
import com.saicone.mcgui.session.GuiSession;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class PageableGui extends LayoutGui {

    @Override
    public @NotNull Metadata createMetadata(@NotNull GuiSession session) {
        return new Metadata(session, this);
    }

    @Override
    protected void updateItem(@NotNull GuiSession session, char id, @NotNull GuiItem item) {
        if (item instanceof PageableGuiItem<?>) {
            meta(session).itemList.remove((PageableGuiItem<?>) item);
        }
        super.updateItem(session, id, item);
    }

    @Override
    public @NotNull Metadata meta(@NotNull GuiSession session) {
        return session.meta();
    }

    protected void nextPageItem(char id, @NotNull GuiItem item) {
        nextPageItem(id, item, item);
    }

    protected void nextPageItem(char id, @NotNull GuiItem active, @NotNull GuiItem inactive) {
        nextPageItem(id, active, inactive, session -> true);
    }

    protected void nextPageItem(char id, @NotNull GuiItem item, int maxPage) {
        nextPageItem(id, item, item, maxPage);
    }

    protected void nextPageItem(char id, @NotNull GuiItem active, @NotNull GuiItem inactive, int maxPage) {
        nextPageItem(id, active, inactive, session -> {
            final PageableGui.Metadata metadata = session.meta();

            return maxPage > metadata.getPage() + 1;
        });
    }

    protected void nextPageItem(char id, @NotNull GuiItem item, @NotNull Predicate<GuiSession> predicate) {
        nextPageItem(id, item, item, predicate);
    }

    protected void nextPageItem(char id, @NotNull GuiItem active, @NotNull GuiItem inactive, @NotNull Predicate<GuiSession> predicate) {
        final GuiItem itemA = active.executes((session, event) -> {
            session.<PageableGui.Metadata>meta().setPage(page -> page + 1);
            session.update();
        });

        item((int) id, VariantGuiItem.valueOf(predicate, itemA, inactive));
    }

    protected void previousPageItem(char id, @NotNull GuiItem item) {
        previousPageItem(id, item, item);
    }

    protected void previousPageItem(char id, @NotNull GuiItem active, @NotNull GuiItem inactive) {
        final GuiItem itemA = active.executes((session, event) -> {
            session.<PageableGui.Metadata>meta().setPage(page -> page - 1);
            session.update();
        });

        item((int) id, VariantGuiItem.valueOf(session -> session.<PageableGui.Metadata>meta().getPage() > 0, itemA, inactive));
    }

    public static class Metadata extends LayoutGui.Metadata {

        int page = 0;
        private final Map<PageableGuiItem<?>, Integer> itemPage = new HashMap<>();
        private final Map<PageableGuiItem<?>, List<?>> itemList = new HashMap<>();

        public Metadata(@NotNull GuiSession session, @NotNull PageableGui gui) {
            super(session, gui);
        }

        public int getPage() {
            return page;
        }

        public int getPage(@NotNull PageableGuiItem<?> item) {
            if (item.isIndependent()) {
                return getItemPage(item);
            } else {
                return getPage();
            }
        }

        private int getItemPage(@NotNull PageableGuiItem<?> item) {
            Integer page = this.itemPage.get(item);
            if (page == null) {
                page = 0;
                this.itemPage.put(item, page);
            }
            return page;
        }

        @NotNull
        @SuppressWarnings("unchecked")
        public <E> List<E> getItemList(@NotNull PageableGuiItem<E> item) {
            List<?> list = itemList.get(item);
            if (list == null) {
                list = item.getGrid().transform(item.createList(getSession()));
                itemList.put(item, list);
            }
            return (List<E>) list;
        }

        public void setPage(int page) {
            this.page = Math.max(0, page);
        }

        public void setPage(@NotNull UnaryOperator<Integer> operator) {
            setPage(operator.apply(getPage()));
        }

        public void setPage(@NotNull PageableGuiItem<?> item, int page) {
            if (item.isIndependent()) {
                setItemPage(item, page);
            } else {
                setPage(page);
            }
        }

        public void setPage(@NotNull PageableGuiItem<?> item, @NotNull UnaryOperator<Integer> operator) {
            if (item.isIndependent()) {
                setItemPage(item, operator.apply(getItemPage(item)));
            } else {
                setPage(operator);
            }
        }

        private void setItemPage(@NotNull PageableGuiItem<?> item, int page) {
            this.itemPage.put(item, Math.max(0, page));
        }
    }
}
