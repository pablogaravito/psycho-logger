import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";
import { useAuth } from "../../hooks/useAuth";
import { formatDateShort } from "../../utils/dateUtils";
import Pagination from "../../components/Pagination.jsx";

const STATUS_COLORS = {
  PAID: "bg-green-900 text-green-400",
  PENDING: "bg-yellow-900 text-yellow-400",
  WRITTEN_OFF: "bg-red-900 text-red-400",
  PARTIAL: "bg-blue-900 text-blue-400",
  REFUNDED: "bg-gray-700 text-gray-400",
};

export default function PaymentList() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { preferences } = useAuth();
  const [filter, setFilter] = useState("PENDING");
  const [page, setPage] = useState(0);
  const [editingId, setEditingId] = useState(null);
  const [editAmount, setEditAmount] = useState("");

  const { data, isLoading } = useQuery({
    queryKey: ["payments", filter, page],
    queryFn: () => {
      const params = new URLSearchParams({ page, size: 15 });
      if (filter !== "ALL") params.append("status", filter);
      return api.get(`/payments?${params}`).then((r) => r.data);
    },
  });

  const markPaidMutation = useMutation({
    mutationFn: ({ id, amount }) =>
      api.put(`/payments/${id}`, {
        status: "PAID",
        amount: parseFloat(amount),
        paidAt: new Date().toISOString(),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries(["payments"]);
      queryClient.invalidateQueries(["dashboard-stats"]);
      setEditingId(null);
    },
  });

  const writeOffMutation = useMutation({
    mutationFn: ({ id, amount }) =>
      api.put(`/payments/${id}`, {
        status: "WRITTEN_OFF",
        amount: parseFloat(amount),
        paidAt: new Date().toISOString(),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries(["payments"]);
      queryClient.invalidateQueries(["dashboard-stats"]);
      setEditingId(null);
    },
  });

  const reactivateMutation = useMutation({
    mutationFn: (id) =>
      api.put(`/payments/${id}`, {
        status: "PENDING",
        paidAt: null,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries(["payments"]);
      queryClient.invalidateQueries(["dashboard-stats"]);
    },
  });

  const startEditing = (payment) => {
    setEditingId(payment.id);
    setEditAmount(payment.amount?.toString() || "");
  };

  const handleFilterChange = (f) => {
    setFilter(f);
    setPage(0);
  };

  if (isLoading) return <div className="text-gray-400">Loading...</div>;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Payments</h1>
          <p className="text-gray-400 text-sm mt-1">
            {data?.totalElements}{" "}
            {filter === "ALL" ? "total" : filter.toLowerCase()}
          </p>
        </div>
      </div>

      {/* Filter tabs */}
      <div className="flex gap-2 mb-6">
        {["PENDING", "PAID", "WRITTEN_OFF", "ALL"].map((f) => (
          <button
            key={f}
            onClick={() => handleFilterChange(f)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
              filter === f
                ? "bg-indigo-600 text-white"
                : "bg-gray-800 text-gray-400 hover:text-white"
            }`}
          >
            {f === "ALL" ? "All" : f.charAt(0) + f.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-800">
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Patient
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Session
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Amount
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Status
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Paid At
              </th>
              <th className="px-6 py-3"></th>
            </tr>
          </thead>
          <tbody>
            {data?.content?.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-6 py-8 text-center text-gray-500">
                  No {filter === "ALL" ? "" : filter.toLowerCase()} payments
                  found
                </td>
              </tr>
            ) : (
              data?.content?.map((payment) => (
                <tr
                  key={payment.id}
                  className="border-b border-gray-800 last:border-0 hover:bg-gray-800 transition"
                >
                  <td className="px-6 py-4">
                    <span
                      onClick={() => navigate(`/patients/${payment.patientId}`)}
                      className="text-indigo-400 hover:text-indigo-300 cursor-pointer font-medium"
                    >
                      {payment.patientName}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-gray-400">
                    {payment.sessionId ? (
                      <span
                        onClick={() =>
                          navigate(`/sessions/${payment.sessionId}`)
                        }
                        className="text-indigo-400 hover:text-indigo-300 cursor-pointer text-xs"
                      >
                        View session →
                      </span>
                    ) : (
                      "—"
                    )}
                  </td>
                  <td className="px-6 py-4">
                    {editingId === payment.id ? (
                      <input
                        type="number"
                        value={editAmount}
                        onChange={(e) => setEditAmount(e.target.value)}
                        className="w-24 bg-gray-700 border border-indigo-500 text-white rounded px-2 py-1 text-sm focus:outline-none"
                        autoFocus
                      />
                    ) : (
                      <span className="text-white font-medium">
                        {payment.amount} {payment.currency}
                      </span>
                    )}
                  </td>
                  <td className="px-6 py-4">
                    <span
                      className={`text-xs font-semibold px-2 py-1 rounded-full ${
                        STATUS_COLORS[payment.status] ||
                        "bg-gray-700 text-gray-400"
                      }`}
                    >
                      {payment.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-gray-400 text-xs">
                    {payment.paidAt
                      ? formatDateShort(payment.paidAt, preferences?.dateFormat)
                      : "—"}
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2 justify-end">
                      {payment.status === "PENDING" && (
                        <>
                          {editingId === payment.id ? (
                            <>
                              <button
                                onClick={() =>
                                  markPaidMutation.mutate({
                                    id: payment.id,
                                    amount: editAmount,
                                  })
                                }
                                disabled={markPaidMutation.isPending}
                                className="bg-green-700 hover:bg-green-600 text-white text-xs font-medium px-3 py-1.5 rounded-lg transition"
                              >
                                ✓ Paid
                              </button>
                              <button
                                onClick={() =>
                                  writeOffMutation.mutate({
                                    id: payment.id,
                                    amount: editAmount,
                                  })
                                }
                                disabled={writeOffMutation.isPending}
                                className="bg-red-700 hover:bg-red-600 text-white text-xs font-medium px-3 py-1.5 rounded-lg transition"
                              >
                                ✓ Write Off
                              </button>
                              <button
                                onClick={() => setEditingId(null)}
                                className="bg-gray-700 hover:bg-gray-600 text-white text-xs font-medium px-3 py-1.5 rounded-lg transition"
                              >
                                Cancel
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                onClick={() => startEditing(payment)}
                                className="bg-green-900 hover:bg-green-800 text-green-400 text-xs font-medium px-3 py-1.5 rounded-lg transition"
                              >
                                Mark Paid
                              </button>
                              <button
                                onClick={() => startEditing(payment)}
                                className="bg-red-900 hover:bg-red-800 text-red-400 text-xs font-medium px-3 py-1.5 rounded-lg transition"
                              >
                                Write Off
                              </button>
                            </>
                          )}
                        </>
                      )}
                      {payment.status === "WRITTEN_OFF" && (
                        <button
                          onClick={() => reactivateMutation.mutate(payment.id)}
                          disabled={reactivateMutation.isPending}
                          className="bg-yellow-900 hover:bg-yellow-800 text-yellow-400 text-xs font-medium px-3 py-1.5 rounded-lg transition"
                        >
                          Reactivate
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        <Pagination data={data} setPage={setPage} />
      </div>
    </div>
  );
}
