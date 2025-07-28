// src/components/Popup/PromotionBox/PromotionBox.js
import './PromotionBox.css';

/**
 * Promotion modal. Lets user pick a piece.
 * @param {Object} props
 * @param {Object} props.square - {x, y} coords
 * @param {string} props.color - 'w' or 'b'
 * @param {Function} props.onPromote - callback with selected piece ('q', 'r', etc.)
 */
const PromotionBox = ({ square, color, onPromote }) => {
  if (!square) return null;

  const options = ['q', 'r', 'b', 'n'];

  const getPositionStyle = () => {
    const style = {};
    if (square.x === 7) style.top = '-12.5%';
    else style.top = '97.5%';

    if (square.y <= 1) style.left = '0%';
    else if (square.y >= 5) style.right = '0%';
    else style.left = `${12.5 * square.y - 20}%`;

    return style;
  };

  return (
    <div className="popup--inner promotion-choices" style={getPositionStyle()}>
      {options.map((option) => (
        <div
          key={option}
          onClick={() => onPromote(option)}
          className={`piece ${color}${option}`}
        />
      ))}
    </div>
  );
};

export default PromotionBox;
