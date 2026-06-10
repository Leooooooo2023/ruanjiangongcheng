import request from '../utils/request'

/**
 * 获取所有停车位（地图展示用，业主端）
 */
export function getAllParkings() {
  return request.get('/api/owner/parkings', { params: { page: 1, size: 100 } })
}

/**
 * 检查某个车位是否可申请（含车位详情+能否申请的判断）
 */
export function checkParking(id) {
  return request.get(`/api/owner/parkings/${id}/check`)
}

/**
 * 业主提交停车位申请
 */
export function applyParking(parkingId) {
  return request.post('/api/owner/parking-applications', { parkingId })
}

/**
 * 业主查看自己的申请列表
 */
export function getMyApplications() {
  return request.get('/api/owner/parking-applications')
}

/**
 * 业主查看自己的车位
 */
export function getMyParking() {
  return request.get('/api/owner/parking')
}
