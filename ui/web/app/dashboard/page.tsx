import { DemoAuthGate } from "../components/demo-auth-gate";
import { DashboardView } from "./dashboard-view";

export default function DashboardPage() {
  return (
    <DemoAuthGate>
      <DashboardView />
    </DemoAuthGate>
  );
}
