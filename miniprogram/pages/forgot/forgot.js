const authApi = require('../../api/auth');

Page({
  data: {
    username: '',
    email: '',
    code: '',
    newPassword: '',
    confirmPassword: '',
    loading: false,
    sendingCode: false,
    countdown: 0,
    timer: null
  },

  onUsernameInput(e) { this.setData({ username: e.detail.value }); },
  onEmailInput(e) { this.setData({ email: e.detail.value }); },
  onCodeInput(e) { this.setData({ code: e.detail.value }); },
  onNewPasswordInput(e) { this.setData({ newPassword: e.detail.value }); },
  onConfirmPasswordInput(e) { this.setData({ confirmPassword: e.detail.value }); },

  async sendCode() {
    const { username, email } = this.data;
    if (!username) {
      wx.showToast({ title: '请输入登录账号', icon: 'none' });
      return;
    }
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || !emailPattern.test(email)) {
      wx.showToast({ title: '请输入正确的邮箱', icon: 'none' });
      return;
    }
    
    this.setData({ sendingCode: true });
    try {
      await authApi.sendResetCode({ username, email });
      wx.showToast({ title: '验证码已发送', icon: 'success' });
      this.setData({ countdown: 60 });
      this.data.timer = setInterval(() => {
        if (this.data.countdown <= 1) {
          clearInterval(this.data.timer);
          this.setData({ countdown: 0 });
        } else {
          this.setData({ countdown: this.data.countdown - 1 });
        }
      }, 1000);
    } catch (e) {
      wx.showToast({ title: e.message || '发送失败', icon: 'none' });
    } finally {
      this.setData({ sendingCode: false });
    }
  },

  async onSubmit() {
    const { username, email, code, newPassword, confirmPassword } = this.data;
    if (!username || !email || !code || !newPassword || !confirmPassword) {
      wx.showToast({ title: '请填写所有字段', icon: 'none' });
      return;
    }
    if (newPassword.length < 6) {
      wx.showToast({ title: '新密码不能少于6位', icon: 'none' });
      return;
    }
    if (newPassword !== confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    try {
      await authApi.resetPassword({ username, email, code, newPassword });
      wx.showToast({ title: '密码重置成功', icon: 'success' });
      setTimeout(() => wx.reLaunch({ url: '/pages/login/login' }), 1500);
    } catch (err) {
      wx.showToast({ title: err.message || '重置失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  goLogin() {
    wx.reLaunch({ url: '/pages/login/login' });
  }
});
