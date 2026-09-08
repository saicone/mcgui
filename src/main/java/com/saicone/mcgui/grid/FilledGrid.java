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

package com.saicone.mcgui.grid;

import com.saicone.mcgui.session.GuiSession;
import com.saicone.mcgui.item.PageableGui;
import com.saicone.mcgui.gui.PageableItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FilledGrid implements PageGrid {

    @Override
    public <E> @Nullable E element(@NotNull PageableItem<E> item, @NotNull GuiSession session, int slot) {
        final PageableGui.Metadata metadata = session.getMeta();

        int index = metadata.getIndex(slot);
        if (index < 0) {
            return null;
        }

        index += metadata.getAmount(item.getId()) * metadata.getPage(item);
        final List<E> list = metadata.getItemList(item);
        if (index >= list.size()) {
            return null;
        }

        return list.get(index);
    }

    @Override
    public @NotNull <E> List<E> subList(@NotNull PageableItem<E> item, @NotNull GuiSession session) {
        final PageableGui.Metadata metadata = session.getMeta();

        final int amount = metadata.getAmount(item.getId());
        final int fromIndex = amount * metadata.getPage(item);

        final List<E> list = metadata.getItemList(item);
        if (fromIndex >= list.size()) {
            return List.of();
        }

        final int toIndex = Math.min(list.size(), fromIndex + amount);

        return list.subList(fromIndex, toIndex);
    }
}
