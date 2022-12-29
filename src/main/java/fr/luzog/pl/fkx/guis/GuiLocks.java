package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GListener;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPickableLocks;
import fr.luzog.pl.fkx.utils.*;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GuiLocks {

    public static ItemStack getMainItem(String lastLoreLine, String command) {
        if (GManager.getCurrentGame() == null || Main.world == null)
            return Items.builder(Material.CHEST)
                    .setName("§9Locks")
                    .setLore("§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine))
                    .setCantClickOn(true)
                    .setGlobalCommandOnClick(command)
                    .build();
        GManager game = GManager.getCurrentGame();
        ArrayList<GPickableLocks.Lock> pl = game.getPickableLocks().getPickableLocks();
        return Items.builder(Material.CHEST)
                .setName("§9Locks")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §9Nombre de locks : §f" + pl.size(),
                        "  §8  > Crochetables : §b" + pl.stream().filter(l ->
                                l.isPickable() && !l.isPicked() && game.getDay() >= l.getLevel()).count(),
                        "  §8  > Crochetés : §2" + pl.stream().filter(l -> l.isPickable() && l.isPicked()).count(),
                        "  §8  > Inaccessibles : §4" + pl.stream().filter(l -> !l.isPickable()).count(),
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getLockItem(GPickableLocks.Lock lock, Location from,
                                        String lastLoreLine, String command) {
        return Items.builder(lock.getLocation().getBlock() == null
                        || lock.getLocation().getBlock().getType() == Material.AIR ?
                        Material.BARRIER : lock.getLocation().getBlock().getType())
                .setName("§9Coffre crochetable : §b" + lock.getId())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §9Niveau : §f" + lock.getLevel(),
                        "  §9Status : §f" + (lock.isPickable() ? lock.isPicked() ? "§cCrocheté" : "§aCrochetable" : "§4Inaccessible"),
                        "  §9CoolDown : §7" + (lock.getOriginalCoolDown() / 20.0) + "s",
                        "  §9Broadcast : §7" + (lock.isAutoBC() ?
                                "§2§l" + SpecialChars.YES + "§2 Activé §7§o(Jour " + lock.getLevel() + ")"
                                : "§4§l" + SpecialChars.NO + "§4 Désactivé"),
                        " ",
                        "  §9---",
                        " ",
                        "  §9Distance : §6" + (Utils.safeDistance(from, lock.getLocation(),
                                true, 2, GPickableLocks.RADIUS)
                                + "m  §7-  §e" + (from == null ? "" : GListener.getOrientationChar(
                                from.getYaw(), from.getX(), from.getZ(),
                                lock.getLocation().getX(), lock.getLocation().getZ(),
                                GPickableLocks.RADIUS))),
                        " ",
                        "  §9Position :",
                        "  §8  > X : §f" + (lock.getLocation() == null ? "§cnull" : lock.getLocation().getX()),
                        "  §8  > Y : §f" + (lock.getLocation() == null ? "§cnull" : lock.getLocation().getY()),
                        "  §8  > Z : §f" + (lock.getLocation() == null ? "§cnull" : lock.getLocation().getZ()),
                        " ",
                        "  §9Monde : §f" + (lock.getLocation() == null ? "§cnull"
                                : Utils.getFormattedWorld(lock.getLocation().getWorld().getName())),
                        " ",
                        "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getLockInventory(GPickableLocks.Lock lock, Location from, String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("No game running", back);
        Inventory inv = Guis.getBaseInventory("§9Coffres Crochetables §f- §b" + lock.getId(), 54, back,
                getMainItem("§7Clic pour rafraîchir", Main.CMD + " locks " + lock.getId()), null);

        inv.setItem(Utils.posOf(4, 1), Items.builder(getLockItem(
                        lock, from, null, "null"))
                .setName("")
                .addLore(
                        "§7Clic Gauche pour se téléporter",
                        "§7Clic Droit pour " + (lock.isAutoBC() ? "§cdésactiver" : "§aactiver"),
                        "§7 l'Auto Broadcast (message auto)",
                        "§7Clic Molette pour broadcast",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " auto-bc §8(§atrue §8|§c false§8)",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " broadcast"
                )
                .setLeftRightCommandOnClick(
                        "tp " + lock.getLocation().getX()
                                + " " + lock.getLocation().getY() + " " + lock.getLocation().getZ()
                                + " " + lock.getLocation().getWorld().getName() + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " auto-bc " + (lock.isAutoBC() ? "false" : "true")
                                + "\n" + Main.CMD + " locks " + lock.getId()
                )
                .setMiddleCommandOnClick(Main.CMD + " locks " + lock.getId() + " broadcast" + "\n" + Main.CMD + " locks " + lock.getId())
                .setCantClickOn(true)
                .build());

        inv.setItem(Utils.posOf(2, 2), Items.builder(Material.NAME_TAG)
                .setName("§9Identifiant : §b" + lock.getId())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour modifier",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " id §f<id>"
                )
                .setLeftRightCommandOnClick(
                        "input " + Main.CMD + " locks " + lock.getId() + " id %s%n" + Main.CMD + " locks",
                        Main.CMD + " locks " + lock.getId()
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(4, 3), Items.builder(Material.TRIPWIRE_HOOK)
                .setName("§9Verrouillage : §f" + (lock.isPicked() ?
                        "§2§l" + SpecialChars.YES + "§2 Déverrouillé"
                        : "§4§l" + SpecialChars.NO + "§4 Verrouillé"))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour " + (lock.isPicked() ? "§4verrouiller" : "§2déverrouiller"),
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " §8(§4lock §8|§2 unlock§8)"
                )
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, lock.isPicked() ? null : 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .setGlobalCommandOnClick(Main.CMD + " locks " + lock.getId() + " " + (lock.isPicked() ? "lock" : "unlock")
                        + "\n" + Main.CMD + " locks " + lock.getId())
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(6, 2), Items.builder(Material.INK_SACK)
                .setDurability((short) (15 - (lock.isPickable() ? DyeColor.LIME.getData() : DyeColor.GRAY.getData())))
                .setName("§9Accessibilité : §f" + (lock.isPickable() ?
                        "§2§l" + SpecialChars.YES + "§2 Accessible"
                        : "§4§l" + SpecialChars.NO + "§4 Inaccessible"))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour rendre " + (lock.isPickable() ? "§4inaccessible" : "§2accessible"),
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " pickable §8(§4false §8|§2 true§8)"
                )
                .setGlobalCommandOnClick(Main.CMD + " locks " + lock.getId() + " pickable " + (lock.isPickable() ? "false" : "true")
                        + "\n" + Main.CMD + " locks " + lock.getId())
                .setCantClickOn(true)
                .build());

        inv.setItem(Utils.posOf(1, 4), Items.builder(Material.STAINED_GLASS_PANE)
                .setDurability(DyeColor.RED.getData())
                .setName("§cDécrémenter le Niveau (§f" + lock.getLevel() + "§c)")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour niv §4- 1",
                        "§7Clic Droit pour niv §4- 2",
                        "§7  (Shift + Gauche pour niv §4- 5§7)",
                        "§7  (Shift + Droit pour niv §4- 10§7)",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " level §f<niveau>"
                )
                .setLeftRightShiftCommandOnClick(
                        Main.CMD + " locks " + lock.getId() + " level " + Math.max(lock.getLevel() - 1, 0) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " level " + Math.max(lock.getLevel() - 5, 0) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " level " + Math.max(lock.getLevel() - 2, 0) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " level " + Math.max(lock.getLevel() - 10, 0) + "\n" + Main.CMD + " locks " + lock.getId()
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(2, 4), Items.builder(Material.NETHER_STAR)
                .setName("§9Niveau : §f" + lock.getLevel())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour §fdéfinir§7 le niveau",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " level §f<niveau>"
                )
                .setGlobalCommandOnClick("input " + Main.CMD + " locks " + lock.getId() + " level %d%n" + Main.CMD + " locks " + lock.getId())
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(3, 4), Items.builder(Material.STAINED_GLASS_PANE)
                .setDurability(DyeColor.LIME.getData())
                .setName("§aIncrémenter le Niveau (§f" + lock.getLevel() + "§a)")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour niv §2+ 1",
                        "§7Clic Droit pour niv §2+ 2",
                        "§7  (Shift + Gauche pour niv §2+ 5§7)",
                        "§7  (Shift + Droit pour niv §2+ 10§7)",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " level §f<niveau>"
                )
                .setLeftRightShiftCommandOnClick(
                        Main.CMD + " locks " + lock.getId() + " level " + (lock.getLevel() + 1) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " level " + (lock.getLevel() + 5) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " level " + (lock.getLevel() + 2) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " level " + (lock.getLevel() + 10) + "\n" + Main.CMD + " locks " + lock.getId()
                )
                .setCantClickOn(true)
                .build());

        inv.setItem(Utils.posOf(5, 4), Items.builder(Material.STAINED_GLASS_PANE)
                .setDurability(DyeColor.RED.getData())
                .setName("§cDécrémenter le Délai (§7" + (lock.getOriginalCoolDown() / 20.0) + "s§c)")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour délai §4- 1 §7sec",
                        "§7Clic Droit pour délai §4- 5 §7sec",
                        "§7  (Shift + Gauche pour délai §4- 10 §7sec)",
                        "§7  (Shift + Droit pour délai §4- 15 §7sec)",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " cooldown §f<délai>"
                )
                .setLeftRightShiftCommandOnClick(
                        Main.CMD + " locks " + lock.getId() + " cooldown "
                                + Math.max((lock.getOriginalCoolDown() / 20.0) - 1, 0.05) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " cooldown "
                                + Math.max((lock.getOriginalCoolDown() / 20.0) - 10, 0.05) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " cooldown "
                                + Math.max((lock.getOriginalCoolDown() / 20.0) - 5, 0.05) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " cooldown "
                                + Math.max((lock.getOriginalCoolDown() / 20.0) - 15, 0.05) + "\n" + Main.CMD + " locks " + lock.getId()
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(6, 4), Items.builder(Material.WATCH)
                .setName("§9Délai : §7" + (lock.getOriginalCoolDown() / 20.0) + "s")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour §fdéfinir§7 le délai",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " cooldown §f<délai>"
                )
                .setGlobalCommandOnClick("input " + Main.CMD + " locks " + lock.getId() + " cooldown %d{0,}%n" + Main.CMD + " locks " + lock.getId())
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(7, 4), Items.builder(Material.STAINED_GLASS_PANE)
                .setDurability(DyeColor.LIME.getData())
                .setName("§aIncrémenter le Délai (§7" + (lock.getOriginalCoolDown() / 20.0) + "s§a)")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour délai §2+ 1 §7sec",
                        "§7Clic Droit pour délai §2+ 5 §7sec",
                        "§7  (Shift + Gauche pour délai §2+ 10 §7sec)",
                        "§7  (Shift + Droit pour délai §2+ 15 §7sec)",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " cooldown §f<délai>"
                )
                .setLeftRightShiftCommandOnClick(
                        Main.CMD + " locks " + lock.getId() + " cooldown "
                                + ((lock.getOriginalCoolDown() / 20.0) + 1) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " cooldown "
                                + ((lock.getOriginalCoolDown() / 20.0) + 10) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " cooldown "
                                + ((lock.getOriginalCoolDown() / 20.0) + 5) + "\n" + Main.CMD + " locks " + lock.getId(),
                        Main.CMD + " locks " + lock.getId() + " cooldown "
                                + ((lock.getOriginalCoolDown() / 20.0) + 15) + "\n" + Main.CMD + " locks " + lock.getId()
                )
                .setCantClickOn(true)
                .build());

        inv.setItem(Utils.posOf(4, 5), Items.builder(Material.STAINED_GLASS_PANE)
                .setDurability(DyeColor.RED.getData())
                .setName("§cSupprimer le Coffre Crochetable")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §cCette action est irréversible§8!",
                        "  §cElle ne détruit pas le coffre,",
                        "  §c mais elle retire juste toutes",
                        "  §c les données du coffre \"pillable\".",
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Molette pour supprimer le coffre",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks " + lock.getId() + " destroy"
                )
                .setMiddleCommandOnClick(Main.CMD + " locks " + lock.getId() + " destroy\n" + back)
                .setCloseOnClick(true)
                .setCantClickOn(true)
                .build());

        return inv;
    }

    public static Inventory getMainInventory(Location from, Location target, String back,
                                             String navigationBaseCommand, int page) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("Game null", back);

        ItemStack targetIs = target == null || target.getBlock() == null || target.getBlock().getType() == Material.AIR ?
                Items.builder(Material.BARRIER).setName("§cAucune cible").setLore("§8" + Guis.loreSeparator)
                        .setCantClickOn(true).build() : null;

        if (targetIs == null) {
            assert target != null;
            GPickableLocks.Lock lock = GManager.getCurrentGame().getPickableLocks().isPickableLock(target) ?
                    GManager.getCurrentGame().getPickableLocks().getLock(target) : null;
            TileEntity te = ((CraftWorld) target.getWorld()).getTileEntityAt(target.getBlockX(),
                    target.getBlockY(), target.getBlockZ());
            targetIs = lock == null ? Items.builder(target.getBlock().getType())
                    .setName((te == null ? "§4" : "§9") + "Cible : " + (te == null ? "§c" : "§b") + target.getBlock().getType().name())
                    .setLore(
                            "§8" + Guis.loreSeparator,
                            " ",
                            "  " + (te == null ? "§4" : "§9") + "Position :",
                            "  §8  > X : §f" + target.getBlock().getX(),
                            "  §8  > Y : §f" + target.getBlock().getY(),
                            "  §8  > Z : §f" + target.getBlock().getZ(),
                            " ",
                            "  " + (te == null ? "§4" : "§9") + "Monde : §f"
                                    + Utils.getFormattedWorld(target.getWorld().getName()),
                            " ",
                            "§8" + Guis.loreSeparator,
                            (te == null ? "§cImpossible de créer un Lock" : "§7Clic pour créer un Lock"
                                    + "\n \n§7Commande :\n§7/" + Main.CMD + " locks create 0\n§7 " + target.getBlock().getX()
                                    + " " + target.getBlock().getY() + " " + target.getBlock().getZ()
                                    + " " + target.getWorld().getName())
                    )
                    .setGlobalCommandOnClick(
                            te == null ? "null" : Main.CMD + " locks create 0 " + target.getBlock().getX()
                                    + " " + target.getBlock().getY() + " " + target.getBlock().getZ()
                                    + " " + target.getWorld().getName() + "\n" + navigationBaseCommand + " " + page
                    )
                    .setCantClickOn(true)
                    .build() : getLockItem(lock, from, "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " locks "
                    + lock.getId(), Main.CMD + " locks " + lock.getId());
        }

        Inventory inv = Guis.getPagedInventory("§9Coffres Crochetables", 54, back,
                getMainItem("Clic pour rafraîchir", navigationBaseCommand + " " + page),
                targetIs, navigationBaseCommand, page, GManager.getCurrentGame().getPickableLocks()
                        .getPickableLocks().stream().map(l ->
                                getLockItem(l, from, "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " locks "
                                        + l.getId(), Main.CMD + " locks " + l.getId())).collect(Collectors.toList()));
        inv.setItem(Utils.posOf(5, 5), Items.builder(GPickableLocks.getMasterKey())
                .addLore(
                        "§8" + Guis.loreSeparator,
                        "§7Dans l'inventaire :",
                        "§7Clic pour donner la clé",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " locks tool"
                )
                .setGlobalCommandOnClick(Main.CMD + " locks tool")
                .setCantClickOn(true)
                .build());
        return inv;
    }
}
