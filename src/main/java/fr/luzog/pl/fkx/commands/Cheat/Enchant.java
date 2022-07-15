package fr.luzog.pl.fkx.commands.Cheat;

import fr.luzog.pl.fkx.utils.CmdUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Enchant implements CommandExecutor, TabCompleter {
    public static final String syntaxe = "/ench (<enchants...> | all) [(<lvl> | max | overMax)], ...";

    public static final LinkedHashMap<String, Integer> enchantByName = new LinkedHashMap<String, Integer>() {{
        put("protection", 0);
        put("fire_protection", 1);
        put("feather_falling", 2);
        put("blast_protection", 3);
        put("projectil_protection", 4);
        put("respiration", 5);
        put("aqua_affinity", 6);
        put("thorns", 7);
        put("depth_strider", 8);

        put("sharpness", 16);
        put("smite", 17);
        put("bane_of_arthropods", 18);
        put("knockback", 19);
        put("fire_aspect", 20);
        put("looting", 21);

        put("efficiency", 32);
        put("silk_touch", 33);
        put("unbreaking", 34);
        put("fortune", 35);

        put("power", 48);
        put("punch", 49);
        put("flame", 50);
        put("infinity", 51);

        put("luck_of_the_sea", 61);
        put("lure", 62);

        // Customs - Do not change
        put("unbreakable", 1091);
        put("hide_attributes", 1092);
        put("hide_destroys", 1093);
        put("hide_enchants", 1094);
        put("hide_placed_on", 1095);
        put("hide_potion_effect", 1096);
        put("hide_unbreakable", 1097);
        put("all", 1101);
        put("clear", 1102);
        put("all_flags", 1103);
        put("clear_flags", 1104);
    }};

    public static final int maxLvl = 32767, remLvl = 32768, intLimit = 2147483647;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        CmdUtils u = new CmdUtils(sender, cmd, msg, args, syntaxe);

        if (args.length >= 1 && args[0].equalsIgnoreCase("?")) {
            u.synt();
            return false;
        }

        if (!(sender instanceof Player)) {
            u.err(CmdUtils.err_not_player);
            return false;
        }

        LinkedHashMap<Integer, Integer> enchants = new LinkedHashMap<>(); // Map<ID, Lvl>
        List<String> raw = Arrays.asList(String.join(" ", args).split(","));
        raw.forEach(s -> {
            if (!s.equals("")) {
                int lvl = 1;
                boolean max = false;
                List<String> arguments = new ArrayList<>(Arrays.asList(s.split(" ")));
                String last = "";
                arguments.add(null);
                while (last.equals("")) {
                    arguments.remove(arguments.size() - 1);
                    last = arguments.get(arguments.size() - 1);
                }
                if (last.equalsIgnoreCase("max")) {
                    max = true;
                    arguments.remove(arguments.size() - 1);
                } else if (last.equalsIgnoreCase("overMax")) {
                    lvl = maxLvl;
                    arguments.remove(arguments.size() - 1);
                } else
                    try {
                        lvl = Integer.parseInt(last);
                        arguments.remove(arguments.size() - 1);
                    } catch (NumberFormatException ignored) {
                    }
                int finalLvl = lvl;
                arguments.forEach(arg -> {
                    if (!arg.equals("")) {
                        String a = arg.startsWith("-") ? arg.replaceFirst("-", "") : arg;
                        boolean rem = arg.startsWith("-");
                        int id = -1;
                        if (enchantByName.containsKey(a.toLowerCase()))
                            id = enchantByName.get(a.toLowerCase());
                        else
                            try {
                                id = Integer.parseInt(a);
                                if (!enchantByName.containsValue(id))
                                    throw new NumberFormatException("");
                            } catch (NumberFormatException ignored) {
                                u.err(CmdUtils.err_unknown_enchant, "('" + a + "')");
                            }
                        if (id != -1)
                            enchants.put(id, rem ? remLvl : finalLvl);
                    }
                });
            }
        });

        if (u.getPlayer().getItemInHand() != null && !u.getPlayer().getItemInHand().getType().equals(Material.AIR))
            enchants.forEach((id, lvl) -> {
                if (id < 1090)
                    if (lvl == remLvl)
                        u.getPlayer().getItemInHand().removeEnchantment(Enchantment.getById(id));
                    else
                        u.getPlayer().getItemInHand().addUnsafeEnchantment(Enchantment.getById(id), lvl);
                else if (id == 1091) {
                    ItemMeta m = u.getPlayer().getItemInHand().getItemMeta();
                    m.spigot().setUnbreakable(lvl != remLvl);
                    u.getPlayer().getItemInHand().setItemMeta(m);
                } else if (id == 1092) {
                    ItemMeta m = u.getPlayer().getItemInHand().getItemMeta();
                    if (lvl == remLvl)
                        m.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    else
                        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    u.getPlayer().getItemInHand().setItemMeta(m);
                } else if (id == 1093) {
                    ItemMeta m = u.getPlayer().getItemInHand().getItemMeta();
                    if (lvl == remLvl)
                        m.removeItemFlags(ItemFlag.HIDE_DESTROYS);
                    else
                        m.addItemFlags(ItemFlag.HIDE_DESTROYS);
                    u.getPlayer().getItemInHand().setItemMeta(m);
                } else if (id == 1094) {
                    ItemMeta m = u.getPlayer().getItemInHand().getItemMeta();
                    if (lvl == remLvl)
                        m.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                    else
                        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    u.getPlayer().getItemInHand().setItemMeta(m);
                } else if (id == 1095) {
                    ItemMeta m = u.getPlayer().getItemInHand().getItemMeta();
                    if (lvl == remLvl)
                        m.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
                    else
                        m.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                    u.getPlayer().getItemInHand().setItemMeta(m);
                } else if (id == 1096) {
                    ItemMeta m = u.getPlayer().getItemInHand().getItemMeta();
                    if (lvl == remLvl)
                        m.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                    else
                        m.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                    u.getPlayer().getItemInHand().setItemMeta(m);
                } else if (id == 1097) {
                    ItemMeta m = u.getPlayer().getItemInHand().getItemMeta();
                    if (lvl == remLvl)
                        m.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    else
                        m.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    u.getPlayer().getItemInHand().setItemMeta(m);
                } else if (id == 1101)
                    for (Enchantment enchantment : Enchantment.values())
                        u.getPlayer().getItemInHand().addUnsafeEnchantment(enchantment, lvl);
                else if (id == 1102)
                    for (Enchantment enchantment : Enchantment.values())
                        u.getPlayer().getItemInHand().removeEnchantment(enchantment);
                else if (id == 1103) {
                    ItemMeta m = u.getPlayer().getItemInHand().getItemMeta();
                    for (ItemFlag itf : ItemFlag.values())
                        m.addItemFlags(itf);
                    u.getPlayer().getItemInHand().setItemMeta(m);
                } else if (id == 1104) {
                    ItemMeta m = u.getPlayer().getItemInHand().getItemMeta();
                    for (ItemFlag itf : ItemFlag.values())
                        m.removeItemFlags(itf);
                    u.getPlayer().getItemInHand().setItemMeta(m);
                }
            });
        else
            u.err(CmdUtils.err_no_item_hold);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        new ArrayList<String>() {{
            add("-");
            enchantByName.forEach((id, lvl) -> addAll(Arrays.asList((args[args.length - 1].startsWith("-") ? "-" : "") + id,
                    (args[args.length - 1].startsWith("-") ? "-" : "") + lvl)));
            try {
                int i = Integer.parseInt(args[args.length - 1]);
                for (int j = 0; j < 10; j++)
                    add(i + "" + j + ",");
            } catch (NumberFormatException ignored) {
            }
        }}.forEach(p -> {
            if (p.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                list.add(p);
        });

        return list;
    }
}
