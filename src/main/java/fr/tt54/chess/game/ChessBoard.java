package fr.tt54.chess.game;

import fr.tt54.chess.game.moves.*;

import java.util.*;

public class ChessBoard {


    private static final int[][] mult = new int[][] {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {1, 0}, {0, 1}, {0, -1}, {-1, 0}};

    private Map<Integer, Set<Integer>> positionsAttackedByBlack = new HashMap<>(); // Key : Case attaquée | Value : Pièces qui l'attaquent
    private Map<Integer, Set<Integer>> positionsAttackedByWhite = new HashMap<>(); // Key : Case attaquée | Value : Pièces qui l'attaquent

    private Map<Integer, Set<Integer>> blackPiecesAttacks = new HashMap<>(); // Key : Position de la pièce attaquante | Value : Positions attaquées
    private Map<Integer, Set<Integer>> whitePiecesAttacks = new HashMap<>(); // Key : Position de la pièce attaquante | Value : Positions attaquées

    private final int[][] board = new int[8][8];
    private int whiteKingPosition = -1;
    private int blackKingPosition = -1;
    private boolean whiteToPlay = true;
    private boolean whiteKingCastle = false, whiteQueenCastle = false, blackQueenCastle = false, blackKingCastle = false;
    private int enPassantPosition = -1; // Position lue : Colonne * 8 + Ligne
    private int halfMoves = 0, totalMoves = 0;


    public boolean updated = false; // Sert uniquement pour l'affichage, ne jamais utiliser ailleurs !


    public ChessBoard() {
        this.loadFEN(getDefaultFEN());
    }

    public ChessBoard(String fen){
        this.loadFEN(fen);
    }

    public String getFEN(){
        String fen = "";

        int amountEmptySpace = 0;
        for(int row = 7; row >= 0; row--){
            for(int column = 0; column < 8; column++){
                int pieceId = this.getPiece(row, column);
                if(pieceId == 0) {
                    amountEmptySpace++;
                } else {
                    ChessPiece piece = ChessPiece.getPiece(pieceId);
                    fen += ((amountEmptySpace == 0) ? "" : amountEmptySpace + "") + piece.getStringId();
                    amountEmptySpace = 0;
                }
            }

            if(amountEmptySpace > 0){
                fen += amountEmptySpace + "";
            }
            fen += "/";
            amountEmptySpace = 0;
        }
        fen = fen.substring(0, fen.length() - 1);

        fen += " " + (whiteToPlay ? "w" : "b");

        String castles = "";
        if(whiteKingCastle) castles += "K";
        if(whiteQueenCastle) castles += "Q";
        if(blackKingCastle) castles += "k";
        if(blackQueenCastle) castles += "q";

        fen += " " + (castles.isEmpty() ? "-" : castles);

        fen += " " + ((enPassantPosition == -1) ? "-" : intToStringPosition(enPassantPosition));

        fen += " " + this.halfMoves;
        fen += " " + this.totalMoves;


        return fen;
    }

    public void loadFEN(String fen){
        int row = 7;
        int column = 0;

        int fenPosition = 0;
        String[] split = fen.split(" ");

        for(int i = 0; i < split[0].length(); i++) {
            char c = split[0].charAt(i);
            if (c == '/') {
                row--;
                column = 0;
            } else if (Character.isDigit(c)) {
                int number = Integer.parseInt(c + "");
                column += number;
            } else {
                ChessPiece piece = ChessPiece.getPiece(c);
                board[row][column] = piece.getId();
                if(piece.getId() == 6){
                    whiteKingPosition = positionToInt(row, column);
                } else if(piece.getId() == -6){
                    blackKingPosition = positionToInt(row, column);
                }
                column++;
            }
        }

        whiteToPlay = split[1].charAt(0) == 'w';

        for(int i = 0; i < split[2].length(); i++) {
            char c = split[2].charAt(i);

            if(c == 'K'){
                whiteKingCastle = true;
            } else if(c == 'k'){
                blackKingCastle = true;
            } else if(c == 'Q'){
                whiteQueenCastle = true;
            } else if(c == 'q'){
                blackQueenCastle = true;
            }
        }

        if(split[3].equalsIgnoreCase("–")){
            enPassantPosition = -1;
        } else {
            for (int i = 0; i < split[3].length(); i++) {
                char c = split[3].charAt(i);

                if (Character.isLetter(c)) {
                    enPassantPosition = (c - 96 - 1) * 8;
                } else if (Character.isDigit(c)) {
                    enPassantPosition += Integer.parseInt(c + "");
                }
            }
        }

        halfMoves = Integer.parseInt(split[4]);
        totalMoves = Integer.parseInt(split[5]);

        this.loadAttackedPositions();
    }

