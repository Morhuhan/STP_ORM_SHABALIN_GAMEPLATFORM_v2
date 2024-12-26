// src/GamePlatformGrid.js
import React, { useState } from 'react';
import GamePlatformModal from './GamePlatformModal';
import LinkGameModal from './LinkGameModal';
import { handleApiResponse } from './apiHelper';

function GamePlatformGrid({ 
  gamePlatforms, 
  setGamePlatforms, 
  selectedGamePlatform, 
  setSelectedGamePlatform, 
  platformId,
  fetchGames
}) {
  const [search, setSearch] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [linkModalOpen, setLinkModalOpen] = useState(false);
  const [editGamePlatform, setEditGamePlatform] = useState(null);
  const [error] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const filteredGamePlatforms = gamePlatforms.filter(gp =>
    gp.name.toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async (id) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту игровую платформу?')) return;
    if (isDeleting) return;
    setIsDeleting(true);
    try {
      const response = await fetch(`/api/gameplatforms/${id}`, {
        method: 'DELETE'
      });
      await handleApiResponse(response);
      setGamePlatforms(gamePlatforms.filter(gp => gp.id !== id));
      if (selectedGamePlatform && selectedGamePlatform.id === id) {
        const newSelected = filteredGamePlatforms.find(gp => gp.id !== id);
        setSelectedGamePlatform(newSelected || null);
      }
    } catch (err) {
      console.error('Не удалось удалить игровую платформу:', err);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleEdit = (gamePlatform) => {
    setEditGamePlatform(gamePlatform);
    setModalOpen(true);
  };

  const handleLink = () => {
    setLinkModalOpen(true);
  };

  return (
    <div className="grid">
      <h2>Игровые Платформы</h2>
      {error && <div className="error">{error}</div>}
      <input 
        type="text" 
        placeholder="Поиск игровых платформ" 
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <button onClick={() => { setEditGamePlatform(null); setModalOpen(true); }}>Добавить Игровую Платформу</button>
      {selectedGamePlatform && (
        <button onClick={handleLink}>Привязать Игру</button>
      )}
      <table>
        <thead>
          <tr>
            <th>Название</th>
            <th>URL Изображения</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {filteredGamePlatforms.map(gp => (
            <tr 
              key={gp.id} 
              onClick={() => setSelectedGamePlatform(gp)}
              className={selectedGamePlatform && selectedGamePlatform.id === gp.id ? 'selected' : ''}
            >
              <td>{gp.name}</td>
              <td><a href={gp.urlImage} target="_blank" rel="noopener noreferrer">Изображение</a></td>
              <td>
                <button onClick={(e) => { e.stopPropagation(); handleEdit(gp); }}>Редактировать</button>
                <button onClick={(e) => { e.stopPropagation(); handleDelete(gp.id); }} disabled={isDeleting}>
                  Удалить
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {modalOpen && 
        <GamePlatformModal 
          onClose={() => setModalOpen(false)} 
          gamePlatforms={gamePlatforms}
          setGamePlatforms={setGamePlatforms}
          editGamePlatform={editGamePlatform}
          platformId={platformId} 
        />
      }
      {linkModalOpen && selectedGamePlatform && (
        <LinkGameModal 
          onClose={() => setLinkModalOpen(false)} 
          gamePlatformId={selectedGamePlatform.id}
          onLink={fetchGames}
          fetchGames={fetchGames}
        />
      )}
    </div>
  );
}

export default GamePlatformGrid;