import request from '../utils/request'

// 分页查询
export function getBuildings(params) {
  return request.get('/api/buildings', { params })
}

// 详情
export function getBuilding(id) {
  return request.get(`/api/buildings/${id}`)
}

// 新增
export function addBuilding(data) {
  return request.post('/api/buildings', data)
}

// 修改
export function updateBuilding(id, data) {
  return request.put(`/api/buildings/${id}`, data)
}

// 删除
export function deleteBuilding(id) {
  return request.delete(`/api/buildings/${id}`)
}

// 获取全部楼栋（不分页，供下拉选择）
export function getAllBuildings() {
  return request.get('/api/buildings/all')
}
