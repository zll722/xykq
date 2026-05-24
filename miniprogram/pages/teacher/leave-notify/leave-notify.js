const { getLeaveNotifications } = require('../../../api/teacher');

Page({
  data: { notifications: [] },

  onShow() {
    this.loadData();
  },

  async loadData() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const notifications = await getLeaveNotifications();
      this.setData({ notifications: notifications || [] });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
