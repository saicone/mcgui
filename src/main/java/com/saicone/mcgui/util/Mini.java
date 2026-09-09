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

package com.saicone.mcgui.util;

import io.github.miniplaceholders.api.MiniPlaceholders;
import io.github.miniplaceholders.api.types.RelationalAudience;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@ApiStatus.Internal
public class Mini {

    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("(?i)(&[0-9A-FK-OR])|(&#[0-9A-F]{6})");
    private static final Pattern SECTION_PATTERN = Pattern.compile("(?i)(§[0-9A-FK-OR])|(§x(§[0-9A-F]){6})");

    public static Mini INSTANCE = new Mini();

    @NotNull
    public static Mini get() {
        return INSTANCE;
    }

    private final MiniMessage miniMessage;

    // lazy init value
    private final Supplier<Boolean> useMiniPlaceholders = new Supplier<>() {
        private transient volatile boolean initialized;
        private transient boolean value;

        private boolean init() {
            try {
                Class.forName("io.github.miniplaceholders.api.MiniPlaceholders");
                return true;
            } catch (Throwable t) {
                return false;
            }
        }

        @Override
        public Boolean get() {
            if (!initialized) {
                synchronized (this) {
                    if (!initialized) {
                        value = init();
                        initialized = true;
                    }
                }
            }
            return value;
        }
    };

    public Mini() {
        this(MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .build()
                )
                .build());
    }

    public Mini(@NotNull MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    public boolean isUsingMiniPlaceholders() {
        return useMiniPlaceholders.get();
    }

    @NotNull
    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    @NotNull
    public Audience relational(@NotNull Audience main, @NotNull Audience secondary) {
        if (useMiniPlaceholders.get()) {
            return RelationalAudience.from(main, secondary);
        } else {
            return main;
        }
    }

    @NotNull
    public Component parse(@NotNull Object object) {
        return parse(null, object);
    }

    @NotNull
    public Component parse(@Nullable Pointered target, @NotNull Object object) {
        final Object first = first(object);
        if (first == null) {
            throw new IllegalArgumentException("Cannot parse null object to Component");
        }

        if (first instanceof Component) {
            return (Component) first;
        } else if (first instanceof String str) {
            if (AMPERSAND_PATTERN.matcher(str).find()) {
                return LegacyComponentSerializer.legacyAmpersand().deserialize(str);
            }
            if (SECTION_PATTERN.matcher(str).find()) {
                return LegacyComponentSerializer.legacySection().deserialize(str);
            }
            return deserialize(target, str);
        } else {
            throw new IllegalArgumentException("Cannot parse object of type " + first.getClass().getName() + " to Component");
        }
    }

    @NotNull
    public Component parseOrEmpty(@Nullable Pointered target, @Nullable Object object) {
        if (object == null) {
            return Component.empty();
        }
        return parse(target, object);
    }

    @NotNull
    public Component deserialize(@Nullable Pointered target, @NotNull String str) {
        if (useMiniPlaceholders.get()) {
            if (target != null) {
                if (target instanceof RelationalAudience<?>) {
                    return miniMessage.deserialize(str, target, MiniPlaceholders.relationalGlobalPlaceholders());
                } else {
                    return miniMessage.deserialize(str, target, MiniPlaceholders.audienceGlobalPlaceholders());
                }
            } else {
                return miniMessage.deserialize(str, MiniPlaceholders.globalPlaceholders());
            }
        } else {
            return miniMessage.deserialize(str);
        }
    }

    @Nullable
    private static Object first(@NotNull Object value) {
        if (value instanceof Iterable) {
            final Iterator<?> iterator = ((Iterable<?>) value).iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        } else if (value instanceof Object[] array) {
            if (array.length > 0) {
                return array[0];
            }
        } else if (value.getClass().isArray()) {
            if (Array.getLength(value) > 0) {
                return Array.get(value, 0);
            }
        } else {
            return value;
        }
        return null;
    }
}
