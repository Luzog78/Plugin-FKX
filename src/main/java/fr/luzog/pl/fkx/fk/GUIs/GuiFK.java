package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.fk.FKListener;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.Heads;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
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
                .setLore(base)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, fk.getState() == FKManager.State.RUNNING ? 0 : null)
                .setCantClickOn(true)
                .setCloseOnClick(true)
                .setGlobalCommandOnClick(fk.getState() == FKManager.State.WAITING ? "fk game start" :
                        fk.getState() == FKManager.State.PAUSED || fk.getState() == FKManager.State.ENDED ? "fk game resume" : "null")
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

    public static ItemStack getWarpsMainItem(FKManager fk, String lastLoreLine, String command) {
        if (fk == null)
            return Items.builder(Material.PAPER)
                    .setName("§fWarps")
                    .setLore(
                            "§8" + Guis.loreSeparator
                                    + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                    )
                    .setCantClickOn(true)
                    .setGlobalCommandOnClick(command)
                    .build();
        return Items.builder(Material.PAPER)
                .setName("§fWarps")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §fWarps : §6" + (4 + fk.getTeams().size() + fk.getNormalZones().size()),
                        "  §8  >  §31§8 Lobby",
                        "  §8  >  §31§8 Spawn",
                        "  §8  >  §32§8 Portails",
                        "  §8  >  §3" + fk.getTeams().size() + "§8 Équipes",
                        "  §8  >  §3" + fk.getNormalZones().size() + "§8 Zones",
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getWarpItem(ItemStack base, String name, Location from, Location loc) {
        return Items.builder(base)
                .setName("§f" + name)
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §8Distance : §6" + (Utils.safeDistance(from, loc, true, 2, 1)
                                + "m  §7-  §e" + (from == null ? "" : FKListener.getOrientationChar(from.getYaw(),
                                from.getX(), from.getZ(), loc.getX(), loc.getZ(), 1))),
                        " ",
                        "  §8Position :",
                        "  §8  > X : §f" + (loc == null ? "§cnull" : loc.getX()),
                        "  §8  > Y : §f" + (loc == null ? "§cnull" : loc.getY()),
                        "  §8  > Z : §f" + (loc == null ? "§cnull" : loc.getZ()),
                        "  §8  > Yaw : §f" + (loc == null ? "§cnull" : loc.getYaw()),
                        "  §8  > Pitch : §f" + (loc == null ? "§cnull" : loc.getPitch()),
                        "  §8  > Monde : §f" + (loc == null ? "§cnull" : Utils.getFormattedWorld(loc.getWorld().getName())),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clique pour aller à §f" + name
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(loc == null ? "" : "tp " + loc.getX() + " " + loc.getY() + " " + loc.getZ()
                        + " " + loc.getYaw() + " " + loc.getPitch() + " " + loc.getWorld().getName())
                .build();
    }

    public static Inventory getWarpsInventory(FKManager fk, Location from, String back, String navigationBaseCommand, int page) {
        if (fk == null)
            return Guis.getErrorInventory("Game null", back);
        return Guis.getPagedInventory("§fWarps", 54, back,
                getWarpsMainItem(fk, "Clic pour rafraîchir", navigationBaseCommand + " " + page), null,
                navigationBaseCommand, page, new ArrayList<ItemStack>() {{
                    add(getWarpItem(new ItemStack(Material.GOLD_BLOCK), "§6Lobby", from, fk.getLobby().getSpawn()));
                    add(getWarpItem(new ItemStack(Material.REDSTONE_BLOCK), "§4Spawn", from, fk.getSpawn().getSpawn()));
                    addAll(fk.getTeams().stream().map(t -> getWarpItem(FKManager.getBanner(t.getColor()),
                            t.getColor() + t.getName(), from, t.getSpawn())).collect(Collectors.toList()));
                    add(getWarpItem(new ItemStack(Material.OBSIDIAN), "§bPortails du Nether",
                            from, fk.getNether().getOverSpawn()));
                    add(getWarpItem(new ItemStack(Material.ENDER_PORTAL_FRAME), "§5Portails de l'End",
                            from, fk.getEnd().getOverSpawn()));
                    addAll(fk.getNormalZones().stream().map(z -> getWarpItem(
                            new ItemStack(Material.LONG_GRASS, 1, (short) 2), "§2" + z.getId(),
                            from, z.getSpawn())).collect(Collectors.toList()));
                }});
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
        inv.setItem(Utils.posOf(6, 3), GuiTeams.getMainItem("Clic pour voir plus", "fk teams"));
        inv.setItem(Utils.posOf(5, 2), GuiLocks.getMainItem("Clic pour voir plus", "fk locks"));

        inv.setItem(Utils.posOf(1, 1), Items.builder(Heads.CHAR_P.getSkull())
                .setName("§bStatus : §a" + fk.getState().name())
                .setLore("§8" + Guis.loreSeparator, "§7Clic pour voir plus")
                .setCantClickOn(true)
                .setGlobalCommandOnClick("fk game state")
                .build());
        inv.setItem(Utils.posOf(7, 1), getWarpsMainItem(FKManager.getCurrentGame(),
                "Clic pour voir plus", "fk warp"));

        return inv;
    }

}
