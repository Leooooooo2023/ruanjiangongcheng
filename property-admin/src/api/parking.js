import request from '../utils/request'

// ====== 停车位管理 ======

export function getParkings(params) {
  return request.get('/api/parkings', { params })
}

export function getParking(id) {
  return request.get(`/api/parkings/${id}`)
}

export function addParking(data) {
  return request.post('/api/parkings', data)
}

export function updateParking(id, data) {
  return request.put(`/api/parkings/${id}`, data)
}

export function deleteParking(id) {
  return request.delete(`/api/parkings/${id}`)
}

export function assignParking(id, data) {
  return request.put(`/api/parkings/${id}/assign`, data)
}

export function revokeParking(id) {
  return request.put(`/api/parkings/${id}/revoke`)
}

// ====== 停车位申请（管理员） ======

/**
 * 获取所有申请列表
 */
export function getParkingApplications() {
  return request.get('/api/parking-applications')
}

/**
 * 审核通过
 */
export function approveApplication(id) {
  return request.put(`/api/parking-applications/${id}/approve`)
}

/**
 * 审核拒绝
 */
export function rejectApplication(id) {
  return request.put(`/api/parking-applications/${id}/reject`)
}
