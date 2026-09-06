import axios from "axios";
import { forcedLogout } from "../utils/logout";
import { toast } from "react-toastify";

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const BASE_URL_DASHBOARD = `${BASE_URL}/api/v1/dashboard`;

export const api = axios.create({
  baseURL: BASE_URL_DASHBOARD,
  withCredentials: true,
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const status = error.response.status;
      if (status === 403) {
        forcedLogout();
      } else if (status >= 400 && status < 500) {
        toast.error(error.response.data?.message || "An error has occured");
      } else if (status >= 500) {
        toast.error(error.response.data?.message || "Internal server error");
      } else if (error.request) {
        toast.error("Network error");
      } else {
        toast.error("An error occured.");
      }
    }
    return Promise.reject(error);
  },
);

export const getDashboardsByUserId = async (jwtToken) => {
  const response = await api.get(`/get-dashboards-by-user`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const addDashboardApi = async (jwtToken, data) => {
  const response = await api.post(`/add-dashboard`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const getInvitationsApi = async (jwtToken) => {
  const response = await api.get("/get-invitations", {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const acceptOrRejectInvitationApi = async (jwtToken, data) => {
  const response = await api.put(`/accept-or-reject-invitation`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const getAllDashboardsApi = async (category, jwtToken) => {
  const response = await api.get(`/get-all-dashboards`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
    params: {
      category: category,
    },
  });
  return response;
};

export const getDashboardById = async (id, jwtToken) => {
  const response = await api.get(`/get-dashboard-by-id/${id}`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const getColumnNamesForDashboard = async (dashboardId, jwtToken) => {
  const response = await api.get(`/get-column-names/${dashboardId}`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const getMemberNamesForDashboard = async (dashboardId, jwtToken) => {
  const response = await api.get(`/get-member-names/${dashboardId}`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const deleteDashboardById = async (dashboardId, jwtToken) => {
  const response = await api.delete(`/delete-dashboard/${dashboardId}`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const archiveDashboardById = async (dashboardId, jwtToken) => {
  const response = await api.put(`/archive-dashboard/${dashboardId}`, null, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const unarchiveDashboardById = async (dashboardId, jwtToken) => {
  const response = await api.put(`/unarchive-dashboard/${dashboardId}`, null, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const getDashboardDetailsForEdit = async (dashboardId, jwtToken) => {
  const response = await api.get(
    `/get-dashboard-details-for-edit/${dashboardId}`,
    {
      headers: {
        Authorization: `Bearer ${jwtToken}`,
      },
    },
  );
  return response;
};

export const editDashboardNameApi = async (jwtToken, data) => {
  const response = await api.put(`/edit-dashboard-name`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const removeDashboardMember = async (jwtToken, data) => {
  const response = await api.put(`/remove-dashboard-member`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const promoteDashboardMember = async (jwtToken, data) => {
  const response = await api.put(`/promote-member-to-master`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const addDashboardMemberAPi = async (jwtToken, data) => {
  const response = await api.post(`/add-dashboard-member`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const editDashboardColumnName = async (jwtToken, data) => {
  const response = await api.put(`/edit-dashboard-column-name`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};
