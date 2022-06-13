package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickHandler implements Listener {

    @EventHandler
    public static void onClick(InventoryClickEvent e) {
        FKPlayer fp = FKManager.getGlobalPlayer(e.getWhoClicked().getUniqueId());
        if (fp != null)
            fp.getStats().increaseClicksOnInventory();
    }

}
