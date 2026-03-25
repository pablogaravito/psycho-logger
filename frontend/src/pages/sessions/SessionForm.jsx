import { useState, useEffect, useMemo } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import TranscribeButton from "../../components/TranscribeButton.jsx";
import api from "../../api/axios";

const SESSION_TYPES = ["INDIVIDUAL", "COUPLE", "FAMILY", "GROUP"];
const SESSION_STATUSES = ["SCHEDULED", "COMPLETED", "CANCELLED", "NO_SHOW"];

export default function SessionForm() {
  const { id } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const isEditing = Boolean(id);
  const preselectedPatientId = searchParams.get("patientId");

  const [form, setForm] = useState({
    scheduledAt: "",
    durationMinutes: 50,
    mainThemes: "",
    content: "",
    nextSession: "",
    isRelevant: false,
    sessionType: "INDIVIDUAL",
    status: "COMPLETED",
    patientIds: preselectedPatientId ? [parseInt(preselectedPatientId)] : [],
  });

  const [isPaid, setIsPaid] = useState(false);
  const [paymentAmount, setPaymentAmount] = useState("");
  const [paymentCurrency, setPaymentCurrency] = useState("SOL");
  const [originalPatientIds, setOriginalPatientIds] = useState([]);

  const { data: patients } = useQuery({
    queryKey: ["patients"],
    queryFn: () => api.get("/patients").then((r) => r.data),
  });

  const { data: session } = useQuery({
    queryKey: ["session", id],
    queryFn: () => api.get(`/sessions/${id}`).then((r) => r.data),
    enabled: isEditing,
  });

  // fetch existing payments for this session when editing
  const { data: existingPayments } = useQuery({
    queryKey: ["payments", "session", id],
    queryFn: () => api.get(`/payments/session/${id}`).then((r) => r.data),
    enabled: isEditing,
  });

  const singlePatientId =
    form.patientIds.length === 1 ? form.patientIds[0] : null;

  const { data: sessionDefaults } = useQuery({
    queryKey: ["session-defaults", singlePatientId],
    queryFn: () =>
      api
        .get(`/settings/user/session-defaults/${singlePatientId}`)
        .then((r) => r.data),
    enabled: Boolean(singlePatientId) && !isEditing,
  });

  useEffect(() => {
    if (sessionDefaults && !isEditing) {
      setForm((prev) => ({
        ...prev,
        durationMinutes: sessionDefaults.defaultSessionDuration || 50,
      }));
      setPaymentCurrency(sessionDefaults.defaultCurrency || "SOL");
      if (sessionDefaults.defaultSessionPrice != null) {
        setPaymentAmount(sessionDefaults.defaultSessionPrice.toString());
      }
    }
  }, [sessionDefaults, isEditing]);

  useEffect(() => {
    if (session) {
      const ids = session.patients?.map((p) => p.id) || [];
      setOriginalPatientIds(ids);
      setForm({
        scheduledAt: session.scheduledAt?.slice(0, 16) || "",
        durationMinutes: session.durationMinutes || 50,
        mainThemes: session.mainThemes || "",
        content: session.content || "",
        nextSession: session.nextSession || "",
        isRelevant: session.isRelevant || false,
        sessionType: session.sessionType || "INDIVIDUAL",
        status: session.status || "COMPLETED",
        patientIds: ids,
      });
    }
  }, [session]);

  const activePatients = useMemo(
    () => patients?.filter((p) => p.isActive) || [],
    [patients],
  );

  const availablePatients = useMemo(
    () => activePatients.filter((p) => !form.patientIds.includes(p.id)),
    [activePatients, form.patientIds],
  );

  const selectedPatients = useMemo(
    () => activePatients.filter((p) => form.patientIds.includes(p.id)),
    [activePatients, form.patientIds],
  );

  const isOriginalPatient = (patientId) =>
    originalPatientIds.includes(patientId);

  const addPatient = (patientId) => {
    if (!patientId) return;
    setForm((prev) => ({
      ...prev,
      patientIds: [...prev.patientIds, parseInt(patientId)],
    }));
  };

  const removePatient = (patientId) => {
    if (isEditing && isOriginalPatient(patientId)) return;
    setForm((prev) => ({
      ...prev,
      patientIds: prev.patientIds.filter((id) => id !== patientId),
    }));
  };

  const mutation = useMutation({
    mutationFn: async (data) => {
      const sessionRes = isEditing
        ? await api.put(`/sessions/${id}`, data.session)
        : await api.post("/sessions", data.session);

      if (!isEditing) {
        if (data.payment) {
          // marked as paid — create PAID payment
          await api.post("/payments", {
            patientId: data.session.patientIds[0],
            sessionId: sessionRes.data.id,
            amount: data.payment.amount,
            currency: data.payment.currency,
            status: "PAID",
            paidAt: new Date().toISOString(),
          });
        } else {
          // not marked as paid — create PENDING payment
          await api.post("/payments", {
            patientId: data.session.patientIds[0],
            sessionId: sessionRes.data.id,
            amount: data.defaultAmount || 0,
            currency: data.defaultCurrency || "SOL",
            status: "PENDING",
          });
        }
      }

      return sessionRes;
    },
    onSuccess: () => {
      queryClient.invalidateQueries(["sessions"]);
      queryClient.invalidateQueries(["payments"]);
      queryClient.invalidateQueries(["dashboard-stats"]);
      navigate("/sessions");
    },
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    mutation.mutate({
      session: {
        ...form,
        durationMinutes: parseInt(form.durationMinutes),
      },
      payment: isPaid
        ? {
            amount: parseFloat(paymentAmount) || 0,
            currency: paymentCurrency,
          }
        : null,
      defaultAmount: parseFloat(paymentAmount) || 0,
      defaultCurrency: paymentCurrency,
    });
  };

  return (
    <div className="max-w-3xl mx-auto">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">
          {isEditing ? "Edit Session" : "New Session"}
        </h1>
        <p className="text-gray-400 text-sm mt-1">
          {isEditing
            ? "Update session details"
            : "Record a new therapy session"}
        </p>
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
        <form onSubmit={handleSubmit} className="space-y-5">
          {/* Patient selector */}
          <div>
            <label className="block text-sm text-gray-400 mb-2">
              Patients *
            </label>

            {/* selected patients */}
            {selectedPatients.length > 0 && (
              <div className="space-y-1 mb-2">
                {selectedPatients.map((p) => {
                  const locked = isEditing && isOriginalPatient(p.id);
                  return (
                    <div
                      key={p.id}
                      className="flex items-center justify-between bg-gray-800 px-4 py-2 rounded-lg"
                    >
                      <span className="text-white text-sm">
                        {p.firstName} {p.lastName}
                      </span>
                      {locked ? (
                        <span className="text-gray-600 text-xs">locked</span>
                      ) : (
                        <button
                          type="button"
                          onClick={() => removePatient(p.id)}
                          className="text-red-400 hover:text-red-300 text-xs font-medium"
                        >
                          Remove
                        </button>
                      )}
                    </div>
                  );
                })}
              </div>
            )}

            {/* add patient dropdown */}
            {availablePatients.length > 0 && (
              <select
                onChange={(e) => {
                  addPatient(e.target.value);
                  e.target.value = "";
                }}
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
                defaultValue=""
              >
                <option value="" disabled>
                  {isEditing ? "Add another patient..." : "Add a patient..."}
                </option>
                {availablePatients.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.firstName} {p.lastName}
                    {p.shortName ? ` (${p.shortName})` : ""}
                  </option>
                ))}
              </select>
            )}

            {isEditing && originalPatientIds.length > 0 && (
              <p className="text-gray-600 text-xs mt-1">
                Original patients are locked and cannot be removed. You can
                still add more patients.
              </p>
            )}
          </div>

          {/* Date and duration */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Date & Time *
              </label>
              <input
                type="datetime-local"
                name="scheduledAt"
                value={form.scheduledAt}
                onChange={handleChange}
                required
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              />
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Duration (minutes)
              </label>
              <input
                type="number"
                name="durationMinutes"
                value={form.durationMinutes}
                onChange={handleChange}
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              />
            </div>
          </div>

          {/* Type and status */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Session Type
              </label>
              <select
                name="sessionType"
                value={form.sessionType}
                onChange={handleChange}
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                {SESSION_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1">Status</label>
              <select
                name="status"
                value={form.status}
                onChange={handleChange}
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                {SESSION_STATUSES.map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Main themes */}
          <div>
            <label className="block text-sm text-gray-400 mb-1">
              Main Themes
            </label>
            <input
              name="mainThemes"
              value={form.mainThemes}
              onChange={handleChange}
              placeholder="e.g. Anxiety, family conflict, work stress..."
              className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
            />
          </div>

          {/* Content */}
          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="block text-sm text-gray-400">
                Session Content
              </label>
              <TranscribeButton
                onTranscribed={(text) =>
                  setForm((prev) => ({
                    ...prev,
                    content: prev.content ? prev.content + "\n\n" + text : text,
                  }))
                }
              />
            </div>
            <textarea
              name="content"
              value={form.content}
              onChange={handleChange}
              rows={8}
              placeholder="Write your session notes here..."
              className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition resize-none"
            />
          </div>

          {/* Next session */}
          <div>
            <label className="block text-sm text-gray-400 mb-1">
              Next Session Notes
            </label>
            <input
              name="nextSession"
              value={form.nextSession}
              onChange={handleChange}
              placeholder="What to cover next session..."
              className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
            />
          </div>

          {/* Relevant flag */}
          <div>
            <label className="flex items-center gap-2 text-sm text-gray-400 cursor-pointer">
              <input
                type="checkbox"
                name="isRelevant"
                checked={form.isRelevant}
                onChange={handleChange}
                className="accent-indigo-500"
              />
              Mark as relevant / important session
            </label>
          </div>

          {/* Payment section — new sessions only */}
          {!isEditing && (
            <div className="border-t border-gray-800 pt-5">
              <label className="flex items-center gap-2 text-sm text-white font-medium cursor-pointer mb-3">
                <input
                  type="checkbox"
                  checked={isPaid}
                  onChange={(e) => setIsPaid(e.target.checked)}
                  className="accent-indigo-500"
                />
                Mark session as paid
              </label>

              {isPaid && (
                <div className="grid grid-cols-2 gap-4 mt-3 pl-5">
                  <div>
                    <label className="block text-sm text-gray-400 mb-1">
                      Amount
                    </label>
                    <input
                      type="number"
                      value={paymentAmount}
                      onChange={(e) => setPaymentAmount(e.target.value)}
                      min={0}
                      step="0.01"
                      placeholder="0.00"
                      className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
                    />
                  </div>
                  <div>
                    <label className="block text-sm text-gray-400 mb-1">
                      Currency
                    </label>
                    <input
                      type="text"
                      value={paymentCurrency}
                      onChange={(e) => setPaymentCurrency(e.target.value)}
                      className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
                    />
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Existing payment notice when editing */}
          {isEditing && existingPayments?.length > 0 && (
            <div className="border-t border-gray-800 pt-5">
              <div className="bg-yellow-900/20 border border-yellow-800 rounded-lg px-4 py-3">
                <p className="text-yellow-400 text-sm font-medium mb-1">
                  ⚠️ This session has a linked payment
                </p>
                {existingPayments.map((payment) => (
                  <p key={payment.id} className="text-yellow-300 text-xs">
                    {payment.patientName} — {payment.amount} {payment.currency}{" "}
                    ({payment.status})
                  </p>
                ))}
                <p className="text-yellow-600 text-xs mt-1">
                  Adding more patients won't affect the existing payment. Manage
                  payments from the patient profile.
                </p>
              </div>
            </div>
          )}

          {mutation.isError && (
            <p className="text-red-400 text-sm">
              Something went wrong. Please try again.
            </p>
          )}

          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="flex-1 bg-gray-800 hover:bg-gray-700 text-white font-semibold rounded-lg py-2.5 transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={mutation.isPending || form.patientIds.length === 0}
              className="flex-1 bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-800 text-white font-semibold rounded-lg py-2.5 transition"
            >
              {mutation.isPending
                ? "Saving..."
                : isEditing
                  ? "Update Session"
                  : "Save Session"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
