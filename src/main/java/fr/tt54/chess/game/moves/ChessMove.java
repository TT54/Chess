package fr.tt54.chess.game.moves;

import fr.tt54.chess.game.EasyChessBoard;

import java.util.Set;

public class ChessMove extends AbstractChessMove{

    private final int previousPos;
    private final int nextPos;

    public ChessMove(int piece, int previousPos, int nextPos) {
        super(piece, piece > 0);
        this.previousPos = previousPos;
        this.nextPos = nextPos;
    }

    @Override
    public Set<Integer> getEditedPositions() {
        return Set.of(previousPos, nextPos);
    }

    @Override
    public void playMove(int[][] board) {
        int[] previous = EasyChessBoard .intToPosition(previousPos);
        int[] next = EasyChessBoard .intToPosition(nextPos);

        board[next[0]][next[1]] = board[previous[0]][previous[1]];
        board[previous[0]][previous[1]] = 0;
    }

    @Override
    public int getAttackedPosition() {
        return this.nextPos;
    }

    @Override
    public int getEnPassantResult() {
        int piece = this.getPiece();
        int[] previous = EasyChessBoard .intToPosition(previousPos);
        int[] next = EasyChessBoard .intToPosition(nextPos);
        if((piece == 1 || piece == -1) && (previous[0] + 2 == next[0] || previous[0] - 2 == next[0])){
            return nextPos;
        }

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

    /**
     * Modifie les états des rocks en fonction du coup
     * @param currentCastles les états des rocks actuels : [blanc côté roi, blanc côté dame, noir côté roi, noir côté dame]
     */
    public void getCastleResults(boolean[] currentCastles){
        if(previousPos == EasyChessBoard .positionToInt(0, 0)){
            currentCastles[1] = false;
        } else if(previousPos == EasyChessBoard .positionToInt(0, 7)){
            currentCastles[0] = false;
        } else if(previousPos == EasyChessBoard .positionToInt(0, 4)){
            currentCastles[0] = false;
            currentCastles[1] = false;
        } else if(previousPos == EasyChessBoard .positionToInt(7, 0)){
            currentCastles[3] = false;
        } else if(previousPos == EasyChessBoard .positionToInt(7, 7)){
            currentCastles[2] = false;
        } else if(previousPos == EasyChessBoard .positionToInt(7, 4)){
            currentCastles[2] = false;
            currentCastles[3] = false;
        }
    }
}
