package fr.tt54.chess.game.moves;

import java.util.Set;

public abstract class AbstractChessMove {

    private int piece;
    private boolean white;

    public AbstractChessMove(int piece, boolean white) {
        this.piece = piece;
        this.white = white;
    }

    public abstract Set<Integer> getEditedPositions();
    public abstract void playMove(int[][] board);
    public abstract int getAttackedPosition();
    public abstract int getEnPassantResult();
    public abstract int getInitialPosition();
    public abstract int getFinalPosition();

    public int getPiece() {
        return piece;
    }

    public boolean isWhite() {
        return white;
    }
}
