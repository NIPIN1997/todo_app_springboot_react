import Container from "react-bootstrap/Container";
import { Header } from "../components/Header";
import { NavigationBar } from "../components/NavigationBar.jsx";

export function Home() {
  return (
    <Container>
      <Header />
      <NavigationBar />
    </Container>
  );
}
