import { LoteSelectorPanel } from '../components/LoteSelectorPanel'
import { LoteStatusPanel } from '../components/LoteStatusPanel'

const events = [
  ['lote.iniciado', 'Publicado apos o upload e pre-validacao do CSV.'],
  ['lote.chunk.concluido', 'Publicado por chunk com totais parciais de sucesso e erro.'],
  ['lote.finalizado', 'Consolida o lote e libera a notificacao final para o frontend.'],
  ['WebSocket /ws/lotes', 'Atualiza a barra de progresso em tempo real sem recarregar a tela.'],
]

export function MessagingPage({ lotes, currentLote, progress, onSelectLote }) {
  return (
    <div className="page-stack">
      <LoteSelectorPanel
        lotes={lotes}
        currentLote={currentLote}
        onSelectLote={onSelectLote}
        title="Lote monitorado"
        description="Escolha o lote para acompanhar os eventos e o status recebido."
      />

      <section className="two-column">
        <div className="panel">
          <div className="panel-heading">
            <div>
              <h2>Mensageria e notificacao</h2>
              <p>Eventos Kafka coordenam o processamento e o frontend acompanha pelo WebSocket.</p>
            </div>
          </div>
          <div className="event-list">
            {events.map(([name, description]) => (
              <div className="event-row" key={name}>
                <strong>{name}</strong>
                <span>{description}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="panel">
          <div className="panel-heading">
            <div>
              <h2>Status recebido</h2>
              <p>Payload mais recente do lote selecionado.</p>
            </div>
          </div>
          <LoteStatusPanel lote={currentLote} progress={progress} />
        </div>
      </section>
    </div>
  )
}
