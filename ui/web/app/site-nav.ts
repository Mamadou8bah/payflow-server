export type SiteNavGroup = {
  label: string;
  links: Array<{ label: string; href: string }>;
};

export const headerNavGroups: SiteNavGroup[] = [
  {
    label: "Customers",
    links: [
      { label: "Wallet app", href: "/app" },
      { label: "Checkout experience", href: "/#audiences" },
      { label: "Transaction history", href: "/customers/transactions" },
    ],
  },
  {
    label: "Merchants",
    links: [
      { label: "Payment links", href: "/signup" },
      { label: "Collections", href: "/#audiences" },
      { label: "Webhook updates", href: "/merchants/webhooks" },
    ],
  },
  {
    label: "Developers",
    links: [
      { label: "Developer docs", href: "/docs" },
      { label: "API reference", href: "/docs#api" },
      { label: "Integration guide", href: "/developers/integration" },
    ],
  },
  {
    label: "Operations",
    links: [
      { label: "Risk controls", href: "/company/security" },
      { label: "Reconciliation", href: "/operations/reconciliation" },
      { label: "Admin dashboard", href: "/dashboard" },
    ],
  },
  {
    label: "Resources",
    links: [
      { label: "Why choose us", href: "/#capabilities" },
      { label: "Help center", href: "/help" },
      { label: "Contact sales", href: "/contact" },
    ],
  },
];

export const footerColumns: Array<{
  title: string;
  links: Array<{ label: string; href: string }>;
}> = [
  {
    title: "Product",
    links: [
      { label: "Customer checkout", href: "/#audiences" },
      { label: "Merchant collections", href: "/#audiences" },
      { label: "Wallet transfers", href: "/#audiences" },
      { label: "Developer docs", href: "/docs" },
    ],
  },
  {
    title: "Company",
    links: [
      { label: "About Payflow", href: "/company/about" },
      { label: "Security", href: "/company/security" },
      { label: "Compliance", href: "/company/compliance" },
      { label: "Contact sales", href: "/contact" },
    ],
  },
  {
    title: "Resources",
    links: [
      { label: "API reference", href: "/docs#api" },
      { label: "Status page", href: "/status" },
      { label: "Help center", href: "/help" },
      { label: "Release notes", href: "/release-notes" },
    ],
  },
  {
    title: "Legal",
    links: [
      { label: "Privacy policy", href: "/legal/privacy" },
      { label: "Terms of service", href: "/legal/terms" },
      { label: "Cookie policy", href: "/legal/cookies" },
    ],
  },
];
