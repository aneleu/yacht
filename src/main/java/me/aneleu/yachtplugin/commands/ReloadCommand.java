package me.aneleu.yachtplugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Bukkit.broadcast(Component.text("RELOADING...").color(NamedTextColor.BLUE));
        Bukkit.getServer().reload();
        Bukkit.broadcast(Component.text("RELOAD COMPLETE.").color(NamedTextColor.AQUA));

        return true;
    }
}
