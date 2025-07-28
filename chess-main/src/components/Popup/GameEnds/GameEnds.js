// src/components/Popup/GameEnds/GameEnds.js
import './GameEnds.css';

/**
 * Simple game result modal. Accepts props from parent.
 * @param {Object} props
 * @param {string} props.status - current game status (from backend)
 * @param {Function} props.onNewGame - function to trigger new game creation
 */
const GameEnds = ({ status, onNewGame }) => {
  if (status === 'ONGOING' || status === 'PROMOTING') return null;

  const isWin = status?.toLowerCase().includes('wins');

  // Choose winner icon
  const resultIcon = status?.toLowerCase().startsWith('white')
    ? '/assets/wk.png'
    : status?.toLowerCase().startsWith('black')
    ? '/assets/bk.png'
    : null;

  return (
    <div className="popup--inner popup--inner__center">
      {/* <h1>{isWin ? status : 'Draw'}</h1> */}
      <p>{!isWin && status}</p>

      {resultIcon && (
        <img
          src={resultIcon}
          alt="winner icon"
          className="game-result-icon"
        />
      )}
    </div>
  );
};

export default GameEnds;
