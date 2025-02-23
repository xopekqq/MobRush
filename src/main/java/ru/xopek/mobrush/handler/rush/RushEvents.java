package ru.xopek.mobrush.handler.rush;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.xopek.mobrush.MobRush;
import ru.xopek.mobrush.handler.aura.Aura;
import ru.xopek.mobrush.handler.database.RushPlayer;
import ru.xopek.mobrush.handler.database.SQLite;
import ru.xopek.mobrush.util.InventoryUtils;
import ru.xopek.mobrush.util.MobUtils;
import ru.xopek.mobrush.util.StringAPI;

public class RushEvents implements Listener {

    /**
     * Класс с основными
     *  ивентами для мини игры.
     */

    @EventHandler
    public void onEntityDeath(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player && e.getEntity() instanceof LivingEntity target) {
            PersistentDataContainer pdt = target.getPersistentDataContainer();

            if (pdt.has(MobUtils.mobTag, PersistentDataType.STRING)) {
                String name = pdt.get(MobUtils.mobTag, PersistentDataType.STRING);

                ItemStack item = player.getInventory().getItem(8);
                Aura aura = MobRush.getInst().getAuraCore().getAuraFromItem(item);

                if (aura != null && aura.getAttributes().getDamage() > 0) {
                    double multiplier = (aura.getAttributes().getDamage() + 100.0) / 100.0;

                    e.setDamage(e.getDamage() * multiplier);
                }

                target.setCustomName(name + StringAPI.asColor(" &c" + Math.max(0, Math.floor(target.getHealth() - e.getDamage())) + "&f/&c" + target.getMaxHealth()));
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity target = e.getEntity();
        PersistentDataContainer pdt = target.getPersistentDataContainer();

        if (pdt.has(MobUtils.mobTag, PersistentDataType.STRING)) {
            Player killer = target.getKiller();
            String uuid = killer != null ? killer.getUniqueId().toString() : null;

            if (uuid != null) {
                MobRush inst = MobRush.getInst();

                SQLite database = inst.getDatabase();
                RushPlayer rushPlayer = database.getPlayer(uuid);

                if (rushPlayer == null) {
                    rushPlayer = new RushPlayer(uuid, killer.getName(), 0, 0);
                }

                Integer reward = pdt.get(MobUtils.mobReward, PersistentDataType.INTEGER);
                if (reward != null) {
                    ItemStack item = killer.getInventory().getItem(8);
                    Aura aura = MobRush.getInst().getAuraCore().getAuraFromItem(item);

                    double moneyMultiplier = 1.0;
                    double xpMultiplier = 1.0;

                    if (aura != null) {
                        Aura.Attributes attributes = aura.getAttributes();

                        if (attributes.getMoney() > 0) moneyMultiplier = (attributes.getMoney() + 100.0) / 100.0;
                        if (attributes.getXp() > 0) xpMultiplier = (attributes.getXp() + 100.0) / 100.0;
                    }

                    rushPlayer.increaseMoney((int) Math.round(reward * moneyMultiplier));
                    rushPlayer.increaseXP((int) Math.round(MobUtils.getXP(e.getEntityType()) * xpMultiplier));

                    database.savePlayer(rushPlayer);

                    killer.sendMessage(StringAPI.asColor("&#00FF00[$]&f Вы получили &#00FF00" + reward * moneyMultiplier + "&f монет за убийство!"));
                }

                inst.getRushCore().getEntities().remove(target);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(InventoryUtils.title)) return;

        e.setCancelled(true);
        ItemStack currentItem = e.getCurrentItem();

        if (currentItem == null) return;

        PersistentDataContainer pdt = currentItem.getItemMeta().getPersistentDataContainer();
        if (pdt.has(InventoryUtils.itemTag, PersistentDataType.STRING)) {
            String name = pdt.get(InventoryUtils.itemTag, PersistentDataType.STRING);

            HumanEntity whoClicked = e.getWhoClicked();
            String uuid = whoClicked.getUniqueId().toString();

            SQLite database = MobRush.getInst().getDatabase();
            RushPlayer rushPlayer = database.getPlayer(uuid);

            if (rushPlayer == null) {
                rushPlayer = new RushPlayer(uuid, whoClicked.getName(), 0, 0);
            }

            switch (name) {
                case "health" -> {
                    int healthPrice = InventoryUtils.getHealthPrice(whoClicked.getMaxHealth());

                    if (rushPlayer.getMoney() >= (healthPrice * 10) && rushPlayer.getXp() >= healthPrice) {
                        rushPlayer.decreaseMoney(healthPrice * 10);
                        database.savePlayer(rushPlayer);

                        whoClicked.setMaxHealth(whoClicked.getMaxHealth() + 2);
                        InventoryUtils.buildShop((Player) whoClicked);

                        whoClicked.sendMessage("Успешная покупка!");
                    }
                }
                case "aura" -> {
                    if (rushPlayer.getMoney() >= 500 && rushPlayer.getXp() >= 225) {
                        rushPlayer.decreaseMoney(500);
                        database.savePlayer(rushPlayer);

                        whoClicked.getInventory().addItem(MobRush.getInst().getAuraCore().getRandomAuraItemStack());
                        InventoryUtils.buildShop((Player) whoClicked);

                        whoClicked.sendMessage("Успешная покупка!");
                    }
                }
            }
        }
    }
}