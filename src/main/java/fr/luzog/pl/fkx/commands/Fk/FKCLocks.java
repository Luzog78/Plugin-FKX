package fr.luzog.pl.fkx.commands.Fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPickableLocks;
import fr.luzog.pl.fkx.utils.CmdUtils;
import fr.luzog.pl.fkx.utils.SpecialChars;
import fr.luzog.pl.fkx.utils.Utils;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FKCLocks {
    public static final String syntaxe = "/fk event [key <rarity>[:<id>] | create <rarity> [<id>] <x> <y> <z> [<world>] | <id> [<args...>]]";
    public static final String synt_lock = "/fk event <id> [info | destroy | getKey | lock | unlock | pickable (true | false) | armorStands (hide | show) | cooldown <cooldown> | rarity <rarity>]";
    public static final String rarity = "Rarity : " + String.join(" / ", Arrays.stream(FKPickableLocks.Rarity.values())
            .map(Enum::name).toArray(String[]::new));

    public static boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, command, msg, args, syntaxe + "\n§r" + rarity);

        if (args.length == 0)
            return false;

        else if (args.length == 1)
            u.succ("TODO -> Event GUIs");

        else if (args[1].equalsIgnoreCase("help") || args[1].equals("?"))
            u.synt();

        else if (args[1].equalsIgnoreCase("key")) {
            if (args.length == 2)
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "<rarity>[:<id>]"));
            else
                try {
                    if (sender instanceof Player)
                        if (u.getPlayer().getInventory().firstEmpty() != -1)
                            u.getPlayer().getInventory().addItem(args[2].contains(":") ?
                                    FKPickableLocks.getLockItem(
                                            FKPickableLocks.Rarity.valueOf(args[2].split(":", 2)[0]
                                                    .toUpperCase()), args[2].split(":", 2)[1])
                                    : args[2].equalsIgnoreCase("admin") ? FKPickableLocks.getMasterKey()
                                    : FKPickableLocks.getLockItem(
                                    FKPickableLocks.Rarity.valueOf(args[2].toUpperCase())));
                        else
                            u.err("Vous n'avez pas de place dans votre inventaire.");
                    else
                        u.err(CmdUtils.err_not_player);
                } catch (IllegalArgumentException e) {
                    u.err("Rareté invalide. (" + (args[2].contains(":") ? args[2].split(":", 2)[0] : args[2]) + ")");
                }
        } else if (args[1].equalsIgnoreCase("create")) {
            if (args.length >= 3)
                try {
                    FKPickableLocks.Rarity rarity = FKPickableLocks.Rarity.valueOf(args[2].toUpperCase());
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
                            if (te != null) {
                                FKManager.getCurrentGame().getPickableLocks().addLock(
                                        new FKPickableLocks.Lock(id, rarity, true, 0L, loc));
                                u.succ("Coffre crochetable §b" + id + "§r créé avec les paramètres par défaut !");
                                FKManager.getCurrentGame().savePickableLocks();
                                FKManager.getCurrentGame().getPickableLocks().updateAll();
                            } else
                                u.err("Aucune TileEntity n'est présente à cet endroit.");
                        }
                    } else
                        u.synt();
                } catch (IllegalArgumentException e) {
                    u.err("Rareté invalide. (" + args[2] + ")");
                }
            else
                u.err(CmdUtils.err_missing_arg.replace("%ARG%", "rarity"));
        } else {
            u.setSyntaxe(synt_lock + "\n§r" + rarity);
            FKPickableLocks.Lock l = FKManager.getCurrentGame().getPickableLocks().getLock(args[1]);
            if (l == null) {
                u.err("Ce coffre crochetable n'existe pas. (" + args[1] + ")");
                return true;
            }
            if (args.length == 2)
                u.succ("TODO -> Event GUI");
            else if (args[2].equalsIgnoreCase("help") || args[2].equals("?"))
                u.synt();
            else if (args[2].equalsIgnoreCase("info")) {
                u.succ("Coffre crochetable :");
                u.succ(" - Id : §b" + l.getId());
                u.succ(" - Rareté : §f" + l.getRarity().getFormattedName(false));
                u.succ(" - Accessible :  §f" + (l.isPickable() ? "§2§l" + SpecialChars.YES + "  Oui" : "§4§l" + SpecialChars.NO + "  Non"));
                u.succ(" - Crocheté :  §f" + (l.isPicked() ? "§2" + SpecialChars.YES + "  Oui" : "§4" + SpecialChars.NO + "  Non"));
                u.succ(" - Temps pour crocheter : §f" + l.getOriginalCoolDown() + " ticks");
                u.succ(" - Temps restant à crocheter : §f" + l.getCoolDown() + " ticks");
                u.succ(" - Nom du pilleur : §6" + (l.getPicker() == null ? "§cnull" : l.getPicker()));
                u.succ(" - Location : §f" + Utils.locToString(l.getLocation(), false, false, true));
            } else if (args[2].equalsIgnoreCase("destroy")) {
                l.destroy(FKManager.getCurrentGame());
                u.succ("Vous avez détruit ce coffre.");
            } else if (args[2].equalsIgnoreCase("getKey")) {
                if (sender instanceof Player)
                    if (u.getPlayer().getInventory().firstEmpty() != -1)
                        u.getPlayer().getInventory().addItem(l.getKey());
                    else
                        u.err("Votre inventaire est plein.");
                else
                    u.err(CmdUtils.err_not_player);
            } else if (args[2].equalsIgnoreCase("lock")) {
                l.lock();
                u.succ("Vous avez verrouillé ce coffre §b" + l.getId() + "§r.");
                FKManager.getCurrentGame().savePickableLocks();
            } else if (args[2].equalsIgnoreCase("unlock")) {
                l.unlock();
                u.succ("Vous avez déverrouillé le coffre §b" + l.getId() + "§r.");
                FKManager.getCurrentGame().savePickableLocks();
            } else if (args[2].equalsIgnoreCase("pickable")) {
                if(args.length >= 4)
                    if(args[3].equalsIgnoreCase("true")) {
                        l.setPickable(true);
                        u.succ("Le coffre §b" + l.getId() + "§r est désormais crochetable.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else if(args[3].equalsIgnoreCase("false")) {
                        l.setPickable(false);
                        u.succ("Le coffre §b" + l.getId() + "§r est désormais inaccessible.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else
                        u.err("Argument invalide. (" + args[3] + ")");
                else
                    u.err("Vous devez préciser si le coffre est accessible ou non.");
            } else if (args[2].equalsIgnoreCase("armorStands")) {
                if(args.length >= 4)
                    if(args[3].equalsIgnoreCase("hide")) {
                        l.hideArmorStand();
                        u.succ("Les armor stands du coffre §b" + l.getId() + "§r sont désormais cachés.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else if(args[3].equalsIgnoreCase("show")) {
                        l.updateArmorStand();
                        u.succ("Les armor stands du coffre §b" + l.getId() + "§r sont désormais visibles.");
                        FKManager.getCurrentGame().savePickableLocks();
                    } else
                        u.err("Argument invalide. (" + args[3] + ")");
                else
                    u.err("Vous devez préciser si les armor stands doivent être visibles ou non.");
            } else if (args[2].equalsIgnoreCase("cooldown")) {
                if(args.length >= 4)
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
            } else if (args[2].equalsIgnoreCase("rarity")) {
                if(args.length >= 4)
                    try {
                        l.setRarity(FKPickableLocks.Rarity.valueOf(args[3].toUpperCase()));
                        u.succ("Le coffre §b" + l.getId() + "§r est désormais de rareté §f"
                                + l.getRarity().getFormattedName(false) + "§r !");
                        FKManager.getCurrentGame().savePickableLocks();
                    } catch (IllegalArgumentException e) {
                        u.err("Rareté invalide. (" + args[3] + ")");
                    }
                else
                    u.err("Vous devez préciser la rareté du coffre.");
            } else
                u.synt();
        }

        return false;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        return null;
    }
}
