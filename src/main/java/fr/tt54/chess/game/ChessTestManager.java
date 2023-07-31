package fr.tt54.chess.game;

import fr.tt54.chess.game.moves.AbstractChessMove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChessTestManager {

    private static List<String> toPrint = new ArrayList<>();

    public static int countLegalMoves(EasyChessBoard board, int depth){
        return countLegalMoves(board, depth, depth);
    }

    public static int countLegalMoves(EasyChessBoard board, int depth, int initialDepth){
        if(depth == initialDepth){
            toPrint.clear();
        }

        int count = 0;

        if(depth > 1) {
            for (AbstractChessMove chessMove : getAllLegalMoves(board)) {
                EasyChessBoard copy = board.clone();
                copy.playMove(chessMove);

                int localCount = countLegalMoves(copy, depth - 1, initialDepth);
                if(depth == initialDepth){
                    toPrint.add(ChessBoard.intToStringPosition(chessMove.getInitialPosition()) + ChessBoard.intToStringPosition(chessMove.getFinalPosition()) + " " + localCount);
                }
                count += localCount;
            }
        } else {
            count += getAllLegalMoves(board).size();
        }

        if(depth == initialDepth){
            for(String str : toPrint.stream().sorted().toList()){
                System.out.println(str);
/*                if(!ChessUserManager.verify.contains(str)){
                    System.out.println("WARING " + str);
                }*/
            }
        }

        return count;
    }


    public static Set<AbstractChessMove> getAllLegalMoves(ChessBoard board){
        Set<AbstractChessMove> legalMoves = new HashSet<>();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++) {
                int piece = board.getPiece(i, j);
                if (piece != 0) {
                    if (board.isWhiteToPlay() && piece > 0) {
                        legalMoves.addAll(board.getPieceMoves(i, j, false, false));
                    } else if (!board.isWhiteToPlay() && piece < 0) {
                        legalMoves.addAll(board.getPieceMoves(i, j, false, false));
                    }
                }
            }
        }
        return legalMoves;
    }

    public static Set<AbstractChessMove> getAllLegalMoves(EasyChessBoard board){
        Set<AbstractChessMove> legalMoves = new HashSet<>();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++) {
                int piece = board.getPiece(i, j);
                if (piece != 0) {
                    if (board.isWhiteToPlay() && piece > 0) {
                        legalMoves.addAll(board.getPieceMoves(i, j, false, false));
                    } else if (!board.isWhiteToPlay() && piece < 0) {
                        legalMoves.addAll(board.getPieceMoves(i, j, false, false));
                    }
                }
            }
        }
        return legalMoves;
    }

}
