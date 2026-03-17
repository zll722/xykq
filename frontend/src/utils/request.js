import axios from 'axios';

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 10000
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (response) => {
    const payload = response.data;
    if (payload.code !== 0) {
      return Promise.reject(new Error(payload.message || '请求失败'));
    }
    return payload.data;
  },
  (error) => Promise.reject(error)
);

export default request;
