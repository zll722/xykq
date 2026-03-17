import request from '../utils/request';

export function applyLeaveApi(payload) {
  return request.post('/leave/requests', payload);
}

export function listMyLeaveApi() {
  return request.get('/leave/requests/my');
}

export function listMyLeaveProgressApi() {
  return request.get('/leave/requests/my-progress');
}

export function cancelLeaveApi(id) {
  return request.put(`/leave/requests/${id}/cancel`);
}

export function listPendingLeaveApi() {
  return request.get('/leave/approvals/pending');
}

export function approveLeaveApi(id, payload) {
  return request.post(`/leave/approvals/${id}`, payload);
}

export function listLeaveApprovalHistoryApi() {
  return request.get('/leave/approvals/history');
}