    public int getPiece(int row, int column){
        if(row < 0 || column < 0 || row > 7 || column > 7)
            return Integer.MAX_VALUE;
        return board[row][column];
    }

    public int getPiece(int position){
        int[] pos = intToPosition(position);
        return getPiece(pos[0], pos[1]);
    }

    public boolean isWhiteToPlay() {
        return whiteToPlay;
    }


    public Set<AbstractChessMove> getPieceMoves(int row, int column, boolean countBlockedPositions, boolean countIllegalMoves){
        Set<AbstractChessMove> moves = new HashSet<>();
        int pieceId = this.getPiece(row, column);
        int currentPosition = positionToInt(row, column);
        ChessPiece piece = ChessPiece.getPiece(pieceId);

        if(piece.getIdWithoutColor() == 1){
            if(piece.getId() > 0){
                if(this.getPiece(row + 1, column) == 0){

                    // On vérifie si on peut promouvoir le pion
                    if(row + 1 == 7){
                        moves.add(new PromotionMove(pieceId, currentPosition, positionToInt(7, column), 2));
                        moves.add(new PromotionMove(pieceId, currentPosition, positionToInt(7, column), 3));
                        moves.add(new PromotionMove(pieceId, currentPosition, positionToInt(7, column), 4));
                        moves.add(new PromotionMove(pieceId, currentPosition, positionToInt(7, column), 5));
                    } else {
                        moves.add(new ChessMove(pieceId, currentPosition, positionToInt(row + 1, column)));
                    }

                    // On vérifie si le pion peut avancer de deux cases
                    if(row == 1 && this.getPiece(row + 2, column) == 0){
                        moves.add(new ChessMove(pieceId, currentPosition, positionToInt(row + 2, column)));
                    }
                }

                if(this.getPiece(row + 1, column + 1) < 0){
                    moves.add(new ChessMove(pieceId, currentPosition, positionToInt(row + 1, column + 1)));
                }

                if(this.getPiece(row + 1, column - 1) < 0){
                    moves.add(new ChessMove(pieceId, currentPosition, positionToInt(row + 1, column - 1)));
                }

                if(row == 4 && this.enPassantPosition >= 0){
                    int[] enPassant = intToPosition(this.enPassantPosition);
                    if(column == enPassant[1] + 1 || column == enPassant[1] - 1){
                        moves.add(new EnPassantMove(pieceId, currentPosition, this.enPassantPosition, enPassantPosition + 1));
                    }
                }
            } else {
                if(this.getPiece(row - 1, column) == 0){
                    // On vérifie si on peut promouvoir le pion
                    if(row - 1 == 0){
                        moves.add(new PromotionMove(pieceId, currentPosition, positionToInt(0, column), -2));
                        moves.add(new PromotionMove(pieceId, currentPosition, positionToInt(0, column), -3));
                        moves.add(new PromotionMove(pieceId, currentPosition, positionToInt(0, column), -4));
                        moves.add(new PromotionMove(pieceId, currentPosition, positionToInt(0, column), -5));
                    } else {
                        moves.add(new ChessMove(pieceId, currentPosition, positionToInt(row - 1, column)));
                    }

                    // On vérifie si le pion peut avancer de deux cases
                    if(row == 6 && this.getPiece(row - 2, column) == 0){
                        moves.add(new ChessMove(pieceId, currentPosition, positionToInt(row - 2, column)));
                    }
                }

                int target = this.getPiece(row - 1, column - 1);
                if(target > 0 && target < 10){
                    moves.add(new ChessMove(pieceId, currentPosition, positionToInt(row - 1, column - 1)));
                }

                target = this.getPiece(row - 1, column + 1);
                if(target > 0 && target < 10){
                    moves.add(new ChessMove(pieceId, currentPosition, positionToInt(row - 1, column + 1)));
                }

                if(row == 3 && this.enPassantPosition >= 0){
                    int[] enPassant = intToPosition(enPassantPosition);
                    if(column == enPassant[1] + 1 || column == enPassant[1] - 1){
                        moves.add(new EnPassantMove(pieceId, currentPosition, this.enPassantPosition, enPassantPosition - 1));
                    }
                }
            }

        } else if(piece.getIdWithoutColor() == 2){
            for(int i = -1; i < 2; i+=2){
                verifyNormalMove(currentPosition, pieceId, row + 1, column + i * 2, moves, countBlockedPositions);
                verifyNormalMove(currentPosition, pieceId, row - 1, column + i * 2, moves, countBlockedPositions);
                verifyNormalMove(currentPosition, pieceId, row + i * 2, column + 1, moves, countBlockedPositions);
                verifyNormalMove(currentPosition, pieceId, row + i * 2, column - 1, moves, countBlockedPositions);
            }
        } else if(piece.getIdWithoutColor() == 3){
            verifySlidingPieceMoves(currentPosition, pieceId, row, column, moves, 0, 4, countBlockedPositions);
        } else if(piece.getIdWithoutColor() == 4){
            verifySlidingPieceMoves(currentPosition, pieceId, row, column, moves, 4, 8, countBlockedPositions);
        } else if(piece.getIdWithoutColor() == 5){
            verifySlidingPieceMoves(currentPosition, pieceId, row, column, moves, 0, 8, countBlockedPositions);
        } else if(piece.getIdWithoutColor() == 6){
            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2; j++){
                    if(i != 0 || j != 0){
                        verifyNormalMove(currentPosition, pieceId, row + i, column + j, moves, countBlockedPositions);
                    }
                }
            }
            if(pieceId > 0){
                if(!isAttackedBy(positionToInt(0, 2), false) && !isAttackedBy(positionToInt(0, 3), false) && !isAttackedBy(positionToInt(0, 4), false) && whiteQueenCastle && this.getPiece(0, 3) == 0 && this.getPiece(0, 2) == 0 && this.getPiece(0, 1) == 0){
                    moves.add(new CastleMove(pieceId, false));
                }
                if(!isAttackedBy(positionToInt(0, 4), false) && !isAttackedBy(positionToInt(0, 5), false) && !isAttackedBy(positionToInt(0, 6), false) && whiteKingCastle && this.getPiece(0, 5) == 0 && this.getPiece(0, 6) == 0){
                    moves.add(new CastleMove(pieceId, true));
                }
            } else{
                if(!isAttackedBy(positionToInt(7, 2), true) && !isAttackedBy(positionToInt(7, 3), true) && !isAttackedBy(positionToInt(7, 4), true) && blackQueenCastle && this.getPiece(7, 3) == 0 && this.getPiece(7, 2) == 0 && this.getPiece(7, 1) == 0){
                    moves.add(new CastleMove(pieceId, false));
                }
                if(!isAttackedBy(positionToInt(7, 4), true) && !isAttackedBy(positionToInt(7, 5), true) && !isAttackedBy(positionToInt(7, 6), true) && blackKingCastle && this.getPiece(7, 5) == 0 && this.getPiece(7, 6) == 0){
                    moves.add(new CastleMove(pieceId, true));
                }
            }
        }

        if(!countIllegalMoves){
            final Set<AbstractChessMove> illegalMoves = new HashSet<>();
            for(AbstractChessMove chessMove : moves){
                ChessBoard copy = this.clone();

                copy.playMove(chessMove);

                if(copy.isKingAttacked(piece.isWhite())){
                    illegalMoves.add(chessMove);
                }
            }

            for(AbstractChessMove chessMove : illegalMoves){
                moves.remove(chessMove);
            }
        }


        return moves;
    }

    public void playMove(AbstractChessMove chessMove){
        if(getPiece(chessMove.getAttackedPosition()) != 0){
            clearAttacksOf(chessMove.getAttackedPosition());
        }

        chessMove.playMove(this.board);
        this.enPassantPosition = chessMove.getEnPassantResult();

        if(chessMove instanceof CastleMove){
            if(chessMove.isWhite()) {
                this.whiteKingCastle = false;
                this.whiteQueenCastle = false;
            } else {
                this.blackQueenCastle = false;
                this.blackKingCastle = false;
            }
        }

        if(chessMove instanceof ChessMove move){
            boolean[] castleResult = new boolean[] {this.whiteKingCastle, this.whiteQueenCastle, this.blackKingCastle, this.blackQueenCastle};
            move.getCastleResults(castleResult);
            this.whiteKingCastle = castleResult[0];
            this.whiteQueenCastle = castleResult[1];
            this.blackKingCastle = castleResult[2];
            this.blackQueenCastle = castleResult[3];
        }

        // On modifie la position du roi si nécessaire
        int piece = chessMove.getPiece();
        if(piece == 6){
            this.whiteKingPosition = chessMove.getFinalPosition();
        } else if(piece == -6){
            this.blackKingPosition = chessMove.getFinalPosition();
        }

        // On modifie les positions attaquées par les pièces qui ont été affectées par le coup
        for(int editedPos : chessMove.getEditedPositions()){
            for(int attackerPos : new ArrayList<>(getWhitePiecesAttacking(editedPos))){
                loadPieceAttack(attackerPos);
                refreshAttackers(attackerPos);
            }
            for(int attackerPos : new ArrayList<>(getBlackPiecesAttacking(editedPos))){
                loadPieceAttack(attackerPos);
                refreshAttackers(attackerPos);
            }
        }

        // On modifie les positions attackées par notre pièce
        loadPieceAttack(chessMove.getFinalPosition());
        refreshAttackers(chessMove.getFinalPosition());
        if(chessMove instanceof CastleMove castleMove){
            loadPieceAttack(castleMove.getRookPosition());
            refreshAttackers(castleMove.getRookPosition());
        }


        whiteToPlay = !whiteToPlay;

        // Permet d'update graphiquement le plateau
        updated = false;
    }

    public boolean isKingAttacked(boolean white){
        return white ?
                getBlackPiecesAttacking(this.whiteKingPosition).size() > 0
                :
                getWhitePiecesAttacking(this.blackKingPosition).size() > 0;
    }

    public void loadAttackedPositions(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                int position = positionToInt(i, j);
                loadPieceAttack(position);
            }
        }
    }

    public void refreshAttackers(int position){
        for(int attacker : new ArrayList<>(getWhitePiecesAttacking(position))){
            loadPieceAttack(attacker);
        }
        for(int attacker : new ArrayList<>(getBlackPiecesAttacking(position))){
            loadPieceAttack(attacker);
        }
    }

    public void loadPieceAttack(int piecePosition){
        clearAttacksOf(piecePosition);
        int piece = this.getPiece(piecePosition);

        if(piece == 0){
            return;
        }

        int[] pos = intToPosition(piecePosition);
        int row = pos[0];
        int column = pos[1];


        Set<Integer> attacks = new HashSet<>();

        if(Math.abs(piece) != 1) {
            for (AbstractChessMove chessMove : this.getPieceMoves(row, column, true, true)) {
                int attackedPos = chessMove.getAttackedPosition();

                if (attackedPos >= 0) {
                    attacks.add(attackedPos);
                }
            }
        } else {
            if(piece == 1){
                // Pion blanc

                attacks.add(positionToInt(row + 1, column - 1));
                attacks.add(positionToInt(row + 1, column + 1));
            } else {
                // Pion noir

                attacks.add(positionToInt(row - 1, column - 1));
                attacks.add(positionToInt(row - 1, column + 1));
            }
        }

        if(piece > 0){
            // Pièce blanche

            this.whitePiecesAttacks.put(piecePosition, attacks);
            for(int attack : attacks){
                Set<Integer> attackers = this.getWhitePiecesAttacking(attack);
                attackers.add(piecePosition);
                this.positionsAttackedByWhite.put(attack, attackers);
            }
        } else {
            // Pièce noire

            this.blackPiecesAttacks.put(piecePosition, attacks);
            for(int attack : attacks){
                Set<Integer> attackers = this.getBlackPiecesAttacking(attack);
                attackers.add(piecePosition);
                this.positionsAttackedByBlack.put(attack, attackers);
            }
        }
    }

    private void clearAttacksOf(int piecePosition) {
        for(int attacked : getWhitePieceAttacks(piecePosition)){
            removeWhitePieceAttack(piecePosition, attacked);
        }
        this.whitePiecesAttacks.remove(piecePosition);
        this.blackPiecesAttacks.remove(piecePosition);
    }

    private void removeWhitePieceAttack(int piecePosition, int attack){
        Set<Integer> attackers = getWhitePiecesAttacking(attack);
        attackers.remove(piecePosition);
        this.positionsAttackedByWhite.put(attack, attackers);

        attackers = getBlackPiecesAttacking(attack);
        attackers.remove(piecePosition);
        this.positionsAttackedByBlack.put(attack, attackers);

        /* Ce code a été retiré pour optimiser les calculs

        Set<Integer> attacked = getWhitePieceAttacks(piecePosition);
        attacked.remove(attack);
        this.whitePiecesAttacks.put(piecePosition, attacked);

        attacked = getBlackPieceAttacks(piecePosition);
        attacked.remove(attack);
        this.blackPiecesAttacks.put(piecePosition, attacked);*/
    }

    public Set<Integer> getWhitePieceAttacks(int piecePosition){
        return this.whitePiecesAttacks.getOrDefault(piecePosition, new HashSet<>());
    }

    public Set<Integer> getBlackPieceAttacks(int piecePosition){
        return this.blackPiecesAttacks.getOrDefault(piecePosition, new HashSet<>());
    }

    public Set<Integer> getWhitePiecesAttacking(int attackedPosition){
        return this.positionsAttackedByWhite.getOrDefault(attackedPosition, new HashSet<>());
    }

    public Set<Integer> getBlackPiecesAttacking(int attackedPosition){
        return this.positionsAttackedByBlack.getOrDefault(attackedPosition, new HashSet<>());
    }

    public void verifySlidingPieceMoves(int currentPosition, int pieceId, int row, int column, Set<AbstractChessMove> moves, int startingRange, int endingRange, boolean countBlockedPositions){
        for(int k = startingRange; k < endingRange; k++) {
            int[] coeffs = mult[k];
            for (int i = 1; i < 8; i++) {
                int newRow = row + coeffs[0] * i;
                int newColumn = column + coeffs[1] * i;

                int target = this.getPiece(newRow, newColumn);
                if(target > 10 || (target * pieceId > 0 && !countBlockedPositions)){
                    break;
                }

                moves.add(new ChessMove(pieceId, currentPosition, positionToInt(newRow, newColumn)));
                if(target * pieceId != 0){
                    break;
                }
            }
        }
    }

    public void verifyNormalMove(int currentPosition, int pieceId, int row, int column, Set<AbstractChessMove> moves, boolean countBlockedPositions){
        int target = this.getPiece(row, column);
        if(target < 10 && (countBlockedPositions || target * pieceId <= 0)){
            moves.add(new ChessMove(pieceId, currentPosition, positionToInt(row, column)));
        }
    }

    public boolean isAttackedBy(int position, boolean white){
        if(white){
            return this.positionsAttackedByWhite.containsKey(position) && this.positionsAttackedByWhite.get(position).size() > 0;
        } else {
            return this.positionsAttackedByBlack.containsKey(position) && !this.positionsAttackedByBlack.get(position).isEmpty();
        }
    }

    @Override
    public ChessBoard clone(){
        ChessBoard board = new ChessBoard(this.getFEN());
        return board;
    }







    public static int positionToInt(int row, int column){
        return column * 8 + row;
    }

    public static String positionToStringPosition(int row, int column) {
        return ((char) (column + 1 + 96)) + "" + (row+1);
    }

    public static String intToStringPosition(int position) {
        int[] pos = intToPosition(position);
        return positionToStringPosition(pos[0], pos[1]);
    }

    /**
     *
     * @param i
     * @return 0 : ligne / 1 : colonne
     */
    public static int[] intToPosition(int i){
        return new int[] {i % 8, i / 8};
    }

    public static String getDefaultFEN(){
        return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq – 0 1";
    }


    public static enum ChessPiece{

        WHITE_PAWN(1, 1, "P", true),
        WHITE_KNIGHT(2, 3, "N", true),
        WHITE_BISHOP(3, 3, "B", true),
        WHITE_ROOK(4, 5, "R", true),
        WHITE_QUEEN(5, 9, "Q", true),
        WHITE_KING(6, 100, "K", true),
        BLACK_PAWN(-1, -1, "p", false),
        BLACK_KNIGHT(-2, -3, "n", false),
        BLACK_BISHOP(-3, -3, "b", false),
        BLACK_ROOK(-4, -5, "r", false),
        BLACK_QUEEN(-5, -9, "q", false),
        BLACK_KING(-6, -100, "k", false);




        private static Map<Integer, ChessPiece> pieceMap = new HashMap<>();
        private static Map<String, ChessPiece> pieceStringMap = new HashMap<>();

        static {
            for (ChessPiece piece : values()){
                pieceMap.put(piece.id, piece);
                pieceStringMap.put(piece.stringId, piece);
            }
        }

        public static ChessPiece getPiece(int id){
            return pieceMap.getOrDefault(id, WHITE_PAWN);
        }

        public static ChessPiece getPiece(String stringId){
            return pieceStringMap.getOrDefault(stringId, WHITE_PAWN);
        }

        public static ChessPiece getPiece(char c){
            return getPiece(c + "");
        }


        private int id;
        private int value;
        private String stringId;
        private boolean isWhite;

        ChessPiece(int id, int value, String stringId, boolean isWhite){
            this.id = id;
            this.value = value;
            this.stringId = stringId;
            this.isWhite = isWhite;
        }

        public int getId() {
            return id;
        }

        public int getIdWithoutColor(){
            return Math.abs(id);
        }

        public int getValue() {
            return value;
        }

        public String getStringId() {
            return stringId;
        }

        public boolean isWhite() {
            return this.isWhite;
        }
    }

}
