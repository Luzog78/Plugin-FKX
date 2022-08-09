package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPickableLocks;
import fr.luzog.pl.fkx.fk.GUIs.GuiDate;
import fr.luzog.pl.fkx.fk.GUIs.GuiLocks;
import fr.luzog.pl.fkx.utils.Broadcast;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FKCLocks {
    public static final String syntaxe = "/fk locks [help | list | tool | page <page>]"
            + "\n§rou /fk locks [create <level> [<id>] <x> <y> <z> [<world>] | <id> [<args...>]]";
    public static final String synt_lock = "/fk locks <id> [help | info | destroy | lock | unlock | broadcast]"
            + "\n§rou /fk locks <id> [cooldown <cooldown> | level <level> | id <newId>]"
            + "\n§rou /fk locks <id> [pickable (true | false) | auto-bc (true | false) | armorStands (hide | show)]";

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe);

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            Bukkit.dispatchCommand(sender, "fk locks page 0");

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("page"))
            if (args.length == 2)
                Bukkit.dispatchCommand(sender, "fk locks page 0");
            else
                try {
                    if (sender instanceof Player) {
                        Block b = u.getPlayer().getTargetBlock(new HashSet<Material>() {{
                            add(Material.AIR);
                            add(Material.WATER);
                            add(Material.STATIONARY_WATER);
                            add(Material.LAVA);
                            add(Material.STATIONARY_LAVA);
                        }}, 16);
                        u.getPlayer().openInventory(GuiLocks.getMainInventory(u.getPlayer().getLocation(),
                                b == null ? null : b.getLocation(), "fk",
                                "fk locks page", Integer.parseInt(args[2])));
                    } else
                        u.err(CmdUtils.err_not_player);
                } catch (NumberFormatException e) {
                    u.err(CmdUtils.err_number_format);
                }

        else if (args[1].equalsIgnoreCase("list")) {
            u.succ("Coffres crochetables (§f" + FKManager.getCurrentGame().getPickableLocks().getPickableLocks().size() + "§r) :");
            ArrayList<FKPickableLocks.Lock> pickable = FKManager.getCurrentGame().getPickableLocks().getPickableLocks()
                    .stream().filter(FKPickableLocks.Lock::isPickable)
                    .collect(Collectors.toCollection(ArrayList::new)),
                    nonPickable = FKManager.getCurrentGame().getPickableLocks().getPickableLocks()
                            .stream().filter(l -> !l.isPickable())
                            .collect(Collectors.toCollection(ArrayList::new));
            new ArrayList<FKPickableLocks.Lock>() {{
                addAll(pickable.stream().sorted((a, b) ->
                                a.isPicked() == b.isPicked() ? 0 : a.isPicked() ? 1 : -1)
                        .collect(Collectors.toList()));
                addAll(nonPickable.stream().sorted((a, b) ->
                                a.isPicked() == b.isPicked() ? 0 : a.isPicked() ? 1 : -1)
                        .collect(Collectors.toList()));
            }}.forEach(l ->
                    u.succ("  >  " + (l.isPickable() ? "§b" : "§c") + l.getId() + "§r - §f" + l.getLevel() + "§r :  "
                            + (l.isPicked() ? "§2✔ Crocheté" : "§4✖ À Crocheter")));
        } else if (args[1].equalsIgnoreCase("tool")) {
            if (sender instanceof Player)
                ((Player) sender).getInventory().addItem(FKPickableLocks.getMasterKey());
            else
                u.err(CmdUtils.err_not_player);
        } else if (args[1].equalsIgnoreCase("create")) {
            if (args.length >= 3)
                try {
                    int level = Integer.parseInt(args[2].toUpperCase());
                    if (args.length >= 6) {
                        Location loc = null;
                        String id = null;
                        if (args.length > 6)
                            try {
                                loc = new Location(sender instanceof Player ? u.getPlayer().getWorld() : Main.world,
                                        Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
                                if (args.length > 7)
                                    if (Bukkit.getWorld(args[7]) != null)
                                        loc.setWorld(Bukkit.getWorld(args[7]));
                                    else
                                        u.err("Monde inconnu. (" + args[7] + ")\n§rCelui par défaut est donc utilisé.");
                                if (FKManager.getCurrentGame().getPickableLocks().getLock(args[3]) != null)
                                    u.err("Cet identifiant est déjà utilisé.");
                                else
                                    id = args[3];
                            } catch (NumberFormatException e) {
                                try {
                                    loc = new Location(Bukkit.getWorld(args[6]),
                                            Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
                                    Bukkit.getWorld(args[6]).getName();
                                    do
                                        id = UUID.randomUUID().toString().substring(0, 6);
                                    while (FKManager.getCurrentGame().getPickableLocks().getLock(id) != null);
                                } catch (NumberFormatException e1) {
                                    u.err("Coordonnées invalides.");
                                } catch (NullPointerException e1) {
                                    u.err("Monde inconnu. (" + args[6] + ")");
                                }
                            }
                        else
                            try {
                                loc = new Location(sender instanceof Player ? u.getPlayer().getWorld() : Main.world,
                                        Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
                                do
                                    id = UUID.randomUUID().toString().substring(0, 6);
                                while (FKManager.getCurrentGame().getPickableLocks().getLock(id) != null);
                            } catch (NumberFormatException e1) {
                                u.err("Coordonnées invalides.");
                            }
                        if (loc != null && id != null) {
                            TileEntity te = ((CraftWorld) loc.getWorld()).getTileEntityAt(loc.getBlockX(),
                                    loc.getBlockY(), loc.getBlockZ());
                            if (te != null)
                                if (!FKManager.getCurrentGame().getPickableLocks().isPickableLock(loc)) {
                                    FKManager.getCurrentGame().getPickableLocks().addLock(
                                            new FKPickableLocks.Lock(id, level, true, 300L, loc));
                                    u.succ("Coffre crochetable §b" + id + "§r de niveau §f" + level
                                            + "§r créé avec les paramètres par défaut !");
                                    FKManager.getCurrentGame().savePickableLocks();
                                    FKManager.getCurrentGame().getPickableLocks().updateAll();
                                } else
                                    u.err("Cette endroit est déjà utilisée pour un coffre crochetable.");
                            else
                                u.err("Aucune TileEntity n'est présente à cet endroit.");
                        }
                    } else
                        u.synt();
                } catch (NumberFormatException e) {
                    u.err("Niveau invalide. (" + args[2] + ")");
                }
            else
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "level"));
        } else {
            u.setSyntaxe(synt_lock);
            if (args.length >= 3 && (args[2].equalsIgnoreCase("help") || args[2].equals("?"))) {
                u.synt();
                return false;
            }

            FKPickableLocks.Lock l = FKManager.getCurrentGame().getPickableLocks().getLock(args[1]);
            if (l == null) {
                u.err("Ce coffre crochetable n'existe pas. (" + args[1] + ")");
                return true;
            }

            if (args.length == 2)
                if (sender instanceof Player)
                    u.getPlayer().openInventory(GuiLocks.getLockInventory(l, u.getPlayer().getLocation(), "fk locks"));
                else
                    u.err(CmdUtils.err_not_player);
            else if (args[2].equalsIgnoreCase("info")) {
                u.succ("Coffre crochetable :");
                u.succ(" - Id : §b" + l.getId());
                u.succ(" - Niveau : §f" + l.getLevel());
                u.succ(" - Accessible :  §f" + (l.isPickable() ? "§2§l" + SpecialChars.YES + "  Oui" : "§4§l" + SpecialChars.NO + "  Non"));
                u.succ(" - Crocheté :  §f" + (l.isPicked() ? "§2" + SpecialChars.YES + "  Oui" : "§4" + SpecialChars.NO + "  Non"));
                u.succ(" - Temps pour crocheter : §f" + l.getOriginalCoolDown() + " ticks");
                u.succ(" - Temps restant à crocheter : §f" + l.getCoolDown() + " ticks");
                u.succ(" - Nom du pilleur : §6" + (l.getPicker() == null ? "§cnull" : l.getPicker()));
                u.succ(" - Location : §f" + Utils.locToString(l.getLocation(), false, false, true));
            } else if (args[2].equalsIgnoreCase("destroy")) {
                l.destroy(FKManager.getCurrentGame());
                u.succ("Vous avez détruit ce coffre.");
            } else if (args[2].equalsIgnoreCase("lock")) {
                l.lock();
                u.succ("Vous avez verrouillé ce coffre §b" + l.getId() + "§r.");
                FKManager.getCurrentGame().savePickableLocks();
            } else if (args[2].equalsIgnoreCase("unlock")) {
                l.unlock();
                u.succ("Vous avez déverrouillé le coffre §b" + l.getId() + "§r.");
                FKManager.getCurrentGame().savePickableLocks();
            } else if (args[2].equalsIgnoreCase("broadcast")) {
                l.broadcast();
            } else if (args[2].equalsIgnoreCase("pickable")) {
                if (args.length >= 4)
                    if (args[3].equalsIgnoreCase("true")) {
                        l.setPickable(true);
                        u.succ("Le coffre §b" + l.getId() + "§r est désormais crochetable.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else if (args[3].equalsIgnoreCase("false")) {
                        l.setPickable(false);
                        u.succ("Le coffre §b" + l.getId() + "§r est désormais inaccessible.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else
                        u.err("Argument invalide. (" + args[3] + ")");
                else
                    u.err("Vous devez préciser si le coffre est accessible ou non.");
            } else if (args[2].equalsIgnoreCase("auto-bc")) {
                if (args.length >= 4)
                    if (args[3].equalsIgnoreCase("true")) {
                        l.setAutoBC(true);
                        u.succ("Le coffre §b" + l.getId() + "§r déclenchera un broadcast dès lors que le jour de sont crochetage est atteint.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else if (args[3].equalsIgnoreCase("false")) {
                        l.setAutoBC(false);
                        u.succ("Le coffre §b" + l.getId() + "§r ne déclenchera aucun broadcast.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else
                        u.err("Argument invalide. (" + args[3] + ")");
                else
                    u.err("Vous devez préciser une valeur booléenne.");
            } else if (args[2].equalsIgnoreCase("armorStands")) {
                if (args.length >= 4)
                    if (args[3].equalsIgnoreCase("hide")) {
                        l.hideArmorStand();
                        u.succ("Les armor stands du coffre §b" + l.getId() + "§r sont désormais cachés.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else if (args[3].equalsIgnoreCase("show")) {
                        l.updateArmorStand();
                        u.succ("Les armor stands du coffre §b" + l.getId() + "§r sont désormais visibles.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else
                        u.err("Argument invalide. (" + args[3] + ")");
                else
                    u.err("Vous devez préciser si les armor stands doivent être visibles ou non.");
            } else if (args[2].equalsIgnoreCase("cooldown")) {
                if (args.length >= 4)
                    try {
                        l.setOriginalCoolDown(Long.parseLong(args[3]));
                        l.resetCoolDown();
                        u.succ("Le cooldown du coffre §b" + l.getId() + "§r est désormais de §f"
                                + l.getOriginalCoolDown() + " ticks§r.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } catch (NumberFormatException e) {
                        u.err(CmdUtils.err_number_format + " (" + args[3] + ")");
                    }
                else
                    u.err("Vous devez préciser si les armor stands doivent être visibles ou non.");
            } else if (args[2].equalsIgnoreCase("level")) {
                if (args.length >= 4)
                    try {
                        l.setLevel(Integer.parseInt(args[3]));
                        u.succ("Le coffre §b" + l.getId() + "§r est désormais de niveau §f"
                                + l.getLevel() + "§r !");
                        FKManager.getCurrentGame().savePickableLocks();
                    } catch (NumberFormatException e) {
                        u.err("Niveau invalide. (" + args[3] + ")");
                    }
                else
                    u.err("Vous devez préciser le niveau du coffre.");
            } else if (args[2].equalsIgnoreCase("id")) {
                if (args.length >= 4)
                    if (FKManager.getCurrentGame().getPickableLocks().getLock(args[3]) == null)
                        try {
                            String old = l.getId();
                            l.setId(args[3]);
                            u.succ("Le coffre §c" + old + "§r est désormais d'identifiant : §b" + l.getId() + "§r !");
                            FKManager.getCurrentGame().savePickableLocks();
                        } catch (NumberFormatException e) {
                            u.err("Niveau invalide. (" + args[3] + ")");
                        }
                    else
                        u.err("Ce coffre existe déjà.");
                else
                    u.err("Vous devez préciser le nouvel identifiant.");
            } else
                u.synt();
        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        DecimalFormat df = new DecimalFormat("0.00");
        ArrayList<Material> containers = new ArrayList<>(Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST,
                Material.ENDER_CHEST, Material.HOPPER, Material.DROPPER, Material.DISPENSER, Material.FURNACE,
                Material.BURNING_FURNACE, Material.BREWING_STAND, Material.ANVIL, Material.BEACON, Material.JUKEBOX));
        return new ArrayList<String>() {{
            FKManager m = FKManager.getCurrentGame();
            FKPickableLocks.Lock l = null;
            if (m != null)
                if (args.length == 2) {
                    Block bb = sender instanceof Player ? ((Player) sender).getTargetBlock(
                            new HashSet<Material>() {{
                                add(Material.AIR);
                                add(Material.WATER);
                                add(Material.STATIONARY_WATER);
                                add(Material.LAVA);
                                add(Material.STATIONARY_LAVA);
                            }}, 16) : null;
                    addAll(m.getPickableLocks().getPickableLocks().stream()
                            .filter(lock -> bb != null && containers.contains(bb.getType())
                                    && bb.getLocation().getBlockX() == lock.getLocation().getBlockX()
                                    && bb.getLocation().getBlockY() == lock.getLocation().getBlockY()
                                    && bb.getLocation().getBlockZ() == lock.getLocation().getBlockZ())
                            .map(FKPickableLocks.Lock::getId)
                            .collect(Collectors.toList()));
                    add("help");
                    add("list");
                    add("tool");
                    add("create");
                    add("page");
                    addAll(m.getPickableLocks().getPickableLocks().stream()
                            .filter(lock -> !(bb != null && containers.contains(bb.getType())
                                    && bb.getLocation().getBlockX() == lock.getLocation().getBlockX()
                                    && bb.getLocation().getBlockY() == lock.getLocation().getBlockY()
                                    && bb.getLocation().getBlockZ() == lock.getLocation().getBlockZ()))
                            .map(FKPickableLocks.Lock::getId)
                            .collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("create")) {
                    if (args.length == 3) {
                        add("0");
                        add((m.getDay() + 1) + "");
                        add((m.getDay() + 2) + "");
                        add((m.getDay() + 3) + "");
                        add((m.getDay() + 4) + "");
                        add((m.getDay() + 5) + "");
                    } else if (args.length == 4) {
                        for (int i = 0; i < 6; i++)
                            add(UUID.randomUUID().toString().substring(0, 6));
                    } else if (args.length == 5) {
                        if (sender instanceof Player) {
                            Block b = ((Player) sender).getTargetBlock(
                                    new HashSet<>(Collections.singletonList(Material.AIR)), 200);
                            if (b != null && containers.contains(b.getType()))
                                add(b.getLocation().getBlockX() + "");
                            else
                                add(df.format(((Player) sender).getLocation().getBlockX()));
                        } else {
                            add("0");
                        }
                    } else if (args.length == 6) {
                        if (sender instanceof Player) {
                            Block b = ((Player) sender).getTargetBlock(
                                    new HashSet<>(Collections.singletonList(Material.AIR)), 200);
                            if (b != null && containers.contains(b.getType()))
                                add(b.getLocation().getBlockY() + "");
                            else
                                add(df.format(((Player) sender).getLocation().getBlockY()));
                        } else {
                            add("0");
                        }
                    } else if (args.length == 7) {
                        if (sender instanceof Player) {
                            Block b = ((Player) sender).getTargetBlock(
                                    new HashSet<>(Collections.singletonList(Material.AIR)), 200);
                            if (b != null && containers.contains(b.getType()))
                                add(b.getLocation().getBlockZ() + "");
                            else
                                add(df.format(((Player) sender).getLocation().getBlockZ()));
                        } else {
                            add("0");
                        }
                    } else if (args.length == 8) {
                        addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                    }
                } else if ((l = m.getPickableLocks().getLock(args[1])) != null) {
                    if (args.length == 3) {
                        add("help");
                        add("info");
                        add("destroy");
                        add("lock");
                        add("unlock");
                        add("broadcast");
                        add("cooldown");
                        add("level");
                        add("pickable");
                        add("auto-bc");
                        add("armorStands");
                    } else if (args.length == 4) {
                        if (args[2].equalsIgnoreCase("cooldown")) {
                            add("100");
                            add("200");
                            add("400");
                            add("600");
                            add("1200");
                            add("2400");
                            add("3600");
                            add("6000");
                        } else if (args[2].equalsIgnoreCase("level")) {
                            add("0");
                            add((m.getDay() + 1) + "");
                            add((m.getDay() + 2) + "");
                            add((m.getDay() + 3) + "");
                            add((m.getDay() + 4) + "");
                            add((m.getDay() + 5) + "");
                        } else if (args[2].equalsIgnoreCase("pickable") || args[2].equalsIgnoreCase("auto-bc")) {
                            add("true");
                            add("false");
                        } else if (args[2].equalsIgnoreCase("armorStands")) {
                            add("hide");
                            add("show");
                        }
                    }
                }
        }};
    }
}
