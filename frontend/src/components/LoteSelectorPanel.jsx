import { statusLabels } from '../constants/importStatus'
import { formatDate } from '../utils/formatters'

export function LoteSelectorPanel({ lotes, currentLote, onSelectLote, title = 'Selecionar lote', description = 'Escolha o lote para visualizar nesta tela.' }) {
  const hasLotes = lotes.content.length > 0

  const handleChange = (event) => {
    const lote = lotes.content.find((item) => item.id === event.target.value)
    if (lote) {
      onSelectLote(lote)
    }
  }

  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <h2>{title}</h2>
          <p>{description}</p>
        </div>
      </div>

      {!hasLotes ? (
        <p className="empty">Nenhum lote enviado.</p>
      ) : (
        <div className="lote-selector-stack">
          <select value={currentLote?.id || ''} onChange={handleChange}>
            <option value="" disabled>Selecione um lote</option>
            {lotes.content.map((lote) => (
            <option key={lote.id} value={lote.id}>
                {lote.nomeArquivo} - {formatDate(lote.criadoEm)} - {statusLabels[lote.status] || lote.status}
            </option>
          ))}
        </select>

          {currentLote ? (
            <div className="lote-summary">
              <div>
                <strong>{currentLote.nomeArquivo}</strong>
                <small>{formatDate(currentLote.criadoEm)} - {statusLabels[currentLote.status] || currentLote.status}</small>
              </div>
            </div>
          ) : null}
        </div>
      )}
    </section>
  )
}
