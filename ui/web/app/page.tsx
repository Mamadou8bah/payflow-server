import { FaqSection } from "./faq-section";

const audienceCards = [
  {
    title: "Wallet transfers",
    text: "Move money between wallets for free with clear confirmations, balance visibility, and transaction history.",
  },
  {
    title: "Merchant collections",
    text: "Create payment links, collect payments, and receive webhook updates as statuses change.",
  },
  {
    title: "Customer checkout",
    text: "Deliver a smooth customer payment flow with secure authentication, clear confirmations, and visible transaction history.",
  },
  {
    title: "Developer integration",
    text: "Create an API key, accept payments from your backend, and confirm with webhooks.",
  },
];

function renderAudienceIcon(title: string) {
  if (title === "Wallet transfers") {
    return (
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="h-7 w-7 text-orange-500" aria-hidden="true">
        <path d="M19 12C19 12.5523 18.5523 13 18 13C17.4477 13 17 12.5523 17 12C17 11.4477 17.4477 11 18 11C18.5523 11 19 11.4477 19 12Z" fill="currentColor"></path>
        <path
          fillRule="evenodd"
          clipRule="evenodd"
          d="M9.94358 3.25H13.0564C14.8942 3.24998 16.3498 3.24997 17.489 3.40314C18.6614 3.56076 19.6104 3.89288 20.3588 4.64124C21.2831 5.56563 21.5777 6.80363 21.6847 8.41008C22.2619 8.6641 22.6978 9.2013 22.7458 9.88179C22.7501 9.94199 22.75 10.0069 22.75 10.067C22.75 10.0725 22.75 10.0779 22.75 10.0833V13.9167C22.75 13.9221 22.75 13.9275 22.75 13.933C22.75 13.9931 22.7501 14.058 22.7458 14.1182C22.6978 14.7987 22.2619 15.3359 21.6847 15.5899C21.5777 17.1964 21.2831 18.4344 20.3588 19.3588C19.6104 20.1071 18.6614 20.4392 17.489 20.5969C16.3498 20.75 14.8942 20.75 13.0564 20.75H9.94359C8.10583 20.75 6.65019 20.75 5.51098 20.5969C4.33856 20.4392 3.38961 20.1071 2.64124 19.3588C1.89288 18.6104 1.56076 17.6614 1.40314 16.489C1.24997 15.3498 1.24998 13.8942 1.25 12.0564V11.9436C1.24998 10.1058 1.24997 8.65019 1.40314 7.51098C1.56076 6.33856 1.89288 5.38961 2.64124 4.64124C3.38961 3.89288 4.33856 3.56076 5.51098 3.40314C6.65019 3.24997 8.10582 3.24998 9.94358 3.25ZM20.1679 15.75H18.2308C16.0856 15.75 14.25 14.1224 14.25 12C14.25 9.87756 16.0856 8.25 18.2308 8.25H20.1679C20.0541 6.90855 19.7966 6.20043 19.2981 5.7019C18.8749 5.27869 18.2952 5.02502 17.2892 4.88976C16.2615 4.75159 14.9068 4.75 13 4.75H10C8.09318 4.75 6.73851 4.75159 5.71085 4.88976C4.70476 5.02502 4.12511 5.27869 3.7019 5.7019C3.27869 6.12511 3.02502 6.70476 2.88976 7.71085C2.75159 8.73851 2.75 10.0932 2.75 12C2.75 13.9068 2.75159 15.2615 2.88976 16.2892C3.02502 17.2952 3.27869 17.8749 3.7019 18.2981C4.12511 18.7213 4.70476 18.975 5.71085 19.1102C6.73851 19.2484 8.09318 19.25 10 19.25H13C14.9068 19.25 16.2615 19.2484 17.2892 19.1102C18.2952 18.975 18.8749 18.7213 19.2981 18.2981C19.7966 17.7996 20.0541 17.0915 20.1679 15.75ZM20.9235 9.75023C20.9032 9.75001 20.8766 9.75 20.8333 9.75H18.2308C16.8074 9.75 15.75 10.8087 15.75 12C15.75 13.1913 16.8074 14.25 18.2308 14.25H20.8333C20.8766 14.25 20.9032 14.25 20.9235 14.2498C20.936 14.2496 20.9426 14.2495 20.9457 14.2493L20.9479 14.2492C21.1541 14.2367 21.2427 14.0976 21.2495 14.0139C21.2495 14.0139 21.2497 14.0076 21.2498 13.9986C21.25 13.9808 21.25 13.9572 21.25 13.9167V10.0833C21.25 10.0428 21.25 10.0192 21.2498 10.0014C21.2497 9.99238 21.2495 9.98609 21.2495 9.98609C21.2427 9.90242 21.1541 9.7633 20.9479 9.75076C20.9479 9.75076 20.943 9.75043 20.9235 9.75023ZM7 8.25C7.41421 8.25 7.75 8.58579 7.75 9V15C7.75 15.4142 7.41421 15.75 7 15.75C6.58579 15.75 6.25 15.4142 6.25 15V9C6.25 8.58579 6.58579 8.25 7 8.25Z"
          fill="currentColor"
        ></path>
      </svg>
    );
  }

  if (title === "Customer checkout") {
    return (
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="h-7 w-7 text-orange-500" aria-hidden="true">
        <path d="M12,2v7.1c1.2,0.4,2,1.5,2,2.8c0,0.5-0.1,1-0.4,1.4l2,1.6c0.1,0,0.2-0.1,0.4-0.1c0.6,0,1,0.4,1,1c0,0.6-0.4,1-1,1 c-0.6,0-1-0.4-1-1v-0.1l-2-1.6c-0.5,0.5-1.2,0.8-2,0.8c-1.7,0-3-1.3-3-3c0-1.3,0.8-2.4,2-2.8v-7H9.9C6.4,2.5,3.5,5.4,3.1,9 c-0.3,2.2,0.3,4.2,1.5,5.8C5.5,16,6,17.3,6,18.8V22h9v-3h2c1.1,0,2-0.9,2-2v-3l1.5-0.6c0.4-0.2,0.6-0.8,0.4-1.2l-1.9-3 C18.6,5.5,15.7,2.5,12,2z M11,10.5c-0.8,0-1.5,0.7-1.5,1.5s0.7,1.5,1.5,1.5s1.5-0.7,1.5-1.5S11.8,10.5,11,10.5z" fill="currentColor" />
      </svg>
    );
  }

  if (title === "Merchant collections") {
    return (
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="h-7 w-7 text-orange-500" aria-hidden="true">
        <path d="M19 15C16.79 15 15 16.79 15 19C15 19.75 15.21 20.46 15.58 21.06C16.27 22.22 17.54 23 19 23C20.46 23 21.73 22.22 22.42 21.06C22.79 20.46 23 19.75 23 19C23 16.79 21.21 15 19 15ZM21.07 18.57L18.94 20.54C18.8 20.67 18.61 20.74 18.43 20.74C18.24 20.74 18.05 20.67 17.9 20.52L16.91 19.53C16.62 19.24 16.62 18.76 16.91 18.47C17.2 18.18 17.68 18.18 17.97 18.47L18.45 18.95L20.05 17.47C20.35 17.19 20.83 17.21 21.11 17.51C21.39 17.81 21.37 18.28 21.07 18.57Z" fill="currentColor"></path>
        <path d="M22 7.54844V7.99844C22 8.54844 21.55 8.99844 21 8.99844H3C2.45 8.99844 2 8.54844 2 7.99844V7.53844C2 5.24844 3.85 3.39844 6.14 3.39844H17.85C20.14 3.39844 22 5.25844 22 7.54844Z" fill="currentColor"></path>
        <path d="M2 11.4983V16.4583C2 18.7483 3.85 20.5983 6.14 20.5983H12.4C12.98 20.5983 13.48 20.1083 13.43 19.5283C13.29 17.9983 13.78 16.3383 15.14 15.0183C15.7 14.4683 16.39 14.0483 17.14 13.8083C18.39 13.4083 19.6 13.4583 20.67 13.8183C21.32 14.0383 22 13.5683 22 12.8783V11.4883C22 10.9383 21.55 10.4883 21 10.4883H3C2.45 10.4983 2 10.9483 2 11.4983ZM8 17.2483H6C5.59 17.2483 5.25 16.9083 5.25 16.4983C5.25 16.0883 5.59 15.7483 6 15.7483H8C8.41 15.7483 8.75 16.0883 8.75 16.4983C8.75 16.9083 8.41 17.2483 8 17.2483Z" fill="currentColor"></path>
      </svg>
    );
  }

  if (title === "Developer integration") {
    return (
      <svg viewBox="0 0 1024 1024" className="h-7 w-7 text-orange-500" xmlns="http://www.w3.org/2000/svg" fill="currentColor" aria-hidden="true">
        <path d="M877.685565 727.913127l-0.584863-0.365539a32.898541 32.898541 0 0 1-8.041866-46.423497 411.816631 411.816631 0 1 0-141.829267 145.777092c14.621574-8.992268 33.62962-5.117551 43.645398 8.772944l0.146216 0.073108a30.412874 30.412874 0 0 1-7.968758 43.206751l-6.141061 4.020933a475.201154 475.201154 0 1 1 163.615412-164.419599 29.974227 29.974227 0 0 1-42.841211 9.357807z m-537.342843-398.584106c7.164571-7.091463 24.71046-9.650239 33.26408 0 10.600641 11.185504 7.164571 29.462472 0 37.138798l-110.612207 107.468569L370.901811 576.14119c7.164571 7.091463 8.114974 27.342343 0 35.384209-9.796455 9.723347-29.828011 8.188081-36.480827 1.535265L208.309909 487.388236a18.423183 18.423183 0 0 1 0-25.953294l132.032813-132.032813z m343.314556 0l132.032813 132.032813a18.423183 18.423183 0 0 1 0 25.953294L689.652124 613.133772c-6.652816 6.579708-25.587754 10.746857-36.553935 0-10.30821-10.235102-7.091463-31.290168 0-38.381632l108.345863-100.669537-111.855041-108.638294c-7.164571-7.676326-9.504023-26.611265 0-36.04218 9.284699-9.138484 26.903696-7.091463 34.068267 0z m-135.54199-26.318833c3.582286-9.504023 21.347498-15.498868 32.679217-11.258612 10.819965 4.020933 17.180349 19.008046 14.256035 28.512069l-119.896906 329.716493c-3.509178 9.504023-20.616419 13.305632-30.193551 9.723347-10.161994-3.509178-21.201282-17.545889-17.545888-26.976804l120.627985-329.716493z" />
      </svg>
    );
  }

  return (
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="h-7 w-7 text-orange-500" aria-hidden="true">
      <circle cx="12" cy="12" r="8" stroke="currentColor" strokeWidth="2"></circle>
    </svg>
  );
}

