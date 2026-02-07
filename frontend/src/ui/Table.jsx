import React from 'react';

export default function Table({ columns, data, rowKey }) {
  return (
    <div className="lms-table-wrap">
      <table className="lms-table">
        <thead>
          <tr>
            {columns.map((c) => (
              <th key={c.key} style={c.thStyle}>{c.header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, idx) => (
            <tr key={rowKey ? rowKey(row) : row.id ?? idx}>
              {columns.map((c) => (
                <td key={c.key} style={c.tdStyle}>{c.render ? c.render(row) : row[c.key]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
