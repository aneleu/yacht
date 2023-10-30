package me.aneleu.yachtplugin;

import me.aneleu.yachtplugin.commands.ReloadCommand;
import me.aneleu.yachtplugin.commands.YachtCommand;
import me.aneleu.yachtplugin.listeners.YachtListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class YachtPlugin extends JavaPlugin {

    public static YachtPlugin plugin;
    public static YachtGame game = null;


    @Override
    public void onEnable() {

        plugin = this;

        getServer().getPluginManager().registerEvents(new YachtListener(), this);

        getCommand("r").setExecutor(new ReloadCommand());

        YachtCommand command = new YachtCommand();
        getCommand("yacht").setExecutor(command);
        getCommand("yacht").setTabCompleter(command);

    }

    @Override
    public void onDisable() {
        if (game != null) {
            game.stop();
        }
    }

}
