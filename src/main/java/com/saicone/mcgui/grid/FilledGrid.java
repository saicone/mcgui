package com.saicone.mcgui.grid;

import com.saicone.mcgui.GuiSession;
import com.saicone.mcgui.PageGrid;
import com.saicone.mcgui.PageableGui;
import com.saicone.mcgui.PageableItem;
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
