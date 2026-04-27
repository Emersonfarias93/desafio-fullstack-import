import { LoteStatusPanel } from '../components/LoteStatusPanel'
import { MetricCard } from '../components/MetricCard'
import { statusLabels } from '../constants/importStatus'

export function DashboardPage({ dashboard, lotes, currentLote, progress, taxaErro, onSelectLote }) {
  const recentLotes = lotes.content.slice(0, 5)

  return (
    <div className="page-stack">
      <section className="metrics-row" aria-label="Resumo da importacao">
        <MetricCard label="Total Importadas de Leads" value={dashboard?.totalLeads ?? 0} />
        <MetricCard label="Total Lotes" value={dashboard?.totalLotes ?? 0} />
        <MetricCard label="Total Processando" value={dashboard?.lotesProcessando ?? 0} />
        <MetricCard label="Taxa de erro" value={taxaErro} />
      </section>

      <section className="two-column">
        <div className="panel">
          <div className="panel-heading">
            <div>
              <h2>Status em tempo real</h2>
              <p>Progresso consolidado pelo consumer e enviado por WebSocket.</p>
            </div>
          </div>
          <LoteStatusPanel lote={currentLote} progress={progress} />
        </div>

        <div className="panel">
          <div className="panel-heading compact">
            <div>
              <h2>Ultimos lotes</h2>
              <p>Selecione um lote para inspecionar detalhes.</p>
            </div>
          </div>
          <div className="lote-list">
            {recentLotes.length === 0 && <p className="empty">Nenhum lote enviado.</p>}
            {recentLotes.map((lote) => (
              <button
                key={lote.id}
                className={`lote-row ${currentLote?.id === lote.id ? 'selected' : ''}`}
                type="button"
                onClick={() => onSelectLote(lote)}
              >
                <span>
                  <strong>{lote.nomeArquivo}</strong>
                  <small>{statusLabels[lote.status] || lote.status}</small>
                </span>
                <b>{lote.progressoPercentual || 0}%</b>
              </button>
            ))}
          </div>
        </div>
      </section>
    </div>
  )
}