const journeySteps = [
  {
    title: "Customer checkout",
    text: "A customer pays using a secure flow backed by login, 2FA, and transaction history.",
    note: "Customer facing",
  },
  {
    title: "Merchant collection",
    text: "Merchants create payment links and listen to webhook events when payment status changes.",
    note: "Merchant facing",
  },
  {
    title: "Risk evaluation",
    text: "Payflow evaluates the transfer before it lands, using thresholds, flags, and 2FA signals.",
    note: "Operator facing",
  },
  {
    title: "Reconciliation and audit",
    text: "Operations teams reconcile mismatches, reverse transactions, and review audit-friendly traces.",
    note: "Back office",
  },
];

const whyChooseItems = [
  {
    iconTitle: "Wallet transfers",
    title: "Free wallet-to-wallet transfers for customers.",
    text: "Customers can send money between Payflow wallets without transfer fees, with clear confirmations, balance visibility, and transaction history.",
  },
  {
    iconTitle: "Merchant collections",
    title: "Simple collection tools for merchants.",
    text: "Merchants can create payment links, accept checkout payments, track statuses, and receive webhook updates without juggling separate tools.",
  },
  {
    iconTitle: "Customer checkout",
    title: "Smooth checkout for every customer.",
    text: "Customers get secure authentication, clear payment confirmations, visible transaction history, and a checkout flow that feels easy from start to finish.",
  },
  {
    iconTitle: "Developer integration",
    title: "Developer-friendly APIs from day one.",
    text: "Developers get clear endpoints for wallets, transfers, payment links, risk checks, reconciliation, and webhook-driven integrations.",
  },
];

