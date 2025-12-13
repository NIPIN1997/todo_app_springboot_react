import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { forcedLogout } from "../utils/logout";

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const BASE_URL_USERS = `${BASE_URL}/api/v1/users`;

export const api = axios.create({ baseURL: BASE_URL_USERS });

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 403) {
      forcedLogout();
    }
    return Promise.reject(error);
  }
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
  const decodedToken = jwtDecode(jwtToken);
  const userId = decodedToken.id;
  const response = await api.get(`/get-user/${userId}`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const editUser = async (jwtToken, data) => {
  const decodedToken = jwtDecode(jwtToken);
  const userId = decodedToken.id;
  const response = await api.put(`/edit-user/${userId}`, data, {
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
    }
  );
  return response;
};

export const loggedInDevicesApi = async (jwtToken) => {
  const decodedToken = jwtDecode(jwtToken);
  const userId = decodedToken.id;
  const response = await api.get(`/logged-in-devices/${userId}`, {
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
    }
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
