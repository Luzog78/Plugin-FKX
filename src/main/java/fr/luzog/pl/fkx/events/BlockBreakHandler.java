package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKAuth;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class BlockBreakHandler implements Listener {

    public static void dropNormally(Location loc, ItemStack is) {
        dropNormally(loc, Collections.singletonList(is));
    }

    public static void dropNormally(Location loc, Collection<ItemStack> is) {
        Location l = Utils.normalize(loc);
        is.forEach(i -> l.getWorld().dropItemNaturally(l, i));
    }

    @EventHandler
    public static void onBreakBlock(BlockBreakEvent e) {
        FKPlayer fp = FKManager.getGlobalPlayer(e.getPlayer().getUniqueId());
        if (fp == null || !fp.hasAuthorization(Events.specialMat.contains(e.getBlock().getType()) ? FKAuth.Type.BREAKSPE : FKAuth.Type.BREAK, Utils.normalize(e.getBlock().getLocation()))) {
            e.setCancelled(true);
            return;
        }

        if (Events.unbreakableMat.contains(e.getBlock().getType())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Main.PREFIX + "§cBlock Incassable.");
            return;
        }

        if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR)
            return;

        int chanceLvl = e.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        boolean silkTouch = e.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0;

        if (e.getPlayer().getItemInHand().getType() == Material.SHEARS
                && (e.getBlock().getType() == Material.LEAVES || e.getBlock().getType() == Material.LEAVES_2))
            silkTouch = true;

        boolean finalSilkTouch = silkTouch;
        Events.breakBlockLoots.forEach(item -> {
            if (item.getMaterials().contains(e.getBlock().getType())) {
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR, true);
                if (item.isExclusive())
                    dropNormally(e.getBlock().getLocation(), item.getLoots().lootsExclusive(chanceLvl, finalSilkTouch));
                else
                    dropNormally(e.getBlock().getLocation(), item.getLoots().lootsInclusive(chanceLvl, finalSilkTouch));
            }
        });
    }

}
