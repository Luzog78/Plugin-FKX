package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.fk.*;
import fr.luzog.pl.fkx.fk.GUIs.GuiPerm;
import fr.luzog.pl.fkx.fk.GUIs.GuiPlayers;
import fr.luzog.pl.fkx.fk.GUIs.GuiZones;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FKCPermissions {
    public static final String syntaxe = "/fk perm [help | list | team | zone | player | global | neutral | friendly | hostile | priority] [<args...>]",
            syntaxe_tzp = "/fk perm (team | zone | player) <teamId/zoneId/playerId> [info | <permission> [<value>]]",
            syntaxe_gnfhp = "/fk perm (global | neutral | friendly | hostile | priority) [info | <permission> [<value>]]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            if (sender instanceof Player)
                u.getPlayer().openInventory(GuiPerm.getInv("fk"));
            else
                u.err(CmdUtils.err_not_player);

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list")) {
            u.succ("Liste des permissions :");
            for (FKPermissions.Type type : FKPermissions.Type.values())
                u.succ(" - §6" + type.name());
        } else if (args[1].equalsIgnoreCase("team") || args[1].equalsIgnoreCase("zone")
                || args[1].equalsIgnoreCase("player")) {
            u.setSyntaxe(syntaxe_tzp);
            if (args.length == 2)
                u.synt();
            else if (args[1].equalsIgnoreCase("team"))
                if (FKManager.getCurrentGame().getTeam(args[2]) == null)
                    u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
                else {
                    FKTeam t = FKManager.getCurrentGame().getTeam(args[2]);
                    if (args.length == 3)
                        if (sender instanceof Player)
                            u.getPlayer().openInventory(GuiPerm.getPermsInv(t.getPermissions(),
                                    FKManager.getBanner(t.getColor()),
                                    GuiPerm.getPermsItem(t.getPermissions(), Material.IRON_SWORD,
                                            "§fPermissions de Team", null, "null"),
                                    "team " + t.getId(), "fk teams " + t.getId()));
                        else
                            u.succ(CmdUtils.err_not_player);
                    else if (args[3].equalsIgnoreCase("info")) {
                        u.succ("Permissions de l'équipe " + t.getColor() + t.getName() + "§r :");
                        for (FKPermissions.Type type : FKPermissions.Type.values())
                            u.succ(" - §6" + type.name() + "§r : " + t.getPermissions().getPermission(type).toFormattedString());
                    } else
                        try {
                            FKPermissions.Type type = FKPermissions.Type.valueOf(args[3].toUpperCase());
                            if (args.length >= 5)
                                try {
                                    FKPermissions.Definition def = FKPermissions.Definition.valueOf(args[4].toUpperCase());
                                    t.getPermissions().setPermission(type, def);
                                    t.savePermissions();
                                } catch (IllegalArgumentException ee) {
                                    u.err("Définition inconnue. (" + args[4] + ")");
                                }
                            u.succ("Permission de l'équipe " + t.getColor() + t.getName() + "§r :\n"
                                    + " - §6" + type.name() + "§r : " + t.getPermissions().getPermission(type).toFormattedString());
                        } catch (IllegalArgumentException e) {
                            u.err("Permission inconnue. (" + args[3] + ")");
                        }
                }
            else if (args[1].equalsIgnoreCase("zone"))
                if (FKManager.getCurrentGame().getZone(args[2]) == null)
                    u.err("Zone inexistante. (" + args[2] + ")");
                else {
                    FKZone z = FKManager.getCurrentGame().getZone(args[2]);
                    if (args.length == 3)
                        if (sender instanceof Player)
                            u.getPlayer().openInventory(GuiPerm.getPermsInv(z.getPermissions(),
                                    GuiZones.getZoneItem(z),
                                    GuiPerm.getPermsItem(z.getPermissions(), Material.IRON_SWORD,
                                            "§fPermissions de Zone", null, "null"),
                                    "zone " + z.getId(), "fk zones " + z.getId()));
                        else
                            u.succ(CmdUtils.err_not_player);
                    else if (args[3].equalsIgnoreCase("info")) {
                        u.succ("Permissions de la zone §f" + z.getId() + "§r :");
                        for (FKPermissions.Type type : FKPermissions.Type.values())
                            u.succ(" - §6" + type.name() + "§r : " + z.getPermissions().getPermission(type).toFormattedString());
                    } else
                        try {
                            FKPermissions.Type type = FKPermissions.Type.valueOf(args[3].toUpperCase());
                            if (args.length >= 5)
                                try {
                                    FKPermissions.Definition def = FKPermissions.Definition.valueOf(args[4].toUpperCase());
                                    z.getPermissions().setPermission(type, def);
                                    z.savePermissions();
                                } catch (IllegalArgumentException ee) {
                                    u.err("Définition inconnue. (" + args[4] + ")");
                                }
                            u.succ("Permission de la zone §f" + z.getId() + "§r :\n"
                                    + " - §6" + type.name() + "§r : " + z.getPermissions().getPermission(type).toFormattedString());
                        } catch (IllegalArgumentException e) {
                            u.err("Permission inconnue. (" + args[3] + ")");
                        }
                }
            else if (args[1].equalsIgnoreCase("player"))
                try {
                    FKPlayer p = FKManager.getCurrentGame().getPlayer(args[2], true);
                    if (args.length == 3)
                        if (sender instanceof Player)
                            u.getPlayer().openInventory(GuiPerm.getPermsInv(p.getPersonalPermissions(),
                                    GuiPlayers.getHead(p.getName(), null, "null"),
                                    GuiPerm.getPermsItem(p.getPersonalPermissions(), Material.IRON_SWORD,
                                            "§fPermissions de Joueur", null, "null"),
                                    "player " + p.getName(), "fk players " + p.getName()));
                        else
                            u.succ(CmdUtils.err_not_player);
                    else if (args[3].equalsIgnoreCase("info")) {
                        u.succ("Permissions du joueur " + p.getDisplayName() + "§r :");
                        for (FKPermissions.Type type : FKPermissions.Type.values())
                            u.succ(" - §6" + type.name() + "§r : " + p.getPersonalPermissions().getPermission(type).toFormattedString());
                    } else
                        try {
                            FKPermissions.Type type = FKPermissions.Type.valueOf(args[3].toUpperCase());
                            if (args.length >= 5)
                                try {
                                    FKPermissions.Definition def = FKPermissions.Definition.valueOf(args[4].toUpperCase());
                                    p.getPersonalPermissions().setPermission(type, def);
                                    p.savePersonalPermissions();
                                } catch (IllegalArgumentException ee) {
                                    u.err("Définition inconnue. (" + args[4] + ")");
                                }
                            u.succ("Permission du joueur " + p.getDisplayName() + "§r :\n"
                                    + " - §6" + type.name() + "§r : " + p.getPersonalPermissions().getPermission(type).toFormattedString());
                        } catch (IllegalArgumentException e) {
                            u.err("Permission inconnue. (" + args[3] + ")");
                        }
                } catch (FKException.PlayerDoesNotExistException e) {
                    u.err(CmdUtils.err_player_does_not_exist + " (" + args[2] + ")");
                }
        } else {
            FKPermissions perm = args[1].equalsIgnoreCase("global") ? FKManager.getCurrentGame().getGlobal()
                    : args[1].equalsIgnoreCase("neutral") ? FKManager.getCurrentGame().getNeutral()
                    : args[1].equalsIgnoreCase("friendly") ? FKManager.getCurrentGame().getFriendly()
                    : args[1].equalsIgnoreCase("hostile") ? FKManager.getCurrentGame().getHostile()
                    : args[1].equalsIgnoreCase("priority") ? FKManager.getCurrentGame().getPriority() : null;
            if (perm != null) {
                u.setSyntaxe(syntaxe_gnfhp);
                if (args.length == 2)
                    if (sender instanceof Player)
                        u.getPlayer().openInventory(GuiPerm.getPermsInv(perm,
                                GuiPerm.getMainItem(null, "null"),
                                args[1].equalsIgnoreCase("global") ? GuiPerm.getPermsItem(perm, Material.BEACON, "§fGlobal", null, "null")
                                        : args[1].equalsIgnoreCase("neutral") ? GuiPerm.getPermsItem(perm, Material.BEACON, "§fZones Neutres", null, "null")
                                        : args[1].equalsIgnoreCase("friendly") ? GuiPerm.getPermsItem(perm, Material.BEACON, "§fZones Amicales", null, "null")
                                        : args[1].equalsIgnoreCase("hostile") ? GuiPerm.getPermsItem(perm, Material.BEACON, "§fZones Hostiles", null, "null")
                                        : args[1].equalsIgnoreCase("priority") ? GuiPerm.getPermsItem(perm, Material.BEACON, "§bPrioritaires", null, "null")
                                        : GuiPerm.getPermsItem(perm, Material.FIREWORK_CHARGE, "§7???", null, "null"),
                                args[1], "fk perm"));
                    else
                        u.succ(CmdUtils.err_not_player);
                else if (args[2].equalsIgnoreCase("info")) {
                    u.succ("Permissions §f" + args[1].toLowerCase() + "§r :");
                    for (FKPermissions.Type type : FKPermissions.Type.values())
                        u.succ(" - §6" + type.name() + "§r : " + perm.getPermission(type).toFormattedString());
                } else if (args[2].equalsIgnoreCase("help") || args[2].equals("?"))
                    u.synt();
                else
                    try {
                        FKPermissions.Type type = FKPermissions.Type.valueOf(args[2].toUpperCase());
                        if (args.length >= 4)
                            try {
                                FKPermissions.Definition def = FKPermissions.Definition.valueOf(args[3].toUpperCase());
                                perm.setPermission(type, def);
                                if (args[1].equalsIgnoreCase("global"))
                                    FKManager.getCurrentGame().getConfig().load().setGlobalPermissions(perm, true).save();
                                else if (args[1].equalsIgnoreCase("neutral"))
                                    FKManager.getCurrentGame().getConfig().load().setNeutralPermissions(perm, true).save();
                                else if (args[1].equalsIgnoreCase("friendly"))
                                    FKManager.getCurrentGame().getConfig().load().setFriendlyPermissions(perm, true).save();
                                else if (args[1].equalsIgnoreCase("hostile"))
                                    FKManager.getCurrentGame().getConfig().load().setHostilePermissions(perm, true).save();
                                else if (args[1].equalsIgnoreCase("priority"))
                                    FKManager.getCurrentGame().getConfig().load().setPriorityPermissions(perm, true).save();
                            } catch (IllegalArgumentException ee) {
                                u.err("Définition inconnue. (" + args[3] + ")");
                            }
                        u.succ("Permission §f" + args[1].toLowerCase() + "§r :\n"
                                + " - §6" + type.name() + "§r : " + perm.getPermission(type).toFormattedString());
                    } catch (IllegalArgumentException e) {
                        u.err("Permission inconnue. (" + args[2] + ")");
                    }
            } else
                u.synt();
        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<String>() {{
            if (args.length == 2) {
                add("?");
                add("help");
                add("list");
                add("team");
                add("zone");
                add("player");
                add("global");
                add("neutral");
                add("friendly");
                add("hostile");
                add("priority");
            } else if (args.length >= 3)
                if (args[1].equalsIgnoreCase("team")) {
                    if (args.length == 3)
                        addAll(FKManager.getCurrentGame().getTeams().stream().map(FKTeam::getId).collect(Collectors.toList()));
                    else if (args.length == 4)
                        addAll(Arrays.stream(FKPermissions.Type.values()).map(FKPermissions.Type::name).collect(Collectors.toList()));
                    else if (args.length == 5)
                        addAll(Arrays.stream(FKPermissions.Definition.values()).map(FKPermissions.Definition::name).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("zone")) {
                    if (args.length == 3)
                        addAll(FKManager.getCurrentGame().getZones().stream().map(FKZone::getId).collect(Collectors.toList()));
                    else if (args.length == 4)
                        addAll(Arrays.stream(FKPermissions.Type.values()).map(FKPermissions.Type::name).collect(Collectors.toList()));
                    else if (args.length == 5)
                        addAll(Arrays.stream(FKPermissions.Definition.values()).map(FKPermissions.Definition::name).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("player")) {
                    if (args.length == 3)
                        addAll(Utils.getAllPlayers());
                    else if (args.length == 4)
                        addAll(Arrays.stream(FKPermissions.Type.values()).map(FKPermissions.Type::name).collect(Collectors.toList()));
                    else if (args.length == 5)
                        addAll(Arrays.stream(FKPermissions.Definition.values()).map(FKPermissions.Definition::name).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("global") || args[1].equalsIgnoreCase("neutral")
                        || args[1].equalsIgnoreCase("friendly") || args[1].equalsIgnoreCase("hostile")
                        || args[1].equalsIgnoreCase("priority")) {
                    if (args.length == 3)
                        addAll(Arrays.stream(FKPermissions.Type.values()).map(FKPermissions.Type::name).collect(Collectors.toList()));
                    else if (args.length == 4)
                        addAll(Arrays.stream(FKPermissions.Definition.values()).map(FKPermissions.Definition::name).collect(Collectors.toList()));
                }
        }};
    }
}
