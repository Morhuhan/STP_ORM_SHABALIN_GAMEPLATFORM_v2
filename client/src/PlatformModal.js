// src/PlatformModal.js
import React, { useState, useEffect } from 'react';
import { handleApiResponse } from './apiHelper';

function PlatformModal({ onClose, platforms, setPlatforms, editPlatform }) {
  const [name, setName] = useState(editPlatform ? editPlatform.name : '');
  const [characteristic, setCharacteristic] = useState(editPlatform ? editPlatform.characteristic : '');
  const [urlImage, setUrlImage] = useState(editPlatform ? editPlatform.urlImage : '');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (editPlatform) {
      setName(editPlatform.name);
      setCharacteristic(editPlatform.characteristic);
      setUrlImage(editPlatform.urlImage);
    }
  }, [editPlatform]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (isSubmitting) return;
    setIsSubmitting(true);
    const payload = { name, characteristic, urlImage };

    try {
      const response = await fetch(editPlatform ? `/api/platforms/${editPlatform.id}` : '/api/platforms', {
        method: editPlatform ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await handleApiResponse(response);

      if (editPlatform) {
        setPlatforms(platforms.map(p => p.id === data.id ? data : p));
      } else {
        setPlatforms([...platforms, data]);
      }
      onClose();
    } catch (err) {
      console.error('Не удалось сохранить платформу:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h3>{editPlatform ? 'Редактировать Платформу' : 'Добавить Платформу'}</h3>
        <form onSubmit={handleSubmit}>
          <label>Название:</label>
          <input 
            type="text" 
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <label>Характеристика:</label>
          <input 
            type="text" 
            value={characteristic}
            onChange={(e) => setCharacteristic(e.target.value)}
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

export default PlatformModal;