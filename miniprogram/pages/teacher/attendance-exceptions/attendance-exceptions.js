const { listTeacherExceptions, getMyClasses, getMyCourses } = require('../../../api/teacher');

Page({
  data: {
    records: [],
    classes: [],
    courses: [],
    courseIndex: -1,
    classIndex: -1,
    date: '',
    statusIndex: -1,
    statusOptions: [
      { label: '全部', value: '' },
      { label: '迟到', value: 'LATE' },
      { label: '缺勤', value: 'ABSENT' },
      { label: '早退', value: 'EARLY_LEAVE' }
    ]
  },

  onLoad() {
    this.initData();
  },

  async initData() {
    wx.showLoading({ title: '加载中' });
    try {
      const classes = await getMyClasses() || [];
      const courses = await getMyCourses() || [];
      this.setData({ classes, courses });
      this.loadRecords();
    } catch (e) {
      console.error(e);
      wx.hideLoading();
    }
  },

  async loadRecords() {
    wx.showLoading({ title: '加载中' });
    try {
      const { courses, classes, courseIndex, classIndex, date, statusIndex, statusOptions } = this.data;
      const params = {};
      if (courseIndex >= 0) params.courseId = courses[courseIndex].id || courses[courseIndex].courseId;
      if (classIndex >= 0) params.classId = classes[classIndex].id || classes[classIndex].classId;
      if (date) params.attendanceDate = date;
      if (statusIndex > 0) params.status = statusOptions[statusIndex].value;

      const records = await listTeacherExceptions(params);
      
      const processed = (records || []).map(r => {
        let pLabel = '低', pColor = 'bg-info', sLabel = r.status;
        if(r.status === 'ABSENT') { pLabel = '高'; pColor = 'bg-danger'; sLabel = '缺勤'; }
        if(r.status === 'LATE') { pLabel = '中'; pColor = 'bg-warning'; sLabel = '迟到'; }
        if(r.status === 'EARLY_LEAVE') { pLabel = '中'; pColor = 'bg-warning'; sLabel = '早退'; }
        return { ...r, pLabel, pColor, sLabel };
      });

      this.setData({ records: processed });
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
  bindStatusChange(e) { this.setData({ statusIndex: e.detail.value }); this.loadRecords(); },

  clearFilters() {
    this.setData({ courseIndex: -1, classIndex: -1, date: '', statusIndex: -1 });
    this.loadRecords();
  },

  goResolve(e) {
    const item = e.currentTarget.dataset.item;
    // jump to adjustments page with pre-filled filters
    wx.navigateTo({
      url: `/pages/teacher/attendance-adjustments/attendance-adjustments?courseId=${item.courseId}&classId=${item.classId}&date=${item.attendanceDate}&id=${item.id}`
    });
  },

  onPullDownRefresh() {
    this.loadRecords().then(() => wx.stopPullDownRefresh());
  }
});
