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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class AbstractGuiSession implements GuiSession {

    private final Player viewer;
    private Player agent;

    private volatile GuiView view;
    private GuiHolder holder;

    private final AtomicReference<Consumer<String>> plainTextConsumer = new AtomicReference<>();
    private final AtomicReference<Consumer<Component>> decoratedTextConsumer = new AtomicReference<>();

    public AbstractGuiSession(@NotNull Player viewer) {
        this.viewer = viewer;
    }

    public void setAgent(@Nullable Player agent) {
        this.agent = agent;
    }

    @Override
    public @NotNull Player viewer() {
        return viewer;
    }

    @Override
    public @Nullable Player agent() {
        return agent;
    }

    @Override
    public @NotNull GuiView view() {
        if (view == null) {
            synchronized (this) {
                if (view == null) {
                    view = new GuiView(viewer.getOpenInventory());
                }
            }
        }
        return view;
    }

    @Override
    public @NotNull GuiHolder holder() {
        return holder;
    }

    @Override
    public void push(@NotNull Gui gui) {
        if (this.holder != null && this.holder.gui().equals(gui)) {
            return;
        }

        this.holder = new GuiHolder(gui, gui.createMetadata(this), gui.createInventory(this));
    }

    @Override
    public void listenPlainChat(@NotNull Consumer<String> consumer) {
        this.plainTextConsumer.set(consumer);
        silentClose();
    }

    @Override
    public void listenDecoratedChat(@NotNull Consumer<Component> consumer) {
        this.decoratedTextConsumer.set(consumer);
        silentClose();
    }

    @Override
    public boolean consumePlainText(@NotNull String input) {
        final Consumer<String> consumer = plainTextConsumer.getAndSet(null);
        if (consumer != null) {
            consumer.accept(input);
            return true;
        }
        return false;
    }

    @Override
    public boolean consumePlainText(@NotNull Component input) {
        final Consumer<String> consumer = plainTextConsumer.getAndSet(null);
        if (consumer != null) {
            consumer.accept(PlainTextComponentSerializer.plainText().serialize(input));
            return true;
        }
        return false;
    }

    @Override
    public boolean consumeDecoratedText(@NotNull String input) {
        final Consumer<Component> consumer = decoratedTextConsumer.getAndSet(null);
        if (consumer != null) {
            consumer.accept(LegacyComponentSerializer.legacySection().deserialize(input));
            return true;
        }
        return false;
    }

    @Override
    public boolean consumeDecoratedText(@NotNull Component input) {
        final Consumer<Component> consumer = decoratedTextConsumer.getAndSet(null);
        if (consumer != null) {
            consumer.accept(input);
            return true;
        }
        return false;
    }
}
