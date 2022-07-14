package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.CustomNBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;

public class PlayerInteractHandler implements Listener {

    @EventHandler
    public static void onInteract(PlayerInteractEvent event) {
        Action a = event.getAction();
        Player p = event.getPlayer();

        List<FKPlayer> fkps = FKManager.getGlobalPlayer(event.getPlayer().getName());

        for (FKPlayer fkp : fkps) {
            if (fkp == null)
                continue;
            fkp.getStats().increaseInteractions();

            if (fkp.getManager().getState() == FKManager.State.PAUSED
                    && !fkp.getTeam().getId().equals(fkp.getManager().getGods().getId())) {
                event.setCancelled(true);
                return;
            }
        }

        ItemStack is = event.getItem();
        if (is == null)
            return;

        CustomNBT nbt = new CustomNBT(is);

        if (nbt.hasKey(Events.canInteractTag) && !nbt.getBoolean(Events.canInteractTag)) {
            event.setCancelled(true);
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
