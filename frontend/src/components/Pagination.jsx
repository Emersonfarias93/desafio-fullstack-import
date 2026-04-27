export function Pagination({ page, totalPages, onChange }) {
  if (!totalPages || totalPages <= 1) {
    return null
  }

  return (
    <div className="pagination">
      <button type="button" onClick={() => onChange(Math.max(0, page - 1))} disabled={page === 0}>
        Anterior
      </button>
      <span>{page + 1} / {totalPages}</span>
      <button type="button" onClick={() => onChange(Math.min(totalPages - 1, page + 1))} disabled={page + 1 >= totalPages}>
        Proxima
      </button>
    </div>
  )
}
