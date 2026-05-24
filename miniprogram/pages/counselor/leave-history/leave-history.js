const { listLeaveHistory } = require('../../../api/counselor');

Page({
  data: { records: [] },

  onShow() {
    this.loadData();
  },

  async loadData() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const records = await listLeaveHistory();
      this.setData({ records: records || [] });
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
