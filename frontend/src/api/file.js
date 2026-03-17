import axios from 'axios';

export async function uploadFileApi(file, category = 'common') {
  const token = localStorage.getItem('token') || '';
  const form = new FormData();
  form.append('file', file);
  form.append('category', category);
  const { data } = await axios.post('/api/v1/files/upload', form, {
    headers: {
      Authorization: token ? `Bearer ${token}` : '',
      'Content-Type': 'multipart/form-data'
    }
  });
  if (data.code !== 0) {
    throw new Error(data.message || '上传失败');
  }
  return data.data.path;
}
