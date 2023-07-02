import { BrowserRouter } from 'react-router-dom'
import './App.css'
import useAuthentication from "./components/useAuthentication";
import api from './api/apiConfig'

import { useEffect, useState } from 'react'
import LocationProvider from './components/LocationProvider';
import RoutesWithAnimation from './components/RoutesWithAnimation';

import { UserState } from './interfaces/types';

function App() {

  const { isLoggedIn, login, logout } = useAuthentication();

  const [modalOpen, setModalOpen] = useState(false);
  const [loggedInUser, setLoggedInUser] = useState<UserState | null>(null);

  const openModal = () => {
    setModalOpen(true);
    document.body.classList.add("modal-open")
  }

  const closeModal = () => {
    setModalOpen(false);
    document.body.classList.remove("modal-open")
  }

  const handleLogin = (token: string): void => {
    login(token);
  };

  const handleLogout = (): void => {
    logout();
  };

  useEffect(() => {
    if (isLoggedIn) {
      const token = localStorage.getItem('jwt');
      const config = {
        headers: {
          Authorization: token
        }
      };
      api.get('/users/user', config)
        .then((response) => {
          setLoggedInUser(response.data)
          console.log(response.data);
        })
        .catch(err => {
          console.error(err)
          logout();
        })
    }
  }, [isLoggedIn, logout])

  

  return (
    <main >
      <BrowserRouter >
        <LocationProvider>
          <RoutesWithAnimation modalOpen={modalOpen} openModal={openModal} closeModal={closeModal} isLoggedIn={isLoggedIn} handleLogout={handleLogout} handleLogin={handleLogin} loggedInUser={loggedInUser}/>
        </LocationProvider>
      </BrowserRouter>
    </main >
  )
}

export default App
