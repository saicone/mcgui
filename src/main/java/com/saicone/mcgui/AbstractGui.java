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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractGui implements Gui {

    protected final InventoryType type;
    protected final Map<Integer, GuiItem> items;
    protected final Set<Gui.Flag> flags;

    public AbstractGui() {
        this(InventoryType.CHEST);
    }

    public AbstractGui(@NotNull InventoryType type) {
        this(type, new HashMap<>(), new HashSet<>());
    }

    public AbstractGui(@NotNull InventoryType type, @NotNull Map<Integer, GuiItem> items, @NotNull Set<Gui.Flag> flags) {
        this.type = type;
        this.items = items;
        this.flags = flags;
    }

    protected void onLoad() {
        // empty default method
    }

    protected void onPreOpen(@NotNull GuiSession session) {
        // empty default method
    }

    protected void onOpen(@NotNull GuiSession session) {
        // empty default method
    }

    protected void onClose(@NotNull GuiSession session, @NotNull InventoryCloseEvent event) {
        // empty default method
    }

    protected void onPreClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
        // empty default method
    }

    protected void onClick(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
        // empty default method
    }

    public boolean has(@NotNull Gui.Flag flag) {
        return flags.contains(flag);
    }

    @Nullable
    protected GuiItem getItem(@NotNull GuiSession session, int slot) {
        return items.get(slot);
    }

    protected void item(char id, @NotNull GuiItem item) {
        item((int) id, item);
    }

    protected void item(int slot, @NotNull GuiItem item) {
        this.items.put(slot, item);
    }

    protected void flags(@NotNull Gui.Flag... flags) {
        Collections.addAll(this.flags, flags);
    }

    @Override
    public void reload() {
        this.items.clear();
        onLoad();
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull GuiSession session, @Nullable Component title) {
        return InventoryCreator.create(session, type, title);
    }

    @Override
    public void open(@NotNull GuiSession session) {
        session.close(false);

        session.rotate(this);
        update(session);

        onPreOpen(session);
        session.open();
        onOpen(session);
    }

    @Override
    public void update(@NotNull GuiSession session) {
        final Metadata meta = meta(session);

        final boolean update;
        final Inventory inventory;
        if (meta.updateInventory()) {
            update = true;
            inventory = createInventory(session, meta.getTitle());
        } else {
            update = false;
            inventory = session.getInventory();
        }

        update(session, inventory);

        if (update) {
            session.updateInventory(inventory);
        }
    }

    protected void update(@NotNull GuiSession session, @NotNull Inventory inventory) {
        for (Map.Entry<Integer, GuiItem> entry : this.items.entrySet()) {
            if (entry.getKey() >= inventory.getSize()) {
                continue;
            }
            inventory.setItem(entry.getKey(), entry.getValue().display(session, entry.getKey()));
        }
    }

    @Override
    public void updateSlots(@NotNull GuiSession session, @NotNull Iterator<Integer> slots) {
        while (slots.hasNext()) {
            final int slot = slots.next();
            if (slot >= session.getInventory().getSize()) {
                continue;
            }
            final GuiItem item = getItem(session, slot);
            if (item == null) {
                continue;
            }
            session.getInventory().setItem(slot, item.display(session, slot));
        }
    }

    public void execute(@NotNull GuiSession session, @NotNull InventoryCloseEvent event) {
        onClose(session, event);
    }

    public void execute(@NotNull GuiSession session, @NotNull InventoryClickEvent event) {
        if (has(Flag.READ_ONLY) && (!(event.getClickedInventory() instanceof PlayerInventory) || event.isShiftClick())) {
            event.setCancelled(true);
        }
        onPreClick(session, event);
        final GuiItem item = getItem(session, event.getRawSlot());
        if (item != null) {
            item.onClick(session, event);
        }
        onClick(session, event);
    }

    public void execute(@NotNull GuiSession session, @NotNull InventoryDragEvent event) {
        if (has(Flag.READ_ONLY) && !(event.getInventory() instanceof PlayerInventory)) {
            event.setCancelled(true);
        }
    }
}
