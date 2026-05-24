const TABS_CONFIG = {
  USER: [
    { text: '首页', path: '/pages/student/index/index', icon: '⊞', activeIcon: '⊞' },
    { text: '课程', path: '/pages/student/schedule/schedule', icon: '◫', activeIcon: '◫' },
    { text: '请假', path: '/pages/student/leave-apply/leave-apply', icon: '✎', activeIcon: '✎' },
    { text: '我的', path: '/pages/student/profile/profile', icon: '◯', activeIcon: '◯' }
  ],
  TEACHER: [
    { text: '工作台', path: '/pages/teacher/index/index', icon: '⊞', activeIcon: '⊞' },
    { text: '考勤', path: '/pages/teacher/attendance-report/attendance-report', icon: '▤', activeIcon: '▤' },
    { text: '请假', path: '/pages/teacher/leave-notify/leave-notify', icon: '◈', activeIcon: '◈' },
    { text: '我的', path: '/pages/teacher/profile/profile', icon: '◯', activeIcon: '◯' }
  ],
  COUNSELOR: [
    { text: '工作台', path: '/pages/counselor/index/index', icon: '⊞', activeIcon: '⊞' },
    { text: '班级', path: '/pages/counselor/classes/classes', icon: '▦', activeIcon: '▦' },
    { text: '审批', path: '/pages/counselor/leave-pending/leave-pending', icon: '✓', activeIcon: '✓' },
    { text: '我的', path: '/pages/counselor/profile/profile', icon: '◯', activeIcon: '◯' }
  ]
};

Component({
  properties: {
    role: { type: String, value: 'USER' },
    currentIndex: { type: Number, value: 0 }
  },

  data: {
    tabs: []
  },

  lifetimes: {
    attached() {
      this.setData({ tabs: TABS_CONFIG[this.data.role] || TABS_CONFIG.USER });
    }
  },

  observers: {
    'role': function(role) {
      this.setData({ tabs: TABS_CONFIG[role] || TABS_CONFIG.USER });
    }
  },

  methods: {
    onTabTap(e) {
      const { index, path } = e.currentTarget.dataset;
      if (index === this.data.currentIndex) return;
      wx.reLaunch({ url: path });
    }
  }
});
