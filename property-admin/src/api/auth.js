import request from '../utils/request'

// 获取图形验证码
export function getCaptcha() {
  return request.get('/api/auth/captcha')
}

// 登录
export function login(data) {
  return request.post('/api/auth/login', data)
}

// 退出
export function logout() {
  return request.post('/api/auth/logout')
}

// 修改密码
export function updatePassword(data) {
  return request.put('/api/auth/password', data)
}

// 获取当前用户信息
export function getUserInfo() {
  return request.get('/api/auth/info')
}
