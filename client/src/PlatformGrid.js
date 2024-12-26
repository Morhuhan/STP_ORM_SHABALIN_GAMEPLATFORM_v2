// src/PlatformGrid.js
import React, { useState } from 'react';
import PlatformModal from './PlatformModal';
import { handleApiResponse } from './apiHelper';

function PlatformGrid({ platforms, setPlatforms, selectedPlatform, setSelectedPlatform }) {
  const [search, setSearch] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editPlatform, setEditPlatform] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const filteredPlatforms = platforms.filter(platform =>
    platform.name.toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async (id) => {
    if (!window.confirm('Вы уверены, что хотите удалить эту платформу?')) return;
    if (isDeleting) return;
    setIsDeleting(true);
    try {
      const response = await fetch(`/api/platforms/${id}`, {
        method: 'DELETE'
      });
      await handleApiResponse(response);
      setPlatforms(platforms.filter(p => p.id !== id));
      if (selectedPlatform && selectedPlatform.id === id) {
        const newSelected = filteredPlatforms.find(p => p.id !== id);
        setSelectedPlatform(newSelected || null);
      }
    } catch (err) {
      console.error('Не удалось удалить платформу:', err);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleEdit = (platform) => {
    setEditPlatform(platform);
    setModalOpen(true);
  };

  return (
    <div className="grid">
      <h2>Платформы</h2>
      <input 
        type="text" 
        placeholder="Поиск Платформ" 
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <button onClick={() => { setEditPlatform(null); setModalOpen(true); }}>Добавить Платформу</button>
      <table>
        <thead>
          <tr>
            <th>Название</th>
            <th>Характеристика</th>
            <th>URL Изображения</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {filteredPlatforms.map(platform => (
            <tr 
              key={platform.id} 
              onClick={() => setSelectedPlatform(platform)}
              className={selectedPlatform && selectedPlatform.id === platform.id ? 'selected' : ''}
            >
              <td>{platform.name}</td>
              <td>{platform.characteristic}</td>
              <td><a href={platform.urlImage} target="_blank" rel="noopener noreferrer">Изображение</a></td>
              <td>
                <button onClick={(e) => { e.stopPropagation(); handleEdit(platform); }}>Редактировать</button>
                <button onClick={(e) => { e.stopPropagation(); handleDelete(platform.id); }} disabled={isDeleting}>
                  Удалить
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {modalOpen && 
        <PlatformModal 
          onClose={() => setModalOpen(false)} 
          platforms={platforms}
          setPlatforms={setPlatforms}
          editPlatform={editPlatform}
        />
      }
    </div>
  );
}

export default PlatformGrid;