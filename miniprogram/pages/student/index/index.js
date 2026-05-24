const { myOverview, listMySchedules } = require('../../../api/attendance');
const { getUserInfo, checkRole } = require('../../../utils/auth');

Page({
  data: {
    userInfo: {},
    stats: {},
    todayCourses: []
  },

  onLoad() {
    if (!checkRole('USER')) return;
    this.setData({ userInfo: getUserInfo() });
  },

  onShow() {
    this.loadData();
  },

  async loadData() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const [stats, schedules] = await Promise.all([myOverview(), listMySchedules()]);
      this.setData({ stats: stats || {} });
      const today = new Date().getDay() || 7;
      const todayCourses = (schedules || []).filter(s => s.weekDay === today);
      this.setData({ todayCourses });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  goSchedule() {
    wx.reLaunch({ url: '/pages/student/schedule/schedule' });
  },

  goSignIn(e) {
    const schedule = e.currentTarget.dataset.schedule;
    if (schedule && schedule.scheduleId) {
      wx.navigateTo({ url: `/pages/student/sign-in/sign-in?scheduleId=${schedule.scheduleId}&courseName=${schedule.courseName}` });
    } else {
      wx.navigateTo({ url: '/pages/student/sign-in/sign-in' });
    }
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
