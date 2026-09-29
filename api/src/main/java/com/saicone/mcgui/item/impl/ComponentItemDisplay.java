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

import com.saicone.mcgui.session.GuiSession;
import com.saicone.mcgui.text.TextDisplay;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ComponentItemDisplay extends AbstractItemDisplay {

    private static final List<Key> HIDE_FLAGS = List.of(
            Key.key("minecraft:enchantments"),
            Key.key("minecraft:attribute_modifiers"),
            Key.key("minecraft:unbreakable"),
            Key.key("minecraft:can_break"),
            Key.key("minecraft:can_place_on"),
            Key.key("minecraft:stored_enchantments"),
            Key.key("minecraft:dyed_color"),
            Key.key("minecraft:trim"),
            Key.key("minecraft:stored_enchantments") // PaperMC only
    );

    public ComponentItemDisplay(
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

        if (text != null) {
            final List<Component> lore = new ArrayList<>();
            final AtomicBoolean first = new AtomicBoolean(true);
            text.forEach(session, component -> {
                if (first.getAndSet(false)) {
                    item.setData(DataComponentTypes.CUSTOM_NAME, component);
                } else {
                    lore.add(component);
                }
            });

            if (!lore.isEmpty()) {
                item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
            }
        }

        if (name != null) {
            item.setData(DataComponentTypes.CUSTOM_NAME, name.getOrEmpty(session));
        }

        if (lore != null) {
            final List<Component> lore = new ArrayList<>();
            this.lore.forEach(session, lore::add);
            item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        }

        if (glowing != null) {
            item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glowing);
        }

        if (model != null) {
            if (model.containsCustomModelData()) {
                final CustomModelData.Builder customModelData = CustomModelData.customModelData();

                if (model.data() != null) {
                    customModelData.addFloat(model.data().floatValue());
                }

                if (model.floats() != null) {
                    model.floats().forEach(customModelData::addFloat);
                }
                if (model.flags() != null) {
                    model.flags().forEach(customModelData::addFlag);
                }
                if (model.strings() != null) {
                    model.strings().forEach(customModelData::addString);
                }
                if (model.colors() != null) {
                    model.colors().forEach(customModelData::addColor);
                }

                item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData.build());
            }

            if (model.model() != null) {
                item.setData(DataComponentTypes.ITEM_MODEL, model.model());
            }
        }

        if (tooltip != null) {
            if (tooltip.style() != null) {
                item.setData(DataComponentTypes.TOOLTIP_STYLE, tooltip.style());
            }

            if (tooltip.hide() != null || tooltip.hidden() != null) {
                final TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();

                if (tooltip.flags() != null) {
                    for (ItemFlag flag : tooltip.flags()) {
                        final int ordinal = flag.ordinal();
                        if (ordinal >= HIDE_FLAGS.size()) continue;
                        if (ordinal == 5 && item.getType() != Material.ENCHANTED_BOOK) {
                            builder.hideTooltip(true);
                            continue;
                        }
                        final DataComponentType type = Registry.DATA_COMPONENT_TYPE.get(HIDE_FLAGS.get(ordinal));
                        if (type != null) {
                            builder.addHiddenComponents(type);
                        }
                    }
                }
                if (tooltip.hide() != null) {
                    builder.hideTooltip(tooltip.hide());
                }
                if (tooltip.hidden() != null) {
                    tooltip.hidden().forEach(key -> {
                        final DataComponentType type = Registry.DATA_COMPONENT_TYPE.get(key);
                        if (type != null) {
                            builder.addHiddenComponents(type);
                        }
                    });
                }

                item.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
            }
        }

        return item;
    }
}
