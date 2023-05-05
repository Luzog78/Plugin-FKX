package fr.luzog.pl.fkx.game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.commands.Chat.Msg;
import fr.luzog.pl.fkx.commands.Other.Ad;
import fr.luzog.pl.fkx.utils.Broadcast;
import fr.luzog.pl.fkx.utils.Color;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static fr.luzog.pl.fkx.commands.Other.Ad.State.WAITING;

public class GListener {

    public static final String y = "§2" + SpecialChars.YES;
    public static final String n = "§4" + SpecialChars.NO;
    public static final String[] a = new String[]{"⬆", "⬈", "➡", "⬊", "⬇", "⬋", "⬅", "⬉", "⬌", "⬍", "§d۞§r"};
    public static final String deactivated = "§c§oDesactivé";
    public static final String no_team = "§4§lAucune équipe";
    public static long autoSaveInventoryTimeout = 600;

    public static int mainTaskID;

    public static void scheduleMainTask() {
        mainTaskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.instance, new BukkitRunnable() {

            @Override
            public void run() {
                new ArrayList<Player>(Bukkit.getOnlinePlayers()) {{
                    if (GManager.getCurrentGame() != null)
                        removeIf(p -> GManager.getCurrentGame().getPlayer(p.getName(), false) != null);
                }}.forEach(p -> {
                    p.setPlayerListName("§8§l[§2" + SpecialChars.LYS + "§8§l]§8 » " + p.getName());
                    p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(getDefaultTHF(p));
                });
            }

        }, 0, 5); // Each 1/4 sec
    }

    public static void cancelMainTask() {
        try {
            Bukkit.getScheduler().cancelTask(mainTaskID);
        } catch (Exception e) {
            Broadcast.err("!Error : Cannot Cancelling Main Task. (" + e + ")");
        }
    }

    private GManager manager;

    private int taskID;
    private long savingTimeOut;
    private long savingCoolDown;

    private Objective objective;
    private Map<String, Integer> l; // ScoreBoard List
    private Map<Integer, String> al; // Ancian ScoreBoard List -> to up to date
    private String scoreName = "Game" /* = "§6§l§n-=[ §1F§aa§3l§cl§5e§en §7K§6i§dn§4g§bd§2o§9m §8I §6]=-" */;

    public GListener() {
        this.savingTimeOut = 60 * 3; // 5 min in sec
        savingCoolDown = savingTimeOut;
        l = new HashMap<>();
        al = new HashMap<>();
    }

    public GListener(long savingTimeOutInSeconds) {
        this.savingTimeOut = savingTimeOutInSeconds;
        savingCoolDown = savingTimeOut;
        l = new HashMap<>();
        al = new HashMap<>();
    }

    public void scheduleTask() {
        objective = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("go"
                + UUID.randomUUID().toString().substring(0, 8), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.instance, new BukkitRunnable() {

            final long delayer = 4L; // Each at 1/4 sec so : original = time / (1/4) == time * 4
            long countDown = savingTimeOut * delayer;

            long autoSaveInventoryCountDown = autoSaveInventoryTimeout;

            @Override
            public void run() {
                if (countDown - 1 == 0) {
                    manager.getPlayers().forEach(GPlayer::saveStats);
                    // TODO -> Save.save();
                    countDown = savingTimeOut * delayer;
                } else
                    countDown--;

                if (autoSaveInventoryCountDown - 5 <= 0) {
                    manager.getPlayers().forEach(GPlayer::saveAutoSaveInventory);
                    autoSaveInventoryCountDown = autoSaveInventoryTimeout;
                } else
                    autoSaveInventoryCountDown -= 5;

                refreshScoreName();
                objective.setDisplayName(scoreName);

                if (manager.getState() == GManager.State.RUNNING) {
                    manager.increaseTime(5, false);

                    if (manager.getTime() >= 24000) {
                        manager.increaseDay(true);
                        manager.setTime(0, false);
                        Broadcast.succ("§e§lNouvelle journée !!§r Passage au jour !" + manager.getDay() + " !");
                        manager.checkActivations(false);
                        manager.saveManager(false);
                        for (GPickableLocks.Lock lock : manager.getPickableLocks().getPickableLocks())
                            if (lock.getLevel() == manager.getDay())
                                lock.broadcast();
                    } else if (manager.getTime() >= 24000 - 100 && manager.getTime() % 20 == 0)
                        Broadcast.log("Nouvelle journée dans !" + ((24000 - manager.getTime()) / 20) + " !secondes§r...");
                    else if (manager.getTime() == 24000 - 200)
                        Broadcast.log("Nouvelle journée dans !10 !secondes§r...");
                    else if (manager.getTime() == 24000 - 400)
                        Broadcast.log("Nouvelle journée dans !20 !secondes§r...");
                    else if (manager.getTime() == 24000 - 600)
                        Broadcast.log("Nouvelle journée dans !30 !secondes§r...");
                    else if (manager.getTime() == 24000 - 1200)
                        Broadcast.log("Nouvelle journée dans !1 !minute§r...");
                    else if (manager.getTime() == 24000 - 1200 * 2)
                        Broadcast.log("Nouvelle journée dans !2 !minutes§r...");
                    else if (manager.getTime() == 24000 - 1200 * 3)
                        Broadcast.log("Nouvelle journée dans !3 !minutes§r...");
                } else
                    manager.increaseTime(0, false);

                setScoreLines();
                updateScoreLines();

                Msg.replies.forEach((p, r) -> {
                    if (r == null)
                        return;
                    if (r.getValue() == 0)
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Msg.replies.remove(p);
                            }
                        }.runTask(Main.instance);
                    else
                        r.setValue(r.getValue() - 5);
                });

                manager.getPlayers().forEach(gPlayer -> {
                    Player p = gPlayer.getPlayer();
                    if (p == null)
                        return;

                    String displayName = gPlayer.getDisplayName();

                    p.setDisplayName(displayName);

                    if (gPlayer.getTeam() == null)
                        displayName = "§8§l[§4" + SpecialChars.ATOM + "§8§l]§8 » " + displayName;

                    if (Vanish.vanished.contains(p.getName()))
                        if (Vanish.isPrefix)
                            displayName = Vanish.pre_suf_ix + displayName;
                        else
                            displayName += Vanish.pre_suf_ix;

                    p.setPlayerListName(displayName);

                    if (!p.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard()))
                        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(getTHF(p));

//                  String directionalArrow = "§6" + new DecimalFormat("0.0").format(p.getLocation().distance(new Location(p.getWorld(), -256.5, p.getLocation().getY(), -143.5)))
//                          + "m §e" + getOrientationChar(p.getLocation().getYaw(), p.getLocation().getX(), p.getLocation().getZ(), -256.5, -143.5);
                    long waitingAds;
                    try {
                        if (gPlayer.getCompass() != null && gPlayer.getCompass().getLocation() != null) {
                            boolean sameWorld = p.getLocation().getWorld() == null
                                    || gPlayer.getCompass().getLocation().getWorld().getUID().equals(p.getLocation().getWorld().getUID());
                            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(
                                    new PacketPlayOutChat(new ChatComponentText(
                                            (gPlayer.getCompass().getName() == null ? "§cnull" : "§6" + gPlayer.getCompass().getName())
                                                    + "  §7-  §6" + Utils.safeDistance(p.getLocation(),
                                                    gPlayer.getCompass().getLocation(), true, 1,
                                                    gPlayer.getCompass().getRadius())
                                                    + "m  " + (sameWorld ? "§e" + getOrientationChar(p.getLocation().getYaw(),
                                                    p.getLocation().getX(), p.getLocation().getZ(),
                                                    gPlayer.getCompass().getLocation().getX(),
                                                    gPlayer.getCompass().getLocation().getZ(),
                                                    gPlayer.getCompass().getRadius()) : "§c۞")
                                    ), (byte) 2));
                        } else if (gPlayer.getTeam() != null && gPlayer.getTeam().getId().equals(GTeam.GODS_ID)
                                && (waitingAds = Ad.ads.stream().filter(a -> a.getState() == WAITING).count()) > 0)
                            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(
                                    new PacketPlayOutChat(new ChatComponentText(
                                            Ad.AD_PREFIX + "§f" + waitingAds + "§a ads en attente"), (byte) 2));
                    } catch (Exception e) {
                    }
                });
            }

        }, 0, 5); // Each 1/4 sec
    }

    public void cancelTask() {
        try {
            l.clear();
            al.clear();
            Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().forEach(o -> {
                if (o.getName().startsWith("go"))
                    o.unregister();
            });
            Bukkit.getScheduler().cancelTask(taskID);
        } catch (Exception e) {
            Broadcast.err("!Error : Cannot Cancelling Auto Task. (" + e + ")");
        }
    }

    public void refreshScoreName() {
        scoreName = Main.SEASON.substring(0, Math.min(Main.SEASON.length(), 20));
        int index = (int) (manager.getTime() / 5 % scoreName.length());
        scoreName = "§6§l" + scoreName.substring(0, index) + "§f§l" + scoreName.charAt(index) + "§6§l" + scoreName.substring(index + 1);
    }

    private String ca(List<String> l) { // ca for charAt
        Random r = new Random();
        int i = r.nextInt(l.size());
        String s = l.get(i) + "";
        l.remove(i);
        return s;
    }

    public void setScoreLines() {
        l.clear();
        l.put("§r", /* ...................................................................................................... */ 12);
        l.put("§8Jour : §3" + manager.getDay(), /* .......................................................................... */ 11);
        l.put("§8Heure : §3" + manager.getFormattedTime(), /* ............................................................... */ 10);
        l.put("§c----------", /* ............................................................................................. */ 9);
        for (int i = 0; i < 4; i++) {
            GOptions.GOption o = new GOptions.GOption[]{manager.getOptions().getPvp(), manager.getOptions().getNether(),
                    manager.getOptions().getAssaults(), manager.getOptions().getEnd()}[i];
            l.put("§a" + o.getName() + "§a : " + (o.isActivated() ? y : n + "§7§O (J" + o.getActivationDay() + ")"), /* . */ -i + 8);
        }
        l.put("§c---------- ", /* ............................................................................................ */ 4);
        l.put("§9Niveau : §f" + GPickableLocks.getPickingLevel(manager), /* ................................................. */ 3);
        l.put("§9Coffres : §c" + manager.getPickableLocks().getPickableLocks().stream().filter(l ->
                l.isPickable() && !l.isPicked() && l.getLevel() <= GPickableLocks.getPickingLevel(manager)).count(), /* ..... */ 2);
        l.put("§c----------  ", /* ........................................................................................... */ 1);
        l.put("§d        " + SpecialChars.MISC_3, /* ......................................................................... */ 0);
    }

    public void updateScoreLines() {
        // TODO, CAN THROWS ConcurrentModificationException !!! (at "l.keySet().forEach(s -> {" line)
        l.keySet().forEach(s -> {
            if (!al.containsValue(s) || (al.containsKey(l.get(s)) && !al.get(l.get(s)).equals(s))) {
                if (al.containsKey(l.get(s)))
                    objective.getScoreboard().resetScores(al.get(l.get(s)));
                objective.getScore(s).setScore(l.get(s));
            }
        });
        al.clear();
        l.keySet().forEach(s -> al.put(l.get(s), s));
    }

    public PacketPlayOutPlayerListHeaderFooter getTHF(Player p) {
        GPlayer fp = manager.getPlayer(p.getName(), false);
        List<String> h = new ArrayList<>(), f = new ArrayList<>();
        h.add("§c======= §9§l-=[ §6§l" + Main.SEASON + " §9§l]=- §c=======");
        h.add(" ");
        if (Main.ORGA != null && !Main.ORGA.isEmpty())
            h.add("§9Organisateurs : §f" + String.join("§9, §f", Main.ORGA));
        h.add("§9Developpeur : §f" + "Luzog78");
        h.add(" ");
        h.add("§3Bienvenue à toi cher §9" + (fp == null ? p.getDisplayName() : fp.getDisplayName()) + "§3,");
        h.add("§3l'équipe souhaite une bonne aventure !");
        h.add("§3N'oublie pas §5§l§o§n[§2§l§o§n/ad§5§l§o§n]§3 si tu as un besoin...");
        h.add("§3Les " + manager.getGods().getColor() + manager.getGods().getName() + "§3 seront là pour aider ^^");
        h.add(" ");
        h.add(" ");
        h.add("§7Joueurs en ligne :");
        h.add("§7---");
        f.add("§7---");
        f.add(" ");
//        Arrays.asList(manager.getOptions().getPvp(), manager.getOptions().getNether(),
//                manager.getOptions().getAssauts(), manager.getOptions().getEnd()).forEach(o -> {
//            f.add("§6- §a" + o.getName() + " : " + (o.isActivated() ? y + " "
//                    : n + " §7§o(Jour " + (o.getActivationDay() == -1 ? deactivated
//                    : o.getActivationDay() + ")")) + "    ");
//        });
        String formatted;
        try {
            f.add(fp == null || fp.getTeam() == null ? no_team
                    : fp.getTeam().getName() + "§7 - §6" + (formatted = Utils.safeDistance(p.getLocation(),
                    manager.getPlayer(p.getName(), false).getTeam().getSpawn(), false, 1,
                    GTeam.TEAM_RADIUS)) + "§e " + (formatted.toLowerCase().contains("x") ?
                    getOrientationChar(0, 0, 0, 0, 0, 0)
                    : getOrientationChar(p.getLocation().getYaw(), p.getLocation().getX(), p.getLocation().getZ(),
                    manager.getPlayer(p.getName(), false).getTeam().getSpawn().getX(), manager.getPlayer(p.getName(),
                            false).getTeam().getSpawn().getZ(), GTeam.TEAM_RADIUS)));
        } catch (Exception e) {
            f.add("§cErreur...");
            System.out.print(Color.RED);
            e.printStackTrace();
            System.out.println(Color.RESET);
        }
        f.add(" ");
//        Note : 100% Working Code
//        f.add("§7§nÉquipes :§r");
//        f.add(" ");
//        ArrayList<String> a1 = new ArrayList<>(), a2 = new ArrayList<>();
//        manager.getParticipantsTeams().stream().filter(t -> !t.isEliminated()).forEach(t -> {
//            String s = "";
//            if (manager.getPlayer(p.getName(), false) == null || manager.getPlayer(p.getName(), false).getTeam() == null
//                    || !manager.getPlayer(p.getName(), false).getTeam().equals(t))
//                s = t.getName() + "§7 » §6" + df.format(t.getSpawn().getX())
//                        + " " + df.format(t.getSpawn().getZ()); // p.getLocation().distance(t.getSpawn())
//            if (!s.equals(""))
//                if (!t.isEliminated()) { // Always false but.. it's wrote so.. I let it here.
//                    if (!a1.isEmpty() && a1.size() % 3 == 0)
//                        s = "\n" + s;
//                    a1.add(s);
//                } else {
//                    if (!a2.isEmpty() && a2.size() % 3 == 0)
//                        s = "\n" + s;
//                    a2.add(s);
//                }
//        });
//        for (String s : String.join("\uffff", a1).replace("\uffff\n", "\n").split("\n"))
//            f.add(s.replace("\uffff", "§r  §b||  §r"));
//        if (a2.isEmpty())
//            f.add(" ");
//        for (String s : String.join("\uffff", a2).replace("\uffff\n", "\n").split("\n"))
//            f.add(s.replace("\uffff", "§r  §b||  §r"));

//        Old Code :/ But works pretty well I think
//        manager.getParticipantsTeams().forEach(t -> {
//            if (manager.getPlayer(p.getName(), false) == null || manager.getPlayer(p.getName(), false).getTeam() == null
//                    || !manager.getPlayer(p.getName(), false).getTeam().equals(t))
//                f.add(t.getName() + "§7 - §6" + (!p.getWorld().getUID().equals(t.getSpawn().getWorld().getUID()) ?
//                        "xxx,x §e" + getOrientationChar(0, 0, 0, 0, 0)
//                        : df.format(p.getLocation().distance(t.getSpawn())) + "§e " + getOrientationChar(p.getLocation().getYaw(), p.getLocation().getX(),
//                        p.getLocation().getZ(), t.getSpawn().getX(), t.getSpawn().getZ())));
//        });
        f.add(" ");
//        f.add("§6Save in " + (getSavingTime() < 60 ? "§c" + getSavingTime() + "§6s"
//                : "§c" + ((int) (getSavingTime() / 60)) + "§6min and §c" + (getSavingTime() % 60) + "§6s"));
        if (fp != null) {
            double ratio;
            String color;
            if (fp.getStats().getKills() == 0 && fp.getStats().getDeaths() == 0) {
                ratio = 1;
                color = "§7⁼";
            } else if (fp.getStats().getKills() == 0) {
                ratio = Double.POSITIVE_INFINITY; // To avoid the "-" char
                color = "§4⁻";
            } else if (fp.getStats().getDeaths() == 0) {
                ratio = Double.POSITIVE_INFINITY;
                color = "§2⁺";
            } else {
                ratio = fp.getStats().getKills() / (fp.getStats().getDeaths() == 0 ? 0.5 : fp.getStats().getDeaths());
                if (ratio < 1) {
                    ratio = (1 / ratio);
                    color = "§4⁻";
                } else if (ratio > 1) {
                    color = "§2⁺";
                } else {
                    color = "§7⁼";
                }
            }
            f.add("§8Kills : §b" + fp.getStats().getKills() + "§7/§c" + fp.getStats().getDeaths()
                    + " §7- §8Ratio : " + color + (ratio == Double.POSITIVE_INFINITY ? "∞"
                    : new DecimalFormat("0.00").format(ratio)));
        }
        f.add("§8En ligne : §b" + Bukkit.getOnlinePlayers().size() + "§7/" + Bukkit.getMaxPlayers() + "   §8Ip :§a " + Main.IP);
        f.add("§c====================================");
        return Utils.getTabHeaderAndFooter(h, f);
    }

    public static PacketPlayOutPlayerListHeaderFooter getDefaultTHF(Player p) {
        List<String> h = new ArrayList<>(), f = new ArrayList<>();
        h.add("§c======= §9§l-=[ §6§l" + Main.SEASON + " §9§l]=- §c=======");
        h.add(" ");
        if (Main.ORGA != null && !Main.ORGA.isEmpty())
            h.add("§9Organisateurs : §f" + String.join("§9, §f", Main.ORGA));
        h.add("§9Developpeur : §f" + "Luzog78");
        h.add(" ");
        h.add("§cBienvenue à toi cher §f" + p.getDisplayName() + "§c,");
        h.add("§cMalheureusement, tu n'es actuellement");
        h.add("§cpas un participant de la partie actuelle");
        h.add("§cde §9§l[ §6§l" + Main.SEASON + " §9§l]§c.");
        h.add("§cDemande à un §4Administrateur de");
        h.add("§ct'ajouter à la partie ou patiente");
        h.add("§cquelques instants...");
        h.add(" ");
        h.add(" ");
        h.add("§7Joueurs en ligne :");
        h.add("§7---");
        f.add("§7---");
        f.add(" ");
        f.add(" ");
        f.add("§8Online : §b" + Bukkit.getOnlinePlayers().size() + "§7/" + Bukkit.getMaxPlayers() + "   §8Ip :§a " + Main.IP);
        f.add("§c====================================");
        return Utils.getTabHeaderAndFooter(h, f);
    }

    /**
     * Formules de <strong style='color: #ff0000'>Luzog78</strong> !<br>
     * Très (trop) fier de lui !<br>
     * Car il les a trouvé <b>seul</b> en <span style='color: #ffffff'>3h15</span>.<br>
     * <br>
     *
     * <br>
     *
     * @param yaw    Yaw orientation in degrees (yaw ∈ [-360 ; 360])
     * @param fromX  Position X of Object A (Player position)
     * @param fromZ  Position Z of Object A (Player position)
     * @param toX    Position X of Object B (Targeted Object position)
     * @param toZ    Position Z of Object B (Targeted Object position)
     * @param radius Radius. If distance is less than radius, orientation is NULL.
     *               &nbsp; <i>(5 is a good value)</i>
     *
     * @return Indication Arrow
     *
     * @luzog Copyrights
     */
    public static String getOrientationChar(double yaw, double fromX, double fromZ, double toX, double toZ, double radius) {
        if (Math.sqrt(Math.pow(fromX - toX, 2) + Math.pow(fromZ - toZ, 2)) <= radius)
            return a[10];

        double y = (yaw >= 0 ? yaw : 360 + yaw) * Math.PI / 180;
        double theta = Math.acos((-Math.sin(y) * (toX - fromX) + Math.cos(y) * (toZ - fromZ))
                / Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toZ - fromZ, 2))) * 180 / Math.PI;
        boolean isLeft = -Math.sin(y) * (toZ - fromZ) - Math.cos(y) * (toX - fromX) > 0;

        if (btw(theta, 22.5, 67.5))
            return a[isLeft ? 1 : 7];
        else if (btw(theta, 67.5, 112.5))
            return a[isLeft ? 2 : 6];
        else if (btw(theta, 112.5, 157.5))
            return a[isLeft ? 3 : 5];
        else if (btw(theta, 157.5, 202.5))
            return a[4];
        else
            return a[0];
    }

    /**
     * Fruit de plusieurs recherches qui n'ont finalement menées nulle part.<br>
     * En espérant qu'un jour, ce bout de code <i><b>inutile</b></i> servira...<br>
     * <br>
     *
     * @param yaw Yaw orientation in degrees (yaw ∈ [-360 ; 360])
     *
     * @return Indication Arrow
     */
    public static String getCompass(double yaw) {
        if (btw(yaw, 22.5, 67.5) || btw(yaw, -337.5, -292.5))
            return a[1];
        else if (btw(yaw, 67.5, 112.5) || btw(yaw, -292.5, -247.5))
            return a[2];
        else if (btw(yaw, 112.5, 157.5) || btw(yaw, -247.5, -202.5))
            return a[3];
        else if (btw(yaw, 157.5, 202.5) || btw(yaw, -202.5, -157.5))
            return a[4];
        else if (btw(yaw, 202.5, 247.5) || btw(yaw, -157.5, -112.5))
            return a[5];
        else if (btw(yaw, 247.5, 292.5) || btw(yaw, -112.5, -67.5))
            return a[6];
        else if (btw(yaw, 292.5, 337.5) || btw(yaw, -67.5, -22.5))
            return a[7];
        else // if(btw(yaw, 0, 22.5) || btw(yaw, 0, -22.5) || btw(yaw, -360, -337.5))
            return a[0];
    }

    public static boolean btw(double n, double a, double b) {
        return n >= a && n < b;
    }

    public GManager getManager() {
        return manager;
    }

    public void setManager(GManager manager) {
        this.manager = manager;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public long getSavingTimeOut() {
        return savingTimeOut;
    }

    public void setSavingTimeOut(long savingTimeOut) {
        this.savingTimeOut = savingTimeOut;
        savingCoolDown = savingTimeOut;
    }

    public long getSavingCoolDown() {
        return savingCoolDown;
    }

    public void setSavingCoolDown(long savingCoolDown) {
        this.savingCoolDown = savingCoolDown;
    }

    public Objective getObjective() {
        return objective;
    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    public Map<String, Integer> getL() {
        return l;
    }

    public void setL(Map<String, Integer> l) {
        this.l = l;
    }

    public Map<Integer, String> getAl() {
        return al;
    }

    public void setAl(Map<Integer, String> al) {
        this.al = al;
    }

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }
}
