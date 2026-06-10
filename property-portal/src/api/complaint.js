import request from '../utils/request'

export function getMyComplaints(params) {
  return request.get('/api/owner/complaints', { params })
}

export function submitComplaint(data) {
  return request.post('/api/owner/complaints', data)
}
