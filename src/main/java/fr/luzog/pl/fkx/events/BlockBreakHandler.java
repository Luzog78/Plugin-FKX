package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.fk.FKPermissions;
import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.fk.FKTeam;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
        FKPlayer fp;
        if (FKManager.getCurrentGame() == null
                || (fp = FKManager.getCurrentGame().getPlayer(e.getPlayer().getName(), false)) == null) {
            e.setCancelled(true);
            return;
        }

        if (!fp.hasPermission(Events.specialMat.contains(e.getBlock().getType()) ?
                FKPermissions.Type.BREAKSPE : FKPermissions.Type.BREAK, Utils.normalize(e.getBlock().getLocation()))) {
            e.setCancelled(true);
            return;
        }

        if (Events.unbreakableMat.contains(e.getBlock().getType())
                && fp.getTeam() != null && fp.getTeam().getId().equals(FKTeam.GODS_ID)) {
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
        if (Main.customLootingBlocksSystem)
            Events.breakBlockLoots.forEach(item -> {
                if (item.getMaterials().contains(e.getBlock().getType())) {
                    e.setCancelled(true);
                    e.getBlock().setType(Material.AIR, true);
                    ArrayList<ItemStack> drops = new ArrayList<>();
                    double exp = 0;
                    if (item.isExclusive())
                        drops.add(item.getLoots().lootsExclusive(chanceLvl, finalSilkTouch));
                    else
                        drops.addAll(item.getLoots().lootsInclusive(chanceLvl, finalSilkTouch));
//                        for(ItemStack i : drops)
//                            if(i.getType() == Material.IRON_INGOT || i.getType() == Material.IRON_ORE)
//                                exp += 0.7;
//                            else if (i.getType() == Material.GOLD_INGOT || i.getType() == Material.GOLD_ORE)
//                                exp += 1.0;
                    dropNormally(e.getBlock().getLocation(), drops);
                }
            });
    }

}
