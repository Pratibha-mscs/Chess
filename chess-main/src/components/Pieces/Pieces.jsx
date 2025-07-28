import React, { useRef } from 'react';
import Piece from './Piece';
import './Pieces.css';

const Pieces = ({ board = [], gameId, onMove, turn, legalMoves, setLegalMoves }) => {
  const ref = useRef();

  const calculateCoords = (e) => {
    const { top, left, width } = ref.current.getBoundingClientRect();
    const tileSize = width / 8;
    const y = Math.floor((e.clientX - left) / tileSize);
    const x = 7 - Math.floor((e.clientY - top) / tileSize);
    return { x, y };
  };

  const onDrop = async (e) => {
    e.preventDefault();
    const { x: toRow, y: toCol } = calculateCoords(e);
    const [piece, rank, file] = e.dataTransfer.getData('text').split(',');
    const fromRow = parseInt(rank);
    const fromCol = parseInt(file);

    const promotion =
      (piece === 'P' && toRow === 0) || (piece === 'p' && toRow === 7)
        ? 'QUEEN'
        : null;

    try {
      const res = await fetch(`${process.env.REACT_APP_API_URL}/api/game/${gameId}/move`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fromRow,
          fromCol,
          toRow,
          toCol,
          pieceType: mapToEnum(piece),
          promotionPiece: promotion,
        }),
      });

      if (!res.ok) {console.log('Invalid move')}
      if (res.ok) {
        const updatedGame = await res.json();
        onMove(updatedGame);
        setLegalMoves([]); // ✅ clear highlights after move
      }
    } catch (err) {
      console.error('Move failed:', err.message);
    }
  };

  const onDragOver = (e) => e.preventDefault();

  const mapToEnum = (piece) => {
    const map = {
      p: 'PAWN', r: 'ROOK', n: 'KNIGHT', b: 'BISHOP', q: 'QUEEN', k: 'KING',
      P: 'PAWN', R: 'ROOK', N: 'KNIGHT', B: 'BISHOP', Q: 'QUEEN', K: 'KING',
    };
    return map[piece.toLowerCase()];
  };

  // ✅ When clicking a piece, fetch and show legal moves
  const handleClick = async (row, col, piece) => {
    const isWhite = piece === piece.toUpperCase();
    if ((turn === 'WHITE' && !isWhite) || (turn === 'BLACK' && isWhite)) return;

    try {
      const res = await fetch(`${process.env.REACT_APP_API_URL}/api/game/${gameId}/legal-moves?row=${row}&col=${col}`);
      const data = await res.json();
      setLegalMoves(data); // ✅ highlight on board
    } catch (err) {
      console.error('Could not fetch legal moves', err);
    }
  };

  return (
    <div className="pieces" ref={ref} onDrop={onDrop} onDragOver={onDragOver}>
      {board.map((rowArr, row) =>
        rowArr.map((piece, col) =>
          piece ? (
            <Piece
              key={`${row}-${col}`}
              rank={row}
              file={col}
              piece={piece}
              className={`piece p-${col}${row}`}
              onClick={() => handleClick(row, col, piece)} // ✅ attach click handler
            />
          ) : null
        )
      )}
    </div>
  );
};

export default Pieces;
