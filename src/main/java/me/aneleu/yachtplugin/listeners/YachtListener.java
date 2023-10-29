package me.aneleu.yachtplugin.listeners;

import me.aneleu.yachtplugin.YachtPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

public class YachtListener implements Listener {

    @EventHandler
    public void onDiceRoll(PlayerInteractEvent e) {

        if (YachtPlugin.game != null) {

            Player p = e.getPlayer();
            Component itemDisplayName = p.getInventory().getItemInMainHand().displayName();
            PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
            String itemName = plainSerializer.serialize(itemDisplayName);
            if (itemName.equalsIgnoreCase("[주사위 굴리기]")) {
                if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                    YachtPlugin.game.dice(p);
                    e.setCancelled(true);
                }
            }
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                RayTraceResult ray_result = p.rayTraceEntities(30, false);
                if (ray_result != null) {
                    Entity ray_e = ray_result.getHitEntity();
                    if (ray_e != null) {
                        if (ray_e.getScoreboardTags().contains("ya.inct")) {
                            YachtPlugin.game.select(p, ray_e);
                        } else if (ray_e.getScoreboardTags().contains("ya.scoreboard")) {
                            YachtPlugin.game.scoreboard(p, ray_e);
                        }
                    }
                }

            }
            
        }

    }

}
