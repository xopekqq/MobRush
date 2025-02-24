package ru.xopek.mobrush.util;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class MathUtils {
    public static double randomMinMax(int min, int max) {
        return (Math.random() * (max - min + 1)) + min;
    }

    public static Location randomizeLocation(Location location) {
        return location.add(randomMinMax(-25, 25), 0, randomMinMax(-25, 25));
    }

    public static int getXP(EntityType entityType, int difficulty) {
        int tier = switch (entityType) {
            case SKELETON -> 2;
            case CAVE_SPIDER -> 3;
            default -> 1;
        };
        int multiplier = tier > 1 ? tier * 2 : tier;

        return 10 + (multiplier * difficulty * tier);
    }
}
