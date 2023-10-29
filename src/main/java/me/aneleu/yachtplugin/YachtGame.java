package me.aneleu.yachtplugin;

import me.aneleu.yachtplugin.tasks.YachtTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class YachtGame {

    public Player p1;
    public Player p2;
    public World w;
    
    public int phase = 0; // 게임 상태 (0: 주사위 굴리기 전 / 1: 주사위 굴리는 중 / 2: 주사위 보관 & 점수판 선택 / 9: 게임 종료)
    int round = 1; // 라운드 (1 ~ 12 라운드)
    int cur_player = 0; // 플레이어 턴 (0: p1, 1: p2)
    int roll_dice_count = 0; // 플레이어가 주사위를 던진 횟수
    public int dice_count = 5; // 선택되지 않은 주사위 수

    int[] selected_dice = new int[]{0, 0, 0, 0, 0};
    public List<Integer> n_selected_dice = new ArrayList<>();

    public List<BlockDisplay> dice_display = new ArrayList<>();
    public List<Interaction> interactionList = new ArrayList<>();
    public HashMap<Integer, List<BlockDisplay>> noon = new HashMap<>();

    int[] dice_exist;
    HashMap<Integer, List<Entity>> tp_entity = new HashMap<>();

    public BukkitTask task = null;

    List<Location> loc = new ArrayList<>(); // 주사위 보관함1~5 좌표
    List<Location> loc2 = new ArrayList<>(); // 던진 주사위 좌표

    final Transformation trans0 = new Transformation(new Vector3f(0.03F, 0, -1.88F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.94F, 0.01F, 2.85F), new AxisAngle4f(0, 0, 0, 0));
    final Transformation trans1 = new Transformation(new Vector3f(0.03F, 0, -0.97F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(0.94F, 0.01F, 1.94F), new AxisAngle4f(0, 0, 0, 0));
    final Transformation trans2 = new Transformation(new Vector3f(0.03F, 0, -1.88F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(1.94F, 0.01F, 2.85F), new AxisAngle4f(0, 0, 0, 0));
    final Transformation trans3 = new Transformation(new Vector3f(0.03F, 0, -0.97F), new AxisAngle4f(0, 0, 0, 0), new Vector3f(1.94F, 0.01F, 1.94F), new AxisAngle4f(0, 0, 0, 0));

    final Transformation text_trans = new Transformation(new Vector3f(-0.05F, 0.02F, 0.9F), new AxisAngle4f((float) Math.toRadians(-90), 1 ,0, 0), new Vector3f(4F, 3, 4.8F), new AxisAngle4f());

    final Transformation text_trans_2 = new Transformation(new Vector3f(0.45F, 0.02F, 0.95F), new AxisAngle4f((float) Math.toRadians(-90), 1 ,0, 0), new Vector3f(2F, 3.2F, 1F), new AxisAngle4f());
    final Transformation text_trans_3 = new Transformation(new Vector3f(0F, 0.02F, 0.8F), new AxisAngle4f((float) Math.toRadians(-90), 1 ,0, 0), new Vector3f(0.9F, 2F, 1F), new AxisAngle4f());
    final Transformation text_trans_4 = new Transformation(new Vector3f(0F, 0.02F, 0.9F), new AxisAngle4f((float) Math.toRadians(-90), 1 ,0, 0), new Vector3f(1.5F, 2.5F, 1F), new AxisAngle4f());

    // 스코어보드 관련 변수
    List<BlockDisplay> sb_pane_p1 = new ArrayList<>();
    List<BlockDisplay> sb_pane_p2 = new ArrayList<>();

    TextDisplay[] sb_txt = new TextDisplay[12];
    List<TextDisplay> txt_1 = new ArrayList<>();
    List<TextDisplay> txt_2 = new ArrayList<>();

    HashMap<Integer, List<Interaction>> sb_inct_p1 = new HashMap<>();
    HashMap<Integer, List<Interaction>> sb_inct_p2 = new HashMap<>();

    int[] bool_p1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    int[] bool_p2 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    int[] calculate = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    int subscore_p1 = 0;
    int subscore_p2 = 0;

    int score_p1 = 0;
    int score_p2 = 0;

    boolean bonused_p1 = false;
    boolean bonused_p2 = false;

    Color c_yellow = Color.fromRGB(237, 173, 23);
    Color c_white = Color.fromRGB(204, 209, 212);
    Color c_gray = Color.fromRGB(54, 57, 62);
    Color c_lightgray = Color.fromRGB(124, 124, 114);

    TextDisplay p1_subscore_txt;
    TextDisplay p2_subscore_txt;
    TextDisplay p1_addscore_txt;
    TextDisplay p2_addscore_txt;
    TextDisplay p1_score_txt;
    TextDisplay p2_score_txt;

    public YachtGame(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
        w = p1.getWorld();


        // 주사위 굴리기 아이템 지급
        ItemStack item = new ItemStack(Material.WHITE_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("주사위 굴리기").color(NamedTextColor.YELLOW));
        meta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        p1.getInventory().clear();
        p1.getInventory().setItem(0, item);
        p2.getInventory().clear();
        p2.getInventory().setItem(0, item);

        loc.add(new Location(w, -3621.5, 70, 3747.5));
        loc.add(new Location(w, -3621.5, 70, 3744.5));
        loc.add(new Location(w, -3621.5, 70, 3741.5));
        loc.add(new Location(w, -3621.5, 70, 3738.5));
        loc.add(new Location(w, -3621.5, 70, 3735.5));

        loc2.add(new Location(w, -3616.5, 67.5, 3747.5));
        loc2.add(new Location(w, -3616.5, 67.5, 3744.5));
        loc2.add(new Location(w, -3616.5, 67.5, 3741.5));
        loc2.add(new Location(w, -3616.5, 67.5, 3738.5));
        loc2.add(new Location(w, -3616.5, 67.5, 3735.5));

        tp_entity.put(0, new ArrayList<>());
        tp_entity.put(1, new ArrayList<>());
        tp_entity.put(2, new ArrayList<>());
        tp_entity.put(3, new ArrayList<>());
        tp_entity.put(4, new ArrayList<>());

        // 스코어보드 판 생성 - line 1
        make_scorepane(new Location(w, -3625, 68, 3761), Material.GRAY_CONCRETE, trans0);
        make_scorepane(new Location(w, -3624, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3623, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3622, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3621, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3620, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3619, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3618, 68, 3761), Material.GRAY_CONCRETE, trans0);
        make_scorepane(new Location(w, -3617, 68, 3761), Material.GRAY_CONCRETE, trans0);

        make_scorepane(new Location(w, -3615, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3613, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3612, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3611, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3610, 68, 3761), Material.WHITE_CONCRETE, trans0);
        make_scorepane(new Location(w, -3609, 68, 3761), Material.WHITE_CONCRETE, trans0);

        make_scorepane(new Location(w, -3607, 68, 3761), Material.GRAY_CONCRETE, trans2);

        // 스코어보드 판 생성 - line 2
        make_scorepane(new Location(w, -3625, 68, 3758), Material.WHITE_CONCRETE, trans1);
        sb_pane_p1.add(make_scorepane(new Location(w, -3624, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3623, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3622, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3621, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3620, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3619, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        make_scorepane(new Location(w, -3618, 68, 3758), Material.LIGHT_GRAY_CONCRETE, trans1); 
        make_scorepane(new Location(w, -3617, 68, 3758), Material.LIGHT_GRAY_CONCRETE, trans1);

        sb_pane_p1.add(make_scorepane(new Location(w, -3615, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3613, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3612, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3611, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3610, 68, 3758), Material.YELLOW_CONCRETE, trans1));
        sb_pane_p1.add(make_scorepane(new Location(w, -3609, 68, 3758), Material.YELLOW_CONCRETE, trans1));

        sb_pane_p1.add( make_scorepane(new Location(w, -3607, 68, 3758), Material.YELLOW_CONCRETE, trans3));


        // 스코어보드 판 생성 - line 3
        make_scorepane(new Location(w, -3625, 68, 3756), Material.WHITE_CONCRETE, trans1);
        sb_pane_p2.add(make_scorepane(new Location(w, -3624, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3623, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3622, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3621, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3620, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3619, 68, 3756), Material.WHITE_CONCRETE, trans1));
        make_scorepane(new Location(w, -3618, 68, 3756), Material.LIGHT_GRAY_CONCRETE, trans1);
        make_scorepane(new Location(w, -3617, 68, 3756), Material.LIGHT_GRAY_CONCRETE, trans1);

        sb_pane_p2.add(make_scorepane(new Location(w, -3615, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3613, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3612, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3611, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3610, 68, 3756), Material.WHITE_CONCRETE, trans1));
        sb_pane_p2.add(make_scorepane(new Location(w, -3609, 68, 3756), Material.WHITE_CONCRETE, trans1));

        sb_pane_p2.add(make_scorepane(new Location(w, -3607, 68, 3756), Material.WHITE_CONCRETE, trans3));

        // 인터렉션 생성
        List<Interaction> temp = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            temp.clear();
            temp.add(make_scoreinct(sb_pane_p1.get(i).getLocation().add(0.5, 0, 0.5), "p1", String.valueOf(i)));
            temp.add(make_scoreinct(sb_pane_p1.get(i).getLocation().add(0.5, 0, -0.5), "p1", String.valueOf(i)));
            sb_inct_p1.put(i, temp);

            temp.clear();
            temp.add(make_scoreinct(sb_pane_p2.get(i).getLocation().add(0.5, 0, 0.5), "p2",String.valueOf(i)));
            temp.add(make_scoreinct(sb_pane_p2.get(i).getLocation().add(0.5, 0, -0.5), "p2",String.valueOf(i)));
            sb_inct_p2.put(i, temp);
        }

        // 텍스트 디스플레이 생성
        make_text(new Location(w, -3625, 68, 3761), "Categories", c_gray, NamedTextColor.WHITE, text_trans_2);
        make_text(new Location(w, -3624, 68, 3761), "Aces", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3623, 68, 3761), "Deuces", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3622, 68, 3761), "Threes", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3621, 68, 3761), "Fours", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3620, 68, 3761), "Fives", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3619, 68, 3761), "Sixes", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3618, 68, 3761), "Subtotal", c_gray, NamedTextColor.WHITE, text_trans_2);
        make_text(new Location(w, -3617, 68, 3761), "+35 Bonus", c_gray, NamedTextColor.WHITE, text_trans_2);
        make_text(new Location(w, -3615, 68, 3761), "Choice", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3613, 68, 3761), "4 of a Kind", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3612, 68, 3761), "Full House", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3611, 68, 3761), "S. Straight", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3610, 68, 3761), "L. Straight", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3609, 68, 3761), "Yacht", c_white, NamedTextColor.BLACK, text_trans_2);
        make_text(new Location(w, -3606.5, 68, 3761), "Total", c_gray, NamedTextColor.WHITE, text_trans_2);

        make_text(new Location(w, -3625, 68, 3758), p1.getName(), c_white, NamedTextColor.BLACK, text_trans_3);
        make_text(new Location(w, -3625, 68, 3756), p2.getName(), c_white, NamedTextColor.BLACK, text_trans_3);

        p1_subscore_txt = make_text(new Location(w, -3618, 68, 3758), "0 / 63", c_lightgray, NamedTextColor.WHITE, text_trans_4);
        p2_subscore_txt = make_text(new Location(w, -3618, 68, 3756), "0 / 63", c_lightgray, NamedTextColor.WHITE, text_trans_4);

        p1_addscore_txt = make_text(new Location(w, -3617, 68, 3758), "0", c_lightgray, NamedTextColor.WHITE, text_trans);
        p2_addscore_txt = make_text(new Location(w, -3617, 68, 3756), "0", c_lightgray, NamedTextColor.WHITE, text_trans);

        p1_score_txt = make_text(new Location(w, -3606.5, 68, 3758), "0", c_yellow, NamedTextColor.BLACK, text_trans);
        p2_score_txt = make_text(new Location(w, -3606.5, 68, 3756), "0", c_white, NamedTextColor.BLACK, text_trans);

    }

    private BlockDisplay make_scorepane(Location loc, Material material, Transformation trans) {
        BlockDisplay d = (BlockDisplay) w.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
        d.setBlock(material.createBlockData());
        d.setTransformation(trans);
        d.addScoreboardTag("ya");
        return d;
    }

    private Interaction make_scoreinct(Location loc, String tag, String tag2) {
        Interaction d = (Interaction) w.spawnEntity(loc, EntityType.INTERACTION);
        d.setInteractionHeight(0.01F);
        d.addScoreboardTag("ya");
        d.addScoreboardTag("ya.scoreboard");
        d.addScoreboardTag("ya.scoreboard."+tag);
        d.addScoreboardTag("ya.scoreboard."+tag2);
        return d;
    }

    private void make_scoretext(Location loc, String text, Color c, int i) {
        TextDisplay d = (TextDisplay) w.spawnEntity(loc, EntityType.TEXT_DISPLAY);
        Location l = d.getLocation();
        l.setYaw(-90);
        d.teleport(l);
        d.setTransformation(text_trans);
        d.setBackgroundColor(c);
        d.text(Component.text(text).color(NamedTextColor.GRAY));
        d.addScoreboardTag("ya");
        sb_txt[i] = d;
     }

    private TextDisplay make_text(Location loc, String text, Color c, NamedTextColor c2, Transformation trans) {
        TextDisplay d = (TextDisplay) w.spawnEntity(loc, EntityType.TEXT_DISPLAY);
        Location l = d.getLocation();
        l.setYaw(-90);
        d.teleport(l);
        d.setTransformation(trans);
        d.setBackgroundColor(c);
        d.text(Component.text(text).color(c2));
        d.addScoreboardTag("ya");
        return d;
    }

    private void clearDice() {
        for (int i = 0; i < 5; i++) {
            if (noon.containsKey(i)) {
                for (BlockDisplay n: noon.get(i)) {
                    if (!n.getScoreboardTags().contains("selected")) {
                        n.remove();
                    }
                }
            }
        }
        for (BlockDisplay dice: dice_display) {
            if (!dice.getScoreboardTags().contains("selected")) {
                dice.remove();
            }
        }
        for (Interaction inct: interactionList) {
            inct.remove();
        }
    }

    public void dice(Player p) {
        if  ((cur_player == 0 && p.equals(p1)) || (cur_player == 1 && p.equals(p2))) {
            if ((phase == 0 || (phase == 2 && roll_dice_count != 3)) && dice_count != 0) {
                clearDice();
                for (TextDisplay t: sb_txt) {
                    if (t != null) {
                        t.remove();
                    }
                }
                task = new YachtTask(this).runTaskTimer(YachtPlugin.plugin, 0L, 1L);
                phase = 1;
                roll_dice_count ++;

                if (dice_count == 1) {
                    dice_exist = new int[]{0, 0, 1, 0, 0};
                } else if (dice_count == 2) {
                    dice_exist = new int[]{0, 1, 0, 1, 0};
                } else if (dice_count == 3) {
                    dice_exist = new int[]{1, 0, 1, 0, 1};
                } else if (dice_count == 4) {
                    dice_exist = new int[]{1, 1, 0, 1, 1};
                } else if (dice_count == 5) {
                    dice_exist = new int[]{1, 1, 1, 1, 1};
                }

            }
        }
    }

    public void select(Player p, Entity e) {
        if ( phase == 2 && ((cur_player == 0 && p.equals(p1) || (cur_player == 1 && p.equals(p2))))) {

            List<BlockDisplay> tp_entities = new ArrayList<>();

            int n = 0;
            for (Entity near_e: e.getNearbyEntities(1, 1, 1)) {
                if (near_e.getScoreboardTags().contains("ya.noon")) {
                    n ++;
                    tp_entities.add((BlockDisplay) near_e);
                } else if (near_e.getScoreboardTags().contains("ya.dice")) {
                    tp_entities.add((BlockDisplay) near_e);
                }
            }

            // 저장된 주사위인지 던진 주사위인지 판단
            if (e.getScoreboardTags().contains("ya.inct.return")) {

                int dice_pos = -1;
                for (int i = 0; i<5; i++) {
                    if (e.getScoreboardTags().contains("ya.return."+i)) {
                        dice_pos = i;
                    }
                }

                tp_entity.get(dice_pos).clear();
                selected_dice[dice_pos] = 0;

                int pos = -1;
                if (dice_exist[0] == 0) {
                    pos = 0;
                } else if (dice_exist[1] == 0) {
                    pos = 1;
                } else if (dice_exist[2] == 0) {
                    pos = 2;
                } else if (dice_exist[3] == 0) {
                    pos = 3;
                } else if (dice_exist[4] == 0) {
                    pos = 4;
                }

                dice_exist[pos] = 1;

                Location inct_loc = loc2.get(pos).clone();
                Interaction inct = (Interaction) w.spawnEntity(inct_loc.add(0.5, 0, 0.5), EntityType.INTERACTION);
                inct.addScoreboardTag("ya");
                inct.addScoreboardTag("ya.inct");
                inct.addScoreboardTag("ya.inct."+pos);
                interactionList.add(inct);

                for (BlockDisplay tp_e: tp_entities) {
                    tp_e.teleport(loc2.get(pos));
                    tp_e.removeScoreboardTag("selected");
                    if (tp_e.getScoreboardTags().contains("ya.noon")) {
                        noon.get(pos).add(tp_e);
                    } else if (tp_e.getScoreboardTags().contains("ya.dice")) {
                        dice_display.add(tp_e);
                    }
                }

                dice_count ++;

            } else {

                int dice_pos = -1;
                for (int i = 0; i<5; i++) {
                    if (e.getScoreboardTags().contains("ya.inct."+i)) {
                        dice_pos = i;
                    }
                }
                dice_exist[dice_pos] = 0;

                int pos = -1;
                if (selected_dice[0] == 0) {
                    pos = 0;
                } else if (selected_dice[1] == 0) {
                    pos = 1;
                } else if (selected_dice[2] == 0) {
                    pos = 2;
                } else if (selected_dice[3] == 0) {
                    pos = 3;
                } else if (selected_dice[4] == 0) {
                    pos = 4;
                }

                Location inct_loc = loc.get(pos).clone();
                Interaction inct = (Interaction) w.spawnEntity(inct_loc.add(0.5, 0, 0.5), EntityType.INTERACTION);
                inct.addScoreboardTag("ya");
                inct.addScoreboardTag("ya.inct");
                inct.addScoreboardTag("ya.inct.return");
                inct.addScoreboardTag("ya.return."+pos);
                tp_entity.get(pos).add(inct);
                for (Entity tp_e: tp_entities) {
                    tp_entity.get(pos).add(tp_e);
                    tp_e.teleport(loc.get(pos));
                    tp_e.addScoreboardTag("selected");
                }

                selected_dice[pos] = n;
                dice_count --;

            }

            e.remove();

            p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.2F, 0.5F);
            p2.playSound(p2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.2F, 0.5F);

        }
    }

    public void scoreboard_calc() {
        calculate = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        Integer[] dice_count_array = new Integer[]{0, 0, 0, 0, 0, 0};
        int sum = 0;
        for (int i: selected_dice) {
            if (i != 0) {
                dice_count_array[i-1] ++;
                sum += i;
            }
        }
        for (int i: n_selected_dice) {
            dice_count_array[i-1] ++;
            sum += i;
        }
        List<Integer> dice_count = Arrays.asList(dice_count_array);

        // Aces ~ Sixes
        for (int i = 0; i < 6; i++) {
            calculate[i] = dice_count_array[i] * (i+1);
        }

        // Choice
        calculate[6] = sum;

        // 4 of a Kind
        if (dice_count.contains(4) || dice_count.contains(5)) {
            calculate[7] = sum;
        }

        // Full House
        if ((dice_count.contains(3) && dice_count.contains(2)) || dice_count.contains(5)) {
            calculate[8] = sum;
        }

        // Small Straight
        if (dice_count_array[0] != 0 && dice_count_array[1] != 0 && dice_count_array[2] != 0 && dice_count_array[3] != 0) {
            calculate[9] = 15;
        }
        if (dice_count_array[1] != 0 && dice_count_array[2] != 0 && dice_count_array[3] != 0 && dice_count_array[4] != 0) {
            calculate[9] = 15;
        }
        if (dice_count_array[2] != 0 && dice_count_array[3] != 0 && dice_count_array[4] != 0 && dice_count_array[5] != 0) {
            calculate[9] = 15;
        }

        // Large Straight
        if (dice_count_array[0] == 1 && dice_count_array[1] == 1 && dice_count_array[2] == 1 && dice_count_array[3] == 1 && dice_count_array[4] == 1) {
            calculate[10] = 30;
        }
        if (dice_count_array[1] == 1 && dice_count_array[2] == 1 && dice_count_array[3] == 1 && dice_count_array[4] == 1 && dice_count_array[5] == 1) {
            calculate[10] = 30;
        }

        // Yacht
        if (dice_count.contains(5)) {
            calculate[11] = 50;
        }

        if (cur_player == 0) {
            for (int i = 0; i < 12; i++) {
                if (bool_p1[i] == 0) {
                    make_scoretext(sb_pane_p1.get(i).getLocation(), String.valueOf(calculate[i]), c_yellow, i);
                } else {
                    sb_txt[i] = null;
                }
            }
        } else {
            for (int i = 0; i < 12; i++) {
                if (bool_p2[i] == 0) {
                    make_scoretext(sb_pane_p2.get(i).getLocation(), String.valueOf(calculate[i]), c_yellow, i);
                } else {
                    sb_txt[i] = null;
                }
            }
        }
    }

    public void scoreboard(Player p, Entity e) {
        if ( phase == 2 && ((cur_player == 0 && p.equals(p1)) || (cur_player == 1 && p.equals(p2))) ) {

            if (cur_player == 0 && e.getScoreboardTags().contains("ya.scoreboard.p1")) {

                int i = -1;
                for (int j = 0; j < 12; j++) {
                    if (e.getScoreboardTags().contains("ya.scoreboard."+j)) {
                        i = j;
                        break;
                    }
                }

                if (sb_txt[i] != null) {
                    sb_txt[i].text(sb_txt[i].text().color(NamedTextColor.BLACK));
                    txt_1.add(sb_txt[i]);
                    sb_txt[i] = null;

                    for (TextDisplay t: sb_txt) {
                        if (t != null) {
                            t.remove();
                        }
                    }
                    for (TextDisplay t: txt_1) {
                        t.setBackgroundColor(c_white);
                    }
                    for (TextDisplay t: txt_2) {
                        t.setBackgroundColor(c_yellow);
                    }

                    p1_score_txt.setBackgroundColor(c_white);
                    p2_score_txt.setBackgroundColor(c_yellow);

                    bool_p1[i] = 1;

                    score_p1 += calculate[i];

                    if (i < 6) {
                        subscore_p1 += calculate[i];
                    }
                    p1_subscore_txt.text(Component.text(subscore_p1 + " / 63").color(NamedTextColor.WHITE));
                    if (subscore_p1 >= 63 && !bonused_p1) {
                        bonused_p1 = true;
                        score_p1 += 35;
                        p1_addscore_txt.text(Component.text("35").color(NamedTextColor.WHITE));
                    }

                    p1_score_txt.text(Component.text(String.valueOf(score_p1)).color(NamedTextColor.BLACK));
                    turn_change();

                }

            } else if (cur_player == 1 && e.getScoreboardTags().contains("ya.scoreboard.p2")) {

                int i = -1;
                for (int j = 0; j < 12; j++) {
                    if (e.getScoreboardTags().contains("ya.scoreboard."+j)) {
                        i = j;
                        break;
                    }
                }

                if (sb_txt[i] != null) {
                    sb_txt[i].text(sb_txt[i].text().color(NamedTextColor.BLACK));
                    txt_2.add(sb_txt[i]);
                    sb_txt[i] = null;

                    for (TextDisplay t: sb_txt) {
                        if (t != null) {
                            t.remove();
                        }
                    }
                    for (TextDisplay t: txt_2) {
                        t.setBackgroundColor(c_white);
                    }
                    for (TextDisplay t: txt_1) {
                        t.setBackgroundColor(c_yellow);
                    }

                    p2_score_txt.setBackgroundColor(c_white);
                    p1_score_txt.setBackgroundColor(c_yellow);

                    bool_p2[i] = 1;

                    score_p2 += calculate[i];

                    if (i < 6) {
                        subscore_p2 += calculate[i];
                    }
                    p2_subscore_txt.text(Component.text(subscore_p2 + " / 63").color(NamedTextColor.WHITE));
                    if (subscore_p2 >= 63 && !bonused_p2) {
                        bonused_p2 = true;
                        score_p2 += 35;
                        p2_addscore_txt.text(Component.text("35").color(NamedTextColor.WHITE));
                    }

                    p2_score_txt.text(Component.text(String.valueOf(score_p2)).color(NamedTextColor.BLACK));
                    turn_change();

                }

            }

        }
    }

    private void turn_change() {

        for (int i = 0; i < 5; i++) {
            for (Entity e: tp_entity.get(i)) {
                e.remove();
            }
            tp_entity.put(i, new ArrayList<>());
        }
        clearDice();

        phase = 0;
        roll_dice_count = 0;
        dice_count = 5;
        selected_dice = new int[]{0, 0, 0, 0, 0};
        sb_txt = new TextDisplay[12];

        if (cur_player == 0) {
            cur_player = 1;
            for (BlockDisplay bd: sb_pane_p1) {
                bd.setBlock(Material.WHITE_CONCRETE.createBlockData());
            }
            for (BlockDisplay bd: sb_pane_p2) {
                bd.setBlock(Material.YELLOW_CONCRETE.createBlockData());
            }
        } else {
            cur_player = 0;
            round ++;
            if (round == 13) {
                end();
            } else {
                for (BlockDisplay bd: sb_pane_p2) {
                    bd.setBlock(Material.WHITE_CONCRETE.createBlockData());
                }
                for (BlockDisplay bd: sb_pane_p1) {
                    bd.setBlock(Material.YELLOW_CONCRETE.createBlockData());
                }
            }

        }

    }

    private void end() {
        phase = 9;
        if (score_p1 > score_p2) {
            p1.sendMessage(Component.text(p1.getName() + " 승!").color(NamedTextColor.GREEN));
            p2.sendMessage(Component.text(p1.getName() + " 승!").color(NamedTextColor.GREEN));
            for (TextDisplay td: txt_1) {
                td.setBackgroundColor(c_yellow);
            }
            for (TextDisplay td: txt_2) {
                td.setBackgroundColor(c_white);
            }
            p2_score_txt.setBackgroundColor(c_white);
            p1_score_txt.setBackgroundColor(c_yellow);
            for (BlockDisplay bd: sb_pane_p1) {
                bd.setBlock(Material.YELLOW_CONCRETE.createBlockData());
            }
            for (BlockDisplay bd: sb_pane_p2) {
                bd.setBlock(Material.WHITE_CONCRETE.createBlockData());
            }
        } else if (score_p1 < score_p2) {
            p1.sendMessage(Component.text(p2.getName() + " 승!").color(NamedTextColor.GREEN));
            p2.sendMessage(Component.text(p2.getName() + " 승!").color(NamedTextColor.GREEN));
            for (TextDisplay td: txt_2) {
                td.setBackgroundColor(c_yellow);
            }
            for (TextDisplay td: txt_1) {
                td.setBackgroundColor(c_white);
            }
            p1_score_txt.setBackgroundColor(c_white);
            p2_score_txt.setBackgroundColor(c_yellow);
            for (BlockDisplay bd: sb_pane_p1) {
                bd.setBlock(Material.WHITE_CONCRETE.createBlockData());
            }
            for (BlockDisplay bd: sb_pane_p2) {
                bd.setBlock(Material.YELLOW_CONCRETE.createBlockData());
            }
        } else {
            p1.sendMessage(Component.text("비김").color(NamedTextColor.AQUA));
            p2.sendMessage(Component.text("비김").color(NamedTextColor.AQUA));
            for (TextDisplay td: txt_1) {
                td.setBackgroundColor(c_yellow);
            }
            for (TextDisplay td: txt_2) {
                td.setBackgroundColor(c_yellow);
            }
            p1_score_txt.setBackgroundColor(c_yellow);
            p2_score_txt.setBackgroundColor(c_yellow);
            for (TextDisplay td: txt_1) {
                td.setBackgroundColor(c_yellow);
            }
            for (TextDisplay td: txt_2) {
                td.setBackgroundColor(c_yellow);
            }
        }

    }

    public void stop() {

        if (task != null) {
            task.cancel();
            task = null;
        }

        for(Entity e: w.getEntities()) {
            if (e.getScoreboardTags().contains("ya")) {
                e.remove();
            }
        }

        YachtPlugin.game = null;

    }

}
