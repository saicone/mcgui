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

package com.saicone.mcgui.item.impl;

import com.saicone.mcgui.item.ItemDisplay;
import com.saicone.mcgui.session.GuiSession;
import com.saicone.mcgui.text.TextDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("deprecation")
public class MetaItemDisplay extends AbstractItemDisplay {

    private static final ItemFlag HIDE_ADDITIONAL_TOOLTIP = ItemFlag.values()[5];

    public MetaItemDisplay(
            @NotNull ItemStack item,
            @Nullable Integer amount,
            @Nullable TextDisplay text,
            @Nullable TextDisplay name,
            @Nullable TextDisplay lore,
            @Nullable Boolean glowing,
            @Nullable Model model,
            @Nullable Tooltip tooltip) {
        super(item, amount, text, name, lore, glowing, model, tooltip);
    }

    @Override
    public @NotNull ItemStack get(@NotNull GuiSession session, @NotNull ItemStack item) {
        if (amount != null) {
            item.setAmount(amount);
        }

        final ItemMeta meta = item.getItemMeta();
        if (text != null) {
            final List<Component> metaLore = new ArrayList<>();
            final AtomicBoolean first = new AtomicBoolean(true);
            text.forEach(session, component -> {
                if (first.getAndSet(false)) {
                    displayName(meta, component);
                } else {
                    metaLore.add(component);
                }
            });

            if (!metaLore.isEmpty()) {
                lore(meta, metaLore);
            }
        }

        if (name != null) {
            displayName(meta, name.get(session));
        }

        if (lore != null) {
            final List<Component> metaLore = new ArrayList<>();
            lore.forEach(session, metaLore::add);
            lore(meta, metaLore);
        }

        if (glowing != null) {
            glowing(meta, glowing);
        }

        if (model != null) {
            if (model.containsCustomModelData()) {
                customModelData(meta, model);
            }
            if (model.model() != null) {
                model(meta, model.model());
            }
        }

        if (tooltip != null) {
            if (tooltip.flags() != null) {
                meta.removeItemFlags(ItemFlag.values());
                meta.addItemFlags(tooltip.flags().toArray(new ItemFlag[0]));
            }

            if (tooltip.style() != null) {
                tooltipStyle(meta, tooltip.style());
            }

            if (tooltip.hide() != null) {
                hideTooltip(meta, tooltip.hide());
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    private void displayName(@NotNull ItemMeta meta, @Nullable Component name) {
        try {
            meta.displayName(name);
        } catch (Throwable t) {
            meta.setDisplayName(name == null ? null : LegacyComponentSerializer.legacySection().serialize(name));
        }
    }

    private void lore(@NotNull ItemMeta meta, @NotNull List<Component> lore) {
        try {
            meta.lore(lore);
        } catch (Throwable t) {
            final List<String> legacyLore = new ArrayList<>(lore.size());
            for (Component component : lore) {
                legacyLore.add(LegacyComponentSerializer.legacySection().serialize(component));
            }
            meta.setLore(legacyLore);
        }
    }

    private void glowing(@NotNull ItemMeta meta, boolean glowing) {
        try {
            meta.setEnchantmentGlintOverride(glowing);
        } catch (Throwable t) {
            meta.addEnchant(Enchantment.THORNS, 1, true);
            if (meta instanceof EnchantmentStorageMeta) {
                meta.addItemFlags(HIDE_ADDITIONAL_TOOLTIP);
            } else {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
    }

    private void customModelData(@NotNull ItemMeta meta, @NotNull ItemDisplay.Model model) {
        try {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();

            if (model.data() != null) {
                component.setFloats(List.of(model.data().floatValue()));
            }

            if (model.floats() != null) {
                component.setFloats(model.floats());
            }
            if (model.flags() != null) {
                component.setFlags(model.flags());
            }
            if (model.strings() != null) {
                component.setStrings(model.strings());
            }
            if (model.colors() != null) {
                component.setColors(model.colors());
            }

            meta.setCustomModelDataComponent(component);
        } catch (Throwable t) {
            try {
                if (model.data() != null) {
                    meta.setCustomModelData(model.data());
                }
            } catch (Throwable ignored) { }
        }
    }

    private void model(@NotNull ItemMeta meta, @NotNull Key model) {
        try {
            meta.setItemModel(new NamespacedKey(model.namespace(), model.value()));
        } catch (Throwable ignored) { }
    }

    private void tooltipStyle(@NotNull ItemMeta meta, @NotNull Key tooltip) {
        try {
            meta.setTooltipStyle(new NamespacedKey(tooltip.namespace(), tooltip.value()));
        } catch (Throwable ignored) { }
    }

    private void hideTooltip(@NotNull ItemMeta meta, boolean hide) {
        try {
            meta.setHideTooltip(hide);
        } catch (Throwable ignored) { }
    }
}
