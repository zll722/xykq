const { post, get } = require('../utils/request');

const login = (data) => post('/auth/login', data);
const getMe = () => get('/auth/me');
const getPermissions = () => get('/auth/permissions');
const changePassword = (data) => post('/auth/change-password', data);

module.exports = { login, getMe, getPermissions, changePassword };
