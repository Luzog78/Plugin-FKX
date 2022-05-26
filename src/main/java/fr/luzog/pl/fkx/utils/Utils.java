package fr.luzog.pl.fkx.utils;

import javafx.util.Pair;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static boolean isInside(Location loc, Location loc1, Location loc2) {
        return (loc.getWorld() == null || loc1.getWorld() == null || loc2.getWorld() == null
                || (loc.getWorld().getUID().equals(loc1.getWorld().getUID()) && !loc.getWorld().getUID().equals(loc2.getWorld().getUID())))
                && ((!(loc1.getX() > loc2.getX()) || (!(loc.getX() > loc1.getX()) && !(loc.getX() < loc2.getX())))
                && !(loc.getX() < loc1.getX()) && !(loc.getX() > loc2.getX())) &&
                ((!(loc1.getY() > loc2.getY()) || (!(loc.getY() > loc1.getY()) && !(loc.getY() < loc2.getY())))
                        && !(loc.getY() < loc1.getY()) && !(loc.getY() > loc2.getY())) &&
                ((!(loc1.getZ() > loc2.getZ()) || (!(loc.getZ() > loc1.getZ()) && !(loc.getZ() < loc2.getZ())))
                        && !(loc.getZ() < loc1.getZ()) && !(loc.getZ() > loc2.getZ()));
    }

    public static int posOf(int col, int row) {
        return row * 9 + col;
    }

    public static Pair<Integer, Integer> colAndRowOf(int position) {
        return new Pair<>(position % 9, position / 9);
    }

    public static List<Integer> zoneOf(int pos1, int pos2) {
        return zoneOf(pos1, pos2, false);
    }

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
        return new ArrayList<Integer>(){{
            for(int col = i1; col <= j1; col++)
                for(int row = i2; row <= j2; row++)
                    if(!wall || col == i1 || col == j1 || row == i2 || row == j2)
                        add(posOf(col, row));
        }};
    }

    public static void fill(Inventory inv, int pos1, int pos2, ItemStack is){
        fill(inv, pos1, pos2, false, is);
    }

    public static void fill(Inventory inv, int pos1, int pos2, boolean wall, ItemStack is){
        zoneOf(pos1, pos2, wall).forEach(p -> inv.setItem(p, is));
    }

}
