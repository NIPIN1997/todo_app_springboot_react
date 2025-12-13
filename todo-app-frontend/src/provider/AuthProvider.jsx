import { loginApi, rememberMeLoginApi, tokenRefreshApi } from "../api/UserApi";
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
  const [deviceId, setDeviceId] = useState(() =>
    localStorage.getItem("deviceId")
  );

  const intervalReference = useRef();
  const jwtTokenReference = useRef(jwtToken);
  const refreshTokenReference = useRef(refreshToken);
  const deviceIdReference = useRef(deviceId);

  useEffect(() => {
    jwtTokenReference.current = jwtToken;
    refreshTokenReference.current = refreshToken;
    deviceIdReference.current = deviceId;
  }, [jwtToken, refreshToken, deviceId]);

  const handleLogout = () => {
    if (intervalReference.current) {
      clearInterval(intervalReference.current);
    }
    setJwtToken(null);
    setRefreshToken(null);
    setIsAuthenticated(false);
    setDeviceId(null);
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
          refreshTokenReference.current,
          deviceIdReference.current
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
        setDeviceId(response.data.data.deviceId);
        setIsAuthenticated(true);
        sessionStorage.setItem("jwtToken", response.data.data.jwtToken);
        sessionStorage.setItem("refreshToken", response.data.data.refreshToken);
        sessionStorage.setItem("isAuthenticated", true);
        localStorage.setItem("deviceId", response.data.data.deviceId);
        if (response.data.data.rememberMeToken != null) {
          localStorage.setItem(
            "rememberMeToken",
            response.data.data.rememberMeToken
          );
        }
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

  const rememberMeLogin = async (data) => {
    try {
      const response = await rememberMeLoginApi(data);
      if (response.data.status == "SUCCESS") {
        setJwtToken(response.data.data.jwtToken);
        setRefreshToken(response.data.data.refreshToken);
        setDeviceId(response.data.data.deviceId);
        setIsAuthenticated(true);
        sessionStorage.setItem("jwtToken", response.data.data.jwtToken);
        sessionStorage.setItem("refreshToken", response.data.data.refreshToken);
        sessionStorage.setItem("isAuthenticated", true);
        localStorage.setItem("deviceId", response.data.data.deviceId);
        localStorage.setItem(
          "rememberMeToken",
          response.data.data.rememberMeToken
        );
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
    rememberMeLogin,
    refreshToken,
    deviceId,
    isAuthenticated,
  };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
