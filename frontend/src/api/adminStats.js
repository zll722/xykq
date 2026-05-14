import request from '../utils/request';
import axios from 'axios';

export function listAdminAttendanceRecordsApi(params) {
  return request.get('/admin/attendance/records', { params });
}

export function attendanceRateApi(params) {
  return request.get('/admin/stats/attendance-rate', { params });
}

export function attendanceAbnormalApi(params) {
  return request.get('/admin/stats/attendance-abnormal', { params });
}

export function exportAttendanceUrl(params) {
  const usp = new URLSearchParams();
  Object.entries(params || {}).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') usp.set(k, v);
  });
  return `/api/v1/admin/stats/export/attendance?${usp.toString()}`;
}

export async function exportAttendanceApi(params) {
  const token = localStorage.getItem('token') || '';
  const url = exportAttendanceUrl(params);
  const response = await axios.get(url, {
    responseType: 'blob',
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  });
  return response.data;
}

export async function exportSummaryApi(params) {
  const token = localStorage.getItem('token') || '';
  const usp = new URLSearchParams();
  Object.entries(params || {}).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') usp.set(k, v);
  });
  const url = `/api/v1/admin/stats/export/summary?${usp.toString()}`;
  const response = await axios.get(url, {
    responseType: 'blob',
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  });
  return response.data;
}

export function listOperationLogsApi(params) {
  return request.get('/admin/system/logs', { params });
}
