const { get, put } = require('../utils/request');

const listMessages = () => get('/messages');
const readMessage = (id) => put(`/messages/${id}/read`);
const readAllMessages = () => put('/messages/read-all');

module.exports = { listMessages, readMessage, readAllMessages };
