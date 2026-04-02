const PAGE_SIZE_OPTIONS = [10, 15, 20, 25, 30];

export default function Pagination({ data, setPage, pageSize, setPageSize }) {
  if (!data) return null;

  return (
    <div className="flex items-center justify-between px-6 py-4 border-t border-gray-800">
      <div className="flex items-center gap-3">
        <p className="text-gray-500 text-xs">
          {data.totalElements > 0 ? (
            <>
              {data.page * data.size + 1}–
              {Math.min((data.page + 1) * data.size, data.totalElements)} of{" "}
              {data.totalElements}
            </>
          ) : (
            "No results"
          )}
        </p>
        {setPageSize && data.totalElements > PAGE_SIZE_OPTIONS[0] && (
          <select
            value={pageSize}
            onChange={(e) => {
              setPageSize(parseInt(e.target.value));
              setPage(0);
            }}
            className="bg-gray-800 border border-gray-700 text-gray-400 rounded px-2 py-1 text-xs focus:outline-none focus:border-indigo-500"
          >
            {PAGE_SIZE_OPTIONS.map((s) => (
              <option key={s} value={s}>
                {s} per page
              </option>
            ))}
          </select>
        )}
      </div>

      {data.totalPages > 1 && (
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
      )}
    </div>
  );
}
