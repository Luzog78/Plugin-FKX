package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.fk.FKTeam;
import fr.luzog.pl.fkx.utils.CustomNBT;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Objects;

public class PlayerInteractHandler implements Listener {

    @EventHandler
    public static void onInteract(PlayerInteractEvent e) {
        FKPlayer fp;
        if (FKManager.getCurrentGame() == null
                || (fp = FKManager.getCurrentGame().getPlayer(e.getPlayer().getName(), false)) == null)
            return;

        if (FKManager.getCurrentGame().getState() != FKManager.State.RUNNING
                && !Objects.equals(fp.getTeamId(), FKTeam.GODS_ID)) {
            e.setCancelled(true);
            return;
        }

        Action a = e.getAction();
        Player p = e.getPlayer();

        if (a == Action.RIGHT_CLICK_BLOCK && e.hasBlock())
            if (e.getClickedBlock().getType() == Material.TNT
                    && !FKManager.getCurrentGame().getOptions().getAssaults().isActivated()) {
                p.sendMessage("§cLes assauts ne sont pas activés.");
                e.setCancelled(true);
                return;
            } else if (e.getClickedBlock().getType() == Material.CHEST) {
                if (Objects.equals(fp.getTeamId(), FKTeam.SPECS_ID)
                        && p.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                    p.sendMessage("§cVous ne pouvez pas ouvrir de coffre.");
                    e.setCancelled(true);
                    return;
                }
                boolean sneak = e.getPlayer().isSneaking();
                Block b = e.getClickedBlock();
                Location l = Utils.normalize(b.getLocation(), true);

                for (FKTeam t : FKManager.getCurrentGame().getTeams())
                    if (t.isInside(b.getLocation()))
                        if (!FKManager.getCurrentGame().getOptions().getAssaults().isActivated()) {
                            p.sendMessage("§7§oVous ouvrez un coffre §6" + t.getColor() + t.getName()
                                    + "§7§o, prenez garde avant le début des assauts...");
                            break;
                        } else if (!t.isEliminated()) {
                            if (!sneak && fp.getTeam() != null)
                                if (t.isEliminating())
                                    p.sendMessage("§cÉquipe déjà en élimination.");
                                else if (!fp.getTeam().everyoneIsNearOf(l) && !Objects.equals(fp.getTeamId(), FKTeam.GODS_ID))
                                    p.sendMessage("§cToute votre équipe n'est pas à proximité du coffre.");
                                else
                                    t.tryToEliminate(fp.getTeam(), l);
                            e.setCancelled(true);
                            return;
                        }
            }

        fp.getStats().increaseInteractions();

        if (!e.hasItem())
            return;

        ItemStack is = e.getItem();
        CustomNBT nbt = new CustomNBT(is);

        if (nbt.hasKey(Events.canInteractTag) && !nbt.getBoolean(Events.canInteractTag)) {
            e.setCancelled(true);
            p.sendMessage(Main.PREFIX + "§cImpossible d'interagir avec l'item.");
            return;
        }

        if (nbt.getBoolean("Mjolnir") && !p.isSneaking()) {
            p.getWorld().strikeLightning(p.getTargetBlock(new HashSet<Material>() {{
                add(Material.AIR);
            }}, 300).getLocation().add(0.0, 1, 0.0));
            return;
        }
    }

}
