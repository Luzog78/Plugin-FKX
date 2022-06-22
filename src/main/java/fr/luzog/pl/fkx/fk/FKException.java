package fr.luzog.pl.fkx.fk;

import java.util.UUID;

public class FKException extends RuntimeException {

    public FKException(String message) {
        super("[FKX Exception] " + message);
    }

    public static class TeamException extends FKException {
        public TeamException(String message) {
            super("[Team] " + message);
        }
    }

    public static class PlayerDoesNotExistException extends FKException {
        public PlayerDoesNotExistException(UUID uuid) {
            super("Player with uuid: '" + uuid + "' does not exist.");
        }

        public PlayerDoesNotExistException(String name) {
            super("Player with name: '" + name + "' does not exist.");
        }

        public PlayerDoesNotExistException(UUID uuid, String name) {
            super("Player with  uuid: '" + uuid + "' and name: '" + name + "' does not exist.");
        }
    }

    public static class DuplicateTeamIdException extends TeamException {
        public DuplicateTeamIdException(String id, String fromManager, String toManager) {
            super("Duplicate team id: '" + id + "' on managers from: '" + fromManager + "' to: '" + toManager + "'");
        }
    }

    public static class PlayerNotInTeamException extends TeamException {
        public PlayerNotInTeamException(String id, UUID playerUuid) {
            super("Player of uuid: '" + playerUuid + "' not in team: '" + id + "'");
        }

        public PlayerNotInTeamException(String id, String playerName) {
            super("Player of name: '" + playerName + "' not in team: '" + id + "'");
        }

        public PlayerNotInTeamException(String id, UUID playerUuid, String playerName) {
            super("Player of uuid: '" + playerUuid + "' and name: '" + playerName + "' not in team: '" + id + "'");
        }
    }

    public static class PlayerAlreadyInTeamException extends TeamException {
        public PlayerAlreadyInTeamException(String id, UUID playerUuid) {
            super("Player of uuid: '" + playerUuid + "' already in team: '" + id + "'");
        }

        public PlayerAlreadyInTeamException(String id, String playerName) {
            super("Player of name: '" + playerName + "' already in team: '" + id + "'");
        }

        public PlayerAlreadyInTeamException(String id, UUID playerUuid, String playerName) {
            super("Player of uuid: '" + playerUuid + "' and name: '" + playerName + "' already in team: '" + id + "'");
        }
    }

}
