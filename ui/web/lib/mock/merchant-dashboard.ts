export const mockMerchantDashboard = {
  storeName: "Acme Collections",
  stats: [
    { label: "Today's collections", value: "GMD 184,200", hint: "32 payments" },
    { label: "Pending settlements", value: "GMD 42,500", hint: "6 awaiting payout" },
    { label: "Active payment links", value: "18", hint: "4 created today" },
    { label: "Webhook success rate", value: "99.2%", hint: "Last 24 hours" },
  ],
  wallets: [
    { id: 7710, name: "Main collections", currency: "GMD", balance: "GMD 128,000", status: "ACTIVE", dailyLimit: "GMD 500,000", dailyUsed: "GMD 184,200" },
    { id: 7711, name: "Payout reserve", currency: "GMD", balance: "GMD 42,500", status: "ACTIVE", dailyLimit: "GMD 200,000", dailyUsed: "GMD 0" },
  ],
  deposits: [
    { id: 1201, walletId: 7710, amount: "GMD 12,000", status: "COMPLETED", reference: "dep_1201", time: "2026-06-27T10:12:00Z" },
    { id: 1200, walletId: 7710, amount: "GMD 4,500", status: "AWAITING_AGENT", reference: "dep_1200", time: "2026-06-27T09:58:00Z" },
  ],
  withdrawals: [
    { id: 801, walletId: 7710, amount: "GMD 25,000", status: "COMPLETED", reference: "wd_801", time: "2026-06-27T08:30:00Z" },
    { id: 800, walletId: 7710, amount: "GMD 10,000", status: "AWAITING_AGENT", reference: "wd_800", time: "2026-06-27T07:15:00Z" },
  ],
  transfers: [
    { id: 99218, from: 7710, to: 7711, amount: "GMD 15,000", status: "COMPLETED", reference: "pf_99218", time: "2026-06-27T09:00:00Z" },
    { id: 99217, from: 7710, to: 6544, amount: "GMD 4,500", status: "PENDING", reference: "pf_99217", time: "2026-06-27T08:45:00Z" },
  ],
  subscriptions: [
    { id: 301, plan: "Monthly SaaS", amount: "GMD 1,500", status: "ACTIVE", customer: "TechCorp Ltd", nextBilling: "2026-07-01" },
    { id: 300, plan: "Annual support", amount: "GMD 12,000", status: "ACTIVE", customer: "School District", nextBilling: "2027-01-15" },
    { id: 299, plan: "Starter", amount: "GMD 500", status: "CANCELLED", customer: "Beta User", nextBilling: "—" },
  ],
  recentPayments: [
    { id: "pay_8821", customer: "Fatou J.", amount: "GMD 4,500", status: "paid", time: "2026-06-27T10:40:00Z" },
    { id: "pay_8820", customer: "Lamin S.", amount: "GMD 12,000", status: "paid", time: "2026-06-27T10:12:00Z" },
    { id: "pay_8819", customer: "Awa C.", amount: "GMD 2,250", status: "pending", time: "2026-06-27T09:58:00Z" },
    { id: "pay_8818", customer: "Omar B.", amount: "GMD 8,900", status: "failed", time: "2026-06-27T09:31:00Z" },
  ],
  paymentLinks: [
    { id: "link_101", title: "June invoice batch", amount: "GMD 15,000", status: "active", uses: 12 },
    { id: "link_100", title: "School fees", amount: "GMD 3,500", status: "active", uses: 44 },
    { id: "link_099", title: "Event tickets", amount: "GMD 800", status: "expired", uses: 210 },
  ],
  webhooks: [
    { id: "wh_m_01", event: "payment_link.paid", status: "delivered", time: "2026-06-27T10:40:00Z" },
    { id: "wh_m_02", event: "payout.settled", status: "delivered", time: "2026-06-27T09:15:00Z" },
    { id: "wh_m_03", event: "payment.failed", status: "retrying", time: "2026-06-27T08:50:00Z" },
  ],
  charts: {
    collectionsByRange: {
      "7d": [
        { label: "Mon", value: 142 }, { label: "Tue", value: 168 }, { label: "Wed", value: 155 },
        { label: "Thu", value: 198 }, { label: "Fri", value: 184 }, { label: "Sat", value: 92 }, { label: "Sun", value: 110 },
      ],
      "30d": [
        { label: "W1", value: 820 }, { label: "W2", value: 940 }, { label: "W3", value: 880 }, { label: "W4", value: 1020 },
      ],
      "90d": [
        { label: "Apr", value: 3200 }, { label: "May", value: 3600 }, { label: "Jun", value: 4100 },
      ],
    },
    paymentStatus: [
      { label: "Paid", value: 28, color: "#10b981" },
      { label: "Pending", value: 6, color: "#f59e0b" },
      { label: "Failed", value: 3, color: "#ef4444" },
    ],
    kpiSparklines: {
      collections: [120, 135, 128, 155, 168, 184, 184],
      links: [12, 14, 15, 16, 17, 18, 18],
    },
  },
};
