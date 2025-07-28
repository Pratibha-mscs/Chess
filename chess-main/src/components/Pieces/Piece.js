import React from 'react';

/**
 * Individual chess piece rendered as draggable <img> element
 */
const Piece = ({ piece, rank, file, className, onClick }) => {
  const handleDragStart = (e) => {
    e.dataTransfer.setData('text', `${piece},${rank},${file}`);
  };

  const pieceImages = {
    p: 'bp.png', P: 'wp.png',
    r: 'br.png', R: 'wr.png',
    n: 'bn.png', N: 'wn.png',
    b: 'bb.png', B: 'wb.png',
    q: 'bq.png', Q: 'wq.png',
    k: 'bk.png', K: 'wk.png',
  };

  const src = `${process.env.PUBLIC_URL}/assets/${pieceImages[piece]}`;

  return (
    <img
      draggable
      onClick={onClick}            
      onDragStart={handleDragStart}
      src={src}
      alt={piece}
      className={className}
    />
  );
};

export default Piece;
