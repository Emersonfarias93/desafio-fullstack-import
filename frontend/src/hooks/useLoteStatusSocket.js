import { useEffect, useRef, useState } from 'react'
import { API_BASE } from '../services/api'

const reconnectDelayMs = 3000

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
    const socketUrl = `${API_BASE.replace(/^http/, 'ws')}/ws/lotes`

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
