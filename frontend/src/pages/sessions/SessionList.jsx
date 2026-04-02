import { useState, useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";
import { useAuth } from "../../hooks/useAuth";
import { formatDateMedium } from "../../utils/dateUtils";
import Pagination from "../../components/Pagination.jsx";

export default function SessionList() {
  const navigate = useNavigate();
  const { preferences } = useAuth();
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(15);

  const { data, isLoading } = useQuery({
    queryKey: ["sessions", page, pageSize],
    queryFn: () =>
      api.get(`/sessions?page=${page}&size=${pageSize}`).then((r) => r.data),
  });

  // search within current page only
  const filtered = useMemo(() => {
    if (!data?.content) return [];
    if (!search) return data.content;
    const q = search.toLowerCase();
    return data.content.filter(
      (s) =>
        s.mainThemes?.toLowerCase().includes(q) ||
        s.patients?.some((p) =>
          `${p.firstName} ${p.lastName}`.toLowerCase().includes(q),
        ),
    );
  }, [data, search]);

  const handleSearch = (val) => {
    setSearch(val);
    setPage(0);
  };

  if (isLoading) return <div className="text-gray-400">Loading...</div>;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Sessions</h1>
          <p className="text-gray-400 text-sm mt-1">
            {data?.totalElements} total
          </p>
        </div>
        <button
          onClick={() => navigate("/sessions/new")}
          className="bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold px-4 py-2 rounded-lg transition"
        >
          + New Session
        </button>
      </div>

      <div className="mb-4">
        <input
          type="text"
          placeholder="Search by patient or theme (current page)..."
          value={search}
          onChange={(e) => handleSearch(e.target.value)}
          className="w-full bg-gray-900 border border-gray-800 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition text-sm"
        />
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-800">
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Date
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Patient(s)
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Main Themes
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Status
              </th>
              <th className="px-6 py-3"></th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                  No sessions found
                </td>
              </tr>
            ) : (
              filtered.map((session) => (
                <tr
                  key={session.id}
                  className="border-b border-gray-800 hover:bg-gray-800 transition cursor-pointer"
                  onClick={() => navigate(`/sessions/${session.id}`)}
                >
                  <td className="px-6 py-4 text-white whitespace-nowrap">
                    {formatDateMedium(
                      session.scheduledAt,
                      preferences?.dateFormat,
                      preferences?.uiLanguage,
                    )}
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex flex-col gap-1">
                      {session.patients?.map((p) => (
                        <span
                          key={p.id}
                          onClick={(e) => {
                            e.stopPropagation();
                            navigate(`/patients/${p.id}`);
                          }}
                          className="text-indigo-400 hover:text-indigo-300 cursor-pointer"
                        >
                          {p.firstName} {p.lastName}
                        </span>
                      ))}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-gray-400 max-w-xs truncate">
                    {session.mainThemes || "—"}
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      <span
                        className={`text-xs font-semibold px-2 py-1 rounded-full ${
                          session.status === "COMPLETED"
                            ? "bg-green-900 text-green-400"
                            : session.status === "SCHEDULED"
                              ? "bg-blue-900 text-blue-400"
                              : "bg-gray-700 text-gray-400"
                        }`}
                      >
                        {session.status}
                      </span>
                      {session.isRelevant && (
                        <span className="text-yellow-400 text-xs">⭐</span>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <span className="text-indigo-400 hover:text-indigo-300 text-xs font-medium">
                      View →
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        <Pagination
          data={data}
          setPage={setPage}
          pageSize={pageSize}
          setPageSize={setPageSize}
        />
      </div>
    </div>
  );
}
