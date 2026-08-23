export type DemoRole = "admin" | "merchant" | "developer" | "customer";

export type DemoUser = {
  email: string;
  password: string;
  role: DemoRole;
  name: string;
  phone?: string;
};

export const DEMO_PASSWORD = "demo123";

export const demoUsers: DemoUser[] = [
  {
    email: "admin@payflow.local",
    password: DEMO_PASSWORD,
    role: "admin",
    name: "Payflow Admin",
  },
  {
    email: "merchant@payflow.local",
    password: DEMO_PASSWORD,
    role: "merchant",
    name: "Acme Merchant",
  },
  {
    email: "developer@payflow.local",
    password: DEMO_PASSWORD,
    role: "developer",
    name: "Payflow Developer",
  },
  {
    email: "customer@payflow.local",
    password: DEMO_PASSWORD,
    role: "customer",
    name: "Fatou Jallow",
    phone: "+220 712 3456",
  },
];

export function findDemoUser(email: string, password: string): DemoUser | null {
  const normalizedEmail = email.trim().toLowerCase();
  return (
    demoUsers.find((user) => user.email === normalizedEmail && user.password === password) ?? null
  );
}

export function getDashboardPath(role: DemoRole): string {
  if (role === "customer") return "/app";
  return "/dashboard";
}
