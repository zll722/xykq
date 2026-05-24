const { listMySchedules } = require('../../../api/attendance');
const { checkRole } = require('../../../utils/auth');

Page({
  data: {
    weekDays: [
      { label: '周一', value: 1 },
      { label: '周二', value: 2 },
      { label: '周三', value: 3 },
      { label: '周四', value: 4 },
      { label: '周五', value: 5 },
      { label: '周六', value: 6 },
      { label: '周日', value: 7 }
    ],
    currentDay: 1,
    schedules: [],
    filteredSchedules: []
  },

  onLoad() {
    if (!checkRole('USER')) return;
    const today = new Date().getDay() || 7;
    this.setData({ currentDay: today });
  },

  onShow() {
    this.loadSchedules();
  },

  async loadSchedules() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const schedules = await listMySchedules();
      this.setData({ schedules: schedules || [] });
      this.filterByDay();
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  switchDay(e) {
    this.setData({ currentDay: e.currentTarget.dataset.day });
    this.filterByDay();
  },

  filterByDay() {
    const filtered = this.data.schedules.filter(s => s.weekDay === this.data.currentDay);
    this.setData({ filteredSchedules: filtered });
  },

  goSignIn(e) {
    const item = e.currentTarget.dataset.item;
    wx.navigateTo({ url: `/pages/student/sign-in/sign-in?scheduleId=${item.scheduleId}&courseName=${item.courseName}` });
  },

  onPullDownRefresh() {
    this.loadSchedules().then(() => wx.stopPullDownRefresh());
  }
});
