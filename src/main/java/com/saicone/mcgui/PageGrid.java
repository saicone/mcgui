package com.saicone.mcgui;

import com.saicone.mcgui.grid.FilledGrid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PageGrid {

    PageGrid DEFAULT = new FilledGrid();

    @NotNull
    default <E> List<E> transform(@NotNull List<E> list) {
        return list;
    }

    @Nullable
    <E> E element(@NotNull PageableItem<E> item, @NotNull GuiSession session, int slot);

    @NotNull
    <E> List<E> subList(@NotNull PageableItem<E> item, @NotNull GuiSession session);

}
