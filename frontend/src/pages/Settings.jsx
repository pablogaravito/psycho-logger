import { useState, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "../hooks/useAuth";
import api from "../api/axios";

const CURRENCIES = ["SOL", "USD", "EUR"];
const LANGUAGES = [
  { value: "en", label: "English" },
  { value: "es", label: "Español" },
];

export default function Settings() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const isAdmin = user?.isAdmin;

  const [orgForm, setOrgForm] = useState({
    defaultCurrency: "USD",
    preferredLanguage: "en",
  });

  const [userForm, setUserForm] = useState({
    defaultSessionDuration: 50,
    defaultSessionPrice: "",
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
        defaultCurrency: orgSettings.defaultCurrency || "USD",
        preferredLanguage: orgSettings.preferredLanguage || "en",
      });
    }
  }, [orgSettings]);

  useEffect(() => {
    if (userSettings) {
      setUserForm({
        defaultSessionDuration: userSettings.defaultSessionDuration || 50,
        defaultSessionPrice: userSettings.defaultSessionPrice ?? "",
      });
    }
  }, [userSettings]);

  const orgMutation = useMutation({
    mutationFn: (data) => api.put("/settings/org", data),
    onSuccess: () => {
      queryClient.invalidateQueries(["org-settings"]);
      setOrgSaved(true);
      setTimeout(() => setOrgSaved(false), 3000);
    },
  });

  const userMutation = useMutation({
    mutationFn: (data) => api.put("/settings/user", data),
    onSuccess: () => {
      queryClient.invalidateQueries(["user-settings"]);
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
      defaultSessionDuration: parseInt(userForm.defaultSessionDuration),
      defaultSessionPrice:
        userForm.defaultSessionPrice !== ""
          ? parseFloat(userForm.defaultSessionPrice)
          : null,
    });
  };

  return (
    <div className="max-w-xl mx-auto space-y-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">Settings</h1>
        <p className="text-gray-400 text-sm mt-1">
          Manage your personal and organization preferences
        </p>
      </div>

      {/* Personal settings — visible to all */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
        <h2 className="text-white font-semibold mb-1">Personal Settings</h2>
        <p className="text-gray-500 text-xs mb-5">
          Your personal defaults for sessions and payments
        </p>

        <form onSubmit={handleUserSubmit} className="space-y-5">
          <div>
            <label className="block text-sm text-gray-400 mb-1">
              Default Session Duration (minutes)
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
            <p className="text-gray-500 text-xs mt-1">
              Pre-filled when creating a new session
            </p>
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
            <p className="text-gray-500 text-xs mt-1">
              Pre-filled when marking a session as paid. Can be overridden per
              patient.
            </p>
          </div>

          <div>
            <label className="flex items-center gap-2 text-sm text-white cursor-pointer">
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
            <p className="text-gray-500 text-xs mt-1 ml-5">
              Include inactive patients in the birthdays section
            </p>
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

      {/* Org settings — admins only */}
      {isAdmin && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
          <h2 className="text-white font-semibold mb-1">
            Organization Settings
          </h2>
          <p className="text-gray-500 text-xs mb-5">
            Applies to all therapists in your organization
            {orgSettings?.updatedByName && (
              <span className="ml-2 text-gray-600">
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
                Preferred Language
              </label>
              <select
                value={orgForm.preferredLanguage}
                onChange={(e) =>
                  setOrgForm((prev) => ({
                    ...prev,
                    preferredLanguage: e.target.value,
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
