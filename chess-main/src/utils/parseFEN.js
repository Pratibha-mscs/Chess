export function parseFEN(fen) {
    const rows = fen.split(' ')[0].split('/');
    return rows.map(row => {
      const result = [];
      for (const char of row) {
        if (isNaN(char)) {
          result.push(char);
        } else {
          for (let i = 0; i < parseInt(char); i++) {
            result.push(null);
          }
        }
      }
      return result;
    });
  }