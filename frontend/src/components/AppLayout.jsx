import { navigationItems } from '../constants/navigation'
import { ToastNotice } from './ToastNotice'

const socketLabels = {
  connecting: 'Conectando',
  connected: 'WebSocket online',
  reconnecting: 'Reconectando',
  error: 'WebSocket instavel',
  closed: 'WebSocket fechado',
}

export function AppLayout({ activePage, onNavigate, onRefresh, socketStatus, message, error, onDismissMessage, onDismissError, children }) {
  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="eyebrow">Desafio Fullstack</span>
          <strong>Importacao de Leads</strong>
        </div>

        <nav className="side-nav" aria-label="Navegacao principal">
          {navigationItems.map((item) => (
            <button
              key={item.id}
              type="button"
              className={activePage === item.id ? 'active' : ''}
              onClick={() => onNavigate(item.id)}
            >
              <span>{item.label}</span>
              <small>{item.description}</small>
            </button>
          ))}
        </nav>
      </aside>

      <section className="main-area">
        <header className="topbar">
          <div>
            <span className="eyebrow">Operacao CSV</span>
            <h1>{navigationItems.find((item) => item.id === activePage)?.label}</h1>
          </div>
          <div className="topbar-actions">
            <span className={`socket-pill ${socketStatus}`}>{socketLabels[socketStatus] || socketStatus}</span>
            <button className="ghost-button" type="button" onClick={onRefresh}>
              Atualizar
            </button>
          </div>
        </header>

        <ToastNotice
          message={error || message}
          type={error ? 'error' : 'success'}
          onClose={error ? onDismissError : onDismissMessage}
        />

        {children}
      </section>
    </main>
  )
}
