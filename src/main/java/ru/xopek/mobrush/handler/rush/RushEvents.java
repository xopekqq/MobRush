package ru.xopek.mobrush.handler.rush;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.xopek.mobrush.MobRush;
import ru.xopek.mobrush.handler.aura.Aura;
import ru.xopek.mobrush.handler.database.MongoDB;
import ru.xopek.mobrush.handler.database.RushPlayer;
import ru.xopek.mobrush.util.InventoryUtils;
import ru.xopek.mobrush.util.MathUtils;
import ru.xopek.mobrush.util.StringUtils;

public class RushEvents implements Listener {

    /**
     * Класс с основными
     * ивентами для мини игры.
     */

    private final MobRush inst;

    public RushEvents(MobRush inst) {
        this.inst = inst;
    }

    @EventHandler
    public void onEntityDeath(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player) || !(event.getEntity() instanceof LivingEntity target)) return;

        PersistentDataContainer pdt = target.getPersistentDataContainer();

        NamespacedKey mobTag = inst.getMobCore().getMobTag();

        if (!pdt.has(mobTag, PersistentDataType.STRING)) return;

        String name = pdt.get(mobTag, PersistentDataType.STRING);

        ItemStack item = player.getInventory().getItem(8);
        Aura aura = inst.getAuraCore().getAuraFromItem(item);

        if (aura != null && aura.getAttributes().getDamage() > 0) {
            double multiplier = (aura.getAttributes().getDamage() + 100.0) / 100.0;

            event.setDamage(event.getDamage() * multiplier);
        }

        target.setCustomName(name + StringUtils.asColor(" &c" + Math.max(0, Math.floor(target.getHealth() - event.getDamage())) + "&f/&c" + target.getMaxHealth()));

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity target = event.getEntity();
        PersistentDataContainer pdt = target.getPersistentDataContainer();

        NamespacedKey mobReward = inst.getMobCore().getMobReward();

        if (!pdt.has(mobReward, PersistentDataType.INTEGER)) return;

        Player killer = target.getKiller();
        String uuid = killer != null ? killer.getUniqueId().toString() : null;

        if (uuid == null) return;

        Bukkit.getScheduler().runTask(inst, () -> {
            MongoDB database = inst.getDatabase();
            RushPlayer rushPlayer = database.getPlayer(uuid);

            Integer reward = pdt.get(mobReward, PersistentDataType.INTEGER);

            ItemStack item = killer.getInventory().getItem(8);
            Aura aura = inst.getAuraCore().getAuraFromItem(item);

            double moneyMultiplier = 1.0;
            double xpMultiplier = 1.0;

            if (aura != null) {
                Aura.Attributes attributes = aura.getAttributes();

                if (attributes.getMoney() > 0) moneyMultiplier = (attributes.getMoney() + 100.0) / 100.0;
                if (attributes.getXp() > 0) xpMultiplier = (attributes.getXp() + 100.0) / 100.0;
            }

            rushPlayer.increaseMoney((int) Math.round(reward * moneyMultiplier));
            rushPlayer.increaseXP((int) Math.round(MathUtils.getXP(event.getEntityType(), inst.getRushCore().getDifficulty()) * xpMultiplier));

            database.savePlayer(rushPlayer);

            killer.sendMessage(StringUtils.asColor("&#00FF00[$]&f Вы получили &#00FF00" + reward * moneyMultiplier + "&f монет за убийство!"));

            inst.getRushCore().getEntities().remove(target);
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(InventoryUtils.title)) return;

        event.setCancelled(true);
        ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null) return;

        PersistentDataContainer pdt = currentItem.getItemMeta().getPersistentDataContainer();
        if (!pdt.has(InventoryUtils.itemTag, PersistentDataType.STRING)) return;

        String name = pdt.get(InventoryUtils.itemTag, PersistentDataType.STRING);

        HumanEntity whoClicked = event.getWhoClicked();

        PlayerInventory inventory = whoClicked.getInventory();
        String uuid = whoClicked.getUniqueId().toString();

        Bukkit.getScheduler().runTask(inst, () -> {
            MongoDB database = inst.getDatabase();
            RushPlayer rushPlayer = database.getPlayer(uuid);

            int money = rushPlayer.getMoney();
            int xp = rushPlayer.getXp();

            switch (name) {
                case "health" -> {
                    int healthPrice = InventoryUtils.getHealthPrice(whoClicked.getMaxHealth());

                    if (money <= (healthPrice * 10) && xp <= healthPrice) return;

                    rushPlayer.decreaseMoney(healthPrice * 10);
                    database.savePlayer(rushPlayer);

                    whoClicked.setMaxHealth(whoClicked.getMaxHealth() + 2);
                    whoClicked.sendMessage("Успешная покупка!");

                    InventoryUtils.buildShop((Player) whoClicked, inst);
                }
                case "aura" -> {
                    if (money <= 500 && xp <= 225) return;

                    rushPlayer.decreaseMoney(500);
                    database.savePlayer(rushPlayer);

                    inventory.addItem(inst.getAuraCore().getRandomAuraItemStack());
                    whoClicked.sendMessage("Успешная покупка!");

                    InventoryUtils.buildShop((Player) whoClicked, inst);
                }
                case "rebirth" -> {
                    int rebirth = rushPlayer.getRebirth();

                    if (money <= (rebirth * 125) && xp <= (rebirth * 75)) return;

                    rushPlayer.setMoney(0);
                    rushPlayer.setXp(0);
                    rushPlayer.increaseRebirth(1);

                    database.savePlayer(rushPlayer);

                    PotionEffect potionEffect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, rebirth * 600, rebirth);

                    whoClicked.addPotionEffect(potionEffect);
                    whoClicked.sendMessage("Успешная покупка!");

                    InventoryUtils.buildShop((Player) whoClicked, inst);
                }
                case "godeye" -> handlePurchase(database, whoClicked, rushPlayer, name, 100);
                case "thunder" -> handlePurchase(database, whoClicked, rushPlayer, name, 250);
                case "fireshow" -> handlePurchase(database, whoClicked, rushPlayer, name, 300);
            }
        });
    }

    private void handlePurchase(MongoDB database, HumanEntity whoClicked, RushPlayer rushPlayer, String name, int cost) {
        int money = rushPlayer.getMoney();
        if (money < cost) {
            whoClicked.sendMessage("Недостаточно монет для покупки.");
            return;
        }

        rushPlayer.decreaseMoney(cost);
        database.savePlayer(rushPlayer);

        whoClicked.getInventory().addItem(inst.getItemCore().getItems().get(name));
        whoClicked.sendMessage("Успешная покупка!");

        InventoryUtils.buildShop((Player) whoClicked, inst);
    }
}