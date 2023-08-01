package fr.tt54.chess.game.managers;

import fr.tt54.chess.Main;
import fr.tt54.chess.game.EasyChessBoard;
import fr.tt54.chess.game.moves.AbstractChessMove;
import fr.tt54.chess.graphic.GraphicPiece;
import fr.ttgraphiclib.graphics.GraphicPanel;
import fr.ttgraphiclib.graphics.nodes.GraphicNode;
import fr.ttgraphiclib.graphics.nodes.RectangleNode;
import fr.ttgraphiclib.thread.Frame;
import fr.ttgraphiclib.thread.MainClass;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChessUserManager extends MainClass {

    private EasyChessBoard board;
    private int selectedPiece = -1;
    private GraphicNode selectNode = null;
    private java.util.List<GraphicNode> allowedMovesNodes = new ArrayList<>();
    private List<GraphicNode> attackedPositionsNodes = new ArrayList<>();
    private boolean white;

    private BotManager botManager = null;

    private Set<AbstractChessMove> allowedMoves = new HashSet<>();

    private static boolean showControls = false;


    public ChessUserManager() {
/*        this.board = new EasyChessBoard("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");

        long begin = System.currentTimeMillis();
        System.out.println(ChessTestManager.countLegalMoves(this.board, 6));

        System.out.println("Temps de calcul : " + ((System.currentTimeMillis() - begin) / 1000d) + "s");*/

        this.board = new EasyChessBoard();
        this.white = true;


        botManager = new MinMaxBotManager(!this.white);





        this.drawBoard();
    }

    @Override
    public void doTickContent(Frame frame) {
        if(!this.board.updated){
            this.updatePanel();
            this.board.updated = true;

            if(this.board.isWhiteToPlay() == botManager.isWhite()){
                System.out.println("Bot starting to play");
                botManager.playMove(this.board);
            }
        }
    }

    private void drawBoard() {
        GraphicPanel panel = Main.panel;
        panel.addNode(new RectangleNode(panel, -405, -405, 810, 810, Color.BLACK));

        boolean white = false;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                RectangleNode rect = new RectangleNode(panel, -400 + j * 100, -400 + (7 - i) * 100, 100, 100, white ? new Color(245, 230, 230) : new Color(100, 75, 75));
                final int row = i;
                final int column = j;
                rect.setClickAction(event -> {
                    if(selectedPiece >= 0){
                        int position = EasyChessBoard.positionToInt(row, column);
                        moveSelectedPiece(position);
                    }
                    unselectPiece();
                });
                white = !white;
            }
            white = !white;
        }
    }

    private void updatePanel() {
        GraphicPanel panel = Main.panel;
        List<GraphicNode> nodes = panel.getNodes().stream().filter(graphicNode -> graphicNode instanceof GraphicPiece).toList();
        List<GraphicNode> removeAttacked = new ArrayList<>(attackedPositionsNodes);

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                int value = board.getPiece(i, j);
                if(value != 0) {
                    EasyChessBoard.ChessPiece piece = EasyChessBoard.ChessPiece.getPiece(value);
                    new GraphicPiece(panel, this, -400 + j * 100, -400 + (7 - i) * 100, piece);
                }
            }
        }

        nodes.forEach(panel::removeNode);
        removeAttacked.forEach(panel::removeNode);
    }

    private void moveSelectedPiece(int targetPosition){
        if(selectedPiece == -1)
            return;

        List<AbstractChessMove> candidates = new ArrayList<>();
        for(AbstractChessMove move : allowedMoves){
            if(move.getFinalPosition() == targetPosition){
                candidates.add(move);
            }
        }

        if(candidates.size() == 0)
            return;

        board.playMove(candidates.get(0));
    }

    public void setSelectedPiece(int row, int column) {
        int position = EasyChessBoard.positionToInt(row, column);
        if(selectedPiece != position) {
            unselectPiece();
            int id = board.getPiece(row, column);
            EasyChessBoard.ChessPiece piece = EasyChessBoard.ChessPiece.getPiece(id);
            if (piece.isWhite() == board.isWhiteToPlay()) {
                this.selectedPiece = position;
                selectNode = new RectangleNode(Main.panel, -400 + column * 100, -400 + (7 - row) * 100, 100, 100, new Color(150, 150, 255, 50));

                allowedMoves.addAll(board.getPieceMoves(row, column, false, false));

                for (AbstractChessMove allowed : allowedMoves) {
                    int[] allowedPos = EasyChessBoard.intToPosition(allowed.getFinalPosition());
                    allowedMovesNodes.add(new RectangleNode(Main.panel, -400 + 25 + allowedPos[1] * 100, -400 + 25 + (7 - allowedPos[0]) * 100, 50, 50, new Color(100, 100, 255, 150)));
                }
            }
        }
    }

    public void unselectPiece(){
        this.selectedPiece = -1;
        GraphicPanel panel = Main.panel;

        if(this.selectNode != null){
            panel.removeNode(selectNode);
        }

        allowedMoves.clear();
        for(GraphicNode node : allowedMovesNodes){
            panel.removeNode(node);
        }
        allowedMovesNodes.clear();
    }

    public boolean isWhite() {
        return white;
    }

    public BotManager getBotManager() {
        return botManager;
    }
}
