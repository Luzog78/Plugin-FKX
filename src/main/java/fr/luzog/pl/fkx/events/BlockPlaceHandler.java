package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPermissions;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class BlockPlaceHandler implements Listener {

    @EventHandler
    public static void onPlaceBlock(BlockPlaceEvent e) {
        List<GPlayer> fps = GManager.getGlobalPlayer(e.getPlayer().getName());
        if (fps.isEmpty()) {
            e.setCancelled(true);
            return;
        }

        for (GPlayer fp : fps) {
            if (fp == null || !fp.hasPermission(Events.specialMat.contains(e.getBlock().getType()) ?
                    GPermissions.Type.PLACESPE : GPermissions.Type.PLACE, Utils.normalize(e.getBlock().getLocation()))) {
                e.setCancelled(true);
                return;
            }

            if (Events.unplaceableMat.contains(e.getBlock().getType())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Main.PREFIX + "§cBlock Imposable.");
                return;
            }

            if (fp.getTeam() != null && fp.getTeam().isInside(e.getBlock().getLocation())
                    && (e.getBlock().getType() == Material.CHEST || e.getBlock().getType() == Material.TRAPPED_CHEST)
                    && (e.getBlock().getLocation().getBlockY() > fp.getTeam().getSpawn().getBlockY() + 20
                    || e.getBlock().getLocation().getBlockY() < fp.getTeam().getSpawn().getBlockY() - 20)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("§cVous ne pouvez placer de coffre qu'entre Y +20 et Y -20 blocks"
                        + " dans votre base par rapport à votre spawn.");
                return;
            }

            fp.getStats().increaseBlocksPlaced();
        }
    }

}
