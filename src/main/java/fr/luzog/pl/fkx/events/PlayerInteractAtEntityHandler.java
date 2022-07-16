package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.commands.Cheat.Freeze;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.fk.FKTeam;
import fr.luzog.pl.fkx.utils.CustomNBT;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.text.DecimalFormat;

public class PlayerInteractAtEntityHandler implements Listener {

    @EventHandler
    public static void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        if (Freeze.frozen.contains(p.getName()))
            return;

        LivingEntity e = (LivingEntity) event.getRightClicked();
        if (!(event.getRightClicked() instanceof LivingEntity))
            return;
        DecimalFormat df = new DecimalFormat("0.0##");


        if (e instanceof Player) {

        } else if (p.isSneaking()) {
//            p.sendMessage("§aType: §b" + e.getType() + "§a,\nVie: §c" + e.getHealth() + "§7/" + e.getMaxHealth()
//                    + "§a,\nVélocité: §f" + df.format(e.getVelocity().getX()) + " ; "
//                    + df.format(e.getVelocity().getY()) + " ; " + df.format(e.getVelocity().getZ())
//                    + "§a,\nUUID: §f" + e.getUniqueId());
        }

        if (new CustomNBT(p.getItemInHand()).getBoolean("Mjolnir") && !p.isSneaking()) {
            p.getWorld().strikeLightning(e.getLocation());
            return;
        }

        FKPlayer fp;
        FKTeam t;
        if (FKManager.getCurrentGame() != null && e.hasMetadata(FKTeam.GUARDIAN_TAG)
                && (fp = FKManager.getCurrentGame().getPlayer(p.getName(), false)) != null
                && fp.getTeam() != null && (t = FKManager.getCurrentGame().getTeam(
                e.getMetadata(FKTeam.GUARDIAN_TAG).get(0).asString())) != null) {
            if (fp.getTeam().getId().equals(t.getId())) {
                p.sendMessage("§aVoici votre §6Gardien des Coffres§a. Durant l'aventure, vous devrez partir en exploration"
                        + " et en guerre contre les autres joueurs pour obtenir des ressources. Avec celles-ci, vous"
                        + " construirez une défense impénétrable afin de protéger votre §6Gardien§a."
                        + "\n§a§lNotez bien§a que pour vous éliminer, les autres équipes n'ont qu'à trouver et assaillir"
                        + " votre  §6Gardien§a, §c100 secondes§a suffisent... alors allez le cacher plus en profondeurs"
                        + " dans votre salle des coffres protégée."
                        + "\n§7 > Clic Droit pour avoir des info."
                        /*+ "\n§7 > Sneak + Clic Gauche pour réinitialiser la position."
                        + "\n§7 > Sneak + Clic Droit pour redéfinir sa position. (Se tp à l'invocateur)"*/
                        + "\n§7 > Pour changer sa position, appelez un §f" + FKManager.getCurrentGame().getGods().getColor()
                        + FKManager.getCurrentGame().getGods().getName() + "§a.");
            } else {
                if (t.isEliminated())
                    p.sendMessage("§cCette équipe a déjà été éliminée.");
                else if (t.isEliminating())
                    p.sendMessage("§cCette équipe est déjà en train de se faire éliminer.");
                else if(t.getManager().getState() != FKManager.State.RUNNING)
                    p.sendMessage("§cImpossible de se faire éliminer dans cette phase.");
                else if(!t.getManager().getOptions().getAssaults().isActivated())
                    p.sendMessage("§cLes Assauts ne sont pas encore activés.");
                else
                    t.tryToEliminate(fp.getTeam());
            }
            event.setCancelled(true);
            return;
        }

        if (e.hasMetadata(FKTeam.GUARDIAN_TAG)) {
            event.setCancelled(true);
            return;
        }
    }

}
