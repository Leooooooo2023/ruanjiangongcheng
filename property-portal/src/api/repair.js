import request from '../utils/request'

export function getMyRepairs(params) {
  return request.get('/api/owner/repairs', { params })
}

export function submitRepair(data) {
  return request.post('/api/owner/repairs', data)
}

export function rateRepair(id, data) {
  return request.put(`/api/owner/repairs/${id}/rate`, data)
}
