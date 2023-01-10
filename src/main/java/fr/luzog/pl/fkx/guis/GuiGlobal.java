package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GListener;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiGlobal {

    public static ItemStack getMainItem(GManager game, String lastLoreLine, String command) {
        if (game == null)
            return Items.l_gray();
        return Items.builder(game.getState() == GManager.State.ENDED ? Heads.MISC_ENDER_PEARL_PURPLE.getSkull()
                        : Heads.MISC_ENDER_PEARL_BLUE.getSkull())
                .setName("§f" + game.getId())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §aID : §6" + game.getId(),
                        "  §aStatus : §7" + game.getState().toString(),
                        "  §aJour : §3" + game.getDay(),
                        "  §aHeure : §3" + game.getFormattedTime(),
                        "  §aJoueurs : §f" + game.getPlayers().size(),
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getStateInventory(String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        GManager game = GManager.getCurrentGame();
        Inventory inv = Guis.getBaseInventory("§bStatus", 36, back,
                getMainItem(game, null, "null"), null);

        inv.setItem(Utils.posOf(4, 1), Items.builder(
                        game.getState() == GManager.State.PAUSED ? Heads.MISC_PLAY_BUTTON_RUBY.getSkull()
                                : game.getState() == GManager.State.RUNNING ? Heads.MISC_PLAY_BUTTON_EMERALD.getSkull()
                                : Heads.MISC_PLAY_BUTTON_SILVER.getSkull())
                .setName("§bStatus : §a" + game.getState().name())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour rafraîchir"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(Main.CMD + " game state")
                .build());

        boolean started = game.getState() != GManager.State.WAITING,
                ended = game.getState() == GManager.State.ENDED;
        String no = "§8" + Guis.loreSeparator + "\n§cImpossible de revenir\n§c dans cet état.\n \n§7Commande :\n§7/",
                already = "§8" + Guis.loreSeparator + "\n§aVous êtes déjà dans\n§a cet état.\n \n§7Commande :\n§7/",
                base = "§8" + Guis.loreSeparator + "\n§7Clic pour changer d'état\n \n§7Commande :\n§7/";

        inv.setItem(Utils.posOf(2, 1), Items.builder(Material.SUGAR)
                .setName("§a" + GManager.State.WAITING.name())
                .setLore((!started ? already : !ended ? no : base) + Main.CMD + " game reboot")
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, game.getState() == GManager.State.WAITING ? 0 : null)
                .setCantClickOn(true)
                .setCloseOnClick(started && !ended)
                .setGlobalCommandOnClick(started && ended ? Main.CMD + " game reboot" : "null")
                .build());
        inv.setItem(Utils.posOf(3, 2), Items.builder(Material.GLOWSTONE_DUST)
                .setName("§a" + GManager.State.RUNNING.name())
                .setLore((game.getState() == GManager.State.RUNNING ? already : base)
                        + (game.getState() == GManager.State.WAITING ? Main.CMD + " game start" : Main.CMD + " game resume"))
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, game.getState() == GManager.State.RUNNING ? 0 : null)
                .setCantClickOn(true)
                .setCloseOnClick(game.getState() != GManager.State.RUNNING)
                .setGlobalCommandOnClick(game.getState() == GManager.State.WAITING ? Main.CMD + " game start" :
                        game.getState() == GManager.State.PAUSED || game.getState() == GManager.State.ENDED ? Main.CMD + " game resume" : "null")
                .build());
        inv.setItem(Utils.posOf(5, 2), Items.builder(Material.REDSTONE)
                .setName("§a" + GManager.State.PAUSED.name())
                .setLore((game.getState() == GManager.State.PAUSED ? already
                        : game.getState() != GManager.State.RUNNING ? no : base) + Main.CMD + " game pause")
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, game.getState() == GManager.State.PAUSED ? 0 : null)
                .setCantClickOn(true)
                .setCloseOnClick(game.getState() == GManager.State.RUNNING)
                .setGlobalCommandOnClick(game.getState() == GManager.State.RUNNING ? Main.CMD + " game pause" : "null")
                .build());
        inv.setItem(Utils.posOf(6, 1), Items.builder(Material.SULPHUR)
                .setName("§a" + GManager.State.ENDED.name())
                .setLore((ended ? already : !started ? no : base) + Main.CMD + " game end")
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, game.getState() == GManager.State.ENDED ? 0 : null)
                .setCantClickOn(true)
                .setCloseOnClick(started && !ended)
                .setGlobalCommandOnClick(started && !ended ? Main.CMD + " game end" : "null")
                .build());

        return inv;
    }

    public static ItemStack getWarpsMainItem(GManager game, String lastLoreLine, String command) {
        if (game == null)
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
                        "  §fWarps : §6" + (4 + game.getTeams().size() + game.getNormalZones().size()),
                        "  §8  >  §31§8 Lobby",
                        "  §8  >  §31§8 Spawn",
                        "  §8  >  §32§8 Portails",
                        "  §8  >  §3" + game.getTeams().size() + "§8 Équipes",
                        "  §8  >  §3" + game.getNormalZones().size() + "§8 Zones",
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
                                + "m  §7-  §e" + (from == null ? "" : GListener.getOrientationChar(from.getYaw(),
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

    public static Inventory getWarpsInventory(GManager game, Location from, String back, String navigationBaseCommand, int page) {
        if (game == null)
            return Guis.getErrorInventory("Game null", back);
        return Guis.getPagedInventory("§fWarps", 54, back,
                getWarpsMainItem(game, "Clic pour rafraîchir", navigationBaseCommand + " " + page), null,
                navigationBaseCommand, page, new ArrayList<ItemStack>() {{
                    add(getWarpItem(new ItemStack(Material.GOLD_BLOCK), "§6Lobby", from, game.getLobby().getSpawn()));
                    add(getWarpItem(new ItemStack(Material.REDSTONE_BLOCK), "§4Spawn", from, game.getSpawn().getSpawn()));
                    addAll(game.getTeams().stream().map(t -> getWarpItem(GManager.getBanner(t.getColor()),
                            t.getColor() + t.getName(), from, t.getSpawn())).collect(Collectors.toList()));
                    add(getWarpItem(new ItemStack(Material.OBSIDIAN), "§bPortails du Nether",
                            from, game.getNether().getOverSpawn()));
                    add(getWarpItem(new ItemStack(Material.ENDER_PORTAL_FRAME), "§5Portails de l'End",
                            from, game.getEnd().getOverSpawn()));
                    addAll(game.getNormalZones().stream().map(z -> getWarpItem(
                            new ItemStack(Material.LONG_GRASS, 1, (short) 2), "§2" + z.getId(),
                            from, z.getSpawn())).collect(Collectors.toList()));
                }});
    }

    public static Inventory getInv(Player opener, String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        GManager game = GManager.getCurrentGame();
        Inventory inv = Guis.getBaseInventory("§b" + Main.SEASON + "§f - §6" + game.getId(), 54, back,
                getMainItem(game, "Clic pour rafraichir", Main.CMD),
                GuiPlayers.getHead(opener == null ? null : opener.getName(),
                        "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " players " + (opener == null ? null : opener.getName()),
                        Main.CMD + " players " + (opener == null ? null : opener.getName())));
        ArrayList<String> l = new ArrayList<>(new HashSet<String>() {{
            addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
            addAll(GManager.getCurrentGame().getPlayers().stream().map(GPlayer::getName).collect(Collectors.toList()));
        }});

        inv.setItem(Utils.posOf(1, 4), GuiPerm.getMainItem(
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " perm", Main.CMD + " perm"));
        inv.setItem(Utils.posOf(2, 4), GuiActivations.getMainItem(
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " activations", Main.CMD + " activations"));
        inv.setItem(Utils.posOf(2, 3), Items.builder(
                        game.getState() == GManager.State.PAUSED ? Heads.MISC_PLAY_BUTTON_RUBY.getSkull()
                                : game.getState() == GManager.State.RUNNING ? Heads.MISC_PLAY_BUTTON_EMERALD.getSkull()
                                : Heads.MISC_PLAY_BUTTON_SILVER.getSkull())
                .setName("§bStatus : §a" + game.getState().name())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour voir plus",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " game state"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(Main.CMD + " game state")
                .build());
        inv.setItem(Utils.posOf(3, 2), GuiDate.getMainItem(
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " date", Main.CMD + " date"));
        inv.setItem(Utils.posOf(4, 3), GuiPlayers.getMain(null,
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " players", Main.CMD + " players",
                l.size(), (int) l.stream().filter(p ->
                        Bukkit.getOfflinePlayer(p).isOnline()).count(), Bukkit.getMaxPlayers()));
        inv.setItem(Utils.posOf(5, 2), GuiLocks.getMainItem(
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " locks", Main.CMD + " locks"));
        inv.setItem(Utils.posOf(6, 3), GuiTeams.getMainItem(
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " teams", Main.CMD + " teams"));
        inv.setItem(Utils.posOf(6, 4), GuiPortals.getMainItem(
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " portal", Main.CMD + " portal"));
        inv.setItem(Utils.posOf(7, 4), GuiZones.getMainItem(
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " zone", Main.CMD + " zone"));

        inv.setItem(Utils.posOf(1, 1), GuiAd.getMainItem(
                "Clic pour voir plus\n \n§7Commande :\n§7/ad", "ad"));
        inv.setItem(Utils.posOf(7, 1), getWarpsMainItem(GManager.getCurrentGame(),
                "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " warp", Main.CMD + " warp"));

        return inv;
    }

}
