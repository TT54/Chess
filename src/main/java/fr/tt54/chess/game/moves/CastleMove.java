package fr.tt54.chess.game.moves;

import fr.tt54.chess.game.ChessBoard;

import java.util.Set;

public class CastleMove extends AbstractChessMove{

    private final boolean kingSide;

    public CastleMove(int piece, boolean kingSide) {
        super(piece, piece > 0);
        this.kingSide = kingSide;
    }

    @Override
    public Set<Integer> getEditedPositions() {
        if(isWhite()){
            if(kingSide){
                return Set.of(ChessBoard.positionToInt(0, 7), ChessBoard.positionToInt(0, 4));
            } else {
                return Set.of(ChessBoard.positionToInt(0, 0), ChessBoard.positionToInt(0, 4));
            }
        } else {
            if(kingSide){
                return Set.of(ChessBoard.positionToInt(7, 7), ChessBoard.positionToInt(7, 4));
            } else {
                return Set.of(ChessBoard.positionToInt(7, 0), ChessBoard.positionToInt(7, 4));
            }
        }
    }

    @Override
    public void playMove(int[][] board) {
        if(isWhite()){
            if(kingSide){
                board[0][7] = 0; // La case h1 devient vide
                board[0][4] = 0; // La case e1 devient vide
                board[0][5] = 4; // La case f1 devient une tour blanche
                board[0][6] = 6; // La case g1 devient un roi blanc
            } else {
                board[0][0] = 0; // La case a1 devient vide
                board[0][4] = 0; // La case e1 devient vide
                board[0][3] = 4; // La case d1 devient une tour blanche
                board[0][2] = 6; // La case c1 devient un roi blanc
            }
        } else {
            if(kingSide){
                board[7][7] = 0; // La case h8 devient vide
                board[7][4] = 0; // La case e8 devient vide
                board[7][5] = -4; // La case f8 devient une tour noire
                board[7][6] = -6; // La case g8 devient un roi noir
            } else {
                board[7][0] = 0; // La case a8 devient vide
                board[7][4] = 0; // La case e8 devient vide
                board[7][3] = -4; // La case d8 devient une tour noire
                board[7][2] = -6; // La case c8 devient un roi noir
            }
        }
    }

    @Override
    public int getAttackedPosition() {
        return -1;
    }

    @Override
    public int getEnPassantResult() {
        return -1;
    }

    @Override
    public int getInitialPosition() {
        if(isWhite()){
            return ChessBoard.positionToInt(0, 4);
        } else {
            return ChessBoard.positionToInt(7, 4);
        }
    }

    @Override
    public int getFinalPosition() {
        if(isWhite()){
            if(kingSide){
                return ChessBoard.positionToInt(0, 6);
            } else {
                return ChessBoard.positionToInt(0, 2);
            }
        } else {
            if(kingSide){
                return ChessBoard.positionToInt(7, 6);
            } else {
                return ChessBoard.positionToInt(7, 2);
            }
        }
    }

    public boolean isKingSide() {
        return kingSide;
    }

    public int getRookPosition(){
        if(isWhite()){
            if(kingSide){
                return ChessBoard.positionToInt(0, 5);
            } else {
                return ChessBoard.positionToInt(0, 3);
            }
        } else {
            if(kingSide){
                return ChessBoard.positionToInt(7, 5);
            } else {
                return ChessBoard.positionToInt(7, 3);
            }
        }
    }
}
