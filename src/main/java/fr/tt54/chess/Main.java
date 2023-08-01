package fr.tt54.chess;

import fr.tt54.chess.game.managers.ChessUserManager;
import fr.ttgraphiclib.GraphicManager;
import fr.ttgraphiclib.graphics.GraphicPanel;
import fr.ttgraphiclib.thread.Frame;

public class Main {

    public static GraphicPanel panel = new GraphicPanel();
    public static Frame frame;
    public static ChessUserManager manager;

    public static void main(String[] args) {
        GraphicManager.setMaxFPS(30);
        GraphicManager.setMaxMovePerSecond(30);
        frame = new Frame("Chess", 900, 900);
        manager = new ChessUserManager();
        frame.setMainClass(manager);
        GraphicManager.enable(frame, panel);
    }

}
