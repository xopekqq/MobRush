package ru.xopek.mobrush.handler.aura;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter @AllArgsConstructor
public class Aura {
    private ItemStack itemStack;
    private Attributes attributes;

    @Getter @AllArgsConstructor
    public static class Attributes {
        private int xp;
        private int money;
        private int damage;
    }
}
