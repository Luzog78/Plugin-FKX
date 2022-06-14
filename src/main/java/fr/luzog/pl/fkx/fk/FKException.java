package fr.luzog.pl.fkx.fk;

public class FKException extends RuntimeException {

    public FKException(String message) {
        super("[FKX Exception] " + message);
    }

    public static class DuplicateTeamIdException extends FKException {
        public DuplicateTeamIdException(String id, String fromManager, String toManager) {
            super("Duplicate team id: '" + id + "' on managers from: '" + fromManager + "' to: '" + toManager + "'");
        }
    }

}
