package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.commands.Admin.Vanish;
import fr.luzog.pl.fkx.utils.Broadcast;
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

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;

public class FKListener {

    public static enum FKState {WAITING, RUNNING, PAUSE, END;}

    public static final String y = "§2✔";
    public static final String n = "§4✖";
    public static final String[] a = new String[]{"⬆", "⬈", "➡", "⬊", "⬇", "⬋", "⬅", "⬉", "⬌", "⬍", "§d۞§r"};
    public static final String deactivated = "§c§oDesactivé";
    public static final String no_team = "§4§lAucune équipe";
    public static final String objectiveId = "fkx-sb";

    private FKManager manager;

    private int taskID;
    private int savingTime;

    private Objective objective;
    private Map<String, Integer> l; // ScoreBoard List
    private Map<Integer, String> al; // Ancian ScoreBoard List -> to up to date
    private String scoreName /* = "§6§l§n-=[ §1F§aa§3l§cl§5e§en §7K§6i§dn§4g§bd§2o§9m §8I §6]=-" */;

    public FKListener(String scoreName) {
        this.scoreName = scoreName;

        savingTime = 60 * 5; // 5 min in sec
        l = new HashMap<>();
        al = new HashMap<>();
    }

    public void scheduleTask() {
        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.instance, new BukkitRunnable() {

            final long originalSaveDelay = savingTime * 4L; // Each at 1/4 sec so : original = time / (1/4) == time * 4
            long delayer = originalSaveDelay;

            @Override
            public void run() {
                if (delayer - 1 == 0) {
                    // TODO -> Save.save();
                    delayer = originalSaveDelay;
                } else
                    delayer--;

                // TODO -> FKManager.refresh();
                // TODO -> refreshScoreName();
                objective.setDisplayName(scoreName);

                setScoreLines();
                updateScoreLines();

                Bukkit.getOnlinePlayers().forEach(p -> {
                    String displayName = p.getName();
                    if (manager.getPlayer(p.getUniqueId()) != null)
                        if (manager.getPlayer(p.getUniqueId()).getCustomName() != null)
                            displayName = manager.getPlayer(p.getUniqueId()).getTeam().getPrefix() + manager.getPlayer(p.getUniqueId()).getCustomName();
                        else
                            displayName = manager.getPlayer(p.getUniqueId()).getTeam().getPrefix() + displayName;
                    if (Vanish.vanished.contains(p.getUniqueId()))
                        if (Vanish.isPrefix)
                            displayName = Vanish.pre_suf_ix + displayName;
                        else
                            displayName += Vanish.pre_suf_ix;
                    if (!p.getDisplayName().equals(displayName)) {
                        p.setDisplayName(displayName);
                        p.setPlayerListName(displayName);
                    }
                    if (!p.getScoreboard().equals(manager.getMainScoreboard()))
                        p.setScoreboard(manager.getMainScoreboard());
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(getTablistHeaderAndFooter(p));
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(getActionBar(p));
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
        l.put("§r", /* ............................................................................ */ 12);
        l.put("§8Jour : §3" + manager.getDay(), /* ................................................ */ 11);
        l.put("§8Heure : §3" + manager.getFormattedTime(), /* ..................................... */ 10);
        l.put("§c----------", /* ................................................................... */ 9);
        for (int i = 0; i < 4; i++) {
            FKOptions.FKOption o = new FKOptions.FKOption[]{manager.getOptions().getPvp(), manager.getOptions().getNether(),
                    manager.getOptions().getAssauts(), manager.getOptions().getEnd()}[i];
            l.put("§a" + o.getName() + "§a : " + (o.isActivated() ? y : n), /* ................ */ -i + 8);
        }
        l.put("§c---------- ", /* .................................................................. */ 4);
        l.put("§9Type : §9Normal", /* .............................................................. */ 3);
        l.put("§9Chest : §4None", /* ............................................................... */ 2);
        l.put("§c----------  ", /* ................................................................. */ 1);
        l.put("§d{EVENT}", /* ...................................................................... */ 0);
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

    public PacketPlayOutPlayerListHeaderFooter getTablistHeaderAndFooter(Player p) {
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        Field a, b;

        try {
            a = packet.getClass().getDeclaredField("a");
            a.setAccessible(true);
            b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return null;
        }

        List<String> h = new ArrayList<>();
        List<String> f = new ArrayList<>();
        h.add("§c========§6[ §9§lFallen Kingdom X§r§6 ]§c========");
        h.add(" ");
        h.add("§9Organisateur : §f" + "Mathis_Bruel");
        h.add("§9Developpeur : §f" + "Luzog78");
        h.add(" ");
        h.add("§3Bienvenue à toi cher §9" + p.getDisplayName() + "§3,");
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
        f.add("§8§l§nVous :§r  " + (manager.getPlayer(p.getUniqueId()) == null ? no_team
                : manager.getPlayer(p.getUniqueId()).getTeam().getName() + "§7 - §6"
                + df.format(p.getLocation().distance(manager.getPlayer(p.getUniqueId()).getTeam().getSpawn()))
                + "§e " + getOrientationChar(p.getLocation().getYaw(), p.getLocation().getX(), p.getLocation().getZ(),
                manager.getPlayer(p.getUniqueId()).getTeam().getSpawn().getX(), manager.getPlayer(p.getUniqueId()).getTeam().getSpawn().getZ())));
        f.add(" ");
        f.add("§8§l§nAutres équipes :§r");
        f.add(" ");
        manager.getTeams().forEach(t -> {
            if (manager.getPlayer(p.getUniqueId()) == null || !manager.getPlayer(p.getUniqueId()).getTeam().equals(t))
                f.add(t.getName() + "§7 - §6" + df.format(p.getLocation().distance(t.getSpawn())) + "§e "
                        + getOrientationChar(p.getLocation().getYaw(), p.getLocation().getX(), p.getLocation().getZ(), t.getSpawn().getX(), t.getSpawn().getZ()));
        });
        f.add(" ");
//        f.add("§6Save in " + (getSavingTime() < 60 ? "§c" + getSavingTime() + "§6s"
//                : "§c" + ((int) (getSavingTime() / 60)) + "§6min and §c" + (getSavingTime() % 60) + "§6s"));
        f.add("§8Online : §b" + Bukkit.getOnlinePlayers().size() + "§7/" + Bukkit.getMaxPlayers() + "   §8Ip : §a"
                + (Bukkit.getServer().getIp().equals("") ? "localhost" : Bukkit.getServer().getIp()));
        f.add("§c==============================");
        /*
         *
         * ^^^ DEF ^^^ -------------- ||| UPDATE |||
         *
         */
        String header = "";
        String footer = "";
        if (h.isEmpty())
            try {
                a.set(packet, new ChatComponentText(""));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        else
            for (String hd : h)
                header += hd + "\n";
        if (f.isEmpty())
            try {
                b.set(packet, new ChatComponentText(""));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        else
            for (String ft : f)
                footer += ft + "\n";
        try {
            a.set(packet, new ChatComponentText(header.substring(0, header.length() - 1)));
            b.set(packet, new ChatComponentText(footer.substring(0, footer.length() - 1)));
        } catch (StringIndexOutOfBoundsException ignore) {
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
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
     *
     * <br>
     *
     * @param yaw   Yaw orientation in degrees (yaw ∈ [-360 ; 360])
     * @param fromX Position X of Object A (Player position)
     * @param fromZ Position Z of Object A (Player position)
     * @param toX   Position X of Object B (Targeted Object position)
     * @param toZ   Position Z of Object B (Targeted Object position)
     * @return Indication Arrow
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
        objective = manager.getMainScoreboard().registerNewObjective(objectiveId, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getSavingTime() {
        return savingTime;
    }

    public void setSavingTime(int savingTime) {
        this.savingTime = savingTime;
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
