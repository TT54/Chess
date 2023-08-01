package fr.tt54.chess.game.managers;

import fr.tt54.chess.game.EasyChessBoard;

public abstract class BotManager {

    public abstract void playMove(EasyChessBoard board);
    public abstract boolean isWhite();

}
