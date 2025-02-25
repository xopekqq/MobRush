package ru.xopek.mobrush.handler.item;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.xopek.mobrush.util.StringUtils;

import java.util.HashMap;
import java.util.List;

@Getter
public class ItemCore {
    private final NamespacedKey itemKey = NamespacedKey.minecraft("paradoxitem");
    private final HashMap<String, ItemStack> items = new HashMap<>();

    public ItemCore() {
        items.put("godeye",
                createItem(Material.ENDER_EYE, StringUtils.asColor("&#FF0000[⭐]&f Глаз Бога"),
                        List.of(StringUtils.asColor("&7Подсвечивает мобов [40 блоков]")), "godeye")
        );
        items.put("thunder",
                createItem(Material.HEART_OF_THE_SEA, StringUtils.asColor("&#FF0000[⭐]&b Гроза"),
                        List.of(StringUtils.asColor("&7Стреляет молнией в"), StringUtils.asColor("&7радиусе 10 блоков")), "thunder")
        );
        items.put("fireshow",
                createItem(Material.BLAZE_POWDER, StringUtils.asColor("&#FF0000[⭐]&b Фаер-Шоу"),
                        List.of(StringUtils.asColor("&7Поджигает огнем в"), StringUtils.asColor("&7радиусе 15 блоков")), "fireshow")
        );
    }

    private ItemStack createItem(Material material, String name, List<String> lore, String tag) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, tag);
        item.setItemMeta(meta);

        return item;
    }
}
