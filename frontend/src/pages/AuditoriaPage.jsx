import { DataTable } from '../components/DataTable'
import { LoteSelectorPanel } from '../components/LoteSelectorPanel'
import { Pagination } from '../components/Pagination'
import { itemStatusLabels, itemStatusOptions } from '../constants/importStatus'

export function AuditoriaPage({ lotes, currentLote, loteItems, itemStatus, setItemStatus, loadLoteItems, onSelectLote }) {
  const loadedItems = loteItems.content.length
  const totalItems = loteItems.page?.totalElements ?? loadedItems

  return (
    <div className="page-stack">
      <LoteSelectorPanel
        lotes={lotes}
        currentLote={currentLote}
        onSelectLote={onSelectLote}
        title="Lote auditado"
        description="Escolha o lote que voce quer inspecionar linha a linha."
      />

      <section className="panel">
        <div className="panel-heading compact">
          <div>
            <h2>Auditoria das linhas</h2>
            <p>{currentLote ? currentLote.nomeArquivo : 'Selecione um lote para consultar as linhas.'}</p>
          </div>
          <div className="auditoria-filter">
            <span>Filtrar por status</span>
            <select value={itemStatus} onChange={(event) => setItemStatus(event.target.value)} disabled={!currentLote}>
              {itemStatusOptions.map((status) => (
                <option key={status || 'ALL'} value={status}>
                  {status ? itemStatusLabels[status] : 'Todos'}
                </option>
              ))}
            </select>
            <small>{loadedItems} exibidos de {totalItems}</small>
          </div>
        </div>

        <DataTable
          columns={['Linha', 'Nome','Status', 'Email', 'Motivo']}
          empty="Nenhuma linha para exibir."
          rows={loteItems.content.map((item) => [
            item.linhaCsv,
            item.nome,
            itemStatusLabels[item.status] || item.status,
            item.email || '-',
            item.motivo || '-',
          ])}
        />

        <Pagination
          page={loteItems.page?.number || 0}
          totalPages={loteItems.page?.totalPages || 0}
          onChange={(page) => loadLoteItems(currentLote?.id, itemStatus, page)}
        />
      </section>
    </div>
  )
}
