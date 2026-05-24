const { getMyClasses } = require('../../../api/counselor');

Page({
  data: { classes: [] },

  onShow() {
    this.loadClasses();
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

  goStudents(e) {
    const { id, name } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/counselor/students/students?classId=${id}&className=${name}` });
  },

  onPullDownRefresh() {
    this.loadClasses().then(() => wx.stopPullDownRefresh());
  }
});
