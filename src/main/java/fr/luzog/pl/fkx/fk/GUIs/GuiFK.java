package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.Heads;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class GuiFK {

    public static ItemStack getMainItem(FKManager fk, String lastLoreLine, String command) {
        if (fk == null)
            return Items.l_gray();
        return Items.builder(fk.getState() == FKManager.State.ENDED ? Heads.MISC_PURPLE_ORB.getSkull() : Heads.MISC_BLUE_ORB.getSkull())
                .setName("§f" + fk.getId())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §aID : §6" + fk.getId(),
                        "  §aStatus : §7" + fk.getState().toString(),
                        "  §aJour : §3" + fk.getDay(),
                        "  §aHeure : §3" + fk.getFormattedTime(),
                        "  §aJoueurs : §f" + fk.getPlayers().size(),
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getStateInventory(Player player, String back) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        FKManager fk = FKManager.getCurrentGame();
        Inventory inv = Guis.getBaseInventory("§bStatus", 36, back,
                getMainItem(fk, null, "null"), null);

        inv.setItem(Utils.posOf(4, 1), Items.builder(Heads.CHAR_P.getSkull())
                .setName("§bStatus : §a" + fk.getState().name())
                .setLore(new ArrayList<>())
                .setCantClickOn(true)
                .build());

        boolean started = fk.getState() != FKManager.State.WAITING,
                ended = fk.getState() == FKManager.State.ENDED;
        ArrayList<String> no = new ArrayList<>(Arrays.asList("§8" + Guis.loreSeparator, "§cImpossible de revenir", "§c dans cet état.")),
                base = new ArrayList<>(Collections.singletonList("§8" + Guis.loreSeparator));

        inv.setItem(Utils.posOf(2, 1), Items.builder(Material.SUGAR)
                .setName("§a" + FKManager.State.WAITING.name())
                .setLore(started && !ended ? no : base)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, fk.getState() == FKManager.State.WAITING ? 0 : null)
                .setCantClickOn(true)
                .setCloseOnClick(true)
                .setGlobalCommandOnClick(ended ? "fk game reboot" : "null")
                .build());
        inv.setItem(Utils.posOf(3, 2), Items.builder(Material.GLOWSTONE_DUST)
                .setName("§a" + FKManager.State.RUNNING.name())
                .setLore(ended ? no : base)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, fk.getState() == FKManager.State.RUNNING ? 0 : null)
                .setCantClickOn(true)
                .setCloseOnClick(true)
                .setGlobalCommandOnClick(fk.getState() == FKManager.State.WAITING ? "fk game start" :
                        fk.getState() == FKManager.State.PAUSED ? "fk game resume" : "null")
                .build());
        inv.setItem(Utils.posOf(5, 2), Items.builder(Material.REDSTONE)
                .setName("§a" + FKManager.State.PAUSED.name())
                .setLore(ended || !started ? no : base)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, fk.getState() == FKManager.State.PAUSED ? 0 : null)
                .setCantClickOn(true)
                .setCloseOnClick(true)
                .setGlobalCommandOnClick(fk.getState() == FKManager.State.RUNNING ? "fk game pause" : "null")
                .build());
        inv.setItem(Utils.posOf(6, 1), Items.builder(Material.SULPHUR)
                .setName("§a" + FKManager.State.ENDED.name())
                .setLore(!started ? no : base)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, fk.getState() == FKManager.State.ENDED ? 0 : null)
                .setCantClickOn(true)
                .setCloseOnClick(true)
                .setGlobalCommandOnClick(started && !ended ? "fk game end" : "null")
                .build());

        return inv;
    }

    public static Inventory getInv(Player opener, String back) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        FKManager fk = FKManager.getCurrentGame();
        Inventory inv = Guis.getBaseInventory("§bFallen Kingdom X§f - §6" + fk.getId(), 54, back,
                getMainItem(fk, "Clic pour rafraichir", "fk"),
                GuiPlayers.getHead(opener == null ? null : opener.getName(), "Clic pour voir plus",
                        "fk players " + (opener == null ? null : opener.getName())));
        ArrayList<String> l = new ArrayList<>(new HashSet<String>() {{
            addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
            addAll(FKManager.getCurrentGame().getPlayers().stream().map(FKPlayer::getName).collect(Collectors.toList()));
        }});

        inv.setItem(Utils.posOf(1, 4), GuiPerm.getMainItem("Clic pour voir plus", "fk perm"));
        inv.setItem(Utils.posOf(3, 2), GuiDate.getMainItem("Clic pour voir plus", "fk date"));
        inv.setItem(Utils.posOf(4, 3), GuiPlayers.getMain(null, "Clic pour voir plus", "fk players",
                l.size(), (int) l.stream().filter(p -> Bukkit.getOfflinePlayer(p).isOnline()).count(), Bukkit.getMaxPlayers()));

        inv.setItem(Utils.posOf(1, 1), Items.builder(Heads.CHAR_P.getSkull())
                .setName("§bStatus : §a" + fk.getState().name())
                .setLore("§8" + Guis.loreSeparator, "§7Clic pour voir plus")
                .setCantClickOn(true)
                .setGlobalCommandOnClick("fk game state")
                .build());

        return inv;
    }

}
