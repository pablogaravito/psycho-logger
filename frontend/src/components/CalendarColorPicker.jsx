const GOOGLE_COLORS = [
  { id: 1, name: "Lavender", hex: "#7986CB" },
  { id: 2, name: "Sage", hex: "#33B679" },
  { id: 3, name: "Grape", hex: "#8E24AA" },
  { id: 4, name: "Flamingo", hex: "#E67C73" },
  { id: 5, name: "Banana", hex: "#F6BF26" },
  { id: 6, name: "Tangerine", hex: "#F4511E" },
  { id: 7, name: "Peacock", hex: "#039BE5" },
  { id: 8, name: "Graphite", hex: "#616161" },
  { id: 9, name: "Blueberry", hex: "#3F51B5" },
  { id: 10, name: "Basil", hex: "#0B8043" },
  { id: 11, name: "Tomato", hex: "#D50000" },
];

export { GOOGLE_COLORS };

export default function CalendarColorPicker({ value, onChange }) {
  return (
    <div>
      <label className="block text-sm text-gray-400 mb-2">Calendar Color</label>
      <div className="flex flex-wrap gap-2">
        {GOOGLE_COLORS.map((color) => (
          <button
            key={color.id}
            type="button"
            title={color.name}
            onClick={() => onChange(color.id)}
            className="relative w-8 h-8 rounded-full transition-transform hover:scale-110"
            style={{ backgroundColor: color.hex }}
          >
            {value === color.id && (
              <span className="absolute inset-0 flex items-center justify-center">
                <svg
                  className="w-4 h-4 text-white drop-shadow"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth={3}
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
              </span>
            )}
          </button>
        ))}
      </div>
      <p className="text-gray-600 text-xs mt-1">
        {GOOGLE_COLORS.find((c) => c.id === value)?.name || "Peacock"} — used
        when adding sessions to Google Calendar
      </p>
    </div>
  );
}
