import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import api from "../../api/axios";
import { useAuth } from "../../hooks/useAuth";

const EMPTY_FORM = {
  firstName: "",
  lastName: "",
  email: "",
  password: "",
  isTherapist: true,
  isAdmin: false,
};

export default function TeamManagement() {
  const { user: currentUser } = useAuth();
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [showPatientAssign, setShowPatientAssign] = useState(null);

  const { data: users, isLoading } = useQuery({
    queryKey: ["users"],
    queryFn: () => api.get("/users").then((r) => r.data),
  });

  const { data: patients } = useQuery({
    queryKey: ["patients"],
    queryFn: () => api.get("/patients").then((r) => r.data),
  });

  // fetch assignments to know who is assigned to whom
  const { data: assignments } = useQuery({
    queryKey: ["assignments"],
    queryFn: () => api.get("/assignments").then((r) => r.data),
  });

  const createMutation = useMutation({
    mutationFn: (data) => api.post("/users", data),
    onSuccess: () => {
      queryClient.invalidateQueries(["users"]);
      setShowForm(false);
      setForm(EMPTY_FORM);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => api.put(`/users/${id}`, data),
    onSuccess: () => {
      queryClient.invalidateQueries(["users"]);
      setEditingUser(null);
      setShowForm(false);
      setForm(EMPTY_FORM);
    },
  });

  const reactivateMutation = useMutation({
    mutationFn: (id) => api.put(`/users/${id}`, { isActive: true }),
    onSuccess: () => queryClient.invalidateQueries(["users"]),
  });

  const deactivateMutation = useMutation({
    mutationFn: (id) => api.delete(`/users/${id}`),
    onSuccess: () => queryClient.invalidateQueries(["users"]),
  });

  const assignMutation = useMutation({
    mutationFn: ({ patientId, therapistId }) =>
      api.put(`/patients/${patientId}/assign?therapistId=${therapistId}`),
    onSuccess: () => {
      queryClient.invalidateQueries(["patients"]);
      queryClient.invalidateQueries(["assignments"]);
    },
  });

  const unassignMutation = useMutation({
    mutationFn: (patientId) => api.put(`/patients/${patientId}/unassign`),
    onSuccess: () => {
      queryClient.invalidateQueries(["patients"]);
      queryClient.invalidateQueries(["assignments"]);
    },
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (editingUser) {
      updateMutation.mutate({ id: editingUser.id, data: form });
    } else {
      createMutation.mutate(form);
    }
  };

  const startEdit = (user) => {
    setEditingUser(user);
    setForm({
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      password: "",
      isTherapist: user.isTherapist,
      isAdmin: user.isAdmin,
    });
    setShowForm(true);
  };

  const cancelForm = () => {
    setShowForm(false);
    setEditingUser(null);
    setForm(EMPTY_FORM);
  };

  // check if user is the last active admin
  const isLastActiveAdmin = (userId) => {
    const activeAdmins = users?.filter((u) => u.isAdmin && u.isActive) || [];
    return activeAdmins.length === 1 && activeAdmins[0].id === userId;
  };

  // get currently assigned patients for a therapist
  const getAssignedPatients = (therapistId) => {
    if (!assignments || !patients) return [];
    const assignedIds = assignments
      .filter((a) => a.therapistId === therapistId && !a.unassignedAt)
      .map((a) => a.patientId);
    return patients.filter((p) => assignedIds.includes(p.id));
  };

  // get unassigned patients (no active assignment)
  const getUnassignedPatients = () => {
    if (!assignments || !patients) return [];
    const assignedPatientIds = assignments
      .filter((a) => !a.unassignedAt)
      .map((a) => a.patientId);
    return patients.filter(
      (p) => p.isActive && !assignedPatientIds.includes(p.id),
    );
  };

  if (isLoading) return <div className="text-gray-400">Loading...</div>;

  return (
    <div className="max-w-4xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Team Management</h1>
          <p className="text-gray-400 text-sm mt-1">
            Manage therapists and patient assignments
          </p>
        </div>
        {!showForm && (
          <button
            onClick={() => setShowForm(true)}
            className="bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold px-4 py-2 rounded-lg transition"
          >
            + Add Therapist
          </button>
        )}
      </div>

      {/* Form */}
      {showForm && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-6">
          <h2 className="text-white font-semibold mb-4">
            {editingUser ? "Edit Therapist" : "New Therapist"}
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm text-gray-400 mb-1">
                  First Name *
                </label>
                <input
                  name="firstName"
                  value={form.firstName}
                  onChange={handleChange}
                  required
                  className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-400 mb-1">
                  Last Name *
                </label>
                <input
                  name="lastName"
                  value={form.lastName}
                  onChange={handleChange}
                  required
                  className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm text-gray-400 mb-1">
                  Email *
                </label>
                <input
                  type="email"
                  name="email"
                  value={form.email}
                  onChange={handleChange}
                  required
                  className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-400 mb-1">
                  {editingUser
                    ? "New Password (leave blank to keep)"
                    : "Password *"}
                </label>
                <input
                  type="password"
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  required={!editingUser}
                  className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 focus:outline-none focus:border-indigo-500 transition"
                />
              </div>
            </div>

            <div className="flex gap-6">
              <label className="flex items-center gap-2 text-sm text-gray-400 cursor-pointer">
                <input
                  type="checkbox"
                  name="isTherapist"
                  checked={form.isTherapist}
                  onChange={handleChange}
                  className="accent-indigo-500"
                />
                Therapist
              </label>
              <label className="flex items-center gap-2 text-sm text-gray-400 cursor-pointer">
                <input
                  type="checkbox"
                  name="isAdmin"
                  checked={form.isAdmin}
                  onChange={handleChange}
                  className="accent-indigo-500"
                />
                Admin
              </label>
            </div>

            {(createMutation.isError || updateMutation.isError) && (
              <p className="text-red-400 text-sm">
                Something went wrong. Please try again.
              </p>
            )}

            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={cancelForm}
                className="flex-1 bg-gray-800 hover:bg-gray-700 text-white font-semibold rounded-lg py-2.5 transition"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={createMutation.isPending || updateMutation.isPending}
                className="flex-1 bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-800 text-white font-semibold rounded-lg py-2.5 transition"
              >
                {createMutation.isPending || updateMutation.isPending
                  ? "Saving..."
                  : editingUser
                    ? "Update"
                    : "Create Therapist"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Team list */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden mb-6">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-800">
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Name
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Email
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Roles
              </th>
              <th className="text-left text-gray-400 font-medium px-6 py-3">
                Status
              </th>
              <th className="px-6 py-3"></th>
            </tr>
          </thead>
          <tbody>
            {users?.map((user) => (
              <tr
                key={user.id}
                className="border-b border-gray-800 last:border-0"
              >
                <td className="px-6 py-4 text-white font-medium">
                  {user.firstName} {user.lastName}
                  {user.id === currentUser?.id && (
                    <span className="ml-2 text-xs text-gray-500">(you)</span>
                  )}
                </td>
                <td className="px-6 py-4 text-gray-400">{user.email}</td>
                <td className="px-6 py-4">
                  <div className="flex gap-1">
                    {user.isTherapist && (
                      <span className="text-xs bg-indigo-900 text-indigo-400 px-2 py-0.5 rounded-full">
                        Therapist
                      </span>
                    )}
                    {user.isAdmin && (
                      <span className="text-xs bg-purple-900 text-purple-400 px-2 py-0.5 rounded-full">
                        Admin
                      </span>
                    )}
                  </div>
                </td>
                <td className="px-6 py-4">
                  <span
                    className={`text-xs font-semibold px-2 py-1 rounded-full ${
                      user.isActive
                        ? "bg-green-900 text-green-400"
                        : "bg-gray-800 text-gray-500"
                    }`}
                  >
                    {user.isActive ? "Active" : "Inactive"}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <div className="flex gap-2 justify-end">
                    {user.isActive && (
                      <button
                        onClick={() => startEdit(user)}
                        className="text-indigo-400 hover:text-indigo-300 text-xs font-medium"
                      >
                        Edit
                      </button>
                    )}
                    {user.isTherapist && user.isActive && (
                      <button
                        onClick={() =>
                          setShowPatientAssign(
                            showPatientAssign === user.id ? null : user.id,
                          )
                        }
                        className="text-blue-400 hover:text-blue-300 text-xs font-medium"
                      >
                        Patients
                      </button>
                    )}
                    {user.id !== currentUser?.id &&
                      user.isActive &&
                      !isLastActiveAdmin(user.id) && (
                        <button
                          onClick={() => deactivateMutation.mutate(user.id)}
                          className="text-red-400 hover:text-red-300 text-xs font-medium"
                        >
                          Deactivate
                        </button>
                      )}
                    {!user.isActive && (
                      <button
                        onClick={() => reactivateMutation.mutate(user.id)}
                        className="text-green-400 hover:text-green-300 text-xs font-medium"
                      >
                        Reactivate
                      </button>
                    )}
                    {user.id === currentUser?.id &&
                      isLastActiveAdmin(user.id) && (
                        <span className="text-gray-600 text-xs">
                          Last admin
                        </span>
                      )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Patient assignment panel */}
      {showPatientAssign && (
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-white font-semibold">
              Patients —{" "}
              {users?.find((u) => u.id === showPatientAssign)?.firstName}{" "}
              {users?.find((u) => u.id === showPatientAssign)?.lastName}
            </h2>
            <button
              onClick={() => setShowPatientAssign(null)}
              className="text-gray-500 hover:text-gray-300 text-sm"
            >
              Close
            </button>
          </div>

          {/* Currently assigned */}
          <div className="mb-5">
            <h3 className="text-gray-400 text-xs font-medium uppercase tracking-wider mb-2">
              Currently Assigned
            </h3>
            {getAssignedPatients(showPatientAssign).length === 0 ? (
              <p className="text-gray-600 text-sm">No patients assigned</p>
            ) : (
              <div className="space-y-2">
                {getAssignedPatients(showPatientAssign).map((patient) => (
                  <div
                    key={patient.id}
                    className="flex items-center justify-between bg-gray-800 px-4 py-3 rounded-lg"
                  >
                    <div className="flex items-center gap-2">
                      {patient.hasDebtFlag && <span>🚩</span>}
                      <span className="text-white text-sm">
                        {patient.firstName} {patient.lastName}
                      </span>
                      {patient.shortName && (
                        <span className="text-gray-500 text-xs">
                          ({patient.shortName})
                        </span>
                      )}
                    </div>
                    <button
                      onClick={() => unassignMutation.mutate(patient.id)}
                      disabled={unassignMutation.isPending}
                      className="bg-red-900 hover:bg-red-800 text-red-400 text-xs px-3 py-1.5 rounded-lg transition"
                    >
                      Unassign
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Unassigned patients */}
          <div>
            <h3 className="text-gray-400 text-xs font-medium uppercase tracking-wider mb-2">
              Unassigned Patients
            </h3>
            {getUnassignedPatients().length === 0 ? (
              <p className="text-gray-600 text-sm">No unassigned patients</p>
            ) : (
              <div className="space-y-2">
                {getUnassignedPatients().map((patient) => (
                  <div
                    key={patient.id}
                    className="flex items-center justify-between bg-gray-800 px-4 py-3 rounded-lg"
                  >
                    <div className="flex items-center gap-2">
                      {patient.hasDebtFlag && <span>🚩</span>}
                      <span className="text-white text-sm">
                        {patient.firstName} {patient.lastName}
                      </span>
                      {patient.shortName && (
                        <span className="text-gray-500 text-xs">
                          ({patient.shortName})
                        </span>
                      )}
                    </div>
                    <button
                      onClick={() =>
                        assignMutation.mutate({
                          patientId: patient.id,
                          therapistId: showPatientAssign,
                        })
                      }
                      disabled={assignMutation.isPending}
                      className="bg-indigo-700 hover:bg-indigo-600 text-white text-xs px-3 py-1.5 rounded-lg transition"
                    >
                      Assign
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
