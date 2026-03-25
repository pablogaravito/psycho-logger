import { useState, useRef } from "react";
import api from "../api/axios";

export default function TranscribeButton({ onTranscribed }) {
  const [recording, setRecording] = useState(false);
  const [transcribing, setTranscribing] = useState(false);
  const [error, setError] = useState(null);
  const mediaRecorderRef = useRef(null);
  const chunksRef = useRef([]);

  const startRecording = async () => {
    setError(null);
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
    try {
      const formData = new FormData();
      formData.append("audio", blob, "recording.webm");

      const res = await api.post("/transcription/transcribe", formData, {
        headers: { "Content-Type": "multipart/form-data" },
        timeout: 120000, // 2 min timeout for long recordings
      });

      onTranscribed(res.data.text);
    } catch {
      setError("Transcription failed. Please try again.");
    } finally {
      setTranscribing(false);
    }
  };

  return (
    <div className="flex items-center gap-2">
      {!transcribing ? (
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
      ) : (
        <div className="flex items-center gap-2 text-indigo-400 text-sm">
          <div className="w-4 h-4 border-2 border-indigo-400 border-t-transparent rounded-full animate-spin"></div>
          Transcribing...
        </div>
      )}
      {error && <p className="text-red-400 text-xs">{error}</p>}
    </div>
  );
}
