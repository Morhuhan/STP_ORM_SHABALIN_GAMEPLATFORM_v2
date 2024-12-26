// src/LinkGameModal.js
import React, { useState, useEffect } from 'react';
import { handleApiResponse } from './apiHelper';

function LinkGameModal({ onClose, gamePlatformId, onLink, fetchGames }) {
  const [availableGames, setAvailableGames] = useState([]);
  const [search, setSearch] = useState('');
  const [error] = useState(null);
  const [isLinking, setIsLinking] = useState(false);

  useEffect(() => {
    const fetchAvailableGames = async () => {
      try {
        const response = await fetch('/api/games');
        const data = await handleApiResponse(response, { showSuccessToast: false });

        const filteredGames = data.filter(game => 
          !game.gamePlatformIds.includes(gamePlatformId)
        );
        setAvailableGames(filteredGames);
      } catch (err) {
        console.error('Не удалось загрузить доступные игры:', err);
      }
    };

    fetchAvailableGames();
  }, [gamePlatformId]);

  const handleLink = async (gameId) => {
    if (isLinking) return;
    setIsLinking(true);
    try {
      const payload = { gamePlatformId };
      const response = await fetch(`/api/games/${gameId}/assign`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const linkedGame = await handleApiResponse(response);
      console.log('Linked Game:', linkedGame);

      setAvailableGames(availableGames.filter(game => game.id !== gameId));

      onLink(linkedGame);

      fetchGames();

      onClose();
    } catch (err) {
      console.error('Не удалось привязать игру:', err);
    } finally {
      setIsLinking(false);
    }
  };

  const filteredGames = availableGames.filter(game =>
    game.name.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="modal">
      <div className="modal-content">
        <h3>Привязать Игру к Платформе</h3>
        {error && <div className="error">{error}</div>}
        <input 
          type="text" 
          placeholder="Поиск игр" 
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <div className="game-grid">
          {filteredGames.length > 0 ? (
            filteredGames.map(game => (
              <div key={game.id} className="game-card">
                <img src={game.cover} alt={game.name} width="100" />
                <p>{game.name}</p>
                <button onClick={() => handleLink(game.id)} disabled={isLinking}>
                  Привязать
                </button>
              </div>
            ))
          ) : (
            <p>Нет доступных игр для привязки.</p>
          )}
        </div>
        <div className="modal-buttons">
          <button onClick={onClose} disabled={isLinking}>Закрыть</button>
        </div>
      </div>
    </div>
  );
}

export default LinkGameModal;