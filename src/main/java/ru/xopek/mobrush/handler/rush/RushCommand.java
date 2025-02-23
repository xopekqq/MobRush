package ru.xopek.mobrush.handler.rush;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.xopek.mobrush.MobRush;
import ru.xopek.mobrush.handler.database.RushPlayer;
import ru.xopek.mobrush.handler.database.SQLite;
import ru.xopek.mobrush.util.InventoryUtils;
import ru.xopek.mobrush.util.StringAPI;

import java.util.Locale;
import java.util.UUID;

public class RushCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(StringAPI.asColor("&#FF0000Введите полную команду. /mobrush help"));
            return false;
        }

        MobRush inst = MobRush.getInst();
        SQLite database = inst.getDatabase();

        String uuid = player.getUniqueId().toString();
        RushPlayer rushPlayer = database.getPlayer(uuid);

        if (rushPlayer == null) {
            rushPlayer = new RushPlayer(uuid, player.getName(), 0, 0);
        }

        String subcmd = args[0].toLowerCase(Locale.ROOT);
        switch (subcmd) {
            case "help" -> {
                player.sendMessage(StringAPI.asColor("&6Помощь:\n/mobrush balance&f - Выводит ваш баланс\n&6/mobrush xp&f - Выводит ваш опыт\n&6/mobrush shop&f - Магазин мини-игры"));

                if (player.hasPermission("mobrush.admin")) {
                    player.sendMessage(StringAPI.asColor("&c/mobrush give <money/xp> <name> <amount>&f - выдача монет/опыта игроку"));
                    player.sendMessage(StringAPI.asColor("&c/mobrush remove <money/xp> <name> <amount>&f - забрать монеты/опыт у игрока"));
                }
            }
            case "xp" -> player.sendMessage(StringAPI.asColor("&9[+]&f Ваш опыт: " + rushPlayer.getXp()));
            case "balance" -> player.sendMessage(StringAPI.asColor("&#00FF00[$]&f Ваш баланс: " + rushPlayer.getMoney()));
            case "shop" -> InventoryUtils.buildShop(player);
            case "remove", "give" -> {
                if (args.length < 4) {
                    player.sendMessage("Недостаточно аргументов!");
                    return false;
                }

                String currency = args[1].toLowerCase(Locale.ROOT);
                Player target = Bukkit.getPlayer(args[2]);
                int amount = Integer.parseInt(args[3]);

                if (target == null) {
                    player.sendMessage("Игрок не найден!");
                    return false;
                }

                String targetUUID = target.getUniqueId().toString();
                RushPlayer targetRushPlayer = database.getPlayer(targetUUID);

                if (targetRushPlayer == null) {
                    targetRushPlayer = new RushPlayer(targetUUID, target.getName(), 0, 0);
                }

                if (currency.equals("money")) {
                    if (subcmd.equals("give")) targetRushPlayer.increaseMoney(amount);
                    else targetRushPlayer.decreaseMoney(amount);

                    player.sendMessage("Успешно");
                } else if (currency.equals("xp")) {
                    if (subcmd.equals("give")) targetRushPlayer.increaseXP(amount);
                    else targetRushPlayer.decreaseXP(amount);

                    player.sendMessage("Успешно");
                } else {
                    player.sendMessage("Неизвестная валюта. Доступно: money или xp");
                }

                database.savePlayer(targetRushPlayer);
            }
        }

        return false;
    }
}
