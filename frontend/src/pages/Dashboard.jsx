import { useQuery } from "@tanstack/react-query";
import api from "../api/axios";
import { useAuth } from "../hooks/useAuth";

function StatCard({ title, value, icon, color }) {
  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
      <div className="flex items-center justify-between mb-3">
        <span className="text-gray-400 text-sm font-medium">{title}</span>
        <span className={`text-2xl`}>{icon}</span>
      </div>
      <p className={`text-4xl font-bold ${color}`}>{value ?? "..."}</p>
    </div>
  );
}

export default function Dashboard() {
  const { user } = useAuth();

  const { data: stats } = useQuery({
    queryKey: ["dashboard-stats"],
    queryFn: () => api.get("/dashboard/stats").then((r) => r.data),
  });

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-white">
          Welcome back, {user?.firstName} 👋
        </h1>
        <p className="text-gray-400 mt-1">
          Here's what's going on in your practice
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <StatCard
          title="Active Patients"
          value={stats?.activePatients}
          icon="👤"
          color="text-indigo-400"
        />
        <StatCard
          title="Sessions This Month"
          value={stats?.sessionsThisMonth}
          icon="📋"
          color="text-green-400"
        />
        <StatCard
          title="Pending Payments"
          value={stats?.pendingPayments}
          icon="💸"
          color="text-yellow-400"
        />
        <StatCard
          title="Upcoming Sessions"
          value={stats?.upcomingSessions}
          icon="📅"
          color="text-blue-400"
        />
      </div>
    </div>
  );
}
