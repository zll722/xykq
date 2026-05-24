const { myOverview, myCalendar } = require('../../../api/attendance');

/**
 * 格式化日期为 YYYY-MM-DD
 * @param {Date} date
 * @returns {string}
 */
function formatDate(date) {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
}

Page({
  data: {
    overview: {
      totalCount: 0,
      presentCount: 0,
      lateCount: 0,
      absentCount: 0,
      presentRate: 0
    },
    calendar: [],
    startDate: '',
    endDate: '',
    querying: false
  },

  onLoad() {
    // 默认查询近 30 天
    const end = new Date();
    const start = new Date();
    start.setDate(start.getDate() - 29);
    this.setData({
      startDate: formatDate(start),
      endDate: formatDate(end)
    });
    this.loadOverview();
    this.loadCalendar();
  },

  async loadOverview() {
    try {
      const data = await myOverview();
      this.setData({ overview: data || {} });
    } catch (err) {
      console.error('加载统计失败', err);
    }
  },

  async loadCalendar() {
    const { startDate, endDate } = this.data;
    this.setData({ querying: true });
    try {
      const params = {};
      if (startDate) params.startDate = startDate;
      if (endDate) params.endDate = endDate;
      const data = await myCalendar(params);
      this.setData({ calendar: Array.isArray(data) ? data : [] });
    } catch (err) {
      console.error('加载日历失败', err);
      this.setData({ calendar: [] });
    } finally {
      this.setData({ querying: false });
    }
  },

  onStartDateChange(e) {
    this.setData({ startDate: e.detail.value });
  },

  onEndDateChange(e) {
    this.setData({ endDate: e.detail.value });
  },

  onQuery() {
    this.loadCalendar();
  },

  onPullDownRefresh() {
    Promise.all([this.loadOverview(), this.loadCalendar()])
      .finally(() => wx.stopPullDownRefresh());
  }
});
