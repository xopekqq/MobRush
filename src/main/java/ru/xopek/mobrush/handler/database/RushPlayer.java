package ru.xopek.mobrush.handler.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class RushPlayer {
    private final String uuid;
    private final String name;
    private int money;
    private int xp;

    public void increaseMoney(int amount) {
        this.money += amount;
    }

    public void decreaseMoney(int amount) {
        this.money -= amount;
    }

    public void increaseXP(int amount) {
        this.xp += amount;
    }

    public void decreaseXP(int amount) {
        this.xp -= amount;
    }
}
