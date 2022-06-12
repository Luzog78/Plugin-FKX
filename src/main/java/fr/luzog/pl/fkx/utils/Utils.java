package fr.luzog.pl.fkx.utils;

import fr.luzog.pl.fkx.Main;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * If the location is within the bounds of the two locations, return true
     *
     * @param loc The location you want to check if it's inside the cuboid.
     * @param loc1 The first location
     * @param loc2 The location of the second corner of the cuboid.
     * @return A boolean value.
     */
    public static boolean isInside(Location loc, Location loc1, Location loc2) {
        double maxX = Math.max(loc1.getX(), loc2.getX()), maxY = Math.max(loc1.getY(), loc2.getY()), maxZ = Math.max(loc1.getZ(), loc2.getZ()),
                minX = Math.min(loc1.getX(), loc2.getX()), minY = Math.min(loc1.getY(), loc2.getY()), minZ = Math.min(loc1.getZ(), loc2.getZ());
        return loc.getX() <= maxX && loc.getX() >= minX && loc.getY() <= maxY && loc.getY() >= minY && loc.getZ() <= maxZ && loc.getZ() >= minZ
                && (loc.getWorld().getUID().equals(loc1.getWorld().getUID()) || loc.getWorld().getUID().equals(loc2.getWorld().getUID()));
    }

    /**
     * Given a column and row, return the position of the cell in the inventory.
     *
     * @param col The column of the cell.
     * @param row The row of the cell you want to get the position of.
     * @return The position of the cell in the array.
     */
    public static int posOf(int col, int row) {
        return row * 9 + col;
    }

    /**
     * Given a position, return the column and row of that position.
     *
     * @param position The position of the cell in the inventory.
     * @return A pair of integers.
     */
    public static Pair<Integer, Integer> colAndRowOf(int position) {
        return new Pair<>(position % 9, position / 9);
    }

    public static List<Integer> zoneOf(int pos1, int pos2) {
        return zoneOf(pos1, pos2, false);
    }

    /**
     * It returns a list of all the positions in the zone between two positions, including the two positions themselves
     *
     * @param pos1 The first position.
     * @param pos2 The position of the second point.
     * @param wall If true, the zone will be a filled zone, if false, it will be an empty zone.
     * @return A list of integers.
     */
    public static List<Integer> zoneOf(int pos1, int pos2, boolean wall) {
        int i1, j1, i2, j2;
        if (colAndRowOf(pos1).getKey() <= colAndRowOf(pos2).getKey()) {
            i1 = colAndRowOf(pos1).getKey();
            j1 = colAndRowOf(pos2).getKey();
        } else {
            i1 = colAndRowOf(pos2).getKey();
            j1 = colAndRowOf(pos1).getKey();
        }
        if (colAndRowOf(pos1).getValue() <= colAndRowOf(pos2).getValue()) {
            i2 = colAndRowOf(pos1).getValue();
            j2 = colAndRowOf(pos2).getValue();
        } else {
            i2 = colAndRowOf(pos2).getValue();
            j2 = colAndRowOf(pos1).getValue();
        }
        return new ArrayList<Integer>() {{
            for (int col = i1; col <= j1; col++)
                for (int row = i2; row <= j2; row++)
                    if (!wall || col == i1 || col == j1 || row == i2 || row == j2)
                        add(posOf(col, row));
        }};
    }

    /**
     * It fills an inventory with an itemstack from position 1 to position 2
     *
     * @param inv The inventory you want to fill
     * @param pos1 The first position to start filling from.
     * @param pos2 The position to stop filling at.
     * @param is The itemstack you want to fill the inventory with
     */
    public static void fill(Inventory inv, int pos1, int pos2, ItemStack is) {
        fill(inv, pos1, pos2, false, is);
    }

    /**
     * Fills the inventory with the given item stack from the given positions.
     *
     * @param inv The inventory to fill
     * @param pos1 The first position of the zone.
     * @param pos2 The second position of the zone.
     * @param wall If true, the zone will be a filled zone, if false, it will be an empty zone.
     * @param is The item to fill the inventory with
     */
    public static void fill(Inventory inv, int pos1, int pos2, boolean wall, ItemStack is) {
        zoneOf(pos1, pos2, wall).forEach(p -> inv.setItem(p, is));
    }

    /**
     * @param p    <strong style="color: #ff0000">null</strong> to Broadcast
     * @param text Use variable <strong style="color: #ffffff">%i%</strong> and use <strong style="color: #ffffff">§r</strong> to default color
     */
    public static void countDown(@Nullable Player p, int sec, boolean ascendant, boolean title, boolean chat, String text,
                                 String defaultColor, String warningColor, String criticalColor, String lastColor, Runnable r) {
        new BukkitRunnable() {
            int i = sec;

            @Override
            public void run() {
                if (i == 0) {
                    this.cancel();
                    r.run();
                    return;
                }

                String ii = (ascendant ? sec - i : i) + "";
                String s = ("§r" + text.replace("%i%", ii).replace("%I%", ii)).replace("\n", "\n§r")
                        .replace("§r", i == 3 ? warningColor : i == 2 ? criticalColor : i == 1 ? lastColor : defaultColor);

                if (p == null) {
                    if (chat)
                        Bukkit.broadcastMessage(s);
                    if (title)
                        Bukkit.getOnlinePlayers().forEach(pl ->
                                pl.sendTitle(s.contains("\n") ? s.split("\n", 2)[0] : s, s.contains("\n") ? s.split("\n", 2)[1] : ""));
                } else {
                    if (title)
                        p.sendTitle(s.contains("\n") ? s.split("\n", 2)[0] : s, s.contains("\n") ? s.split("\n", 2)[1] : "");
                    if (chat)
                        p.sendMessage(s);
                }

                i--;
            }
        }.runTaskTimer(Main.instance, 0, 20);
    }

    /**
     * It takes a location, and returns a location that is the center of the block that the original location is in
     * (Y position included)
     *
     * @param loc The location to normalize
     * @return A Location object
     */
    public static Location normalize(Location loc) {
        return normalize(loc, true);
    }

    /**
     * It takes a location, and returns a location that is the center of the block that the original location is in
     *
     * @param loc The location to normalize
     * @param y If true, the y-coordinate will be normalized. If false, it will not.
     * @return A Location object
     */
    public static Location normalize(Location loc, boolean y) {
        Location l = loc.clone();
        l.setX(((int) l.getX()) + 0.5);
        if(y)
            l.setY(((int) l.getY()) + 0.5);
        l.setZ(((int) l.getZ()) + 0.5);
        l.setYaw(0);
        l.setPitch(0);
        return l;
    }

}
