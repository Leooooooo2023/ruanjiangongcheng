import request from '../utils/request'

export function getRepairs(params) {
  return request.get('/api/repairs', { params })
}

export function getRepair(id) {
  return request.get(`/api/repairs/${id}`)
}

export function assignRepair(id, data) {
  return request.put(`/api/repairs/${id}/assign`, data)
}

export function updateRepairStatus(id, data) {
  return request.put(`/api/repairs/${id}/status`, data)
}
