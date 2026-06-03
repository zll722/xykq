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

  goCourses() {
    wx.navigateTo({ url: '/pages/teacher/courses/courses' });
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

  goAttendanceRecords() {
    wx.navigateTo({ url: '/pages/teacher/attendance-records/attendance-records' });
  },

  goAttendanceExceptions() {
    wx.navigateTo({ url: '/pages/teacher/attendance-exceptions/attendance-exceptions' });
  },

  goAttendanceAdjustments() {
    wx.navigateTo({ url: '/pages/teacher/attendance-adjustments/attendance-adjustments' });
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
