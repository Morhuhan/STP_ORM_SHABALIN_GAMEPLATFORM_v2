// src/GameModal.js
import React, { useState, useEffect } from 'react';
import { handleApiResponse } from './apiHelper';

function GameModal({ onClose, games, setGames, editGame }) {
  const [formData, setFormData] = useState({
    name: '',
    genre: '',
    releaseDate: '',
    cover: '',
    score: ''
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (editGame) {
      setFormData({
        name: editGame.name,
        genre: editGame.genre,
        releaseDate: editGame.releaseDate,
        cover: editGame.cover,
        score: editGame.score
      });
    }
  }, [editGame]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (isSubmitting) return;
    setIsSubmitting(true);
    try {
      const method = editGame ? 'PUT' : 'POST';
      const url = editGame ? `/api/games/${editGame.id}` : '/api/games';
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });
      const data = await handleApiResponse(response);
      if (editGame) {
        setGames(games.map(game => game.id === editGame.id ? data : game));
      } else {
        setGames([...games, data]);
      }
      onClose();
    } catch (err) {
      console.error('Ошибка при сохранении игры:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h2>{editGame ? 'Редактировать Игру' : 'Создать Игру'}</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            name="name"
            placeholder="Название"
            value={formData.name}
            onChange={handleChange}
            required
          />
          <input
            type="text"
            name="genre"
            placeholder="Жанр"
            value={formData.genre}
            onChange={handleChange}
            required
          />
          <input
            type="date"
            name="releaseDate"
            placeholder="Дата Релиза"
            value={formData.releaseDate}
            onChange={handleChange}
            required
          />
          <input
            type="url"
            name="cover"
            placeholder="Ссылка на обложку"
            value={formData.cover}
            onChange={handleChange}
            required
          />
          <input
            type="text"
            name="score"
            placeholder="Оценка"
            value={formData.score}
            onChange={handleChange}
            required
          />
          <button type="submit" disabled={isSubmitting}>
            {editGame ? 'Сохранить Изменения' : 'Создать Игру'}
          </button>
        </form>
        <button onClick={onClose}>Закрыть</button>
      </div>
    </div>
  );
}

export default GameModal;