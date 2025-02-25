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
import ru.xopek.mobrush.MobRush;
import ru.xopek.mobrush.handler.database.MongoDB;
import ru.xopek.mobrush.handler.database.RushPlayer;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryUtils {
    /**
     * Утилитный класс для работы
     * с инвентарем и билда магазина.
     */

    public static String title = "Магазин - MobRush";
    public static NamespacedKey itemTag = NamespacedKey.minecraft("rushitem");

    public static void buildShop(Player player, MobRush inst) {
        Inventory inventory = Bukkit.createInventory(null, 45, title);

        int healthPrice = getHealthPrice(player.getMaxHealth());

        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, buildGlass());
        }

        inventory.setItem(11,
                buildItem(Material.REDSTONE, "&c[Увеличить здоровье]",
                        List.of("&fЦена: &e" + healthPrice * 10 + "&f монет и &9" + healthPrice + "&f опыта"), "health"));

        Bukkit.getScheduler().runTask(inst, () -> {
            MongoDB database = inst.getDatabase();
            RushPlayer rushPlayer = database.getPlayer(player.getUniqueId().toString());

            int rebirth = rushPlayer.getRebirth();

            inventory.setItem(13,
                    buildItem(Material.NETHER_STAR, "&6[Сделать престиж]",
                            List.of("&fУ вас &6" + rebirth + "&f перерождений!", "&f", "&fЦена: &e" + (rebirth * 125) + "&f монет и &9" + (rebirth * 75) + "&f опыта"), "rebirth"));
        });

        inventory.setItem(15,
                buildItem(Material.MUSIC_DISC_PIGSTEP, "&c[Купить рандом ауру]",
                        List.of("&fЦена: &a500&f монет и &9225&f опыта"), "aura"));

        inventory.setItem(30,
                buildItem(Material.ENDER_EYE, "&#FF0000[⭐]&f Глаз Бога",
                        List.of("&7Подсвечивает мобов в радиусе 40 блоков", "", "&fЦена: &e100&f монет"), "godeye"));
        inventory.setItem(31,
                buildItem(Material.HEART_OF_THE_SEA, "&#FF0000[⭐]&b Гроза",
                        List.of("&7Стреляет молнией в радиусе 10 блоков", "", "&fЦена: &e250&f монет"), "thunder"));
        inventory.setItem(32,
                buildItem(Material.BLAZE_POWDER, "&#FF0000[⭐]&f Фаер-Шоу",
                        List.of("&7Поджигает огнем в радиусе 15 блоков", "", "&fЦена: &e300&f монет"), "fireshow"));

        player.openInventory(inventory);

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
        itemMeta.setDisplayName(StringUtils.asColor(name));

        List<String> coloredLore = lore
                .stream()
                .map(StringUtils::asColor)
                .collect(Collectors.toList());

        itemMeta.setLore(coloredLore);
        itemMeta.getPersistentDataContainer().set(itemTag, PersistentDataType.STRING, tag);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
