const { getUserInfo, logout } = require('../../../utils/auth');
const { changePassword } = require('../../../api/auth');

Page({
  data: { userInfo: {} },

  onShow() {
    this.setData({ userInfo: getUserInfo() || {} });
  },

  goClasses() {
    wx.navigateTo({ url: '/pages/teacher/classes/classes' });
  },

  goChangePassword() {
    wx.showModal({
      title: '修改密码',
      placeholderText: '请输入旧密码',
      editable: true,
      success: (res1) => {
        if (!res1.confirm || !res1.content) return;
        const oldPassword = res1.content;
        wx.showModal({
          title: '输入新密码',
          placeholderText: '请输入新密码（至少6位）',
          editable: true,
          success: async (res2) => {
            if (!res2.confirm || !res2.content) return;
            const newPassword = res2.content;
            if (newPassword.length < 6) {
              wx.showToast({ title: '新密码至少6位', icon: 'none' });
              return;
            }
            try {
              wx.showLoading({ title: '修改中...' });
              await changePassword({ oldPassword, newPassword });
              wx.hideLoading();
              wx.showToast({ title: '密码修改成功', icon: 'success' });
            } catch (err) {
              wx.hideLoading();
              console.error(err);
            }
          }
        });
      }
    });
  },

  onLogout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success(res) {
        if (res.confirm) {
          logout();
        }
      }
    });
  }
});