const proofStats = [
  ["Customer payments", "Checkout flows with 2FA and transaction history"],
  ["Merchant collections", "Payment links and webhook-driven updates"],
  ["Wallet operations", "Limits, freezing, unfreezing, and balances"],
  ["Operational control", "Risk flags, reconciliation, and audit trail"],
];

const faqSections = [
  {
    title: "Sending money",
    description: "For customers moving money, checking balances, and reviewing payment activity.",
    questions: [
      "Are wallet-to-wallet transfers really free?",
      "How quickly does money move between Payflow wallets?",
      "Can customers see their balance and transaction history?",
      "What happens if a customer sends money to the wrong wallet?",
    ],
  },
  {
    title: "Getting paid",
    description: "For merchants collecting payments through links, checkout flows, and status updates.",
    questions: [
      "Can merchants create payment links for customers?",
      "How do merchants know when a payment succeeds or fails?",
      "Can payment status updates be sent through webhooks?",
      "Where can merchants track collections and customer payments?",
    ],
  },
  {
    title: "Building with Payflow",
    description: "For developers integrating wallets, payment links, transfers, and webhooks.",
    questions: [
      "Does Payflow provide documented API endpoints?",
      "Can developers build wallet and transfer flows with the API?",
      "Are webhook events available for payment lifecycle updates?",
      "How does Payflow support reliable integrations?",
    ],
  },
  {
    title: "Account help",
    description: "For users who need support with access, limits, reversals, and payment issues.",
    questions: [
      "Can Payflow require extra verification for sensitive actions?",
      "What should users do when a payment needs review?",
      "Can balances be frozen or limited for safety?",
      "How are reversals and reconciliation handled?",
    ],
  },
];

const customerHighlights = [
  "Secure login with 2FA when required",
  "Visible transaction history and balance state",
  "Safer payment confirmations and reversals",
];

const merchantHighlights = [
  "Create payment links for checkout and invoices",
  "Receive webhook events for payment lifecycle updates",
  "Track collections, payouts, and merchant-facing status",
];

