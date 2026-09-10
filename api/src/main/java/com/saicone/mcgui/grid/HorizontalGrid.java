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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HorizontalGrid extends SpacedGrid {

    @NotNull
    public static HorizontalGrid valueOf(int width, int height) {
        if (width < 1) {
            throw new IllegalArgumentException("Width cannot be less than 1");
        }
        if (height < 1) {
            throw new IllegalArgumentException("Height cannot be less than 1");
        }
        return new HorizontalGrid(width, height);
    }

    private final int width;
    private final int height;

    public HorizontalGrid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    @Override
    protected int spaces() {
        return height;
    }

    @Override
    public @NotNull <E> List<E> transform(@NotNull List<E> list) {
        final int size = list.size();
        final List<E> result = new ArrayList<>(this.width() * this.height());
        for (int col = 0; col < this.width(); col++) {
            for (int row = 0; row < this.height(); row++) {
                final int sourceIndex = row * this.width() + col;
                result.add(sourceIndex < size ? list.get(sourceIndex) : null);
            }
        }
        return result;
    }
}
