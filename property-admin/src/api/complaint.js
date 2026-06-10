import request from '../utils/request'

export function getComplaints(params) {
  return request.get('/api/complaints', { params })
}

export function getComplaint(id) {
  return request.get(`/api/complaints/${id}`)
}

export function replyComplaint(id, data) {
  return request.put(`/api/complaints/${id}/reply`, data)
}
