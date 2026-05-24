const { signIn, getMyScheduleRecord } = require('../../../api/attendance');

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
    if (options.scheduleId) {
      this.checkExistingRecord(options.scheduleId);
    }
  },

  async checkExistingRecord(scheduleId) {
    try {
      const res = await getMyScheduleRecord(scheduleId);
      if (res && res.status) {
        this.applySignResult(res);
      }
    } catch (err) {
      // 未签到或查询失败，保持签到按钮显示
    }
  },

  applySignResult(res) {
    const statusMap = { PRESENT: '签到成功', NORMAL: '签到成功', LATE: '迟到签到', ABSENT: '缺勤' };
    const rawTime = res.signedAt || '';
    const signTime = rawTime.length >= 19 ? rawTime.substring(11, 19) : rawTime;
    this.setData({
      signResult: {
        status: res.status,
        statusText: statusMap[res.status] || res.status,
        signTime: signTime || this.data.currentTime
      }
    });
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
      this.applySignResult(res);
    } catch (err) {
      console.error(err);
    } finally {
      this.setData({ loading: false });
    }
  }
});
