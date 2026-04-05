import { createContext, useState, useEffect } from "react";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [preferences, setPreferences] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const userData = localStorage.getItem("user");
    const savedPrefs = localStorage.getItem("preferences");
    if (token && userData) {
      setUser(JSON.parse(userData));
      if (savedPrefs) setPreferences(JSON.parse(savedPrefs));
    }
    setLoading(false);
  }, []);

  const fetchPreferences = async (token) => {
    try {
      const res = await fetch(`${import.meta.env.VITE_API_URL}/preferences`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const prefs = await res.json();
      localStorage.setItem("preferences", JSON.stringify(prefs));
      setPreferences(prefs);
    } catch {
      // use safe defaults if fetch fails
      const defaults = {
        transcriptionLanguage: "es",
        uiLanguage: "es",
        dateFormat: "DD/MM/YYYY",
      };
      setPreferences(defaults);
    }
  };

  const login = async (userData, token) => {
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(userData));
    setUser(userData);
    await fetchPreferences(token);
  };

  const refreshPreferences = async () => {
    const token = localStorage.getItem("token");
    if (token) await fetchPreferences(token);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    localStorage.removeItem("preferences");
    setUser(null);
    setPreferences(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        logout,
        loading,
        preferences,
        refreshPreferences,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
