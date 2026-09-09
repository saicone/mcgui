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

package com.saicone.mcgui.text;

import com.saicone.mcgui.session.GuiSession;
import com.saicone.mcgui.util.Mini;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public interface TextDisplay {

    @NotNull
    static TextDisplay empty() {
        return EmptyDisplay.INSTANCE;
    }

    @NotNull
    static TextDisplay plain(@Nullable String component) {
        if (component == null) {
            return empty();
        }
        return plainComponent(Mini.get().parse(component));
    }

    @NotNull
    static TextDisplay plain(@Nullable Iterable<String> components) {
        if (components == null) {
            return empty();
        }
        final List<Component> list = new ArrayList<>();
        for (String s : components) {
            if (s == null || s.isEmpty()) {
                list.add(Component.empty());
            } else {
                list.add(Mini.get().parse(s));
            }
        }
        return plainComponent(list);
    }

    @NotNull
    static TextDisplay plain(@Nullable String... components) {
        if (components == null || components.length == 0) {
            return empty();
        }
        final List<Component> list = new ArrayList<>();
        for (String s : components) {
            if (s == null || s.isEmpty()) {
                list.add(Component.empty());
            } else {
                list.add(Mini.get().parse(s));
            }
        }
        return plainComponent(list);
    }

    @NotNull
    static TextDisplay plainComponent(@Nullable Component component) {
        if (component == null) {
            return empty();
        }
        return new PlainDisplay(component, null);
    }

    @NotNull
    static TextDisplay plainComponent(@Nullable Iterable<Component> components) {
        if (components == null) {
            return empty();
        }
        return new PlainDisplay(null, components);
    }

    @NotNull
    static TextDisplay plainComponent(@NotNull Component... components) {
        if (components.length == 0) {
            return empty();
        }
        return new PlainDisplay(components[0], Arrays.asList(components));
    }

    @NotNull
    static TextDisplay mini(@Nullable String component) {
        if (component == null) {
            return empty();
        }
        return new MiniDisplay(component, null);
    }

    @NotNull
    static TextDisplay mini(@Nullable Iterable<String> components) {
        if (components == null) {
            return empty();
        }
        return new MiniDisplay(null, components);
    }

    @NotNull
    static TextDisplay mini(@Nullable String... components) {
        if (components == null || components.length == 0) {
            return empty();
        }
        return new MiniDisplay(components[0], Arrays.asList(components));
    }

    @NotNull
    static TextDisplay miniComponent(@Nullable Component component) {
        if (component == null) {
            return empty();
        }
        return mini(Mini.get().getMiniMessage().serialize(component));
    }

    @NotNull
    static TextDisplay miniComponent(@Nullable Iterable<Component> components) {
        if (components == null) {
            return empty();
        }
        final List<String> list = new ArrayList<>();
        for (Component component : components) {
            if (component == null) {
                list.add("");
            } else {
                list.add(Mini.get().getMiniMessage().serialize(component));
            }
        }
        return mini(list);
    }

    @NotNull
    static TextDisplay miniComponent(@NotNull Component... components) {
        if (components.length == 0) {
            return empty();
        }
        final List<String> list = new ArrayList<>();
        for (Component component : components) {
            list.add(Mini.get().getMiniMessage().serialize(component));
        }
        return mini(list);
    }

    @Nullable
    Component get(@NotNull GuiSession session);

    default void forEach(@NotNull GuiSession session, @NotNull Consumer<Component> consumer) {
        final Component component = get(session);
        if (component != null) {
            consumer.accept(component);
        }
    }

    final class EmptyDisplay implements TextDisplay {

        private static final EmptyDisplay INSTANCE = new EmptyDisplay();

        @Override
        public Component get(@NotNull GuiSession session) {
            return null;
        }

        @Override
        public void forEach(@NotNull GuiSession session, @NotNull Consumer<Component> consumer) {
            // empty method
        }
    }

    final class PlainDisplay implements TextDisplay {

        private final Component component;
        private final Iterable<Component> iterable;

        public PlainDisplay(@Nullable Component component, @Nullable Iterable<Component> iterable) {
            this.component = component;
            this.iterable = iterable;
        }

        @Override
        public Component get(@NotNull GuiSession session) {
            if (component != null) {
                return component;
            } else if (iterable != null) {
                for (Component c : iterable) {
                    if (c != null) {
                        return c;
                    }
                }
            }
            return null;
        }

        @Override
        public void forEach(@NotNull GuiSession session, @NotNull Consumer<Component> consumer) {
            if (iterable != null) {
                iterable.forEach(consumer);
            } else if (component != null) {
                consumer.accept(component);
            }
        }
    }

    final class MiniDisplay implements TextDisplay {

        private final String mini;
        private final Iterable<String> iterable;

        public MiniDisplay(@Nullable String mini, @Nullable Iterable<String> iterable) {
            this.mini = mini;
            this.iterable = iterable;
        }

        @Override
        public Component get(@NotNull GuiSession session) {
            if (mini != null) {
                return Mini.get().parse(session.pointer(), session.parse(mini));
            } else if (iterable != null) {
                for (String s : iterable) {
                    if (s != null) {
                        return Mini.get().parse(session.pointer(), session.parse(s));
                    }
                }
            }
            return null;
        }

        @Override
        public void forEach(@NotNull GuiSession session, @NotNull Consumer<Component> consumer) {
            if (iterable != null) {
                iterable.forEach(s -> consumer.accept(Mini.get().parse(session.pointer(), session.parse(s))));
            } else if (mini != null) {
                consumer.accept(Mini.get().parse(session.pointer(), session.parse(mini)));
            }
        }
    }
}
