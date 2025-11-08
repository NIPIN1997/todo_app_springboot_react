import { loginApi, tokenRefreshApi } from "../api/UserApi";
import { useEffect, useRef, useState } from "react";
import AuthContext from "../context/AuthContext.jsx";
import { toast } from "react-toastify";
import { logout } from "../utils/logout.js";

export const AuthProvider = ({ children }) => {
  const [jwtToken, setJwtToken] = useState(() =>
    sessionStorage.getItem("jwtToken")
  );
  const [refreshToken, setRefreshToken] = useState(() =>
    sessionStorage.getItem("refreshToken")
  );
  const [isAuthenticated, setIsAuthenticated] = useState(() =>
    sessionStorage.getItem("isAuthenticated")
  );
  const intervalReference = useRef();
  const jwtTokenReference = useRef(jwtToken);
  const refreshTokenReference = useRef(refreshToken);
  useEffect(() => {
    jwtTokenReference.current = jwtToken;
    refreshTokenReference.current = refreshToken;
  }, [jwtToken, refreshToken]);
  const handleLogout = () => {
    if (intervalReference.current) {
      clearInterval(intervalReference.current);
    }
    setJwtToken(null);
    setRefreshToken(null);
    setIsAuthenticated(false);
    logout(jwtToken);
  };
  useEffect(() => {
    if (intervalReference.current) {
      clearInterval(intervalReference.current);
    }
    if (!jwtTokenReference.current) {
      return;
    }
    intervalReference.current = setInterval(async () => {
      try {
        const response = await tokenRefreshApi(
          jwtTokenReference.current,
          refreshTokenReference.current
        );
        if (response.data.status == "SUCCESS") {
          setJwtToken(response.data.data.jwtToken);
          setRefreshToken(response.data.data.refreshToken);
          setIsAuthenticated(true);
          sessionStorage.setItem("jwtToken", response.data.data.jwtToken);
          sessionStorage.setItem(
            "refreshToken",
            response.data.data.refreshToken
          );
          sessionStorage.setItem("isAuthenticated", true);
        } else {
          toast.error("Failed to refresh token. Please login again.");
          handleLogout();
        }
      } catch (error) {
        toast.error("Failed to refresh token. Please login again.");
        setTimeout(() => handleLogout(), 5000);
      }
    }, 240000);
    return () => clearInterval(intervalReference.current);
  }, [jwtToken, refreshToken]);
  const login = async (data) => {
    try {
      const response = await loginApi(data);
      if (response.data.status == "SUCCESS") {
        setJwtToken(response.data.data.jwtToken);
        setRefreshToken(response.data.data.refreshToken);
        setIsAuthenticated(true);
        sessionStorage.setItem("jwtToken", response.data.data.jwtToken);
        sessionStorage.setItem("refreshToken", response.data.data.refreshToken);
        sessionStorage.setItem("isAuthenticated", true);
        return { success: true, message: null };
      } else {
        return { success: false, message: response.message };
      }
    } catch (error) {
      const errorMessage =
        error?.response?.data?.message ||
        error?.message ||
        "Login failed. Please try again.";
      return { success: false, message: errorMessage };
    }
  };
  const value = {
    jwtToken,
    login,
    handleLogout,
    refreshToken,
    isAuthenticated,
  };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
