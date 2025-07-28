import React from 'react';
import './MoveHistory.css';

const MoveHistory = ({ moves }) => {
  return (
    <div className="move-history">
      <h2>📜 Move History</h2>
      {moves.length === 0 ? (
        <p>No moves yet.</p>
      ) : (
        <ul>
          {moves.map((move, idx) => (
            <li key={move.id}>
              {idx + 1}. {move.pieceType} {String.fromCharCode(97 + move.fromCol)}{8 - move.fromRow} →
              {String.fromCharCode(97 + move.toCol)}{8 - move.toRow}
              {move.isCapture && " ×"}
              {move.promotion && ` (Promoted to ${move.promotion})`}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default MoveHistory;
