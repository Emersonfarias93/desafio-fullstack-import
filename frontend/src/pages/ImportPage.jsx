import { LoteStatusPanel } from '../components/LoteStatusPanel'

export function ImportPage({
  currentLote,
  selectedFile,
  isDragging,
  isProcessing,
  isImportLocked,
  progress,
  setSelectedFile,
  setIsDragging,
  processFile,
}) {
  return (
    <section className="two-column">
      <div className="panel">
        <div className="panel-heading">
          <div>
            <h2>Upload e parsing do CSV</h2>
            <p>Envio multipart para /api/lotes com validacao de formato, cabecalho e UTF-8.</p>
          </div>
        </div>

        <label
          className={`dropzone ${isDragging ? 'dragging' : ''}`}
          onDragOver={(event) => {
            event.preventDefault()
            if (isImportLocked) return
            setIsDragging(true)
          }}
          onDragLeave={() => setIsDragging(false)}
          onDrop={(event) => {
            event.preventDefault()
            setIsDragging(false)
            if (isImportLocked) return
            setSelectedFile(event.dataTransfer.files?.[0] || null)
          }}
        >
          <input
            type="file"
            accept=".csv,text/csv"
            disabled={isImportLocked}
            onChange={(event) => setSelectedFile(event.target.files?.[0] || null)}
          />
          <strong>{isProcessing ? 'Processamento em andamento' : selectedFile?.name || 'Selecionar CSV'}</strong>
          <span>{isProcessing ? 'Aguarde a conclusao para enviar outro arquivo' : selectedFile ? `${(selectedFile.size / 1024).toFixed(1)} KB` : 'Arraste o arquivo para ca'}</span>
        </label>

        <div className="actions">
          <button type="button" onClick={processFile} disabled={!selectedFile || isImportLocked}>
            {isProcessing ? 'Processando...' : 'Processar CSV'}
          </button>
        </div>
      </div>

      <div className="panel">
        <div className="panel-heading">
          <div>
            <h2>Retorno do lote</h2>
            <p>O processamento inicia automaticamente apos a pre-validacao.</p>
          </div>
        </div>
        <LoteStatusPanel lote={currentLote} progress={progress} />
      </div>
    </section>
  )
}
