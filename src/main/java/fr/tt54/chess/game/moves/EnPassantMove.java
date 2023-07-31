package fr.tt54.chess.game.moves;

import fr.tt54.chess.game.ChessBoard;

import java.util.Set;

public class EnPassantMove extends AbstractChessMove{

    private final int previousPos;
    private final int capturedPos;
    private final int nextPos;

    public EnPassantMove(int piece, int previousPos, int capturedPos, int nextPos) {
        super(piece, piece > 0);
        this.previousPos = previousPos;
        this.capturedPos = capturedPos;
        this.nextPos = nextPos;
    }

    @Override
    public Set<Integer> getEditedPositions() {
        return Set.of(previousPos, capturedPos, nextPos);
    }

    @Override
    public void playMove(int[][] board) {
        int[] previous = ChessBoard.intToPosition(previousPos);
        int[] next = ChessBoard.intToPosition(nextPos);
        int[] captured = ChessBoard.intToPosition(capturedPos);

        board[next[0]][next[1]] = board[previous[0]][previous[1]];
        board[previous[0]][previous[1]] = 0;
        board[captured[0]][captured[1]] = 0;
    }

    @Override
    public int getAttackedPosition() {
        return this.capturedPos;
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
