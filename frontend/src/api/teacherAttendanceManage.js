import request from '../utils/request';

export function listTeacherAttendanceRecordsApi(params) {
  return request.get('/teacher/attendance/records', { params });
}

export function listTeacherAttendanceExceptionsApi(params) {
  return request.get('/teacher/attendance/exceptions', { params });
}

export function adjustTeacherAttendanceRecordApi(id, payload) {
  return request.post(`/teacher/attendance/records/${id}/adjust`, payload);
}

export function resolveTeacherAttendanceExceptionApi(id, payload) {
  return request.put(`/teacher/attendance/exceptions/${id}/resolve`, payload);
}

export function listTeacherClassesApi() {
  return request.get('/teacher/classes');
}

export function listTeacherCoursesApi() {
  return request.get('/teacher/courses');
}
