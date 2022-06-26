package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.utils.Broadcast;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.text.DecimalFormat;
import java.util.*;

public class FKListener {

    public static final String y = "§2" + SpecialChars.YES;
    public static final String n = "§4" + SpecialChars.NO;
    public static final String[] a = new String[]{"⬆", "⬈", "➡", "⬊", "⬇", "⬋", "⬅", "⬉", "⬌", "⬍", "§d۞§r"};
    public static final String deactivated = "§c§oDesactivé";
    public static final String no_team = "§4§lAucune équipe";

    private FKManager manager;

    private int taskID;
    private long savingTimeOut;
    private long savingCoolDown;
    private final String objectiveId;

    private Objective objective;
    private Map<String, Integer> l; // ScoreBoard List
    private Map<Integer, String> al; // Ancian ScoreBoard List -> to up to date
    private String scoreName /* = "§6§l§n-=[ §1F§aa§3l§cl§5e§en §7K§6i§dn§4g§bd§2o§9m §8I §6]=-" */;

    public FKListener(String objectiveId, String scoreName, long savingTimeOut) {
        this.scoreName = scoreName;
        this.objectiveId = objectiveId;

        this.savingTimeOut = savingTimeOut; // 60 * 5; // 5 min in sec
        savingCoolDown = savingTimeOut;
        l = new HashMap<>();
        al = new HashMap<>();
    }

