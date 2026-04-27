export function StatTerm({ label, value }) {
  return (
    <div>
      <dt>{label}</dt>
      <dd>{value ?? 0}</dd>
    </div>
  )
}
