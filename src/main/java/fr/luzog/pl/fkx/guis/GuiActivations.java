package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GOptions;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuiActivations {

    public static enum OptionType {
        PVP(Material.BOW),
        NETHER(Material.OBSIDIAN),
        ASSAULTS(Material.TNT),
        END(Material.ENDER_PORTAL_FRAME);

        private final Material material;

        OptionType(Material material) {
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }

        public static OptionType fromMaterial(Material material) {
            return Arrays.stream(values()).filter(type -> type.getMaterial() == material).findFirst().orElse(null);
        }

        public static OptionType fromName(String name) {
            return Arrays.stream(values()).filter(type -> type.name().equalsIgnoreCase(name)).findFirst().orElse(null);
        }
    }

    public static ItemStack getMainItem(String lastLoreLine, String command) {
        if (GManager.getCurrentGame() == null || Main.world == null)
            return Items.builder(Material.COMMAND)
                    .setName("§5Activations")
                    .setLore("§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                    .setCantClickOn(true)
                    .setGlobalCommandOnClick(command)
                    .build();
        GManager game = GManager.getCurrentGame();
        return Items.builder(Material.COMMAND)
                .setName("§5Activations")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        game.getOptions().getOptions().stream()
                                .map(o -> "  §5" + o.getName() + " : " + o.getFormattedActivation() + " §7§o(Jour " + o.getActivationDay() + ")")
                                .collect(Collectors.joining("\n \n")),
                        " ",
                        "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getOptionItem(Material mat, GOptions.GOption opt,
                                          String lastLoreLines, String cmdLeft, String cmdRight) {
        return Items.builder(mat)
                .setName("§5" + opt.getName() + " : " + opt.getFormattedActivation() + " §7§o(Jour " + opt.getActivationDay() + ")")
                .setLore("§8" + Guis.loreSeparator + (lastLoreLines == null ? "" : "\n§7" + lastLoreLines))
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(cmdLeft, cmdRight)
                .build();
    }

    public static Inventory getOptionInventory(Material mat, GOptions.GOption opt, String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);

        String refresh = Main.CMD + " activations " + opt.getId();

        GManager game = GManager.getCurrentGame();
        Inventory inv = Guis.getBaseInventory("§5Activations §f- §d" + opt.getName(), 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 1), getOptionItem(mat, opt,
                "§7Clic Gauche pour rafraîchir"
                        + "\n§7Clic Droit pour §fdéfinir"
                        + "\n "
                        + "\n§7Commande :"
                        + "\n§7/" + Main.CMD + " activations " + opt.getId() + " set §8(§2on §8|§4 off §8|§f <jour>§8)",
                refresh, "input 1 " + Main.CMD + " activations " + opt.getId() + " set %s%n" + refresh));

        inv.setItem(Utils.posOf(1, 1), Items.builder(Items.red())
                .setAmount(3)
                .setName("§cDéfinir 3 jours avant")
                .setLore("§8" + Guis.loreSeparator, "§7Commande :",
                        "§7/" + Main.CMD + " activations " + opt.getId() + " set " + Math.max(opt.getActivationDay() - 3, 1))
                .setGlobalCommandOnClick(Main.CMD + " activations " + opt.getId() + " set " + Math.max(opt.getActivationDay() - 3, 1) + "\n" + refresh)
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(2, 1), Items.builder(Items.red())
                .setAmount(2)
                .setName("§cDéfinir 2 jours avant")
                .setLore("§8" + Guis.loreSeparator, "§7Commande :",
                        "§7/" + Main.CMD + " activations " + opt.getId() + " set " + Math.max(opt.getActivationDay() - 2, 1))
                .setGlobalCommandOnClick(Main.CMD + " activations " + opt.getId() + " set " + Math.max(opt.getActivationDay() - 2, 1) + "\n" + refresh)
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(3, 1), Items.builder(Items.red())
                .setAmount(1)
                .setName("§cDéfinir au jour précédent")
                .setLore("§8" + Guis.loreSeparator, "§7Commande :",
                        "§7/" + Main.CMD + " activations " + opt.getId() + " set " + Math.max(opt.getActivationDay() - 1, 1))
                .setGlobalCommandOnClick(Main.CMD + " activations " + opt.getId() + " set " + Math.max(opt.getActivationDay() - 1, 1) + "\n" + refresh)
                .setCantClickOn(true)
                .build());

        inv.setItem(Utils.posOf(5, 1), Items.builder(Items.lime())
                .setAmount(1)
                .setName("§aDéfinir au jour suivant")
                .setLore("§8" + Guis.loreSeparator, "§7Commande :",
                        "§7/" + Main.CMD + " activations " + opt.getId() + " set " + (opt.getActivationDay() + 1))
                .setGlobalCommandOnClick(Main.CMD + " activations " + opt.getId() + " set " + (opt.getActivationDay() + 1) + "\n" + refresh)
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(6, 1), Items.builder(Items.lime())
                .setAmount(2)
                .setName("§aDéfinir 2 jours après")
                .setLore("§8" + Guis.loreSeparator, "§7Commande :",
                        "§7/" + Main.CMD + " activations " + opt.getId() + " set " + (opt.getActivationDay() + 2))
                .setGlobalCommandOnClick(Main.CMD + " activations " + opt.getId() + " set " + (opt.getActivationDay() + 2) + "\n" + refresh)
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(7, 1), Items.builder(Items.lime())
                .setAmount(3)
                .setName("§aDéfinir 3 jours après")
                .setLore("§8" + Guis.loreSeparator, "§7Commande :",
                        "§7/" + Main.CMD + " activations " + opt.getId() + " set " + (opt.getActivationDay() + 3))
                .setGlobalCommandOnClick(Main.CMD + " activations " + opt.getId() + " set " + (opt.getActivationDay() + 3) + "\n" + refresh)
                .setCantClickOn(true)
                .build());

        Utils.fill(inv, Utils.posOf(1, 2), Utils.posOf(7, 2),
                Items.builder(opt.isActivated() ? Items.lime() : Items.red())
                        .setName((opt.isActivated() ? "§2Activé" : "§4Désactivé") + "  " + opt.getFormattedActivation())
                        .setLore("§8" + Guis.loreSeparator,
                                "§7Clic pour §f" + (opt.isActivated() ? "§4désactiver" : "§2activer"),
                                " ",
                                "§7Commande :",
                                "§7/" + Main.CMD + " activations " + opt.getId() + " set §8(§2on §8|§4 off§8)")
                        .setGlobalCommandOnClick(Main.CMD + " activations " + opt.getId() + " set " + (opt.isActivated() ? "off" : "on") + "\n" + refresh)
                        .setCantClickOn(true)
                        .build());

        for (int i = 0; i < 2; i++)
            for (int j = 1; j < 8; j++)
                inv.setItem(Utils.posOf(j, i + 3), Items.builder(Material.STAINED_GLASS_PANE)
                        .setDurability(DyeColor.PINK.getData())
                        .setAmount(j + i * 6)
                        .setName("§dDéfinir au jour " + (j + i * 6))
                        .setLore("§8" + Guis.loreSeparator, "§7Commande :",
                                "§7/" + Main.CMD + " activations " + opt.getId() + " set " + (j + i * 6))
                        .setGlobalCommandOnClick(Main.CMD + " activations " + opt.getId() + " set " + (j + i * 6) + "\n" + refresh)
                        .setCantClickOn(true)
                        .build());

        return inv;
    }

    public static Inventory getMainInventory(String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        Inventory inv = Guis.getBaseInventory("§5Activations", 54, back,
                getMainItem(null, "null"),
                Items.builder(Material.NETHER_STAR)
                        .setName("§aEffectuer un Check")
                        .setLore("§8" + Guis.loreSeparator, "§7Clic pour effectuer un check",
                                " ",
                                "§7Commande :",
                                "§7/" + Main.CMD + " activations check")
                        .setGlobalCommandOnClick(Main.CMD + " activations check")
                        .setCantClickOn(true)
                        .build());

        inv.setItem(Utils.posOf(1, 2), getOptionItem(OptionType.PVP.getMaterial(),
                GManager.getCurrentGame().getOptions().getPvp(),
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " activations pvp",
                Main.CMD + " activations pvp", Main.CMD + " activations pvp"));
        inv.setItem(Utils.posOf(3, 3), getOptionItem(OptionType.NETHER.getMaterial(),
                GManager.getCurrentGame().getOptions().getNether(),
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " activations nether",
                Main.CMD + " activations nether", Main.CMD + " activations nether"));
        inv.setItem(Utils.posOf(5, 3), getOptionItem(OptionType.ASSAULTS.getMaterial(),
                GManager.getCurrentGame().getOptions().getAssaults(),
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " activations assaults",
                Main.CMD + " activations assaults", Main.CMD + " activations assaults"));
        inv.setItem(Utils.posOf(7, 2), getOptionItem(OptionType.END.getMaterial(),
                GManager.getCurrentGame().getOptions().getEnd(),
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " activations end",
                Main.CMD + " activations end", Main.CMD + " activations end"));

        return inv;
    }

}
