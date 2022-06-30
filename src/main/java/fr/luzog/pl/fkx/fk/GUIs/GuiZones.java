package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.fk.FKZone;
import fr.luzog.pl.fkx.utils.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiZones {

    public static ItemStack getZoneItem(FKZone z) {
        return Items.builder(Material.LONG_GRASS).setDurability((short) 2).setName("§2" + z.getId()).setLore("§8" + Guis.loreSeparator).build();
    }

}
