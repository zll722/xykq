const { applyLeave } = require('../../../api/leave');
const { uploadFile } = require('../../../utils/request');
const { checkRole } = require('../../../utils/auth');

Page({
  data: {
    leaveTypes: [
      { label: '事假', value: 'PERSONAL' },
      { label: '病假', value: 'SICK' },
      { label: '其他', value: 'OTHER' }
    ],
    selectedType: '',
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
    this.setData({ 'form.startTime': e.detail.value + ' 00:00' });
  },

  onEndDateChange(e) {
    this.setData({ 'form.endTime': e.detail.value + ' 23:59' });
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
        'form.proofUrl': uploadRes.url || uploadRes
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
      wx.showToast({ title: '请选择开始日期', icon: 'none' });
      return;
    }
    if (!form.endTime) {
      wx.showToast({ title: '请选择结束日期', icon: 'none' });
      return;
    }
    if (!form.reason || !form.reason.trim()) {
      wx.showToast({ title: '请填写请假原因', icon: 'none' });
      return;
    }
    if (form.startTime > form.endTime) {
      wx.showToast({ title: '结束日期不能早于开始日期', icon: 'none' });
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
