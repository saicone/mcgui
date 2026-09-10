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

package com.saicone.mcgui.io;

import com.saicone.mcgui.button.ComposedGuiButton;
import com.saicone.mcgui.button.GuiButton;
import com.saicone.mcgui.button.SimpleGuiButton;
import com.saicone.mcgui.item.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class GuiButtonReader extends AbstractReader<GuiButton> implements ConditionReader, ExecutionReader {

    protected GuiButtonReader(@NotNull Object value) {
        super(value);
    }

    @Override
    public GuiButton read() {
        if (isMap()) {
            return readSimpleButton(this);
        } else if (isList()) {
            return readComposedButton(list());
        } else {
            return null;
        }
    }

    @Nullable
    protected GuiButton readButton(@NotNull Object value) {
        if (value instanceof Map<?, ?> map) {
            return readSimpleButton(map);
        } else if (value instanceof List<?> list) {
            return readComposedButton(list);
        } else {
            return null;
        }
    }

    @Nullable
    protected GuiButton readSimpleButton(@NotNull Map<?, ?> map) {
        return readSimpleButton(ofMap(map));
    }

    @Nullable
    protected GuiButton readSimpleButton(@NotNull AbstractReader<?> reader) {
        final Object condition = reader.readAny(ConditionReader.CONDITION_PATTERN);
        final Object display = reader.readAny(ItemDisplayReader.DISPLAY_PATTERN);
        final Object execution = reader.readAny(ExecutionReader.EXECUTION_PATTERN);

        final ItemDisplay itemDisplay = ItemDisplayReader.read(display);
        if (itemDisplay == null) {
            return null;
        }

        return new SimpleGuiButton(
                condition == null ? TRUE : readCondition(condition),
                itemDisplay,
                execution == null ? NOOP : readExecution(execution)
        );
    }

    @Nullable
    protected GuiButton readComposedButton(@NotNull List<?> list) {
        final List<GuiButton> buttons = new ArrayList<>();
        for (Object element : list) {
            final GuiButton button = readButton(element);
            if (button != null) {
                buttons.add(button);
            }
        }
        if (buttons.isEmpty()) {
            return null;
        } else if (buttons.size() == 1) {
            return buttons.get(0);
        } else {
            return new ComposedGuiButton(buttons);
        }
    }
}
