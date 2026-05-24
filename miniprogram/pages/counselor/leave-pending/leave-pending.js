const { listPendingLeaves, approveLeave } = require('../../../api/counselor');

Page({
  data: {
    list: [],
    remarks: {}
  },

  onShow() {
    this.loadData();
  },

  async loadData() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const list = await listPendingLeaves();
      this.setData({ list: list || [] });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  onRemarkInput(e) {
    const id = e.currentTarget.dataset.id;
    const remarks = { ...this.data.remarks, [id]: e.detail.value };
    this.setData({ remarks });
  },

  async onApprove(e) {
    const id = e.currentTarget.dataset.id;
    const remark = this.data.remarks[id] || '';
    try {
      await approveLeave(id, { action: 'APPROVE', remark });
      wx.showToast({ title: '已通过', icon: 'success' });
      this.loadData();
    } catch (err) {
      console.error(err);
    }
  },

  async onReject(e) {
    const id = e.currentTarget.dataset.id;
    const remark = this.data.remarks[id] || '';
    if (!remark) {
      wx.showToast({ title: '请填写驳回原因', icon: 'none' });
      return;
    }
    try {
      await approveLeave(id, { action: 'REJECT', remark });
      wx.showToast({ title: '已驳回', icon: 'success' });
      this.loadData();
    } catch (err) {
      console.error(err);
    }
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
