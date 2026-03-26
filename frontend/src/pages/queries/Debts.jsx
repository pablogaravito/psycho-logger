import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";

export default function Debts() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [showInactive, setShowInactive] = useState(false);
  const [expanded, setExpanded] = useState(null);
  const [editingPayment, setEditingPayment] = useState(null);
  const [editAmount, setEditAmount] = useState("");
  const [writingOffPayment, setWritingOffPayment] = useState(null);
  const [writeOffAmount, setWriteOffAmount] = useState("");

  const { data: debts, isLoading } = useQuery({
    queryKey: ["debts"],
    queryFn: () => api.get("/payments/debts").then((r) => r.data),
  });

  const markPaidMutation = useMutation({
    mutationFn: ({ paymentId, amount }) =>
      api.put(`/payments/${paymentId}`, {
        status: "PAID",
        amount: parseFloat(amount),
        paidAt: new Date().toISOString(),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries(["debts"]);
      queryClient.invalidateQueries(["payments"]);
      queryClient.invalidateQueries(["dashboard-stats"]);
      setEditingPayment(null);
    },
  });

  const markAllPaidMutation = useMutation({
    mutationFn: async ({ payments, amount }) => {
      // split amount evenly across sessions
      const perSession = parseFloat(amount) / payments.length;
      await Promise.all(
        payments.map((p) =>
          api.put(`/payments/${p.paymentId}`, {
            status: "PAID",
            amount: perSession,
            paidAt: new Date().toISOString(),
          }),
        ),
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries(["debts"]);
      queryClient.invalidateQueries(["payments"]);
      queryClient.invalidateQueries(["dashboard-stats"]);
      setEditingPayment(null);
    },
  });

  const writeOffMutation = useMutation({
    mutationFn: ({ paymentId, amount }) =>
      api.put(`/payments/${paymentId}`, {
        status: "WRITTEN_OFF",
        amount: parseFloat(amount),
        paidAt: new Date().toISOString(),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries(["debts"]);
      queryClient.invalidateQueries(["payments"]);
      queryClient.invalidateQueries(["dashboard-stats"]);
    },
  });

  const writeOffAllMutation = useMutation({
    mutationFn: async (payments) => {
      await Promise.all(
        payments.map((p) =>
          api.put(`/payments/${p.paymentId}`, {
            status: "WRITTEN_OFF",
            amount: parseFloat(p.amount) || 0,
            paidAt: new Date().toISOString(),
          }),
        ),
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries(["debts"]);
      queryClient.invalidateQueries(["payments"]);
      queryClient.invalidateQueries(["dashboard-stats"]);
    },
  });

  const filtered =
    debts?.filter((d) => (showInactive ? true : d.isActive)) || [];

  const inactiveCount = debts?.filter((d) => !d.isActive).length || 0;

  if (isLoading) return <div className="text-gray-400">Loading...</div>;

  return (
    <div className="max-w-3xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Pending Debts</h1>
          <p className="text-gray-400 text-sm mt-1">
            {filtered.length} patients with pending payments
          </p>
        </div>
        {inactiveCount > 0 && (
          <label className="flex items-center gap-2 text-sm text-gray-400 cursor-pointer">
            <input
              type="checkbox"
              checked={showInactive}
              onChange={(e) => setShowInactive(e.target.checked)}
              className="accent-indigo-500"
            />
            Show inactive ({inactiveCount})
          </label>
        )}
      </div>

      {filtered.length === 0 ? (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-8 text-center">
          <p className="text-gray-500">🎉 No pending debts!</p>
        </div>
      ) : (
        <div className="space-y-3">
          {filtered.map((debt) => (
            <div
              key={debt.patientId}
              className={`bg-gray-900 border rounded-xl overflow-hidden transition ${
                debt.hasDebtFlag ? "border-red-800" : "border-gray-800"
              }`}
            >
              {/* Patient row — clickable to expand */}
              <div
                className="flex items-center justify-between px-5 py-4 cursor-pointer hover:bg-gray-800 transition"
                onClick={() =>
                  setExpanded(
                    expanded === debt.patientId ? null : debt.patientId,
                  )
                }
              >
                <div className="flex items-center gap-3">
                  <span className="text-white font-medium">
                    {debt.hasDebtFlag && <span className="mr-1">🚩</span>}
                    {debt.patientName}
                    {debt.shortName && (
                      <span className="text-gray-500 text-sm ml-1">
                        ({debt.shortName})
                      </span>
                    )}
                  </span>
                  {!debt.isActive && (
                    <span className="text-xs bg-gray-800 text-gray-500 px-2 py-0.5 rounded-full">
                      Inactive
                    </span>
                  )}
                </div>

                <div className="flex items-center gap-4">
                  <div className="text-right">
                    <p className="text-yellow-400 font-semibold">
                      {debt.totalPending} {debt.pendingPayments[0]?.currency}
                    </p>
                    <p className="text-gray-500 text-xs">
                      {debt.pendingCount} session
                      {debt.pendingCount !== 1 ? "s" : ""}
                    </p>
                  </div>
                  <span className="text-gray-500 text-sm">
                    {expanded === debt.patientId ? "▲" : "▼"}
                  </span>
                </div>
              </div>

              {/* Expanded detail */}
              {expanded === debt.patientId && (
                <div className="border-t border-gray-800 px-5 py-4">
                  {/* Bulk actions */}
                  <div className="flex gap-2 mb-4">
                    <button
                      onClick={() => navigate(`/patients/${debt.patientId}`)}
                      className="text-indigo-400 hover:text-indigo-300 text-xs font-medium"
                    >
                      View Profile →
                    </button>
                    <span className="text-gray-700">·</span>
                    {editingPayment === `all-${debt.patientId}` ? (
                      <div className="flex items-center gap-2">
                        <span className="text-gray-400 text-xs">
                          Total amount:
                        </span>
                        <input
                          type="number"
                          value={editAmount}
                          onChange={(e) => setEditAmount(e.target.value)}
                          className="w-24 bg-gray-700 border border-indigo-500 text-white rounded px-2 py-1 text-xs focus:outline-none"
                          autoFocus
                        />
                        <button
                          onClick={() =>
                            markAllPaidMutation.mutate({
                              payments: debt.pendingPayments,
                              amount: editAmount,
                            })
                          }
                          disabled={markAllPaidMutation.isPending}
                          className="bg-green-700 hover:bg-green-600 text-white text-xs px-3 py-1 rounded-lg"
                        >
                          Confirm
                        </button>
                        <button
                          onClick={() => setEditingPayment(null)}
                          className="text-gray-400 text-xs"
                        >
                          Cancel
                        </button>
                      </div>
                    ) : (
                      <>
                        <button
                          onClick={() => {
                            setEditingPayment(`all-${debt.patientId}`);
                            setEditAmount(debt.totalPending.toString());
                          }}
                          className="text-green-400 hover:text-green-300 text-xs font-medium"
                        >
                          Mark All Paid
                        </button>
                        <span className="text-gray-700">·</span>
                        <button
                          onClick={() =>
                            writeOffAllMutation.mutate(debt.pendingPayments)
                          }
                          disabled={writeOffAllMutation.isPending}
                          className="text-red-400 hover:text-red-300 text-xs font-medium"
                        >
                          Write Off All
                        </button>
                      </>
                    )}
                  </div>

                  {/* Individual sessions */}
                  <div className="space-y-2">
                    {debt.pendingPayments.map((payment) => (
                      <div
                        key={payment.paymentId}
                        className="flex items-center justify-between bg-gray-800 px-4 py-3 rounded-lg"
                      >
                        <div>
                          <p className="text-white text-sm">
                            {payment.sessionDate
                              ? new Date(
                                  payment.sessionDate,
                                ).toLocaleDateString("es-PE", {
                                  weekday: "short",
                                  year: "numeric",
                                  month: "short",
                                  day: "numeric",
                                })
                              : "No session date"}
                          </p>
                          <p className="text-yellow-400 text-xs mt-0.5">
                            {payment.amount} {payment.currency}
                          </p>
                        </div>

                        <div className="flex items-center gap-2">
                          {editingPayment === payment.paymentId ? (
                            <>
                              <input
                                type="number"
                                value={editAmount}
                                onChange={(e) => setEditAmount(e.target.value)}
                                className="w-20 bg-gray-700 border border-indigo-500 text-white rounded px-2 py-1 text-xs focus:outline-none"
                                autoFocus
                              />
                              <button
                                onClick={() =>
                                  markPaidMutation.mutate({
                                    paymentId: payment.paymentId,
                                    amount: editAmount,
                                  })
                                }
                                disabled={markPaidMutation.isPending}
                                className="bg-green-700 hover:bg-green-600 text-white text-xs px-2 py-1 rounded-lg"
                              >
                                ✓
                              </button>
                              <button
                                onClick={() => setEditingPayment(null)}
                                className="text-gray-400 text-xs px-1"
                              >
                                ✕
                              </button>
                            </>
                          ) : (
                            <>
                              {payment.sessionId && (
                                <button
                                  onClick={() =>
                                    navigate(`/sessions/${payment.sessionId}`)
                                  }
                                  className="text-indigo-400 hover:text-indigo-300 text-xs"
                                >
                                  Session →
                                </button>
                              )}
                              <button
                                onClick={() => {
                                  setEditingPayment(payment.paymentId);
                                  setEditAmount(
                                    payment.amount?.toString() || "",
                                  );
                                }}
                                className="bg-green-900 hover:bg-green-800 text-green-400 text-xs px-2 py-1 rounded-lg"
                              >
                                Paid
                              </button>
                              {writingOffPayment === payment.paymentId ? (
                                <>
                                  <input
                                    type="number"
                                    value={writeOffAmount}
                                    onChange={(e) =>
                                      setWriteOffAmount(e.target.value)
                                    }
                                    className="w-20 bg-gray-700 border border-red-500 text-white rounded px-2 py-1 text-xs focus:outline-none"
                                    autoFocus
                                  />
                                  <button
                                    onClick={() => {
                                      writeOffMutation.mutate({
                                        paymentId: payment.paymentId,
                                        amount: writeOffAmount,
                                      });
                                      setWritingOffPayment(null);
                                    }}
                                    disabled={writeOffMutation.isPending}
                                    className="bg-red-700 hover:bg-red-600 text-white text-xs px-2 py-1 rounded-lg"
                                  >
                                    ✓
                                  </button>
                                  <button
                                    onClick={() => setWritingOffPayment(null)}
                                    className="text-gray-400 text-xs px-1"
                                  >
                                    ✕
                                  </button>
                                </>
                              ) : (
                                <button
                                  onClick={() => {
                                    setWritingOffPayment(payment.paymentId);
                                    setWriteOffAmount(
                                      payment.amount?.toString() || "",
                                    );
                                  }}
                                  className="bg-red-900 hover:bg-red-800 text-red-400 text-xs px-2 py-1 rounded-lg"
                                >
                                  Write Off
                                </button>
                              )}
                            </>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
