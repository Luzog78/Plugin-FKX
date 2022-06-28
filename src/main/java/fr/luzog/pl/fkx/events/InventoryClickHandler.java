package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.CustomNBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class InventoryClickHandler implements Listener {

    @EventHandler
    public static void onClick(InventoryClickEvent e) {
        List<FKPlayer> fps = FKManager.getGlobalPlayer(e.getWhoClicked().getName());

        for (FKPlayer fp : fps)
            if (fp != null)
                fp.getStats().increaseClicksOnInventory();

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR)
            if (e.getWhoClicked() instanceof Player) {
                CustomNBT nbt = new CustomNBT(e.getCurrentItem());
                if (nbt.hasKey(Events.closeTag) && nbt.getBoolean(Events.closeTag))
                    e.getWhoClicked().closeInventory();
                if (nbt.hasKey(Events.cantClickOnTag) && nbt.getBoolean(Events.cantClickOnTag))
                    e.setCancelled(true);
                if (nbt.hasKey(Events.exeTag))
                    ((Player) e.getWhoClicked()).performCommand(nbt.getString(Events.exeTag));
            }
    }

}
