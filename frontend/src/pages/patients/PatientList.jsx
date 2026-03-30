import { useState, useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";
import { calculateAge } from "../../utils/dateUtils";
import { useAuth } from "../../hooks/useAuth";

export default function PatientList() {
  const navigate = useNavigate();
  const [showInactive, setShowInactive] = useState(false);
  const { user } = useAuth();

  const { data: patients, isLoading } = useQuery({
    queryKey: ["patients"],
    queryFn: () => api.get("/patients").then((r) => r.data),
  });

  const { data: users } = useQuery({
    queryKey: ["users"],
    queryFn: () => api.get("/users").then((r) => r.data),
    enabled: user?.isAdmin, // only fetch if admin, therapists don't need this
  });

  const activeTherapists =
    users?.filter((u) => u.isTherapist && u.isActive) || [];
  const showTherapistColumn = user?.isAdmin && activeTherapists.length > 1;

  const filtered = useMemo(() => {
    if (!patients) return [];
    return patients
      .filter((p) => (showInactive ? true : p.isActive))
      .sort((a, b) => {
        // active first, then alphabetical
        if (a.isActive !== b.isActive) return a.isActive ? -1 : 1;
        const nameA = `${a.firstName} ${a.lastName}`.toLowerCase();
        const nameB = `${b.firstName} ${b.lastName}`.toLowerCase();
        return nameA.localeCompare(nameB);
      });
  }, [patients, showInactive]);

  if (isLoading) return <div className="text-gray-400">Loading...</div>;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Patients</h1>
          <p className="text-gray-400 text-sm mt-1">
            {filtered.length} {showInactive ? "total" : "active"}
          </p>
        </div>
        <div className="flex items-center gap-3">
          <label className="flex items-center gap-2 text-sm text-gray-400 cursor-pointer select-none">
            <input
              type="checkbox"
              checked={showInactive}
              onChange={(e) => setShowInactive(e.target.checked)}
              className="accent-indigo-500"
            />
            Show inactive
          </label>
          <button
            onClick={() => navigate("/patients/new")}
            className="bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold px-4 py-2 rounded-lg transition"
          >
            + New Patient
          </button>
        </div>
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-800">
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Name
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Short Name
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Age
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Gender
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Status
              </th>
              {showTherapistColumn && (
                <th className="text-left text-gray-400 font-medium px-6 py-3">
                  Therapist
                </th>
              )}
              <th className="px-6 py-3"></th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((patient) => (
              <tr
                key={patient.id}
                className={`border-b border-gray-800 hover:bg-gray-800 transition cursor-pointer ${
                  !patient.isActive ? "opacity-50" : ""
                }`}
                onClick={() => navigate(`/patients/${patient.id}`)}
              >
                <td className="px-6 py-4 text-white font-medium">
                  <div className="flex items-center gap-2">
                    {patient.hasDebtFlag && (
                      <span
                        title={patient.debtFlagNote || "Has outstanding debt"}
                      >
                        🚩
                      </span>
                    )}
                    {patient.firstName} {patient.lastName}
                  </div>
                </td>
                <td className="px-6 py-4 text-gray-400">
                  {patient.shortName || "—"}
                </td>
                <td className="px-6 py-4 text-gray-400">
                  {patient.dateOfBirth
                    ? calculateAge(patient.dateOfBirth)
                    : "—"}
                </td>
                <td className="px-6 py-4 text-gray-400">
                  {patient?.gender
                    ? patient.gender === "FEMALE"
                      ? "F"
                      : patient.gender === "MALE"
                        ? "M"
                        : "?"
                    : "—"}
                </td>
                <td className="px-6 py-4">
                  <span
                    className={`text-xs font-semibold px-2 py-1 rounded-full ${
                      patient.isActive
                        ? "bg-green-900 text-green-400"
                        : "bg-gray-800 text-gray-500"
                    }`}
                  >
                    {patient.isActive ? "Active" : "Inactive"}
                  </span>
                </td>
                {showTherapistColumn && (
                  <td className="px-6 py-4 text-gray-400 text-sm">
                    {patient.assignedTherapistName || (
                      <span className="text-gray-600">Unassigned</span>
                    )}
                  </td>
                )}
                <td className="px-6 py-4 text-right">
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      navigate(`/patients/${patient.id}`);
                    }}
                    className="text-indigo-400 hover:text-indigo-300 text-xs font-medium"
                  >
                    View →
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
