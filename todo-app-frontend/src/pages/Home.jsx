import { Header } from "../components/Header";
import { NavigationBar } from "../components/NavigationBar.jsx";
import { Tasks } from "../components/Tasks.jsx";
import styles from "../styles/home.module.css";

export function Home() {
  return (
    <div className={styles.main_division}>
      <Header />
      <NavigationBar />
      <Tasks />
    </div>
  );
}
