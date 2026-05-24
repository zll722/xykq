const { getDashboard } = require('../../../api/teacher');
const { getUserInfo, checkRole } = require('../../../utils/auth');

Page({
  data: {
    userInfo: {},
    stats: {}
  },

  onLoad() {
    if (!checkRole('TEACHER')) return;
    this.setData({ userInfo: getUserInfo() });
  },

  onShow() {
    this.loadData();
  },

  async loadData() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const stats = await getDashboard();
      this.setData({ stats: stats || {} });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  goClasses() {
    wx.navigateTo({ url: '/pages/teacher/classes/classes' });
  },

  goReport() {
    wx.reLaunch({ url: '/pages/teacher/attendance-report/attendance-report' });
  },

  goLeave() {
    wx.reLaunch({ url: '/pages/teacher/leave-notify/leave-notify' });
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
