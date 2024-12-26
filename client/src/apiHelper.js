// src/apiHelper.js
import { toast } from 'react-toastify';
import React from 'react';

export const handleApiResponse = async (response, options = { showSuccessToast: true }) => {
  let data;
  try {
    data = await response.json();
  } catch (err) {
    toast.error('Невозможно обработать ответ сервера.');
    throw new Error('Невозможно обработать ответ сервера.');
  }

  if (!response.ok) {
    const errorMessage = data.message || 'Произошла ошибка при выполнении запроса.';
    toast.error(`Ошибка ${response.status}: ${errorMessage}`);
    throw new Error(errorMessage);
  }

  if (data.success) {
    if (data.message && options.showSuccessToast) {
      toast.success(data.message);
    }
    return data.data;
  } else {
    let content = <div><strong>{data.message}</strong></div>;

    if (data.data && typeof data.data === 'object') {
      const errorList = Object.entries(data.data).map(([field, msg]) => (
        <li key={field}><strong>{field}:</strong> {msg}</li>
      ));
      content = (
        <div>
          <strong>{data.message}</strong>
          <ul>
            {errorList}
          </ul>
        </div>
      );
    }

    toast.error(content);
    throw new Error(data.message || 'Произошла ошибка.');
  }
};