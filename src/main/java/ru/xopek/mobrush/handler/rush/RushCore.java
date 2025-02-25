package ru.xopek.mobrush.handler.rush;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import ru.xopek.mobrush.MobRush;
import ru.xopek.mobrush.handler.mob.MobTypeEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RushCore {

    /**
     * Основная механика спавна мобов
     * Спавн в центре карты (world.getSpawnLocation()).
     */

    private final MobRush inst;

    private int difficulty = 0;
    private final List<LivingEntity> entities = new ArrayList<>();

    public RushCore(MobRush inst) {
        this.inst = inst;
    }

    public void update() {
        if (entities.isEmpty()) {
            difficulty++;
            spawnRandomEntity(MobTypeEnum.FALLEN_WARRIOR, 4);
            spawnRandomEntity(MobTypeEnum.SNIPER, 2);
            spawnRandomEntity(MobTypeEnum.NIGHT_HUNTER, 1);
        }

        /**
         * Павший воин ночью охотится быстрее, а
         * утром более уязвим из за сниженной скорости
         */

        World world = Bukkit.getWorld("world");
        for (LivingEntity entity : entities) {
            if (entity.getType() != EntityType.ZOMBIE) continue;

            inst.getMobCore().modifyMobAttribute(entity, Attribute.GENERIC_MOVEMENT_SPEED, world.isDayTime() ? 0.3 : 0.4, false);
        }

    }

    public void spawnRandomEntity(MobTypeEnum mobTypeEnum, int count) {
        for (int i = 0; i < (difficulty * count); i++) {
            LivingEntity entity = inst.getMobCore().createMob(mobTypeEnum, difficulty);
            entities.add(entity);
        }
    }
}
