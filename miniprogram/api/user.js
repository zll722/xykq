const { get, put } = require('../utils/request');

const getMyProfile = () => get('/users/me');
const updateMyProfile = (data) => put('/users/me', data);

module.exports = { getMyProfile, updateMyProfile };
