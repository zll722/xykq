const { listMyLeaveProgress, cancelLeave } = require('../../../api/leave');

Page({
  data: { records: [] },

  onShow() {
    this.loadData();
  },

  async loadData() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const records = await listMyLeaveProgress();
      this.setData({ records: records || [] });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  async onCancel(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认',
      content: '确定要取消该请假申请吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await cancelLeave(id);
            wx.showToast({ title: '已取消', icon: 'success' });
            this.loadData();
          } catch (err) {
            console.error(err);
          }
        }
      }
    });
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
