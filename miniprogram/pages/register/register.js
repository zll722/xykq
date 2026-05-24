const authApi = require('../../api/auth');

Page({
  data: {
    realName: '',
    studentNo: '',
    classId: '',
    username: '',
    password: '',
    confirmPassword: '',
    classes: [],
    classIndex: -1,
    classNames: [],
    loading: false
  },

  onLoad() {
    this.loadClasses();
  },

  async loadClasses() {
    try {
      const list = await authApi.getClasses();
      const classNames = list.map(c => `${c.className}（${c.classCode}）`);
      this.setData({ classes: list, classNames });
    } catch (e) {
      wx.showToast({ title: '获取班级失败', icon: 'none' });
    }
  },

  onRealNameInput(e) { this.setData({ realName: e.detail.value }); },
  onStudentNoInput(e) { this.setData({ studentNo: e.detail.value }); },
  onUsernameInput(e) { this.setData({ username: e.detail.value }); },
  onPasswordInput(e) { this.setData({ password: e.detail.value }); },
  onConfirmPasswordInput(e) { this.setData({ confirmPassword: e.detail.value }); },

  onClassChange(e) {
    const index = parseInt(e.detail.value);
    this.setData({ classIndex: index, classId: this.data.classes[index].id });
  },

  async onRegister() {
    const { realName, studentNo, classId, username, password, confirmPassword } = this.data;
    if (!realName || !studentNo || !classId || !username || !password || !confirmPassword) {
      wx.showToast({ title: '请填写所有字段', icon: 'none' });
      return;
    }
    if (password.length < 6) {
      wx.showToast({ title: '密码不能少于6位', icon: 'none' });
      return;
    }
    if (password !== confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    try {
      await authApi.register({ realName, studentNo, classId, username, password });
      wx.showToast({ title: '注册成功', icon: 'success' });
      setTimeout(() => wx.reLaunch({ url: '/pages/login/login' }), 1500);
    } catch (err) {
      console.error('注册失败', err);
    } finally {
      this.setData({ loading: false });
    }
  },

  goLogin() {
    wx.reLaunch({ url: '/pages/login/login' });
  }
});
