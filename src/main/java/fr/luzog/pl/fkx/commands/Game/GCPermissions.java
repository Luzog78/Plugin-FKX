package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.*;
import fr.luzog.pl.fkx.guis.GuiPerm;
import fr.luzog.pl.fkx.guis.GuiPlayers;
import fr.luzog.pl.fkx.guis.GuiZones;
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

public class GCPermissions {
    public static final String syntaxe = "/" + Main.CMD + " perm [help | list | team | zone | player | global | neutral | friendly | hostile | priority] [<args...>]",
            syntaxe_tzp = "/" + Main.CMD + " perm (team | zone | player) <teamId/zoneId/playerId> [info | <permission> [<value>]]",
            syntaxe_gnfhp = "/" + Main.CMD + " perm (global | neutral | friendly | hostile | priority) [info | <permission> [<value>]]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            if (sender instanceof Player)
                u.getPlayer().openInventory(GuiPerm.getInv(Main.CMD));
            else
                u.err(CmdUtils.err_not_player);

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("list")) {
            u.succ("Liste des permissions :");
            for (GPermissions.Type type : GPermissions.Type.values())
                u.succ(" - §6" + type.name());
        } else if (args[1].equalsIgnoreCase("team") || args[1].equalsIgnoreCase("zone")
                || args[1].equalsIgnoreCase("player")) {
            u.setSyntaxe(syntaxe_tzp);
            if (args.length == 2)
                u.synt();
            else if (args[1].equalsIgnoreCase("team"))
                if (GManager.getCurrentGame().getTeam(args[2]) == null)
                    u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
                else {
                    GTeam t = GManager.getCurrentGame().getTeam(args[2]);
                    if (args.length == 3)
                        if (sender instanceof Player)
                            u.getPlayer().openInventory(GuiPerm.getPermsInv(t.getPermissions(),
                                    GManager.getBanner(t.getColor()),
                                    GuiPerm.getPermsItem(t.getPermissions(), Material.IRON_SWORD,
                                            "§fPermissions de Team", null, "null"),
                                    "team " + t.getId(), Main.CMD + " teams " + t.getId()));
                        else
                            u.succ(CmdUtils.err_not_player);
                    else if (args[3].equalsIgnoreCase("info")) {
                        u.succ("Permissions de l'équipe " + t.getColor() + t.getName() + "§r :");
                        for (GPermissions.Type type : GPermissions.Type.values())
                            u.succ(" - §6" + type.name() + "§r : " + t.getPermissions().getPermission(type).toFormattedString());
                    } else
                        try {
                            GPermissions.Type type = GPermissions.Type.valueOf(args[3].toUpperCase());
                            if (args.length >= 5)
                                try {
                                    GPermissions.Definition def = GPermissions.Definition.valueOf(args[4].toUpperCase());
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
                if (GManager.getCurrentGame().getZone(args[2]) == null)
                    u.err("Zone inexistante. (" + args[2] + ")");
                else {
                    GZone z = GManager.getCurrentGame().getZone(args[2]);
                    if (args.length == 3)
                        if (sender instanceof Player)
                            u.getPlayer().openInventory(GuiPerm.getPermsInv(z.getPermissions(),
                                    GuiZones.getZoneItem(z, null, "null"),
                                    GuiPerm.getPermsItem(z.getPermissions(), Material.IRON_SWORD,
                                            "§fPermissions de Zone", null, "null"),
                                    "zone " + z.getId(), Main.CMD + " zone " + z.getId()));
                        else
                            u.succ(CmdUtils.err_not_player);
                    else if (args[3].equalsIgnoreCase("info")) {
                        u.succ("Permissions de la zone §f" + z.getId() + "§r :");
                        for (GPermissions.Type type : GPermissions.Type.values())
                            u.succ(" - §6" + type.name() + "§r : " + z.getPermissions().getPermission(type).toFormattedString());
                    } else
                        try {
                            GPermissions.Type type = GPermissions.Type.valueOf(args[3].toUpperCase());
                            if (args.length >= 5)
                                try {
                                    GPermissions.Definition def = GPermissions.Definition.valueOf(args[4].toUpperCase());
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
                    GPlayer p = GManager.getCurrentGame().getPlayer(args[2], true);
                    if (args.length == 3)
                        if (sender instanceof Player)
                            u.getPlayer().openInventory(GuiPerm.getPermsInv(p.getPersonalPermissions(),
                                    GuiPlayers.getHead(p.getName(), null, "null"),
                                    GuiPerm.getPermsItem(p.getPersonalPermissions(), Material.IRON_SWORD,
                                            "§fPermissions de Joueur", null, "null"),
                                    "player " + p.getName(), Main.CMD + " players " + p.getName()));
                        else
                            u.succ(CmdUtils.err_not_player);
                    else if (args[3].equalsIgnoreCase("info")) {
                        u.succ("Permissions du joueur " + p.getDisplayName() + "§r :");
                        for (GPermissions.Type type : GPermissions.Type.values())
                            u.succ(" - §6" + type.name() + "§r : " + p.getPersonalPermissions().getPermission(type).toFormattedString());
                    } else
                        try {
                            GPermissions.Type type = GPermissions.Type.valueOf(args[3].toUpperCase());
                            if (args.length >= 5)
                                try {
                                    GPermissions.Definition def = GPermissions.Definition.valueOf(args[4].toUpperCase());
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
                } catch (GException.PlayerDoesNotExistException e) {
                    u.err(CmdUtils.err_player_does_not_exist + " (" + args[2] + ")");
                }
        } else {
            GPermissions perm = args[1].equalsIgnoreCase("global") ? GManager.getCurrentGame().getGlobal()
                    : args[1].equalsIgnoreCase("neutral") ? GManager.getCurrentGame().getNeutral()
                    : args[1].equalsIgnoreCase("friendly") ? GManager.getCurrentGame().getFriendly()
                    : args[1].equalsIgnoreCase("hostile") ? GManager.getCurrentGame().getHostile()
                    : args[1].equalsIgnoreCase("priority") ? GManager.getCurrentGame().getPriority() : null;
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
                                args[1], Main.CMD + " perm"));
                    else
                        u.succ(CmdUtils.err_not_player);
                else if (args[2].equalsIgnoreCase("info")) {
                    u.succ("Permissions §f" + args[1].toLowerCase() + "§r :");
                    for (GPermissions.Type type : GPermissions.Type.values())
                        u.succ(" - §6" + type.name() + "§r : " + perm.getPermission(type).toFormattedString());
                } else if (args[2].equalsIgnoreCase("help") || args[2].equals("?"))
                    u.synt();
                else
                    try {
                        GPermissions.Type type = GPermissions.Type.valueOf(args[2].toUpperCase());
                        if (args.length >= 4)
                            try {
                                GPermissions.Definition def = GPermissions.Definition.valueOf(args[3].toUpperCase());
                                perm.setPermission(type, args[1].equalsIgnoreCase("global")
                                        && def == GPermissions.Definition.DEFAULT ? GPermissions.Definition.OFF : def);
                                if (args[1].equalsIgnoreCase("global"))
                                    GManager.getCurrentGame().getConfig().load().setGlobalPermissions(perm, true).save();
                                else if (args[1].equalsIgnoreCase("neutral"))
                                    GManager.getCurrentGame().getConfig().load().setNeutralPermissions(perm, true).save();
                                else if (args[1].equalsIgnoreCase("friendly"))
                                    GManager.getCurrentGame().getConfig().load().setFriendlyPermissions(perm, true).save();
                                else if (args[1].equalsIgnoreCase("hostile"))
                                    GManager.getCurrentGame().getConfig().load().setHostilePermissions(perm, true).save();
                                else if (args[1].equalsIgnoreCase("priority"))
                                    GManager.getCurrentGame().getConfig().load().setPriorityPermissions(perm, true).save();
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
                        addAll(GManager.getCurrentGame().getTeams().stream().map(GTeam::getId).collect(Collectors.toList()));
                    else if (args.length == 4)
                        addAll(Arrays.stream(GPermissions.Type.values()).map(GPermissions.Type::name).collect(Collectors.toList()));
                    else if (args.length == 5)
                        addAll(Arrays.stream(GPermissions.Definition.values()).map(GPermissions.Definition::name).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("zone")) {
                    if (args.length == 3)
                        addAll(GManager.getCurrentGame().getZones().stream().map(GZone::getId).collect(Collectors.toList()));
                    else if (args.length == 4)
                        addAll(Arrays.stream(GPermissions.Type.values()).map(GPermissions.Type::name).collect(Collectors.toList()));
                    else if (args.length == 5)
                        addAll(Arrays.stream(GPermissions.Definition.values()).map(GPermissions.Definition::name).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("player")) {
                    if (args.length == 3)
                        addAll(Utils.getAllPlayers());
                    else if (args.length == 4)
                        addAll(Arrays.stream(GPermissions.Type.values()).map(GPermissions.Type::name).collect(Collectors.toList()));
                    else if (args.length == 5)
                        addAll(Arrays.stream(GPermissions.Definition.values()).map(GPermissions.Definition::name).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("global") || args[1].equalsIgnoreCase("neutral")
                        || args[1].equalsIgnoreCase("friendly") || args[1].equalsIgnoreCase("hostile")
                        || args[1].equalsIgnoreCase("priority")) {
                    if (args.length == 3)
                        addAll(Arrays.stream(GPermissions.Type.values()).map(GPermissions.Type::name).collect(Collectors.toList()));
                    else if (args.length == 4)
                        addAll(Arrays.stream(GPermissions.Definition.values()).map(GPermissions.Definition::name).collect(Collectors.toList()));
                }
        }};
    }
}
