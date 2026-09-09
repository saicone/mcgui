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

import com.saicone.mcgui.GuiListener;
import com.saicone.mcgui.session.AbstractGuiSession;
import com.saicone.mcgui.session.GuiSession;
import com.saicone.mcgui.impl.GuiBuilderImpl;
import com.saicone.mcgui.item.GuiItem;
import com.saicone.mcgui.util.PluginSource;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Gui {

    @NotNull
    @SuppressWarnings("unchecked")
    static <T extends Builder<T>> T builder() {
        return (T) new GuiBuilderImpl();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    static <T extends Builder<T>> T builder(@NotNull InventoryType type) {
        return (T) new GuiBuilderImpl(type);
    }

    void reload();

    @NotNull
    default Metadata createMetadata(@NotNull GuiSession session) {
        return new Metadata(session, this);
    }

    @Nullable
    default Component createTitle(@NotNull GuiSession session) {
        return null;
    }

    @NotNull
    default Inventory createInventory(@NotNull GuiSession session) {
        return createInventory(session, meta(session).getTitle());
    }

    @NotNull
    Inventory createInventory(@NotNull GuiSession session, @Nullable Component title);

    @NotNull
    default GuiSession open(@NotNull Player player) {
        final GuiSession session = Registry.get(player);
        open(session);
        return session;
    }

    void open(@NotNull GuiSession session);

    void update(@NotNull GuiSession session);

    default void updateTitle(@NotNull GuiSession session) {
        final Metadata meta = meta(session);
        if (meta.updateTitle()) {
            final Inventory sessionInventory = session.getInventory();
            final Inventory inventory = createInventory(session, meta.getTitle());
            for (int slot = 0; slot < inventory.getSize() && slot < sessionInventory.getSize(); slot++) {
                inventory.setItem(slot, sessionInventory.getItem(slot));
            }
            session.holder().setInventory(inventory);
        }
    }

    default void updateSlots(@NotNull GuiSession session, int[] slots) {
        updateSlots(session, Arrays.stream(slots).iterator());
    }

    default void updateSlots(@NotNull GuiSession session, @NotNull Iterable<Integer> slots) {
        updateSlots(session, slots.iterator());
    }

    void updateSlots(@NotNull GuiSession session, @NotNull Iterator<Integer> slots);

    @NotNull
    default Metadata meta(@NotNull GuiSession session) {
        return session.meta();
    }

    class Metadata {

        private static final Component NO_INIT = Component.text("");

        private final GuiSession session;
        private final Gui gui;

        private Component title = NO_INIT;
        private boolean silentClose;

        public Metadata(@NotNull GuiSession session, @NotNull Gui gui) {
            this.session = session;
            this.gui = gui;
        }

        public boolean isSilentClose() {
            return silentClose;
        }

        @NotNull
        public GuiSession getSession() {
            return session;
        }

        @NotNull
        public Gui getGui() {
            return gui;
        }

        @Nullable
        public Component getTitle() {
            if (title == NO_INIT) {
                updateTitle();
            }
            return title;
        }

        public void setSilentClose(boolean silentClose) {
            this.silentClose = silentClose;
        }

        public boolean updateInventory() {
            return updateTitle();
        }

        public boolean updateTitle() {
            final Component previousTitle = this.title;
            this.title = getGui().createTitle(getSession());
            return previousTitle == NO_INIT || !Objects.equals(previousTitle, this.title);
        }
    }

    enum Flag {
        READ_ONLY;
    }

    interface Builder<T extends Builder<T>> {

        @NotNull
        @Contract("_ -> this")
        default T title(@NotNull Component title) {
            return title(session -> title);
        }

        @NotNull
        @Contract("_ -> this")
        T title(@NotNull Function<GuiSession, Component> title);

        @NotNull
        @Contract("_ -> this")
        default T layout(@NotNull String... layout) {
            return layout(session -> layout);
        }

        @NotNull
        @Contract("_ -> this")
        T layout(@NotNull Function<GuiSession, String[]> layout);

        @NotNull
        @Contract("_ -> this")
        default T size(int size) {
            return size(session -> size);
        }

        @NotNull
        @Contract("_ -> this")
        T size(@NotNull Function<GuiSession, Integer> size);

        @NotNull
        @Contract("_, _ -> this")
        default T item(char id, @NotNull GuiItem item) {
            return item((int) id, item);
        }

        @NotNull
        @Contract("_, _ -> this")
        T item(int slot, @NotNull GuiItem item);

        @NotNull
        @Contract("_ -> this")
        T onPreOpen(@NotNull Consumer<GuiSession> onPreOpen);

        @NotNull
        @Contract("_ -> this")
        T onOpen(@NotNull Consumer<GuiSession> onOpen);

        @NotNull
        @Contract("_ -> this")
        T onClose(@NotNull BiConsumer<GuiSession, InventoryCloseEvent> onClose);

        @NotNull
        @Contract("_ -> this")
        T onPreClick(@NotNull BiConsumer<GuiSession, InventoryClickEvent> onPreClick);

        @NotNull
        @Contract("_ -> this")
        T onClick(@NotNull BiConsumer<GuiSession, InventoryClickEvent> onClick);

        @NotNull
        @Contract("_ -> this")
        T flags(@NotNull Gui.Flag... flags);

        @NotNull
        Gui build();
    }

    final class Registry {

        static {
            GuiListener.INSTANCE.registerEvents(PluginSource.unchecked());
        }

        private static final Map<UUID, GuiSession> SESSIONS = new HashMap<>();

        @NotNull
        public static GuiSession get(@NotNull Player player) {
            GuiSession session = SESSIONS.get(player.getUniqueId());
            if (session == null) {
                session = new AbstractGuiSession(player) { };
                SESSIONS.put(player.getUniqueId(), session);
            }
            return session;
        }

        @Nullable
        public static GuiSession getOrNull(@NotNull Player player) {
            return SESSIONS.get(player.getUniqueId());
        }

        public static void remove(@NotNull Player player) {
            SESSIONS.remove(player.getUniqueId());
        }
    }
}
