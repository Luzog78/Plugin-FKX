package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.fk.*;
import fr.luzog.pl.fkx.fk.GUIs.GuiCompass;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.utils.Utils;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FKCCompass {
    public static final String syntaxe = "/fk compass [help | nothing | lobby | spawn | nether | end | team <id> | zone <id> | lock <id> | custom <x> <y> <z> | page <page>]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0) {
            u.synt();
        } else if (args.length == 1) {
            Bukkit.dispatchCommand(sender, "fk compass page 0");
        } else if (args[1].equals("?") || args[1].equalsIgnoreCase("help")) {
            u.synt();
        } else if (args[1].equalsIgnoreCase("page")) {
            if (args.length == 2)
                Bukkit.dispatchCommand(sender, "fk compass page 0");
            else
                try {
                    if (sender instanceof Player)
                        u.getPlayer().openInventory(GuiCompass.getInventory(u.getPlayer().getLocation(),
                                null, "fk compass page", Integer.parseInt(args[2])));
                    else
                        u.synt();
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format + " (" + args[2] + ")");
                }
        } else {
            FKManager fk = FKManager.getCurrentGame();
            if (fk == null) {
                u.err("Aucune partie en cours.");
                return true;
            }
            if (!(sender instanceof Player)) {
                u.err(CmdUtils.err_not_player);
                return true;
            }
            FKPlayer fp = fk.getPlayer(u.getPlayer().getName(), false);
            if (fp == null) {
                u.err("Vous n'êtes pas dans la partie.");
                return true;
            }
            Location loc = null;
            String name = null;
            double radius = 0;
            switch (args[1].toLowerCase()) {
                case "lobby":
                    loc = fk.getLobby().getSpawn();
                    name = "Lobby";
                    radius = FKZone.LOBBY_RADIUS;
                    break;
                case "spawn":
                    loc = fk.getSpawn().getSpawn();
                    name = "Spawn";
                    radius = FKZone.SPAWN_RADIUS;
                    break;
                case "nether":
                    loc = fk.getNether().getDimSpawn();
                    name = fk.getNether().getName();
                    radius = Portal.RADIUS;
                    break;
                case "end":
                    loc = fk.getEnd().getDimSpawn();
                    name = fk.getEnd().getName();
                    radius = Portal.RADIUS;
                    break;
                case "team":
                    if (args.length > 2)
                        if (fk.getTeam(args[2]) != null) {
                            loc = fk.getTeam(args[2]).getSpawn();
                            name = fk.getTeam(args[2]).getColor() + fk.getTeam(args[2]).getName();
                            radius = FKTeam.TEAM_RADIUS;
                        } else
                            u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
                    break;
                case "zone":
                    if (args.length > 2)
                        if (fk.getZone(args[2]) != null) {
                            loc = fk.getZone(args[2]).getSpawn();
                            name = fk.getZone(args[2]).getId();
                            radius = FKZone.ZONE_RADIUS;
                        } else
                            u.err("Zone introuvable (" + args[2] + ")");
                    break;
                case "lock":
                    if (args.length > 2)
                        if (fk.getPickableLocks().getLock(args[2]) != null) {
                            loc = fk.getPickableLocks().getLock(args[2]).getLocation();
                            name = "Coffre " + fk.getPickableLocks().getLock(args[2]).getId();
                            radius = FKPickableLocks.RADIUS;
                        } else
                            u.err("Coffre introuvable (" + args[2] + ")");
                    break;
                case "custom":
                    if (args.length > 4)
                        try {
                            double x = Double.parseDouble(args[2]), y = Double.parseDouble(args[3]), z = Double.parseDouble(args[4]);
                            loc = new Location(u.getPlayer().getWorld(), x, y, z);
                            name = String.format("Custom (%.2f %.2f %.2f)", x, y, z);
                            radius = 0.1;
                        } catch (NumberFormatException e) {
                            u.err(CmdUtils.err_number_format + " (" + args[2] + ", " + args[3] + ", " + args[4] + ")");
                        }
                    else
                        u.err("Vous devez spécifier un <x>, un <y> et un <z>.");
                    break;
            }
            if (loc == null && !args[1].equalsIgnoreCase("nothing")) {
                u.err("Warp inconnu.");
                return false;
            }
            fp.setCompass(new FKPlayer.Compass(name, radius, loc), true);

            if(args[1].equalsIgnoreCase("nothing"))
                ((CraftPlayer) u.getPlayer()).getHandle().playerConnection.sendPacket(
                        new PacketPlayOutChat(new ChatComponentText("§7---"), (byte) 2));
        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<String>() {{
            CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);
            FKManager fk = u.getPlayer() == null || FKManager.getGlobalPlayer(u.getPlayer().getName()).isEmpty() ?
                    FKManager.getCurrentGame() : FKManager.getGlobalPlayer(u.getPlayer().getName()).get(0).getManager();
            if (fk != null)
                if (args.length == 2) {
                    add("help");
                    add("nothing");
                    add("lobby");
                    add("spawn");
                    add("nether");
                    add("end");
                    add("team");
                    add("zone");
                    add("lock");
                    add("page");
                } else if (args[1].equalsIgnoreCase("lobby") || args[1].equalsIgnoreCase("spawn")
                        || args[1].equalsIgnoreCase("nether") || args[1].equalsIgnoreCase("end")) {
                    addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("team")) {
                    if (args.length == 3)
                        addAll(fk.getTeams().stream().map(FKTeam::getId).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("zone")) {
                    if (args.length == 3)
                        addAll(fk.getZones().stream().map(FKZone::getId).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("lock")) {
                    if (args.length == 3)
                        addAll(fk.getPickableLocks().getPickableLocks().stream()
                                .map(FKPickableLocks.Lock::getId).collect(Collectors.toList()));
                }
        }};
    }
}
