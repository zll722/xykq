App({
  globalData: {
    baseUrl: 'http://localhost:8080/api/v1',
    token: '',
    userInfo: null,
    roleCode: ''
  },

  onLaunch() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    if (token) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
      this.globalData.roleCode = userInfo ? userInfo.roleCode : '';
    }
  }
});
