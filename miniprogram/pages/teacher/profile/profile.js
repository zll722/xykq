const { getUserInfo, logout } = require('../../../utils/auth');
const { changePassword } = require('../../../api/auth');
const { getMyProfile, updateMyProfile } = require('../../../api/user');
const { uploadFile } = require('../../../utils/request');
const { resolveFileUrl } = require('../../../utils/url');

Page({
  data: {
    userInfo: {},
    profile: { realName: '', phone: '', email: '', avatarUrl: '' },
    saving: false
  },

  onShow() {
    this.setData({ userInfo: getUserInfo() || {} });
    this.loadProfile();
  },

  async loadProfile() {
    try {
      const profile = await getMyProfile();
      if (profile && profile.avatarUrl) {
        profile.avatarUrl = resolveFileUrl(profile.avatarUrl);
      }
      this.setData({ profile: profile || {} });
    } catch (err) {
      console.error('加载资料失败', err);
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`profile.${field}`]: e.detail.value });
  },

  async saveProfile() {
    const { profile } = this.data;
    this.setData({ saving: true });
    try {
      await updateMyProfile({
        realName: profile.realName,
        phone: profile.phone,
        email: profile.email,
        avatarUrl: profile.avatarUrl
      });
      wx.showToast({ title: '资料已保存', icon: 'success' });
    } catch (err) {
      console.error('保存失败', err);
      wx.showToast({ title: '保存失败，请重试', icon: 'none' });
    } finally {
      this.setData({ saving: false });
    }
  },

  chooseAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const tempFilePath = res.tempFilePaths[0];
        wx.showLoading({ title: '上传中...' });
        try {
          const result = await uploadFile(tempFilePath, 'avatar');
          const rawPath = (result && result.path) ? result.path : (typeof result === 'string' ? result : '');
          const avatarUrl = resolveFileUrl(rawPath);
          this.setData({ 'profile.avatarUrl': avatarUrl });
          wx.showToast({ title: '头像已更新', icon: 'success' });
        } catch (err) {
          wx.showToast({ title: '上传失败，请重试', icon: 'none' });
          console.error(err);
        } finally {
          wx.hideLoading();
        }
      }
    });
  },

  goCourses() {
    wx.navigateTo({ url: '/pages/teacher/courses/courses' });
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
