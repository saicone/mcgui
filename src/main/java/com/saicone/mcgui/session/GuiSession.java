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
package com.saicone.mcgui.session;

import com.saicone.mcgui.gui.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface GuiSession extends InventoryHolder {

    @NotNull
    Player getAgent();

    @NotNull
    GuiView getView();

    @NotNull
    Optional<Gui> getLast();

    @NotNull
    <T extends Gui> T getGui();

    @NotNull
    <T extends Gui.Metadata> T getMeta();

    @Override
    @NotNull
    Inventory getInventory();

    @Nullable
    @Contract("!null -> !null")
    Component parse(@Nullable String string);

    @NotNull
    List<Component> parse(@NotNull List<String> list);

    void rotate(@NotNull Gui gui);

    void open();

    void close();

    void close(boolean send);

    void silentClose();

    void update();

    void updateTitle();

    void updateInventory(@NotNull Inventory inventory);

    void updateSlots(int... slots);

    void updateItems(char... ids);

    void execute(@NotNull InventoryCloseEvent event);

    void execute(@NotNull InventoryClickEvent event);

    void execute(@NotNull InventoryDragEvent event);

    void listenPlainChat(@NotNull Consumer<String> consumer);

    void listenDecoratedChat(@NotNull Consumer<Component> consumer);

    boolean consumePlainText(@NotNull String input);

    boolean consumePlainText(@NotNull Component input);

    boolean consumeDecoratedText(@NotNull String input);

    boolean consumeDecoratedText(@NotNull Component input);
}
