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

import com.saicone.mcgui.text.ComposedGuiTitle;
import com.saicone.mcgui.text.GuiTitle;
import com.saicone.mcgui.text.SimpleGuiTitle;
import com.saicone.mcgui.text.TextDisplay;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class GuiTitleReader extends AbstractReader<GuiTitle> implements ConditionReader {

    public static final @Language("RegExp") String TITLE_PATTERN = "(gui-?)?title";

    protected GuiTitleReader(@NotNull Object value) {
        super(value);
    }

    @Override
    public GuiTitle read() {
        if (isMap()) {
            return readSimpleTitle(this);
        } else if (isList()) {
            return readComposedTitle(list());
        } else {
            return readTitle(value());
        }
    }

    @Nullable
    protected GuiTitle readTitle(@NotNull Object value) {
        if (value instanceof Map<?, ?> map) {
            return readSimpleTitle(map);
        } else if (value instanceof List<?> list) {
            return readComposedTitle(list);
        } else {
            return new SimpleGuiTitle(TRUE, TextDisplay.mini(value));
        }
    }

    @Nullable
    protected GuiTitle readSimpleTitle(@NotNull Map<?, ?> map) {
        return readSimpleTitle(ofMap(map));
    }

    @Nullable
    protected GuiTitle readSimpleTitle(@NotNull AbstractReader<?> reader) {
        final Object condition = reader.readAny(ConditionReader.CONDITION_PATTERN);
        final Object title = reader.readAny(GuiTitleReader.TITLE_PATTERN);
        if (title == null) {
            return null;
        }
        return new SimpleGuiTitle(condition == null ? TRUE : readCondition(condition), TextDisplay.mini(title));
    }

    @Nullable
    protected GuiTitle readComposedTitle(@NotNull List<?> list) {
        final List<GuiTitle> titles = new ArrayList<>();
        for (Object element : list) {
            final GuiTitle title = readTitle(element);
            if (title != null) {
                titles.add(title);
            }
        }
        if (titles.isEmpty()) {
            return null;
        } else if (titles.size() == 1) {
            return titles.get(0);
        } else {
            return new ComposedGuiTitle(titles);
        }
    }
}
