import { useState } from "react";
import { Link, useNavigate } from 'react-router-dom'
import { motion } from "framer-motion";

import api from "../api/apiConfig";
import ForgotPasswordModal from "../components/ForgotPasswordModal";
import LoadingSpinnerModal from "../components/LoadingSpinnerModal";

interface ModalProps {
  modalOpen: boolean;
  openModal: () => void;
  closeModal: () => void;
  handleLogin: (token: string) => void;
}

const Signin: React.FC<ModalProps> = ({ openModal, modalOpen, closeModal, handleLogin }) => {
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const [identifierError, setIdentifierError] = useState("");
  const [passwordError, setPasswordError] = useState("");

  const [loading, setLoading] = useState(false);

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!identifier || identifier.length === 0) {
      setIdentifierError("Email/Username is required");
      return;
    } else {
      setIdentifierError("");
    }

    if (!password) {
      setPasswordError("Password is required");
      return;
    } else {
      setPasswordError("");
    }

    setLoading(true);
    console.log("submitted");
    // Login the user
    const requestBody = {
      identifier: identifier,
      password: password
    };

    api.post('/users/login', requestBody)
      .then(response => {
        console.log(response.data);
        handleLogin(response.data);
        navigate('/');
        window.location.reload();
        console.log("Logged in successfully");
        setLoading(false);
      })
      .catch(error => {
        console.error(error);
        const errorMessage = error.response.data.message;
        if (errorMessage.includes("Email")) {
          setIdentifierError(errorMessage);
        } else if (errorMessage.includes("Password")) {
          setPasswordError(errorMessage);
        } else if (errorMessage.includes("Username")) {
          setIdentifierError(errorMessage);
        } else if (errorMessage.includes("verified")) {
          setIdentifierError(errorMessage);
        }
        setLoading(false);
      });
  };

  return (
    <motion.div
      initial={{ opacity: 0, x: -50 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 1 }}
      className="flex justify-center items-center min-h-screen">
      {!modalOpen ?
        <div className="container bg-primaryWhite shadow p-8 space-y-5 rounded-xl w-80 md:w-96">

          <div className="flex justify-center p-4 items-center">
            <Link to="/">
              <img
                src="../src/assets/logo.png"
                alt="logo"
                className="logo-image h-12"
              />
            </Link>

          </div>
          <form onSubmit={(e) => handleSubmit(e)}>
            <div className="flex flex-col space-y-3">
              <h2 className="text-black font-bold">Sign-in to your account.</h2>
              <div className="flex flex-col">
                <div className="flex justify-between">
                  <label
                    htmlFor="identifier"
                    className={`block mb-2 text-sm font-small text-gray-900 ${identifierError ? "text-red-500" : "text-black"
                      }`}
                  >
                    Email/Username
                  </label>
                  <small className="block mb-2 text-sm font-small text-gray-900 text-red-500 text-end">{identifierError}</small>
                </div>

                <input
                  type="identifier"
                  name="identifier"
                  id="identifier"
                  placeholder="Your email address or username"
                  className={`bg-gray-100 p-2 rounded-md text-black focus:outline-none focus:shadow-xl focus:bg-primaryWhite ${identifierError && "border-2 border-red-500"
                    }`}
                  value={identifier}
                  onChange={(e) => setIdentifier(e.target.value)}
                ></input>
              </div>
              <div className="flex flex-col">
                <div className="flex justify-between">
                  <label
                    htmlFor="password"
                    className={`block mb-2 text-sm font-small text-gray-900 ${passwordError ? "text-red-500" : "text-black"
                      }`}
                  >
                    Password
                  </label>
                  <small className="block mb-2 text-sm font-small text-gray-900 text-red-500 text-end	">{passwordError}</small>
                </div>
                <input
                  type="password"
                  name="password"
                  id="emapasswordil"
                  placeholder="Your password"
                  className={`bg-gray-100 p-2 rounded-md text-black focus:outline-none focus:shadow-lg focus:bg-primaryWhite ${passwordError && "border-2 border-red-500"
                    }`}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                ></input>
              </div>
              <div>
                <button
                  type="submit"
                  className=" font-bold text-white bg-primaryBlue rounded-md p-2 w-full hover:brightness-125"
                >
                  Sign in
                </button>
              </div>
              <div>
                <div className="cursor-pointer block text-sm font-small text-primaryBlue w-fit hover:underline" onClick={openModal}>
                  Forgot your password?
                </div>
              </div>
              <div className="flex gap-1">
                <p className="block mb-2 text-sm font-small text-gray-900 text-black">
                  New to Comentario?
                </p>
                <Link to="/sign-up" className="block mb-2 text-sm font-small text-primaryBlue hover:underline">Get started.</Link>
              </div>
            </div>
          </form>
        </div>
        : <ForgotPasswordModal closeModal={closeModal} handleLogin={handleLogin}/>}
      {loading && <LoadingSpinnerModal closeModal={closeModal} />}
    </motion.div>
  );
};

export default Signin;
