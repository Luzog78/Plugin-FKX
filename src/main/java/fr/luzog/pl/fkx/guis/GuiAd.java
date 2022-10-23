package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.commands.Other.Ad;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.utils.CustomNBT;
import fr.luzog.pl.fkx.utils.Heads;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;
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
        String c1 = navigationBaseCommand + " " + (reversed ? "-" : "+") + (sorted.ordinal() == SortType.values().length - 1 ?
                SortType.values()[0].name() : SortType.values()[sorted.ordinal() + 1].name()) + " " + page,
                c2 = navigationBaseCommand + " " + (reversed ? "-" : "+") + (sorted.ordinal() == 0 ?
                        SortType.values()[SortType.values().length - 1].name()
                        : SortType.values()[sorted.ordinal() - 1].name()) + " " + page,
                c3 = navigationBaseCommand + " " + (reversed ? "+" : "-") + sorted.name() + " " + page;
        return Items.builder(Material.EMERALD_BLOCK)
                .setName("§7Tri : §8[" + Arrays.stream(SortType.values())
                        .map(t -> (t.equals(sorted) ? "§f§l" : "§8") + t.name())
                        .collect(Collectors.joining("§8, ")) + "§8]")
                .setLore(
                        "§7Ordre : §d" + (reversed ? "Inversé" : "Naturel"),
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour trier par le suivant",
                        "§7Clic Droit pour trier par le précédent",
                        "§7Clic Molette pour inverser l'ordre",
                        " ",
                        "§7Commande :",
                        "§7/" + c1,
                        "§7/" + c2,
                        "§7/" + c3
                )
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(c1, c2)
                .setMiddleCommandOnClick(c3)
                .build();
    }

    public static ItemStack getAdItem(Ad.Item ad, String command) {
        return Items.builder(/* Material.EMERALD */ Heads.getSkullOf(
                Ad.SYS_NAME.equals(ad.getSender()) ? "Microsoft" : ad.getSender()))
                .setName("§2§lAD : §3#" + Ad.df.format(ad.getId()))
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
                        "  §2Joueur : §6" + (GManager.getCurrentGame() == null
                                || GManager.getCurrentGame().getPlayer(ad.getSender(), false) == null ?
                                ad.getSender() : GManager.getCurrentGame().getPlayer(ad.getSender(), false).getDisplayName()),
                        "  §2Admin : §f" + (ad.getAdmin() == null ? "§cnull" : ad.getAdmin()),
                        "  §2Description : §f" + (ad.getMessage() == null ? "§cnull" : ad.getMessage()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour " + (ad.getState() == Ad.State.ACCEPTED ? "§4fermer" : "§2accepter"),
                        "§7  (Shift pour " + (ad.getState() != Ad.State.ACCEPTED ? "§4fermer" : "§2accepter") + "§7)",
                        "§7Clic Droit pour " + (ad.getState() == Ad.State.IGNORED ? "§lmettre en attente" : "§8ignorer"),
                        "§7  (Shift pour " + (ad.getState() != Ad.State.IGNORED ? "§lattente" : "§8ignorer") + "§7)",
                        "§7Clic Molette pour se tp",
                        " ",
                        "§7Commande :",
                        "§7/ad §8(§2accept §8|§4 close§8)§7 " + Ad.df.format(ad.getId()),
                        "§7/ad §8(§7§lwaiting §8|§8§l ignore§8)§7 " + Ad.df.format(ad.getId())
                )
                .setCantClickOn(true)
                .setLeftRightShiftCommandOnClick(
                        ad.getState() == Ad.State.ACCEPTED ? "ad close " + ad.getId() + "\n" + command
                                : "ad accept " + ad.getId() + "\n" + command,
                        ad.getState() != Ad.State.ACCEPTED ? "ad close " + ad.getId() + "\n" + command
                                : "ad accept " + ad.getId() + "\n" + command,
                        ad.getState() == Ad.State.IGNORED ? "ad waiting " + ad.getId() + "\n" + command
                                : "ad ignore " + ad.getId() + "\n" + command,
                        ad.getState() != Ad.State.IGNORED ? "ad waiting " + ad.getId() + "\n" + command
                                : "ad ignore " + ad.getId() + "\n" + command
                )
                .setMiddleCommandOnClick("tp " + ad.getSender())
                .build();
    }

    public static Inventory getAdsInventory(SortType sorted, boolean reversed, String back, String navigationBaseCommand, int page) {
        ArrayList<Ad.Item> ads = new ArrayList<>();
        if (sorted == SortType.ID) {
            ads.addAll(Ad.ads);
        } else if (sorted == SortType.DATE) {
            ads.addAll(Ad.ads.stream().sorted((a, b) -> b.getDate().compareTo(a.getDate())).collect(Collectors.toList()));
        } else if (sorted == SortType.STATUS) {
            new ArrayList<List<Ad.Item>>(Arrays.asList(
                    Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.WAITING).collect(Collectors.toList()),
                    Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.ACCEPTED).collect(Collectors.toList()),
                    Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.CLOSED).collect(Collectors.toList()),
                    Ad.ads.stream().filter(ad -> ad.getState() == Ad.State.IGNORED).collect(Collectors.toList())
            )) {{
                forEach(l -> {
                    for (int i = 0; i < l.size() / 28; i++)
                        for (int j = 0; j < 28; j++)
                            ads.add(l.get(i * 28 + j));

                    for (int i = 0; i < l.size() % 28; i++)
//                        ads.add(l.get(l.size() - i - 1));
                        ads.add(l.get(((int) (l.size() / 28) * 28) + i));
                    for (int i = 0; i < 28 - l.size() % 28; i++)
                        ads.add(null);
                });
            }};
        } else if (sorted == SortType.INSISTENCE) {
            ads.addAll(Ad.ads.stream().sorted((a, b) -> b.getInsistence() - a.getInsistence()).collect(Collectors.toList()));
        } else if (sorted == SortType.SENDER) {
            ads.addAll(Ad.ads.stream().sorted(Comparator.comparing(ad -> ad.getSender() == null ? "\uffff" : ad.getSender())).collect(Collectors.toList()));
        } else if (sorted == SortType.ADMIN) {
            ads.addAll(Ad.ads.stream().sorted(Comparator.comparing(ad -> ad.getAdmin() == null ? "\uffff" : ad.getAdmin())).collect(Collectors.toList()));
        } else if (sorted == SortType.MESSAGE) {
            ads.addAll(Ad.ads.stream().sorted(Comparator.comparing(ad -> ad.getMessage() == null ? "\uffff" : ad.getMessage())).collect(Collectors.toList()));
        }

        ArrayList<ItemStack> items = ads.stream().map(ad -> ad == null ? null : getAdItem(ad,
                        navigationBaseCommand + " " + (reversed ? "-" : "+") + sorted.name() + " " + page))
                .collect(Collectors.toCollection(ArrayList::new));
        items.replaceAll(item -> item == null ? new CustomNBT(Items.black())
                .setString("nonStackable", UUID.randomUUID().toString()).build() : item);

        if (reversed)
            Collections.reverse(items);

        return Guis.getPagedInventory(Ad.AD_PREFIX + "§lList", 54, back,
                getMainItem("Clic pour rafraîchir", navigationBaseCommand + " "
                        + (reversed ? "-" : "+") + sorted.name() + " " + page),
                getFilterItem(sorted, reversed, navigationBaseCommand, page),
                navigationBaseCommand + " " + (reversed ? "-" : "+") + sorted.name(), page, items);
    }
}