const appStoreOptions = [
  {
    name: "Google Play",
    href: "https://play.google.com/store",
    description: "Android app",
    icon: (
      <svg viewBox="-1 0 24 24" xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-slate-300" fill="currentColor" aria-hidden="true">
        <path d="m3.751.61 13.124 7.546-2.813 2.813zm-2.719-.61 12.047 12-12.046 12c-.613-.271-1.033-.874-1.033-1.575 0-.023 0-.046.001-.068v.003-20.719c-.001-.019-.001-.042-.001-.065 0-.701.42-1.304 1.022-1.571l.011-.004zm19.922 10.594c.414.307.679.795.679 1.344 0 .022 0 .043-.001.065v-.003c.004.043.007.094.007.145 0 .516-.25.974-.636 1.258l-.004.003-2.813 1.593-3.046-2.999 3.047-3.047zm-17.203 12.796 10.312-10.359 2.813 2.813z" />
      </svg>
    ),
  },
  {
    name: "App Store",
    href: "https://www.apple.com/app-store/",
    description: "iPhone and iPad",
    icon: (
      <svg viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-slate-300" fill="currentColor" aria-hidden="true">
        <path d="M15.994 7.556l0.569-0.981c0.35-0.613 1.131-0.819 1.744-0.469s0.819 1.131 0.469 1.744l-5.469 9.469h3.956c1.281 0 2 1.506 1.444 2.55h-11.594c-0.706 0-1.275-0.569-1.275-1.275s0.569-1.275 1.275-1.275h3.25l4.162-7.213-1.3-2.256c-0.35-0.613-0.144-1.388 0.469-1.744 0.612-0.35 1.387-0.144 1.744 0.469zM11.075 21.181l-1.225 2.125c-0.35 0.613-1.131 0.819-1.744 0.469s-0.819-1.131-0.469-1.744l0.913-1.575c1.025-0.319 1.863-0.075 2.525 0.725zM21.631 17.325h3.319c0.706 0 1.275 0.569 1.275 1.275s-0.569 1.275-1.275 1.275h-1.844l1.244 2.156c0.35 0.613 0.143 1.387-0.469 1.744-0.613 0.35-1.388 0.144-1.744-0.469-2.094-3.631-3.669-6.35-4.712-8.162-1.069-1.844-0.306-3.694 0.45-4.319 0.837 1.438 2.087 3.606 3.756 6.5zM16 0.5c-8.563 0-15.5 6.938-15.5 15.5s6.938 15.5 15.5 15.5c8.563 0 15.5-6.938 15.5-15.5s-6.938-15.5-15.5-15.5zM29.5 16c0 7.419-6.006 13.5-13.5 13.5-7.419 0-13.5-6.006-13.5-13.5 0-7.419 6.006-13.5 13.5-13.5 7.419 0 13.5 6.006 13.5 13.5z" />
      </svg>
    ),
  },
  {
    name: "Huawei AppGallery",
    href: "https://appgallery.huawei.com/",
    description: "Huawei devices",
    icon: (
      <svg fill="currentColor" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-slate-300" aria-hidden="true">
        <path d="M4.896 8.188c0 0-2.469 2.359-2.604 4.854v0.464c0.109 2.016 1.63 3.203 1.63 3.203 2.438 2.385 8.344 5.385 9.729 6.063 0 0 0.083 0.042 0.135-0.010l0.026-0.052v-0.057c-3.786-8.25-8.917-14.464-8.917-14.464zM12.865 24.802c-0.026-0.109-0.13-0.109-0.13-0.109l-9.839 0.349c1.063 1.906 2.865 3.37 4.745 2.932 1.281-0.333 4.214-2.375 5.172-3.068 0.083-0.068 0.052-0.12 0.052-0.12zM12.974 23.76c-4.323-2.922-12.693-7.385-12.693-7.385-0.203 0.609-0.266 1.198-0.281 1.729v0.094c0 1.427 0.531 2.427 0.531 2.427 1.068 2.255 3.12 2.938 3.12 2.938 0.938 0.396 1.87 0.411 1.87 0.411 0.161 0.026 5.865 0 7.385 0 0.068 0 0.109-0.068 0.109-0.068v-0.078c0-0.042-0.042-0.068-0.042-0.068zM12.078 4.255c-1.938 0.495-3.328 2.198-3.427 4.198v0.547c0.042 0.802 0.214 1.401 0.214 1.401 0.88 3.865 5.151 10.198 6.068 11.531 0.068 0.068 0.135 0.042 0.135 0.042 0.052-0.021 0.083-0.078 0.078-0.135 1.417-14.13-1.479-17.891-1.479-17.891-0.427 0.026-1.589 0.307-1.589 0.307zM23.146 7.281c0 0-0.651-2.401-3.25-3.042 0 0-0.76-0.188-1.563-0.292 0 0-2.906 3.745-1.495 17.906 0.016 0.094 0.083 0.104 0.083 0.104 0.094 0.042 0.13-0.036 0.13-0.036 0.964-1.375 5.203-7.682 6.068-11.521 0 0 0.479-1.87 0.026-3.12zM19.255 24.708c0 0-0.094 0-0.12 0.063 0 0-0.016 0.094 0.036 0.135 0.932 0.682 3.802 2.667 5.177 3.068 0 0 0.214 0.068 0.573 0.078h0.182c0.922-0.026 2.536-0.49 4-3.010l-9.865-0.333zM29.693 13.495c0.188-2.75-2.589-5.297-2.589-5.307 0 0-5.13 6.214-8.891 14.401 0 0-0.042 0.104 0.026 0.172l0.052 0.010h0.083c1.411-0.703 7.276-3.693 9.703-6.052 0 0 1.536-1.24 1.615-3.224zM31.719 16.349c0 0-8.37 4.49-12.693 7.396 0 0-0.068 0.057-0.042 0.151 0 0 0.042 0.078 0.094 0.078 1.547 0 7.417 0 7.563-0.026 0 0 0.76-0.026 1.693-0.385 0 0 2.078-0.667 3.161-3.031 0 0 0.974-1.932 0.224-4.182z" />
      </svg>
    ),
  },
  {
    name: "Galaxy Store",
    href: "https://galaxystore.samsung.com/",
    description: "Samsung devices",
    icon: (
      <svg viewBox="0 0 192 192" xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-slate-300" fill="none" aria-hidden="true">
        <g stroke="currentColor" strokeWidth="12">
          <path d="M61 64v-7c0-19.33 15.67-35 35-35s35 15.67 35 35v7"></path>
          <path strokeLinejoin="round" d="m28 67 7.681 79.11A26.44 26.44 42.23 0 0 62.001 170h68a26.44 26.44 137.8 0 0 26.32-23.89L164.002 67z"></path>
        </g>
      </svg>
    ),
  },
  {
    name: "Amazon Appstore",
    href: "https://www.amazon.com/gp/mas/get/android",
    description: "Fire tablets",
    icon: (
      <svg viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-slate-300" fill="currentColor" aria-hidden="true">
        <g>
          <path fillRule="evenodd" clipRule="evenodd" d="M293.596,233.97 c0,26.322,0.627,48.264-12.651,71.65c-10.724,19.022-27.791,30.698-46.749,30.698c-25.905,0-41.069-19.73-41.069-48.979 c0-57.525,51.607-67.983,100.469-67.983V233.97z M361.701,398.655c-4.48,4.005-10.934,4.283-15.971,1.567 c-22.446-18.64-26.462-27.263-38.718-45.009c-37.07,37.767-63.335,49.094-111.356,49.094c-56.871,0-101.09-35.085-101.09-105.269 c0-54.833,29.688-92.112,72.023-110.394c36.647-16.086,87.836-19.004,127.006-23.397v-8.774c0-16.074,1.253-35.091-8.218-48.979 c-8.217-12.43-24.013-17.542-37.905-17.542c-25.76,0-48.67,13.196-54.288,40.552c-1.178,6.094-5.612,12.11-11.745,12.425 l-65.459-7.092c-5.524-1.241-11.676-5.682-10.074-14.119c15.036-79.421,86.762-103.418,151.037-103.418 c32.857,0,75.823,8.774,101.729,33.63c32.857,30.71,29.7,71.65,29.7,116.248v105.223c0,31.65,13.138,45.543,25.487,62.615 c4.317,6.128,5.292,13.44-0.209,17.92c-13.8,11.571-38.324,32.869-51.811,44.87L361.701,398.655z M454.261,417.377 c-62.721,26.602-130.884,39.461-192.884,39.461c-91.933,0-180.924-25.209-252.882-67.096c-6.302-3.668-10.968,2.797-5.733,7.532 c66.702,60.236,154.845,96.425,252.732,96.425c69.846,0,150.949-21.971,206.903-63.254 C471.646,423.598,463.72,413.361,454.261,417.377z M470.962,467.655c-2.043,5.106,2.345,7.172,6.964,3.296 c30.014-25.116,37.767-77.716,31.615-85.317c-6.093-7.532-58.565-14.021-90.599,8.461c-4.921,3.481-4.062,8.24,1.394,7.59 c18.036-2.17,58.182-6.986,65.343,2.183C492.828,413.036,477.717,450.779,470.962,467.655z" />
        </g>
      </svg>
    ),
  },
  {
    name: "Windows Store",
    href: "https://apps.microsoft.com/",
    description: "Desktop and tablet",
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-slate-300" aria-hidden="true">
        <path d="M3 4.5 11 3v8H3V4.5zm0 15L11 21v-8H3v6.5zM13 3l8-1.5V11h-8V3zm0 20 8-1.5V13h-8v10z" />
      </svg>
    ),
  },
  {
    name: "Web app",
    href: "/login",
    description: "Browser access",
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-slate-300" aria-hidden="true">
        <path d="M4 4h16v16H4V4zm2 2v2h12V6H6zm0 4v8h12v-8H6z" />
      </svg>
    ),
  },
  {
    name: "APK download",
    href: "/download",
    description: "Android install file",
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-slate-300" aria-hidden="true">
        <path d="M12 3l4 4h-3v6h-2V7H8l4-4zm-7 10h14v8H5v-8zm2 2v4h10v-4H7z" />
      </svg>
    ),
  },
];

