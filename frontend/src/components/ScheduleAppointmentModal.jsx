import { useState } from "react";
import { GOOGLE_COLORS } from "./CalendarColorPicker";
import { useQuery } from "@tanstack/react-query";
import api from "../api/axios";

export default function ScheduleAppointmentModal({ patient, onClose }) {
  const today = new Date();
  const defaultDate = today.toISOString().split("T")[0];
  const defaultTime = "09:00";

  const [date, setDate] = useState(defaultDate);
  const [time, setTime] = useState(defaultTime);
  const [duration, setDuration] = useState(50);

  const { data: userSettings } = useQuery({
    queryKey: ["user-settings"],
    queryFn: () => api.get("/settings/user").then((r) => r.data),
  });

  // use default duration from settings if available
  useState(() => {
    if (userSettings?.defaultSessionDuration) {
      setDuration(userSettings.defaultSessionDuration);
    }
  }, [userSettings]);

  const patientColor = patient?.calendarColor || 7;
  const colorHex =
    GOOGLE_COLORS.find((c) => c.id === patientColor)?.hex || "#039BE5";

  /* const buildGoogleCalendarUrl = () => {
    // format dates for Google Calendar: YYYYMMDDTHHmmss
    const start = new Date(`${date}T${time}:00`);
    const end = new Date(start.getTime() + duration * 60000);

    const format = (d) =>
      d
        .toISOString()
        .replace(/[-:]/g, "")
        .replace(/\.\d{3}/, "")
        .slice(0, 15);

    const title = encodeURIComponent(
      `Sesión — ${patient?.shortName || patient?.firstName}`,
    );
    const details = encodeURIComponent("Sesión de terapia");
    const startStr = format(start);
    const endStr = format(end);

    return (
      `https://calendar.google.com/calendar/render?action=TEMPLATE` +
      `&text=${title}` +
      `&dates=${startStr}/${endStr}` +
      `&details=${details}` +
      `&color=${patientColor}`
    );
  }; */

  const buildGoogleCalendarUrl = () => {
    // format as local datetime without UTC conversion
    const formatLocal = (dateStr, timeStr, durationMinutes = 0) => {
      const [year, month, day] = dateStr.split("-");
      const [hours, minutes] = timeStr.split(":");

      // calculate end time
      let endHours = parseInt(hours);
      let endMinutes = parseInt(minutes) + durationMinutes;
      endHours += Math.floor(endMinutes / 60);
      endMinutes = endMinutes % 60;

      if (durationMinutes === 0) {
        return `${year}${month}${day}T${hours.padStart(2, "0")}${minutes.padStart(2, "0")}00`;
      }
      return `${year}${month}${day}T${String(endHours).padStart(2, "0")}${String(endMinutes).padStart(2, "0")}00`;
    };

    const startStr = formatLocal(date, time);
    const endStr = formatLocal(date, time, duration);

    const title = encodeURIComponent(
      `Sesión — ${patient?.shortName || patient?.firstName}`,
    );
    const details = encodeURIComponent("Sesión de terapia");

    return (
      `https://calendar.google.com/calendar/render?action=TEMPLATE` +
      `&text=${title}` +
      `&dates=${startStr}/${endStr}` +
      `&details=${details}` +
      `&color=${patientColor}`
    );
  };

  const handleOpen = () => {
    window.open(buildGoogleCalendarUrl(), "_blank");
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
      <div className="bg-gray-900 border border-gray-800 rounded-2xl p-6 w-full max-w-md shadow-2xl">
        {/* Header */}
        <div className="flex items-center justify-between mb-5">
          <div>
            <h2 className="text-white font-semibold text-lg">
              Schedule Appointment
            </h2>
            <div className="flex items-center gap-2 mt-1">
              <div
                className="w-3 h-3 rounded-full"
                style={{ backgroundColor: colorHex }}
              />
              <p className="text-gray-400 text-sm">
                {patient?.firstName} {patient?.lastName}
                {patient?.shortName && (
                  <span className="text-gray-500 ml-1">
                    ({patient.shortName})
                  </span>
                )}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-300 text-xl leading-none"
          >
            ✕
          </button>
        </div>

        {/* Form */}
        <div className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm text-gray-400 mb-1">Date</label>
              <input
                type="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
                min={defaultDate}
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              />
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1">Time</label>
              <input
                type="time"
                value={time}
                onChange={(e) => setTime(e.target.value)}
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm text-gray-400 mb-1">
              Duration (minutes)
            </label>
            <input
              type="number"
              value={duration}
              onChange={(e) => setDuration(parseInt(e.target.value))}
              min={15}
              step={5}
              className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
            />
          </div>

          {/* Preview */}
          <div className="bg-gray-800 rounded-lg px-4 py-3 text-sm">
            <p className="text-gray-400 mb-1">Event preview:</p>
            <div className="flex items-center gap-2">
              <div
                className="w-3 h-3 rounded-sm flex-shrink-0"
                style={{ backgroundColor: colorHex }}
              />
              <p className="text-white font-medium">
                Sesión — {patient?.shortName || patient?.firstName}
              </p>
            </div>
            <p className="text-gray-400 mt-1 ml-5">
              {new Date(`${date}T${time}`).toLocaleDateString("es-PE", {
                weekday: "long",
                year: "numeric",
                month: "long",
                day: "numeric",
              })}{" "}
              · {time} · {duration} min
            </p>
          </div>
        </div>

        {/* Actions */}
        <div className="flex gap-3 mt-6">
          <button
            onClick={onClose}
            className="flex-1 bg-gray-800 hover:bg-gray-700 text-white font-semibold rounded-lg py-2.5 transition"
          >
            Cancel
          </button>
          <button
            onClick={handleOpen}
            className="flex-1 bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-lg py-2.5 transition flex items-center justify-center gap-2"
          >
            <span>Open in Google Calendar</span>
            <span>↗</span>
          </button>
        </div>

        <p className="text-gray-600 text-xs text-center mt-3">
          Google Calendar will open in a new tab with the event pre-filled
        </p>
      </div>
    </div>
  );
}
