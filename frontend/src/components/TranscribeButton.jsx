import { useState, useRef } from "react";
import api from "../api/axios";

export default function TranscribeButton({ onTranscribed }) {
  const [recording, setRecording] = useState(false);
  const [transcribing, setTranscribing] = useState(false);
  const [error, setError] = useState(null);
  const [lastBlob, setLastBlob] = useState(null); // last recording (in case it fails for retry)
  const mediaRecorderRef = useRef(null);
  const chunksRef = useRef([]);
  const pollIntervalRef = useRef(null);

  const startRecording = async () => {
    setError(null);
    setLastBlob(null);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mediaRecorder = new MediaRecorder(stream);
      mediaRecorderRef.current = mediaRecorder;
      chunksRef.current = [];

      mediaRecorder.ondataavailable = (e) => {
        if (e.data.size > 0) chunksRef.current.push(e.data);
      };

      mediaRecorder.onstop = async () => {
        stream.getTracks().forEach((t) => t.stop());
        const blob = new Blob(chunksRef.current, { type: "audio/webm" });
        setLastBlob(blob); // for retry
        await sendForTranscription(blob);
      };

      mediaRecorder.start();
      setRecording(true);
    } catch {
      setError("Microphone access denied");
    }
  };

  const stopRecording = () => {
    mediaRecorderRef.current?.stop();
    setRecording(false);
    setTranscribing(true);
  };

  const sendForTranscription = async (blob) => {
    setError(null);
    setTranscribing(true);
    try {
      const formData = new FormData();
      formData.append("audio", blob, "recording.webm");

      const res = await api.post("/transcription/transcribe", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      const { jobId } = res.data;
      startPolling(jobId);
    } catch {
      setError("Failed to send audio.");
      setTranscribing(false);
    }
  };

  const startPolling = (jobId) => {
    pollIntervalRef.current = setInterval(async () => {
      try {
        const res = await api.get(`/transcription/status/${jobId}`);
        const { status, text, error: jobError } = res.data;

        if (status === "DONE") {
          clearInterval(pollIntervalRef.current);
          setTranscribing(false);
          setLastBlob(null); // clear blob on success
          onTranscribed(text);
        } else if (status === "FAILED") {
          clearInterval(pollIntervalRef.current);
          setTranscribing(false);
          setError(jobError || "Transcription failed");
        }
      } catch {
        clearInterval(pollIntervalRef.current);
        setTranscribing(false);
        setError("Lost connection while transcribing");
      }
    }, 2000);
  };

  return (
    <div className="flex items-center gap-2 flex-wrap">
      {!transcribing ? (
        <>
          <button
            type="button"
            onClick={recording ? stopRecording : startRecording}
            className={`flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition ${
              recording
                ? "bg-red-700 hover:bg-red-600 text-white animate-pulse"
                : "bg-gray-700 hover:bg-gray-600 text-gray-300"
            }`}
          >
            {recording ? (
              <>
                <span className="w-2 h-2 bg-red-400 rounded-full"></span>
                Stop Recording
              </>
            ) : (
              <>🎤 Dictate Notes</>
            )}
          </button>

          {/* retry button — only shows after a failure */}
          {error && lastBlob && (
            <button
              type="button"
              onClick={() => sendForTranscription(lastBlob)}
              className="flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium bg-indigo-700 hover:bg-indigo-600 text-white transition"
            >
              🔄 Retry
            </button>
          )}
        </>
      ) : (
        <div className="flex items-center gap-2 text-indigo-400 text-sm">
          <div className="w-4 h-4 border-2 border-indigo-400 border-t-transparent rounded-full animate-spin"></div>
          Transcribing...
        </div>
      )}

      {error && <p className="text-red-400 text-xs w-full">{error}</p>}
    </div>
  );
}
