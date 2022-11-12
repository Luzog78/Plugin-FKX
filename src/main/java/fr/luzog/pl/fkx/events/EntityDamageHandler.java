package fr.luzog.pl.fkx.events;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.game.GTeam;
import fr.luzog.pl.fkx.utils.Broadcast;
import fr.luzog.pl.fkx.utils.Loots;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EntityDamageHandler implements Listener {

    @EventHandler
    public static void onDamage(EntityDamageEvent e) {
        if (e.getEntity().getType() == EntityType.ARMOR_STAND)
            return;

        if (!(e.getEntity() instanceof LivingEntity))
            return;

        LivingEntity entity = (LivingEntity) e.getEntity();
        Location tempLoc = entity.getLocation().add(0, 0.5, 0);

        if (GManager.getCurrentGame() != null && entity.hasMetadata(GTeam.PLUNDER_STAND_TAG)
                && e.getDamage() < 1_000_000_000_000_000_000_000.0
                && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            e.setCancelled(true);
            return;
        }

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            GPlayer fp = GManager.getCurrentGame() == null ? null : GManager.getCurrentGame().getPlayer(p.getName(), false);

            if (fp == null || (fp.getManager().getState() == GManager.State.PAUSED
                    && !fp.getTeam().getId().equals(fp.getManager().getGods().getId()))) {
                e.setCancelled(true);
                return;
            }

            if (e.getDamage() < 1_000_000_000_000_000_000_000.0) // Avoid /kill command
                fp.getStats().increaseDamageTaken(e.getDamage());

            if (entity.getHealth() - e.getFinalDamage() <= 0) {
                fp.getStats().increaseDeaths();
                List<String> m = new ArrayList<>();
                switch (e.getCause()) {
                    case BLOCK_EXPLOSION:
                        m.add("a été explosé.");
                        m.add("a été explosé.");
                        m.add("a pété... §oPÉTÉ !");
                        break;
                    case CONTACT:
                    case CUSTOM:
                        break;
                    case DROWNING:
                        m.add("s'est noyé.");
                        m.add("ne savait pas nager.");
                        m.add("glouglouglouu...");
                        break;
                    case ENTITY_ATTACK:
                        String s = e.getEntity().getMetadata(Events.lastDamagerMeta).get(0).asString();
                        m.add("a été tué par " + s + "§r.");
                        m.add("s'est fait décimé par " + s + "§r.");
                        m.add("s'est fait violenté par " + s + "§r.");
                        m.add("a perdu le combat contre " + s + "§r.");
                        m.add("a perdu la bataille contre " + s + "§r.");
                        m.add("s'est mesuré à " + s + "§r.");
                        m.add("s'est opposé à " + s + "§r.");
                        m.add("a défié " + s + "§r et a perdu...");
                        break;
                    case ENTITY_EXPLOSION:
                        m.add("a été explosé.");
                        m.add("n'est pas fan des creepers. ^^'");
                        m.add("a pété... §oPÉTÉ !");
                        break;
                    case FALL:
                        m.add("est mort de chute.");
                        m.add("s'est écrasé par terre.");
                        m.add("est descendu trop vite.");
                        m.add("a fait un plongeon raté.");
                        m.add("s'est pris pour un oiseau.");
                        m.add("a imité une crêpe.");
                        break;
                    case FALLING_BLOCK:
                        m.add("a été tué par un bloc.");
                        break;
                    case FIRE:
                        m.add("a été carbonisé instantanément !!");
                        break;
                    case FIRE_TICK:
                        m.add("a brûlé.");
                        m.add("n'est pas fan des flammes.");
                        m.add("a subit les flammes.");
                        m.add("s'est fait décimer par le feu.");
                        m.add("a voulu faire un barbecue.");
                        break;
                    case LAVA:
                        m.add("a nagé dans la lave.");
                        m.add("a tenté un plongeon dans le magma.");
                        m.add("a voulu faire plouf dans la lave.");
                        break;
                    case LIGHTNING:
                        m.add("a été frappé par la foudre.");
                        m.add("s'est électrocuté.");
                        m.add("a été touché par un éclair.");
                        m.add("a subit un coup de foudre.");
                        break;
                    case MAGIC:
                        m.add("a été tué par la magie.");
                        break;
                    case MELTING:
                        m.add("a été fondu.");
                        break;
                    case POISON:
                        m.add("a été empoisonné.");
                        break;
                    case PROJECTILE:
                        m.add("s'est fait tirer dessus.");
                        break;
                    case STARVATION:
                        m.add("est mort de faim.");
                        m.add("a oublié de manger.");
                        break;
                    case SUFFOCATION:
                        m.add("s'est étouffé.");
                        m.add("s'est asphyxié.");
                        m.add("a suffoqué.");
                        break;
                    case SUICIDE:
                        m.add("s'est suicidé. lol.");
                        break;
                    case THORNS:
                        m.add("s'est pris sa propre attaque.");
                        break;
                    case VOID:
                        m.add("est tombé dans le vide.");
                        m.add("a rejoint le vide.");
                        m.add("est tombé dans l'infini...");
                        break;
                    case WITHER:
                        m.add("a été infecté par le wither.");
                        break;
                    default:
                        break;
                }
                if(m.size() == 0) {
                    m.add("est mort.");
                    m.add("n'est plus.");
                    m.add("s'est fait décimé.");
                }
                Broadcast.mess(fp.getDisplayName() + "§c "
                        + m.get(new Random().nextInt(m.size())).replace("§r", "§c"));
                e.setCancelled(true);
                p.setHealth(p.getMaxHealth());
                p.setFoodLevel(20);
                p.setSaturation(20f);
                ((ExperienceOrb) p.getWorld().spawnEntity(p.getLocation(), EntityType.EXPERIENCE_ORB))
                        .setExperience((int) (Utils.lvlToExp(p.getLevel() + p.getExp()) * 0.75));
                p.setLevel(0);
                p.setExp(0);
                p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
                p.teleport(p.getBedSpawnLocation() == null ?
                        fp.getManager().getSpawn() == null || fp.getManager().getSpawn().getSpawn() == null ?
                                fp.getManager().getLobby() == null || fp.getManager().getLobby().getSpawn() == null ?
                                        Main.world == null ?
                                                p.getLocation()
                                                : Main.world.getSpawnLocation()
                                        : fp.getManager().getLobby().getSpawn()
                                : fp.getManager().getSpawn().getSpawn()
                        : p.getBedSpawnLocation());

                new ArrayList<ItemStack>() {{
                    addAll(Arrays.asList(p.getInventory().getContents()));
                    addAll(Arrays.asList(p.getInventory().getArmorContents()));
                    removeIf(is -> is == null || is.getType() == Material.AIR);
                    forEach(is -> p.getWorld().dropItemNaturally(tempLoc, is));
                }};
                p.getInventory().setArmorContents(new ItemStack[4]);
                p.getInventory().clear();
            }
        } else if (entity.getHealth() - e.getFinalDamage() <= 0)
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!e.isCancelled() && Main.customLootingMobsSystem && !entity.hasMetadata(GTeam.PLUNDER_STAND_TAG))
                        if (entity instanceof Creeper) {
                            if (((Creeper) entity).isPowered())
                                new Loots()
                                        .add(0.333, new ItemStack(Material.TNT))
                                        .add(0.333, new ItemStack(Material.TNT))
                                        .lootsInclusive()
                                        .forEach(is -> entity.getWorld().dropItemNaturally(tempLoc, is));
                            else
                                new Loots()
                                        .add(0.5, new ItemStack(Material.SULPHUR))
                                        .add(0.5, new ItemStack(Material.SULPHUR))
                                        .lootsInclusive()
                                        .forEach(is -> entity.getWorld().dropItemNaturally(tempLoc, is));
                        } else
                            Events.killMobLoots.forEach(loot -> {
                                if (entity.getType() == loot.getType() && (loot.getData() == Events.EntityData.WHATEVER || (loot.getData() != Events.EntityData.WHATEVER
                                        && (entity instanceof Creeper && ((Creeper) entity).isPowered() == (loot.getData() == Events.EntityData.CREEPER_SUPERCHARGED))
                                        || (entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == (loot.getData() == Events.EntityData.SKELETON_WITHER ?
                                        Skeleton.SkeletonType.WITHER : Skeleton.SkeletonType.NORMAL))))) {
                                    Location loc = entity.getLocation().clone().add(0, 0.3, 0);
                                    int chanceLvl = entity.hasMetadata(Events.lastDamageLootingLevelTag) ?
                                            entity.getMetadata(Events.lastDamageLootingLevelTag).get(0).asInt() : 0;
                                    boolean silkTouch = entity.hasMetadata(Events.lastDamageSilkTouchTag) && entity.getMetadata(Events.lastDamageSilkTouchTag).get(0).asBoolean();
                                    if (loot.isExclusive()) {
                                        ItemStack is = loot.getLoots().lootsExclusive(chanceLvl, silkTouch);
                                        if (is.getType() != Material.AIR)
                                            loc.getWorld().dropItemNaturally(loc, is);
                                    } else
                                        loot.getLoots().lootsInclusive(chanceLvl, silkTouch).forEach(is -> loc.getWorld().dropItemNaturally(loc, is));
                                }
                            });
                }
            }.runTask(Main.instance);
    }

}
