package ru.xopek.mobrush;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import ru.xopek.mobrush.handler.aura.AuraCore;
import ru.xopek.mobrush.handler.rush.RushCommand;
import ru.xopek.mobrush.handler.rush.RushCore;
import ru.xopek.mobrush.handler.rush.RushEvents;
import ru.xopek.mobrush.handler.database.SQLite;

import java.io.File;

@Getter
public final class MobRush extends JavaPlugin {

    @Getter
    private static MobRush inst;
    private RushCore rushCore;
    private AuraCore auraCore;
    private SQLite database;

    @Override
    public void onEnable() {
        inst = this;

        this.saveDefaultConfig();

        /**
         * Инициализация основных механик, таких как бд и кор мобов
         */

        database = new SQLite(new File(this.getDataFolder(), "database.db"));
        rushCore = new RushCore();
        auraCore = new AuraCore();

        auraCore.setup();

        Bukkit.getPluginCommand("mobrush").setExecutor(new RushCommand());

        Bukkit.getScheduler().runTaskTimer(this, rushCore::update, 0L, 20L);
        Bukkit.getPluginManager().registerEvents(new RushEvents(), this);
    }

    @Override
    public void onDisable() {
        /**
         * Убийство всех мобов перед выключением
         */
        for (LivingEntity entity : rushCore.getEntities()) {
            entity.setHealth(0);
        }
    }
}
