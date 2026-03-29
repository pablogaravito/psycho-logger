import { useParams, useNavigate } from "react-router-dom";
import api from "../../api/axios";
import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { formatTimestamp, formatDateLong } from "../../utils/dateUtils";
import { useAuth } from "../../hooks/useAuth";

const STATUS_COLORS = {
  PAID: "bg-green-900 text-green-400",
  PENDING: "bg-yellow-900 text-yellow-400",
  WRITTEN_OFF: "bg-red-900 text-red-400",
  PARTIAL: "bg-blue-900 text-blue-400",
  REFUNDED: "bg-gray-700 text-gray-400",
};

export default function SessionDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { preferences } = useAuth();
  const { dateFormat, timeFormat, uiLanguage } = preferences || {};

  const { data: session, isLoading } = useQuery({
    queryKey: ["session", id],
    queryFn: () => api.get(`/sessions/${id}`).then((r) => r.data),
  });

  const { data: sessionPayments } = useQuery({
    queryKey: ["payments", "session", id],
    queryFn: () => api.get(`/payments/session/${id}`).then((r) => r.data),
  });

  const queryClient = useQueryClient();
  const [editingPayment, setEditingPayment] = useState(false);
  const [paymentAmount, setPaymentAmount] = useState("");

  const markPaidMutation = useMutation({
    mutationFn: ({ paymentId, amount }) =>
      api.put(`/payments/${paymentId}`, {
        status: "PAID",
        amount: parseFloat(amount),
        paidAt: new Date().toISOString(),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries(["payments", "session", id]);
      queryClient.invalidateQueries(["dashboard-stats"]);
      setEditingPayment(false);
    },
  });

  const createPaymentMutation = useMutation({
    mutationFn: (data) => api.post("/payments", data),
    onSuccess: () => {
      queryClient.invalidateQueries(["payments", "session", id]);
      queryClient.invalidateQueries(["dashboard-stats"]);
      setEditingPayment(false);
    },
  });

  if (isLoading) return <div className="text-gray-400">Loading...</div>;

  return (
    <div className="max-w-3xl mx-auto">
      {/* Header */}
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">
            {formatDateLong(session?.scheduledAt, uiLanguage)}
          </h1>
          <div className="flex items-center gap-2 mt-2">
            <span
              className={`text-xs font-semibold px-2 py-1 rounded-full ${
                session?.status === "COMPLETED"
                  ? "bg-green-900 text-green-400"
                  : session?.status === "SCHEDULED"
                    ? "bg-blue-900 text-blue-400"
                    : "bg-gray-700 text-gray-400"
              }`}
            >
              {session?.status}
            </span>
            <span className="text-xs text-gray-500">
              {session?.sessionType}
            </span>
            <span className="text-xs text-gray-500">
              {session?.durationMinutes} min
            </span>
            {session?.isRelevant && (
              <span className="text-yellow-400 text-xs">⭐ Relevant</span>
            )}
          </div>
        </div>
        <button
          onClick={() => navigate(`/sessions/${id}/edit`)}
          className="bg-gray-800 hover:bg-gray-700 text-white text-sm font-semibold px-4 py-2 rounded-lg transition"
        >
          Edit
        </button>
      </div>

      {/* Patients */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-4">
        <h2 className="text-white font-semibold mb-3">Patient(s)</h2>
        <div className="flex flex-wrap gap-2">
          {session?.patients?.map((p) => (
            <span
              key={p.id}
              onClick={() => navigate(`/patients/${p.id}`)}
              className="bg-gray-800 text-indigo-400 hover:text-indigo-300 text-sm px-3 py-1.5 rounded-lg cursor-pointer transition"
            >
              {p.firstName} {p.lastName}
            </span>
          ))}
        </div>
      </div>

      {/* Main themes */}
      {session?.mainThemes && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-4">
          <h2 className="text-white font-semibold mb-2">Main Themes</h2>
          <p className="text-gray-300 text-sm">{session.mainThemes}</p>
        </div>
      )}

      {/* Content */}
      {session?.content && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-4">
          <h2 className="text-white font-semibold mb-2">Session Notes</h2>
          <p className="text-gray-300 text-sm whitespace-pre-wrap leading-relaxed">
            {session.content}
          </p>
        </div>
      )}

      {/* Next session */}
      {session?.nextSession && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-4">
          <h2 className="text-white font-semibold mb-2">Next Session</h2>
          <p className="text-gray-300 text-sm">{session.nextSession}</p>
        </div>
      )}

      {/* Payment section */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-4">
        <h2 className="text-white font-semibold mb-3">Payment</h2>

        {sessionPayments?.length > 0 ? (
          <div className="space-y-2">
            {sessionPayments.map((payment) => (
              <div
                key={payment.id}
                className="flex items-center justify-between"
              >
                <div>
                  <span
                    className={`text-xs font-semibold px-2 py-1 rounded-full ${
                      STATUS_COLORS[payment.status] ||
                      "bg-gray-700 text-gray-400"
                    }`}
                  >
                    {payment.status}
                  </span>
                  <span className="text-white text-sm ml-3">
                    {payment.amount} {payment.currency}
                  </span>
                </div>
                {payment.status === "PENDING" &&
                  (editingPayment ? (
                    <div className="flex items-center gap-2">
                      <input
                        type="number"
                        value={paymentAmount}
                        onChange={(e) => setPaymentAmount(e.target.value)}
                        className="w-24 bg-gray-700 border border-indigo-500 text-white rounded px-2 py-1 text-sm focus:outline-none"
                        autoFocus
                      />
                      <button
                        onClick={() =>
                          markPaidMutation.mutate({
                            paymentId: payment.id,
                            amount: paymentAmount,
                          })
                        }
                        className="bg-green-700 hover:bg-green-600 text-white text-xs px-3 py-1.5 rounded-lg"
                      >
                        Confirm
                      </button>
                      <button
                        onClick={() => setEditingPayment(false)}
                        className="bg-gray-700 text-white text-xs px-3 py-1.5 rounded-lg"
                      >
                        Cancel
                      </button>
                    </div>
                  ) : (
                    <button
                      onClick={() => {
                        setEditingPayment(true);
                        setPaymentAmount(payment.amount?.toString() || "");
                      }}
                      className="bg-green-900 hover:bg-green-800 text-green-400 text-xs px-3 py-1.5 rounded-lg"
                    >
                      Mark Paid
                    </button>
                  ))}
              </div>
            ))}
          </div>
        ) : // no payment yet
        editingPayment ? (
          <div className="flex items-center gap-3">
            <input
              type="number"
              value={paymentAmount}
              onChange={(e) => setPaymentAmount(e.target.value)}
              placeholder="Amount"
              className="w-32 bg-gray-800 border border-gray-700 text-white rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-indigo-500"
              autoFocus
            />
            <button
              onClick={() =>
                createPaymentMutation.mutate({
                  patientId: session?.patients?.[0]?.id,
                  sessionId: parseInt(id),
                  amount: parseFloat(paymentAmount) || 0,
                  currency: "SOL",
                  status: "PAID",
                  paidAt: new Date().toISOString(),
                })
              }
              className="bg-green-700 hover:bg-green-600 text-white text-xs px-3 py-1.5 rounded-lg"
            >
              Confirm
            </button>
            <button
              onClick={() => setEditingPayment(false)}
              className="bg-gray-700 text-white text-xs px-3 py-1.5 rounded-lg"
            >
              Cancel
            </button>
          </div>
        ) : (
          <div className="flex items-center justify-between">
            <p className="text-gray-500 text-sm">No payment recorded</p>
            <button
              onClick={() => setEditingPayment(true)}
              className="bg-green-900 hover:bg-green-800 text-green-400 text-xs px-3 py-1.5 rounded-lg"
            >
              Mark as Paid
            </button>
          </div>
        )}
      </div>

      {/* Footer */}
      <div className="text-xs text-gray-600 mt-4">
        Created:{" "}
        {session?.createdAt
          ? formatTimestamp(session.createdAt, dateFormat, timeFormat)
          : "—"}
        {session?.updatedAt && (
          <span className="ml-4">
            Updated:{" "}
            {formatTimestamp(session.updatedAt, dateFormat, timeFormat)}
          </span>
        )}
      </div>
    </div>
  );
}
