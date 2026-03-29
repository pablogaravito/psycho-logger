import { useState, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "../hooks/useAuth";
import api from "../api/axios";

const CURRENCIES = ["SOL", "USD", "EUR"];
const LANGUAGES = [
  { value: "en", label: "English" },
  { value: "es", label: "Español" },
];
const DATE_FORMATS = [
  { value: "DD/MM/YYYY", label: "DD/MM/YYYY (26/03/2026)" },
  { value: "MM/DD/YYYY", label: "MM/DD/YYYY (03/26/2026)" },
  { value: "YYYY-MM-DD", label: "YYYY-MM-DD (2026-03-26)" },
  { value: "DD MMM YYYY", label: "DD MMM YYYY (26 Mar 2026)" },
];

const DEFAULT_ORG_OPTION = { value: "", label: "Use Default" };

// Then create a combined array if you need it
const ALL_LANGUAGE_OPTIONS = [DEFAULT_ORG_OPTION, ...LANGUAGES];

export default function Settings() {
  const { user, refreshPreferences } = useAuth();
  const queryClient = useQueryClient();
  const isAdmin = user?.isAdmin;

  // tab state — default to 'personal', admins can switch to 'org'
  const [activeTab, setActiveTab] = useState("personal");

  const [orgForm, setOrgForm] = useState({
    defaultCurrency: "SOL",
    transcriptionLanguage: "es",
    uiLanguage: "es",
    dateFormat: "DD/MM/YYYY",
  });

  const [userForm, setUserForm] = useState({
    defaultSessionDuration: 50,
    defaultSessionPrice: "",
    showInactiveBirthdays: false,
    transcriptionLanguage: "",
    uiLanguage: "",
    dateFormat: "",
    timeFormat: "24h",
  });

  const [orgSaved, setOrgSaved] = useState(false);
  const [userSaved, setUserSaved] = useState(false);

  const { data: orgSettings } = useQuery({
    queryKey: ["org-settings"],
    queryFn: () => api.get("/settings/org").then((r) => r.data),
    enabled: isAdmin,
  });

  const { data: userSettings } = useQuery({
    queryKey: ["user-settings"],
    queryFn: () => api.get("/settings/user").then((r) => r.data),
  });

  useEffect(() => {
    if (orgSettings) {
      setOrgForm({
        defaultCurrency: orgSettings.defaultCurrency || "SOL",
        transcriptionLanguage: orgSettings.transcriptionLanguage || "es",
        uiLanguage: orgSettings.uiLanguage || "es",
        dateFormat: orgSettings.dateFormat || "DD/MM/YYYY",
      });
    }
  }, [orgSettings]);

  useEffect(() => {
    if (userSettings) {
      setUserForm({
        defaultSessionDuration: userSettings.defaultSessionDuration || 50,
        defaultSessionPrice: userSettings.defaultSessionPrice ?? "",
        showInactiveBirthdays: userSettings.showInactiveBirthdays || false,
        transcriptionLanguage: userSettings.transcriptionLanguage || "",
        uiLanguage: userSettings.uiLanguage || "",
        dateFormat: userSettings.dateFormat || "",
        timeFormat: userSettings.timeFormat || "24h",
      });
    }
  }, [userSettings]);

  const orgMutation = useMutation({
    mutationFn: (data) => api.put("/settings/org", data),
    onSuccess: () => {
      queryClient.invalidateQueries(["org-settings"]);
      refreshPreferences();
      setOrgSaved(true);
      setTimeout(() => setOrgSaved(false), 3000);
    },
  });

  const userMutation = useMutation({
    mutationFn: (data) => api.put("/settings/user", data),
    onSuccess: () => {
      queryClient.invalidateQueries(["user-settings"]);
      refreshPreferences();
      setUserSaved(true);
      setTimeout(() => setUserSaved(false), 3000);
    },
  });

  const handleOrgSubmit = (e) => {
    e.preventDefault();
    orgMutation.mutate(orgForm);
  };

  const handleUserSubmit = (e) => {
    e.preventDefault();
    userMutation.mutate({
      ...userForm,
      defaultSessionDuration: parseInt(userForm.defaultSessionDuration),
      defaultSessionPrice:
        userForm.defaultSessionPrice !== ""
          ? parseFloat(userForm.defaultSessionPrice)
          : null,
      transcriptionLanguage: userForm.transcriptionLanguage || null,
      uiLanguage: userForm.uiLanguage || null,
      dateFormat: userForm.dateFormat || null,
    });
  };

  return (
    <div className="max-w-xl mx-auto">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">Settings</h1>
        <p className="text-gray-400 text-sm mt-1">
          Manage your personal and organization preferences
        </p>
      </div>

      {/* Tabs — only show if admin */}
      {isAdmin && (
        <div className="flex gap-1 bg-gray-900 border border-gray-800 rounded-xl p-1 mb-6">
          <button
            onClick={() => setActiveTab("personal")}
            className={`flex-1 py-2 rounded-lg text-sm font-medium transition ${
              activeTab === "personal"
                ? "bg-indigo-600 text-white"
                : "text-gray-400 hover:text-white"
            }`}
          >
            👤 Personal
          </button>
          <button
            onClick={() => setActiveTab("org")}
            className={`flex-1 py-2 rounded-lg text-sm font-medium transition ${
              activeTab === "org"
                ? "bg-indigo-600 text-white"
                : "text-gray-400 hover:text-white"
            }`}
          >
            🏢 Organization
          </button>
        </div>
      )}

      {/* Personal Settings */}
      {activeTab === "personal" && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
          <h2 className="text-white font-semibold mb-1">Personal Settings</h2>
          <p className="text-gray-500 text-xs mb-5">
            Your personal defaults. Overrides org settings where applicable.
          </p>

          <form onSubmit={handleUserSubmit} className="space-y-5">
            {/* Session defaults — two per row */}

            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Session Duration (min)
              </label>
              <input
                type="number"
                value={userForm.defaultSessionDuration}
                onChange={(e) =>
                  setUserForm((prev) => ({
                    ...prev,
                    defaultSessionDuration: e.target.value,
                  }))
                }
                min={1}
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              />
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Default Session Price
              </label>
              <input
                type="number"
                value={userForm.defaultSessionPrice}
                onChange={(e) =>
                  setUserForm((prev) => ({
                    ...prev,
                    defaultSessionPrice: e.target.value,
                  }))
                }
                min={0}
                step="0.01"
                placeholder="e.g. 150.00"
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              />
            </div>

            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Transcription Language
              </label>
              <select
                value={userForm.transcriptionLanguage}
                onChange={(e) =>
                  setUserForm((prev) => ({
                    ...prev,
                    transcriptionLanguage: e.target.value,
                  }))
                }
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                {ALL_LANGUAGE_OPTIONS.map((l) => (
                  <option key={l.value} value={l.value}>
                    {l.label}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Interface Language
              </label>
              <select
                value={userForm.uiLanguage}
                onChange={(e) =>
                  setUserForm((prev) => ({
                    ...prev,
                    uiLanguage: e.target.value,
                  }))
                }
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                {ALL_LANGUAGE_OPTIONS.map((l) => (
                  <option key={l.value} value={l.value}>
                    {l.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Date format */}
            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Date Format
              </label>
              <select
                value={userForm.dateFormat}
                onChange={(e) =>
                  setUserForm((prev) => ({
                    ...prev,
                    dateFormat: e.target.value,
                  }))
                }
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                <option value="">Use org default</option>
                {DATE_FORMATS.map((f) => (
                  <option key={f.value} value={f.value}>
                    {f.label}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Time Format
              </label>
              <select
                value={userForm.timeFormat || "24h"}
                onChange={(e) =>
                  setUserForm((prev) => ({
                    ...prev,
                    timeFormat: e.target.value,
                  }))
                }
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                <option value="24h">24h (22:00)</option>
                <option value="12h">12h (10:00 PM)</option>
              </select>
            </div>

            {/* Birthdays toggle */}
            <div>
              <label className="flex items-center gap-2 text-sm text-gray-400 cursor-pointer">
                <input
                  type="checkbox"
                  checked={userForm.showInactiveBirthdays || false}
                  onChange={(e) =>
                    setUserForm((prev) => ({
                      ...prev,
                      showInactiveBirthdays: e.target.checked,
                    }))
                  }
                  className="accent-indigo-500"
                />
                Show inactive patient birthdays
              </label>
            </div>

            <button
              type="submit"
              disabled={userMutation.isPending}
              className="w-full bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-800 text-white font-semibold rounded-lg py-2.5 transition"
            >
              {userMutation.isPending ? "Saving..." : "Save Personal Settings"}
            </button>
            {userSaved && (
              <p className="text-green-400 text-sm text-center">
                ✓ Personal settings saved
              </p>
            )}
          </form>
        </div>
      )}

      {/* Org Settings — admin only */}
      {activeTab === "org" && isAdmin && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
          <h2 className="text-white font-semibold mb-1">
            Organization Settings
          </h2>
          <p className="text-gray-500 text-xs mb-5">
            Applies to all users unless overridden personally.
            {orgSettings?.updatedByName && (
              <span className="ml-1 text-gray-600">
                · Last updated by {orgSettings.updatedByName}
              </span>
            )}
          </p>

          <form onSubmit={handleOrgSubmit} className="space-y-5">
            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Default Currency
              </label>
              <select
                value={orgForm.defaultCurrency}
                onChange={(e) =>
                  setOrgForm((prev) => ({
                    ...prev,
                    defaultCurrency: e.target.value,
                  }))
                }
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                {CURRENCIES.map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Transcription Language
              </label>
              <select
                value={orgForm.transcriptionLanguage}
                onChange={(e) =>
                  setOrgForm((prev) => ({
                    ...prev,
                    transcriptionLanguage: e.target.value,
                  }))
                }
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                {LANGUAGES.map((l) => (
                  <option key={l.value} value={l.value}>
                    {l.label}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Interface Language
              </label>
              <select
                value={orgForm.uiLanguage}
                onChange={(e) =>
                  setOrgForm((prev) => ({
                    ...prev,
                    uiLanguage: e.target.value,
                  }))
                }
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                {LANGUAGES.map((l) => (
                  <option key={l.value} value={l.value}>
                    {l.label}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1">
                Date Format
              </label>
              <select
                value={orgForm.dateFormat}
                onChange={(e) =>
                  setOrgForm((prev) => ({
                    ...prev,
                    dateFormat: e.target.value,
                  }))
                }
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
              >
                {DATE_FORMATS.map((f) => (
                  <option key={f.value} value={f.value}>
                    {f.label}
                  </option>
                ))}
              </select>
            </div>

            <button
              type="submit"
              disabled={orgMutation.isPending}
              className="w-full bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-800 text-white font-semibold rounded-lg py-2.5 transition"
            >
              {orgMutation.isPending
                ? "Saving..."
                : "Save Organization Settings"}
            </button>
            {orgSaved && (
              <p className="text-green-400 text-sm text-center">
                ✓ Organization settings saved
              </p>
            )}
          </form>
        </div>
      )}
    </div>
  );
}
