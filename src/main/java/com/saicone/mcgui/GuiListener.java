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

import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class GuiListener implements Listener {

    public void registerEvents(@NotNull Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatDecorateEvent");
            plugin.getServer().getPluginManager().registerEvents(new PaperEvents(), plugin);
        } catch (ClassNotFoundException e) {
            plugin.getServer().getPluginManager().registerEvents(new BukkitEvents(), plugin);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event) {
        final InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GuiSession) {
            final GuiSession session = (GuiSession) holder;
            session.execute(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrag(InventoryDragEvent event) {
        final InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GuiSession) {
            final GuiSession session = (GuiSession) holder;
            for (int slot : event.getRawSlots()) {
                if (slot < session.getView().getTopInventory().getSize()) {
                    session.execute(event);
                }
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClose(InventoryCloseEvent event) {
        final InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GuiSession) {
            final GuiSession session = (GuiSession) holder;
            session.execute(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent event) {
        Gui.Registry.remove(event.getPlayer());
    }

    @SuppressWarnings("deprecation")
    private static final class BukkitEvents implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        private void onPlainChat(AsyncPlayerChatEvent event) {
            final GuiSession session = Gui.Registry.getOrNull(event.getPlayer());
            if (session != null && session.consumePlainText(event.getMessage())) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onDecoratedChat(AsyncPlayerChatEvent event) {
            final GuiSession session = Gui.Registry.getOrNull(event.getPlayer());
            if (session != null && session.consumeDecoratedText(event.getMessage())) {
                event.setCancelled(true);
            }
        }
    }

    private static final class PaperEvents implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        private void onPlainChat(AsyncChatDecorateEvent event) {
            final GuiSession session = Gui.Registry.getOrNull(event.player());
            if (session != null && session.consumePlainText(event.result())) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onDecoratedChat(AsyncChatEvent event) {
            final GuiSession session = Gui.Registry.getOrNull(event.getPlayer());
            if (session != null && session.consumeDecoratedText(event.message())) {
                event.setCancelled(true);
            }
        }
    }
}
