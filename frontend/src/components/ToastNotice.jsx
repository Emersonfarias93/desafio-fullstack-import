import { useEffect } from 'react'

export function ToastNotice({ message, type = 'success', onClose }) {
  useEffect(() => {
    if (!message) {
      return undefined
    }

    const timeout = window.setTimeout(() => {
      onClose()
    }, type === 'error' ? 9000 : 3200)

    return () => window.clearTimeout(timeout)
  }, [message, onClose, type])

  if (!message) {
    return null
  }

  return (
    <div className={`toast-notice ${type}`} role={type === 'error' ? 'alert' : 'status'} aria-live="polite">
      <div className="toast-copy">
        <strong>{type === 'error' ? 'Atencao' : 'Concluido'}</strong>
        <span>{message}</span>
      </div>
      <button type="button" className="ghost-button toast-close" onClick={onClose}>
        x
      </button>
    </div>
  )
}
