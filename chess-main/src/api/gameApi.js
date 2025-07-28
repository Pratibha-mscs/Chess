import axios from 'axios';

const BASE_URL = `${process.env.REACT_APP_API_URL}/api`;

export const getGameById = (id) => axios.get(`${BASE_URL}/game/${id}`);

export const sendMove = (gameId, move) =>
  axios.post(`${BASE_URL}/game/${gameId}/move`, move);


export const createNewGame = () => axios.post(`${BASE_URL}/game/new`);

export const getMoveHistory = (gameId) =>
  axios.get(`${BASE_URL}/game/${gameId}/moves`);