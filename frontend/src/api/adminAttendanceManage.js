import request from '../utils/request';

export function listAttendanceExceptionsApi(params) {
  return request.get('/admin/attendance/exceptions', { params });
}

export function adjustAttendanceRecordApi(id, payload) {
  return request.post(`/admin/attendance/records/${id}/adjust`, payload);
}

export function resolveAttendanceExceptionApi(id, payload) {
  return request.put(`/admin/attendance/exceptions/${id}/resolve`, payload);
}
