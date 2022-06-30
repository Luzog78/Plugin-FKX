package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.utils.Heads;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class GuiFK {

    public static ItemStack getMainItem(FKManager fk, String lastLoreLine, String command) {
        if(fk == null)
            return Items.l_gray();
        return Items.builder(fk.getState() == FKManager.State.ENDED ? Heads.MISC_PURPLE_ORB.getSkull() :  Heads.MISC_BLUE_ORB.getSkull())
                .setName("§f" + fk.getId())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §aID : §6" + fk.getId(),
                        "  §aStatus : §7" + fk.getState().toString(),
                        "  §aJour : §3" + fk.getDay(),
                        "  §aHeure : §f" + fk.getFormattedTime(),
                        "  §aJoueurs : §f" + fk.getPlayers().size(),
                        " ",
                        "§8" + Guis.loreSeparator
                        + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getStateInventory(FKManager fk, Player player) {

        // TODO

    }

    public static Inventory getInv(FKManager fk, @Nullable Player p) {
        return getInv(fk, p, null);
    }

    public static Inventory getInv(FKManager fk, @Nullable Player p, String back) {
        Inventory inv = Guis.getBaseInventory("§bFallen Kingdom X§f - §6" + fk.getId(), 54, back,
                getMainItem(fk, "Clic pour rafraichir", "fk game gui " + fk.getId()),
                GuiPlayers.getHead(p == null ? null : p.getName(), "Clic pour voir plus", "fk players " + (p == null ? null : p.getName())));

        inv.setItem(Utils.posOf(1, 4), GuiPerm.getMainItem("Clic pour voir plus", "fk perm"));

        return inv;
    }

}
