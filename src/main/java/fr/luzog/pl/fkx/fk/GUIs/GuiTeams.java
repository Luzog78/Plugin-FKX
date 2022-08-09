package fr.luzog.pl.fkx.fk.GUIs;

import fr.luzog.pl.fkx.fk.FKManager;
import fr.luzog.pl.fkx.fk.FKPickableLocks;
import fr.luzog.pl.fkx.fk.FKPlayer;
import fr.luzog.pl.fkx.fk.FKTeam;
import fr.luzog.pl.fkx.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class GuiTeams {

    public static ItemStack getMainItem(String lastLoreLine, String command) {
        if (FKManager.getCurrentGame() == null)
            return FKManager.getBanner();
        return Items.builder(FKManager.getBanner())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §6Nombre d'équipes : §f" + FKManager.getCurrentGame().getTeams().size(),
                        "  §8  > Participantes : §3" + FKManager.getCurrentGame().getParticipantsTeams().stream().filter(t -> !t.isEliminated()).count(),
                        "  §8  > Éliminées : §4" + FKManager.getCurrentGame().getParticipantsTeams().stream().filter(FKTeam::isEliminated).count(),
                        "  §8  > + Gods + Specs",
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static ItemStack getTeamItem(FKTeam team, String lastLoreLine, String command) {
        if (team == null)
            return Items.builder(FKManager.getBanner())
                    .setName("§cAucune équipe")
                    .setLore("§8" + Guis.loreSeparator)
                    .setCantClickOn(true)
                    .build();
        return Items.builder(FKManager.getBanner(team.getColor()))
                .setName("§6Équipe : §f" + team.getColor() + team.getName())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §6ID : §f" + team.getId(),
                        "  §6Nom : §f" + team.getColor() + team.getName(),
                        "  §6Préfixe : §f§7'" + team.getColor() + team.getPrefix() + "§7'",
                        "  §6Couleur : §f" + team.getColor() + team.getColor().name(),
                        "  §6Éliminée : §f" + (team.isEliminated() ? "§2" + SpecialChars.YES + "Oui" : "§4" + SpecialChars.NO + "Non"),
                        "  §8  > Éliminateurs : §6" + (team.getEliminators() == null ? "§cnull"
                                : FKManager.getCurrentGame().getTeam(team.getEliminators()) == null ? team.getEliminators()
                                : FKManager.getCurrentGame().getTeam(team.getEliminators()).getColor()
                                + FKManager.getCurrentGame().getTeam(team.getEliminators()).getName()),
                        " ",
                        "  §6Joueurs : §3" + team.getPlayers().size()
                                + (team.getPlayers().size() == 0 ? "" : "\n  §8 > §f" + (team.getPlayers().stream()
                                .map(FKPlayer::getDisplayName).collect(Collectors.joining("\n  §8 > §f")))),
                        " ",
                        "  §6Rayon : §f" + team.getRadius() + " blocks",
                        "  §6Spawn : §f" + Utils.locToString(team.getSpawn(), true, true, true),
                        " ",
                        "§8" + Guis.loreSeparator
                                + (lastLoreLine == null ? "" : "\n§7" + lastLoreLine)
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(command)
                .build();
    }

    public static Inventory getColorInventory(FKTeam team, String back) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("§cAucune partie en cours", back);
        if (team == null)
            return Guis.getErrorInventory("§cÉquipe introuvable", back);
        String refresh = "fk teams " + team.getId() + " colorGui";

        Inventory inv = Guis.getBaseInventory("§6Équipe §7-§b" + team.getId() + "§7 » §dCouleur", 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(6, 2), getTeamItem(team, "Clic pour rafraîchir", refresh));
        inv.setItem(Utils.posOf(6, 3), Items.builder(Material.INK_SACK)
                .setDurability((short) (15 - Utils.chatToDataColor(team.getColor())))
                .setName("§6Couleur : §f" + team.getColor() + team.getColor().name())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour rafraîchir",
                        "§7Commandes :",
                        "§7/fk teams " + team.getId() + " colorGui",
                        "§7/fk teams " + team.getId() + " options --c <color>"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(refresh)
                .build());

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                short k = (short) (i * 4 + j);
                ChatColor c = Utils.dataToChatColor(k);
                if (c != null)
                    inv.setItem(Utils.posOf(j + 1, i + 1), Items.builder(Material.INK_SACK)
                            .setDurability((short) (15 - k))
                            .setName("§6Couleur : §f" + c + c.name())
                            .setLore(
                                    "§8" + Guis.loreSeparator,
                                    "§7Clic pour redéfinir la couleur",
                                    "§7Commande :",
                                    "§7/fk teams " + team.getId() + " options --c " + c.name()
                            )
                            .setCantClickOn(true)
                            .setGlobalCommandOnClick("fk teams " + team.getId() + " options --c " + c.name() + "\n" + refresh)
                            .build());
            }

        return inv;
    }

    public static Inventory getTeamPlayers(FKTeam team, String back, String navigationBaseCommand, int page) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("§cAucune partie en cours", back);
        if (team == null)
            return Guis.getErrorInventory("§cÉquipe introuvable", back);
        return Guis.getPagedInventory("§6Équipe §7-§b" + team.getId() + "§7 » §eJoueurs", 54, back,
                GuiPlayers.getMain(team.getId(), "Clic pour rafraîchir",
                        navigationBaseCommand + " " + page, team.getPlayers().size(),
                        (int) team.getPlayers().stream().filter(p -> p.getPlayer() != null).count(), -1),
                getTeamItem(team, "Clic pour rafraîchir", navigationBaseCommand + " " + page),
                navigationBaseCommand, page, team.getPlayers().stream().map(p ->
                                GuiPlayers.getHead(p.getName(), "Clic pour voir plus", "fk players " + p.getName()))
                        .collect(Collectors.toList()));
    }

    public static Inventory getTeamsInventory(String back, String navigationBaseCommand, int page) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("§cAucune partie en cours", back);
        return Guis.getPagedInventory("§6Équipes", 54, back,
                getMainItem("Clic pour rafraîchir", navigationBaseCommand + " " + page),
                Items.builder(Material.NETHER_STAR)
                        .setName("§6Créer une équipe")
                        .setLore(
                                "§8" + Guis.loreSeparator,
                                " ",
                                "  §6Vous pouvez dès maintenant",
                                "  §6 créer une équipe en cliquant",
                                "  §6 sur cet item.",
                                " ",
                                "  §6Notez bien que vous devrez",
                                "  §6 indiquer au moins l'ID de l'équipe.",
                                "  §6Si vous ne souhaitez pas préciser",
                                "  §6 le 2nd paramètre, notez juste \".\"",
                                " ",
                                "§8" + Guis.loreSeparator,
                                "§7Clic Gauche pour créer une équipe",
                                "§7Commande :",
                                "§7/fk teams create §f<id> [<options>]"
                        )
                        .setLeftRightCommandOnClick(
                                "input 2 fk teams create %s %s%nfk teams",
                                "fk teams"
                        )
                        .setCantClickOn(true)
                        .build(),
                navigationBaseCommand, page, FKManager.getCurrentGame().getTeams().stream().map(t ->
                        getTeamItem(t, "Clic pour voir les options",
                                "fk teams " + t.getId())).collect(Collectors.toList()));
    }

    public static Inventory getTeamInventory(Player seer, FKTeam team, String back) {
        if (FKManager.getCurrentGame() == null)
            return Guis.getErrorInventory("§cAucune partie en cours", back);
        if (team == null)
            return Guis.getErrorInventory("§cÉquipe introuvable", back);
        String refresh = "fk teams " + team.getId();

        Inventory inv = Guis.getBaseInventory("§6Équipe §7-§b" + team.getId(), 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 1), Items.builder(getTeamItem(team, "Clic pour rafraîchir", "null"))
                .addLore("§7Clic Droit pour prendre la bannière").setCantClickOn(true)
                .setLeftRightCommandOnClick(refresh, "fk banner " + team.getColor().name()).build());
        inv.setItem(Utils.posOf(4, 3), GuiPlayers.getMain(team.getId(),
                "Clic pour voir les joueurs", "fk teams " + team.getId() + " playersGui",
                team.getPlayers().size(), (int) team.getPlayers().stream().filter(p ->
                        p.getPlayer() != null).count(), -1));

        inv.setItem(Utils.posOf(1, 2), Items.builder(Material.SIGN)
                .setName("§6Nom : §f" + team.getColor() + team.getName())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour changer le nom",
                        "§7Commande :",
                        "§7/fk teams " + team.getId() + " options --d §f<displayName>"
                )
                .setLeftRightCommandOnClick(
                        "input 1 fk teams " + team.getId() + " options --d %s%nfk teams " + team.getId(),
                        "fk teams " + team.getId()
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(2, 2), Items.builder(Material.NAME_TAG)
                .setName("§6Préfixe : §7'§f" + team.getColor() + team.getPrefix() + "§7'")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour changer le préfixe",
                        "§7Commande :",
                        "§7/fk teams " + team.getId() + " options --p §f<prefix>"
                )
                .setLeftRightCommandOnClick(
                        "input 1 fk teams " + team.getId() + " options --p %s%nfk teams " + team.getId(),
                        "fk teams " + team.getId()
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(2, 3), Items.builder(Material.INK_SACK)
                .setDurability((short) (15 - Utils.chatToDataColor(team.getColor())))
                .setName("§6Couleur : §f" + team.getColor() + team.getColor().name())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour voir plus",
                        "§7Commandes :",
                        "§7/fk teams " + team.getId() + " colorGui",
                        "§7/fk teams " + team.getId() + " options --c <color>"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick("fk teams " + team.getId() + " colorGui")
                .build());
        Location nLoc = Utils.normalize(seer.getLocation());
        inv.setItem(Utils.posOf(2, 4), Items.builder(Material.BED)
                .setName("§6Spawn")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §8> X : §f" + team.getSpawn().getX(),
                        "  §8> Y : §f" + team.getSpawn().getY(),
                        "  §8> Z : §f" + team.getSpawn().getZ(),
                        "  §8> Yaw : §f" + team.getSpawn().getYaw(),
                        "  §8> Pitch : §f" + team.getSpawn().getPitch(),
                        "  §8> Monde : §f" + team.getSpawn().getWorld().getName(),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour se téléporter",
                        "§7Clic Droit pour redéfinir ici",
                        "§7Clic molette pour invoquer l'Autel",
                        "§7Commandes :",
                        "§7/fk teams " + team.getId() + " altar",
                        "§7/fk teams " + team.getId() + " options --s <x> <y> <z> <yaw> [<pitch>] [<world>]"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(
                        "tp " + team.getSpawn().getX() + " " + team.getSpawn().getY() + " " + team.getSpawn().getZ()
                                + " " + team.getSpawn().getYaw() + " " + team.getSpawn().getPitch()
                                + " " + team.getSpawn().getWorld().getName() + "\n" + refresh,
                        "fk teams " + team.getId() + " options --s " + nLoc.getX()
                                + " " + nLoc.getY() + " " + nLoc.getZ()
                                + " " + nLoc.getYaw() + " " + nLoc.getPitch()
                                + " " + seer.getWorld().getName() + "\n" + refresh
                )
                .setMiddleCommandOnClick("fk teams " + team.getId() + " altar")
                .build());
        inv.setItem(Utils.posOf(1, 4), Items.builder(Material.FENCE)
                .setName("§6Rayon : §f" + team.getRadius())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour augmenter de 0.5",
                        "§7 (Shift pour augmenter de 1)",
                        "§7Clic Droit pour diminuer de 0.5",
                        "§7 (Shift pour diminuer de 1)",
                        "§7Clic Molette pour créer le mur",
                        "§7 > Pose une couche de §8Cobble",
                        "§7Commande :",
                        "§7/fk teams " + team.getId() + " options --r <radius>"
                )
                .setCantClickOn(true)
                .setLeftRightShiftCommandOnClick(
                        "fk teams " + team.getId() + " options --r " + (team.getRadius() + 0.5) + "\n" + refresh,
                        "fk teams " + team.getId() + " options --r " + (team.getRadius() + 1) + "\n" + refresh,
                        "fk teams " + team.getId() + " options --r " + (team.getRadius() > 0.5 ? team.getRadius() - 0.5 : 0) + "\n" + refresh,
                        "fk teams " + team.getId() + " options --r " + (team.getRadius() > 1 ? team.getRadius() - 1 : 0) + "\n" + refresh
                )
                .setMiddleCommandOnClick("fk teams " + team.getId() + " wall 1 cobblestone")
                .build());


        inv.setItem(Utils.posOf(7, 2), GuiPerm.getPermsItem(team.getPermissions(), Material.IRON_SWORD,
                "Permissions", "Clic pour voir les permissions", "fk perm team " + team.getId()));
        inv.setItem(Utils.posOf(6, 3), Items.builder(Material.ARMOR_STAND)
                .setName("§6ArmorStands")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        (!(team.getId().equals(FKTeam.GODS_ID) || team.getId().equals(FKTeam.SPECS_ID)) ?
                                "§7Clic Gauche pour le montrer"
                                        + "\n§7Clic Droit pour le cacher"
                                : " \n  §cCette équipe n'a pas d'armor stand...\n \n§8" + Guis.loreSeparator),
                        "§7Commande :",
                        "§7/fk teams " + team.getId() + " armorStand (hide | show)"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(
                        "fk teams " + team.getId() + " armorStand show\n" + refresh,
                        "fk teams " + team.getId() + " armorStand hide\n" + refresh
                )
                .build());
        inv.setItem(Utils.posOf(7, 4), Items.builder(Items.red())
                .setName("§cStatus :  §f" + (team.getId().equals(FKTeam.GODS_ID) || team.getId().equals(FKTeam.SPECS_ID) ?
                        "§7Ne compte pas"
                        : team.isEliminated() ? "§4" + SpecialChars.NO + "  Éliminée"
                        : team.isEliminating() ? "§b" + SpecialChars.FLAG_FILLED + "  En cours d'élimination"
                        : "§2" + SpecialChars.YES + "  Participante"))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §8> Éliminateurs : §f" + (team.getEliminators() == null ? "§cnull"
                                : FKManager.getCurrentGame().getTeam(team.getEliminators()).getName() != null ?
                                FKManager.getCurrentGame().getTeam(team.getEliminators()).getColor()
                                        + FKManager.getCurrentGame().getTeam(team.getEliminators()).getName()
                                : "§6" + team.getEliminators()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Shift Clic Gauche pour le éliminer",
                        "§7Shift Clic Droit pour réintroduire",
                        "§7Commande :",
                        "§7/fk teams (eliminate | reintroduce) " + team.getId()
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(
                        "fk teams eliminate " + team.getId() + "\n" + refresh,
                        "fk teams reintroduce " + team.getId() + "\n" + refresh
                )
                .build());

        return inv;
    }
}
