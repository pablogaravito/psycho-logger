export default function Pagination({ data, setPage }) {
  if (!data || data.totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-between px-6 py-4 border-t border-gray-800">
      <p className="text-gray-500 text-xs">
        Showing {data.page * data.size + 1}–
        {Math.min((data.page + 1) * data.size, data.totalElements)} of{" "}
        {data.totalElements}
      </p>
      <div className="flex gap-2">
        <button
          onClick={() => setPage((p) => p - 1)}
          disabled={data.first}
          className="px-3 py-1.5 bg-gray-800 text-gray-400 rounded-lg text-xs disabled:opacity-50 hover:bg-gray-700 transition"
        >
          ← Previous
        </button>
        <span className="px-3 py-1.5 text-gray-400 text-xs">
          {data.page + 1} / {data.totalPages}
        </span>
        <button
          onClick={() => setPage((p) => p + 1)}
          disabled={data.last}
          className="px-3 py-1.5 bg-gray-800 text-gray-400 rounded-lg text-xs disabled:opacity-50 hover:bg-gray-700 transition"
        >
          Next →
        </button>
      </div>
    </div>
  );
}
