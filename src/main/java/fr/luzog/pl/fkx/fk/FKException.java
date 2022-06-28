package fr.luzog.pl.fkx.fk;

import java.util.UUID;

public class FKException extends RuntimeException {

    public FKException(String message) {
        super("[FKX Exception] " + message);
    }

    public static class IllegalGameIdException extends FKException {
        public IllegalGameIdException(String id) {
            super("Cannot create game with id: '" + id + "'");
        }
    }

    public static class TeamException extends FKException {
        public TeamException(String message) {
            super("[Team] " + message);
        }
    }

    public static class PlayerDoesNotExistException extends FKException {
        public PlayerDoesNotExistException(String name) {
            super("Player with name: '" + name + "' does not exist.");
        }
    }

    public static class DuplicateTeamIdException extends TeamException {
        public DuplicateTeamIdException(String id, String manager) {
            super("Duplicate team id: '" + id + "' on manager: '" + manager);
        }
    }

    public static class IllegalTeamIdException extends TeamException {
        public IllegalTeamIdException(String id, String manager) {
            super("Cannot set teamId to: '" + id + "' on manager: '" + manager);
        }
    }

    public static class CannotChangeTeamIdException extends TeamException {
        public CannotChangeTeamIdException(String teamId, String manager) {
            super("Cannot change id of team: '" + teamId + "' on manager: '" + manager);
        }
    }

    public static class PlayerNotInTeamException extends TeamException {
        public PlayerNotInTeamException(String id, String playerName) {
            super("Player of name: '" + playerName + "' not in team: '" + id + "'");
        }
    }

    public static class PlayerAlreadyInTeamException extends TeamException {
        public PlayerAlreadyInTeamException(String id, String playerName) {
            super("Player of name: '" + playerName + "' already in team: '" + id + "'");
        }
    }

}
