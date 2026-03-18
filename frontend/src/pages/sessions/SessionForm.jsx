import { useState, useEffect, useMemo } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
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

  const { data: patients } = useQuery({
    queryKey: ["patients"],
    queryFn: () => api.get("/patients").then((r) => r.data),
  });

  const { data: session } = useQuery({
    queryKey: ["session", id],
    queryFn: () => api.get(`/sessions/${id}`).then((r) => r.data),
    enabled: isEditing,
  });

  useEffect(() => {
    if (session) {
      setForm({
        scheduledAt: session.scheduledAt?.slice(0, 16) || "",
        durationMinutes: session.durationMinutes || 50,
        mainThemes: session.mainThemes || "",
        content: session.content || "",
        nextSession: session.nextSession || "",
        isRelevant: session.isRelevant || false,
        sessionType: session.sessionType || "INDIVIDUAL",
        status: session.status || "COMPLETED",
        patientIds: session.patients?.map((p) => p.id) || [],
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

  const addPatient = (patientId) => {
    if (!patientId) return;
    setForm((prev) => ({
      ...prev,
      patientIds: [...prev.patientIds, parseInt(patientId)],
    }));
  };

  const removePatient = (patientId) => {
    setForm((prev) => ({
      ...prev,
      patientIds: prev.patientIds.filter((id) => id !== patientId),
    }));
  };

  const mutation = useMutation({
    mutationFn: (data) =>
      isEditing
        ? api.put(`/sessions/${id}`, data)
        : api.post("/sessions", data),
    onSuccess: () => {
      queryClient.invalidateQueries(["sessions"]);
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
      ...form,
      durationMinutes: parseInt(form.durationMinutes),
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
            <div className="flex gap-2 mb-2">
              <select
                onChange={(e) => {
                  addPatient(e.target.value);
                  e.target.value = "";
                }}
                className="flex-1 bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
                defaultValue=""
              >
                <option value="" disabled>
                  Add a patient...
                </option>
                {availablePatients.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.firstName} {p.lastName}
                    {p.shortName ? ` (${p.shortName})` : ""}
                  </option>
                ))}
              </select>
            </div>
            {selectedPatients.length > 0 && (
              <div className="space-y-1">
                {selectedPatients.map((p) => (
                  <div
                    key={p.id}
                    className="flex items-center justify-between bg-gray-800 px-4 py-2 rounded-lg"
                  >
                    <span className="text-white text-sm">
                      {p.firstName} {p.lastName}
                    </span>
                    <button
                      type="button"
                      onClick={() => removePatient(p.id)}
                      className="text-red-400 hover:text-red-300 text-xs font-medium"
                    >
                      Remove
                    </button>
                  </div>
                ))}
              </div>
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
            <label className="block text-sm text-gray-400 mb-1">
              Session Content
            </label>
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
