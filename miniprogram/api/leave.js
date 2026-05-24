const { get, post, put } = require('../utils/request');

const applyLeave = (data) => post('/leave/requests', data);
const listMyLeave = () => get('/leave/requests/my');
const listMyLeaveProgress = () => get('/leave/requests/my-progress');
const cancelLeave = (id) => put(`/leave/requests/${id}/cancel`);
const getLeaveDetail = (id) => get(`/leave/requests/${id}`);

module.exports = { applyLeave, listMyLeave, listMyLeaveProgress, cancelLeave, getLeaveDetail };
