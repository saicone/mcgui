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

package com.saicone.mcgui.input;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class KeyInput {

    public static final int CLICK = 0;
    public static final int SHIFT = 1;
    public static final int SWAP = 2;
    public static final int MIDDLE_CLICK = 3;
    public static final int THROW = 4;
    public static final int DRAG = 5;
    public static final int DOUBLE_CLICK = 6;

    public static final KeyInput UNKNOWN = new KeyInput(-1, (byte) -1, null) {{
        try {
            bukkitType = ClickType.valueOf("UNKNOWN");
        } catch (Throwable ignored) { }
    }};

    private static final List<KeyInput> VALUES = new ArrayList<>();
    private static final Map<Integer, Map<Integer, KeyInput>> ENTRIES = new HashMap<>();

    @NotNull
    public static List<KeyInput> values() {
        return VALUES;
    }

    private static final List<String> CONTAINER_INPUTS = List.of(
            "PICKUP",
            "QUICK_MOVE",
            "SWAP",
            "CLONE",
            "THROW",
            "QUICK_CRAFT",
            "PICKUP_ALL"
    );

    @NotNull
    public static KeyInput from(@NotNull String containerInput, @NotNull Number button, @Nullable Number slotNum) {
        final int mode = CONTAINER_INPUTS.indexOf(containerInput);
        if (mode == -1) {
            return UNKNOWN;
        }
        return from(mode, button, slotNum);
    }

    @NotNull
    public static KeyInput from(@NotNull Enum<?> containerInput, @NotNull Number button, @Nullable Number slotNum) {
        return from(containerInput.ordinal(), button, slotNum);
    }

    @NotNull
    public static KeyInput from(@NotNull Number mode, @NotNull Number button, @Nullable Number slotNum) {
        final var map = ENTRIES.get(mode.intValue());
        if (map != null) {
            final KeyInput input;
            if (slotNum != null && slotNum.intValue() == -999) {
                input = map.get(combine(button, slotNum));
            } else {
                input = map.get(combine(button, null));
            }

            if (input != null) {
                return input;
            }
        }

        return UNKNOWN;
    }

    private static final Set<InventoryAction> DETECTED_ACTIONS = new HashSet<>();

    @NotNull
    private static InventoryAction action(@NotNull InventoryAction action) {
        DETECTED_ACTIONS.add(action);
        return action;
    }

    @Nullable
    private static InventoryAction action(@NotNull String action) {
        try {
            return action(InventoryAction.valueOf(action));
        } catch (Throwable t) {
            return null;
        }
    }

    @NotNull
    public static KeyInput from(@NotNull InventoryClickEvent event) {
        final ClickType type = event.getClick();
        if (type == ClickType.NUMBER_KEY) {
            return from(KeyInput.SWAP, event.getHotbarButton(), null);
        } else {
            InventoryAction action = event.getAction();
            if (!DETECTED_ACTIONS.contains(action)) {
                action = null;
            }
            for (KeyInput input : values()) {
                if (type.equals(input.bukkitType()) && Objects.equals(action, input.bukkitAction())) {
                    return input;
                }
            }
        }

        return UNKNOWN;
    }

    private static int combine(@NotNull Number button, @Nullable Number slot) {
        if (slot == null) {
            return button.intValue();
        }
        return (slot.shortValue() << 8) | (button.byteValue() & 0xFF);
    }

    private final int mode;
    private final byte button;
    private final Short slot;

    protected ClickType bukkitType;
    protected InventoryAction bukkitAction;

    private KeyInput(int mode, int button, @Nullable Integer slot) {
        this.mode = mode;
        this.button = (byte) button;
        this.slot = slot == null ? null : slot.shortValue();

        VALUES.add(this);
        ENTRIES.computeIfAbsent(this.mode, k -> new HashMap<>()).put(combine(this.button, this.slot), this);
    }

    public int mode() {
        return mode;
    }

    public byte button() {
        return button;
    }

    @Nullable
    public Short slot() {
        return slot;
    }

    @UnknownNullability
    public ClickType bukkitType() {
        return bukkitType;
    }

    @UnknownNullability
    public InventoryAction bukkitAction() {
        return bukkitAction;
    }

    public static class Click extends KeyInput {

        public static final Click PICKUP_LEFT = new Click(0) {{
            bukkitType = ClickType.LEFT;
        }};
        public static final Click PICKUP_RIGHT = new Click(1) {{
            bukkitType = ClickType.RIGHT;
        }};
        public static final Click DROP_LEFT = new Click(0, -999) {{
            bukkitType = ClickType.WINDOW_BORDER_LEFT;
            bukkitAction = action(InventoryAction.DROP_ALL_CURSOR);
        }};
        public static final Click DROP_RIGHT = new Click(1, -999) {{
            bukkitType = ClickType.WINDOW_BORDER_RIGHT;
            bukkitAction = action(InventoryAction.DROP_ONE_CURSOR);
        }};

        private Click(int button) {
            this(button, null);
        }

        private Click(int button, @Nullable Integer slot) {
            super(KeyInput.CLICK, button, slot);
        }
    }

    public static class Shift extends KeyInput {

        public static final Shift LEFT = new Shift(0) {{
            bukkitType = ClickType.SHIFT_LEFT;
        }};
        public static final Shift RIGHT = new Shift(1) {{
            bukkitType = ClickType.SHIFT_RIGHT;
        }};

        private Shift(int button) {
            super(KeyInput.SHIFT, button, null);
        }
    }

    public static class Swap extends KeyInput {

        public static final Swap NUMBER_KEY_1 = new Swap(0) {{
            bukkitType = ClickType.NUMBER_KEY;
        }};
        public static final Swap NUMBER_KEY_2 = new Swap(1) {{
            bukkitType = ClickType.NUMBER_KEY;
        }};
        public static final Swap NUMBER_KEY_3 = new Swap(2) {{
            bukkitType = ClickType.NUMBER_KEY;
        }};
        public static final Swap NUMBER_KEY_4 = new Swap(3) {{
            bukkitType = ClickType.NUMBER_KEY;
        }};
        public static final Swap NUMBER_KEY_5 = new Swap(4) {{
            bukkitType = ClickType.NUMBER_KEY;
        }};
        public static final Swap NUMBER_KEY_6 = new Swap(5) {{
            bukkitType = ClickType.NUMBER_KEY;
        }};
        public static final Swap NUMBER_KEY_7 = new Swap(6) {{
            bukkitType = ClickType.NUMBER_KEY;
        }};
        public static final Swap NUMBER_KEY_8 = new Swap(7) {{
            bukkitType = ClickType.NUMBER_KEY;
        }};
        public static final Swap NUMBER_KEY_9 = new Swap(8) {{
            bukkitType = ClickType.NUMBER_KEY;
        }};
        public static final Swap OFFHAND = new Swap(40) {{
            try {
                bukkitType = ClickType.SWAP_OFFHAND;
            } catch (Throwable ignored) { }
        }};

        private Swap(int button) {
            super(KeyInput.SWAP, button, null);
        }
    }

    public static class MiddleClick extends KeyInput {

        public static final MiddleClick CLONE = new MiddleClick(2) {{
            bukkitType = ClickType.MIDDLE;
            bukkitAction = action(InventoryAction.CLONE_STACK);
        }};

        private MiddleClick(int button) {
            super(KeyInput.MIDDLE_CLICK, button, null);
        }
    }

    public static class Throw extends KeyInput {

        public static final Throw DROP = new Throw(0) {{
            bukkitType = ClickType.DROP;
            bukkitAction = action(InventoryAction.DROP_ONE_SLOT);
        }};
        public static final Throw CONTROL_DROP = new Throw(1) {{
            bukkitType = ClickType.CONTROL_DROP;
            bukkitAction = action(InventoryAction.DROP_ALL_SLOT);
        }};

        private Throw(int button) {
            super(KeyInput.THROW, button, null);
        }
    }

    public static class Drag extends KeyInput {

        public static final Drag START_LEFT = new Drag(0, -999);
        public static final Drag START_RIGHT = new Drag(4, -999);
        public static final Drag START_MIDDLE = new Drag(8, -999);

        public static final Drag ADD_LEFT = new Drag(1);
        public static final Drag ADD_RIGHT = new Drag(5);
        public static final Drag ADD_MIDDLE = new Drag(9);

        public static final Drag END_LEFT = new Drag(2, -999);
        public static final Drag END_RIGHT = new Drag(6, -999);
        public static final Drag END_MIDDLE = new Drag(10, -999);

        private Drag(int button) {
            this(button, null);
        }

        private Drag(int button, @Nullable Integer slot) {
            super(KeyInput.DRAG, button, slot);
        }
    }

    public static class DoubleClick extends KeyInput {

        public static final DoubleClick PICKUP_ALL = new DoubleClick(0) {{
            bukkitType = ClickType.DOUBLE_CLICK;
        }};
        // NOTE: Impossible in vanilla clients
        public static final DoubleClick PICKUP_ALL_REVERSE = new DoubleClick(1) {{
            bukkitType = ClickType.DOUBLE_CLICK;
        }};

        private DoubleClick(int button) {
            super(KeyInput.DOUBLE_CLICK, button, null);
        }
    }
}
