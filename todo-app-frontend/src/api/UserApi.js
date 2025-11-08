import axios from "axios";
import { jwtDecode } from "jwt-decode";

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const BASE_URL_USERS = `${BASE_URL}/api/v1/users`;

export const loginApi = async (data) => {
  const response = await axios.post(`${BASE_URL_USERS}/login`, data);
  return response;
};

export const tokenRefreshApi = async (jwtToken, refreshToken) => {
  const response = await axios.post(
    `${BASE_URL_USERS}/refresh-token`,
    { refreshToken },
    {
      headers: {
        Authorization: `Bearer ${jwtToken}`,
      },
    }
  );
  return response;
};

export const getUserById = async (jwtToken) => {
  const decodedToken = jwtDecode(jwtToken);
  const userId = decodedToken.id;
  const response = await axios.get(`${BASE_URL_USERS}/get-user/${userId}`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const editUser = async (jwtToken, data) => {
  const decodedToken = jwtDecode(jwtToken);
  const userId = decodedToken.id;
  const response = await axios.put(
    `${BASE_URL_USERS}/edit-user/${userId}`,
    data,
    {
      headers: {
        Authorization: `Bearer ${jwtToken}`,
      },
    }
  );
  return response;
};

export const logoutApi = async (jwtToken) => {
  const response = await axios.post(
    `${BASE_URL_USERS}/logout`,
    {},
    {
      headers: {
        Authorization: `Bearer ${jwtToken}`,
      },
    }
  );
  console.log(response);
  return response;
};
