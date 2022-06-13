package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.commands.Cheat.Freeze;
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

public class PlayerInteractHandler implements Listener {

    @EventHandler
    public static void onInteract(PlayerInteractEvent event) {
        Action a = event.getAction();
        Player p = event.getPlayer();

        FKPlayer fkp = FKManager.getGlobalPlayer(p.getUniqueId());
        if(fkp != null)
            fkp.getStats().increaseInteractions();

        if (Freeze.frozen.contains(p.getUniqueId()))
            return;

        ItemStack is = event.getItem();
        if(is == null)
            return;

        CustomNBT nbt = new CustomNBT(is);

        if(nbt.hasKey(Events.canInteractTag) && !nbt.getBoolean(Events.canInteractTag)){
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
