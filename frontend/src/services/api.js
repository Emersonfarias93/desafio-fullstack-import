export const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, options)

  if (!response.ok) {
    let detail = 'Nao foi possivel completar a solicitacao.'

    try {
      const body = await response.json()
      detail = body.message || detail
    } catch {
      detail = await response.text()
    }

    throw new Error(detail)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

export function getDashboard() {
  return apiRequest('/api/dashboard')
}

export function getLotes({ page = 0, size = 6 } = {}) {
  return apiRequest(`/api/lotes?page=${page}&size=${size}`)
}

export function getLeads({ filters = {}, page = 0, size = 10 } = {}) {
  const params = new URLSearchParams({ page: String(page), size: String(size), sort: 'criadoEm,desc' })

  Object.entries(filters).forEach(([key, value]) => {
    if (value.trim()) {
      params.set(key, value.trim())
    }
  })

  return apiRequest(`/api/leads?${params}`)
}

export function createLead(payload) {
  return apiRequest('/api/leads', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function updateLead(id, payload) {
  return apiRequest(`/api/leads/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function deleteLead(id) {
  return apiRequest(`/api/leads/${id}`, {
    method: 'DELETE',
  })
}

export function getLoteItems({ loteId, status = '', page = 0, size = 8 }) {
  if (!loteId) {
    return Promise.resolve(null)
  }

  const params = new URLSearchParams({ page: String(page), size: String(size), sort: 'linhaCsv,asc' })
  if (status) {
    params.set('status', status)
  }

  return apiRequest(`/api/lotes/${loteId}/itens?${params}`)
}

export function getLoteStatus(loteId) {
  return apiRequest(`/api/lotes/${loteId}/status`)
}

export function uploadLote(file) {
  const form = new FormData()
  form.append('arquivo', file)

  return apiRequest('/api/lotes', { method: 'POST', body: form })
}
