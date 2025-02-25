package ru.xopek.mobrush;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import ru.xopek.mobrush.handler.aura.AuraCore;
import ru.xopek.mobrush.handler.database.MongoDB;
import ru.xopek.mobrush.handler.item.ItemCore;
import ru.xopek.mobrush.handler.item.ItemEvents;
import ru.xopek.mobrush.handler.mob.MobCore;
import ru.xopek.mobrush.handler.rush.RushCommand;
import ru.xopek.mobrush.handler.rush.RushCore;
import ru.xopek.mobrush.handler.rush.RushEvents;

@Getter
public final class MobRush extends JavaPlugin {

    private RushCore rushCore;
    private AuraCore auraCore;
    private MobCore mobCore;
    private ItemCore itemCore;

    private MongoDB database;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        /**
         * Инициализация основных механик, таких как бд и кор мобов
         */

        this.database = new MongoDB(this);
        this.rushCore = new RushCore(this);
        this.auraCore = new AuraCore(this);
        this.itemCore = new ItemCore();
        this.mobCore = new MobCore();

        Bukkit.getPluginCommand("mobrush").setExecutor(new RushCommand(this));

        Bukkit.getScheduler().runTaskTimer(this, rushCore::update, 0L, 20L);

        Bukkit.getPluginManager().registerEvents(new RushEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemEvents(this), this);
    }

    @Override
    public void onDisable() {
        /**
         * Убийство всех мобов перед выключением плагина
         * Сделано для того, чтобы не стакалось слишком много мобов
         */

        for (LivingEntity entity : rushCore.getEntities())
            entity.setHealth(0);

        database.close();
    }
}
