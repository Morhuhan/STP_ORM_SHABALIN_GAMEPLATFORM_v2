// src/GameGrid.js
import React, { useState } from 'react';
import GameModal from './GameModal';
import AllGamesModal from './AllGamesModal'; 
import { handleApiResponse } from './apiHelper';

function GameGrid({ games, setGames, fetchGames, selectedGamePlatform }) {
  const [search, setSearch] = useState('');
  const [gameModalOpen, setGameModalOpen] = useState(false);
  const [allGamesModalOpen, setAllGamesModalOpen] = useState(false);
  const [isUnlinking, setIsUnlinking] = useState(false);

  const filteredGames = games.filter(game =>
    game.name.toLowerCase().includes(search.toLowerCase())
  );

  const handleUnlink = async (gameId) => {
    if (!selectedGamePlatform) {
      return;
    }
    if (!window.confirm('Вы уверены, что хотите отвязать эту игру от платформы?')) return;
    if (isUnlinking) return;
    setIsUnlinking(true);
    try {
      const response = await fetch(`/api/games/${gameId}/unassign`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ gamePlatformId: selectedGamePlatform.id }) 
      });
      await handleApiResponse(response);
      setGames(games.filter(g => g.id !== gameId));
      fetchGames();
    } catch (err) {
      console.error('Не удалось отвязать игру:', err);
    } finally {
      setIsUnlinking(false);
    }
  };

  const handleGameModalClose = () => {
    setGameModalOpen(false);
    fetchGames(); 
  };

  const handleAllGamesModalClose = () => {
    setAllGamesModalOpen(false);
    fetchGames();
  };

  return (
    <div className="grid" id='game-grid'>
      <div className="game-grid-header">
        <button onClick={() => setAllGamesModalOpen(true)}>Все игры</button>
        <h2>Игры</h2>
      </div>
      <div className="game-controls">
      </div>
      <input 
        type="text" 
        placeholder="Поиск Игр" 
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <table>
        <thead>
          <tr>
            <th>Название</th>
            <th>Жанр</th>
            <th>Дата Релиза</th>
            <th>Обложка</th>
            <th>Оценка</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {filteredGames.map(game => (
            <tr key={game.id}>
              <td>{game.name}</td>
              <td>{game.genre}</td>
              <td>{new Date(game.releaseDate).toLocaleDateString()}</td>
              <td>
                <a href={game.cover} target="_blank" rel="noopener noreferrer">Обложка</a>
              </td>
              <td>{game.score}</td>
              <td>
                <button onClick={() => handleUnlink(game.id)} disabled={isUnlinking}>
                  Отвязать
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {gameModalOpen && (
        <GameModal 
          onClose={handleGameModalClose} 
          games={games}
          setGames={setGames}
        />
      )}
      {allGamesModalOpen && (
        <AllGamesModal 
          onClose={handleAllGamesModalClose} 
          fetchGames={fetchGames}
        />
      )}
    </div>
  );
}

export default GameGrid;