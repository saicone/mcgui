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

package com.saicone.mcgui.session;

import com.saicone.mcgui.gui.Gui;
import com.saicone.mcgui.util.Lazy;
import com.saicone.mcgui.util.PluginSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GuiHolder {

    private static final Lazy<Boolean> MULTITHREADING = Lazy.init(() -> {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (Throwable t) {
            return false;
        }
    });

    protected Gui gui;
    protected Gui.Metadata meta;
    protected Inventory inventory;

    public GuiHolder(@NotNull Gui gui, @NotNull Gui.Metadata meta, @NotNull Inventory inventory) {
        this.gui = gui;
        this.meta = meta;
        this.inventory = inventory;
    }

    @ApiStatus.Internal
    public boolean isPersistent() {
        return gui().has(Gui.Flag.PERSISTENT_HOLDER);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends Gui> T gui() {
        return (T) gui;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends Gui.Metadata> T meta() {
        return (T) meta;
    }

    @NotNull
    public Inventory inventory() {
        return inventory;
    }

    @ApiStatus.Internal
    public void setInventory(@NotNull Inventory inventory) {
        final Inventory previous = this.inventory;
        this.inventory = inventory;

        // Update title for all viewers
        if (previous != null) {
            for (HumanEntity viewer : new ArrayList<>(previous.getViewers())) {
                openInventory(viewer, this.inventory);
            }
        }
    }

    public void open(@NotNull HumanEntity viewer) {
        if (this.inventory != null) {
            openInventory(viewer, this.inventory);
        }
    }

    protected void openInventory(@NotNull HumanEntity viewer, @NotNull Inventory inventory) {
        if (MULTITHREADING.get()) {
            viewer.getScheduler().run(PluginSource.unchecked(), task -> viewer.openInventory(inventory), null);
        } else if (Bukkit.isPrimaryThread()) {
            viewer.openInventory(inventory);
        } else {
            Bukkit.getScheduler().runTask(PluginSource.unchecked(), () -> viewer.openInventory(inventory));
        }
    }

    public void close() {
        if (this.inventory != null) {
            for (HumanEntity viewer : new ArrayList<>(this.inventory.getViewers())) {
                viewer.closeInventory();
            }
        }
    }
}
