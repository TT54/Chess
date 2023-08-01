package fr.tt54.chess.graphic;

import fr.tt54.chess.Main;
import fr.tt54.chess.game.EasyChessBoard;
import fr.tt54.chess.game.managers.ChessUserManager;
import fr.ttgraphiclib.graphics.GraphicPanel;
import fr.ttgraphiclib.graphics.interfaces.Movable;
import fr.ttgraphiclib.graphics.nodes.ImageNode;

import java.net.URL;

public class GraphicPiece extends ImageNode implements Movable {

    private EasyChessBoard.ChessPiece piece;
    private boolean isClicked = false;
    private int column;
    private int row;

    public GraphicPiece(GraphicPanel panel, ChessUserManager manager, double x, double y, EasyChessBoard.ChessPiece piece) {
        super(panel, x, y, 100, 100, getPieceImage(piece));
        this.piece = piece;
        this.column = this.getColumn();
        this.row = this.getRow();



        this.setClickAction(event -> {
            if(this.piece.isWhite() != manager.isWhite()){
                return;
            }

            if(!this.isClicked) {
                this.isClicked = true;

                Main.manager.setSelectedPiece(this.row, this.column);
            }
        });



        this.setMouseReleasedAction(event -> {
            this.isClicked = false;
        });
    }

    public int getColumn(){
        return (int) this.x/100+4;
    }

    public int getRow(){
        return 7 - (int) (this.y/100+4);
    }

    private static URL getPieceImage(EasyChessBoard.ChessPiece piece) {
        return GraphicPiece.class.getResource("/" + piece.name().toLowerCase() + ".png");
    }

    @Override
    public void move() {

    }
}
