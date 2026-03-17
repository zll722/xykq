import request from '../utils/request';

export function listMessagesApi() {
  return request.get('/messages');
}

export function readMessageApi(id) {
  return request.put(`/messages/${id}/read`);
}

export function readAllMessageApi() {
  return request.put('/messages/read-all');
}
