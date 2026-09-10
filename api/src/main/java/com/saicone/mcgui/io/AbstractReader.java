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

package com.saicone.mcgui.io;

import com.cryptomorin.xseries.XItemFlag;
import com.saicone.mcgui.util.Lazy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

public abstract class AbstractReader<T> {

    protected static final Lazy<Boolean> USE_XSERIES_API = Lazy.init(() -> {
        try {
            Class.forName("com.cryptomorin.xseries.XMaterial");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    });

    @NotNull
    protected static AbstractReader<Object> ofMap(@NotNull Map<?, ?> map) {
        return new AbstractReader<>(map) {
            @Override
            public Object read() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private final Object value;

    protected AbstractReader(@NotNull Object value) {
        if (value instanceof ConfigurationSection section) {
            this.value = sectionToMap(section);
        } else {
            this.value = value;
        }
    }

    public boolean isList() {
        return value instanceof List<?>;
    }

    public boolean isMap() {
        return value instanceof Map<?, ?>;
    }

    @NotNull
    public Object value() {
        return value;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public List<Object> list() {
        return (List<Object>) value;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public Map<String, Object> map() {
        return (Map<String, Object>) value;
    }

    @UnknownNullability
    public abstract T read();

    @Nullable
    protected Object readAny(@NotNull @Language("RegExp") String key) {
        final Pattern pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
        for (Map.Entry<String, Object> entry : map().entrySet()) {
            if (pattern.matcher(entry.getKey()).matches()) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    protected Boolean readBoolean(@NotNull @Language("RegExp") String key) {
        Object value = readAny(key);
        return value instanceof Boolean ? (Boolean) value : null;
    }

    @Nullable
    protected Byte readByte(@NotNull @Language("RegExp") String key) {
        Object value = readAny(key);
        return value instanceof Number number ? number.byteValue() : null;
    }

    @Nullable
    protected Short readShort(@NotNull @Language("RegExp") String key) {
        Object value = readAny(key);
        return value instanceof Number number ? number.shortValue() : null;
    }

    @Nullable
    protected Integer readInteger(@NotNull @Language("RegExp") String key) {
        Object value = readAny(key);
        return value instanceof Number number ? number.intValue() : null;
    }

    @Nullable
    protected Long readLong(@NotNull @Language("RegExp") String key) {
        Object value = readAny(key);
        return value instanceof Number number ? number.longValue() : null;
    }

    @Nullable
    protected Float readFloat(@NotNull @Language("RegExp") String key) {
        Object value = readAny(key);
        return value instanceof Number number ? number.floatValue() : null;
    }

    @Nullable
    protected Double readDouble(@NotNull @Language("RegExp") String key) {
        Object value = readAny(key);
        return value instanceof Number number ? number.doubleValue() : null;
    }

    @Nullable
    protected String readString(@NotNull @Language("RegExp") String key) {
        Object value = readAny(key);
        return value instanceof String ? (String) value : null;
    }

    @NotNull
    protected <E> List<E> readAnyList(@NotNull @Language("RegExp") String key, @NotNull Function<Object, E> mapper) {
        Object value = readAny(key);
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(mapper)
                    .filter(Objects::nonNull)
                    .toList();
        }
        return List.of();
    }

    @NotNull
    protected List<Boolean> readBooleanList(@NotNull @Language("RegExp") String key) {
        return readAnyList(key, value -> value instanceof Boolean b ? b : null);
    }

    @NotNull
    protected List<Byte> readByteList(@NotNull @Language("RegExp") String key) {
        return readAnyList(key, value -> value instanceof Number number ? number.byteValue() : null);
    }

    @NotNull
    protected List<Short> readShortList(@NotNull @Language("RegExp") String key) {
        return readAnyList(key, value -> value instanceof Number number ? number.shortValue() : null);
    }

    @NotNull
    protected List<Integer> readIntegerList(@NotNull @Language("RegExp") String key) {
        return readAnyList(key, value -> value instanceof Number number ? number.intValue() : null);
    }

    @NotNull
    protected List<Long> readLongList(@NotNull @Language("RegExp") String key) {
        return readAnyList(key, value -> value instanceof Number number ? number.longValue() : null);
    }

    @NotNull
    protected List<Float> readFloatList(@NotNull @Language("RegExp") String key) {
        return readAnyList(key, value -> value instanceof Number number ? number.floatValue() : null);
    }

    @NotNull
    protected List<Double> readDoubleList(@NotNull @Language("RegExp") String key) {
        return readAnyList(key, value -> value instanceof Number number ? number.doubleValue() : null);
    }

    @NotNull
    protected List<String> readStringList(@NotNull @Language("RegExp") String key) {
        return readAnyList(key, String::valueOf);
    }

    @NotNull
    protected List<ItemFlag> readItemFlagList(@NotNull @Language("RegExp") String key) {
        return readAnyList(key, element -> readItemFlag(String.valueOf(element)));
    }

    @Nullable
    protected AbstractReader<Object> readSub(@NotNull @Language("RegExp") String key) {
        Object value = readAny(key);
        if (value instanceof Map<?, ?> sub) {
            return ofMap(sub);
        }
        return null;
    }

    @Nullable
    protected static ItemFlag readItemFlag(@NotNull String flag) {
        if (USE_XSERIES_API.get()) {
            return XItemFlag.of(flag).map(XItemFlag::get).orElse(null);
        } else {
            try {
                return ItemFlag.valueOf(flag.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    @NotNull
    protected static Map<String, Object> sectionToMap(@NotNull ConfigurationSection section) {
        final Map<String, Object> map = new HashMap<>();
        for (String key : section.getKeys(false)) {
            final Object value = section.get(key);
            if (value instanceof ConfigurationSection sub) {
                map.put(key, sectionToMap(sub));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }
}
