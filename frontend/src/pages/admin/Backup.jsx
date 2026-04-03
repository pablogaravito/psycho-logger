import { useState, useRef } from "react";
import api from "../../api/axios";

export default function Backup() {
  const [backupPassword, setBackupPassword] = useState("");
  const [showBackupPassword, setShowBackupPassword] = useState(false);
  const [backingUp, setBackingUp] = useState(false);
  const [backupError, setBackupError] = useState(null);
  const [backupSuccess, setBackupSuccess] = useState(false);

  const [restoreFile, setRestoreFile] = useState(null);
  const [restorePassword, setRestorePassword] = useState("");
  const [showRestorePassword, setShowRestorePassword] = useState(false);
  const [restoring, setRestoring] = useState(false);
  const [restoreError, setRestoreError] = useState(null);
  const [restoreSuccess, setRestoreSuccess] = useState(false);
  const [showRestoreConfirm, setShowRestoreConfirm] = useState(false);

  const fileInputRef = useRef(null);

  const handleBackup = async () => {
    if (!backupPassword) {
      setBackupError("Please enter your password to encrypt the backup");
      return;
    }
    setBackingUp(true);
    setBackupError(null);
    setBackupSuccess(false);

    try {
      const response = await api.post(
        "/backup/download",
        { password: backupPassword },
        { responseType: "blob" },
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      const date = new Date().toISOString().slice(0, 10);
      link.setAttribute("download", `psychologger_backup_${date}.sql.gz.enc`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);

      setBackupSuccess(true);
      setBackupPassword("");
      setTimeout(() => setBackupSuccess(false), 5000);
    } catch {
      setBackupError("Backup failed. Check your password and try again.");
    } finally {
      setBackingUp(false);
    }
  };

  const handleRestoreConfirm = async () => {
    if (!restoreFile || !restorePassword) return;
    setRestoring(true);
    setRestoreError(null);
    setShowRestoreConfirm(false);

    try {
      const formData = new FormData();
      formData.append("file", restoreFile);
      formData.append("password", restorePassword);

      await api.post("/backup/restore", formData, {
        headers: { "Content-Type": "multipart/form-data" },
        timeout: 120000, // 2 min for large databases
      });

      setRestoreSuccess(true);
      setRestoreFile(null);
      setRestorePassword("");
      if (fileInputRef.current) fileInputRef.current.value = "";
    } catch (err) {
      const msg =
        err.response?.status === 403
          ? "Invalid password"
          : "Restore failed. The file may be corrupted or wrong password.";
      setRestoreError(msg);
    } finally {
      setRestoring(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">Database Backup</h1>
        <p className="text-gray-400 text-sm mt-1">
          Backup and restore your database
        </p>
      </div>

      {/* Backup card */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-6">
        <h2 className="text-white font-semibold mb-1">
          Create Encrypted Backup
        </h2>
        <p className="text-gray-500 text-xs mb-5">
          Your backup will be encrypted with AES-256 using your password. Store
          it somewhere safe. You will need your password to restore.
        </p>

        <div className="space-y-4">
          <div className="relative">
            <input
              type={showBackupPassword ? "text" : "password"}
              value={backupPassword}
              onChange={(e) => setBackupPassword(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleBackup()}
              placeholder="Enter your password to encrypt"
              className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition text-sm pr-16"
            />
            <button
              type="button"
              onClick={() => setShowBackupPassword((v) => !v)}
              className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-300 text-xs"
            >
              {showBackupPassword ? "Hide" : "Show"}
            </button>
          </div>

          {backupError && <p className="text-red-400 text-sm">{backupError}</p>}
          {backupSuccess && (
            <p className="text-green-400 text-sm">
              ✓ Backup downloaded successfully!
            </p>
          )}

          <button
            onClick={handleBackup}
            disabled={backingUp || !backupPassword}
            className="w-full bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-800 disabled:text-indigo-600 text-white font-semibold rounded-lg py-2.5 transition flex items-center justify-center gap-2"
          >
            {backingUp ? (
              <>
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                Creating backup...
              </>
            ) : (
              <>🔒 Download Encrypted Backup</>
            )}
          </button>
        </div>
      </div>

      {/* Restore card */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
        <h2 className="text-white font-semibold mb-1">Restore from Backup</h2>
        <p className="text-gray-500 text-xs mb-5">
          Upload a backup file to restore your database. This will overwrite all
          current data.
        </p>

        {/* Warning banner */}
        <div className="bg-red-900/20 border border-red-800 rounded-lg px-4 py-3 mb-5">
          <p className="text-red-400 text-xs font-medium">
            ⚠️ Warning — Destructive Operation
          </p>
          <p className="text-red-300 text-xs mt-1">
            Restoring will permanently overwrite all current data. Make sure you
            have a recent backup before proceeding.
          </p>
        </div>

        <div className="space-y-4">
          {/* File picker */}
          <div>
            <label className="block text-sm text-gray-400 mb-1">
              Backup File (.sql.gz.enc)
            </label>
            <input
              ref={fileInputRef}
              type="file"
              accept=".enc"
              onChange={(e) => setRestoreFile(e.target.files[0])}
              className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:border-indigo-500 transition file:mr-3 file:py-1 file:px-3 file:rounded file:border-0 file:text-xs file:bg-gray-700 file:text-gray-300 hover:file:bg-gray-600"
            />
          </div>

          {/* Password */}
          <div className="relative">
            <input
              type={showRestorePassword ? "text" : "password"}
              value={restorePassword}
              onChange={(e) => setRestorePassword(e.target.value)}
              placeholder="Password used when backup was created"
              className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition text-sm pr-16"
            />
            <button
              type="button"
              onClick={() => setShowRestorePassword((v) => !v)}
              className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-300 text-xs"
            >
              {showRestorePassword ? "Hide" : "Show"}
            </button>
          </div>

          {restoreError && (
            <p className="text-red-400 text-sm">{restoreError}</p>
          )}
          {restoreSuccess && (
            <p className="text-green-400 text-sm">
              ✓ Database restored successfully! Please refresh the page.
            </p>
          )}

          {/* Confirm dialog */}
          {showRestoreConfirm && (
            <div className="bg-red-900/30 border border-red-800 rounded-lg p-4">
              <p className="text-red-300 text-sm font-medium mb-3">
                Are you absolutely sure? This cannot be undone.
              </p>
              <div className="flex gap-3">
                <button
                  onClick={() => setShowRestoreConfirm(false)}
                  className="flex-1 bg-gray-700 hover:bg-gray-600 text-white text-sm font-medium py-2 rounded-lg transition"
                >
                  Cancel
                </button>
                <button
                  onClick={handleRestoreConfirm}
                  className="flex-1 bg-red-700 hover:bg-red-600 text-white text-sm font-medium py-2 rounded-lg transition"
                >
                  Yes, restore database
                </button>
              </div>
            </div>
          )}

          {!showRestoreConfirm && (
            <button
              onClick={() => {
                if (!restoreFile) {
                  setRestoreError("Please select a backup file");
                  return;
                }
                if (!restorePassword) {
                  setRestoreError("Please enter the backup password");
                  return;
                }
                setRestoreError(null);
                setShowRestoreConfirm(true);
              }}
              disabled={restoring || !restoreFile || !restorePassword}
              className="w-full bg-red-900 hover:bg-red-800 disabled:bg-gray-800 disabled:text-gray-600 text-red-400 font-semibold rounded-lg py-2.5 transition flex items-center justify-center gap-2"
            >
              {restoring ? (
                <>
                  <div className="w-4 h-4 border-2 border-red-400 border-t-transparent rounded-full animate-spin" />
                  Restoring database...
                </>
              ) : (
                <>🔄 Restore from Backup</>
              )}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
