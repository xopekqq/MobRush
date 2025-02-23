package ru.xopek.mobrush.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import ru.xopek.mobrush.MobRush;
import ru.xopek.mobrush.ems.MobType;

public class MobUtils {
    /**
     * Утилитный класс, чтобы проще
     * создавать кастомных мобов.
     */

    public static NamespacedKey mobTag = NamespacedKey.minecraft("mobrush");
    public static NamespacedKey mobReward = NamespacedKey.minecraft("mobreward");

    private static LivingEntity createGenericMob(EntityType entityType, String name, double health, int reward, Location location) {
        location.add(Math.floor(Math.random() * 26) - 13, 0, Math.floor(Math.random() * 26) - 13);

        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        entity.setCustomName(name);
        entity.setCustomNameVisible(true);
        entity.getPersistentDataContainer().set(mobTag, PersistentDataType.STRING, name);
        entity.getPersistentDataContainer().set(mobReward, PersistentDataType.INTEGER, reward);

        increaseMobHealth(entity, health);

        return entity;
    }

    public static LivingEntity createMob(MobType mobType, int difficulty) {
        LivingEntity entity = null;

        World world = Bukkit.getWorld("world");
        Location spawnLocation = world.getSpawnLocation().clone();

        switch (mobType) {
            case FALLEN_WARRIOR -> {
                entity = createGenericMob(EntityType.ZOMBIE, "Павший воин", 50.0, difficulty * 85, spawnLocation);
                ((Zombie) entity).setShouldBurnInDay(false);
                ((Zombie) entity).setBaby(false);
            }
            case SNIPER -> {
                entity = createGenericMob(EntityType.SKELETON, "Снайпер", 150.0, difficulty * 215, spawnLocation);
                ((Skeleton) entity).setShouldBurnInDay(false);

                increaseMobSpeed(entity, 1.1);
                increaseAttackSpeed(entity, 1.2);
            }
            case NIGHT_HUNTER -> {
                entity = createGenericMob(EntityType.CAVE_SPIDER, "Ночной охотник", 300.0, difficulty * 550, spawnLocation);

                increaseMobSpeed(entity, 1.35);
            }
        }

        return entity;
    }

    public static void increaseMobSpeed(LivingEntity mob, double speedMultiplier) {
        AttributeInstance attribute = mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        if (attribute != null) {
            double currentSpeed = attribute.getValue();
            attribute.setBaseValue(currentSpeed * speedMultiplier);
        }
    }

    public static void setMobSpeed(LivingEntity mob, double speed) {
        AttributeInstance attribute = mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        if (attribute != null) {
            attribute.setBaseValue(speed);
        }
    }

    public static void increaseAttackSpeed(LivingEntity mob, double speedMultiplier) {
        AttributeInstance attribute = mob.getAttribute(Attribute.GENERIC_ATTACK_SPEED);

        if (attribute != null) {
            double currentSpeed = attribute.getValue();
            attribute.setBaseValue(currentSpeed * speedMultiplier);
        }
    }

    public static void increaseMobHealth(LivingEntity mob, double healthIncrease) {
        AttributeInstance healthAttribute = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            double currentMaxHealth = healthAttribute.getBaseValue();
            healthAttribute.setBaseValue(currentMaxHealth + healthIncrease);

            mob.setHealth(mob.getHealth() + healthIncrease);
        }
    }

    public static int getXP(EntityType entityType) {
        int tier = entityType == EntityType.ZOMBIE ? 1 :
                        entityType == EntityType.SKELETON ? 2 :
                        entityType == EntityType.CAVE_SPIDER ? 3 : 1;

        int multiplier = tier > 1 ? tier * 2 : tier;

        return (10 + (multiplier * MobRush.getInst().getRushCore().getDifficulty()) * tier);
    }

}
