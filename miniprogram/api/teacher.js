const { get, post, put } = require('../utils/request');

const getDashboard = () => get('/teacher/dashboard');
const getMyClasses = () => get('/teacher/classes');
const getMyCourses = () => get('/teacher/courses');
const getClassAttendance = (classId) => get(`/teacher/classes/${classId}/attendance`);
const getLeaveNotifications = () => get('/teacher/leave-notifications');

const listTeacherRecords = (params) => get('/teacher/attendance/records', params);
const listTeacherExceptions = (params) => get('/teacher/attendance/exceptions', params);
const adjustTeacherRecord = (id, data) => post(`/teacher/attendance/records/${id}/adjust`, data);
const resolveTeacherException = (id, data) => put(`/teacher/attendance/exceptions/${id}/resolve`, data);

module.exports = { 
  getDashboard, getMyClasses, getMyCourses, getClassAttendance, getLeaveNotifications,
  listTeacherRecords, listTeacherExceptions, adjustTeacherRecord, resolveTeacherException
};
