import { statusLabels } from '../constants/importStatus'
import { formatDuration } from '../utils/formatters'
import { StatTerm } from './StatTerm'

export function LoteStatusPanel({ lote, progress }) {
  if (!lote) {
    return (
      <div className="empty-state">
        <strong>Nenhum lote selecionado ou processado</strong>
        <span>Envie um CSV ou escolha um lote no historico para acompanhar o status.</span>
      </div>
    )
  }

  return (
    <div className="status-block">
      <div className="status-header">
        <div>
          <span className={`status-pill ${lote.status?.toLowerCase()}`}>{statusLabels[lote.status] || lote.status}</span>
          <h3>{lote.nomeArquivo}</h3>
        </div>
        <strong>{progress}%</strong>
      </div>
      <div className="progress-track">
        <span style={{ width: `${progress}%` }} />
      </div>
      <dl className="status-grid">
        <StatTerm label="Linhas" value={lote.totalLinhas} />
        <StatTerm label="Processadas" value={lote.totalProcessadas} />
        <StatTerm label="Validas" value={lote.totalValidas} />
        <StatTerm label="Importadas" value={lote.totalImportadas} />
        <StatTerm label="Duplicadas" value={lote.totalDuplicadas} />
        <StatTerm label="Invalidas" value={lote.totalInvalidas} />
        <StatTerm label="Erros" value={lote.totalErros} />
        <StatTerm label="Chunks" value={`${lote.chunksConcluidos ?? 0}/${lote.totalChunks ?? 0}`} />
        <StatTerm label="Media/chunk" value={formatDuration(lote.tempoMedioChunkMs)} />
        <StatTerm label="Tempo restante" value={formatDuration(lote.tempoEstimadoRestanteMs)} />
      </dl>
    </div>
  )
}
