const { get, post } = require('../utils/request');

const getDashboard = () => get('/counselor/dashboard');
const getMyClasses = () => get('/counselor/classes');
const getClassStudents = (classId) => get(`/counselor/classes/${classId}/students`);
const listPendingLeaves = () => get('/counselor/leave/pending');
const approveLeave = (requestId, data) => post(`/counselor/leave/${requestId}/approve`, data);
const listLeaveHistory = () => get('/counselor/leave/history');

module.exports = { getDashboard, getMyClasses, getClassStudents, listPendingLeaves, approveLeave, listLeaveHistory };
