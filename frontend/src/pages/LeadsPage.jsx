import { useState } from 'react'
import { Dialog } from '../components/Dialog'
import { Pagination } from '../components/Pagination'
import { formatDate } from '../utils/formatters'
import { EMPTY_LEAD_FORM, toLeadFormValues, toLeadPayload } from '../utils/leadForm'

export function LeadsPage({ leads, filters, setFilters, loadLeads, createLead, updateLead, deleteLead }) {
  const [dialogMode, setDialogMode] = useState(null)
  const [selectedLead, setSelectedLead] = useState(null)
  const [formValues, setFormValues] = useState(EMPTY_LEAD_FORM)
  const [submitting, setSubmitting] = useState(false)
  const [searchField, setSearchField] = useState('nome')

  const currentPage = leads.page?.number || 0
  const dialogOpen = dialogMode !== null

  const openCreateDialog = () => {
    setDialogMode('create')
    setSelectedLead(null)
    setFormValues(EMPTY_LEAD_FORM)
  }

  const openEditDialog = (lead) => {
    setDialogMode('edit')
    setSelectedLead(lead)
    setFormValues(toLeadFormValues(lead))
  }

  const openDeleteDialog = (lead) => {
    setDialogMode('delete')
    setSelectedLead(lead)
    setFormValues(toLeadFormValues(lead))
  }

  const closeDialog = () => {
    setDialogMode(null)
    setSelectedLead(null)
    setFormValues(EMPTY_LEAD_FORM)
  }

  const handleCreate = async (event) => {
    event.preventDefault()
    setSubmitting(true)

    try {
      await createLead(toLeadPayload(formValues))
      closeDialog()
    } finally {
      setSubmitting(false)
    }
  }

  const handleUpdate = async (event) => {
    event.preventDefault()

    if (!selectedLead) {
      return
    }

    setSubmitting(true)

    try {
      await updateLead(selectedLead.id, toLeadPayload(formValues), currentPage)
      closeDialog()
    } finally {
      setSubmitting(false)
    }
  }

  const handleDelete = async () => {
    if (!selectedLead) {
      return
    }

    const targetPage = leads.content.length === 1 && currentPage > 0 ? currentPage - 1 : currentPage
    setSubmitting(true)

    try {
      await deleteLead(selectedLead.id, targetPage)
      closeDialog()
    } finally {
      setSubmitting(false)
    }
  }

  const dialogTitle = dialogMode === 'create'
    ? 'Cadastro Lead Novo'
    : dialogMode === 'delete'
      ? 'Excluir Cadastro'
      : 'Atualizar Dados no Cadastro'

  const dialogDescription = dialogMode === 'create'
    ? 'Preencha os dados para adicionar um novo registro manualmente.'
    : dialogMode === 'delete'
      ? 'Essa exclusao remove o lead da base principal.'
      : 'Atualize os campos abaixo ou remova o lead, se necessario.'

  const searchValue = filters[searchField] || ''

  const handleSearchFieldChange = (field) => {
    setSearchField(field)
    setFilters({ nome: '', email: '', origem: '' })
  }

  const handleSearchValueChange = (value) => {
    setFilters({
      nome: '',
      email: '',
      origem: '',
      [searchField]: value,
    })
  }

  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <h2>Persistencia e consulta de leads</h2>
          <p>{leads.page?.totalElements ?? 0} registros disponiveis com filtro por nome, email e origem.</p>
        </div>
        <button type="button" onClick={openCreateDialog}>Cadastrar Manual</button>
      </div>

      <form
        className="filters"
        onSubmit={(event) => {
          event.preventDefault()
          loadLeads(0)
        }}
      >
        <select value={searchField} onChange={(event) => handleSearchFieldChange(event.target.value)}>
          <option value="nome">Buscar por nome</option>
          <option value="email">Buscar por email</option>
          <option value="origem">Buscar por origem</option>
        </select>
        <input
          placeholder={searchField === 'nome' ? 'Digite o nome' : searchField === 'email' ? 'Digite o email' : 'Digite a origem'}
          value={searchValue}
          onChange={(event) => handleSearchValueChange(event.target.value)}
        />
        <button type="submit">Buscar</button>
      </form>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Nome</th>
              <th>Email</th>
              <th>Telefone</th>
              <th>Origem</th>
              <th>Cadastro</th>
              <th>Ação</th>
            </tr>
          </thead>
          <tbody>
            {leads.content.length === 0 && (
              <tr>
                <td colSpan={6} className="empty">Nenhum lead importado ainda.</td>
              </tr>
            )}
            {leads.content.map((lead) => (
              <tr key={lead.id}>
                <td>{lead.nome}</td>
                <td>{lead.email}</td>
                <td>{lead.telefone || '-'}</td>
                <td>{lead.origem || '-'}</td>
                <td>{formatDate(lead.dataCadastro)}</td>
                <td>
                  <div className="table-actions">
                    <button type="button" className="ghost-button" onClick={() => openEditDialog(lead)} disabled={submitting}>
                      Alterar/Excluir
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        page={currentPage}
        totalPages={leads.page?.totalPages || 0}
        onChange={(page) => loadLeads(page)}
      />

      <Dialog open={dialogOpen} title={dialogTitle} description={dialogDescription} onClose={() => {
        if (!submitting) {
          closeDialog()
        }
      }}>
        {(dialogMode === 'create' || dialogMode === 'edit') && (
          <form className="dialog-form" onSubmit={dialogMode === 'create' ? handleCreate : handleUpdate}>
            <div className="dialog-grid">
              <label>
                <span>Nome</span>
                <input
                  placeholder="Nome"
                  value={formValues.nome}
                  onChange={(event) => setFormValues({ ...formValues, nome: event.target.value })}
                  disabled={submitting}
                  required
                />
              </label>
              <label>
                <span>Email</span>
                <input
                  placeholder="Email"
                  type="email"
                  value={formValues.email}
                  onChange={(event) => setFormValues({ ...formValues, email: event.target.value })}
                  disabled={submitting}
                  required
                />
              </label>
              <label>
                <span>Telefone</span>
                <input
                  placeholder="Telefone"
                  value={formValues.telefone}
                  onChange={(event) => setFormValues({ ...formValues, telefone: event.target.value })}
                  disabled={submitting}
                />
              </label>
              <label>
                <span>Origem</span>
                <input
                  placeholder="Origem"
                  value={formValues.origem}
                  onChange={(event) => setFormValues({ ...formValues, origem: event.target.value })}
                  disabled={submitting}
                />
              </label>
              <label>
                <span>Data de cadastro</span>
                <input
                  type="datetime-local"
                  value={formValues.dataCadastro}
                  onChange={(event) => setFormValues({ ...formValues, dataCadastro: event.target.value })}
                  disabled={submitting}
                />
              </label>
            </div>
            <div className="dialog-actions">
              {dialogMode === 'edit' ? (
                <button type="button" className="ghost-button danger-button" onClick={() => openDeleteDialog(selectedLead)} disabled={submitting}>
                  Excluir
                </button>
              ) : <span />}
              <div className="dialog-actions-right">
                <button type="button" className="ghost-button" onClick={closeDialog} disabled={submitting}>
                  Cancelar
                </button>
                <button type="submit" disabled={submitting} className="ghost-button">
                  {dialogMode === 'create' ? 'Salvar' : 'Alterar'}
                </button>
              </div>
            </div>
          </form>
        )}

        {dialogMode === 'delete' && selectedLead ? (
          <div className="dialog-delete">
            <div className="dialog-delete-card">
              <strong>{selectedLead.nome}</strong>
              <span>{selectedLead.email}</span>
            </div>
            <p>Essa operacao nao pode ser desfeita.</p>
            <div className="dialog-actions">
              <span />
              <div className="dialog-actions-right">
                <button type="button" className="ghost-button" onClick={() => openEditDialog(selectedLead)} disabled={submitting}>
                  Voltar
                </button>
                <button type="button" className="ghost-button danger-button" onClick={handleDelete} disabled={submitting}>
                  Confirmar
                </button>
              </div>
            </div>
          </div>
        ) : null}
      </Dialog>
    </section>
  )
}