const footerColumns = [
  {
    title: "Product",
    links: ["Customer checkout", "Merchant collections", "Wallet transfers", "Developer docs"],
  },
  {
    title: "Company",
    links: ["About Payflow", "Security", "Compliance", "Contact sales"],
  },
  {
    title: "Resources",
    links: ["API reference", "Status page", "Help center", "Release notes"],
  },
];

const socialLinks = [
  {
    name: "X",
    href: "https://x.com/",
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" aria-hidden="true">
        <path d="M18.244 2H21l-6.19 7.066L22 22h-6.657l-5.21-6.72L4.244 22H1.486l6.64-7.576L2 2h6.803l4.787 6.128L18.244 2Zm-1.168 18h1.922L7.759 3.956H5.7L17.076 20Z" />
      </svg>
    ),
  },
  {
    name: "LinkedIn",
    href: "https://www.linkedin.com/",
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" aria-hidden="true">
        <path d="M6.94 6.5A1.94 1.94 0 1 1 3.06 6.5a1.94 1.94 0 0 1 3.88 0ZM3.3 8.98h3.33V21H3.3V8.98ZM9.2 8.98h3.19v1.64h.05c.44-.84 1.52-1.73 3.13-1.73 3.35 0 3.97 2.2 3.97 5.07V21h-3.33v-5.36c0-1.28-.02-2.92-1.78-2.92-1.79 0-2.06 1.4-2.06 2.83V21H9.2V8.98Z" />
      </svg>
    ),
  },
  {
    name: "Instagram",
    href: "https://www.instagram.com/",
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" aria-hidden="true">
        <path d="M7.75 2h8.5A5.75 5.75 0 0 1 22 7.75v8.5A5.75 5.75 0 0 1 16.25 22h-8.5A5.75 5.75 0 0 1 2 16.25v-8.5A5.75 5.75 0 0 1 7.75 2Zm0 1.5A4.25 4.25 0 0 0 3.5 7.75v8.5a4.25 4.25 0 0 0 4.25 4.25h8.5a4.25 4.25 0 0 0 4.25-4.25v-8.5a4.25 4.25 0 0 0-4.25-4.25h-8.5Zm9.12 1.88a.92.92 0 1 1 0 1.84.92.92 0 0 1 0-1.84ZM12 7.25A4.75 4.75 0 1 1 12 16.75a4.75 4.75 0 0 1 0-9.5Zm0 1.5A3.25 3.25 0 1 0 12 15.25 3.25 3.25 0 0 0 12 8.75Z" />
      </svg>
    ),
  },
  {
    name: "YouTube",
    href: "https://www.youtube.com/",
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" aria-hidden="true">
        <path d="M21.8 8.02a3 3 0 0 0-2.12-2.12C17.8 5.4 12 5.4 12 5.4s-5.8 0-7.68.5A3 3 0 0 0 2.2 8.02 31.1 31.1 0 0 0 1.8 12a31.1 31.1 0 0 0 .4 3.98 3 3 0 0 0 2.12 2.12c1.88.5 7.68.5 7.68.5s5.8 0 7.68-.5a3 3 0 0 0 2.12-2.12A31.1 31.1 0 0 0 22.2 12a31.1 31.1 0 0 0-.4-3.98ZM10.2 15.2V8.8L15.6 12l-5.4 3.2Z" />
      </svg>
    ),
  },
  {
    name: "GitHub",
    href: "https://github.com/",
    icon: (
      <svg viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" aria-hidden="true">
        <path d="M12 2a10 10 0 0 0-3.16 19.49c.5.09.68-.22.68-.48v-1.68c-2.77.6-3.35-1.34-3.35-1.34-.45-1.13-1.1-1.43-1.1-1.43-.9-.62.07-.61.07-.61 1 .07 1.53 1.04 1.53 1.04.88 1.52 2.3 1.08 2.86.82.09-.64.35-1.08.64-1.33-2.21-.25-4.53-1.11-4.53-4.95 0-1.09.39-1.98 1.03-2.68-.1-.26-.45-1.31.1-2.73 0 0 .84-.27 2.75 1.02a9.5 9.5 0 0 1 5 0c1.9-1.29 2.74-1.02 2.74-1.02.56 1.42.21 2.47.1 2.73.64.7 1.03 1.59 1.03 2.68 0 3.85-2.33 4.7-4.55 4.94.36.31.68.92.68 1.86v2.76c0 .26.18.58.69.48A10 10 0 0 0 12 2Z" />
      </svg>
    ),
  },
];

