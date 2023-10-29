package me.aneleu.yachtplugin.tasks;

import me.aneleu.yachtplugin.YachtGame;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class YachtTask extends BukkitRunnable {

    YachtGame game;
    int dice_count;
    int time = 0;

    Player p1;
    Player p2;

    World w;

    List<Location> loc = new ArrayList<>();
    List<BlockDisplay> dice = new ArrayList<>();

    final Transformation trans0 = new Transformation(new Vector3f(0.4F, 0.82F, 0.4F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f(0, 0, 0, 0));
    final Transformation trans1 = new Transformation(new Vector3f(0.1F, 0.82F, 0.1F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f(0, 0, 0, 0));
    final Transformation trans2 = new Transformation(new Vector3f(0.7F, 0.82F, 0.7F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f(0, 0, 0, 0));
    final Transformation trans3 = new Transformation(new Vector3f(0.7F, 0.82F, 0.1F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f(0, 0, 0, 0));
    final Transformation trans4 = new Transformation(new Vector3f(0.1F, 0.82F, 0.7F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f(0, 0, 0, 0));
    final Transformation trans5 = new Transformation(new Vector3f(0.1F, 0.82F, 0.4F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f(0, 0, 0, 0));
    final Transformation trans6 = new Transformation(new Vector3f(0.7F, 0.82F, 0.4F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f(0, 0, 0, 0));

    public YachtTask(YachtGame game) {
        this.game = game;

        dice_count = game.dice_count;
        p1 = game.p1;
        p2 = game.p2;
        w = game.w;

        loc.add(new Location(w, -3616.5, 67.5, 3747.5));
        loc.add(new Location(w, -3616.5, 67.5, 3744.5));
        loc.add(new Location(w, -3616.5, 67.5, 3741.5));
        loc.add(new Location(w, -3616.5, 67.5, 3738.5));
        loc.add(new Location(w, -3616.5, 67.5, 3735.5));

        game.n_selected_dice.clear();
        game.dice_display.clear();
        game.interactionList.clear();
        game.noon.clear();
        for (int i = 0; i < 5; i ++) {
            game.noon.put(i, new ArrayList<>());
        }

    }

    private void summonDice(int... elements) {
        for (int i: elements) {
            BlockDisplay d = (BlockDisplay) w.spawnEntity(loc.get(i), EntityType.BLOCK_DISPLAY);
            d.addScoreboardTag("ya");
            d.addScoreboardTag("ya.dice");
            d.addScoreboardTag("ya.dice."+i);

            d.setBlock(Material.WHITE_CONCRETE.createBlockData());

            game.dice_display.add(d);
            dice.add(d);

            Interaction inct = (Interaction) w.spawnEntity(loc.get(i).add(0.5, 0, 0.5), EntityType.INTERACTION);
            inct.addScoreboardTag("ya");
            inct.addScoreboardTag("ya.inct");
            inct.addScoreboardTag("ya.inct."+i);

            game.interactionList.add(inct);

        }
    }

    private BlockDisplay summonNoon(Location loc, Transformation trans) {
        BlockDisplay d = (BlockDisplay) w.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        d.setBlock(Material.BLACK_CONCRETE.createBlockData());
        d.setTransformation(trans);
        d.addScoreboardTag("ya");
        d.addScoreboardTag("ya.noon");
        return d;
    }

    private void setDice(BlockDisplay bd, int num) {
        int i = -1;
        Set<String> tags = bd.getScoreboardTags();
        for (int j = 0; j < 5; j++) {
            if (tags.contains("ya.dice."+j)) {
                i = j;
                break;
            }
        }

        List<BlockDisplay> noon_displays = new ArrayList<>();
        if (num == 1) {
            noon_displays.add(summonNoon(bd.getLocation(), trans0));
        } else if (num == 2) {
            noon_displays.add(summonNoon(bd.getLocation(), trans3));
            noon_displays.add(summonNoon(bd.getLocation(), trans4));
        } else if (num == 3) {
            noon_displays.add(summonNoon(bd.getLocation(), trans0));
            noon_displays.add(summonNoon(bd.getLocation(), trans3));
            noon_displays.add(summonNoon(bd.getLocation(), trans4));
        } else if (num == 4) {
            noon_displays.add(summonNoon(bd.getLocation(), trans1));
            noon_displays.add(summonNoon(bd.getLocation(), trans2));
            noon_displays.add(summonNoon(bd.getLocation(), trans3));
            noon_displays.add(summonNoon(bd.getLocation(), trans4));
        } else if (num == 5) {
            noon_displays.add(summonNoon(bd.getLocation(), trans0));
            noon_displays.add(summonNoon(bd.getLocation(), trans1));
            noon_displays.add(summonNoon(bd.getLocation(), trans2));
            noon_displays.add(summonNoon(bd.getLocation(), trans3));
            noon_displays.add(summonNoon(bd.getLocation(), trans4));
        } else if (num == 6) {
            noon_displays.add(summonNoon(bd.getLocation(), trans1));
            noon_displays.add(summonNoon(bd.getLocation(), trans2));
            noon_displays.add(summonNoon(bd.getLocation(), trans3));
            noon_displays.add(summonNoon(bd.getLocation(), trans4));
            noon_displays.add(summonNoon(bd.getLocation(), trans5));
            noon_displays.add(summonNoon(bd.getLocation(), trans6));
        }
        if (game.noon.containsKey(i)) {
            for (BlockDisplay noon: game.noon.get(i)) {
                noon.remove();
            }
        }
        game.noon.put(i, noon_displays);
    }

    private void roll() {
        for (BlockDisplay bd: dice) {
            setDice(bd, (int) (Math.random()*6) + 1);
        }
    }

    private void end(int num) {
        w.spawnParticle(Particle.TOTEM, dice.get(0).getLocation().add(0.5, 0.3, 0.5), 10, 0, 0, 0, 0.3);

        int n = 0;
        for (Entity near_e: dice.get(0).getNearbyEntities(1, 1, 1)) {
            if (near_e.getScoreboardTags().contains("ya.noon")) {
                n ++;
            }
        }
        game.n_selected_dice.add(n);

        dice.remove(0);
        p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.2F, 1.5F);
        p2.playSound(p2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.2F, 1.5F);
        if (dice_count == num) {
            game.phase = 2;
            game.scoreboard_calc();
            game.task.cancel();
            game.task = null;
        }
    }

    @Override
    public void run() {

        time ++;

        if (time == 1) {

            if (dice_count == 1) {
                summonDice(2);
            } else if (dice_count == 2) {
                summonDice(1, 3);
            } else if (dice_count == 3) {
                summonDice(0, 2, 4);
            } else if (dice_count == 4) {
                summonDice(0, 1, 3, 4);
            } else if (dice_count == 5) {
                summonDice(0, 1, 2, 3, 4);
            }
            p1.playSound(p1.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.1F, 1.0F);
            p2.playSound(p2.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.1F, 1.0F);

            roll();

        }

        if (time >= 10 && time % 2 == 0) {
            roll();
            p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.1F, 1.0F);
            p2.playSound(p2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.1F, 1.0F);
        }

        if (time == 40) {
            end(1);
        } else if (time == 50) {
            end(2);
        } else if (time == 60) {
            end(3);
        } else if (time == 70) {
            end(4);
        } else if (time == 80) {
            end(5);
        }

    }

}
