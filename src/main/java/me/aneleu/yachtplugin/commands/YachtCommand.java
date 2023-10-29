package me.aneleu.yachtplugin.commands;

import me.aneleu.yachtplugin.YachtGame;
import me.aneleu.yachtplugin.YachtPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class YachtCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player p) {

            if (args[0].equalsIgnoreCase("start")) {

                if (args.length != 3) {
                    p.sendMessage(Component.text("/yacht start <player1> <player2>").color(NamedTextColor.GRAY));
                    return true;
                }
                
                if (YachtPlugin.game != null) {
                    p.sendMessage(Component.text("게임이 이미 진행 중입니다.").color(NamedTextColor.GRAY));
                    return true;
                }

                Player p1 = Bukkit.getPlayer(args[1]);
                Player p2 = Bukkit.getPlayer(args[2]);
                if (p1 == null || p2 == null) {
                    p.sendMessage(Component.text("존재하지 않는 플레이어입니다.").color(NamedTextColor.RED));
                    return true;
                }

                YachtPlugin.game = new YachtGame(p1, p2);

            } else if (args[0].equalsIgnoreCase("stop")) {
                if (YachtPlugin.game == null) {
                    p.sendMessage(Component.text("진행 중인 게임이 없습니다.").color(NamedTextColor.GRAY));
                } else {
                    YachtPlugin.game.stop();
                    p.sendMessage(Component.text("게임을 종료했습니다.").color(NamedTextColor.YELLOW));
                }
            }

        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            List<String> arg1 = new ArrayList<>();
            arg1.add("start");
            arg1.add("stop");
            return arg1;
        }
        if (args[0].equalsIgnoreCase("start") && (args.length == 2 || args.length == 3)) {
            List<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (int i = 0; i < players.length; i++) {
                playerNames.add(players[i].getName());
            }
            return playerNames;

        }

        return List.of();
    }
}
