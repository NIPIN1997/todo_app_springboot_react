import { useEffect, useState } from "react";
import { Header } from "../components/Header";
import { NavigationBar } from "../components/NavigationBar";
import { useAuth } from "../hooks/UseAuth";
import { deviceLogoutApi, loggedInDevicesApi, logoutApi } from "../api/UserApi";
import { toast } from "react-toastify";
import styles from "../styles/devices.module.css";
import Button from "react-bootstrap/esm/Button";

export function Devices() {
  const [devices, setDevices] = useState([]);
  const { jwtToken, handleLogout } = useAuth();
  const getDevices = async () => {
    const response = await loggedInDevicesApi(jwtToken);
    setDevices(response.data.data);
  };
  useEffect(() => {
    getDevices();
  }, []);
  const handleClick = async (device) => {
    const response = await deviceLogoutApi(jwtToken, device);
    if (response.status == 200) {
      if (String(localStorage.getItem("deviceId")) === String(device)) {
        handleLogout();
      } else {
        getDevices();
      }
    }
  };
  return (
    <>
      <Header />
      <NavigationBar />
      <div className={styles.devices_division}>
        <h1 className={styles.heading}>Logged In Devices</h1>
        {devices.map((element, index) => (
          <div key={index} className={styles.devices_sub_division}>
            <div className={styles.single_device_division}>
              {index + 1}. {element.browser} on {element.os} {element.osVersion}
              {String(sessionStorage.getItem("deviceId")) ===
                String(element.deviceId) && <span> (this device) </span>}
            </div>
            <div>
              <Button
                variant="danger"
                onClick={() => handleClick(element.deviceId)}
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  fill="currentColor"
                  className="bi bi-box-arrow-right me-2"
                  viewBox="0 0 16 16"
                >
                  <path
                    fillRule="evenodd"
                    d="M10 12.5a.5.5 0 0 1-.5.5h-8a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 .5.5v2a.5.5 0 0 0 1 0v-2A1.5 1.5 0 0 0 9.5 2h-8A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h8a1.5 1.5 0 0 0 1.5-1.5v-2a.5.5 0 0 0-1 0z"
                  />
                  <path
                    fillRule="evenodd"
                    d="M15.854 8.354a.5.5 0 0 0 0-.708l-3-3a.5.5 0 0 0-.708.708L14.293 7.5H5.5a.5.5 0 0 0 0 1h8.793l-2.147 2.146a.5.5 0 0 0 .708.708z"
                  />
                </svg>
                logout
              </Button>
            </div>
          </div>
        ))}
      </div>
    </>
  );
}
