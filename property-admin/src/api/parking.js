import request from '../utils/request'

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
