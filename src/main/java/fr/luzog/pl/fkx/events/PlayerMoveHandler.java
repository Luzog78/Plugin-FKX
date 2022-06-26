package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.fk.FKException;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.fk.FKZone;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class PlayerMoveHandler implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        List<FKPlayer> fkps = FKManager.getGlobalPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName());

        for (FKPlayer p : fkps) {
            if (p == null)
                return;

            if (p.getManager().getState() == FKManager.State.PAUSED && !p.getTeam().getId().equals(p.getManager().getGods().getId())
                    && (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getFrom().getZ())) {
                e.getPlayer().teleport(e.getFrom());
                return;
            }

            if (!e.isCancelled()) {
                p.getStats().increaseTraveledDistance(e.getTo().distance(e.getFrom()));
                if (e.getFrom().getY() + 0.419 < e.getTo().getY())
                    p.getStats().increaseJumps();
            }

//        FKZone from = p.getZone(e.getFrom()), to = p.getZone(e.getTo());
//        if ((from == null && to != null) || (from != null && to == null) ||
//                (from != null && to != null && !from.getId().equals(to.getId())))
//            e.getPlayer().sendMessage("§aVous entrez dans §f" + (to == null ? "null" : to.getId()) + "§a !");

            if (!e.isCancelled()) {
                p.getManager().getNether().tryToTeleport(e.getPlayer(), p.getManager(), true);
                p.getManager().getEnd().tryToTeleport(e.getPlayer(), p.getManager(), true);
            }
        }
    }

}
