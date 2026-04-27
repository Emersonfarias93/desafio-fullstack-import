import { LoteSelectorPanel } from '../components/LoteSelectorPanel'
import { LoteStatusPanel } from '../components/LoteStatusPanel'
import { MetricCard } from '../components/MetricCard'
import { formatDuration } from '../utils/formatters'

export function ProcessingPage({ lotes, currentLote, progress, onSelectLote }) {
  return (
    <div className="page-stack">
      <LoteSelectorPanel
        lotes={lotes}
        currentLote={currentLote}
        onSelectLote={onSelectLote}
        title="Lote em analise"
        description="Selecione o lote que voce quer acompanhar no processamento."
      />

      <section className="metrics-row" aria-label="Processamento multi-thread">
        <MetricCard label="Chunk configurado" value="2000" />
        <MetricCard label="Threads padrao" value="8" />
        <MetricCard label="Processadas" value={currentLote?.totalProcessadas ?? 0} />
        <MetricCard label="Chunks" value={`${currentLote?.chunksConcluidos ?? 0}/${currentLote?.totalChunks ?? 0}`} />
        <MetricCard label="Media/chunk" value={formatDuration(currentLote?.tempoMedioChunkMs)} />
        <MetricCard label="Tempo restante" value={formatDuration(currentLote?.tempoEstimadoRestanteMs)} />
      </section>

      <section className="panel">
        <div className="panel-heading">
          <div>
            <h2>Processamento multi-thread</h2>
            <p>Chunks independentes validam, deduplicam e persistem leads com controle por email unico.</p>
          </div>
        </div>
        <LoteStatusPanel lote={currentLote} progress={progress} />
      </section>
    </div>
  )
}
