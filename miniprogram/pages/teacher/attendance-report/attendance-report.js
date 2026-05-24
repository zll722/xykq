const { getMyClasses, getClassAttendance } = require('../../../api/teacher');

Page({
  data: {
    classes: [],
    classId: '',
    className: '',
    records: []
  },

  onLoad(options) {
    if (options.classId) {
      this.setData({ classId: options.classId, className: decodeURIComponent(options.className || '') });
      this.loadAttendance(options.classId);
    } else {
      this.loadClasses();
    }
  },

  async loadClasses() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const classes = await getMyClasses();
      this.setData({ classes: classes || [] });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  selectClass(e) {
    const { id, name } = e.currentTarget.dataset;
    this.setData({ classId: id, className: name });
    this.loadAttendance(id);
  },

  async loadAttendance(classId) {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const raw = records || [];
      const processed = raw.map(item => ({
        ...item,
        attendRate: item.totalCount > 0 ? Math.round(item.presentCount / item.totalCount * 100) : 0
      }));
      this.setData({ records: processed });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  backToList() {
    this.setData({ classId: '', className: '', records: [] });
    this.loadClasses();
  },

  onPullDownRefresh() {
    const { classId } = this.data;
    const task = classId ? this.loadAttendance(classId) : this.loadClasses();
    task.then(() => wx.stopPullDownRefresh());
  }
});
