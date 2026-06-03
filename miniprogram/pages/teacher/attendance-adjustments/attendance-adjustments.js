const { listTeacherRecords, adjustTeacherRecord, getMyClasses, getMyCourses } = require('../../../api/teacher');

Page({
  data: {
    records: [],
    classes: [],
    courses: [],
    courseIndex: -1,
    classIndex: -1,
    date: '',
    
    showAdjustModal: false,
    selectedRecord: null,
    adjustStatusIndex: 0,
    adjustOptions: [
      { label: '正常', value: 'PRESENT' },
      { label: '迟到', value: 'LATE' },
      { label: '缺勤', value: 'ABSENT' },
      { label: '请假', value: 'LEAVE' }
    ],
    reason: ''
  },

  onLoad(options) {
    this.initData(options);
  },

  async initData(options = {}) {
    wx.showLoading({ title: '加载中' });
    try {
      const classes = await getMyClasses() || [];
      const courses = await getMyCourses() || [];
      this.setData({ classes, courses });
      
      if (options.courseId || options.classId || options.date) {
        const courseIndex = courses.findIndex(c => String(c.id || c.courseId) === String(options.courseId));
        const classIndex = classes.findIndex(c => String(c.id || c.classId) === String(options.classId));
        this.setData({
          courseIndex: courseIndex >= 0 ? courseIndex : -1,
          classIndex: classIndex >= 0 ? classIndex : -1,
          date: options.date || ''
        });
      }
      
      await this.loadRecords();

      if (options.id) {
        const target = this.data.records.find(r => String(r.id) === String(options.id));
        if (target) {
          this.openAdjustModal(target);
        }
      }
    } catch (e) {
      console.error(e);
      wx.hideLoading();
    }
  },

  async loadRecords() {
    wx.showLoading({ title: '加载中' });
    try {
      const { courses, classes, courseIndex, classIndex, date } = this.data;
      const params = {};
      if (courseIndex >= 0) params.courseId = courses[courseIndex].id || courses[courseIndex].courseId;
      if (classIndex >= 0) params.classId = classes[classIndex].id || classes[classIndex].classId;
      if (date) params.attendanceDate = date;

      const records = await listTeacherRecords(params);
      this.setData({ records: records || [] });
    } catch (e) {
      console.error(e);
      wx.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  bindCourseChange(e) { this.setData({ courseIndex: e.detail.value }); this.loadRecords(); },
  bindClassChange(e) { this.setData({ classIndex: e.detail.value }); this.loadRecords(); },
  bindDateChange(e) { this.setData({ date: e.detail.value }); this.loadRecords(); },

  clearFilters() {
    this.setData({ courseIndex: -1, classIndex: -1, date: '' });
    this.loadRecords();
  },

  onAdjustBtnTap(e) {
    const item = e.currentTarget.dataset.item;
    this.openAdjustModal(item);
  },

  openAdjustModal(item) {
    this.setData({
      selectedRecord: item,
      showAdjustModal: true,
      adjustStatusIndex: 0,
      reason: ''
    });
  },

  closeAdjustModal() {
    this.setData({ showAdjustModal: false, selectedRecord: null });
  },

  bindAdjustStatusChange(e) {
    this.setData({ adjustStatusIndex: e.detail.value });
  },

  bindReasonInput(e) {
    this.setData({ reason: e.detail.value });
  },

  async submitAdjust() {
    const { selectedRecord, adjustOptions, adjustStatusIndex, reason } = this.data;
    if (!selectedRecord) return;
    if (!reason.trim()) {
      return wx.showToast({ title: '请输入修正原因', icon: 'none' });
    }

    wx.showLoading({ title: '提交中' });
    try {
      const payload = {
        afterStatus: adjustOptions[adjustStatusIndex].value,
        reason: reason.trim()
      };
      await adjustTeacherRecord(selectedRecord.id, payload);
      wx.showToast({ title: '修正成功' });
      this.closeAdjustModal();
      this.loadRecords();
    } catch (e) {
      console.error(e);
      wx.showToast({ title: e.message || '修正失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  onPullDownRefresh() {
    this.loadRecords().then(() => wx.stopPullDownRefresh());
  }
});
