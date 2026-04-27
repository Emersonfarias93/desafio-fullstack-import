export const statusLabels = {
  RECEBIDO: 'Recebido',
  VALIDANDO: 'Validando',
  PROCESSANDO: 'Processando',
  FINALIZADO: 'Finalizado',
  FINALIZADO_COM_ERRO: 'Finalizado com erro',
  CANCELADO: 'Cancelado',
}

export const itemStatusLabels = {
  INVALIDO: 'Invalido',
  DUPLICADO_EXATO: 'Duplicado',
  POSSIVEL_DUPLICADO: 'Possivel duplicado',
  IMPORTADO: 'Importado',
  ERRO: 'Erro',
}

export const itemStatusOptions = [
  '',
  'INVALIDO',
  'DUPLICADO_EXATO',
  'IMPORTADO',
  'ERRO',
]

export const finalLoteStatuses = new Set(['FINALIZADO', 'FINALIZADO_COM_ERRO', 'CANCELADO'])

export function isProcessingLote(lote) {
  return lote?.status === 'PROCESSANDO'
}

export function isFinishedLote(lote) {
  return lote ? finalLoteStatuses.has(lote.status) : false
}
