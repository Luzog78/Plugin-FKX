package fr.luzog.pl.fkx.guis;

import fr.luzog.pl.fkx.Main;
import fr.luzog.pl.fkx.game.GManager;
import fr.luzog.pl.fkx.game.GPlayer;
import fr.luzog.pl.fkx.game.GTeam;
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
        if (GManager.getCurrentGame() == null)
            return GManager.getBanner();
        return Items.builder(GManager.getBanner())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §6Nombre d'équipes : §f" + GManager.getCurrentGame().getTeams().size(),
                        "  §8  > Participantes : §3" + GManager.getCurrentGame().getParticipantsTeams().stream().filter(t -> !t.isEliminated()).count(),
                        "  §8  > Éliminées : §4" + GManager.getCurrentGame().getParticipantsTeams().stream().filter(GTeam::isEliminated).count(),
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

    public static ItemStack getTeamItem(GTeam team, String lastLoreLine, String command) {
        if (team == null)
            return Items.builder(GManager.getBanner())
                    .setName("§cAucune équipe")
                    .setLore("§8" + Guis.loreSeparator)
                    .setCantClickOn(true)
                    .build();
        return Items.builder(GManager.getBanner(team.getColor()))
                .setName("§6Équipe : §f" + team.getColor() + team.getName())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §6ID : §f" + team.getId(),
                        "  §6Nom : §f" + team.getColor() + team.getName(),
                        "  §6Préfixe : §f§7'" + team.getColor() + team.getPrefix() + "§7'",
                        "  §6Couleur : §f" + team.getColor() + team.getColor().name(),
                        "  §6Délai d'élimination : §7" + (team.getDefaultEliminationCooldown() / 20.0) + "s",
                        "  §6Éliminée : §f" + (team.isEliminated() ? "§2" + SpecialChars.YES + "Oui" : "§4" + SpecialChars.NO + "Non"),
                        "  §8  > Éliminateurs : §6" + (team.getEliminators() == null ? "§cnull"
                                : GManager.getCurrentGame().getTeam(team.getEliminators()) == null ? team.getEliminators()
                                : GManager.getCurrentGame().getTeam(team.getEliminators()).getColor()
                                + GManager.getCurrentGame().getTeam(team.getEliminators()).getName()),
                        " ",
                        "  §6Joueurs : §3" + team.getPlayers().size()
                                + (team.getPlayers().size() == 0 ? "" : "\n  §8 > §f" + (team.getPlayers().stream()
                                .map(GPlayer::getDisplayName).collect(Collectors.joining("\n  §8 > §f")))),
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

    public static Inventory getColorInventory(GTeam team, String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("§cAucune partie en cours", back);
        if (team == null)
            return Guis.getErrorInventory("§cÉquipe introuvable", back);
        String refresh = Main.CMD + " teams " + team.getId() + " colorGui";

        Inventory inv = Guis.getBaseInventory("§6Équipe §f-§b " + team.getId() + "§f » §dCouleur", 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(6, 2), getTeamItem(team, "Clic pour rafraîchir", refresh));
        inv.setItem(Utils.posOf(6, 3), Items.builder(Material.INK_SACK)
                .setDurability((short) (15 - Utils.chatToDataColor(team.getColor())))
                .setName("§6Couleur : §f" + team.getColor() + team.getColor().name())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour rafraîchir",
                        " ",
                        "§7Commandes :",
                        "§7/" + Main.CMD + " teams " + team.getId() + " colorGui",
                        "§7/" + Main.CMD + " teams " + team.getId() + " options --c §f<color>"
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
                                    " ",
                                    "§7Commande :",
                                    "§7/" + Main.CMD + " teams " + team.getId() + " options --c " + c + c.name()
                            )
                            .setCantClickOn(true)
                            .setGlobalCommandOnClick(Main.CMD + " teams " + team.getId() + " options --c " + c.name() + "\n" + refresh)
                            .build());
            }

        return inv;
    }

    public static Inventory getTeamPlayers(GTeam team, String back, String navigationBaseCommand, int page) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("§cAucune partie en cours", back);
        if (team == null)
            return Guis.getErrorInventory("§cÉquipe introuvable", back);
        return Guis.getPagedInventory("§6Équipe §f-§b " + team.getId() + "§f » §eJoueurs", 54, back,
                GuiPlayers.getMain(team.getId(), "Clic pour rafraîchir",
                        navigationBaseCommand + " " + page, team.getPlayers().size(),
                        (int) team.getPlayers().stream().filter(p -> p.getPlayer() != null).count(), -1),
                getTeamItem(team, "Clic pour rafraîchir", navigationBaseCommand + " " + page),
                navigationBaseCommand, page, team.getPlayers().stream().map(p ->
                                GuiPlayers.getHead(p.getName(),
                                        "Clic pour voir plus\n \n§7Commande :\n§7/" + Main.CMD + " players " + p.getName(),
                                        Main.CMD + " players " + p.getName()))
                        .collect(Collectors.toList()));
    }

    public static Inventory getTeamsInventory(String back, String navigationBaseCommand, int page) {
        if (GManager.getCurrentGame() == null)
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
                                " ",
                                "§7Commande :",
                                "§7/" + Main.CMD + " teams create §f<id> §8[§f<options>§8]"
                        )
                        .setLeftRightCommandOnClick(
                                "input " + Main.CMD + " teams create %s %s%n" + Main.CMD + " teams",
                                Main.CMD + " teams"
                        )
                        .setCantClickOn(true)
                        .build(),
                navigationBaseCommand, page, GManager.getCurrentGame().getTeams().stream().map(t ->
                        getTeamItem(t, "Clic pour voir les options\n \n§7Commande :\n§7/" + Main.CMD + " teams "
                                + t.getId(), Main.CMD + " teams " + t.getId())).collect(Collectors.toList()));
    }

    public static Inventory getTeamInventory(Player seer, GTeam team, String back) {
        if (GManager.getCurrentGame() == null)
            return Guis.getErrorInventory("§cAucune partie en cours", back);
        if (team == null)
            return Guis.getErrorInventory("§cÉquipe introuvable", back);
        String refresh = Main.CMD + " teams " + team.getId();

        Inventory inv = Guis.getBaseInventory("§6Équipe §f-§b " + team.getId(), 54, back,
                getMainItem(null, "null"), null);

        inv.setItem(Utils.posOf(4, 1), Items.builder(
                        getTeamItem(team, "Clic pour rafraîchir", "null"))
                .addLore(
                        "§7Clic Droit pour prendre la bannière",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " banner " + team.getColor() + team.getColor().name()
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(refresh, Main.CMD + " banner " + team.getColor().name())
                .build());

        inv.setItem(Utils.posOf(4, 3), GuiPlayers.getMain(team.getId(),
                "Clic pour voir les joueurs\n \n§7Commande:\n§7/" + Main.CMD + " teams " + team.getId() + " playersGui",
                Main.CMD  + " teams " + team.getId() + " playersGui",
                team.getPlayers().size(), (int) team.getPlayers().stream().filter(p ->
                        p.getPlayer() != null).count(), -1));

        inv.setItem(Utils.posOf(1, 2), Items.builder(Material.SIGN)
                .setName("§6Nom : §f" + team.getColor() + team.getName())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour changer le nom",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " teams " + team.getId() + " options --d §f<displayName>"
                )
                .setLeftRightCommandOnClick(
                        "input " + Main.CMD + " teams " + team.getId() + " options --d %s%n" + refresh,
                        refresh
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(2, 2), Items.builder(Material.NAME_TAG)
                .setName("§6Préfixe : §7'§f" + team.getColor() + team.getPrefix() + "§7'")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour changer le préfixe",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " teams " + team.getId() + " options --p §f<prefix>"
                )
                .setLeftRightCommandOnClick(
                        "input " + Main.CMD + " teams " + team.getId() + " options --p %s%n" + refresh,
                        refresh
                )
                .setCantClickOn(true)
                .build());
        inv.setItem(Utils.posOf(2, 3), Items.builder(Material.INK_SACK)
                .setDurability((short) (15 - Utils.chatToDataColor(team.getColor())))
                .setName("§6Couleur : §f" + team.getColor() + team.getColor().name())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic pour voir plus",
                        " ",
                        "§7Commandes :",
                        "§7/" + Main.CMD + " teams " + team.getId() + " colorGui",
                        "§7/" + Main.CMD + " teams " + team.getId() + " options --c §f<color>"
                )
                .setCantClickOn(true)
                .setGlobalCommandOnClick(Main.CMD + " teams " + team.getId() + " colorGui")
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
                        "§7Clic Droit pour définir ici",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " teams " + team.getId() + " options --s §f<x> <y> <z> §8[§f<yaw> <pitch>§8] [§f<world>§8]"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(
                        "tp " + team.getSpawn().getX() + " " + team.getSpawn().getY() + " " + team.getSpawn().getZ()
                                + " " + team.getSpawn().getYaw() + " " + team.getSpawn().getPitch()
                                + " " + team.getSpawn().getWorld().getName() + "\n" + refresh,
                        Main.CMD + " teams " + team.getId() + " options --s " + nLoc.getX()
                                + " " + nLoc.getY() + " " + nLoc.getZ()
                                + " " + nLoc.getYaw() + " " + nLoc.getPitch()
                                + " " + seer.getWorld().getName() + "\n" + refresh
                )
                .setMiddleCommandOnClick("input " + Main.CMD + " teams " + team.getId() + " options --s %l{x,y,z,yw,pi,w}%n" + refresh)
                .build());
        inv.setItem(Utils.posOf(1, 4), Items.builder(Material.FENCE)
                .setName("§6Rayon : §f" + team.getRadius())
                .setLore(
                        "§8" + Guis.loreSeparator,
                        "§7Clic Gauche pour §2 + 1",
                        "§7Clic Droit pour §4 - 1",
                        "§7Clic Molette pour §fdéfinir",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " teams " + team.getId() + " options --r §f<radius>"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(
                        Main.CMD + " teams " + team.getId() + " options --r " + (team.getRadius() == 0 ? 0.5 : team.getRadius() + 1) + "\n" + refresh,
                        Main.CMD + " teams " + team.getId() + " options --r " + (team.getRadius() > 0.5 ? team.getRadius() - 1 : 0) + "\n" + refresh
                )
                .setMiddleCommandOnClick("input " + Main.CMD + " teams " + team.getId() + " options --r %f%n" + refresh)
                .build());


        inv.setItem(Utils.posOf(7, 2), GuiPerm.getPermsItem(team.getPermissions(), Material.IRON_SWORD,
                "§fPermissions", "Clic pour voir les permissions\n \n§7Commande:\n§7/" + Main.CMD + " perm team "
                        + team.getId(), Main.CMD + " perm team " + team.getId()));
        inv.setItem(Utils.posOf(6, 2), Items.builder(Material.WATCH)
                .setName("§6Délai d'élimination : §7" + (team.getDefaultEliminationCooldown() / 20.0) + "s")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        (!(team.getId().equals(GTeam.GODS_ID) || team.getId().equals(GTeam.SPECS_ID)) ?
                                "§7Clic Gauche pour §2 + 1s"
                                        + "\n§7  (Shift pour §2 + 5s§7)"
                                        + "\n§7Clic Droit pour §4 - 1s"
                                        + "\n§7  (Shift pour §4 - 5s§7)"
                                        + "\n§7Clic Molette pour §fdéfinir"
                                : " \n  §cCette équipe n'est pas éliminable...\n \n§8" + Guis.loreSeparator),
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " teams " + team.getId() + " options --e §f<delay>"
                )
                .setCantClickOn(true)
                .setLeftRightShiftCommandOnClick(
                        Main.CMD  + " teams " + team.getId() + " options --e " + ((team
                                .getDefaultEliminationCooldown() / 20.0) + 1) + "\n" + refresh,
                        Main.CMD + " teams " + team.getId() + " options --e " + ((team
                                .getDefaultEliminationCooldown() / 20.0) + 5) + "\n" + refresh,
                        Main.CMD + " teams " + team.getId() + " options --e " + ((team
                                .getDefaultEliminationCooldown() / 20.0) - 1 < 0 ? 0 : (team
                                .getDefaultEliminationCooldown() / 20.0) - 1) + "\n" + refresh,
                        Main.CMD + " teams " + team.getId() + " options --e " + ((team
                                .getDefaultEliminationCooldown() / 20.0) - 5 < 0 ? 0 : (team
                                .getDefaultEliminationCooldown() / 20.0) - 5) + "\n" + refresh
                )
                .setMiddleCommandOnClick("input " + Main.CMD + " teams " + team.getId() + " options --e %f%n" + refresh)
                .build());
        inv.setItem(Utils.posOf(6, 3), Items.builder(Material.BRICK)
                .setName("§6Constructions")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        (!(team.getId().equals(GTeam.GODS_ID) || team.getId().equals(GTeam.SPECS_ID)) ?
                                "§7Clic Gauche pour créer l'Autel"
                                        + "\n§7Clic Droit pour poser §f1 §7couche de §8Cobble"
                                        + "\n§7  (Shift pour poser §fX §7couches de §8Y§7)"
                                : " \n  §cCette équipe n'a pas d'armor stand...\n \n§8" + Guis.loreSeparator),
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " teams " + team.getId() + " altar",
                        "§7/" + Main.CMD + " teams " + team.getId() + " wall §f1 §8cobblestone"
                )
                .setCantClickOn(true)
                .setLeftRightShiftCommandOnClick(
                        Main.CMD  + " teams " + team.getId() + " altar\n" + refresh,
                        refresh,
                        Main.CMD + " teams " + team.getId() + " wall 1 cobblestone\n" + refresh,
                        "input " + Main.CMD + " teams " + team.getId() + " wall %d %s%n" + refresh
                )
                .build());
        inv.setItem(Utils.posOf(6, 4), Items.builder(Material.ARMOR_STAND)
                .setName("§6ArmorStand")
                .setLore(
                        "§8" + Guis.loreSeparator,
                        (!(team.getId().equals(GTeam.GODS_ID) || team.getId().equals(GTeam.SPECS_ID)) ?
                                "§7Clic Gauche pour le §2montrer"
                                        + "\n§7Clic Droit pour le §4cacher"
                                : " \n  §cCette équipe n'a pas d'armor stand...\n \n§8" + Guis.loreSeparator),
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " teams " + team.getId() + " armorStand §8(§2show §8| §4hide§8)"
                )
                .setCantClickOn(true)
                .setLeftRightCommandOnClick(
                        Main.CMD + " teams " + team.getId() + " armorStand show\n" + refresh,
                        Main.CMD + " teams " + team.getId() + " armorStand hide\n" + refresh
                )
                .build());
        inv.setItem(Utils.posOf(7, 4), Items.builder(Items.red())
                .setName("§cStatus :  §f" + (team.getId().equals(GTeam.GODS_ID) || team.getId().equals(GTeam.SPECS_ID) ?
                        "§7Ne compte pas"
                        : team.isEliminated() ? "§4" + SpecialChars.NO + "  Éliminée"
                        : team.isEliminating() ? "§b" + SpecialChars.FLAG_FILLED + "  En cours d'élimination"
                        : "§2" + SpecialChars.YES + "  Participante"))
                .setLore(
                        "§8" + Guis.loreSeparator,
                        " ",
                        "  §8> Éliminateurs : §f" + (team.getEliminators() == null ? "§cnull"
                                : GManager.getCurrentGame().getTeam(team.getEliminators()).getName() != null ?
                                GManager.getCurrentGame().getTeam(team.getEliminators()).getColor()
                                        + GManager.getCurrentGame().getTeam(team.getEliminators()).getName()
                                : "§6" + team.getEliminators()),
                        " ",
                        "§8" + Guis.loreSeparator,
                        "§7Shift Clic Gauche pour §4éliminer",
                        "§7Shift Clic Droit pour §2réintroduire",
                        " ",
                        "§7Commande :",
                        "§7/" + Main.CMD + " teams §8(§4eliminate §8| §2reintroduce§8)§7 " + team.getId()
                )
                .setCantClickOn(true)
                .setLeftRightShiftCommandOnClick(
                        refresh,
                        Main.CMD  + " teams eliminate " + team.getId() + "\n" + refresh,
                        refresh,
                        Main.CMD + " teams reintroduce " + team.getId() + "\n" + refresh
                )
                .build());

        return inv;
    }
}
