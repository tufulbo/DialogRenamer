package com.jcruz.dialogrenamer.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LoreService {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    public boolean isValidItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    public String getNameAsPlainText(ItemStack item) {
        if (!isValidItem(item)) {
            return "";
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || meta.displayName() == null) {
            return "";
        }
        return serializer.serialize(meta.displayName());
    }

    public String getLoreAsPlainText(ItemStack item) {
        if (!isValidItem(item)) {
            return "";
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore() || meta.lore() == null) {
            return "";
        }
        List<Component> currentLore = meta.lore();
        return currentLore.stream()
                .map(serializer::serialize)
                .collect(Collectors.joining("\n"));
    }

    public void updateItem(ItemStack item, String nameText, boolean updateName, String loreText, boolean updateLore) {
        if (!isValidItem(item) || (!updateName && !updateLore)) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        if (updateName) {
            if (nameText != null && !nameText.isBlank()) {
                Component nameComp = serializer.deserialize(nameText.replace('§', '&'));
                if (!nameComp.hasDecoration(TextDecoration.ITALIC)) {
                    nameComp = nameComp.decoration(TextDecoration.ITALIC, false);
                }
                meta.displayName(nameComp);
            } else {
                meta.displayName(null);
            }
        }

        if (updateLore) {
            List<Component> newLore = new ArrayList<>();
            if (loreText != null && !loreText.isBlank()) {
                newLore = Arrays.stream(loreText.split("\n", -1))
                        .map(line -> {
                            Component comp = serializer.deserialize(line.replace('§', '&'));
                            if (!comp.hasDecoration(TextDecoration.ITALIC)) {
                                return comp.decoration(TextDecoration.ITALIC, false);
                            }
                            return comp;
                        })
                        .collect(Collectors.toList());
            }
            meta.lore(newLore.isEmpty() ? null : newLore);
        }

        item.setItemMeta(meta);
    }
}
