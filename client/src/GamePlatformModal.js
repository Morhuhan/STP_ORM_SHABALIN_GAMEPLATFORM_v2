// src/GamePlatformModal.js
import React, { useState } from 'react';
import { handleApiResponse } from './apiHelper';

function GamePlatformModal({ onClose, gamePlatforms, setGamePlatforms, editGamePlatform, platformId }) {
  const [name, setName] = useState(editGamePlatform ? editGamePlatform.name : '');
  const [urlImage, setUrlImage] = useState(editGamePlatform ? editGamePlatform.urlImage : '');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (isSubmitting) return;
    setIsSubmitting(true);
    const payload = { 
      name, 
      urlImage, 
      platformId 
    };

    try {
      const response = await fetch(editGamePlatform ? `/api/gameplatforms/${editGamePlatform.id}` : '/api/gameplatforms', {
        method: editGamePlatform ? 'PUT' : 'POST', 
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await handleApiResponse(response);

      if (editGamePlatform) {
        setGamePlatforms(gamePlatforms.map(gp => gp.id === data.id ? data : gp));
      } else {
        setGamePlatforms([...gamePlatforms, data]);
      }
      onClose();
    } catch (err) {
      console.error('Не удалось сохранить игровую платформу:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h3>{editGamePlatform ? 'Редактировать Игровую Платформу' : 'Добавить Игровую Платформу'}</h3>
        <form onSubmit={handleSubmit}>
          <label>Название:</label>
          <input 
            type="text" 
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <label>URL Изображения:</label>
          <input 
            type="url" 
            value={urlImage}
            onChange={(e) => setUrlImage(e.target.value)}
            required
          />
          <div className="modal-buttons">
            <button type="submit" disabled={isSubmitting}>Сохранить</button>
            <button type="button" onClick={onClose} disabled={isSubmitting}>Отмена</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default GamePlatformModal;