package ru.xopek.mobrush.handler.item;

import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.xopek.mobrush.MobRush;
import ru.xopek.mobrush.util.ParticleUtils;

public class ItemEvents implements Listener {

    private final MobRush inst;

    public ItemEvents(MobRush inst) {
        this.inst = inst;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item == null) return;

        PersistentDataContainer pdt = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey itemKey = inst.getItemCore().getItemKey();

        if (!pdt.has(itemKey, PersistentDataType.STRING)) return;

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        String tag = pdt.get(itemKey, PersistentDataType.STRING);

        if (player.hasCooldown(item.getType())) return;

        switch (tag) {
            case "godeye" -> {
                item.setAmount(item.getAmount() - 1);

                ParticleUtils.startParticleWave(inst, player.getLocation().clone(), Particle.CLOUD, 40, 5, 75);
                player.getWorld().getEntities()
                        .stream()
                        .filter(target -> target instanceof LivingEntity)
                        .filter(target -> !target.equals(player))
                        .filter(target -> target.getLocation().distance(player.getLocation()) <= 40)
                        .map(target -> (LivingEntity) target)
                        .forEach(target -> target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 160, 0)));

                player.setCooldown(item.getType(), 600);
            }
            case "thunder" -> {
                item.setAmount(item.getAmount() - 1);

                ParticleUtils.startParticleWave(inst, player.getLocation().clone(), Particle.CLOUD, 10, 5, 75);
                player.getWorld().getEntities()
                        .stream()
                        .filter(target -> target instanceof LivingEntity)
                        .filter(target -> !target.equals(player))
                        .filter(target -> target.getLocation().distance(player.getLocation()) <= 10)
                        .map(target -> (LivingEntity) target)
                        .forEach(target -> target.getWorld().strikeLightning(target.getLocation()));

                player.setCooldown(item.getType(), 1200);
            }
            case "fireshow" -> {
                item.setAmount(item.getAmount() - 1);

                ParticleUtils.startParticleWave(inst, player.getLocation().clone(), Particle.FLAME, 15, 5, 75);
                player.getWorld().getEntities()
                        .stream()
                        .filter(target -> target instanceof LivingEntity)
                        .filter(target -> !target.equals(player))
                        .filter(target -> target.getLocation().distance(player.getLocation()) <= 15)
                        .map(target -> (LivingEntity) target)
                        .forEach(target -> target.setFireTicks(120));

                player.setCooldown(item.getType(), 1200);
            }
        }

    }
}
