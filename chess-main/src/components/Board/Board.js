import React from 'react';
import './Board.css';
import Ranks from './bits/Ranks';
import Files from './bits/Files';
import Pieces from '../Pieces/Pieces';
import PromotionBox from '../Popup/PromotionBox/PromotionBox';
import Popup from '../Popup/Popup';
import GameEnds from '../Popup/GameEnds/GameEnds';

const Board = ({
  board,
  setGame,
  gameId,
  turn,
  legalMoves,
  setLegalMoves,
  checkPosition // ✅ King in check comes from backend
}) => {
  const ranks = Array(8).fill().map((_, i) => 8 - i);
  const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];

  const getClassName = (i, j) => {
    let c = 'tile';
    c += (i + j) % 2 === 0 ? ' tile--dark' : ' tile--light';

    // ✅ Highlight legal moves
    if (legalMoves.some(pos => pos.row === i && pos.col === j)) {
      c += ' highlight';
    }

    // ✅ Simple king check outline
    if (checkPosition && checkPosition.row === i && checkPosition.col === j) {
      c += ' king-in-check';
    }

    return c;
  };

  const handleDrop = async (e) => {
    e.preventDefault();
    const boardRect = e.currentTarget.getBoundingClientRect();
    const tileSize = boardRect.width / 8;

    const [piece, fromRow, fromCol] = e.dataTransfer.getData('text').split(',');
    if (!piece) return;

    const toCol = Math.floor((e.clientX - boardRect.left) / tileSize);
    const toRow = 7 - Math.floor((e.clientY - boardRect.top) / tileSize);

    const promotion =
      (piece === 'P' && toRow === 0) || (piece === 'p' && toRow === 7)
        ? 'QUEEN'
        : null;

    try {
      const res = await fetch(`${process.env.REACT_APP_API_URL}/api/game/${gameId}/move`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fromRow: parseInt(fromRow),
          fromCol: parseInt(fromCol),
          toRow,
          toCol,
          pieceType: mapToEnum(piece),
          promotionPiece: promotion,
        }),
      });

      const updatedGame = await res.json();
      setGame(updatedGame);
    } catch (err) {
      console.error('Move failed:', err.message);
    }
  };

  const mapToEnum = (piece) => {
    const map = {
      p: 'PAWN', r: 'ROOK', n: 'KNIGHT', b: 'BISHOP', q: 'QUEEN', k: 'KING',
      P: 'PAWN', R: 'ROOK', N: 'KNIGHT', B: 'BISHOP', Q: 'QUEEN', K: 'KING',
    };
    return map[piece.toLowerCase()];
  };

  return (
    <div className='board'>
      <Ranks ranks={ranks} />

      <div className="board-container">
        <div className="tiles" onDrop={handleDrop} onDragOver={(e) => e.preventDefault()}>
          {ranks.map((rank, i) =>
            files.map((_, j) => {
              const tileRow = 7 - i;
              const tileCol = j;

              return (
                <div
                  key={`tile-${tileRow}-${tileCol}`}
                  className={getClassName(tileRow, tileCol)}
                ></div>
              );
            })
          )}
        </div>

        <Pieces
          board={board}
          gameId={gameId}
          onMove={setGame}
          turn={turn}
          legalMoves={legalMoves}
          setLegalMoves={setLegalMoves}
        />
      </div>

      <Popup>
        <PromotionBox />
        <GameEnds />
      </Popup>

      <Files files={files} />
    </div>
  );
};

export default Board;
