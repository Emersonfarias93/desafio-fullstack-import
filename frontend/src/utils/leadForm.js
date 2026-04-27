export const EMPTY_LEAD_FORM = {
  nome: '',
  email: '',
  telefone: '',
  origem: '',
  dataCadastro: '',
}

export function toLeadFormValues(lead) {
  if (!lead) {
    return { ...EMPTY_LEAD_FORM }
  }

  return {
    nome: lead.nome || '',
    email: lead.email || '',
    telefone: lead.telefone || '',
    origem: lead.origem || '',
    dataCadastro: toDateTimeLocalValue(lead.dataCadastro),
  }
}

export function toLeadPayload(values) {
  return {
    nome: values.nome.trim(),
    email: values.email.trim(),
    telefone: values.telefone.trim(),
    origem: values.origem.trim(),
    dataCadastro: values.dataCadastro ? values.dataCadastro : null,
  }
}

function toDateTimeLocalValue(value) {
  if (!value) {
    return ''
  }

  const date = new Date(value)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')

  return `${year}-${month}-${day}T${hours}:${minutes}`
}
