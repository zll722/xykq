const authApi = require('../../api/auth');
const { setToken, setUserInfo, getHomePageByRole } = require('../../utils/auth');

Page({
  data: {
    username: '',
    password: '',
    loading: false
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  async onLogin() {
    const { username, password } = this.data;
    if (!username || !password) {
      wx.showToast({ title: '请输入账号和密码', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    try {
      const res = await authApi.login({ username, password });
      setToken(res.token);
      const userInfo = await authApi.getMe();
      setUserInfo(userInfo);
      const homePage = getHomePageByRole(userInfo.roleCode);
      wx.reLaunch({ url: homePage });
    } catch (err) {
      console.error('登录失败', err);
    } finally {
      this.setData({ loading: false });
    }
  }
});