const headerNavGroups = [
  {
    label: "Customers",
    links: [
      { label: "Wallet app", href: "/app" },
      { label: "Checkout experience", href: "#audiences" },
      { label: "Transaction history", href: "/customers/transactions" },
    ],
  },
  {
    label: "Merchants",
    links: [
      { label: "Payment links", href: "/signup" },
      { label: "Collections", href: "#audiences" },
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
      { label: "Risk controls", href: "#capabilities" },
      { label: "Reconciliation", href: "/operations/reconciliation" },
      { label: "Admin dashboard", href: "/dashboard" },
    ],
  },
  {
    label: "Resources",
    links: [
      { label: "Why choose us", href: "#capabilities" },
      { label: "FAQ", href: "#faq" },
      { label: "Contact sales", href: "#cta" },
    ],
  },
];

export default function HomePage() {
  return (
    <main className="bg-slate-100 text-slate-900">
      <header className="sticky top-0 z-[9999] border-b border-slate-200 bg-white backdrop-blur">
        <div className="mx-auto grid w-[min(1280px,calc(100%-2rem))] grid-cols-[auto_1fr_auto] items-center gap-4 py-3.5">
          <a href="/" className="flex items-center">
            <img
              src="https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png"
              alt="Payflow"
              className="h-10 w-10 origin-left scale-[1.75] object-contain md:scale-[2] lg:scale-[2.25]"
            />
          </a>

          <nav className="hidden items-center justify-center gap-2 text-base font-semibold text-slate-700 md:flex">
            {headerNavGroups.map((group) => (
              <div key={group.label} className="group relative">
                <button className="flex items-center gap-1.5 rounded-full px-4 py-2 transition-colors hover:bg-slate-100 hover:text-slate-950" type="button">
                  {group.label}
                  <svg viewBox="0 0 20 20" className="h-4 w-4 text-slate-500 transition-transform group-hover:rotate-180" fill="currentColor" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                    <path d="M5.23 7.21a.75.75 0 0 1 1.06.02L10 11.17l3.71-3.94a.75.75 0 1 1 1.08 1.04l-4.25 4.5a.75.75 0 0 1-1.08 0l-4.25-4.5a.75.75 0 0 1 .02-1.06Z" />
                  </svg>
                </button>
                <div className="invisible absolute left-1/2 top-full z-[10000] mt-3 w-64 -translate-x-1/2 rounded-2xl border border-slate-200 bg-white p-2 opacity-0 shadow-[0_22px_55px_rgba(15,23,42,0.14)] transition-all duration-200 group-hover:visible group-hover:translate-y-0 group-hover:opacity-100">
                  <div className="absolute -top-3 left-0 h-3 w-full" />
                  {group.links.map((link) => (
                    <a key={link.label} href={link.href} className="block rounded-xl px-4 py-3 text-sm font-bold text-slate-700 transition-colors hover:bg-orange-50 hover:text-orange-700">
                      {link.label}
                    </a>
                  ))}
                </div>
              </div>
            ))}
          </nav>

          <div className="flex items-center gap-3 justify-self-end">
            <a className="rounded-full bg-orange-600 px-5 py-2 text-sm font-bold text-white" href="/signup">Get started</a>
            <a className="rounded-full border border-slate-900 px-5 py-2 text-sm font-bold text-slate-900" href="/login">LOG IN</a>
          </div>
        </div>
      </header>

      <section className="bg-white py-16 md:py-20" id="how">
        <div className="mx-auto grid w-[min(1280px,calc(100%-2rem))] items-center gap-12 lg:grid-cols-[minmax(0,1.05fr)_minmax(380px,0.85fr)]">
          <div className="max-w-3xl">
            <p className="text-sm font-bold uppercase tracking-[0.28em] text-orange-600">THE GLOBAL NETWORK FOR SERIOUS PAYMENTS</p>
            <h1 className="mt-4 text-[3rem]  leading-[0.98] tracking-[-0.04em] text-slate-800">
              A payments platform built for customers, merchants, and operators.
            </h1>
            <p className="mt-6 max-w-2xl text-lg leading-relaxed text-slate-600 md:text-xl">
              Payflow connects customer checkout, merchant payment links, wallets, free wallet-to-wallet transfers, risk, and reconciliation in one platform. It keeps the payment journey simple for users and observable for the teams that run it.
            </p>

            <div className="mt-8 flex items-center gap-3 text-slate-700">
              <span className="grid h-6 w-6 place-items-center rounded-full border-2 border-orange-500 text-orange-500">✓</span>
              <p className="font-semibold">Free wallet-to-wallet transfers, merchant collections, and operational controls in one place.</p>
            </div>

            <div className="mt-9 flex flex-wrap items-center gap-4">
              <a href="/signup" className="rounded-full bg-orange-600 px-10 py-4 text-base font-bold text-white shadow-[0_14px_30px_rgba(249,115,22,0.28)]">Get started</a>
              <div className="flex flex-col">
                <p className="text-sm font-black uppercase tracking-[0.16em] text-emerald-600">Merchant-ready</p>
                <p className="text-sm text-slate-500">Payment links, webhooks, free wallet transfers, and collections workflows</p>
              </div>
            </div>
          </div>

          <div className="relative flex justify-center lg:justify-end">
            <div className="relative h-[500px] w-full max-w-[500px] overflow-hidden rounded-[3rem] rounded-br-[10rem] bg-[radial-gradient(circle_at_top_left,rgba(249,115,22,0.24),transparent_36%),radial-gradient(circle_at_top_right,rgba(59,130,246,0.18),transparent_24%),linear-gradient(180deg,#f8fafc_0%,#eef2ff_100%)] p-6 shadow-[0_30px_80px_rgba(15,23,42,0.10)]">
              <div className="absolute inset-0 bg-[radial-gradient(circle_at_70%_55%,rgba(17,24,39,0.86),rgba(17,24,39,0.18)_34%,transparent_62%)]" />
              <img
                src="https://res.cloudinary.com/dflsnes44/image/upload/v1780151927/wp_vznega.jpg"
                alt="Payflow customer and merchant visual"
                className="relative z-10 h-full w-full rounded-[2.4rem] rounded-br-[8rem] object-cover object-center shadow-[0_24px_60px_rgba(15,23,42,0.24)]"
              />
              <div className="absolute left-6 top-6 z-20 rounded-full border border-white/60 bg-white/80 px-4 py-2 text-xs font-bold uppercase tracking-[0.18em] text-slate-600 backdrop-blur">
                Customer and merchant experience
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="py-20 md:py-24" id="audiences">
        <div className="mx-auto w-[min(1280px,calc(100%-2rem))]">

          <div className="grid gap-12 md:grid-cols-2 xl:grid-cols-4">
            {audienceCards.map((item) => (
              <article key={item.title} className="max-w-sm">
                <div className="flex h-12 w-12 items-center justify-center rounded-full border border-slate-200 bg-white shadow-sm">
                  {renderAudienceIcon(item.title)}
                </div>
                <h3 className="mt-7 text-2xl font-black leading-tight text-slate-900">{item.title}</h3>
                <p className="mt-4 text-lg leading-relaxed text-slate-600">{item.text}</p>
              </article>
            ))}
          </div>
        </div>
      </section>

      <section className="py-16">

      </section>

      <section className="bg-orange-600 py-16 text-white">
        <div className="mx-auto w-[min(1280px,calc(100%-2rem))]">
          <h2 className="text-center text-5xl font-black">Get the Payflow apps</h2>
          <p className="mt-4 text-center text-lg text-white/90">Start with the Play Store or App Store — or download directly for other platforms.</p>

          <div className="mt-10 relative overflow-hidden">
            <div className="pointer-events-none absolute inset-y-0 left-0 z-10 w-20 bg-gradient-to-r from-orange-600 to-transparent" />
            <div className="pointer-events-none absolute inset-y-0 right-0 z-10 w-20 bg-gradient-to-l from-orange-600 to-transparent" />

            <div className="app-marquee-track flex w-max gap-6 py-2">
              {[...appStoreOptions, ...appStoreOptions].map((item, index) => (
                <a
                  key={`${item.name}-${index}`}
                  href={item.href}
                  target={item.href.startsWith("/") ? undefined : "_blank"}
                  rel={item.href.startsWith("/") ? undefined : "noopener noreferrer"}
                  className="group flex h-40 w-[16rem] items-center justify-center gap-5 px-4 text-slate-300 transition-transform duration-300 hover:-translate-y-1 md:w-[18rem] md:px-5"
                >
                  <div className="flex h-24 w-24 shrink-0 items-center justify-center">
                    {item.icon}
                  </div>
                  <p className="min-w-0 text-lg font-black leading-tight md:text-xl">{item.name}</p>
                </a>
              ))}
            </div>
          </div>
        </div>
      </section>

      <section className="bg-[#f4f6fb] py-20 md:py-24" id="capabilities">
        <div className="relative mx-auto grid w-[min(1280px,calc(100%-2rem))] gap-12 lg:grid-cols-[minmax(0,0.9fr)_minmax(420px,0.75fr)]">
          <div className="lg:sticky lg:top-28 lg:h-[calc(100vh-7rem)] lg:py-8">
            <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-600">Why choose us</p>
            <h2 className="mt-5 max-w-3xl text-4xl font-black leading-tight text-slate-950 md:text-6xl">
              What makes Payflow different?
            </h2>
            <p className="mt-8 max-w-2xl text-2xl font-black leading-tight text-slate-900">
              Built around customers, merchants, and developers.
            </p>
            <p className="mt-5 max-w-2xl text-lg leading-relaxed text-slate-600 md:text-xl">
              Payflow gives customers free wallet-to-wallet transfers, gives merchants practical ways to collect, and gives developers the APIs and webhooks to build reliable payment experiences. The platform stays simple on the surface while still giving operators the controls they need behind the scenes.
            </p>
            <a href="/login" className="mt-9 inline-flex rounded-full bg-orange-600 px-9 py-4 text-base font-bold text-white shadow-[0_14px_30px_rgba(249,115,22,0.28)]">
              Get started
            </a>
          </div>

          <div className="space-y-10 lg:pb-[12vh]">
            {whyChooseItems.map((item) => (
              <article key={item.title} className="flex min-h-[70vh] items-start justify-center pt-2 lg:items-center">
                <div className="flex min-h-[26rem] w-full max-w-[36rem] flex-col items-center justify-center rounded-[2rem] bg-white px-10 py-14 text-center shadow-[0_35px_90px_rgba(15,23,42,0.10)] md:px-14">
                  <div className="mb-9 flex h-24 w-24 items-center justify-center rounded-2xl bg-orange-50">
                    {renderAudienceIcon(item.iconTitle)}
                  </div>
                  <h3 className="max-w-md text-3xl font-black leading-tight text-slate-950">{item.title}</h3>
                  <p className="mt-6 max-w-md text-lg leading-relaxed text-slate-600">{item.text}</p>
                </div>
              </article>
            ))}
          </div>
        </div>
      </section>

      <section className="bg-[#dfe3f4] py-16">
        <div className="mx-auto grid w-[min(1280px,calc(100%-2rem))] items-start gap-8 md:grid-cols-2">
          <blockquote>
            <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-600">Customer and merchant trust</p>
            <p className="mt-4 text-4xl font-black leading-tight text-slate-900 md:text-5xl">
              "Payflow gave our team a single command center for customer payments, merchant links, and risk incidents."
            </p>
            <footer className="mt-8">
              <p className="text-2xl font-black text-slate-900">Head of Payments</p>
              <p className="text-xl text-slate-600">Merchant platform</p>
            </footer>
          </blockquote>

          <div className="rounded-[3rem]  shadow-xl">
            <div className="">
              <div className="aspect-video w-full overflow-hidden rounded-lg">
                <a
                  href="https://www.youtube.com/watch?v=OyokCk5y7wU"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="block w-full h-full relative focus:outline-none"
                  aria-label="Open video in YouTube"
                >
                  <img
                    src="https://img.youtube.com/vi/OyokCk5y7wU/maxresdefault.jpg"
                    alt="Video preview"
                    className="w-full h-full object-cover border-0"
                  />
                  <div className="absolute inset-0 flex items-center justify-center">
                    <div className="h-16 w-16 rounded-full bg-orange-600/95 flex items-center justify-center shadow-lg">
                      <svg viewBox="0 0 24 24" className="h-8 w-8 text-white" fill="currentColor" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                        <path d="M8 5v14l11-7z" />
                      </svg>
                    </div>
                  </div>
                </a>
              </div>
            </div>
          </div>
        </div>
      </section>

      <FaqSection sections={faqSections} />

      <footer className="bg-slate-50" id="cta">
        <div className="w-full bg-white border-t border-b border-slate-100 py-16">
          <div className="mx-auto w-[min(1280px,calc(100%-2rem))] p-8 md:p-10">
            <div className="grid gap-10 border-b border-slate-100 pb-10 lg:grid-cols-[1.1fr_0.9fr] lg:items-end">
              <div>
                <p className="text-sm font-bold uppercase tracking-[0.22em] text-orange-600">Payflow</p>
                <h2 className="mt-4 text-4xl font-black leading-tight text-slate-900 md:text-5xl">Ready to launch with Payflow?</h2>
                <p className="mt-5 max-w-3xl text-lg leading-relaxed text-slate-700 md:text-xl">
                  Build customer payment flows, merchant collection tools, and operator controls with idempotency, risk checks, reconciliation workflows, and webhook notifications.
                </p>
              </div>

              <div className="flex flex-wrap gap-4 lg:justify-end">
                <a className="rounded-full bg-orange-600 px-8 py-3 text-base font-black text-white transition-transform hover:-translate-y-0.5" href="/signup">Get started</a>
                <a className="rounded-full border border-slate-900 px-8 py-3 text-base font-black text-slate-900 transition-colors hover:bg-slate-50" href="/docs">Developer docs</a>
              </div>
            </div>

            <div className="mt-10 grid gap-10 lg:grid-cols-[1.1fr_1.3fr]">
              <div>
                <p className="max-w-xl text-sm leading-relaxed text-slate-600 md:text-base">
                  A payments platform for customers, merchants, operators, and developers. Secure, observable, and built for production money movement.
                </p>

                <div className="mt-8 flex flex-wrap gap-4">
                  {socialLinks.map((social) => (
                    <a
                      key={social.name}
                      href={social.href}
                      target="_blank"
                      rel="noopener noreferrer"
                      aria-label={social.name}
                      className="inline-flex h-11 w-11 items-center justify-center rounded-full text-slate-700 border border-slate-100 transition-colors hover:bg-slate-50 hover:text-orange-600"
                    >
                      {social.icon}
                    </a>
                  ))}
                </div>
              </div>

              <div className="grid gap-8 sm:grid-cols-3">
                {footerColumns.map((column) => (
                  <div key={column.title}>
                    <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-600">{column.title}</p>
                    <ul className="mt-4 space-y-3 text-sm text-slate-700 md:text-base">
                      {column.links.map((link) => (
                        <li key={link}>
                          <a href="#" className="transition-colors hover:text-orange-600">{link}</a>
                        </li>
                      ))}
                    </ul>
                  </div>
                ))}
              </div>
            </div>

            <div className="mt-10 flex flex-col gap-4 border-t border-slate-100 pt-6 text-sm text-slate-500 md:flex-row md:items-center md:justify-between">
              <p>© 2026 Payflow. All rights reserved.</p>
              <div className="flex flex-wrap gap-4">
                <a href="#faq" className="hover:text-orange-600 text-slate-600">FAQ</a>
                <a href="#capabilities" className="hover:text-orange-600 text-slate-600">Capabilities</a>
                <a href="#audiences" className="hover:text-orange-600 text-slate-600">Who it serves</a>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </main>
  );
}
