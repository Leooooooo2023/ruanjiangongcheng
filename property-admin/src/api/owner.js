import request from '../utils/request'

export function getOwners(params) {
  return request.get('/api/owners', { params })
}

export function getOwner(id) {
  return request.get(`/api/owners/${id}`)
}

export function addOwner(data) {
  return request.post('/api/owners', data)
}

export function updateOwner(id, data) {
  return request.put(`/api/owners/${id}`, data)
}

export function deleteOwner(id) {
  return request.delete(`/api/owners/${id}`)
}
