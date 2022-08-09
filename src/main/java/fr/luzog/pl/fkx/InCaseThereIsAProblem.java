package fr.luzog.pl.fkx;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.*;
import java.util.stream.Collectors;

public class InCaseThereIsAProblem {

    public static ArrayList<Material> breakWhitelist, placeWhitelist, breakBlacklist, placeBlacklist;

    public static void init() {
        Config c = new Config().load()
                .setBreakWhitelist(new ArrayList<>(), false)
                .setPlaceWhitelist(new ArrayList<>(), false)
                .setBreakBlacklist(new ArrayList<>(), false)
                .setPlaceBlacklist(new ArrayList<>(), false)
                .save().load();
        breakWhitelist = c.getBreakWhitelist();
        placeWhitelist = c.getPlaceWhitelist();
        breakBlacklist = c.getBreakBlacklist();
        placeBlacklist = c.getPlaceBlacklist();
    }

    public static class Config extends fr.luzog.pl.fkx.utils.Config {
        public static final String BREAK_WHITELIST = "blocks.break.whitelist", BREAK_BLACKLIST = "blocks.break.blacklist",
                PLACE_WHITELIST = "blocks.place.whitelist", PLACE_BLACKLIST = "blocks.place.blacklist";

        public Config() {
            super("In Case There Is A Problem.yml", true);
        }

        @Override
        public Config load() {
            super.load();
            return this;
        }

        @Override
        public Config save() {
            super.save();
            return this;
        }

        public ArrayList<Material> getBreakWhitelist() {
            return super.matchList(BREAK_WHITELIST, Material.values());
        }

        public Config setBreakWhitelist(ArrayList<Material> whitelist, boolean force) {
            super.set(BREAK_WHITELIST, whitelist.stream().map(Material::name).collect(Collectors.toList()), force);
            return this;
        }

        public ArrayList<Material> getBreakBlacklist() {
            return super.matchList(BREAK_BLACKLIST, Material.values());
        }

        public Config setBreakBlacklist(ArrayList<Material> blacklist, boolean force) {
            super.set(BREAK_BLACKLIST, blacklist.stream().map(Material::name).collect(Collectors.toList()), force);
            return this;
        }

        public ArrayList<Material> getPlaceWhitelist() {
            return super.matchList(PLACE_WHITELIST, Material.values());
        }

        public Config setPlaceWhitelist(ArrayList<Material> whitelist, boolean force) {
            super.set(PLACE_WHITELIST, whitelist.stream().map(Material::name).collect(Collectors.toList()), force);
            return this;
        }

        public ArrayList<Material> getPlaceBlacklist() {
            return super.matchList(PLACE_BLACKLIST, Material.values());
        }

        public Config setPlaceBlacklist(ArrayList<Material> blacklist, boolean force) {
            super.set(PLACE_BLACKLIST, blacklist.stream().map(Material::name).collect(Collectors.toList()), force);
            return this;
        }
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public static void onBreakBlock(BlockBreakEvent e) {
            if (breakWhitelist.contains(e.getBlock().getType())) {
                e.setCancelled(false);
            } else if (breakBlacklist.contains(e.getBlock().getType())) {
                e.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public static void onPlaceBlock(BlockPlaceEvent e) {
            if (placeWhitelist.contains(e.getBlock().getType())) {
                e.setCancelled(false);
            } else if (placeBlacklist.contains(e.getBlock().getType())) {
                e.setCancelled(true);
            }
        }

    }

}
