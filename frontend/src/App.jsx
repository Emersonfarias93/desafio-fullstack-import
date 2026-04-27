import { useState } from 'react'
import './App.css'
import { AppLayout } from './components/AppLayout'
import { isProcessingLote } from './constants/importStatus'
import { useImportWorkspace } from './hooks/useImportWorkspace'
import { ArchitecturePage } from './pages/ArchitecturePage'
import { AuditoriaPage } from './pages/AuditoriaPage'
import { DashboardPage } from './pages/DashboardPage'
import { ImportPage } from './pages/ImportPage'
import { LeadsPage } from './pages/LeadsPage'
import { MessagingPage } from './pages/MessagingPage'
import { ProcessingPage } from './pages/ProcessingPage'

function App() {
  const [activePage, setActivePage] = useState('dashboard')
  const workspace = useImportWorkspace()
  const processingLote = workspace.lotes.content.find(isProcessingLote) || null
  const importLote = processingLote
  const importProgress = importLote?.progressoPercentual || 0

  const selectLote = (lote) => {
    workspace.selectLote(lote)
  }

  const pages = {
    dashboard: (
      <DashboardPage
        dashboard={workspace.dashboard}
        lotes={workspace.lotes}
        currentLote={workspace.currentLote}
        progress={workspace.progress}
        taxaErro={workspace.taxaErro}
        onSelectLote={selectLote}
      />
    ),
    importacao: (
      <ImportPage
        currentLote={importLote}
        selectedFile={workspace.selectedFile}
        isDragging={workspace.isDragging}
        isProcessing={workspace.isProcessing}
        isImportLocked={workspace.isImportLocked}
        progress={importProgress}
        setSelectedFile={workspace.setSelectedFile}
        setIsDragging={workspace.setIsDragging}
        processFile={workspace.processFile}
      />
    ),
    auditoria: (
      <AuditoriaPage
        lotes={workspace.lotes}
        currentLote={workspace.currentLote}
        loteItems={workspace.loteItems}
        itemStatus={workspace.itemStatus}
        setItemStatus={workspace.setItemStatus}
        loadLoteItems={workspace.loadLoteItems}
        onSelectLote={selectLote}
      />
    ),
    leads: (
      <LeadsPage
        leads={workspace.leads}
        filters={workspace.filters}
        setFilters={workspace.setFilters}
        loadLeads={workspace.loadLeads}
        createLead={workspace.createLeadRecord}
        updateLead={workspace.updateLeadRecord}
        deleteLead={workspace.deleteLeadRecord}
      />
    ),
    processamento: (
      <ProcessingPage
        lotes={workspace.lotes}
        currentLote={workspace.currentLote}
        progress={workspace.progress}
        onSelectLote={selectLote}
      />
    ),
    mensageria: (
      <MessagingPage
        lotes={workspace.lotes}
        currentLote={workspace.currentLote}
        progress={workspace.progress}
        onSelectLote={selectLote}
      />
    ),
    arquitetura: <ArchitecturePage />,
  }

  return (
    <AppLayout
      activePage={activePage}
      onNavigate={setActivePage}
      onRefresh={() => workspace.refreshWorkspace().catch((exception) => workspace.setError(exception.message))}
      socketStatus={workspace.socketStatus}
      message={workspace.message}
      error={workspace.error}
      onDismissMessage={() => workspace.setMessage('')}
      onDismissError={() => workspace.setError('')}
    >
      {pages[activePage] || pages.dashboard}
    </AppLayout>
  )
}

export default App
