package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKPermissions;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        List<FKPlayer> fps = FKManager.getGlobalPlayer(e.getPlayer().getName());
        if (fps.isEmpty()) {
            e.setCancelled(true);
            return;
        }

        for (FKPlayer fp : fps) {
            if (fp == null || !fp.hasPermission(Events.specialMat.contains(e.getBlock().getType()) ?
                    FKPermissions.Type.BREAKSPE : FKPermissions.Type.BREAK, Utils.normalize(e.getBlock().getLocation()))) {
                e.setCancelled(true);
                return;
            }

            if (Events.unbreakableMat.contains(e.getBlock().getType())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Main.PREFIX + "§cBlock Incassable.");
                return;
            }

            fp.getStats().increaseBlocksBroken();
            if (Arrays.asList(Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE,
                    Material.LAPIS_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.QUARTZ_ORE,
                    Material.GLOWING_REDSTONE_ORE).contains(e.getBlock().getType()))
                fp.getStats().increaseOresBroken();

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

}
