const { getMyClasses } = require('../../../api/counselor');

Page({
  data: { classes: [] },

  onShow() {
    this.loadClasses();
  },

  async loadClasses() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const raw = await getMyClasses();
      const classes = (raw || []).map(item => {
        const pct = item.capacity > 0
          ? Math.round(item.studentCount / item.capacity * 100)
          : 0;
        const countLabel = item.capacity > 0
          ? (item.studentCount || 0) + ' / ' + item.capacity
          : String(item.studentCount || 0);
        return { ...item, capacityPct: pct, capacityWidth: pct + '%', countLabel };
      });
      this.setData({ classes });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  goStudents(e) {
    const { id, name } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/counselor/students/students?classId=${id}&className=${name}` });
  },

  onPullDownRefresh() {
    this.loadClasses().then(() => wx.stopPullDownRefresh());
  }
});
