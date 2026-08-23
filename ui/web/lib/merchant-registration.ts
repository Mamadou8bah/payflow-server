export const GAMBIA_REGIONS = [
  { value: "BANJUL", label: "Banjul" },
  { value: "KANIFING", label: "Kanifing Municipal Council" },
  { value: "BRIKAMA", label: "West Coast Region (Brikama)" },
  { value: "MANSAKONKO", label: "Lower River Region (Mansakonko)" },
  { value: "KEREWAN", label: "North Bank Region (Kerewan)" },
  { value: "KUNTAUR", label: "Central River Region (Kuntaur)" },
  { value: "BASSE", label: "Upper River Region (Basse)" },
] as const;

export const BUSINESS_CATEGORIES = [
  { value: "RETAIL_SHOP", label: "Retail shop" },
  { value: "MARKET_STALL", label: "Market stall / lumo trader" },
  { value: "RESTAURANT_FOOD", label: "Restaurant / food vendor" },
  { value: "SALON_BEAUTY", label: "Salon / beauty services" },
  { value: "PHARMACY_HEALTH", label: "Pharmacy / health" },
  { value: "HARDWARE_BUILDING", label: "Hardware / building materials" },
  { value: "TRANSPORT_LOGISTICS", label: "Transport / logistics" },
  { value: "PROFESSIONAL_SERVICES", label: "Professional services" },
  { value: "ONLINE_ECOMMERCE", label: "Online / e-commerce" },
  { value: "OTHER", label: "Other" },
] as const;

export const ID_TYPES = [
  { value: "GAMBIAN_NATIONAL_ID", label: "Gambian National ID" },
  { value: "ECOWAS_PASSPORT", label: "ECOWAS passport" },
  { value: "DRIVERS_LICENSE", label: "Driver's license" },
  { value: "VOTERS_CARD", label: "Voter's card" },
] as const;

export const REGISTRATION_STEPS = [
  { id: 1, title: "Phone", hint: "Verify your Gambian mobile number" },
  { id: 2, title: "Business", hint: "Shop or company details" },
  { id: 3, title: "Owner", hint: "Identity of the person registering" },
  { id: 4, title: "Account", hint: "Email and password for login" },
] as const;

export type RegistrationStep = 1 | 2 | 3 | 4 | 5;

export function formatGambianPhone(digits: string): string {
  const clean = digits.replace(/\D/g, "").slice(0, 7);
  if (clean.length <= 3) return clean;
  return `${clean.slice(0, 3)} ${clean.slice(3)}`;
}

export function toE164Phone(localDigits: string): string {
  const digits = localDigits.replace(/\D/g, "");
  return `+220${digits}`;
}
