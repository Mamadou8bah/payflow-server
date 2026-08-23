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
  { value: "MARKET_STALL", label: "Market stall" },
  { value: "RESTAURANT_FOOD", label: "Restaurant / food" },
  { value: "SALON_BEAUTY", label: "Salon / beauty" },
  { value: "PHARMACY_HEALTH", label: "Pharmacy / health" },
  { value: "HARDWARE_BUILDING", label: "Hardware" },
  { value: "TRANSPORT_LOGISTICS", label: "Transport" },
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
  { id: 1, title: "Phone" },
  { id: 2, title: "Verify" },
  { id: 3, title: "Business" },
  { id: 4, title: "Owner" },
  { id: 5, title: "Account" },
] as const;

export function formatGambianPhone(digits: string): string {
  const clean = digits.replace(/\D/g, "").slice(0, 7);
  if (clean.length <= 3) return clean;
  return `${clean.slice(0, 3)} ${clean.slice(3)}`;
}

export function toE164Phone(localDigits: string): string {
  return `+220${localDigits.replace(/\D/g, "")}`;
}
