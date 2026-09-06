import axios from "axios";
import { forcedLogout } from "../utils/logout";
import { toast } from "react-toastify";

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const BASE_URL_USERS = `${BASE_URL}/api/v1/users`;

export const api = axios.create({ baseURL: BASE_URL_USERS });

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

export const loginApi = async (data) => {
  const response = await api.post(`/login`, data);
  return response;
};

export const tokenRefreshApi = async (jwtToken, refreshToken, deviceId) => {
  const data = {
    refreshToken: refreshToken,
    deviceId: deviceId,
  };
  const response = await api.post(`/refresh-token`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const getUserById = async (jwtToken) => {
  const response = await api.get(`/get-user`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const editUser = async (jwtToken, data) => {
  const response = await api.put(`/edit-user`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const logoutApi = async (jwtToken, deviceId) => {
  const response = await api.post(
    `/logout`,
    { deviceId },
    {
      headers: {
        Authorization: `Bearer ${jwtToken}`,
      },
    },
  );
  return response;
};

export const loggedInDevicesApi = async (jwtToken) => {
  const response = await api.get(`/logged-in-devices`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const deviceLogoutApi = async (jwtToken, deviceId) => {
  const response = await api.post(
    `/logout-device`,
    { deviceId },
    {
      headers: {
        Authorization: `Bearer ${jwtToken}`,
      },
    },
  );
  return response;
};

export const rememberMeLoginApi = async (data) => {
  const response = await api.post(`/remember-me-login`, data);
  return response;
};

export const signupApi = async (data) => {
  const response = await api.post(`/signup`, data);
  return response;
};

export const checkUsername = async (jwtToken, username) => {
  const response = await api.get(`/check-username-existence`, {
    params: { username },
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};
