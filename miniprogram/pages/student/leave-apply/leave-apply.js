const { applyLeave } = require('../../../api/leave');
const { uploadFile } = require('../../../utils/request');
const { checkRole } = require('../../../utils/auth');

/**
 * @param {number} hour
 * @param {number} minute
 * @param {number} dayOffset
 * @returns {{ date: string, clock: string, datetime: string }}
 */
function buildDatetime(hour, minute, dayOffset) {
  const date = new Date();
  date.setDate(date.getDate() + (dayOffset || 0));
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(hour).padStart(2, '0');
  const mi = String(minute).padStart(2, '0');
  return {
    date: `${yyyy}-${mm}-${dd}`,
    clock: `${hh}:${mi}`,
    datetime: `${yyyy}-${mm}-${dd} ${hh}:${mi}:00`
  };
}

Page({
  data: {
    leaveTypes: [
      { label: '病假', value: 'SICK' },
      { label: '事假', value: 'PERSONAL' },
      { label: '公假', value: 'PUBLIC' },
      { label: '其他', value: 'OTHER' }
    ],
    selectedType: '',
    startDate: '',
    startClock: '',
    endDate: '',
    endClock: '',
    form: {
      leaveType: '',
      startTime: '',
      endTime: '',
      reason: '',
      proofUrl: ''
    },
    attachment: '',
    loading: false
  },

  onLoad() {
    if (!checkRole('USER')) return;
  },

  onTypeChange(e) {
    const idx = e.detail.value;
    const type = this.data.leaveTypes[idx];
    this.setData({
      selectedType: type.label,
      'form.leaveType': type.value
    });
  },

  onStartDateChange(e) {
    const date = e.detail.value;
    this.setData({ startDate: date });
    this._syncStartTime(date, this.data.startClock);
  },

  onStartTimeChange(e) {
    const clock = e.detail.value;
    this.setData({ startClock: clock });
    this._syncStartTime(this.data.startDate, clock);
  },

  onEndDateChange(e) {
    const date = e.detail.value;
    this.setData({ endDate: date });
    this._syncEndTime(date, this.data.endClock);
  },

  onEndTimeChange(e) {
    const clock = e.detail.value;
    this.setData({ endClock: clock });
    this._syncEndTime(this.data.endDate, clock);
  },

  _syncStartTime(date, clock) {
    if (date && clock) {
      this.setData({ 'form.startTime': `${date} ${clock}:00` });
    }
  },

  _syncEndTime(date, clock) {
    if (date && clock) {
      this.setData({ 'form.endTime': `${date} ${clock}:00` });
    }
  },

  applyPreset(e) {
    const preset = e.currentTarget.dataset.preset;
    let start, end;
    if (preset === 'morningToday') {
      start = buildDatetime(8, 0, 0);
      end = buildDatetime(12, 0, 0);
    } else if (preset === 'afternoonToday') {
      start = buildDatetime(13, 30, 0);
      end = buildDatetime(18, 0, 0);
    } else {
      start = buildDatetime(8, 0, 1);
      end = buildDatetime(18, 0, 1);
    }
    this.setData({
      startDate: start.date,
      startClock: start.clock,
      endDate: end.date,
      endClock: end.clock,
      'form.startTime': start.datetime,
      'form.endTime': end.datetime
    });
  },

  onReasonInput(e) {
    this.setData({ 'form.reason': e.detail.value });
  },

  async chooseFile() {
    try {
      const res = await new Promise((resolve, reject) => {
        wx.chooseMessageFile({
          count: 1,
          type: 'file',
          success: resolve,
          fail: reject
        });
      });
      const file = res.tempFiles[0];
      wx.showLoading({ title: '上传中...' });
      const uploadRes = await uploadFile(file.path, 'leave');
      wx.hideLoading();
      this.setData({
        attachment: file.name,
        'form.proofUrl': typeof uploadRes === 'string' ? uploadRes : (uploadRes.path || uploadRes.url || '')
      });
      wx.showToast({ title: '上传成功', icon: 'success' });
    } catch (err) {
      wx.hideLoading();
      if (err && err.errMsg && err.errMsg.includes('cancel')) return;
      wx.showToast({ title: '上传失败', icon: 'none' });
      console.error(err);
    }
  },

  async submitLeave() {
    const { form } = this.data;
    if (!form.leaveType) {
      wx.showToast({ title: '请选择请假类型', icon: 'none' });
      return;
    }
    if (!form.startTime) {
      wx.showToast({ title: '请选择开始时间', icon: 'none' });
      return;
    }
    if (!form.endTime) {
      wx.showToast({ title: '请选择结束时间', icon: 'none' });
      return;
    }
    if (!form.reason || !form.reason.trim()) {
      wx.showToast({ title: '请填写请假原因', icon: 'none' });
      return;
    }
    if (form.startTime >= form.endTime) {
      wx.showToast({ title: '结束时间不能早于或等于开始时间', icon: 'none' });
      return;
    }
    this.setData({ loading: true });
    try {
      await applyLeave({
        leaveType: form.leaveType,
        startTime: form.startTime,
        endTime: form.endTime,
        reason: form.reason.trim(),
        proofUrl: form.proofUrl || null
      });
      wx.showToast({ title: '提交成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateTo({ url: '/pages/student/leave-progress/leave-progress' });
      }, 1500);
    } catch (err) {
      console.error(err);
    } finally {
      this.setData({ loading: false });
    }
  },

  goHistory() {
    wx.navigateTo({ url: '/pages/student/leave-history/leave-history' });
  },

  goProgress() {
    wx.navigateTo({ url: '/pages/student/leave-progress/leave-progress' });
  }
});
