package ru.xopek.mobrush.handler.aura;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.xopek.mobrush.MobRush;
import ru.xopek.mobrush.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AuraCore {
    private final HashMap<String, Aura> auraMap = new HashMap<>();
    private final NamespacedKey auraTag = NamespacedKey.minecraft("rushaura");

    private final MobRush inst;

    public AuraCore(MobRush inst) {
        this.inst = inst;

        /**
         * Подгружаем ауры из конфига..
         */

        FileConfiguration config = inst.getConfig();
        ConfigurationSection allAurasSection = config.getConfigurationSection("auras");

        for (String auraKey : allAurasSection.getKeys(false)) {
            ConfigurationSection auraSection = allAurasSection.getConfigurationSection(auraKey);

            String name = StringUtils.asColor(auraSection.getString("name"));
            List<String> lore = auraSection.getStringList("lore")
                    .stream()
                    .map(StringUtils::asColor)
                    .collect(Collectors.toList());

            lore.add(StringUtils.asColor("&8Работает в 8 слоте хотбара!"));

            ItemStack itemStack = new ItemStack(Material.MUSIC_DISC_PIGSTEP);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);
            itemMeta.getPersistentDataContainer().set(auraTag, PersistentDataType.STRING, auraKey);

            itemStack.setItemMeta(itemMeta);

            ConfigurationSection attributesSection = auraSection.getConfigurationSection("attributes");

            int xp = attributesSection.contains("xp") ? attributesSection.getInt("xp") : 0;
            int money = attributesSection.contains("money") ? attributesSection.getInt("money") : 0;
            int damage = attributesSection.contains("damage") ? attributesSection.getInt("damage") : 0;

            Aura.Attributes attributes = new Aura.Attributes(xp, money, damage);
            Aura aura = new Aura(itemStack, attributes);

            this.auraMap.put(auraKey, aura);

        }
    }

    public Aura getAuraFromItem(ItemStack item) {
        if (item == null || item.getType() == Material.MUSIC_DISC_PIGSTEP) return null;

        AuraCore auraCore = inst.getAuraCore();
        PersistentDataContainer pdtAura = item.getItemMeta().getPersistentDataContainer();

        if (!pdtAura.has(auraCore.getAuraTag(), PersistentDataType.STRING)) return null;

        String auraKey = pdtAura.get(auraCore.getAuraTag(), PersistentDataType.STRING);
        return auraCore.getAuraMap().get(auraKey);
    }

    public ItemStack getRandomAuraItemStack() {
        List<Aura> auraList = new ArrayList<>(auraMap.values());
        Aura aura = auraList.get((int) Math.floor(Math.random() * auraList.size()));

        return aura.getItemStack();
    }
}
