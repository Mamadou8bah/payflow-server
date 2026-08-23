export type DateRangeKey = "7d" | "30d" | "90d";

export const DATE_RANGE_OPTIONS: { value: DateRangeKey; label: string }[] = [
  { value: "7d", label: "Last 7 days" },
  { value: "30d", label: "Last 30 days" },
  { value: "90d", label: "Last 90 days" },
];

export function downloadCsv(filename: string, headers: string[], rows: string[][]) {
  const escape = (cell: string) => {
    const value = String(cell ?? "");
    if (value.includes(",") || value.includes('"') || value.includes("\n")) {
      return `"${value.replace(/"/g, '""')}"`;
    }
    return value;
  };

  const lines = [headers.map(escape).join(","), ...rows.map((row) => row.map(escape).join(","))];
  const blob = new Blob([lines.join("\n")], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename.endsWith(".csv") ? filename : `${filename}.csv`;
  link.click();
  URL.revokeObjectURL(url);
}

export function matchesSearch(query: string, values: (string | number | undefined | null)[]) {
  const q = query.trim().toLowerCase();
  if (!q) return true;
  return values.some((v) => String(v ?? "").toLowerCase().includes(q));
}

export function filterByDateRange<T extends { time?: string; createdAt?: string; receivedAt?: string; timestamp?: string }>(
  items: T[],
  range: DateRangeKey
): T[] {
  const days = range === "7d" ? 7 : range === "30d" ? 30 : 90;
  const cutoff = Date.now() - days * 24 * 60 * 60 * 1000;
  return items.filter((item) => {
    const raw = item.time ?? item.createdAt ?? item.receivedAt ?? item.timestamp;
    if (!raw) return true;
    return new Date(raw).getTime() >= cutoff;
  });
}

export function paginate<T>(items: T[], page: number, pageSize: number) {
  const totalPages = Math.max(1, Math.ceil(items.length / pageSize));
  const safePage = Math.min(Math.max(1, page), totalPages);
  const start = (safePage - 1) * pageSize;
  return { items: items.slice(start, start + pageSize), page: safePage, totalPages, total: items.length };
}
