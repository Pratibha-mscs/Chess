import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

let stompClient = null;

export const connectGame = (gameId, onGameUpdate) => {
  stompClient = new Client({
    webSocketFactory: () => new SockJS(`${process.env.REACT_APP_WS_URL}`),
    onConnect: () => {
      stompClient.subscribe(`/topic/game/${gameId}`, (msg) => {
        const gameState = JSON.parse(msg.body);
        onGameUpdate(gameState);
      });
    },
  });
  stompClient.activate();
};

export const disconnectGame = () => {
  if (stompClient) stompClient.deactivate();
};
