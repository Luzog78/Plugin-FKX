package fr.luzog.pl.fkx.commands.Fun;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Rename implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/rename (reset | name <name...> | lore <line>;...)";
    public static final String complete_syntaxe = "Syntaxe: /rename reset ou /rename name <name...> ou /rename lore <lore...>\n"
            + " > Avec un objet dans la main.\n"
            + " > Avec ';' pour séparer les lignes.\n"
            + " > Avec '&' pour la couleur (&bTEST >> §bTEST§r).";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length >= 1 && (args[0].equals("?") || args[0].equalsIgnoreCase("help")))
            u.err(complete_syntaxe);
        else if (sender instanceof Player)
            try {
                if (args.length >= 1 && args[0].equalsIgnoreCase("reset")) {
                    ItemStack is = u.getPlayer().getItemInHand();
                    ItemMeta meta = is.getItemMeta();
                    meta.setDisplayName(null);
                    meta.setLore(null);
                    is.setItemMeta(meta);
                    u.getPlayer().setItemInHand(is);
                } else if (args.length >= 2)
                    if (args[0].equalsIgnoreCase("name")) {
                        ItemStack is = u.getPlayer().getItemInHand();
                        ItemMeta meta = is.getItemMeta();
                        meta.setDisplayName(String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                                .replace(";", "\n").replace("&", "§")
                                .replace("§§", "&").replace("§r", "§f§o"));
                        is.setItemMeta(meta);
                        u.getPlayer().setItemInHand(is);
                    } else if (args[0].equalsIgnoreCase("lore")) {
                        ItemStack is = u.getPlayer().getItemInHand();
                        ItemMeta meta = is.getItemMeta();
                        List<String> lore = Arrays.stream(String.join(" ", Arrays.copyOfRange(args, 1, args.length)).split(";"))
                                .collect(Collectors.toList());
                        lore.replaceAll(s -> s.replace("&", "§").replace("§§", "&").replace("§r", "§5§o"));
                        meta.setLore(lore);
                        is.setItemMeta(meta);
                        u.getPlayer().setItemInHand(is);
                    } else
                        u.synt();
                else
                    u.synt();
            } catch (Exception e) {
                u.err("Aucun item dans la main (ou item incorrecte).");
            }
        else
            u.err(CmdUtils.err_not_player);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            if (args.length == 1) {
                add("name");
                add("lore");
            }
            addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}