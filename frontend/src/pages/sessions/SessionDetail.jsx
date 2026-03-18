import { useParams, useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import api from "../../api/axios";

export default function SessionDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const { data: session, isLoading } = useQuery({
    queryKey: ["session", id],
    queryFn: () => api.get(`/sessions/${id}`).then((r) => r.data),
  });

  if (isLoading) return <div className="text-gray-400">Loading...</div>;

  return (
    <div className="max-w-3xl mx-auto">
      {/* Header */}
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">
            {new Date(session?.scheduledAt).toLocaleDateString("es-PE", {
              weekday: "long",
              year: "numeric",
              month: "long",
              day: "numeric",
            })}
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

      {/* Footer */}
      <div className="text-xs text-gray-600 mt-4">
        Created:{" "}
        {session?.createdAt
          ? new Date(session.createdAt).toLocaleString()
          : "—"}
        {session?.updatedAt && (
          <span className="ml-4">
            Updated: {new Date(session.updatedAt).toLocaleString()}
          </span>
        )}
      </div>
    </div>
  );
}
