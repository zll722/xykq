import request from '../utils/request';

export function listMySchedulesApi() {
  return request.get('/schedules/my');
}

export function signInApi(payload) {
  return request.post('/attendance/sign-in', payload);
}

export function listMyAttendanceRecordsApi() {
  return request.get('/attendance/records/my');
}

export function myOverviewApi() {
  return request.get('/stats/my-overview');
}

export function myCalendarApi(params) {
  return request.get('/stats/my-calendar', { params });
}
