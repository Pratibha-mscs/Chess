package com.chess.backend.service;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.board.BoardSerializer;
import com.chess.backend.core.board.Position;
import com.chess.backend.core.engine.GameEngine;
import com.chess.backend.core.piece.*;
import com.chess.backend.dto.GameResponse;
import com.chess.backend.dto.GameStateDTO;
import com.chess.backend.dto.MoveRequest;
import com.chess.backend.model.*;
import com.chess.backend.repository.GameRepository;
import com.chess.backend.repository.MoveRepository;
import com.chess.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.chess.backend.ws.GameWebSocketService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final MoveRepository moveRepository;
    private final UserRepository userRepository;
    private final GameWebSocketService webSocketService;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository,
                           MoveRepository moveRepository,
                           UserRepository userRepository,
                           GameWebSocketService webSocketService) {
        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
        this.userRepository = userRepository;
        this.webSocketService = webSocketService;
    }

    @Override
    public Game createGame(String whiteUsername, String blackUsername) {
        User whitePlayer = Optional.ofNullable(userRepository.findByUsername(whiteUsername))
                .orElseThrow(() -> new RuntimeException("White user not found: " + whiteUsername));
        User blackPlayer = Optional.ofNullable(userRepository.findByUsername(blackUsername))
                .orElseThrow(() -> new RuntimeException("Black user not found: " + blackUsername));

        if (whitePlayer == null || blackPlayer == null) {
            throw new RuntimeException("User not found: white=" + whiteUsername + ", black=" + blackUsername);
        }

        Game game = new Game();
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.initializeBoard(); // Optional: set initial FEN or engine setup

        return gameRepository.save(game);
    }

    @Override
    public Game getGameById(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with ID: " + gameId));
    }

    @Override
    @Transactional
    public Game processMove(String gameId, MoveRequest moveRequest) {
        Game game = getGameById(gameId);
        Board board = BoardSerializer.fromFEN(game.getBoardState());
        GameEngine engine = new GameEngine(board);

        // Prepare core move and validate
        com.chess.backend.core.model.Move coreMove = new com.chess.backend.core.model.Move(
                moveRequest.getFromRow(),
                moveRequest.getFromCol(),
                moveRequest.getToRow(),
                moveRequest.getToCol()
        );

        boolean isValid = engine.makeMove(coreMove);
        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Move is not legal for this piece.");
        }

        // Persist move
        Move move = new Move();
        move.setId(UUID.randomUUID());
        move.setGame(game);
        move.setFromRow(moveRequest.getFromRow());
        move.setFromCol(moveRequest.getFromCol());
        move.setToRow(moveRequest.getToRow());
        move.setToCol(moveRequest.getToCol());
        move.setPieceType(moveRequest.getPieceType());
        move.setPromotion(moveRequest.getPromotionPiece());
        move.setCapture(false); // You can enhance this based on board state

        move.setMoveNumber(game.getMoves().size() + 1);
        move.setTimestamp(java.time.Instant.now());

        moveRepository.save(move);
        game.addMove(move);
        game.switchTurn(); // Implement this in your Game entity
        game.setBoardState(BoardSerializer.toFEN(board));

        Game updatedGame = gameRepository.save(game);

        return updatedGame;
    }

    @Override
    public GameResponse makeMove(String gameId, MoveRequest moveRequest) {
        Game game = gameRepository.findById(gameId).orElseThrow();

        // Deserialize board
        Board board = BoardSerializer.fromFEN(game.getBoardState());

        // Source and target
        Position from = new Position(moveRequest.getFromRow(), moveRequest.getFromCol());
        Position to = new Position(moveRequest.getToRow(), moveRequest.getToCol());

        // Validate bounds
        if (!from.isInsideBoard() || !to.isInsideBoard()) {
            throw new IllegalArgumentException("Invalid board coordinates.");
        }

        // Get piece and validate turn
        IPiece piece = board.getPieceAt(from);
        if (piece == null || piece.getColor() != game.getTurn()) {
            throw new IllegalArgumentException("No piece at source or wrong turn.");
        }

        // Validate move using piece's legalMoves logic
        List<Position> legalMoves = board.getLegalMoves(from);
        boolean isLegal = legalMoves.stream()
                .anyMatch(pos -> pos.getRow() == to.getRow() && pos.getCol() == to.getCol());

        if (!isLegal) {
            throw new IllegalArgumentException("Move is not legal for this piece.");
        }

        // Handle promotion logic
        if (moveRequest.getPromotionPiece() != null &&
                piece instanceof Pawn &&
                (to.getRow() == 0 || to.getRow() == 7)) {

            IPiece promoted = switch (moveRequest.getPromotionPiece()) {
                case "QUEEN"  -> new Queen(piece.getColor());
                case "ROOK"   -> new Rook(piece.getColor());
                case "BISHOP" -> new Bishop(piece.getColor());
                case "KNIGHT" -> new Knight(piece.getColor());
                default -> throw new IllegalArgumentException("Invalid promotion piece.");
            };

            board.setPieceAt(from, null);
            board.setPieceAt(to, promoted);
        } else {
            // Regular move
            board.setPieceAt(from, null);
            board.setPieceAt(to, piece);
        }

        // 🔄 Update FEN + turn
        game.setBoardState(BoardSerializer.toFEN(board));


        Color currentTurn = game.getTurn();
        Color opponent = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;

        // 🛑 Game end checks
        if (board.isCheckmate(opponent)) {
            game.setStatus((currentTurn == Color.WHITE)
                    ? GameStatus.BLACK_WINS : GameStatus.WHITE_WINS);
            System.out.println("💀 Checkmate detected. Winner: " + currentTurn);
        } else if (board.isStalemate(opponent)) {
            game.setStatus(GameStatus.DRAW);
        } else {
            game.setStatus(GameStatus.ONGOING);
        }

        game.setTurn(opponent); // ✅ switch turn at end


        Position kingInCheck = null;
        if (board.isKingInCheck(opponent)) {
            kingInCheck = board.findKingPosition(opponent);
        }

        int moveCount = moveRepository.findByGame(game).size();

        // Track if it's a capture
        boolean isCapture = board.getPieceAt(to) != null;

        // Create Move entity
        Move move = Move.builder()
                .game(game)
                .fromRow(from.getRow())
                .fromCol(from.getCol())
                .toRow(to.getRow())
                .toCol(to.getCol())
                .pieceType(moveRequest.getPieceType())
                .promotion(moveRequest.getPromotionPiece())
                .isCapture(isCapture)
                .moveNumber(moveCount + 1)
                .timestamp(Instant.now())
                .build();

        moveRepository.save(move);
        gameRepository.save(game);

        GameResponse response = new GameResponse();
        response.setId(game.getId());
        response.setBoardState(game.getBoardState());
        response.setTurn(game.getTurn());
        response.setStatus(game.getStatus());
        response.setCheckPosition(kingInCheck);

        webSocketService.sendGameUpdate(gameId, response);
        return response;
    }

    @Override
    public List<Position> getLegalMoves(String gameId, int row, int col) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        Board board = BoardSerializer.fromFEN(game.getBoardState());
        return board.getLegalMoves(new Position(row, col));
    }

    @Override
    public Game createGameWithDefaults() {
        Game game = gameRepository.findById("game-0001").orElseThrow(); // or create new Game if needed
        game.setBoardState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        game.setTurn(Color.WHITE);
        game.setStatus(GameStatus.ONGOING);
        return gameRepository.save(game);
    }

    public GameResponse partialGameStateOnError(String gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        Board board = BoardSerializer.fromFEN(game.getBoardState());
        System.out.println("🚨 Invalid move caught in controller");

        Position checkPos = null;
        if (board.isKingInCheck(game.getTurn())) {
            checkPos = board.findKingPosition(game.getTurn());
            System.out.println("✅ King in check at: " + checkPos);  // ✅ DEBUG
        }

        GameResponse response = new GameResponse();
        response.setId(game.getId());
        response.setBoardState(game.getBoardState());
        response.setTurn(game.getTurn());
        response.setStatus(game.getStatus());
        response.setCheckPosition(checkPos);
        return response;
    }

    @Override
    public GameResponse resignGame(String gameId, Color player) {
        Game game = gameRepository.findById(gameId).orElseThrow();

        if (game.getStatus() != GameStatus.ONGOING) {
            throw new IllegalStateException("Game is already over.");
        }

        game.setStatus(player == Color.WHITE ? GameStatus.BLACK_WINS : GameStatus.WHITE_WINS);
        gameRepository.save(game);
        webSocketService.sendGameUpdate(gameId, GameResponse.fromGame(game));
        return GameResponse.fromGame(game);
    }

    @Override
    public GameResponse offerDraw(String gameId, Color player) {
        Game game = gameRepository.findById(gameId).orElseThrow();

        if (game.getStatus() != GameStatus.ONGOING) {
            throw new IllegalStateException("Game is already over.");
        }

        game.setStatus(GameStatus.DRAW);
        gameRepository.save(game);
        webSocketService.sendGameUpdate(gameId, GameResponse.fromGame(game));
        return GameResponse.fromGame(game);
    }

    @Override
    public List<Move> getMoveHistory(String gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new IllegalArgumentException("Game not found with ID: " + gameId));
        return moveRepository.findByGame(game);
    }
}
