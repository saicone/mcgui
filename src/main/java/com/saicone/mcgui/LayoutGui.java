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

import com.saicone.mcgui.util.InventoryCreator;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class LayoutGui extends AbstractGui {

    public LayoutGui() {
        super();
    }

    public LayoutGui(@NotNull InventoryType type) {
        super(type);
    }

    public LayoutGui(@NotNull InventoryType type, @NotNull Map<Integer, GuiItem> items, @NotNull Set<Flag> flags) {
        super(type, items, flags);
    }

    @Override
    public @Nullable GuiItem getItem(@NotNull GuiSession session, int slot) {
        return super.getItem(session, meta(session).getId(slot));
    }

    @Override
    public @NotNull LayoutGui.Metadata createMetadata(@NotNull GuiSession session) {
        return new Metadata(session, this);
    }

    @NotNull
    public abstract String[] createLayout(@NotNull GuiSession session);

    @Override
    public @NotNull Inventory createInventory(@NotNull GuiSession session, @Nullable Component title) {
        if (this.type == InventoryType.CHEST) {
            int slots = meta(session).getSlots();
            final int rows = (slots - 1) / 9 + 1;
            return InventoryCreator.create(session, Math.min(rows * 9, 6 * 9), title);
        }
        return super.createInventory(session, title);
    }

    @Override
    protected void update(@NotNull GuiSession session, @NotNull Inventory inventory) {
        final String[] layout = meta(session).getLayout();
        int slot = 0;
        for (String row : layout) {
            for (int column = 0; column < row.length(); column++) {
                final GuiItem item = this.items.get((int) row.charAt(column));
                if (item != null) {
                    inventory.setItem(slot, item.display(session, slot));
                }
                slot++;
            }
        }
    }

    public void updateItems(@NotNull GuiSession session, char... ids) {
        updateItems(session, new Iterator<Character>() {
            private int currentIndex;

            @Override
            public boolean hasNext() {
                return currentIndex < ids.length;
            }

            @Override
            public Character next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return ids[currentIndex++];
            }
        });
    }

    public void updateItems(@NotNull GuiSession session, @NotNull Iterable<Character> ids) {
        updateItems(session, ids.iterator());
    }

    public void updateItems(@NotNull GuiSession session, @NotNull Iterator<Character> ids) {
        while (ids.hasNext()) {
            final char id = ids.next();
            final GuiItem item = this.items.get((int) id);
            if (item == null) {
                continue;
            }
            updateItem(session, id, item);
        }
    }

    protected void updateItem(@NotNull GuiSession session, char id, @NotNull GuiItem item) {
        for (Integer slot : meta(session).getSlots(id)) {
            if (slot >= session.getInventory().getSize()) {
                break;
            }
            session.getInventory().setItem(slot, item.display(session, slot));
        }
    }

    @Override
    public @NotNull LayoutGui.Metadata meta(@NotNull GuiSession session) {
        return session.getMeta();
    }

    public static class Metadata extends Gui.Metadata {

        private String[] layout;
        private Integer slots;
        private List<Integer> indexes;
        private Map<Character, Integer> amounts;

        public Metadata(@NotNull GuiSession session, @NotNull LayoutGui gui) {
            super(session, gui);
        }

        @Override
        public @NotNull LayoutGui getGui() {
            return (LayoutGui) super.getGui();
        }

        @NotNull
        public String[] getLayout() {
            if (this.layout == null) {
                this.updateLayout();
            }
            return this.layout;
        }

        @NotNull
        public String[] getLayout(boolean update) {
            if (update) {
                this.updateLayout();
            }
            return this.layout;
        }

        public int getSlots() {
            if (this.slots == null) {
                this.updateLayout();
            }
            return this.slots;
        }

        @NotNull
        public Iterable<Integer> getSlots(char id) {
            return new Iterable<Integer>() {
                @Override
                public @NotNull Iterator<Integer> iterator() {
                    return new Iterator<Integer>() {

                        private int rowIndex = 0;
                        private int columnIndex = 0;
                        private Integer found;

                        @Override
                        public boolean hasNext() {
                            if (found != null) {
                                return true;
                            }
                            for (; this.rowIndex < Metadata.this.getLayout().length; this.rowIndex++) {
                                final String row = Metadata.this.getLayout()[this.rowIndex];
                                final int column = row.indexOf(id, this.columnIndex);
                                if (column >= 0) {
                                    if (column + 1 >= row.length()) {
                                        this.rowIndex++;
                                        this.columnIndex = 0;
                                    } else {
                                        this.columnIndex = column + 1;
                                    }
                                    this.found = column + this.rowIndex * 9;
                                    return true;
                                } else {
                                    this.columnIndex = 0;
                                }
                            }
                            return false;
                        }

                        @Override
                        public Integer next() {
                            if (found == null) {
                                throw new NoSuchElementException();
                            }
                            final Integer result = found;
                            found = null;
                            return result;
                        }
                    };
                }
            };
        }

        public int getIndex(int slot) {
            return this.getIndexes().get(slot);
        }

        @NotNull
        public List<Integer> getIndexes() {
            if (this.indexes == null) {
                this.updateLayout();
            }
            return this.indexes;
        }

        public int getAmount(char id) {
            return this.getAmounts().get(id);
        }

        @NotNull
        public Map<Character, Integer> getAmounts() {
            if (this.amounts == null) {
                this.updateLayout();
            }
            return this.amounts;
        }

        public char getId(int slot) {
            if (slot < 0) {
                return '\0';
            }
            final int i = slot / 9;
            if (i >= this.getLayout().length) {
                return '\0';
            }
            final String row = this.getLayout()[i];
            final int column = slot % 9;
            if (column >= row.length()) {
                return '\0';
            }
            return row.charAt(column);
        }

        @NotNull
        public Iterable<ItemStack> getItems(char id) {
            return new Iterable<ItemStack>() {
                @Override
                public @NotNull Iterator<ItemStack> iterator() {
                    return new Iterator<ItemStack>() {

                        private final Iterator<Integer> delegate = getSlots(id).iterator();

                        @Override
                        public boolean hasNext() {
                            return delegate.hasNext();
                        }

                        @Override
                        public ItemStack next() {
                            final int index = delegate.next();
                            return Metadata.this.getSession().getInventory().getItem(index);
                        }
                    };
                }
            };
        }

        @Override
        public boolean updateInventory() {
            return super.updateInventory() | updateLayout();
        }

        public boolean updateLayout() {
            final Integer previousSlots = this.slots;
            this.layout = getGui().createLayout(getSession());
            this.slots = 0;
            this.indexes = new ArrayList<>();
            this.amounts = new HashMap<>();
            for (String row : this.layout) {
                this.slots += row.length();
                for (int column = 0; column < row.length(); column++) {
                    final char slot = row.charAt(column);
                    final int index = this.amounts.getOrDefault(slot, 0);
                    this.indexes.add(index);
                    this.amounts.put(slot, index + 1);
                }
            }
            return previousSlots == null || !previousSlots.equals(this.slots);
        }
    }
}
