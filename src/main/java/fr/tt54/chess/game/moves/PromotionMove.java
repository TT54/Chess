package fr.tt54.chess.game.moves;

import fr.tt54.chess.game.ChessBoard;

import java.util.Set;

public class PromotionMove extends AbstractChessMove{

    private final int previousPos;
    private final int nextPos;
    private final int promotionResult;

    public PromotionMove(int piece, int previousPos, int nextPos, int promotionResult) {
        super(piece, piece > 0);
        this.previousPos = previousPos;
        this.nextPos = nextPos;
        this.promotionResult = promotionResult;
    }

    @Override
    public Set<Integer> getEditedPositions() {
        return Set.of(previousPos, nextPos);
    }

    @Override
    public void playMove(int[][] board) {
        int[] previous = ChessBoard.intToPosition(previousPos);
        int[] next = ChessBoard.intToPosition(nextPos);

        board[previous[0]][previous[1]] = 0;
        board[next[0]][next[1]] = promotionResult;
    }

    @Override
    public int getAttackedPosition() {
        return nextPos;
    }

    @Override
    public int getEnPassantResult() {
        return -1;
    }

    @Override
    public int getInitialPosition() {
        return previousPos;
    }

    @Override
    public int getFinalPosition() {
        return nextPos;
    }
}
