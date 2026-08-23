"use client";

import { useMemo, useState } from "react";
import { DashboardToolbar } from "./dashboard-toolbar";
import { downloadCsv, matchesSearch, paginate, type DateRangeKey } from "./dashboard-utils";
import { PanelCard, tableClass, tableHeadClass, tableRowClass } from "./dashboard-shell";

export type DataColumn<T> = {
  key: string;
  label: string;
  render?: (row: T) => React.ReactNode;
  exportValue?: (row: T) => string;
  searchValue?: (row: T) => string;
};

export function DataTable<T extends Record<string, unknown>>({
  title,
  subtitle,
  columns,
  rows,
  exportFilename,
  searchPlaceholder = "Search table…",
  pageSize = 8,
  dateKey,
  action,
}: {
  title: string;
  subtitle?: string;
  columns: DataColumn<T>[];
  rows: T[];
  exportFilename: string;
  searchPlaceholder?: string;
  pageSize?: number;
  dateKey?: keyof T;
  action?: React.ReactNode;
}) {
  const [search, setSearch] = useState("");
  const [dateRange, setDateRange] = useState<DateRangeKey>("30d");
  const [page, setPage] = useState(1);
  const [refreshKey, setRefreshKey] = useState(0);

  const filtered = useMemo(() => {
    void refreshKey;
    const days = dateRange === "7d" ? 7 : dateRange === "30d" ? 30 : 90;
    const cutoff = Date.now() - days * 24 * 60 * 60 * 1000;

    return rows.filter((row) => {
      const searchValues = columns.flatMap((col) => {
        if (col.searchValue) return [col.searchValue(row)];
        const val = row[col.key];
        return [typeof val === "string" || typeof val === "number" ? val : ""];
      });
      if (!matchesSearch(search, searchValues)) return false;

      if (dateKey && row[dateKey]) {
        const raw = String(row[dateKey]);
        if (new Date(raw).getTime() < cutoff) return false;
      }
      return true;
    });
  }, [rows, columns, search, dateRange, dateKey, refreshKey]);

  const { items, page: safePage, totalPages, total } = paginate(filtered, page, pageSize);

  function handleDownload() {
    const headers = columns.map((c) => c.label);
    const csvRows = filtered.map((row) =>
      columns.map((col) => (col.exportValue ? col.exportValue(row) : String(row[col.key] ?? "")))
    );
    downloadCsv(exportFilename, headers, csvRows);
  }

  return (
    <PanelCard title={title} subtitle={subtitle} action={action}>
      <DashboardToolbar
        search={search}
        onSearchChange={(v) => { setSearch(v); setPage(1); }}
        searchPlaceholder={searchPlaceholder}
        dateRange={dateKey ? dateRange : undefined}
        onDateRangeChange={dateKey ? (v) => { setDateRange(v); setPage(1); } : undefined}
        onDownload={handleDownload}
        onRefresh={() => setRefreshKey((k) => k + 1)}
        resultCount={total}
      />
      <div className="overflow-x-auto">
        <table className={tableClass}>
          <thead className={tableHeadClass}>
            <tr>
              {columns.map((col) => (
                <th key={col.key} className="px-3 py-3 font-semibold">{col.label}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {items.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="px-3 py-10 text-center text-slate-500">
                  No results match your search.
                </td>
              </tr>
            ) : (
              items.map((row, i) => (
                <tr key={i} className={tableRowClass}>
                  {columns.map((col) => (
                    <td key={col.key} className="px-3 py-4">
                      {col.render ? col.render(row) : String(row[col.key] ?? "")}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
      {totalPages > 1 ? (
        <div className="mt-4 flex items-center justify-between bg-slate-100 px-3 py-4 rounded-2xl">
          <p className="text-xs font-semibold text-slate-500">
            Page {safePage} of {totalPages}
          </p>
          <div className="flex gap-2">
            <button type="button" disabled={safePage <= 1} onClick={() => setPage((p) => p - 1)} className="rounded-full bg-white px-3 py-1.5 text-xs font-bold text-slate-800 shadow-sm disabled:cursor-not-allowed disabled:bg-slate-300">
              Previous
            </button>
            <button type="button" disabled={safePage >= totalPages} onClick={() => setPage((p) => p + 1)} className="rounded-full bg-white px-3 py-1.5 text-xs font-bold text-slate-800 shadow-sm disabled:cursor-not-allowed disabled:bg-slate-300">
              Next
            </button>
          </div>
        </div>
      ) : null}
    </PanelCard>
  );
}
