import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./hooks/useAuth";
import Layout from "./components/layout/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import PatientList from "./pages/patients/PatientList";
import PatientForm from "./pages/patients/PatientForm";
import PatientProfile from "./pages/patients/PatientProfile";
import SessionList from "./pages/sessions/SessionList";
import SessionForm from "./pages/sessions/SessionForm";
import SessionDetail from "./pages/sessions/SessionDetail";
import PaymentList from "./pages/payments/PaymentList";
import Debts from "./pages/queries/Debts";
import Birthdays from "./pages/queries/Birthdays";

function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div>Loading...</div>;
  if (!user) return <Navigate to="/login" />;
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="patients" element={<PatientList />} />
        <Route path="patients/new" element={<PatientForm />} />
        <Route path="patients/:id" element={<PatientProfile />} />
        <Route path="sessions" element={<SessionList />} />
        <Route path="sessions/new" element={<SessionForm />} />
        <Route path="sessions/:id" element={<SessionDetail />} />
        <Route path="payments" element={<PaymentList />} />
        <Route path="queries/debts" element={<Debts />} />
        <Route path="queries/birthdays" element={<Birthdays />} />
      </Route>
    </Routes>
  );
}
