package ru.xopek.mobrush.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryUtils {
    /**
     * Утилитный класс для работы
     * с инвентарем и билда магазина.
     */

    public static String title = "Магазин - MobRush";
    public static NamespacedKey itemTag = NamespacedKey.minecraft("rushitem");

    public static Inventory buildShop(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 45, title);

        int healthPrice = getHealthPrice(player.getMaxHealth());

        inventory.setItem(21,
                buildItem(Material.REDSTONE, "&c[Увеличить здоровье]",
                        List.of("&fЦена: &a"+ healthPrice * 10 +"&f монет и &9"+ healthPrice +"&f опыта"), "health"));

        inventory.setItem(23,
                buildItem(Material.MUSIC_DISC_PIGSTEP, "&c[Купить рандом ауру]",
                        List.of("&fЦена: &a500&f монет и &9225&f опыта"), "aura"));

        for (int i = 0; i < 45; i++) {
            if (i != 21 && i != 23) inventory.setItem(i, buildGlass());
        }

        player.openInventory(inventory);

        return inventory;
    }

    public static int getHealthPrice(double health) {
        return (int) Math.round(((health / 10) * 5));
    }

    private static ItemStack buildGlass() {
        ItemStack itemStack = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(" ");
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private static ItemStack buildItem(Material material, String name, List<String> lore, String tag) {
        ItemStack itemStack = new ItemStack(material);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(StringAPI.asColor(name));

        List<String> coloredLore = lore
                .stream()
                .map(StringAPI::asColor)
                .collect(Collectors.toList());

        itemMeta.setLore(coloredLore);
        itemMeta.getPersistentDataContainer().set(itemTag, PersistentDataType.STRING, tag);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
