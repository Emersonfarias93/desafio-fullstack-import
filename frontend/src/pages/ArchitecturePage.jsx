const decisions = [
  ['Docker Compose', 'Sobe frontend, backend, PostgreSQL e Kafka em um unico comando.'],
  ['Migrations', 'Flyway versiona as tabelas Lead, Lote, LoteItem e LoteProcessamento.'],
  ['Concorrencia', 'Chunks usam pool controlado para nao esgotar conexoes do banco.'],
  ['Deduplicacao', 'Email unico no PostgreSQL com insert idempotente.'],
]

export function ArchitecturePage() {
  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <h2>Docker, README e arquitetura</h2>
          <p>Resumo dos entregaveis tecnicos citados no desafio.</p>
        </div>
      </div>

      <div className="event-list">
        {decisions.map(([name, description]) => (
          <div className="event-row" key={name}>
            <strong>{name}</strong>
            <span>{description}</span>
          </div>
        ))}
      </div>
    </section>
  )
}
