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

import com.cryptomorin.xseries.XMaterial;
import com.saicone.mcgui.item.ItemDisplay;
import com.saicone.mcgui.text.TextDisplay;
import com.saicone.mcgui.util.Lazy;
import com.saicone.rtag.util.SkullTexture;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemDisplayReader extends AbstractReader<ItemDisplay> {

    public static final @Language("RegExp") String DISPLAY_PATTERN = "item(-?(stack|display))?|display(-?item)?";
    protected static final Lazy<Boolean> USE_RTAG_API = Lazy.init(() -> {
        try {
            Class.forName("com.saicone.rtag.util.SkullTexture");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    });

    @UnknownNullability
    public static ItemDisplay read(@Nullable Object object) {
        if (object instanceof ConfigurationSection section) {
            return read(section);
        } else if (object instanceof Map<?, ?> map) {
            return read(map);
        } else {
            return null;
        }
    }

    @UnknownNullability
    public static ItemDisplay read(@NotNull ConfigurationSection section) {
        return new ItemDisplayReader(section).read();
    }

    @UnknownNullability
    public static ItemDisplay read(@NotNull Map<String, Object> map) {
        return new ItemDisplayReader(map).read();
    }

    protected ItemDisplayReader(@NotNull Object value) {
        super(value);
    }

    @Override
    public ItemDisplay read() {
        return readBuilder().build();
    }

    @NotNull
    protected ItemDisplay.Builder readBuilder() {
        final ItemDisplay.Builder builder;
        final ItemStack item = readItemStack();
        if (item == null) {
            builder = ItemDisplay.builder(Material.STONE);
        } else {
            builder = ItemDisplay.builder(item);
        }

        final Integer amount = readInteger("amount|count");
        if (amount != null) {
            builder.amount(amount);
        }

        final Object text = readAny("text");
        if (text != null) {
            builder.text(TextDisplay.mini(text));
        }

        final String name = readString("(display-?)?name");
        if (name != null) {
            builder.name(name);
        }

        final Object lore = readAny("(display-?)?lore(-?lines?)?");
        if (lore != null) {
            builder.lore(TextDisplay.mini(lore));
        }

        final Boolean glowing = readBoolean("glow(ing)?|shin[ye]");
        if (glowing != null) {
            builder.glowing(glowing);
        }

        final Integer customModelData = readInteger("(custom-?)?model-?data");
        if (customModelData != null) {
            builder.model(itemModel -> itemModel.data(customModelData));
        }

        final Object model = readAny("(item-?)?model");
        if (model instanceof String modelStr) {
            builder.model(itemModel -> itemModel.model(Key.key(modelStr)));
        } else if (model instanceof Map<?, ?> modelMap) {
            builder.model(itemModel -> readModel(itemModel, ofMap(modelMap)));
        }

        final List<ItemFlag> flags = readItemFlagList("((item|hide)-?)?flags");
        if (!flags.isEmpty()) {
            builder.tooltip(itemTooltip -> itemTooltip.flags(flags));
        }

        final Object tooltip = readAny("(item-?)?tooltip(-?style)?");
        if (tooltip instanceof String tooltipStr) {
            builder.tooltip(itemTooltip -> itemTooltip.style(Key.key(tooltipStr)));
        } else if (tooltip instanceof Map<?, ?> tooltipMap) {
            builder.tooltip(itemTooltip -> readTooltip(itemTooltip, ofMap(tooltipMap)));
        }

        return builder;
    }

    protected void readModel(@NotNull ItemDisplay.Model itemModel, @NotNull AbstractReader<Object> reader) {
        final Integer data = reader.readInteger("(custom-?)?(model-?)?data");
        if (data != null) {
            itemModel.data(data);
        }

        final String model = reader.readString("(item-?)?model");
        if (model != null) {
            itemModel.model(Key.key(model));
        }

        final List<Float> floats = reader.readFloatList("model(-?)?floats");
        if (!floats.isEmpty()) {
            itemModel.floats(floats);
        }

        final List<Boolean> flags = reader.readBooleanList("model(-?)?flags");
        if (!flags.isEmpty()) {
            itemModel.flags(flags);
        }

        final List<String> strings = reader.readStringList("model(-?)?strings");
        if (!strings.isEmpty()) {
            itemModel.strings(strings);
        }

        // TODO: Add model colors reader
    }

    protected void readTooltip(@NotNull ItemDisplay.Tooltip itemTooltip, @NotNull AbstractReader<Object> reader) {
        final List<ItemFlag> flags = reader.readItemFlagList("((item|hide)-?)?flags");
        if (!flags.isEmpty()) {
            itemTooltip.flags(flags);
        }

        final String style = reader.readString("(item-?)?tooltip(-?style)?");
        if (style != null) {
            itemTooltip.style(Key.key(style));
        }

        final Boolean hide = reader.readBoolean("(item|hide)-?tooltip");
        if (hide != null) {
            itemTooltip.hide(hide);
        }

        final List<Key> hidden = reader.readAnyList("hidden(-?)?tooltip", element -> {
            if (element instanceof String str) {
                return Key.key(str);
            }
            return null;
        });
        if (!hidden.isEmpty()) {
            itemTooltip.hidden(hidden);
        }
    }

    @Nullable
    protected ItemStack readItemStack() {
        final String texture = readString("texture|(head|skull)-?texture");
        if (texture != null && USE_RTAG_API.get()) {
            return SkullTexture.mojang().item(texture);
        }

        final String type = readString("type|mat(erial)?|item");
        if (type != null) {
            return readItemStack(type);
        }
        return null;
    }

    @Nullable
    protected ItemStack readItemStack(@NotNull String type) {
        if ((type.startsWith("head:") || type.startsWith("skull:")) && USE_RTAG_API.get()) {
            final String texture = type.substring(type.indexOf(':') + 1);
            return SkullTexture.mojang().item(texture);
        }

        if (USE_XSERIES_API.get()) {
            final Optional<XMaterial> material = XMaterial.matchXMaterial(type);
            if (material.isPresent()) {
                return material.get().parseItem();
            }
        } else {
            final Material material = Material.getMaterial(type.toUpperCase());
            if (material != null) {
                return new ItemStack(material);
            }
        }
        return null;
    }
}
