const app = getApp();

const setToken = (token) => {
  app.globalData.token = token;
  wx.setStorageSync('token', token);
};

const getToken = () => {
  return app.globalData.token || wx.getStorageSync('token');
};

const setUserInfo = (userInfo) => {
  app.globalData.userInfo = userInfo;
  app.globalData.roleCode = userInfo ? userInfo.roleCode : '';
  wx.setStorageSync('userInfo', userInfo);
};

const getUserInfo = () => {
  return app.globalData.userInfo || wx.getStorageSync('userInfo');
};

const getRoleCode = () => {
  const info = getUserInfo();
  return info ? info.roleCode : '';
};

const isLoggedIn = () => {
  return !!getToken();
};

const logout = () => {
  app.globalData.token = '';
  app.globalData.userInfo = null;
  app.globalData.roleCode = '';
  wx.removeStorageSync('token');
  wx.removeStorageSync('userInfo');
  wx.reLaunch({ url: '/pages/login/login' });
};

const checkRole = (expectedRole) => {
  const role = getRoleCode();
  if (role !== expectedRole) {
    wx.reLaunch({ url: '/pages/login/login' });
    return false;
  }
  return true;
};

const getHomePageByRole = (roleCode) => {
  switch (roleCode) {
    case 'USER': return '/pages/student/index/index';
    case 'TEACHER': return '/pages/teacher/index/index';
    case 'COUNSELOR': return '/pages/counselor/index/index';
    default: return '/pages/login/login';
  }
};

module.exports = {
  setToken, getToken, setUserInfo, getUserInfo,
  getRoleCode, isLoggedIn, logout, checkRole, getHomePageByRole
};
