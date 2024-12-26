// src/AllGamesModal.js
import React, { useState, useEffect } from 'react';
import GameModal from './GameModal';
import { handleApiResponse } from './apiHelper';

function AllGamesModal({ onClose, fetchGames }) {
  const [allGames, setAllGames] = useState([]);
  const [search, setSearch] = useState('');
  const [gameModalOpen, setGameModalOpen] = useState(false);
  const [selectedGame, setSelectedGame] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    const fetchAllGames = async () => {
      try {
        const response = await fetch('/api/games');
        const data = await handleApiResponse(response, { showSuccessToast: false });
        setAllGames(data);
      } catch (err) {
        console.error('Не удалось загрузить все игры:', err);
      }
    };

    fetchAllGames();
  }, []);

  const handleDelete = async (gameId) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту игру?')) return;
    if (isDeleting) return;
    setIsDeleting(true);
    try {
      const response = await fetch(`/api/games/${gameId}`, {
        method: 'DELETE'
      });
      await handleApiResponse(response);
      setAllGames(allGames.filter(game => game.id !== gameId));
      fetchGames();
    } catch (err) {
      console.error('Не удалось удалить игру:', err);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleEdit = (game) => {
    setSelectedGame(game);
    setGameModalOpen(true);
  };

  const handleCreateGame = () => {
    setSelectedGame(null);
    setGameModalOpen(true);
  };

  const handleGameModalClose = () => {
    setGameModalOpen(false);
    setSelectedGame(null);
    const refreshAllGames = async () => {
      try {
        const response = await fetch('/api/games');
        const data = await handleApiResponse(response, { showSuccessToast: false });
        setAllGames(data);
        fetchGames();
      } catch (err) {
        console.error('Не удалось обновить все игры:', err);
      }
    };
    refreshAllGames();
  };

  const filteredAllGames = allGames.filter(game =>
    game.name.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="modal">
      <div className="modal-content">
        <button onClick={handleCreateGame} style={{ marginBottom: '10px' }}>Создать игру</button>
        <input 
          type="text" 
          placeholder="Поиск игр" 
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <div className="modal-body">
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
              {filteredAllGames.map(game => (
                <tr key={game.id}>
                  <td>{game.name}</td>
                  <td>{game.genre}</td>
                  <td>{new Date(game.releaseDate).toLocaleDateString()}</td>
                  <td>
                    <a href={game.cover} target="_blank" rel="noopener noreferrer">Обложка</a>
                  </td>
                  <td>{game.score}</td>
                  <td>
                    <button onClick={() => handleEdit(game)}>Редактировать</button>
                    <button onClick={() => handleDelete(game.id)}>Удалить</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="modal-buttons">
          <button onClick={onClose}>Закрыть</button>
        </div>
        {gameModalOpen && (
          <GameModal 
            onClose={handleGameModalClose} 
            games={allGames}
            setGames={setAllGames}
            editGame={selectedGame}
          />
        )}
      </div>
    </div>
  );
}

export default AllGamesModal;