const { signIn } = require('../../../api/attendance');

Page({
  data: {
    scheduleId: '',
    courseName: '',
    currentTime: '',
    loading: false,
    signResult: null
  },

  onLoad(options) {
    this.setData({
      scheduleId: options.scheduleId || '',
      courseName: options.courseName || '课程签到'
    });
    this.updateTime();
    this.timer = setInterval(() => this.updateTime(), 1000);
  },

  onUnload() {
    if (this.timer) clearInterval(this.timer);
  },

  updateTime() {
    const now = new Date();
    const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`;
    this.setData({ currentTime: time });
  },

  async doSignIn() {
    this.setData({ loading: true });
    try {
      const res = await signIn({ scheduleId: Number(this.data.scheduleId) });
      const statusMap = { NORMAL: '签到成功', LATE: '迟到签到', ABSENT: '缺勤' };
      this.setData({
        signResult: {
          status: res.status,
          statusText: statusMap[res.status] || res.status,
          signTime: res.signTime || this.data.currentTime
        }
      });
    } catch (err) {
      console.error(err);
    } finally {
      this.setData({ loading: false });
    }
  }
});
