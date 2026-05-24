const app = getApp();

/**
 * 将后端返回的相对路径转换为完整 HTTP URL
 * @param {string} path - 相对路径，如 /uploads/avatar/xxx.jpg
 * @returns {string} 完整 URL
 */
function resolveFileUrl(path) {
  if (!path) return '';
  if (path.startsWith('http://') || path.startsWith('https://')) return path;
  const baseUrl = (app.globalData && app.globalData.baseUrl) || '';
  // baseUrl 形如 http://localhost:8080/api/v1，取主机部分
  const hostMatch = baseUrl.match(/^(https?:\/\/[^/]+)/);
  const host = hostMatch ? hostMatch[1] : '';
  return host + path;
}

module.exports = { resolveFileUrl };
