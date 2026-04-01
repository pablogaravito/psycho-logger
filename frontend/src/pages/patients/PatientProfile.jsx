import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import api from "../../api/axios";
import { useAuth } from "../../hooks/useAuth";
import {
  formatDateLong,
  formatDateShort,
  formatDateMedium,
  formatAge,
} from "../../utils/dateUtils";
import Pagination from "../../components/Pagination.jsx";
import ScheduleAppointmentModal from "../../components/ScheduleAppointmentModal.jsx";

const STATUS_COLORS = {
  PAID: "bg-green-900 text-green-400",
  PENDING: "bg-yellow-900 text-yellow-400",
  WRITTEN_OFF: "bg-red-900 text-red-400",
  PARTIAL: "bg-blue-900 text-blue-400",
  REFUNDED: "bg-gray-700 text-gray-400",
};

export default function PatientProfile() {
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { user, preferences } = useAuth();
  const { dateFormat, uiLanguage } = preferences || {};

  const [showSchedule, setShowSchedule] = useState(false);
  const [sessionsPage, setSessionsPage] = useState(0);
  const [paymentsPage, setPaymentsPage] = useState(0);
  const [editingPayment, setEditingPayment] = useState(false);
  const [paymentAmount, setPaymentAmount] = useState("");

  const { data: patient, isLoading: loadingPatient } = useQuery({
    queryKey: ["patient", id],
    queryFn: () => api.get(`/patients/${id}`).then((r) => r.data),
  });

  const { data: sessionsData, isLoading: loadingSessions } = useQuery({
    queryKey: ["sessions", "patient", id, sessionsPage],
    queryFn: () =>
      api
        .get(`/sessions/patient/${id}?page=${sessionsPage}&size=5`)
        .then((r) => r.data),
  });

  const { data: paymentsData, isLoading: loadingPayments } = useQuery({
    queryKey: ["payments", "patient", id, paymentsPage],
    queryFn: () =>
      api
        .get(`/payments/patient/${id}?page=${paymentsPage}&size=5`)
        .then((r) => r.data),
  });

  const toggleActiveMutation = useMutation({
    mutationFn: () =>
      patient.isActive
        ? api.delete(`/patients/${id}`)
        : api.put(`/patients/${id}`, { ...patient, isActive: true }),
    onSuccess: () => {
      queryClient.invalidateQueries(["patient", id]);
      queryClient.invalidateQueries(["patients"]);
    },
  });

  const createPaymentMutation = useMutation({
    mutationFn: (data) => api.post("/payments", data),
    onSuccess: () => {
      queryClient.invalidateQueries(["payments", "patient", id, paymentsPage]);
      queryClient.invalidateQueries(["dashboard-stats"]);
      setEditingPayment(false);
    },
  });

  if (loadingPatient) return <div className="text-gray-400">Loading...</div>;

  return (
    <div className="max-w-4xl mx-auto">
      {/* Header */}
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">
            {patient?.firstName} {patient?.lastName}
          </h1>
          {patient?.shortName && (
            <p className="text-gray-400 text-sm mt-0.5">
              "{patient.shortName}"
            </p>
          )}
          <span
            className={`inline-block mt-2 text-xs font-semibold px-2 py-1 rounded-full ${
              patient?.isActive
                ? "bg-green-900 text-green-400"
                : "bg-gray-800 text-gray-500"
            }`}
          >
            {patient?.isActive ? "Active" : "Inactive"}
          </span>
        </div>
        <div className="flex gap-2">
          {user?.isTherapist && (
            <>
              <button
                onClick={() => navigate(`/sessions/new?patientId=${id}`)}
                className="bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold px-4 py-2 rounded-lg transition"
              >
                + New Session
              </button>
              <button
                onClick={() => setShowSchedule(true)}
                className="bg-gray-800 hover:bg-gray-700 text-white text-sm font-semibold px-4 py-2 rounded-lg transition"
              >
                📅 Schedule
              </button>
            </>
          )}
          <button
            onClick={() => navigate(`/patients/${id}/edit`)}
            className="bg-gray-800 hover:bg-gray-700 text-white text-sm font-semibold px-4 py-2 rounded-lg transition"
          >
            Edit
          </button>
          <button
            onClick={() => toggleActiveMutation.mutate()}
            disabled={toggleActiveMutation.isPending}
            className={`text-sm font-semibold px-4 py-2 rounded-lg transition ${
              patient?.isActive
                ? "bg-red-900 hover:bg-red-800 text-red-400"
                : "bg-green-900 hover:bg-green-800 text-green-400"
            }`}
          >
            {patient?.isActive ? "Deactivate" : "Reactivate"}
          </button>
        </div>
      </div>

      {/* Debt flag banner */}
      {patient?.hasDebtFlag && (
        <div className="bg-red-900/30 border border-red-800 rounded-xl px-5 py-4 mb-6">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-red-400 font-semibold text-sm">
                🚩 Outstanding Debt
              </p>
              <p className="text-red-300 text-sm mt-1">
                {patient.writtenOffAmount != null ? (
                  <>
                    <span className="font-semibold">
                      {patient.writtenOffAmount} SOL
                    </span>
                    {patient.oldestWrittenOffDate && (
                      <span className="text-red-400 ml-1">
                        · since{" "}
                        {formatDateMedium(
                          patient.oldestWrittenOffDate,
                          dateFormat,
                          uiLanguage,
                        )}
                      </span>
                    )}
                  </>
                ) : (
                  patient.debtFlagNote
                )}
              </p>
            </div>
            {user?.isAdmin && (
              <button
                onClick={() =>
                  api
                    .put(`/patients/${id}/clear-flag`)
                    .then(() => queryClient.invalidateQueries(["patient", id]))
                }
                className="text-xs text-red-400 hover:text-red-300 border border-red-800 px-3 py-1 rounded-lg"
              >
                Clear Flag
              </button>
            )}
          </div>
        </div>
      )}

      {/* Handover notes */}
      {patient?.handoverNotes && (
        <div className="bg-yellow-900/20 border border-yellow-800 rounded-xl p-6 mb-6">
          <h2 className="text-yellow-400 font-semibold mb-2">
            📋 Handover Notes
          </h2>
          <p className="text-yellow-200 text-sm whitespace-pre-wrap">
            {patient.handoverNotes}
          </p>
        </div>
      )}

      {/* Patient Info */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-6">
        <h2 className="text-white font-semibold mb-4">Patient Information</h2>
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <span className="text-gray-400">Date of Birth</span>
            <p className="text-white mt-0.5">
              {patient?.dateOfBirth ? (
                <>
                  {formatDateLong(patient.dateOfBirth, uiLanguage)}
                  <span className="text-gray-400 text-sm ml-2">
                    ({formatAge(patient.dateOfBirth, uiLanguage)})
                  </span>
                </>
              ) : (
                "—"
              )}
            </p>
          </div>
          <div>
            <span className="text-gray-400">Gender</span>
            <p className="text-white mt-0.5">{patient?.gender || "—"}</p>
          </div>
          <div>
            <span className="text-gray-400">Email</span>
            <p className="text-white mt-0.5">{patient?.email || "—"}</p>
          </div>
          <div>
            <span className="text-gray-400">Phone</span>
            <p className="text-white mt-0.5">{patient?.phone || "—"}</p>
          </div>
          <div>
            <span className="text-gray-400">Assigned Therapist</span>
            <p className="text-white mt-0.5">
              {patient?.assignedTherapistName || (
                <span className="text-gray-500">Unassigned</span>
              )}
            </p>
          </div>
          {patient?.notes && (
            <div className="col-span-2">
              <span className="text-gray-400">Notes</span>
              <p className="text-white mt-0.5">{patient.notes}</p>
            </div>
          )}
        </div>
      </div>

      {/* Sessions — therapists only */}
      {user?.isTherapist && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-white font-semibold">
              Sessions
              <span className="text-gray-500 font-normal text-sm ml-2">
                ({sessionsData?.totalElements || 0})
              </span>
            </h2>
          </div>

          {loadingSessions ? (
            <p className="text-gray-400 text-sm">Loading sessions...</p>
          ) : sessionsData?.totalElements === 0 ? (
            <p className="text-gray-500 text-sm">No sessions yet.</p>
          ) : (
            <>
              <div className="space-y-2">
                {sessionsData?.content?.map((session) => (
                  <div
                    key={session.id}
                    onClick={() => navigate(`/sessions/${session.id}`)}
                    className="flex items-center justify-between px-4 py-3 bg-gray-800 rounded-lg cursor-pointer hover:bg-gray-750 transition"
                  >
                    <div>
                      <p className="text-white text-sm font-medium">
                        {session?.mainThemes || "--"}
                      </p>
                      <p className="text-gray-400 text-xs mt-0.5 truncate max-w-md">
                        {formatDateMedium(
                          session.scheduledAt,
                          dateFormat,
                          uiLanguage,
                        )}
                      </p>
                    </div>
                    <div className="flex items-center gap-3">
                      {session.isRelevant && (
                        <span className="text-yellow-400 text-xs">
                          ⭐ Relevant
                        </span>
                      )}
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
                    </div>
                  </div>
                ))}
              </div>
              <Pagination data={sessionsData} setPage={setSessionsPage} />
            </>
          )}
        </div>
      )}

      {/* Payments */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
        <h2 className="text-white font-semibold mb-4">
          Payments
          <span className="text-gray-500 font-normal text-sm ml-2">
            ({paymentsData?.totalElements || 0})
          </span>
        </h2>

        {loadingPayments ? (
          <p className="text-gray-400 text-sm">Loading payments...</p>
        ) : paymentsData?.totalElements === 0 ? (
          <div className="flex items-center justify-between">
            <p className="text-gray-500 text-sm">No payments recorded.</p>
            {user?.isTherapist &&
              (editingPayment ? (
                <div className="flex items-center gap-2">
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
                        patientId: parseInt(id),
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
                <button
                  onClick={() => setEditingPayment(true)}
                  className="bg-green-900 hover:bg-green-800 text-green-400 text-xs px-3 py-1.5 rounded-lg"
                >
                  Mark as Paid
                </button>
              ))}
          </div>
        ) : (
          <>
            <div className="space-y-2">
              {paymentsData?.content?.map((payment) => (
                <div
                  key={payment.id}
                  className="flex items-center justify-between px-4 py-3 bg-gray-800 rounded-lg"
                >
                  <div>
                    <p className="text-white text-sm font-medium">
                      {payment.amount} {payment.currency}
                    </p>
                    {payment.paidAt && (
                      <p className="text-gray-400 text-xs mt-0.5">
                        {formatDateShort(payment.paidAt, dateFormat)}
                      </p>
                    )}
                  </div>
                  <span
                    className={`text-xs font-semibold px-2 py-1 rounded-full ${
                      STATUS_COLORS[payment.status] ||
                      "bg-gray-700 text-gray-400"
                    }`}
                  >
                    {payment.status}
                  </span>
                </div>
              ))}
            </div>
            <Pagination data={paymentsData} setPage={setPaymentsPage} />
          </>
        )}
      </div>

      {showSchedule && (
        <ScheduleAppointmentModal
          patient={patient}
          onClose={() => setShowSchedule(false)}
        />
      )}
    </div>
  );
}
