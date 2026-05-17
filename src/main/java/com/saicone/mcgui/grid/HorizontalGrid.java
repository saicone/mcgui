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
