package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKAuth;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class BlockPlaceHandler implements Listener {

    @EventHandler
    public static void onPlaceBlock(BlockPlaceEvent e) {
        List<FKPlayer> fps = FKManager.getGlobalPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName());
        if (fps.isEmpty()) {
            e.setCancelled(true);
            return;
        }

        for (FKPlayer fp : fps) {
            if (fp == null || !fp.hasAuthorization(Events.specialMat.contains(e.getBlock().getType()) ? FKAuth.Type.PLACESPE : FKAuth.Type.PLACE, Utils.normalize(e.getBlock().getLocation()))) {
                e.setCancelled(true);
                return;
            }

            if (Events.unplaceableMat.contains(e.getBlock().getType())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Main.PREFIX + "§cBlock Imposable.");
                return;
            }

            fp.getStats().increaseBlocksPlaced();
        }
    }

}