    public void scheduleTask() {
        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.instance, new BukkitRunnable() {

            final long delayer = 4L; // Each at 1/4 sec so : original = time / (1/4) == time * 4
            long countDown = savingTimeOut * delayer;

            @Override
            public void run() {
                if (countDown - 1 == 0) {
                    // TODO -> Save.save();
                    countDown = savingTimeOut * delayer;
                } else
                    countDown--;

                // TODO -> refreshScoreName();
                // TODO -> objective.setDisplayName(scoreName);

                if (manager.getState() == FKManager.State.RUNNING) {
                    manager.increaseTime(5, false);

                    if (manager.getTime() >= 24000) {
                        manager.increaseDay(true);
                        manager.setTime(0, false);
                        Broadcast.succ("§e§lNouvelle journée !!§r Passage au jour !" + manager.getDay() + " !");
                        manager.checkActivations(false);
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
                }

                setScoreLines();
                updateScoreLines();

                manager.getPlayers().forEach(fkp -> {
                    Player p = fkp.getPlayer();
                    if (p == null)
                        return;

                    String displayName = fkp.getDisplayName();

                    p.setDisplayName(displayName);

                    if (Vanish.vanished.contains(p.getUniqueId()))
                        if (Vanish.isPrefix)
                            displayName = Vanish.pre_suf_ix + displayName;
                        else
                            displayName += Vanish.pre_suf_ix;

                    p.setPlayerListName(displayName);

                    if (!p.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard()))
                        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(getTHF(p));
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(getActionBar(p));
                });

                new ArrayList<Player>(Bukkit.getOnlinePlayers()) {{
                    removeIf(p -> !FKManager.getGlobalPlayer(p.getUniqueId(), p.getName()).isEmpty());
                }}.forEach(p -> {
                    p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(getDefaultTHF(p));
                });
            }

        }, 0, 5); // Each 1/4 sec
    }

    public void cancelTask() {
        try {
            Bukkit.getScheduler().cancelTask(taskID);
        } catch (Exception e) {
            Broadcast.err("!Error : Cannot Cancelling Auto Task. (" + e + ")");
        }
    }

    public void refreshScoreName() {
        String c = "abcde123456789";
        String space = "   ";
        List<String> l = new ArrayList<>();
        for (char ch : c.toCharArray())
            l.add(ch + "");
        String p = "§";
        scoreName = space + p + ca(l) + "F" + p + ca(l) + "a" + p + ca(l) + "l" + p + ca(l) + "l" + p + ca(l) + "e" + p
                + ca(l) + "n " + p + ca(l) + "K" + p + ca(l) + "i" + p + ca(l) + "n" + p + ca(l) + "g" + p + ca(l) + "d"
                + p + ca(l) + "o" + p + ca(l) + "m " + p + ca(l) + "I" + space;
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
            FKOptions.FKOption o = new FKOptions.FKOption[]{manager.getOptions().getPvp(), manager.getOptions().getNether(),
                    manager.getOptions().getAssaults(), manager.getOptions().getEnd()}[i];
            l.put("§a" + o.getName() + "§a : " + (o.isActivated() ? y : n + "§7§O (J" + o.getActivationDay() + ")"), /* . */ -i + 8);
        }
        l.put("§c---------- ", /* ............................................................................................ */ 4);
        l.put("§9Type : §9Normal", /* ........................................................................................ */ 3);
        l.put("§9Chest : §4None", /* ......................................................................................... */ 2);
        l.put("§c----------  ", /* ........................................................................................... */ 1);
        l.put("§d{EVENT}", /* ................................................................................................ */ 0);
    }

    public void updateScoreLines() {
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
        List<String> h = new ArrayList<>(), f = new ArrayList<>();
        h.add("§c======= §9§l-=[ §6§lFallen Kingdom X §9§l]=- §c=======");
        h.add(" ");
        h.add("§9Organisateur : §f" + "Mathis_Bruel");
        h.add("§9Developpeur : §f" + "Luzog78");
        h.add(" ");
        h.add("§3Bienvenue à toi cher §9" + (manager.getPlayer(p.getUniqueId(), false) == null ? p.getDisplayName() : manager.getPlayer(p.getUniqueId(), false).getDisplayName()) + "§3,");
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
        DecimalFormat df = new DecimalFormat("0.0");
        f.add("§8§l§nVous :§r  " + (manager.getPlayer(p.getUniqueId(), false) == null ? no_team
                : manager.getPlayer(p.getUniqueId(), false).getTeam().getName() + "§7 - §6"
                + (!p.getWorld().getUID().equals(manager.getPlayer(p.getUniqueId(), false).getTeam().getSpawn().getWorld().getUID()) ?
                "xxx,x §e" + getOrientationChar(0, 0, 0, 0, 0)
                : df.format(p.getLocation().distance(manager.getPlayer(p.getUniqueId(), false).getTeam().getSpawn()))
                + "§e " + getOrientationChar(p.getLocation().getYaw(), p.getLocation().getX(), p.getLocation().getZ(),
                manager.getPlayer(p.getUniqueId(), false).getTeam().getSpawn().getX(), manager.getPlayer(p.getUniqueId(), false).getTeam().getSpawn().getZ()))));
        f.add(" ");
        f.add("§8§l§nAutres équipes :§r");
        f.add(" ");
        manager.getParticipantsTeams().forEach(t -> {
            if (manager.getPlayer(p.getUniqueId(), false) == null || !manager.getPlayer(p.getUniqueId(), false).getTeam().equals(t))
                f.add(t.getName() + "§7 - §6" + (!p.getWorld().getUID().equals(t.getSpawn().getWorld().getUID()) ?
                        "xxx,x §e" + getOrientationChar(0, 0, 0, 0, 0)
                        : df.format(p.getLocation().distance(t.getSpawn())) + "§e " + getOrientationChar(p.getLocation().getYaw(), p.getLocation().getX(),
                        p.getLocation().getZ(), t.getSpawn().getX(), t.getSpawn().getZ())));
        });
        f.add(" ");
//        f.add("§6Save in " + (getSavingTime() < 60 ? "§c" + getSavingTime() + "§6s"
//                : "§c" + ((int) (getSavingTime() / 60)) + "§6min and §c" + (getSavingTime() % 60) + "§6s"));
        f.add("§8Online : §b" + Bukkit.getOnlinePlayers().size() + "§7/" + Bukkit.getMaxPlayers() + "   §8Ip : §a"
                + (Bukkit.getServer().getIp().equals("") ? "localhost" : Bukkit.getServer().getIp()));
        f.add("§c====================================");
        return Utils.getTabHeaderAndFooter(h, f);
    }

    public PacketPlayOutPlayerListHeaderFooter getDefaultTHF(Player p) {
        List<String> h = new ArrayList<>(), f = new ArrayList<>();
        h.add("§c======= §9§l-=[ §6§lFallen Kingdom X §9§l]=- §c=======");
        h.add(" ");
        h.add("§9Organisateur : §f" + "Mathis_Bruel");
        h.add("§9Developpeur : §f" + "Luzog78");
        h.add(" ");
        h.add("§cBienvenue à toi cher §f" + p.getDisplayName() + "§c,");
        h.add("§cMalheureusement, tu n'es actuellement");
        h.add("§cpas un participant à §6Fallen Kingdom §lX§c.");
        h.add("§cDemande à un §4Administrateur");
        h.add("§cde t'ajouter dans une partie.");
        h.add(" ");
        h.add(" ");
        h.add("§7Joueurs en ligne :");
        h.add("§7---");
        f.add("§7---");
        f.add(" ");
        f.add(" ");
        f.add("§8Online : §b" + Bukkit.getOnlinePlayers().size() + "§7/" + Bukkit.getMaxPlayers() + "   §8Ip : §a"
                + (Bukkit.getServer().getIp().equals("") ? "localhost" : Bukkit.getServer().getIp()));
        f.add("§c====================================");
        return Utils.getTabHeaderAndFooter(h, f);
    }

    public PacketPlayOutChat getActionBar(Player p) {
        String msg = "§6" + new DecimalFormat("0.0").format(p.getLocation().distance(new Location(p.getWorld(), -256.5, p.getLocation().getY(), -143.5)))
                + "m §e" + getOrientationChar(p.getLocation().getYaw(), p.getLocation().getX(), p.getLocation().getZ(), -256.5, -143.5);
        return new PacketPlayOutChat(new ChatComponentText(msg), (byte) 2);
    }

    /**
     * Formules de <strong style='color: #ff0000'>Luzog78</strong> !<br>
     * Très (trop) fier de lui !<br>
     * Car il les a trouvé <n>seul</n> en <span style='color: #ffffff'>3h15</span>.<br>
     * <br>
     * &nbsp; &nbsp; <strong>--> EDIT : </strong> Au final, ça marche toujours pas bien. ^^'<br>
     *
     * <br>
     *
     * @param yaw   Yaw orientation in degrees (yaw ∈ [-360 ; 360])
     * @param fromX Position X of Object A (Player position)
     * @param fromZ Position Z of Object A (Player position)
     * @param toX   Position X of Object B (Targeted Object position)
     * @param toZ   Position Z of Object B (Targeted Object position)
     *
     * @return Indication Arrow
     *
     * @luzog Copyrights
     */
    public static String getOrientationChar(double yaw, double fromX, double fromZ, double toX, double toZ) {
        if (Math.abs(fromX - toX) < 3 && Math.abs(fromZ - toZ) < 3)
            return a[10];

        double y = (yaw >= 0 ? yaw : 360 - yaw) * Math.PI / 180;
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

    public FKManager getManager() {
        return manager;
    }

    public void setManager(FKManager manager) {
        this.manager = manager;
        objective = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("fko" + UUID.randomUUID().toString().substring(0, 4) + ":" + objectiveId, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
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

    public String getObjectiveId() {
        return objectiveId;
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
