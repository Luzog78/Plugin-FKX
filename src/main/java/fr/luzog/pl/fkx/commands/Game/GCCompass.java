package fr.luzog.pl.fkx.commands.Game;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.*;
import fr.luzog.pl.fkx.guis.GuiCompass;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Portal;
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
import java.util.Objects;
import java.util.stream.Collectors;

public class GCCompass {
    public static final String syntaxe = "/" + Main.CMD + " compass [help | nothing | lobby | spawn | nether | end | team <id> | zone <id> | lock <id> | custom <x> <y> <z> | page <page>]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        GManager game = GManager.getCurrentGame();
        if (game == null) {
            u.err("Aucune partie en cours.");
            return true;
        }
        GPlayer fp = game.getPlayer(u.getPlayer().getName(), false);
        if (fp == null) {
            u.err("Vous n'êtes pas dans la partie.");
            return true;
        }
        if(!Main.globalConfig.isCompassActivated() && !sender.isOp()
                && !Objects.equals(fp.getTeamId(), GTeam.GODS_ID)
                && !Objects.equals(fp.getTeamId(), GTeam.SPECS_ID)) {
            u.err("La boussole est désactivée.");
            return true;
        }

        if (args.length == 0) {
            u.synt();
        } else if (args.length == 1) {
            Bukkit.dispatchCommand(sender, Main.CMD + " compass page 0");
        } else if (args[1].equals("?") || args[1].equalsIgnoreCase("help")) {
            u.synt();
        } else if (args[1].equalsIgnoreCase("page")) {
            if (args.length == 2)
                Bukkit.dispatchCommand(sender, Main.CMD + " compass page 0");
            else
                try {
                    if (sender instanceof Player)
                        u.getPlayer().openInventory(GuiCompass.getInventory(u.getPlayer().getLocation(),
                                null, Main.CMD + " compass page", Integer.parseInt(args[2])));
                    else
                        u.synt();
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format + " (" + args[2] + ")");
                }
        } else {
            if (!(sender instanceof Player)) {
                u.err(CmdUtils.err_not_player);
                return true;
            }
            Location loc = null;
            String name = null;
            double radius = 0;
            switch (args[1].toLowerCase()) {
                case "lobby":
                    loc = game.getLobby().getSpawn();
                    name = "Lobby";
                    radius = GZone.LOBBY_RADIUS;
                    break;
                case "spawn":
                    loc = game.getSpawn().getSpawn();
                    name = "Spawn";
                    radius = GZone.SPAWN_RADIUS;
                    break;
                case "nether":
                    loc = game.getNether().getDimSpawn();
                    name = game.getNether().getName();
                    radius = Portal.RADIUS;
                    break;
                case "end":
                    loc = game.getEnd().getDimSpawn();
                    name = game.getEnd().getName();
                    radius = Portal.RADIUS;
                    break;
                case "team":
                    if (args.length > 2)
                        if (game.getTeam(args[2]) != null) {
                            loc = game.getTeam(args[2]).getSpawn();
                            name = game.getTeam(args[2]).getColor() + game.getTeam(args[2]).getName();
                            radius = GTeam.TEAM_RADIUS;
                        } else
                            u.err(CmdUtils.err_team_not_found + " (" + args[2] + ")");
                    break;
                case "zone":
                    if (args.length > 2)
                        if (game.getZone(args[2]) != null) {
                            loc = game.getZone(args[2]).getSpawn();
                            name = game.getZone(args[2]).getId();
                            radius = GZone.ZONE_RADIUS;
                        } else
                            u.err("Zone introuvable (" + args[2] + ")");
                    break;
                case "lock":
                    if (args.length > 2)
                        if (game.getPickableLocks().getLock(args[2]) != null) {
                            loc = game.getPickableLocks().getLock(args[2]).getLocation();
                            name = "Coffre " + game.getPickableLocks().getLock(args[2]).getId();
                            radius = GPickableLocks.RADIUS;
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
            fp.setCompass(new GPlayer.Compass(name, radius, loc), true);

            if(args[1].equalsIgnoreCase("nothing"))
                ((CraftPlayer) u.getPlayer()).getHandle().playerConnection.sendPacket(
                        new PacketPlayOutChat(new ChatComponentText("§7---"), (byte) 2));
        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return new ArrayList<String>() {{
            CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);
            GManager game = u.getPlayer() == null || GManager.getGlobalPlayer(u.getPlayer().getName()).isEmpty() ?
                    GManager.getCurrentGame() : GManager.getGlobalPlayer(u.getPlayer().getName()).get(0).getManager();
            if (game != null)
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
                        addAll(game.getTeams().stream().map(GTeam::getId).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("zone")) {
                    if (args.length == 3)
                        addAll(game.getZones().stream().map(GZone::getId).collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("lock")) {
                    if (args.length == 3)
                        addAll(game.getPickableLocks().getPickableLocks().stream()
                                .map(GPickableLocks.Lock::getId).collect(Collectors.toList()));
                }
        }};
    }
}
