import { useQuery } from "@tanstack/react-query";
import api from "../api/axios";
import { useAuth } from "../hooks/useAuth";
import { useState } from "react";

const MONTH_NAMES = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];

function StatCard({ title, value, icon, color, subtitle }) {
  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
      <div className="flex items-center justify-between mb-3">
        <span className="text-gray-400 text-sm font-medium">{title}</span>
        <span className="text-2xl">{icon}</span>
      </div>
      <p className={`text-4xl font-bold ${color}`}>{value ?? "..."}</p>
      {subtitle && <p className="text-gray-600 text-xs mt-1">{subtitle}</p>}
    </div>
  );
}

export default function Dashboard() {
  const { user } = useAuth();

  const [birthdayDismissed, setBirthdayDismissed] = useState(false);

  const { data: stats } = useQuery({
    queryKey: ["dashboard-stats"],
    queryFn: () => api.get("/dashboard/stats").then((r) => r.data),
  });

  const { data: birthdayData } = useQuery({
    queryKey: ["birthdays", false],
    queryFn: () =>
      api.get("/patients/birthdays?includeInactive=false").then((r) => r.data),
  });

  const todayBirthdays = birthdayData?.filter((b) => b.daysUntil === 0) || [];

  const { data: snapshots } = useQuery({
    queryKey: ["dashboard-snapshots"],
    queryFn: () => api.get("/dashboard/snapshots").then((r) => r.data),
  });

  const currentMonth = MONTH_NAMES[new Date().getMonth()];

  return (
    <div>
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-white">
          Welcome back, {user?.firstName} 👋
        </h1>
        <p className="text-gray-400 mt-1">
          Here's what's going on in your practice
        </p>
      </div>

      {/* Birthday banner */}
      {todayBirthdays?.length > 0 && !birthdayDismissed && (
        <div className="bg-yellow-900/30 border border-yellow-800 rounded-xl px-5 py-4 mb-6 flex items-center justify-between">
          <div>
            <p className="text-yellow-400 font-semibold">
              🎂 Birthday{todayBirthdays.length > 1 ? "s" : ""} today!
            </p>
            <p className="text-yellow-300 text-sm mt-0.5">
              {todayBirthdays.map((b) => b.patientName).join(", ")}
            </p>
          </div>
          <button
            onClick={() => setBirthdayDismissed(true)}
            className="text-yellow-600 hover:text-yellow-400 text-sm ml-4"
          >
            ✕
          </button>
        </div>
      )}

      {/* Current month stats */}
      <h2 className="text-white font-semibold mb-4">{currentMonth} Overview</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-5 mb-10">
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
          title="Upcoming Sessions"
          value={stats?.upcomingSessions}
          icon="📅"
          color="text-blue-400"
          subtitle="Next 7 days"
        />
        <StatCard
          title="Collected This Month"
          value={
            stats?.collectedThisMonth != null
              ? `${stats.collectedThisMonth}`
              : "0"
          }
          icon="💰"
          color="text-emerald-400"
        />
        <StatCard
          title="Pending Payments"
          value={stats?.pendingPayments}
          icon="💸"
          color="text-yellow-400"
        />
        <StatCard
          title="Birthdays This Week" // ← was "Birthdays This Month"
          value={stats?.birthdaysThisMonth}
          icon="🎂"
          color="text-pink-400"
          subtitle="Next 7 days"
        />
      </div>

      {/* Monthly history */}
      {snapshots?.length > 0 && (
        <div>
          <h2 className="text-white font-semibold mb-4">Monthly History</h2>
          <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-800">
                  <th className="text-left text-gray-400 font-medium px-6 py-3">
                    Month
                  </th>
                  <th className="text-right text-gray-400 font-medium px-6 py-3">
                    Active Patients
                  </th>
                  <th className="text-right text-gray-400 font-medium px-6 py-3">
                    Sessions
                  </th>
                  <th className="text-right text-gray-400 font-medium px-6 py-3">
                    Completed
                  </th>
                  <th className="text-right text-gray-400 font-medium px-6 py-3">
                    Collected
                  </th>
                  <th className="text-right text-gray-400 font-medium px-6 py-3">
                    Pending
                  </th>
                </tr>
              </thead>
              <tbody>
                {snapshots.map((s, i) => (
                  <tr
                    key={i}
                    className="border-b border-gray-800 last:border-0 hover:bg-gray-800 transition"
                  >
                    <td className="px-6 py-4 text-white font-medium">
                      {MONTH_NAMES[s.month - 1]} {s.year}
                    </td>
                    <td className="px-6 py-4 text-gray-300 text-right">
                      {s.activePatients}
                    </td>
                    <td className="px-6 py-4 text-gray-300 text-right">
                      {s.totalSessions}
                    </td>
                    <td className="px-6 py-4 text-gray-300 text-right">
                      {s.completedSessions}
                    </td>
                    <td className="px-6 py-4 text-emerald-400 text-right font-medium">
                      {s.totalCollected}
                    </td>
                    <td className="px-6 py-4 text-yellow-400 text-right">
                      {s.totalPending}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {snapshots?.length === 0 && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 text-center">
          <p className="text-gray-500 text-sm">
            Monthly history will appear here after the first month completes.
          </p>
        </div>
      )}
    </div>
  );
}
