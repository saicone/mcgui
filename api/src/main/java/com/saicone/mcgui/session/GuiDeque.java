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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class GuiDeque {

    public static final int DEFAULT_MAX_SIZE = 15;

    private final int maxSize;
    private final Deque<GuiHolder> holders = new ArrayDeque<>();

    public GuiDeque() {
        this(DEFAULT_MAX_SIZE);
    }

    public GuiDeque(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }

        this.maxSize = maxSize;
    }

    public void push(@NotNull GuiHolder holder) {
        if (!holder.isPersistent()) {
            evictIfNecessary();
        }

        holders.addFirst(holder);
    }

    private void evictIfNecessary() {
        int count = 0;

        for (GuiHolder holder : holders) {
            if (!holder.isPersistent()) {
                count++;
            }
        }

        if (count < maxSize) {
            return;
        }

        var iterator = holders.descendingIterator();

        while (iterator.hasNext()) {
            if (!iterator.next().isPersistent()) {
                iterator.remove();
                return;
            }
        }
    }

    @UnknownNullability
    public GuiHolder latest() {
        return holders.peekFirst();
    }

    @UnknownNullability
    public GuiHolder find(@NotNull Gui gui) {
        for (GuiHolder holder : holders) {
            if (holder.gui().equals(gui)) {
                return holder;
            }
        }

        return null;
    }

    @NotNull
    public List<GuiHolder> asList() {
        return holders.stream().toList();
    }

    public int size() {
        return holders.size();
    }

    public int normalSize() {
        return (int) holders.stream()
                .filter(holder -> !holder.isPersistent())
                .count();
    }

    public void clear() {
        holders.clear();
    }
}
