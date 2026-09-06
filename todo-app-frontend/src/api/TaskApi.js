import axios from "axios";
import { forcedLogout } from "../utils/logout";
import { toast } from "react-toastify";

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;
const BASE_URL_TASK = `${BASE_URL}/api/v1/task`;

export const api = axios.create({
  baseURL: BASE_URL_TASK,
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

export const addTaskApi = async (jwtToken, data) => {
  const response = await api.post(`/add-task`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const dragTaskApi = async (jwtToken, data) => {
  const response = await api.put(`/drag-task`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const getTaskById = async (jwtToken, taskId) => {
  const response = await api.get(`/get-task/${taskId}`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const deleteTaskById = async (jwtToken, taskId) => {
  const response = await api.delete(`/delete-task/${taskId}`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const getTaskDetailsEdit = async (jwtToken, taskId) => {
  const response = await api.get(`/get-task-details-edit/${taskId}`, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};

export const editTaskApi = async (jwtToken, data) => {
  const response = await api.put(`/edit-task`, data, {
    headers: {
      Authorization: `Bearer ${jwtToken}`,
    },
  });
  return response;
};
