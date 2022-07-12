package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.utils.DayMoment;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GuiDate {

    public static ItemStack getMainItem(String lastLoreLine, String command) {
        if (FKManager.getCurrentGame() == null || Main.world == null)
            return Items.builder(Material.WATCH)
                    .setName("§fDate")
                    .setLore("§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                    .setCantClickOn(true)
                    .setGlobalCommandOnClick(command)
                    .build();
        FKManager fk = FKManager.getCurrentGame();
        return Items.builder(Material.WATCH)
                .setName("§eDate")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §eJour : §3" + fk.getDay(),
                        "  §eHeur : §3" + fk.getFormattedTime(),
                        "  §eMétéo : §f" + (Main.world.isThundering() ? "§7Orageux" : Main.world.hasStorm() ? "§9Pluvieux" : "§6Ensoleillé"),
                        "  ---",
                        "  §eWeather Duration (ticks) : §f" + Main.world.getWeatherDuration(),
                        "  §eThunder Duration (ticks) : §f" + Main.world.getThunderDuration(),
                        " ",
                        "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getDayItem(String lastLoreLine, String command) {
        return Items.builder(Material.BED)
                .setName("§eJour : §3" + FKManager.getCurrentGame().getDay())
                .setLore("§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getHourItem(String lastLoreLine, String command) {
        return Items.builder(Material.DOUBLE_PLANT)
                .setName("§eHeure : §3" + FKManager.getCurrentGame().getFormattedTime())
                .setLore("§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getWeatherItem(String lastLoreLine, String command) {
        return Items.builder(Material.RAW_FISH)
                .setDurability((short) 3)
                .setName("§eMétéo : §f" + (Main.world.isThundering() ? "§7Orageux" : Main.world.hasStorm() ? "§9Pluvieux" : "§6Ensoleillé"))
                .setLore("§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getDayInventory(String back) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        if (Main.world == null)
            return Guis.getErrorInventory("No world", back);
        FKManager fk = FKManager.getCurrentGame();
        Inventory inv = Guis.getBaseInventory("§eDate §f- §3Jour", 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 1),
                getDayItem("Clic pour rafraichir", "fk date day"));

        inv.setItem(Utils.posOf(1, 1), Items.builder(Items.red())
                .setAmount(3)
                .setName("§cRevenir 3 jours avant")
                .setLore("§8" + Guis.loreSeparator, " ", "§7Commande :",
                        "§7fk date set " + (fk.getDay() - 3))
                .setGlobalCommandOnClick("fk date day set " + (fk.getDay() - 3))
                .setCloseOnClick(true)
                .build());
        inv.setItem(Utils.posOf(2, 1), Items.builder(Items.red())
                .setAmount(2)
                .setName("§cRevenir 2 jours avant")
                .setLore("§8" + Guis.loreSeparator, " ", "§7Commande :",
                        "§7fk date set " + (fk.getDay() - 2))
                .setGlobalCommandOnClick("fk date day set " + (fk.getDay() - 2))
                .setCloseOnClick(true)
                .build());
        inv.setItem(Utils.posOf(3, 1), Items.builder(Items.red())
                .setAmount(1)
                .setName("§cRevenir au jour precedent")
                .setLore("§8" + Guis.loreSeparator, " ", "§7Commande :",
                        "§7fk date set " + (fk.getDay() - 1))
                .setGlobalCommandOnClick("fk date day set " + (fk.getDay() - 1))
                .setCloseOnClick(true)
                .build());

        inv.setItem(Utils.posOf(5, 1), Items.builder(Items.lime())
                .setAmount(1)
                .setName("§aPasser au jour suivant")
                .setLore("§8" + Guis.loreSeparator, " ", "§7Commande :",
                        "§7fk date set " + (fk.getDay() + 1))
                .setGlobalCommandOnClick("fk date day set " + (fk.getDay() + 1))
                .setCloseOnClick(true)
                .build());
        inv.setItem(Utils.posOf(6, 1), Items.builder(Items.lime())
                .setAmount(2)
                .setName("§aAvancer de 2 jours")
                .setLore("§8" + Guis.loreSeparator, " ", "§7Commande :",
                        "§7fk date set " + (fk.getDay() + 2))
                .setGlobalCommandOnClick("fk date day set " + (fk.getDay() + 2))
                .setCloseOnClick(true)
                .build());
        inv.setItem(Utils.posOf(7, 1), Items.builder(Items.lime())
                .setAmount(3)
                .setName("§aAvancer de 3 jours")
                .setLore("§8" + Guis.loreSeparator, " ", "§7Commande :",
                        "§7fk date set " + (fk.getDay() + 3))
                .setGlobalCommandOnClick("fk date day set " + (fk.getDay() + 3))
                .setCloseOnClick(true)
                .build());

        for (int i = 0; i < 2; i++)
            for (int j = 1; j < 8; j++)
                inv.setItem(Utils.posOf(j, i + 3), Items.builder(Material.STAINED_GLASS_PANE)
                        .setDurability(DyeColor.PINK.getData())
                        .setAmount(j + i * 6)
                        .setName("§dPasser au jour " + (j + i * 6))
                        .setLore("§8" + Guis.loreSeparator, " ", "§7Commande :",
                                "§7fk date set " + (j + i * 6))
                        .setGlobalCommandOnClick("fk date day set " + (j + i * 6))
                        .setCloseOnClick(true)
                        .build());

        return inv;
    }

    public static Inventory getHourInventory(String back) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        if (Main.world == null)
            return Guis.getErrorInventory("No world", back);
        Inventory inv = Guis.getBaseInventory("§eDate §f- §3Heure", 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 1),
                getHourItem("Clic pour rafraichir", "fk date time"));

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    if (j != 2 || k != 2)
                        try {
                            DayMoment m = DayMoment.values()[k + j * 3 + i * 8];
                            inv.setItem(Utils.posOf(k + 1 + i * 4, j + 2),
                                    Items.builder(Material.STAINED_GLASS_PANE)
                                            .setDurability(i == 0 ? DyeColor.YELLOW.getData()
                                                    : DyeColor.LIGHT_BLUE.getData())
                                            .setName((i == 0 ? "§e" : "§1")
                                                    + (m.getFormattedName().length() <= 2 ?
                                                    " " : m.getFormattedName()))
                                            .setLore(
                                                    "§8" + Guis.loreSeparator,
                                                    " ",
                                                    "  §8> Heure : §f" + m.getFormattedTime(),
                                                    "  §8> Temps : §f" + m.getHour(),
                                                    " ",
                                                    "§7Commandes possibles :",
                                                    "§7fk date time set " + m.name(),
                                                    "§7fk date time set " + m.getFormattedTime(),
                                                    "§7fk date time set " + m.getHour()
                                            )
                                            .setGlobalCommandOnClick("fk date time set " + m.getHour()
                                                    + "\nfk date time")
                                            .setCantClickOn(true)
                                            .build());
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }

        return inv;
    }

    public static Inventory getWeatherInventory(String back){
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        if (Main.world == null)
            return Guis.getErrorInventory("No world", back);
        Inventory inv = Guis.getBaseInventory("§eDate §f- §3Météo", 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 1),
                getWeatherItem("Clic pour rafraichir", "fk date weather"));

        inv.setItem(Utils.posOf(2, 2), Items.builder(Material.BUCKET)
                .setName("§6Amener le Soleil")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Commande :",
                        "§7fk date weather set sun"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick("fk date weather set sun\nfk date weather")
                .build());
        inv.setItem(Utils.posOf(4, 3), Items.builder(Material.WATER_BUCKET)
                .setName("§9Déverser la Pluie")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Commande :",
                        "§7fk date weather set rain"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick("fk date weather set rain\nfk date weather")
                .build());
        inv.setItem(Utils.posOf(6, 2), Items.builder(Material.LAVA_BUCKET)
                .setName("§7Faire gronder l'Orage")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Commande :",
                        "§7fk date weather set thunder"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick("fk date weather set thunder\nfk date weather")
                .build());

        return inv;
    }

    public static Inventory getMainInventory(String back) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        if (Main.world == null)
            return Guis.getErrorInventory("No world", back);
        Inventory inv = Guis.getBaseInventory("§eDate", 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(2, 2), getHourItem("Clic pour voir plus", "fk date time"));
        inv.setItem(Utils.posOf(4, 3), getDayItem("Clic pour voir plus", "fk date day"));
        inv.setItem(Utils.posOf(6, 2), getWeatherItem("Clic pour voir plus", "fk date weather"));

        return inv;
    }

}
