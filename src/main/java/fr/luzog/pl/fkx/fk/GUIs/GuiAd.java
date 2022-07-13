package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.commands.Other.Ad;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.utils.Heads;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

public class GuiAd {

    public static enum SortType {
        ID,
        DATE,
        STATUS,
        INSISTENCE,
        SENDER,
        ADMIN,
        MESSAGE
    }

    public static ItemStack getMainItem(String lastLoreLine, String command) {
        return Items.builder(Material.EMERALD)
                .setName("§5§l§n-=[§2§l §k0§2§l §n/AD§2§l §k0§5§l §n]=-")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2Nombre de requêtes : §f" + Ad.ads.size(),
                        "  §2Requêtes en attente : §3" + Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.WAITING).count(),
                        "  §2Requêtes terminées : §f" + Ad.ads.stream().filter(ad ->
                                ad.getState() == Ad.State.CLOSED || ad.getState() == Ad.State.IGNORED).count(),
                        "  §8  > Acceptées : §2" + Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.ACCEPTED).count(),
                        "  §8  > Fermées : §4" + Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.CLOSED).count(),
                        "  §8  > Ignorées : §7" + Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.IGNORED).count(),
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getFilterItem(SortType sorted, boolean reversed, String navigationBaseCommand, int page) {
        String shiftCmd = navigationBaseCommand + " " + (reversed ? "+" : "-") + sorted.name() + " " + page;
        return Items.builder(Material.EMERALD_BLOCK)
                .setName("§7Trier par : §d" + (reversed ? "-" : "+") + " " + sorted.name())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour trier par : " + (sorted.ordinal() == SortType.values().length - 1 ?
                                SortType.values()[0].name() : SortType.values()[sorted.ordinal() + 1].name()),
                        "§7Clic Droit pour trier par : " + (sorted.ordinal() == 0 ?
                                SortType.values()[SortType.values().length - 1].name()
                                : SortType.values()[sorted.ordinal() - 1].name()),
                        "§7Shift Clic pour inverser l'ordre"
                )
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .setCantClickOn(true)
                .setLeftRightShiftCommandOnClick(
                        navigationBaseCommand + " " + (reversed ? "-" : "+") + (sorted.ordinal() == SortType.values().length - 1 ?
                                SortType.values()[0].name() : SortType.values()[sorted.ordinal() + 1].name()) + " " + page,
                        shiftCmd,
                        navigationBaseCommand + " " + (reversed ? "-" : "+") + (sorted.ordinal() == 0 ?
                                SortType.values()[SortType.values().length - 1].name()
                                : SortType.values()[sorted.ordinal() - 1].name()) + " " + page,
                        shiftCmd
                )
                .build();
    }

    public static ItemStack getAdItem(int id, Ad.Item ad, String command) {
        return Items.builder(/* Material.EMERALD */ Heads.getSkullOf(Ad.SYS_NAME.equals(ad.getSender()) ? "Microsoft" : ad.getSender()))
                .setName("§2§lAD : §3#" + Ad.df.format(id))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §2Date : §f" + new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(ad.getDate()),
                        "  §8  >  Il y a " + Utils.compareDate(ad.getDate(), new Date(), false) + ".",
                        "  §2Status : §f" + (ad.getState() == Ad.State.WAITING ? "§7EN ATTENTE"
                                : ad.getState() == Ad.State.ACCEPTED ? "§aPRIS EN CHARGE"
                                : ad.getState() == Ad.State.IGNORED ? "§8IGNORÉ"
                                : ad.getState() == Ad.State.CLOSED ? "§4FERMÉE" : ad.getState()),
                        "  §2Insistence : " + (ad.getInsistence() == 1 ? "§f1"
                                : ad.getInsistence() == 2 ? "§62"
                                : ad.getInsistence() == 3 ? "§c3"
                                : "§4" + ad.getInsistence()),
                        " ",
                        "  §2---",
                        " ",
                        "  §2Joueur : §6" + (FKManager.getCurrentGame() == null
                                || FKManager.getCurrentGame().getPlayer(ad.getSender(), false) == null ?
                                ad.getSender() : FKManager.getCurrentGame().getPlayer(ad.getSender(), false).getDisplayName()),
                        "  §2Admin : §f" + (ad.getAdmin() == null ? "§cnull" : ad.getAdmin()),
                        "  §2Description : §f" + (ad.getMessage() == null ? "§cnull" : ad.getMessage()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour " + (ad.getState() == Ad.State.ACCEPTED ? "fermer" : "accepter"),
                        "§7Clic Droit pour " + (ad.getState() == Ad.State.IGNORED ? "mettre en attente" : "ignorer")
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(
                        ad.getState() == Ad.State.ACCEPTED ? "ad close " + id + "\n" + command
                                : "ad accept " + id + "\n" + command,
                        ad.getState() == Ad.State.IGNORED ? "ad waiting " + id + "\n" + command
                                : "ad ignore " + id + "\n" + command
                )
                .build();
    }

    public static Inventory getAdsInventory(SortType sorted, boolean reversed, String back, String navigationBaseCommand, int page) {
        ArrayList<Ad.Item> ads = new ArrayList<>();
        if (sorted == SortType.ID) {
            ads.addAll(Ad.ads);
        } else if (sorted == SortType.DATE) {
            ads.addAll(Ad.ads.stream().sorted((a, b) -> b.getDate().compareTo(a.getDate())).collect(Collectors.toList()));
        } else if (sorted == SortType.STATUS) {
            ads.addAll(Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.WAITING).collect(Collectors.toList()));
            ads.addAll(Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.ACCEPTED).collect(Collectors.toList()));
            ads.addAll(Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.IGNORED).collect(Collectors.toList()));
            ads.addAll(Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.CLOSED).collect(Collectors.toList()));
        } else if (sorted == SortType.INSISTENCE) {
            ads.addAll(Ad.ads.stream().sorted((a, b) -> b.getInsistence() - a.getInsistence()).collect(Collectors.toList()));
        } else if (sorted == SortType.SENDER) {
            ads.addAll(Ad.ads.stream().sorted(Comparator.comparing(ad -> ad.getSender() == null ? "\uffff" : ad.getSender())).collect(Collectors.toList()));
        } else if (sorted == SortType.ADMIN) {
            ads.addAll(Ad.ads.stream().sorted(Comparator.comparing(ad -> ad.getAdmin() == null ? "\uffff" : ad.getAdmin())).collect(Collectors.toList()));
        } else if (sorted == SortType.MESSAGE) {
            ads.addAll(Ad.ads.stream().sorted(Comparator.comparing(ad -> ad.getMessage() == null ? "\uffff" : ad.getMessage())).collect(Collectors.toList()));
        }

        ArrayList<ItemStack> items = ads.stream().map(ad -> getAdItem(Ad.ads.indexOf(ad), ad,
                navigationBaseCommand + " " + (reversed ? "-" : "+") + sorted.name() + " " + page))
                .collect(Collectors.toCollection(ArrayList::new));

        if(reversed)
            Collections.reverse(items);

        return Guis.getPagedInventory("ads", 54, back,
                getMainItem("Clic pour rafraîchir", navigationBaseCommand + " "
                        + (reversed ? "-" : "+") + sorted.name() + " " + page),
                getFilterItem(sorted, reversed, navigationBaseCommand, page),
                navigationBaseCommand + " " + (reversed ? "-" : "+") + sorted.name(), page, items);
    }
}
