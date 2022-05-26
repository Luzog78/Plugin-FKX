package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.commands.Cheat.Freeze;
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
        if (Freeze.frozen.contains(p.getUniqueId()))
            return;

        LivingEntity e = (LivingEntity) event.getRightClicked();
        if (!(event.getRightClicked() instanceof LivingEntity))
            return;
        DecimalFormat df = new DecimalFormat("0.0##");


        if (e instanceof Player)
            "".toLowerCase();

        else if (p.isSneaking())
            p.sendMessage("§aType: §b" + e.getType() + "§a,\nVie: §c" + e.getHealth() + "§7/" + e.getMaxHealth()
                    + "§a,\nVélocité: §f" + df.format(e.getVelocity().getX()) + " ; "
                    + df.format(e.getVelocity().getY()) + " ; " + df.format(e.getVelocity().getZ())
                    + "§a,\nUUID: §f" + e.getUniqueId());

        if (new CustomNBT(p.getItemInHand()).getBoolean("Mjolnir") && !p.isSneaking()) {
            p.getWorld().strikeLightning(e.getLocation());
            return;
        }
    }

}
