package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
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
        return Items.builder(Material.BED)
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

    public static Inventory getMainInventory(String back) {
        if (FKManager.getCurrentGame() == null || Main.world == null)
            return Guis.getErrorInventory("No game running", back);
        FKManager fk = FKManager.getCurrentGame();
        Inventory inv = Guis.getBaseInventory("§bDate", 36, back,
                getMainItem(null, "null"), null);

        return inv;
    }

}
