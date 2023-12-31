import { BrowserRouter } from 'react-router-dom'
import './App.css'
import api from './api/apiConfig'

import { useEffect, useState } from 'react'
import LocationProvider from './components/LocationProvider';
import RoutesWithAnimation from './components/RoutesWithAnimation';

import { UserState } from './interfaces/types';
import ScrollToTop from './components/ScrollToTop';

function App() {

  const [modalOpen, setModalOpen] = useState(false);
  const [loggedInUser, setLoggedInUser] = useState<UserState | undefined>();
  const [isLoggedIn, setIsLoggedIn] = useState(true);

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

  const logout = () => {
    localStorage.removeItem('jwt');
    setIsLoggedIn(false);
    console.log("logged out")
  };

  const login = (token: string): void => {
    localStorage.setItem('jwt', token);
    setIsLoggedIn(true);
    console.log("logged in")
  };

  useEffect(() => {
    const token = localStorage.getItem('jwt');
    if (token) {
      const config = {
        headers: {
          Authorization: token
        }
      };
      api.get('/users/user', config)
        .then((response) => {
          setIsLoggedIn(true);
          setLoggedInUser(response.data)
          console.log(response.data);
        })
        .catch(err => {
          console.error(err)
          setIsLoggedIn(false);
          setLoggedInUser(undefined)
          localStorage.removeItem('jwt');
          console.log("logged out")
        })
    }

  }, [])

  return (
    <main className='text-sm lg:text-base'>
      <BrowserRouter >
        <ScrollToTop/>
        <LocationProvider>
          <RoutesWithAnimation modalOpen={modalOpen} openModal={openModal} closeModal={closeModal} isLoggedIn={isLoggedIn} handleLogout={handleLogout} handleLogin={handleLogin} loggedInUser={loggedInUser} />
        </LocationProvider>
      </BrowserRouter>
    </main >
  )
}

export default App
