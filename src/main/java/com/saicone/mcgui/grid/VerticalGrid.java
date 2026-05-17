package com.saicone.mcgui.grid;

import org.jetbrains.annotations.NotNull;

public class VerticalGrid extends SpacedGrid {

    @NotNull
    public static VerticalGrid valueOf(int spaces) {
        if (spaces < 1) {
            throw new IllegalArgumentException("Spaces cannot be less than 1");
        }
        return new VerticalGrid(spaces);
    }

    private final int width;

    public VerticalGrid(int width) {
        this.width = width;
    }

    public int width() {
        return width;
    }

    @Override
    protected int spaces() {
        return width;
    }
}
