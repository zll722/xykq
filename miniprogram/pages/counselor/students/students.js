const { getClassStudents } = require('../../../api/counselor');

Page({
  data: {
    classId: '',
    className: '',
    students: []
  },

  onLoad(options) {
    this.setData({
      classId: options.classId || '',
      className: options.className || ''
    });
    wx.setNavigationBarTitle({ title: options.className || '班级学生' });
    this.loadStudents();
  },

  async loadStudents() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const students = await getClassStudents(this.data.classId);
      this.setData({ students: students || [] });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  onPullDownRefresh() {
    this.loadStudents().then(() => wx.stopPullDownRefresh());
  }
});
