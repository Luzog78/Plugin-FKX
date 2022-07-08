package fr.luzog.pl.fkx.fk;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.utils.Config;
import fr.luzog.pl.fkx.utils.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class FKTeam {

    public static final String GODS_ID = "gods", GODS_FILE = "Gods.yml",
            SPECS_ID = "specs", SPECS_FILE = "Specs.yml";

    public void saveToConfig(String gameId, boolean soft) {
        getConfig(gameId)
                .load()

                .setName(name, !soft)
                .setPrefix(prefix, !soft)
                .setColor(color.name(), !soft)
                .setRadius(radius, !soft)
                .setSpawn(spawn, !soft)
                .setPermissions(permissions, !soft)

                .save();
    }

    public Config.Team getConfig(String gameId) {
        return new Config.Team("game-" + gameId + "/teams/" + (id.equalsIgnoreCase(GODS_ID) ? GODS_FILE : id.equalsIgnoreCase(SPECS_ID) ? SPECS_FILE : id + ".yml"));
    }

    private String id, name, prefix;
    private ChatColor color;
    private Location spawn, chestsRoom;
    private UUID guardian;
    private double radius;

    private Team scoreboardTeam;

    private FKPermissions permissions;

    public FKTeam(String id) {
        this.id = id;
        this.name = id;
        this.prefix = id + " ";
        this.color = ChatColor.WHITE;
        this.spawn = null;
        this.chestsRoom = null;
        this.guardian = null;
        this.radius = 0;
        this.permissions = new FKPermissions(FKPermissions.Definition.DEFAULT);
        scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("fkt" + UUID.randomUUID().toString().substring(0, 4) + ":" + id);
        updateParams();
    }

    public FKTeam(String id, String name, String prefix, ChatColor color, Location spawn, Location chestsRoom,
                  UUID guardian, double radius, FKPermissions permissions) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.spawn = spawn;
        this.chestsRoom = chestsRoom;
        this.guardian = guardian;
        this.radius = radius;
        this.permissions = permissions;
        scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("fkt" + UUID.randomUUID().toString().substring(0, 4) + ":" + id);
        updateParams();
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

    public Entity spawnGuardian() {
        if(chestsRoom == null && spawn == null)
            return null;
        Villager e = (Villager) Main.world.spawnEntity(chestsRoom == null ? spawn : chestsRoom, EntityType.VILLAGER);
        e.setAdult();
        e.setAgeLock(true);
        e.setCanPickupItems(false);
        e.setNoDamageTicks(Integer.MAX_VALUE);
        e.setRemoveWhenFarAway(false);
        //((CraftLivingEntity) e).getHandle().getDataWatcher().watch(15, (byte) 1); // 0 for AI (so 1 == NoAI)
        EntityLiving nms = ((CraftLivingEntity) e).getHandle();
        NBTTagCompound nbt = nms.getNBTTag();

        nbt.setByte("NoAI", (byte) 1);
        nbt.setByte("NoGravity", (byte) 1);

        nms.f(nbt);
        return e;
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

    public void setChestsRoom(Location chestsRoom, boolean save) {
        this.chestsRoom = chestsRoom;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setChestsRoom(chestsRoom, true).save();
        }
    }

    public UUID getGuardian() {
        return guardian;
    }

    public void setGuardian(UUID guardian, boolean save) {
        this.guardian = guardian;
        if (save && getManager() != null) {
            if (!getConfig(getManager().getId()).exists())
                saveToConfig(getManager().getId(), true);
            getConfig(getManager().getId()).load().setGuardian(guardian, true).save();
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

    public ItemStack getBanner() {
        ItemStack banner = new ItemStack(Material.BANNER, 1, (short) (15 - Utils.chatToDataColor(color)));
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        DyeColor dye = Utils.chatToDyeColor(color), white = DyeColor.WHITE;
        meta.addPattern(new Pattern(dye, PatternType.CURLY_BORDER));
        meta.addPattern(new Pattern(white, PatternType.CURLY_BORDER));
        meta.addPattern(new Pattern(dye, PatternType.STRIPE_CENTER));
        meta.addPattern(new Pattern(white, PatternType.STRIPE_CENTER));
        meta.addPattern(new Pattern(dye, PatternType.FLOWER));
        meta.addPattern(new Pattern(white, PatternType.FLOWER));
        banner.setItemMeta(meta);
        return banner;
    }
}
