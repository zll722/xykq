const app = getApp();

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = app.globalData.token || wx.getStorageSync('token');
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    };
    if (token) {
      header['Authorization'] = `Bearer ${token}`;
    }

    wx.request({
      url: `${app.globalData.baseUrl}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      header,
      success(res) {
        if (res.statusCode === 200) {
          const data = res.data;
          if (data.code === 0) {
            resolve(data.data);
          } else {
            wx.showToast({ title: data.message || '请求失败', icon: 'none' });
            reject(data);
          }
        } else if (res.statusCode === 401) {
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          app.globalData.token = '';
          app.globalData.userInfo = null;
          app.globalData.roleCode = '';
          wx.reLaunch({ url: '/pages/login/login' });
          reject(res);
        } else {
          wx.showToast({ title: '服务器错误', icon: 'none' });
          reject(res);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络连接失败', icon: 'none' });
        reject(err);
      }
    });
  });
};

const get = (url, data) => request({ url, method: 'GET', data });
const post = (url, data) => request({ url, method: 'POST', data });
const put = (url, data) => request({ url, method: 'PUT', data });
const del = (url, data) => request({ url, method: 'DELETE', data });

const uploadFile = (filePath, category) => {
  return new Promise((resolve, reject) => {
    const token = app.globalData.token || wx.getStorageSync('token');
    wx.uploadFile({
      url: `${app.globalData.baseUrl}/files/upload`,
      filePath,
      name: 'file',
      formData: { category: category || 'attachment' },
      header: { 'Authorization': `Bearer ${token}` },
      success(res) {
        if (res.statusCode === 200) {
          const data = JSON.parse(res.data);
          if (data.code === 0) {
            resolve(data.data);
          } else {
            reject(data);
          }
        } else {
          reject(res);
        }
      },
      fail: reject
    });
  });
};

module.exports = { request, get, post, put, del, uploadFile };
