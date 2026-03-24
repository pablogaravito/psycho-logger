import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";

function getDaysLabel(daysUntil) {
  if (daysUntil === 0) return { label: "Today! 🎂", color: "text-yellow-400" };
  if (daysUntil < 0)
    return {
      label: `${Math.abs(daysUntil)} day${Math.abs(daysUntil) !== 1 ? "s" : ""} ago`,
      color: "text-gray-500",
    };
  if (daysUntil === 1) return { label: "Tomorrow", color: "text-green-400" };
  return { label: `In ${daysUntil} days`, color: "text-indigo-400" };
}

export default function Birthdays() {
  const navigate = useNavigate();

  const { data: userSettings } = useQuery({
    queryKey: ["user-settings"],
    queryFn: () => api.get("/settings/user").then((r) => r.data),
  });

  const includeInactive = userSettings?.showInactiveBirthdays || false;

  const { data: birthdays, isLoading } = useQuery({
    queryKey: ["birthdays", includeInactive],
    queryFn: () =>
      api
        .get(`/patients/birthdays?includeInactive=${includeInactive}`)
        .then((r) => r.data),
    enabled: userSettings !== undefined,
  });

  const todayBirthdays = birthdays?.filter((b) => b.daysUntil === 0) || [];
  const pastBirthdays = birthdays?.filter((b) => b.daysUntil < 0) || [];
  const upcomingBirthdays = birthdays?.filter((b) => b.daysUntil > 0) || [];

  if (isLoading) return <div className="text-gray-400">Loading...</div>;

  return (
    <div className="max-w-2xl mx-auto">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">Birthdays</h1>
        <p className="text-gray-400 text-sm mt-1">
          7 days back · 21 days ahead
        </p>
      </div>

      {birthdays?.length === 0 ? (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-8 text-center">
          <p className="text-gray-500">No birthdays in this window</p>
        </div>
      ) : (
        <div className="space-y-6">
          {/* Today */}
          {todayBirthdays.length > 0 && (
            <div>
              <h2 className="text-yellow-400 font-semibold mb-3">🎂 Today</h2>
              <div className="space-y-2">
                {todayBirthdays.map((b) => (
                  <BirthdayCard
                    key={b.patientId}
                    birthday={b}
                    navigate={navigate}
                  />
                ))}
              </div>
            </div>
          )}

          {/* Upcoming */}
          {upcomingBirthdays.length > 0 && (
            <div>
              <h2 className="text-white font-semibold mb-3">Upcoming</h2>
              <div className="space-y-2">
                {upcomingBirthdays.map((b) => (
                  <BirthdayCard
                    key={b.patientId}
                    birthday={b}
                    navigate={navigate}
                  />
                ))}
              </div>
            </div>
          )}

          {/* Recent — past */}
          {pastBirthdays.length > 0 && (
            <div>
              <h2 className="text-gray-500 font-semibold mb-3">Recent</h2>
              <div className="space-y-2">
                {pastBirthdays.map((b) => (
                  <BirthdayCard
                    key={b.patientId}
                    birthday={b}
                    navigate={navigate}
                  />
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

function BirthdayCard({ birthday, navigate }) {
  const { label, color } = getDaysLabel(birthday.daysUntil);

  return (
    <div
      className={`flex items-center justify-between px-5 py-4 rounded-xl border cursor-pointer hover:bg-gray-800 transition ${
        birthday.daysUntil === 0
          ? "bg-yellow-900/20 border-yellow-800"
          : "bg-gray-900 border-gray-800"
      }`}
      onClick={() => navigate(`/patients/${birthday.patientId}`)}
    >
      <div>
        <p className="text-white font-medium">
          {birthday.patientName}
          {birthday.shortName && (
            <span className="text-gray-500 text-sm ml-1">
              ({birthday.shortName})
            </span>
          )}
          {!birthday.isActive && (
            <span className="ml-2 text-xs text-gray-600 bg-gray-800 px-2 py-0.5 rounded-full">
              Inactive
            </span>
          )}
        </p>
        <p className="text-gray-400 text-xs mt-0.5">
          {new Date(birthday.dateOfBirth).toLocaleDateString("es-PE", {
            month: "long",
            day: "numeric",
          })}{" "}
          · Turns {birthday.age}
        </p>
      </div>
      <span className={`text-sm font-medium ${color}`}>{label}</span>
    </div>
  );
}
