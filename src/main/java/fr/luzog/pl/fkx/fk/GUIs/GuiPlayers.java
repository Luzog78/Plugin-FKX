package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.events.Events;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.CustomNBT;
import fr.luzog.pl.fkx.utils.Heads;
import fr.luzog.pl.fkx.utils.Items;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class GuiPlayers {

    public static ItemStack getHead(Player p, @Nullable String lastLoreLine, @Nullable String command) {
        if (p == null)
            return new CustomNBT(Items.i(Material.SKULL, 1, (byte) 3, "§c???")).set(Events.cantClickOnTag, true).build();
        FKPlayer fkp = FKManager.getCurrentGame().getPlayer(p.getName(), false);
        DecimalFormat df = new DecimalFormat("0.00");
        CustomNBT nbt = Items.builder(Heads.getSkullOf(p.getName(), p.getDisplayName()))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§8Dans le jeu : §6" + (fkp == null ? "§cAucun" : fkp.getManager().getId()),
                        " ",
                        "  §aNom : §f" + (fkp == null ? p.getName() : fkp.getName()),
                        "  §aUUID : §7" + (fkp == null ? p.getUniqueId() : fkp.getLastUuid()),
                        "  §aTeam : §f" + (fkp == null ? "§cHors Jeu" : fkp.getTeam() == null ? "§4§lAucune" : fkp.getTeam().getName()),
                        "  §aNom d'Affichage : §f" + (fkp == null ? p.getDisplayName() : fkp.getDisplayName()),
                        "    ---",
                        "  §aVie : §c" + df.format(p.getFoodLevel()) + "§7 /20.0",
                        "  §aNourriture : §a" + df.format(p.getFoodLevel()) + "§7 /20.0",
                        "  §aSaturation : §e" + df.format(p.getSaturation()) + "§7 /20.0",
                        "    ---",
                        "  §aLocalisation : §f" + Utils.locToString(p.getLocation(), true, true, false),
                        "  §aMonde : §f" + (p.getWorld().getName().equalsIgnoreCase("world") ? "§2OverWorld"
                                : p.getWorld().getName().equalsIgnoreCase("world_nether") ? "§dNether"
                                : p.getWorld().getName().equalsIgnoreCase("world_the_end") ? "§5End"
                                : p.getWorld().getName()),
                        "  §aZone : §f" + (fkp == null ? "§cHors Jeu" : fkp.getZone() == null ? "§cAucune" : fkp.getZone().getId() + "§7 (" + fkp.getZone().getType() + ")"),
                        " ",
                        "§8" + Guis.loreSeparator + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .getNBT()
                .set(Events.cantClickOnTag, true);
        if (command != null)
            nbt.set(Events.exeTag, command);
        return nbt.build();
    }

}
