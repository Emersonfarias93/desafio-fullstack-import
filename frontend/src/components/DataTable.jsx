export function DataTable({ columns, rows, empty }) {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {columns.map((column) => <th key={column}>{column}</th>)}
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="empty">{empty}</td>
            </tr>
          )}
          {rows.map((row, index) => (
            <tr key={`${index}-${row[0]}`}>
              {row.map((cell, cellIndex) => <td key={`${cellIndex}-${String(cell)}`}>{cell}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
