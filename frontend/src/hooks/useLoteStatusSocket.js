import { useEffect, useRef, useState } from 'react'
import { API_BASE } from '../services/api'

const reconnectDelayMs = 3000

function buildSocketUrl() {
  if (!API_BASE) {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    return `${protocol}//${window.location.host}/ws/lotes`
  }

  if (API_BASE.startsWith('http://') || API_BASE.startsWith('https://')) {
    return `${API_BASE.replace(/^http/, 'ws')}/ws/lotes`
  }

  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  return `${protocol}//${window.location.host}${API_BASE}/ws/lotes`
}

export function useLoteStatusSocket({ onStatus }) {
  const [socketStatus, setSocketStatus] = useState('connecting')
  const onStatusRef = useRef(onStatus)

  useEffect(() => {
    onStatusRef.current = onStatus
  }, [onStatus])

  useEffect(() => {
    let socket = null
    let reconnectTimer = null
    let shouldReconnect = true
    const socketUrl = buildSocketUrl()

    function connect() {
      setSocketStatus('connecting')
      socket = new WebSocket(socketUrl)

      socket.onopen = () => {
        setSocketStatus('connected')
      }

      socket.onmessage = (event) => {
        try {
          const payload = JSON.parse(event.data)

          if (payload.type === 'lote.status') {
            onStatusRef.current(payload.lote)
          }
        } catch {
          // Mensagens desconhecidas do socket sao ignoradas.
        }
      }

      socket.onerror = () => {
        setSocketStatus('error')
      }

      socket.onclose = () => {
        if (!shouldReconnect) {
          setSocketStatus('closed')
          return
        }

        setSocketStatus('reconnecting')
        reconnectTimer = window.setTimeout(connect, reconnectDelayMs)
      }
    }

    connect()

    return () => {
      shouldReconnect = false
      window.clearTimeout(reconnectTimer)

      if (socket) {
        socket.close()
      }
    }
  }, [])

  return socketStatus
}
