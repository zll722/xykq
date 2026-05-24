const { listMyAttendanceRecords } = require('../../../api/attendance');

Page({
  data: {
    records: [],
    filteredRecords: [],
    currentFilter: 'all',
    filterStartDate: '',
    filterEndDate: ''
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

  onStartDateChange(e) {
    this.setData({ filterStartDate: e.detail.value });
    this.applyFilter();
  },

  onEndDateChange(e) {
    this.setData({ filterEndDate: e.detail.value });
    this.applyFilter();
  },

  clearDateFilter() {
    this.setData({ filterStartDate: '', filterEndDate: '' });
    this.applyFilter();
  },

  applyFilter() {
    const { records, currentFilter, filterStartDate, filterEndDate } = this.data;
    let filtered = currentFilter === 'all' ? records : records.filter(r => r.status === currentFilter);
    if (filterStartDate) {
      filtered = filtered.filter(r => r.attendanceDate >= filterStartDate);
    }
    if (filterEndDate) {
      filtered = filtered.filter(r => r.attendanceDate <= filterEndDate);
    }
    this.setData({ filteredRecords: filtered });
  },

  onPullDownRefresh() {
    this.loadRecords().then(() => wx.stopPullDownRefresh());
  }
});
