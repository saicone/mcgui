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

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApiStatus.Internal
public final class PAPI {

    public static PAPI INSTANCE = new PAPI();

    @NotNull
    public static PAPI get() {
        return INSTANCE;
    }

    private final Lazy<Boolean> present = Lazy.init(() -> Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"));

    public boolean isPresent() {
        return present.get();
    }

    public boolean isPlaceholderValue(@NotNull String s) {
        return s.length() >= 2 &&
                s.startsWith("%") &&
                s.endsWith("%") &&
                s.indexOf('%', 1) == s.length() - 1;
    }

    public boolean contains(@NotNull String s) {
        if (isPresent()) {
            return contains(PlaceholderAPI.getPlaceholderPattern(), s);
        } else {
            return false;
        }
    }

    public boolean containsBracket(@NotNull String s) {
        if (isPresent()) {
            return contains(PlaceholderAPI.getBracketPlaceholderPattern(), s);
        } else {
            return false;
        }
    }

    private boolean contains(@NotNull Pattern pattern, @NotNull String s) {
        final Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String match = matcher.group(1);
            final int index = match.indexOf('_');
            if (index == 0) {
                continue;
            } else if (index > 0) {
                match = match.substring(0, index);
            }
            if (PlaceholderAPI.isRegistered(match)) {
                return true;
            }
        }
        return false;
    }
}
