import { toast } from "react-toastify";
import { logoutApi } from "../api/UserApi";

export async function logout(jwtToken) {
  sessionStorage.removeItem("jwtToken");
  sessionStorage.removeItem("refreshToken");
  sessionStorage.removeItem("isAuthenticated");
  const deviceId = localStorage.getItem("deviceId");
  localStorage.removeItem("deviceId");
  try {
    await logoutApi(jwtToken, deviceId);
  } catch (error) {
    toast.error("Logout failed.");
  } finally {
    window.location.href = "/";
  }
}

export async function forcedLogout() {
  toast.error("Access denied.");
  setTimeout(() => {
    sessionStorage.removeItem("jwtToken");
    sessionStorage.removeItem("refreshToken");
    sessionStorage.removeItem("isAuthenticated");
    localStorage.removeItem("deviceId");
    window.location.href = "/";
  }, 3000);
}
