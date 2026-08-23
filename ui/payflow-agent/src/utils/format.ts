import type { OperationType } from "../types";

export function formatMoney(amount: number, currency: string): string {
  return `${currency} ${amount.toLocaleString("en-GB", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}

export function getGreeting(): string {
  const hour = new Date().getHours();
  if (hour < 12) return "Good morning";
  if (hour < 17) return "Good afternoon";
  return "Good evening";
}

export function formatTimestamp(value: string): string {
  return new Intl.DateTimeFormat("en-GB", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

export function operationLabel(type: OperationType): string {
  return type === "DEPOSIT" ? "Cash-in" : "Cash-out";
}

export function statusLabel(status: string): string {
  switch (status) {
    case "AWAITING_AGENT":
      return "Awaiting agent";
    case "PENDING":
      return "Pending";
    case "APPROVED":
      return "Approved";
    case "COMPLETED":
      return "Completed";
    case "FAILED":
      return "Failed";
    default:
      return status;
  }
}
