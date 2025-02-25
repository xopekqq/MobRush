package ru.xopek.mobrush.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import ru.xopek.mobrush.MobRush;

public class ParticleUtils {
    public static void startParticleWave(MobRush inst, Location location, Particle particle, double maxRadius, double step, int points) {
        final double[] currentRadius = {1.0};

        Bukkit.getScheduler().runTaskTimerAsynchronously(inst, task -> {
            if (currentRadius[0] > maxRadius) {
                task.cancel();
                return;
            }

            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = currentRadius[0] * Math.cos(angle);
                double z = currentRadius[0] * Math.sin(angle);
                location.getWorld().spawnParticle(particle, location.clone().add(x, 1, z), 1, 0, 0, 0, 0);
            }

            currentRadius[0] += step;
        }, 0L, 1L);
    }
}
