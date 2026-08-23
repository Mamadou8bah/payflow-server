import { DemoAuthGate } from "../components/demo-auth-gate";
import { CustomerAppView } from "./customer-app-view";

export default function CustomerAppPage() {
  return (
    <DemoAuthGate role="customer">
      <CustomerAppView />
    </DemoAuthGate>
  );
}
