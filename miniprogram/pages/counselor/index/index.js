const { getDashboard } = require('../../../api/counselor');
const { getUserInfo, checkRole } = require('../../../utils/auth');

Page({
  data: {
    userInfo: {},
    stats: {}
  },

  onLoad() {
    if (!checkRole('COUNSELOR')) return;
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
    wx.reLaunch({ url: '/pages/counselor/classes/classes' });
  },

  goPending() {
    wx.reLaunch({ url: '/pages/counselor/leave-pending/leave-pending' });
  },

  goHistory() {
    wx.navigateTo({ url: '/pages/counselor/leave-history/leave-history' });
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
