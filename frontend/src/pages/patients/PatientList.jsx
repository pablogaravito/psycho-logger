import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";
import { useAuth } from "../../hooks/useAuth";
import { calculateAge } from "../../utils/dateUtils";
import Pagination from "../../components/Pagination.jsx";

export default function PatientList() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [showInactive, setShowInactive] = useState(false);
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery({
    queryKey: ["patients", page, showInactive],
    queryFn: () =>
      api
        .get(`/patients?page=${page}&size=15&showInactive=${showInactive}`)
        .then((r) => r.data),
  });

  const { data: users } = useQuery({
    queryKey: ["users"],
    queryFn: () => api.get("/users").then((r) => r.data),
    enabled: user?.isAdmin,
  });

  const activeTherapists =
    users?.filter((u) => u.isTherapist && u.isActive) || [];
  const showTherapistColumn = user?.isAdmin && activeTherapists.length > 1;

  const handleShowInactive = (val) => {
    setShowInactive(val);
    setPage(0);
  };

  if (isLoading) return <div className="text-gray-400">Loading...</div>;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Patients</h1>
          <p className="text-gray-400 text-sm mt-1">
            {data?.totalElements} {showInactive ? "total" : "active"}
          </p>
        </div>
        <div className="flex items-center gap-3">
          <label className="flex items-center gap-2 text-sm text-gray-400 cursor-pointer select-none">
            <input
              type="checkbox"
              checked={showInactive}
              onChange={(e) => handleShowInactive(e.target.checked)}
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
            {data?.content?.map((patient) => (
              <tr
                key={patient.id}
                className={`border-b border-gray-800 hover:bg-gray-800 transition cursor-pointer ${
                  !patient.isActive ? "opacity-50" : ""
                }`}
                onClick={() => navigate(`/patients/${patient.id}`)}
              >
                <td className="px-6 py-4 text-white font-medium">
                  <div className="flex items-center gap-2">
                    {patient.hasDebtFlag && <span>🚩</span>}
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
                  <span className="text-indigo-400 hover:text-indigo-300 text-xs font-medium">
                    View →
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <Pagination data={data} setPage={setPage} />
      </div>
    </div>
  );
}
