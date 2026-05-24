const { listMessages, readMessage, readAllMessages } = require('../../../api/message');

Page({
  data: { messages: [] },

  onShow() {
    this.loadMessages();
  },

  async loadMessages() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const messages = await listMessages();
      this.setData({ messages: messages || [] });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  async onRead(e) {
    const id = e.currentTarget.dataset.id;
    try {
      await readMessage(id);
      this.loadMessages();
    } catch (err) {
      console.error(err);
    }
  },

  async readAll() {
    try {
      await readAllMessages();
      wx.showToast({ title: '已全部标记已读', icon: 'success' });
      this.loadMessages();
    } catch (err) {
      console.error(err);
    }
  },

  onPullDownRefresh() {
    this.loadMessages().then(() => wx.stopPullDownRefresh());
  }
});
