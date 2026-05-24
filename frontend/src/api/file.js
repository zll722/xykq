import request from '../utils/request';

/**
 * 上传文件到服务器
 * @param {File} file - 待上传的文件对象
 * @param {string} [category='common'] - 文件分类目录
 * @returns {Promise<string>} 返回服务器存储路径（如 /uploads/common/202505/xxx.jpg）
 */
export async function uploadFileApi(file, category = 'common') {
  const form = new FormData();
  form.append('file', file);
  form.append('category', category);
  // 不手动设置 Content-Type，由浏览器自动生成含 boundary 的 multipart 头
  const data = await request.post('/files/upload', form);
  return data.path;
}
