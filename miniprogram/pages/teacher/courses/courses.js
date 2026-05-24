const { getMyCourses } = require('../../../api/teacher');

const WEEK_DAYS = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日'];

Page({
  data: { courses: [] },

  onShow() {
    this.loadCourses();
  },

  async loadCourses() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const list = await getMyCourses();
      const courses = (list || []).map(item => ({
        ...item,
        weekDayLabel: WEEK_DAYS[item.weekDay] || ('第' + item.weekDay + '天'),
        schedule: `${WEEK_DAYS[item.weekDay] || ''} ${item.startTime}–${item.endTime}`
      }));
      this.setData({ courses });
    } catch (err) {
      console.error('加载课程失败', err);
    } finally {
      wx.hideLoading();
    }
  },

  onPullDownRefresh() {
    this.loadCourses().then(() => wx.stopPullDownRefresh());
  }
});
