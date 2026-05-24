const { get, post } = require('../utils/request');

const listMySchedules = () => get('/schedules/my');
const signIn = (data) => post('/attendance/sign-in', data);
const listMyAttendanceRecords = () => get('/attendance/records/my');
const myOverview = () => get('/stats/my-overview');
const myCalendar = (params) => get('/stats/my-calendar', params);

module.exports = { listMySchedules, signIn, listMyAttendanceRecords, myOverview, myCalendar };
