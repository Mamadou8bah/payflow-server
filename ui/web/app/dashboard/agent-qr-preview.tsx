"use client";

import QRCode from "react-qr-code";
import { encodeAgentQrPayload, type AgentQrPayload } from "../../lib/agent-qr";

export function AgentQrPreview({
  payload,
  title,
  hint,
}: {
  payload: AgentQrPayload;
  title: string;
  hint?: string;
}) {
  const value = encodeAgentQrPayload(payload);

  return (
    <div className="rounded-[1.5rem] bg-slate-100 p-4">
      <p className="text-sm font-bold text-slate-900">{title}</p>
      {hint ? <p className="mt-1 text-xs text-slate-600">{hint}</p> : null}
      <div className="mt-4 flex flex-col items-center gap-4 sm:flex-row sm:items-start">
        <div className="rounded-2xl border border-slate-200 bg-white p-4">
          <QRCode value={value} size={168} level="M" />
        </div>
        <dl className="min-w-0 flex-1 space-y-2 text-sm">
          <div className="rounded-xl bg-white px-3 py-2">
            <dt className="text-[10px] font-bold uppercase tracking-wide text-slate-500">Reference</dt>
            <dd className="font-mono text-xs font-bold text-[#123c91]">{payload.reference}</dd>
          </div>
          <div className="rounded-xl bg-white px-3 py-2">
            <dt className="text-[10px] font-bold uppercase tracking-wide text-slate-500">Wallet</dt>
            <dd className="font-semibold text-slate-900">{payload.walletName} <span className="text-slate-500">#{payload.walletId}</span></dd>
          </div>
          <div className="rounded-xl bg-white px-3 py-2">
            <dt className="text-[10px] font-bold uppercase tracking-wide text-slate-500">Amount</dt>
            <dd className="font-black text-slate-950">{payload.currency} {payload.amount}</dd>
          </div>
          <div className="rounded-xl bg-white px-3 py-2">
            <dt className="text-[10px] font-bold uppercase tracking-wide text-slate-500">Status</dt>
            <dd className="font-semibold text-amber-800">Awaiting agent scan</dd>
          </div>
        </dl>
      </div>
      <p className="mt-4 text-center text-xs font-semibold text-slate-600 sm:text-left">
        An agent scans this code to complete the {payload.operation.toLowerCase()} on your wallet.
      </p>
    </div>
  );
}

export function PrimaryWalletBanner({
  name,
  id,
  balance,
  currency,
}: {
  name: string;
  id: number;
  balance: string;
  currency: string;
}) {
  return (
    <div className="rounded-2xl bg-blue-100 px-4 py-3">
      <p className="text-[10px] font-bold uppercase tracking-wide text-[#123c91]">Your wallet</p>
      <p className="mt-1 text-sm font-black text-slate-950">{name}</p>
      <p className="text-xs font-semibold text-slate-600">#{id} · {balance || `${currency} 0`}</p>
    </div>
  );
}
