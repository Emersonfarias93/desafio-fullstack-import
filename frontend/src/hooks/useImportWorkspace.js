/* eslint-disable react-hooks/set-state-in-effect */
import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { isFinishedLote, isProcessingLote } from '../constants/importStatus'
import { createLead, deleteLead, getDashboard, getLeads, getLoteItems, getLotes, getLoteStatus, updateLead, uploadLote } from '../services/api'
import { useLoteStatusSocket } from './useLoteStatusSocket'

const emptyPage = { content: [], page: { number: 0, totalPages: 0, totalElements: 0 } }

export function useImportWorkspace() {
  const [dashboard, setDashboard] = useState(null)
  const [leads, setLeads] = useState(emptyPage)
  const [lotes, setLotes] = useState({ content: [] })
  const [loteItems, setLoteItems] = useState({ content: [], page: { number: 0, totalPages: 0 } })
  const [currentLote, setCurrentLote] = useState(null)
  const [selectedFile, setSelectedFile] = useState(null)
  const [isDragging, setIsDragging] = useState(false)
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [filters, setFilters] = useState({ nome: '', email: '', origem: '' })
  const [itemStatus, setItemStatus] = useState('')
  const currentLoteIdRef = useRef(null)
  const lastSocketStatusAtRef = useRef(0)

  const loadDashboard = useCallback(async () => {
    const data = await getDashboard()
    setDashboard(data)
  }, [])

  const loadLotes = useCallback(async () => {
    const data = await getLotes({ page: 0, size: 8 })
    setLotes(data)

    if (!data.content.length) {
      currentLoteIdRef.current = null
      setCurrentLote(null)
      return
    }

    if (!currentLoteIdRef.current) {
      const lotePreferencial = data.content.find(isProcessingLote) || data.content[0]
      currentLoteIdRef.current = lotePreferencial.id
      setCurrentLote(lotePreferencial)
      return
    }

    const loteAtualizado = data.content.find((lote) => lote.id === currentLoteIdRef.current)
    if (loteAtualizado) {
      setCurrentLote(loteAtualizado)
    } else {
      currentLoteIdRef.current = data.content[0].id
      setCurrentLote(data.content[0])
    }
  }, [])

  const loadLeads = useCallback(async (page = 0) => {
    const data = await getLeads({ filters, page, size: 10 })
    setLeads(data)
  }, [filters])

  const loadLoteItems = useCallback(async (loteId = currentLoteIdRef.current, status = itemStatus, page = 0) => {
    const data = await getLoteItems({ loteId, status, page, size: 10 })
    if (data) {
      setLoteItems(data)
    }
  }, [itemStatus])

  const refreshWorkspace = useCallback(() => Promise.all([
    loadDashboard(),
    loadLeads(),
    loadLotes(),
  ]), [loadDashboard, loadLeads, loadLotes])

  const selectLote = useCallback((lote) => {
    currentLoteIdRef.current = lote.id
    setCurrentLote(lote)
    loadLoteItems(lote.id).catch((exception) => setError(exception.message))
  }, [loadLoteItems])

  const handleSocketStatus = useCallback((lote) => {
    const shouldTrackLote = !currentLoteIdRef.current || lote.id === currentLoteIdRef.current

    if (shouldTrackLote) {
      currentLoteIdRef.current = lote.id
      lastSocketStatusAtRef.current = Date.now()
      setCurrentLote(lote)
    }

    if (isFinishedLote(lote)) {
      if (shouldTrackLote) {
        loadLoteItems(lote.id).catch(() => {})
      }

      loadDashboard().catch(() => {})
      loadLotes().catch(() => {})
      loadLeads().catch(() => {})
    }
  }, [loadDashboard, loadLeads, loadLoteItems, loadLotes])

  const socketStatus = useLoteStatusSocket({ onStatus: handleSocketStatus })

  useEffect(() => {
    refreshWorkspace().catch((exception) => setError(exception.message))
  }, [refreshWorkspace])

  useEffect(() => {
    currentLoteIdRef.current = currentLote?.id || null
  }, [currentLote])

  useEffect(() => {
    if (!isProcessingLote(currentLote)) {
      return undefined
    }

    const timer = window.setInterval(async () => {
      if (Date.now() - lastSocketStatusAtRef.current < 12000) {
        return
      }

      try {
        const lote = await getLoteStatus(currentLote.id)
        setCurrentLote(lote)
      } catch {
        //
      }
    }, 10000)

    return () => window.clearInterval(timer)
  }, [currentLote])

  useEffect(() => {
    loadLoteItems(currentLote?.id, itemStatus).catch((exception) => setError(exception.message))
  }, [currentLote?.id, itemStatus, loadLoteItems])

  const processFile = useCallback(async () => {
    if (!selectedFile) {
      setError('Selecione um CSV para enviar.')
      return
    }

    setLoading(true)
    setError('')
    setMessage('')

    try {
      const lote = await uploadLote(selectedFile)
      currentLoteIdRef.current = lote.id
      lastSocketStatusAtRef.current = Date.now()
      setCurrentLote(lote)
      setMessage('Processamento iniciado. O progresso sera atualizado automaticamente.')
      await Promise.all([loadDashboard(), loadLotes(), loadLoteItems(lote.id, itemStatus)])
    } catch (exception) {
      setError(exception.message)
    } finally {
      setLoading(false)
    }
  }, [itemStatus, loadDashboard, loadLoteItems, loadLotes, selectedFile])

  const createLeadRecord = useCallback(async (payload) => {
    setError('')
    setMessage('')

    try {
      const lead = await createLead(payload)
      await Promise.all([loadDashboard(), loadLeads(0)])
      setMessage('Lead criado com sucesso.')
      return lead
    } catch (exception) {
      setError(exception.message)
      throw exception
    }
  }, [loadDashboard, loadLeads])

  const updateLeadRecord = useCallback(async (id, payload, page = leads.page?.number || 0) => {
    setError('')
    setMessage('')

    try {
      const lead = await updateLead(id, payload)
      await Promise.all([loadDashboard(), loadLeads(page)])
      setMessage('Lead atualizado com sucesso.')
      return lead
    } catch (exception) {
      setError(exception.message)
      throw exception
    }
  }, [leads.page?.number, loadDashboard, loadLeads])

  const deleteLeadRecord = useCallback(async (id, page = leads.page?.number || 0) => {
    setError('')
    setMessage('')

    try {
      await deleteLead(id)
      await Promise.all([loadDashboard(), loadLeads(page)])
      setMessage('Lead removido com sucesso.')
    } catch (exception) {
      setError(exception.message)
      throw exception
    }
  }, [leads.page?.number, loadDashboard, loadLeads])

  const progress = currentLote?.progressoPercentual || 0
  const isProcessing = isProcessingLote(currentLote)
  const isImportLocked = loading || isProcessing
  const taxaErro = useMemo(() => {
    if (!dashboard) {
      return '0,0%'
    }

    return `${dashboard.taxaErro.toFixed(1).replace('.', ',')}%`
  }, [dashboard])

  return {
    dashboard,
    leads,
    lotes,
    loteItems,
    currentLote,
    selectedFile,
    isDragging,
    loading,
    message,
    error,
    filters,
    itemStatus,
    progress,
    isProcessing,
    isImportLocked,
    socketStatus,
    taxaErro,
    setCurrentLote,
    setSelectedFile,
    setIsDragging,
    setFilters,
    setItemStatus,
    setMessage,
    setError,
    loadDashboard,
    loadLotes,
    loadLeads,
    loadLoteItems,
    refreshWorkspace,
    selectLote,
    processFile,
    createLeadRecord,
    updateLeadRecord,
    deleteLeadRecord,
  }
}
