package fr.tt54.chess.game.managers;

import fr.tt54.chess.game.EasyChessBoard;
import fr.tt54.chess.game.moves.AbstractChessMove;
import fr.tt54.chess.game.objects.Tree;

import java.util.HashMap;
import java.util.Map;

public class MinMaxBotManager extends BotManager{

    private static final int DEPTH = 4;

    private boolean white;
    private final Map<EasyChessBoard, AbstractChessMove> movesMax = new HashMap<>();

    public MinMaxBotManager(boolean white) {
        this.white = white;
    }


    public void playMove(EasyChessBoard board){
        System.out.println("I try to find a move");
        BoardWithMove result = minMax(board.clone(), null, 4, true);
        System.out.println("I succeed");

        board.playMove(result.getMove());
    }

    @Override
    public boolean isWhite() {
        return this.white;
    }


    /**
     *
     * @return un nombre positif si l'ordinateur est gagnant, négatif sinon
     */
    public int evaluateBoard(EasyChessBoard board){
        int value = 0;

        if(board == null)
            return value;

        if(board.isMate(this.white)){
            return Integer.MIN_VALUE;
        }

        if(board.isMate(!this.white)){
            return Integer.MAX_VALUE;
        }

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++) {
                int piece = board.getPiece(i, j);

                if(piece == 0)
                    continue;

                if ((this.white && piece > 0) || (!this.white && piece < 0)) {
                    value += EasyChessBoard.ChessPiece.getPiece(piece).getValue() * 10 + board.getPieceMoves(i, j, false, false).size();
                } else {
                    value -= EasyChessBoard.ChessPiece.getPiece(piece).getValue() * 10 + board.getPieceMoves(i, j, false, false).size();
                }
            }
        }

        return value;
    }

    public int minMax(Tree<BoardWithMove> tree, int depth, boolean maximizing){
        if(depth == 0 || tree.isLeaf()){
            return evaluateBoard(tree.getValue().getBoard());
        }

        int value;
        if(maximizing){
            value = Integer.MIN_VALUE;
            for(Tree<BoardWithMove> child : tree.getChildren()){
                value = Math.max(value, minMax(child, depth - 1, false));
            }
        } else {
            value = Integer.MAX_VALUE;
            for(Tree<BoardWithMove> child : tree.getChildren()){
                value = Math.min(value, minMax(child, depth - 1, true));
            }
        }

        return value;
    }


    public BoardWithMove minMax(EasyChessBoard board, AbstractChessMove currentMove, int depth, boolean maximizing){
        if(depth == 1 || board.isMate(maximizing == this.white)){
            return new BoardWithMove(board, currentMove, evaluateBoard(board));
        }

        int value;
        BoardWithMove ret = null;
        if(maximizing){
            value = Integer.MIN_VALUE;
            for(AbstractChessMove move : board.getAllowedMoves(this.white)){
                EasyChessBoard copy = board.clone();
                copy.playMove(move);
                BoardWithMove calculated = minMax(copy, move, depth - 1, false);
                calculated.setMove(move);
                int newValue = Math.max(value, calculated.getEvaluation());
                if(newValue != value){
                    value = newValue;
                    ret = calculated;
                } else if(value == Integer.MIN_VALUE){
                    ret = calculated;
                }
            }
        } else {
            value = Integer.MAX_VALUE;
            for(AbstractChessMove move : board.getAllowedMoves(!this.white)){
                EasyChessBoard copy = board.clone();
                copy.playMove(move);
                BoardWithMove calculated = minMax(copy, move, depth - 1, true);
                int newValue = Math.min(value, calculated.getEvaluation());
                if(newValue != value){
                    value = newValue;
                    ret = calculated;
                } else if(value == Integer.MAX_VALUE){
                    ret = calculated;
                }
            }
        }

        return ret;
    }




    public class BoardWithMove{

        private EasyChessBoard board;
        private AbstractChessMove move;
        private int evaluation;

        public BoardWithMove(EasyChessBoard board, AbstractChessMove move, int evaluation) {
            this.board = board;
            this.move = move;
            this.evaluation = evaluation;
        }

        public EasyChessBoard getBoard() {
            return board;
        }

        public AbstractChessMove getMove() {
            return move;
        }

        public int getEvaluation() {
            return evaluation;
        }

        public void setMove(AbstractChessMove move) {
            this.move = move;
        }
    }

}
