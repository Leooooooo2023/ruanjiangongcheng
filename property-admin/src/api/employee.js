import request from '../utils/request'

export function getEmployees(params) {
  return request.get('/api/employees', { params })
}

export function getEmployee(id) {
  return request.get(`/api/employees/${id}`)
}

export function addEmployee(data) {
  return request.post('/api/employees', data)
}

export function updateEmployee(id, data) {
  return request.put(`/api/employees/${id}`, data)
}

export function deleteEmployee(id) {
  return request.delete(`/api/employees/${id}`)
}
