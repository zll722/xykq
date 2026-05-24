const { myOverview, listMySchedules } = require('../../../api/attendance');
const { listMyLeaveProgress } = require('../../../api/leave');
const { listMessages } = require('../../../api/message');
const { getUserInfo, checkRole } = require('../../../utils/auth');

Page({
  data: {
    userInfo: {},
    stats: {},
    todayCourses: [],
    unreadCount: 0,
    latestApprovalText: '暂无审批',
    signStatusText: '加载中'
  },

  onLoad() {
    if (!checkRole('USER')) return;
    this.setData({ userInfo: getUserInfo() });
  },

  onShow() {
    this.loadData();
  },

  async loadData() {
    wx.showLoading({ title: '加载中', mask: true });
    try {
      const [stats, schedules, leaveProgress, messages] = await Promise.all([
        myOverview(),
        listMySchedules(),
        listMyLeaveProgress().catch(() => []),
        listMessages().catch(() => [])
      ]);

      this.setData({ stats: stats || {} });

      const today = new Date().getDay() || 7;
      const todayCourses = (schedules || []).filter(s => s.weekDay === today);
      this.setData({ todayCourses });

      const msgList = Array.isArray(messages) ? messages : [];
      const unreadCount = msgList.filter(m => m.readFlag !== 1).length;
      this.setData({ unreadCount });

      const approvalList = Array.isArray(leaveProgress) ? leaveProgress : [];
      const latest = approvalList[0];
      let latestApprovalText = '暂无审批记录';
      if (latest) {
        const statusMap = { PENDING: '审批中', APPROVED: '已通过', REJECTED: '已驳回', CANCELLED: '已撤销' };
        latestApprovalText = statusMap[latest.approvalStatus] || statusMap[latest.status] || '有新进展';
      }
      this.setData({ latestApprovalText });

      const openCourse = todayCourses.find(c => c.signStatus === 'OPEN');
      const signStatusText = todayCourses.length === 0 ? '今日无课' : openCourse ? '可签到' : '待开放';
      this.setData({ signStatusText });
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  goSchedule() {
    wx.reLaunch({ url: '/pages/student/schedule/schedule' });
  },

  goSignIn(e) {
    const schedule = e.currentTarget.dataset.schedule;
    if (schedule && schedule.scheduleId) {
      wx.navigateTo({ url: `/pages/student/sign-in/sign-in?scheduleId=${schedule.scheduleId}&courseName=${schedule.courseName}` });
    } else {
      wx.navigateTo({ url: '/pages/student/sign-in/sign-in' });
    }
  },

  goMessages() {
    wx.navigateTo({ url: '/pages/student/messages/messages' });
  },

  goLeaveHistory() {
    wx.navigateTo({ url: '/pages/student/leave-history/leave-history' });
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
