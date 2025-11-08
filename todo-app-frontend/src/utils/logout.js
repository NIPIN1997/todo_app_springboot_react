import { toast } from "react-toastify";
import { logoutApi } from "../api/UserApi";

export async function logout(jwtToken) {
  sessionStorage.removeItem("jwtToken");
  sessionStorage.removeItem("refreshToken");
  sessionStorage.removeItem("isAuthenticated");
  try {
    console.log("logout");
    await logoutApi(jwtToken);
  } catch (error) {
    toast.error("Logout failed.");
  } finally {
    window.location.href = "/";
  }
}
