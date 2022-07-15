package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.Broadcast;
import fr.luzog.pl.fkx.utils.Config;
import fr.luzog.pl.fkx.utils.Utils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class FKTeam {

    public static final String GODS_ID = "gods", GODS_FILE = "Gods.yml",
            SPECS_ID = "specs", SPECS_FILE = "Specs.yml", GUARDIAN_TAG = "FKGuardian";
    public static final long ELIMINATION_TIMEOUT = 2000;

    public void saveToConfig(String gameId, boolean soft) {
        if (soft)
            return;

        getConfig(gameId)
                .load()

                .setName(name, true)
                .setPrefix(prefix, true)
                .setColor(color.name(), true)
                .setEliminators(eliminators, true)
                .setRadius(radius, true)
                .setOldPlayers(oldPlayers, true)
                .setEliminated(isEliminated, true)
                .setSpawn(spawn, true)
                .setChestsRoom(chestsRoom, true)
                .setGuardian(guardian, true)
                .setPermissions(permissions, true)

                .save();
    }

    public Config.Team getConfig(String gameId) {
        return new Config.Team("game-" + gameId + "/teams/" + (id.equalsIgnoreCase(GODS_ID) ? GODS_FILE : id.equalsIgnoreCase(SPECS_ID) ? SPECS_FILE : id + ".yml"));
    }

    private String id, name, prefix, eliminators;
    private ChatColor color;
    private Location spawn, chestsRoom;
    private UUID guardian, armorStand;
    private double radius;
    private ArrayList<String> oldPlayers;
    private boolean isEliminated;
    private long eliminationCooldown;

    private Team scoreboardTeam;

    private FKPermissions permissions;

    public FKTeam(String id) {
        this.id = id;
        this.name = id;
        this.prefix = id + " ";
        this.eliminators = null;
        this.color = ChatColor.WHITE;
        this.spawn = new Location(Main.world, 0, 0, 0);
        this.chestsRoom = null;
        this.guardian = null;
        this.armorStand = null;
        this.radius = 0;
        this.oldPlayers = new ArrayList<>();
        this.isEliminated = false;
        this.eliminationCooldown = 0;
        this.permissions = new FKPermissions(FKPermissions.Definition.DEFAULT);
        scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("fkt" + UUID.randomUUID().toString().substring(0, 4) + ":" + id);
        updateParams();
    }

    public FKTeam(String id, String name, String prefix, String eliminators, ChatColor color, Location spawn,
                  Location chestsRoom, UUID guardian, UUID armorStand, double radius, ArrayList<String> oldPlayers,
                  boolean isEliminated, long eliminationCooldown, FKPermissions permissions) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.eliminators = eliminators;
        this.color = color;
        this.spawn = spawn;
        this.chestsRoom = chestsRoom;
        this.guardian = guardian;
        this.armorStand = armorStand;
        this.radius = radius;
        this.oldPlayers = oldPlayers == null ? new ArrayList<>() : oldPlayers;
        this.isEliminated = isEliminated;
        this.eliminationCooldown = eliminationCooldown;
        this.permissions = permissions;
        scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("fkt" + UUID.randomUUID().toString().substring(0, 4) + ":" + id);
        updateParams();
    }

    public String getProgressBar() {
        if (eliminationCooldown == 0)
            if (isEliminated)
                return Utils.progressBar("§f[", "§f]", "", "", "§2|", "", 16, 1.0f, "§a{p}% {b}");
            else
                return Utils.progressBar("§f[", "§f]", "", "", "", "§c|", 32, 0.0f, "§a{p..}% {b}");
        else
            return Utils.progressBar("§f[", "§f]", new String[]{
                            "§e|", "§e|", "§e|", "§e|", "§a|", "§a|", "§a|", "§a|", "§a|", "§a|",
                            "§a|", "§a|", "§a|", "§a|", "§2|", "§2|", "§2|", "§6|", "§c|", "§4|"
                    }, "§7|", "§2|", "§c|", 32,
                    (float) ((eliminationCooldown * 1.0) / ELIMINATION_TIMEOUT), "§a{p.}% {b}");
    }

    public boolean isEliminating() {
        return eliminationCooldown > 0;
    }

    public void tryToEliminate(FKTeam team) {
        eliminators = team.getId();
        getGuardian().setCustomNameVisible(true);

        team.getPlayers().stream().map(FKPlayer::getPlayer).filter(Objects::nonNull).forEach(p -> {
            p.sendMessage("§aVous essayez d'assaillir le §6Guardien des Coffres§a de l'équipe " + color + name + "§a !"
                    + "\n§aVous avez §c100 secondes§a à tenir à moins de §c5 blocs§a du §6Gardien§a... Courage !");
            p.playSound(p.getLocation(), Sound.VILLAGER_IDLE, Float.MAX_VALUE, 1);
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                getGuardian();
                Entity e = getArmorStand();
                e.setCustomNameVisible(true);

                if (!isEliminated && team.getPlayers().stream().anyMatch(p -> p.getPlayer() != null
                        && p.getPlayer().getLocation().distance(chestsRoom) <= 5) && team.getId().equals(eliminators))
                    if (eliminationCooldown < ELIMINATION_TIMEOUT) {
                        eliminationCooldown++;
                        e.setCustomName(getProgressBar());
                    } else {
                        setEliminators(eliminators, true);
                        eliminate(true, true, true);
                        team.getPlayers().stream().map(FKPlayer::getPlayer).filter(Objects::nonNull).forEach(p -> {
                            p.sendMessage("§aBien joué camarades !");
                            p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
                        });
                        cancel();
                    }
                else {
                    eliminationCooldown = 0;
                    eliminators = null;
                    e.setCustomName(getProgressBar());
                    e.setCustomNameVisible(false);
                    team.getPlayers().stream().map(FKPlayer::getPlayer).filter(Objects::nonNull).forEach(p -> {
                        p.sendMessage("§cAssaut Annulé.\n§cPlus aucun joueur n'assaillit le §6Gardien des coffres§c.");
                        p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 1);
                    });
                    cancel();
                }

            }
        }.runTaskTimer(Main.instance, 0, 1);
    }

    public void eliminate(boolean broadcast, boolean checkForTheOther, boolean save) {
        isEliminated = true;
        eliminationCooldown = 0;
        Entity e = getArmorStand();
        e.setCustomName(getProgressBar());
        e.setCustomNameVisible(isEliminated);
        getPlayers().forEach(p -> {
            oldPlayers.add(p.getName());
            if (broadcast && p.getPlayer() != null)
                p.getPlayer().sendMessage(Main.PREFIX + "§4§lVous avez été éliminé de la partie.");
            p.leaveTeam(false);
            p.setTeam(FKTeam.SPECS_ID, true);
            if (p.getPlayer() != null) {
                p.getPlayer().setGameMode(GameMode.SPECTATOR);
                if (getManager().getSpawn().getSpawn() != null)
                    p.getPlayer().teleport(getManager().getSpawn().getSpawn());
                else if (getManager().getLobby().getSpawn() != null)
                    p.getPlayer().teleport(getManager().getLobby().getSpawn());
            }
        });
        Broadcast.announcement(Main.PREFIX + "§r§lLa team " + name + " a été !éliminée de la partie."
                + "\nDonc !" + oldPlayers.size() + " !joueurs ont été éliminés.");
        if (save && getManager() != null)
            saveToConfig(getManager().getId(), false);
        if (checkForTheOther) {
            long teams = getManager().getTeams().stream().filter(t -> !t.getId().equals(GODS_ID)
                    && !t.getId().equals(SPECS_ID) && !t.isEliminated()).count();
            if (teams == 0 || teams == 1)
                getManager().end();
            else
                Broadcast.log("§eIl reste !" + teams + " !équipes en jeu !");
        }
    }

    public void reintroduce(boolean broadcast, boolean save) {
        isEliminated = false;
        eliminationCooldown = 0;
        Entity e = getArmorStand();
        e.setCustomName(getProgressBar());
        e.setCustomNameVisible(isEliminated);
        int count = oldPlayers.size();
        oldPlayers.forEach(pName -> {
            FKPlayer p = getManager().getPlayer(pName, true);
            if (broadcast && p.getPlayer() != null)
                p.getPlayer().sendMessage(Main.PREFIX + "§2§lVous avez été réintégré à la partie.");
            p.leaveTeam(false);
            p.setTeam(id, true);
            if (p.getPlayer() != null) {
                p.getPlayer().setGameMode(GameMode.SURVIVAL);
                if (spawn != null)
                    p.getPlayer().teleport(spawn);
                else if (getManager().getSpawn().getSpawn() != null)
                    p.getPlayer().teleport(getManager().getSpawn().getSpawn());
                else if (getManager().getLobby().getSpawn() != null)
                    p.getPlayer().teleport(getManager().getLobby().getSpawn());
            }
        });
        oldPlayers.clear();
        Broadcast.announcement(Main.PREFIX + "§r§lLa team " + name + " a été !réintégrée à la partie."
                + "\nDonc !" + count + " !joueurs sont de retour.");
        if (save && getManager() != null)
            saveToConfig(getManager().getId(), false);
    }

    public boolean isInside(Location loc) {
        Location l1 = getSpawn().clone(), l2 = getSpawn().clone();
        l1.setY(-1);
        l2.setY(256);
        return Utils.isInside(loc, l1.add(radius, 0, radius), l2.subtract(radius, 0, radius));
    }

    public FKZone getZone(boolean friendly) {
        return new FKZone(getId(), friendly ? FKZone.Type.FRIENDLY : FKZone.Type.HOSTILE, getSpawn().clone(),
                getSpawn().clone().subtract(radius, radius, radius), getSpawn().clone().add(radius, radius, radius), getPermissions());
    }

    public FKManager getManager() {
        for (FKManager manager : FKManager.registered)
            for (FKTeam team : manager.getTeams())
                if (team.equals(this))
                    return manager;
        return null;
    }

    public void updatePlayers() {
        scoreboardTeam.getPlayers().forEach(p -> scoreboardTeam.removePlayer(p));
        scoreboardTeam.getEntries().forEach(e -> scoreboardTeam.addEntry(e));
        getPlayers().forEach(p -> {
            if (p.getName() != null)
                scoreboardTeam.addPlayer(Bukkit.getOfflinePlayer(p.getName()));
        });
    }

    public void updateParams() {
        scoreboardTeam.setDisplayName(getName());
        scoreboardTeam.setPrefix(getPrefix());
        scoreboardTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
        scoreboardTeam.setCanSeeFriendlyInvisibles(false);
        scoreboardTeam.setAllowFriendlyFire(true);
    }

    public Entity getGuardian() {
        if (id.equalsIgnoreCase(GODS_ID) || id.equalsIgnoreCase(SPECS_ID))
            return null;
        if (chestsRoom == null)
            setChestsRoom(spawn.clone(), false, true);
        Villager e = null;
        for (Entity ee : chestsRoom.getWorld().getEntities())
            if (ee.getUniqueId().equals(guardian)) {
                e = (Villager) ee;
                break;
            }
        if (e == null) {
            e = (Villager) Main.world.spawnEntity(chestsRoom, EntityType.VILLAGER);
            e.setAdult();
            e.setAgeLock(true);
            e.setCanPickupItems(false);
            e.setNoDamageTicks(Integer.MAX_VALUE);
            e.setRemoveWhenFarAway(false);
            ((CraftLivingEntity) e).getHandle().getDataWatcher().watch(15, (byte) 1); // NoGravity Option
//            EntityLiving nms = ((CraftLivingEntity) e).getHandle();
//            NBTTagCompound nbt = nms.getNBTTag();
//
//            nbt.setByte("NoAI", (byte) 1);
//            nbt.setByte("NoGravity", (byte) 1);
//
//            nms.f(nbt);
            e.setMetadata(GUARDIAN_TAG, new FixedMetadataValue(Main.instance, id));
            setGuardianUuid(e.getUniqueId(), true);
        }
        return e;
    }

    public void killGuardian() {
        if (guardian != null)
            Main.world.getEntities().stream().filter(e -> e.getUniqueId().equals(guardian)).forEach(Entity::remove);
        setGuardianUuid(null, true);
    }

    public Entity getArmorStand() {
        if (id.equalsIgnoreCase(GODS_ID) || id.equalsIgnoreCase(SPECS_ID))
            return null;
        if (chestsRoom == null)
            setChestsRoom(spawn.clone(), false, true);
        ArmorStand e = null;
        for (Entity ee : chestsRoom.getWorld().getEntities())
            if (ee.getUniqueId().equals(armorStand)) {
                e = (ArmorStand) ee;
                break;
            }
        if (e == null) {
            e = (ArmorStand) Main.world.spawnEntity(chestsRoom.clone().add(0, 1, 0), EntityType.ARMOR_STAND);
            e.setCustomName(getProgressBar());
            e.setCustomNameVisible(isEliminating() || isEliminated);
            e.setVisible(false);
            e.setGravity(false);
            e.setCanPickupItems(false);
            e.setSmall(true);
            e.setBasePlate(false);
            e.setRemoveWhenFarAway(false);
            e.setNoDamageTicks(Integer.MAX_VALUE); // ~3.4 years of god mod
            setArmorStandUuid(e.getUniqueId(), true);
        }
        return e;
    }

    public void killArmorStand() {
        if (armorStand != null)
            Main.world.getEntities().stream().filter(e -> e.getUniqueId().equals(armorStand)).forEach(Entity::remove);
        setArmorStandUuid(null, true);
    }

    public String getId() {
        return id;
    }

    public void setId(@Nonnull String id, boolean renameFile) {
        if (getManager() != null) {
            if (getManager().getTeam(id) != null && !getManager().getTeam(id).equals(this))
                throw new FKException.DuplicateTeamIdException(id, getManager().getId());
            if (this.id.equalsIgnoreCase(GODS_ID) || this.id.equalsIgnoreCase(SPECS_ID))
                throw new FKException.CannotChangeTeamIdException(this.id, getManager().getId());
        }
        if (renameFile && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).getFile().renameTo(new File(getConfig(getManager().getId()).getFile().getParentFile().getPath() + "/" + id + ".yml"));
        }
        this.id = id;
    }

    public String getName() {
        return color + name.replace("§r", color.toString()) + color + "§r";
    }

    public void setName(String name, boolean save) {
        this.name = name;
        updateParams();
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setName(name, true).save();
        }
    }

    public String getPrefix() {
        String p = color + prefix.replace("§r", color.toString());
        if (prefix.contains("§"))
            for (String s : prefix.toLowerCase().split("§"))
                if (!s.startsWith("r") && !s.startsWith((color.getChar() + "").toLowerCase())) {
                    p += color;
                    break;
                }
        while (p.contains(color + "" + color))
            p = p.replace(color + "" + color, color.toString());
        return p;
    }

    public void setPrefix(String prefix, boolean save) {
        this.prefix = prefix;
        updateParams();
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setPrefix(prefix, true).save();
        }
    }

    public String getEliminators() {
        return eliminators;
    }

    public void setEliminators(String eliminators, boolean save) {
        this.eliminators = eliminators;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setEliminators(eliminators, true).save();
        }
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color, boolean save) {
        this.color = color;
        updateParams();
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setColor(color.name(), true).save();
        }
    }

    public Location getSpawn() {
        return spawn == null ? getManager() == null ? null : getManager().getSpawn().getSpawn() : spawn;
    }

    public void setSpawn(Location spawn, boolean save) {
        this.spawn = spawn;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setSpawn(spawn, true).save();
        }
    }

    public Location getChestsRoom() {
        return chestsRoom;
    }

    public void setChestsRoom(Location chestsRoom, boolean tpGuardian, boolean save) {
        this.chestsRoom = chestsRoom;
        if (tpGuardian) {
            getGuardian().teleport(chestsRoom);
            getArmorStand().teleport(chestsRoom.clone().add(0, 1, 0));
        }
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setChestsRoom(chestsRoom, true).save();
        }
    }

    public UUID getGuardianUuid() {
        return guardian;
    }

    public void setGuardianUuid(UUID guardian, boolean save) {
        this.guardian = guardian;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setGuardian(guardian, true).save();
        }
    }

    public UUID getArmorStandUuid() {
        return armorStand;
    }

    public void setArmorStandUuid(UUID armorStand, boolean save) {
        this.armorStand = armorStand;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setArmorStand(armorStand, true).save();
        }
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius, boolean save) {
        this.radius = radius;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setRadius(radius, true).save();
        }
    }

    public ArrayList<String> getOldPlayers() {
        return oldPlayers;
    }

    public void setOldPlayers(ArrayList<String> oldPlayers, boolean save) {
        this.oldPlayers = oldPlayers;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setOldPlayers(oldPlayers, true).save();
        }
    }

    public ArrayList<FKPlayer> getPlayers() {
        ArrayList<FKPlayer> ps = new ArrayList<>();
        if (getManager() != null)
            ps.addAll(getManager().getPlayers());
        ps.removeIf(p -> p.getTeam() == null || !p.getTeamId().equals(id));
        return ps;
    }

    /*
     * public FKPlayer getPlayer(UUID uuid) {
     *     for (FKPlayer player : getPlayers())
     *         if (player.getUuid().equals(uuid))
     *             return player;
     *     return null;
     * }
     */

    public FKPlayer getPlayer(@Nonnull String name) {
        for (FKPlayer player : getPlayers())
            if (name.equalsIgnoreCase(player.getName()))
                return player;
        return null;
    }

    /**
     * @throws FKException.PlayerAlreadyInTeamException
     * @deprecated This method is deprecated. Use {@link FKPlayer#setTeam(String, boolean)} instead.
     */
    @Deprecated
    public void addPlayer(FKPlayer player) {
        if (player.getTeam() == null)
            player.setTeam(id, true);
        else
            throw new FKException.PlayerAlreadyInTeamException(id, player.getName());
    }

    /**
     * @throws FKException.PlayerNotInTeamException
     * @deprecated This method is deprecated. Use {@link FKPlayer#leaveTeam(boolean)} instead.
     */
    @Deprecated
    public void removePlayer(FKPlayer player) {
        if (player.getTeam() == this)
            player.leaveTeam(true);
        else
            throw new FKException.PlayerNotInTeamException(id, player.getName());
    }

    public boolean isEliminated() {
        return isEliminated;
    }

    public void setEliminated(boolean isEliminated, boolean save) {
        this.isEliminated = isEliminated;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setEliminated(isEliminated, true).save();
        }
    }

    public long getEliminationCooldown() {
        return eliminationCooldown;
    }

    public void setEliminationCooldown(long eliminationCooldown) {
        this.eliminationCooldown = eliminationCooldown;
    }

    public Team getScoreboardTeam() {
        return scoreboardTeam;
    }

    /**
     * @deprecated You may not use this method.
     */
    @Deprecated
    public void setScoreboardTeam(Team scoreboardTeam) {
        this.scoreboardTeam = scoreboardTeam;
    }

    public FKPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(FKPermissions permissions, boolean save) {
        this.permissions = permissions;
        if (save)
            savePermissions();
    }

    public void savePermissions() {
        if (getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setPermissions(permissions, true).save();
        }
    }
}
