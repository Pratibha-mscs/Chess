import React, { useEffect, useState } from 'react';
import './App.css';

import Board from './components/Board/Board';
import { getGameById, createNewGame, getMoveHistory } from './api/gameApi';
import { parseFEN } from './utils/parseFEN';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import MoveHistory from './components/MoveHistory/MoveHistory';

const GAME_ID = '33333333-3333-3333-3333-333333333333';

const App = () => {
  const [game, setGame] = useState(null);               // Game metadata
  const [board, setBoard] = useState([]);               // 2D array board
  const [legalMoves, setLegalMoves] = useState([]);     // Legal move highlights
  const [checkPosition, setCheckPosition] = useState(null); // King under check
  const [invalidAttempt, setInvalidAttempt] = useState(false); // Blink flag
  const [moves, setMoves] = useState([]);

  // 🔁 Fetch game on load
  useEffect(() => {
    const fetchGame = async () => {
      try {
        const res = await getGameById(GAME_ID);
        const gameData = res.data;

        setGame(gameData);
        setBoard(parseFEN(gameData.boardState));
        setCheckPosition(gameData.checkPosition || null);
      } catch (err) {
        console.error('Error loading game:', err);
      }
    };

    fetchGame();
  }, []);

  useEffect(() => {
    if (!game || !game.id) return;

    const fetchMoves = async () => {
      try {
        const res = await getMoveHistory(game.id);
        setMoves(res.data);
      } catch (err) {
        console.error('Failed to load move history:', err);
      }
    };

    fetchMoves();
  }, [game]);
    
  // ✅ WebSocket subscription
  useEffect(() => {
    const socket = new SockJS(process.env.REACT_APP_WS_URL);
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        stompClient.subscribe(`/topic/game/${GAME_ID}`, (message) => {
          const gameUpdate = JSON.parse(message.body);
          setGame(gameUpdate);
        });
      },
      onStompError: (frame) => {
        console.error("STOMP error:", frame.headers['message']);
      }
    });
    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, []);

  // 🔄 Update board when game object updates
  useEffect(() => {
    if (game) {
      setBoard(parseFEN(game.boardState));
      setLegalMoves([]);
      setCheckPosition(game.checkPosition || null);
      setInvalidAttempt(false); // clear previous blink if any
    }
    
  }, [game]);

  // 🔁 Start new game handler
  const startNewGame = async () => {
    try {
      const res = await createNewGame();
      const gameData = res.data;

      if (!gameData.boardState) throw new Error('Missing boardState');

      setGame(gameData);
      setBoard(parseFEN(gameData.boardState));
      setCheckPosition(gameData.checkPosition || null);
      setInvalidAttempt(false);
    } catch (err) {
      console.error('Failed to start new game:', err.message);
    }
  };

  const handleResign = async (playerColor) => {
    try {
      const res = await fetch(`${process.env.REACT_APP_API_URL}/api/game/${GAME_ID}/resign?player=${playerColor}`, {
        method: 'POST'
      });
      const data = await res.json();
      setGame(data);
    } catch (err) {
      console.error("Resign failed", err);
    }
  };

  const handleDraw = async (playerColor) => {
    try {
      const res = await fetch(`${process.env.REACT_APP_API_URL}/api/game/${GAME_ID}/offer-draw?player=${playerColor}`, {
        method: 'POST'
      });
      const data = await res.json();
      setGame(data);
    } catch (err) {
      console.error("Offer draw failed", err);
    }
  };



  if (!game) return <div className="loading">Loading game...</div>;

  return (
    <div className="app">
      <h1 className="title">Chess Game - {GAME_ID}</h1>
  
      <div className="main-content">
        <Board
          board={board}
          setGame={setGame}
          gameId={GAME_ID}
          turn={game.turn}
          legalMoves={legalMoves}
          setLegalMoves={setLegalMoves}
          checkPosition={checkPosition}
          invalidAttempt={invalidAttempt}
          setInvalidAttempt={setInvalidAttempt}
        />
  
        {/* ▶️ Right Side: Buttons + Move History */}
        <div className="sidebar">
          <div className="button-bar">
            <button className="new-game-btn" onClick={startNewGame}>🔁 New Game</button>
            <button className="resign-btn" onClick={() => handleResign(game.turn)}>🏳️ Resign</button>
            <button className="draw-btn" onClick={() => handleDraw(game.turn)}>🤝 Offer Draw</button>
          </div>
  
          <MoveHistory moves={moves} />
          </div>
      </div>
    </div>
  );
  
};

export default App;
