import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import api from "../../api/axios";
import { useAuth } from "../../hooks/useAuth";
import { formatTimestamp } from "../../utils/dateUtils";

const ENTITY_TYPES = ["Patient", "Session", "Payment", "User"];

const ACTION_COLORS = {
  VIEW: "text-gray-400",
  CREATE: "text-green-400",
  UPDATE: "text-blue-400",
  DELETE: "text-red-400",
  LOGIN: "text-indigo-400",
  FLAG: "text-red-400",
  CLEAR_FLAG: "text-green-400",
  ASSIGN: "text-blue-400",
  UNASSIGN: "text-yellow-400",
  MARK_PAID: "text-green-400",
  WRITE_OFF: "text-red-400",
  REACTIVATE: "text-yellow-400",
};

export default function AuditLog() {
  const { preferences } = useAuth();
  const [entityType, setEntityType] = useState("");
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery({
    queryKey: ["audit-logs", entityType, page],
    queryFn: () => {
      const params = new URLSearchParams({ page, size: 50 });
      if (entityType) params.append("entityType", entityType);
      return api.get(`/audit?${params}`).then((r) => r.data);
    },
  });

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Audit Log</h1>
          <p className="text-gray-400 text-sm mt-1">
            Record of all actions performed in your organization
          </p>
        </div>

        {/* Filter */}
        <select
          value={entityType}
          onChange={(e) => {
            setEntityType(e.target.value);
            setPage(0);
          }}
          className="bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2 text-sm focus:outline-none focus:border-indigo-500"
        >
          <option value="">All types</option>
          {ENTITY_TYPES.map((t) => (
            <option key={t} value={t}>
              {t}
            </option>
          ))}
        </select>
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-800">
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                When
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                User
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Action
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Entity
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Details
              </th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr>
                <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                  Loading...
                </td>
              </tr>
            ) : data?.content?.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                  No audit logs yet
                </td>
              </tr>
            ) : (
              data?.content?.map((log) => (
                <tr
                  key={log.id}
                  className="border-b border-gray-800 last:border-0 hover:bg-gray-800 transition"
                >
                  <td className="px-6 py-3 text-gray-400 text-xs whitespace-nowrap">
                    {formatTimestamp(
                      log.createdAt,
                      preferences?.dateFormat,
                      preferences?.timeFormat,
                    )}
                  </td>
                  <td className="px-6 py-3 text-white text-xs">
                    {log.userName}
                  </td>
                  <td className="px-6 py-3">
                    <span
                      className={`text-xs font-semibold ${
                        ACTION_COLORS[log.action] || "text-gray-400"
                      }`}
                    >
                      {log.action}
                    </span>
                  </td>
                  <td className="px-6 py-3 text-gray-400 text-xs">
                    {log.entityType} #{log.entityId}
                  </td>
                  <td className="px-6 py-3 text-gray-500 text-xs">
                    {log.details || "—"}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        {/* Pagination */}
        {data && data.totalPages > 1 && (
          <div className="flex items-center justify-between px-6 py-4 border-t border-gray-800">
            <p className="text-gray-500 text-xs">
              Showing {data.number * 50 + 1}–
              {Math.min((data.number + 1) * 50, data.totalElements)} of{" "}
              {data.totalElements} entries
            </p>
            <div className="flex gap-2">
              <button
                onClick={() => setPage((p) => p - 1)}
                disabled={data.first}
                className="px-3 py-1.5 bg-gray-800 text-gray-400 rounded-lg text-xs disabled:opacity-50 hover:bg-gray-700 transition"
              >
                ← Previous
              </button>
              <button
                onClick={() => setPage((p) => p + 1)}
                disabled={data.last}
                className="px-3 py-1.5 bg-gray-800 text-gray-400 rounded-lg text-xs disabled:opacity-50 hover:bg-gray-700 transition"
              >
                Next →
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
