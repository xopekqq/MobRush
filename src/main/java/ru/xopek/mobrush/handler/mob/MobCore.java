package ru.xopek.mobrush.handler.mob;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;
import ru.xopek.mobrush.util.MathUtils;

@Getter
public class MobCore {

    private final NamespacedKey mobTag = NamespacedKey.minecraft("mobrush");
    private final NamespacedKey mobReward = NamespacedKey.minecraft("mobreward");


    public LivingEntity createMob(MobTypeEnum mobTypeEnum, int difficulty) {
        LivingEntity entity = null;

        World world = Bukkit.getWorld("world");
        Location spawnLocation = MathUtils.randomizeLocation(world.getSpawnLocation().clone());

        switch (mobTypeEnum) {
            case FALLEN_WARRIOR -> {
                entity = createGenericMob(EntityType.ZOMBIE, "Павший воин", 50.0, difficulty * 85, spawnLocation);

                ((Zombie) entity).setShouldBurnInDay(false);
                ((Zombie) entity).setBaby(false);
            }
            case SNIPER -> {
                entity = createGenericMob(EntityType.SKELETON, "Снайпер", 150.0, difficulty * 215, spawnLocation);

                ((Skeleton) entity).setShouldBurnInDay(false);

                modifyMobAttribute(entity, Attribute.GENERIC_MOVEMENT_SPEED, 1.25, true);
                modifyMobAttribute(entity, Attribute.GENERIC_ATTACK_SPEED, 1.3, true);
            }
            case NIGHT_HUNTER -> {
                entity = createGenericMob(EntityType.CAVE_SPIDER, "Ночной охотник", 300.0, difficulty * 550, spawnLocation);

                modifyMobAttribute(entity, Attribute.GENERIC_MOVEMENT_SPEED, 1.4, true);
            }
        }

        return entity;
    }

    private LivingEntity createGenericMob(EntityType entityType, String name, double health, int reward, Location location) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        entity.setCustomName(name);
        entity.setCustomNameVisible(true);
        entity.getPersistentDataContainer().set(mobTag, PersistentDataType.STRING, name);
        entity.getPersistentDataContainer().set(mobReward, PersistentDataType.INTEGER, reward);

        modifyMobAttribute(entity, Attribute.GENERIC_MAX_HEALTH, health, true);

        return entity;
    }

    public void modifyMobAttribute(LivingEntity mob, Attribute attribute, double value, boolean multiply) {
        AttributeInstance attrInstance = mob.getAttribute(attribute);

        if (attrInstance == null) return;

        if (attribute == Attribute.GENERIC_MAX_HEALTH) {
            double currentMaxHealth = attrInstance.getBaseValue();
            attrInstance.setBaseValue(currentMaxHealth + value);
            mob.setHealth(mob.getHealth() + value);
        } else {
            if (multiply) {
                double currentValue = attrInstance.getBaseValue();
                attrInstance.setBaseValue(currentValue * value);
            } else attrInstance.setBaseValue(value);
        }
    }


}
