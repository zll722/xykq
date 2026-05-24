const { get } = require('../utils/request');

const getDashboard = () => get('/teacher/dashboard');
const getMyClasses = () => get('/teacher/classes');
const getMyCourses = () => get('/teacher/courses');
const getClassAttendance = (classId) => get(`/teacher/classes/${classId}/attendance`);
const getLeaveNotifications = () => get('/teacher/leave-notifications');

module.exports = { getDashboard, getMyClasses, getMyCourses, getClassAttendance, getLeaveNotifications };
