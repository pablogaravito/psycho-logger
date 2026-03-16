import { NavLink } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

const navigation = [
  { name: "Dashboard", path: "/", icon: "⊞" },
  { name: "Patients", path: "/patients", icon: "👤" },
  { name: "Sessions", path: "/sessions", icon: "📋" },
  { name: "Payments", path: "/payments", icon: "💰" },
  { name: "Debts", path: "/queries/debts", icon: "💸" },
  { name: "Birthdays", path: "/queries/birthdays", icon: "🎂" },
];

export default function Sidebar() {
  const { user, logout } = useAuth();

  return (
    <div className="w-64 bg-gray-900 border-r border-gray-800 flex flex-col h-screen fixed left-0 top-0">
      {/* Logo */}
      <div className="px-6 py-5 border-b border-gray-800">
        <h1 className="text-white font-bold text-xl tracking-tight">
          PsychoLogger
        </h1>
        <p className="text-gray-500 text-xs mt-0.5">
          {user?.firstName} {user?.lastName}
        </p>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        {navigation.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            end={item.path === "/"}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
                isActive
                  ? "bg-indigo-600 text-white"
                  : "text-gray-400 hover:bg-gray-800 hover:text-white"
              }`
            }
          >
            <span>{item.icon}</span>
            {item.name}
          </NavLink>
        ))}
      </nav>

      {/* User / Logout */}
      <div className="px-3 py-4 border-t border-gray-800">
        <button
          onClick={logout}
          className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400 hover:bg-gray-800 hover:text-white transition"
        >
          <span>🚪</span>
          Sign out
        </button>
      </div>
    </div>
  );
}
