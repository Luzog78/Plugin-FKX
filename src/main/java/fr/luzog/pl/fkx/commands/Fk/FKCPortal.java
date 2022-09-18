package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.GUIs.GuiPortals;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.Portal;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FKCPortal {
    public static final String syntaxe = "/fk portal [help | (nether | end) [info | open | close]]"
            + "\n§cou /fk portal (nether | end) (cooldown <cooldown> | openingMat <material> | closingMat <material> | data <data>)"
            + "\n§cou /fk portal (nether | end) (over | dim) (spawn | pos1 | pos2) (get | reset | set <x> <y> <z> [<yw> <pi>] [<world>])";

    public static boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        GuiPortals.PortalType type;
        Portal p;

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            if (sender instanceof Player)
                u.getPlayer().openInventory(GuiPortals.getMainInventory("fk"));
            else
                u.err(CmdUtils.err_not_player);

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if ((type = args[1].equalsIgnoreCase("nether") ? GuiPortals.PortalType.NETHER
                : args[1].equalsIgnoreCase("end") ? GuiPortals.PortalType.END : null) != null
                && (p = type == GuiPortals.PortalType.NETHER ? FKManager.getCurrentGame().getNether()
                : type == GuiPortals.PortalType.END ? FKManager.getCurrentGame().getEnd() : null) != null) {

            if (args.length == 2)
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiPortals.getPortalInventory(
                            p, type, u.getPlayer().getLocation(), "fk portal"));
                else
                    u.err(CmdUtils.err_not_player);

            else if (args[2].equalsIgnoreCase("info")) {
                u.succ("Portail  §d" + p.getName() + "§r  :");
                u.succ(" - Status : " + (p.isOpened() ? "§2§l" + SpecialChars.YES + "§2  Ouvert"
                        : "§4§l" + SpecialChars.NO + "§4  Fermé"));
                u.succ(" - Cooldown : §7" + (p.getCoolDown() / 20.0) + "s");
                u.succ(" - Constructions : ");
                u.succ("   > Opening Material : §f" + p.getOpenedMat().name());
                u.succ("   > Closing Material : §f" + p.getClosedMat().name());
                u.succ("   > Data : §f" + p.getData());
                u.succ(" - Over : ");
                u.succ("   > Spawn : §f" + Utils.locToString(p.getOverSpawn(), true, true, true));
                u.succ("   > Pos1 : §7" + Utils.locToString(p.getOverPortal1(), false, false, true));
                u.succ("   > Pos2 : §7" + Utils.locToString(p.getOverPortal2(), false, false, true));
                u.succ(" - Dim : ");
                u.succ("   > Spawn : §f" + Utils.locToString(p.getDimSpawn(), true, true, true));
                u.succ("   > Pos1 : §7" + Utils.locToString(p.getDimPortal1(), false, false, true));
                u.succ("   > Pos2 : §7" + Utils.locToString(p.getDimPortal2(), false, false, true));
            } else if (args[2].equalsIgnoreCase("open")) {
                p.open();

                if (type == GuiPortals.PortalType.NETHER)
                    FKManager.getCurrentGame().saveNether();
                else if (type == GuiPortals.PortalType.END)
                    FKManager.getCurrentGame().saveEnd();

                u.succ("Portail §d" + p.getName() + "§r ouvert !");
            } else if (args[2].equalsIgnoreCase("close")) {
                p.close();

                if (type == GuiPortals.PortalType.NETHER)
                    FKManager.getCurrentGame().saveNether();
                else if (type == GuiPortals.PortalType.END)
                    FKManager.getCurrentGame().saveEnd();

                u.succ("Portail §d" + p.getName() + "§r fermé !");
            } else if (args[2].equalsIgnoreCase("cooldown")) {
                if (args.length == 3)
                    u.err("/fk portal " + p.getName() + " cooldown <cooldown>");
                else if (args.length == 4) {
                    try {
                        long cooldown = (long) (Double.parseDouble(args[3]) * 20);
                        boolean opened = p.isOpened();

                        if (opened)
                            p.close();

                        p.setCoolDown(cooldown);

                        if (type == GuiPortals.PortalType.NETHER)
                            FKManager.getCurrentGame().saveNether();
                        else if (type == GuiPortals.PortalType.END)
                            FKManager.getCurrentGame().saveEnd();

                        if (opened)
                            p.open();

                        u.succ("Portail §d" + p.getName() + "§r cooldown mis à §7" + (cooldown / 20.0) + "s§r !");
                    } catch (NumberFormatException e) {
                        u.err("/fk portal " + p.getName() + " cooldown <cooldown>");
                    }
                }
            } else if (args[2].equalsIgnoreCase("openingMat")) {
                if (args.length == 3)
                    u.err("/fk portal " + p.getName() + " openingMat <material>");
                else if (args.length == 4) {
                    try {
                        Material mat = Material.valueOf(args[3]);
                        boolean opened = p.isOpened();

                        if (opened)
                            p.close();

                        p.setOpenedMat(mat);

                        if (type == GuiPortals.PortalType.NETHER)
                            FKManager.getCurrentGame().saveNether();
                        else if (type == GuiPortals.PortalType.END)
                            FKManager.getCurrentGame().saveEnd();

                        if (opened)
                            p.open();

                        u.succ("Portail §d" + p.getName() + "§r matériel d'ouverture mis à §f" + mat.name() + "§r !");
                    } catch (IllegalArgumentException e) {
                        u.err("/fk portal " + p.getName() + " openingMat <material>");
                    }
                }
            } else if (args[2].equalsIgnoreCase("closingMat")) {
                if (args.length == 3)
                    u.err("/fk portal " + p.getName() + " closingMat <material>");
                else if (args.length == 4) {
                    try {
                        Material mat = Material.valueOf(args[3]);
                        boolean opened = p.isOpened();

                        if (opened)
                            p.close();

                        p.setClosedMat(mat);

                        if (type == GuiPortals.PortalType.NETHER)
                            FKManager.getCurrentGame().saveNether();
                        else if (type == GuiPortals.PortalType.END)
                            FKManager.getCurrentGame().saveEnd();

                        if (opened)
                            p.open();

                        u.succ("Portail §d" + p.getName() + "§r matériel de fermeture mis à §f" + mat.name() + "§r !");
                    } catch (IllegalArgumentException e) {
                        u.err("/fk portal " + p.getName() + " closingMat <material>");
                    }
                }
            } else if (args[2].equalsIgnoreCase("data")) {
                if (args.length == 3)
                    u.err("/fk portal " + p.getName() + " data <data>");
                else if (args.length == 4) {
                    try {
                        byte data = Byte.parseByte(args[3]);
                        boolean opened = p.isOpened();

                        if (opened)
                            p.close();

                        p.setData(data);

                        if (type == GuiPortals.PortalType.NETHER)
                            FKManager.getCurrentGame().saveNether();
                        else if (type == GuiPortals.PortalType.END)
                            FKManager.getCurrentGame().saveEnd();

                        if (opened)
                            p.open();

                        u.succ("Portail §d" + p.getName() + "§r data mis à §f" + data + "§r !");
                    } catch (NumberFormatException e) {
                        u.err("/fk portal " + p.getName() + " data <data>");
                    }
                }
            } else if (args.length >= 5) {
                int w, t; // world (dim/ovr), type (sp, p1, p2)

                if ((w = args[2].equalsIgnoreCase("over") ? 0
                        : args[2].equalsIgnoreCase("dim") ? 1 : -1) != -1)

                    if ((t = args[3].equalsIgnoreCase("spawn") ? 0
                            : args[3].equalsIgnoreCase("pos1") ? 1
                            : args[3].equalsIgnoreCase("pos2") ? 2 : -1) != -1)

                        if (args[4].equalsIgnoreCase("get"))
                            u.succ("Coordonnées " + (w == 0 ? "§2Over" : "§5Dim") + " §6"
                                    + (t == 0 ? "Spawn" : t == 1 ? "Pos1" : "Pos2") + "§r du portail §d"
                                    + p.getName() + "§r : §" + (t == 0 ? "f" : "7") + Utils.locToString(w == 0 ?
                                            t == 0 ? p.getOverSpawn() : t == 1 ? p.getOverPortal1() : p.getOverPortal2()
                                            : t == 0 ? p.getDimSpawn() : t == 1 ? p.getDimPortal1() : p.getDimPortal2(),
                                    t == 0, t == 0, true));

                        else if (args[4].equalsIgnoreCase("reset")) {
                            boolean opened = p.isOpened();

                            if (opened)
                                p.close();

                            if (w == 0)
                                if (t == 1)
                                    p.setOverPortal1(null);
                                else if (t == 2)
                                    p.setOverPortal2(null);
                                else
                                    p.setOverSpawn(new Location(Main.world, 0, 0, 0));
                            else if (t == 1)
                                p.setDimPortal1(null);
                            else if (t == 2)
                                p.setDimPortal2(null);
                            else
                                p.setDimSpawn(new Location(Main.world, 0, 0, 0));

                            if (type == GuiPortals.PortalType.NETHER)
                                FKManager.getCurrentGame().saveNether();
                            else if (type == GuiPortals.PortalType.END)
                                FKManager.getCurrentGame().saveEnd();

                            if (opened)
                                p.open();

                            u.succ("Coordonnées " + (w == 0 ? "§2Over" : "§5Dim") + " §6"
                                    + (t == 0 ? "Spawn" : t == 1 ? "Pos1" : "Pos2") + "§r du portail §d"
                                    + p.getName() + "§r : §" + (t == 0 ? "f" : "7") + Utils.locToString(w == 0 ?
                                            t == 0 ? p.getOverSpawn() : t == 1 ? p.getOverPortal1() : p.getOverPortal2()
                                            : t == 0 ? p.getDimSpawn() : t == 1 ? p.getDimPortal1() : p.getDimPortal2(),
                                    t == 0, t == 0, true));
                        } else if (args[4].equalsIgnoreCase("set")) {
                            Double x = null, y = null, z = null;
                            Float yw = null, pi = null;
                            World wo = null;
                            try {
                                x = Double.parseDouble(args[5]);
                                y = Double.parseDouble(args[6]);
                                z = Double.parseDouble(args[7]);
                                if (args.length > 8)
                                    if (args.length == 9) {
                                        if ((wo = Bukkit.getWorld(args[8])) == null) {
                                            u.err("Monde '" + args[8] + "' inconnu.");
                                            return false;
                                        }
                                    } else {
                                        yw = Float.parseFloat(args[8]);
                                        pi = Float.parseFloat(args[9]);
                                        if (args.length >= 11) {
                                            if ((wo = Bukkit.getWorld(args[10])) == null) {
                                                u.err("Monde '" + args[10] + "' inconnu.");
                                                return false;
                                            }
                                        }
                                    }

                                if (wo == null)
                                    wo = sender instanceof Player ? u.getPlayer().getLocation().getWorld() : Main.world;
                                Location loc = yw == null ? new Location(wo, x, y, z) : new Location(wo, x, y, z, yw, pi);


                                boolean opened = p.isOpened();

                                if (opened)
                                    p.close();

                                if (w == 0) {
                                    if (t == 0)
                                        p.setOverSpawn(loc);
                                    else if (t == 1)
                                        p.setOverPortal1(loc);
                                    else
                                        p.setOverPortal2(loc);
                                } else {
                                    if (t == 0)
                                        p.setDimSpawn(loc);
                                    else if (t == 1)
                                        p.setDimPortal1(loc);
                                    else
                                        p.setDimPortal2(loc);
                                }

                                if (type == GuiPortals.PortalType.NETHER)
                                    FKManager.getCurrentGame().saveNether();
                                else if (type == GuiPortals.PortalType.END)
                                    FKManager.getCurrentGame().saveEnd();

                                if (opened)
                                    p.open();

                                u.succ("Portail mis-à-jour !");
                            } catch (NumberFormatException e) {
                                u.err(CmdUtils.err_number_format + " ("
                                        + args[x == null ? 5 : y == null ? 6 : z == null ? 7
                                        : yw == null ? 8 : pi == null ? 9 : 10] + ")");
                            } catch (IndexOutOfBoundsException e) {
                                u.err(CmdUtils.err_missing_arg.replace("%ARG%",
                                        x == null ? "x" : y == null ? "y" : z == null ? "z"
                                                : yw == null ? "yaw" : pi == null ? "pitch" : "world"));
                            }
                        } else
                            u.err(CmdUtils.err_arg.replace("%ARG%", args[4]));
                    else
                        u.err(CmdUtils.err_arg.replace("%ARG%", args[3]));
                else
                    u.err(CmdUtils.err_arg.replace("%ARG%", args[2]));

            } else
                u.synt();

        } else
            u.err("Portail inconnu.");

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        return new ArrayList<String>() {{
            if (args.length == 2) {
                add("help");
                add("nether");
                add("end");
            } else if ((args[1].equalsIgnoreCase("nether") || args[1].equalsIgnoreCase("end"))) {
                if (args.length == 3) {
                    add("info");
                    add("open");
                    add("close");
                    add("cooldown");
                    add("openingMat");
                    add("closingMat");
                    add("data");
                    add("over");
                    add("dim");
                } else if (args[2].equalsIgnoreCase("cooldown")) {
                    if (args.length == 4) {
                        add("1");
                        add("3");
                        add("5");
                        add("8");
                        add("10");
                        add("15");
                    }
                } else if (args[2].equalsIgnoreCase("openingMat") || args[2].equalsIgnoreCase("closingMat")) {
                    if (args.length == 4) {
                        addAll(Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList()));
                    }
                } else if (args[2].equalsIgnoreCase("data")) {
                    if (args.length == 4) {
                        for (int i = 0; i < 16; i++)
                            add(i + "");
                    }
                } else if (args[2].equalsIgnoreCase("over") || args[2].equalsIgnoreCase("dim")) {
                    if (args.length == 4) {
                        add("spawn");
                        add("pos1");
                        add("pos2");
                    } else if (args[3].equalsIgnoreCase("spawn") || args[3].equalsIgnoreCase("pos1") || args[3].equalsIgnoreCase("pos2")) {
                        if (args.length == 5) {
                            add("get");
                            add("set");
                        } else if (args[4].equalsIgnoreCase("set") && sender instanceof Player) {
                            Location l = ((Player) sender).getLocation();
                            DecimalFormat df = new DecimalFormat("#.00");
                            if (args.length == 6)
                                add(df.format(l.getX()));
                            else if (args.length == 7)
                                add(df.format(l.getY()));
                            else if (args.length == 8)
                                add(df.format(l.getZ()));
                            else if (args.length == 9) {
                                add(df.format(l.getYaw()));
                                addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                            } else if (args.length == 10)
                                add(df.format(l.getPitch()));
                            else if (args.length == 11)
                                addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                        }
                    }
                }
            }
        }};
    }
}
