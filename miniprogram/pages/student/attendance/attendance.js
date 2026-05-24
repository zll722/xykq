const { listMyAttendanceRecords } = require('../../../api/attendance');

Page({
  data: {
    records: [],
    filteredRecords: [],
    currentFilter: 'all'
  },

  onShow() {
    this.loadRecords();
  },

  async loadRecords() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const records = await listMyAttendanceRecords();
      this.setData({ records: records || [] });
      this.applyFilter();
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  setFilter(e) {
    this.setData({ currentFilter: e.currentTarget.dataset.filter });
    this.applyFilter();
  },

  applyFilter() {
    const { records, currentFilter } = this.data;
    const filtered = currentFilter === 'all' ? records : records.filter(r => r.status === currentFilter);
    this.setData({ filteredRecords: filtered });
  },

  onPullDownRefresh() {
    this.loadRecords().then(() => wx.stopPullDownRefresh());
  }
});
