package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class InventoryClickHandler implements Listener {

    @EventHandler
    public static void onClick(InventoryClickEvent e) {
        List<FKPlayer> fps = FKManager.getGlobalPlayer(e.getWhoClicked().getUniqueId(), e.getWhoClicked().getName());

        for (FKPlayer fp : fps)
            if (fp != null)
                fp.getStats().increaseClicksOnInventory();
    }

}
