// src/App.js
import React, { useState, useEffect, useCallback } from 'react';
import PlatformGrid from './PlatformGrid';
import GamePlatformGrid from './GamePlatformGrid';
import GameGrid from './GameGrid';
import { handleApiResponse } from './apiHelper';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; 
import './App.css';

function App() {
  const [platforms, setPlatforms] = useState([]);
  const [selectedPlatform, setSelectedPlatform] = useState(null);
  const [gamePlatforms, setGamePlatforms] = useState([]);
  const [selectedGamePlatform, setSelectedGamePlatform] = useState(null);
  const [games, setGames] = useState([]);

  useEffect(() => {
    const fetchPlatforms = async () => {
      try {
        const response = await fetch('/api/platforms');
        const data = await handleApiResponse(response, { showSuccessToast: false });
        setPlatforms(data);
        if (data.length > 0) {
          setSelectedPlatform(data[0]);
        }
      } catch (err) {
        console.error('Не удалось загрузить платформы:', err);
      }
    };

    fetchPlatforms();
  }, []);

  const fetchGames = useCallback(async () => {
    if (selectedGamePlatform) {
      try {
        const response = await fetch('/api/games');
        const data = await handleApiResponse(response, { showSuccessToast: false });
        const filtered = data.filter(game => game.gamePlatformIds.includes(selectedGamePlatform.id));
        setGames(filtered);
      } catch (err) {
        console.error('Не удалось загрузить игры:', err);
      }
    } else {
      setGames([]);
    }
  }, [selectedGamePlatform]);

  useEffect(() => {
    if (selectedPlatform) {
      const fetchGamePlatforms = async () => {
        try {
          const response = await fetch('/api/gameplatforms');
          const data = await handleApiResponse(response, { showSuccessToast: false });
          const filtered = data.filter(gp => gp.platformId === selectedPlatform.id);
          setGamePlatforms(filtered);
          if (filtered.length > 0) {
            setSelectedGamePlatform(filtered[0]);
          } else {
            setSelectedGamePlatform(null);
            setGames([]);
          }
        } catch (err) {
          console.error('Не удалось загрузить игровые платформы:', err);
        }
      };

      fetchGamePlatforms();
    }
  }, [selectedPlatform]);

  useEffect(() => {
    fetchGames();
  }, [fetchGames]);

  return (
    <div className="App">
      <div className="grids">
        <PlatformGrid 
          platforms={platforms} 
          setPlatforms={setPlatforms}
          selectedPlatform={selectedPlatform}
          setSelectedPlatform={setSelectedPlatform}
        />
        <GamePlatformGrid 
          gamePlatforms={gamePlatforms} 
          setGamePlatforms={setGamePlatforms}
          selectedGamePlatform={selectedGamePlatform}
          setSelectedGamePlatform={setSelectedGamePlatform}
          platformId={selectedPlatform ? selectedPlatform.id : null}
          fetchGames={fetchGames} 
        />
        <GameGrid 
          games={games} 
          setGames={setGames}
          fetchGames={fetchGames} 
          selectedGamePlatform={selectedGamePlatform}
        />
      </div>
    </div>
  );
}

export default App;