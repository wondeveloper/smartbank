import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

export default function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false); 
  const token = localStorage.getItem("accessToken");
  const refreshToken = localStorage.getItem("refreshToken");
  const [userFirstName, setUserFirstName] = useState<string | null>("");
  const navigate = useNavigate();

  useEffect(() => { const token = localStorage.getItem("token"); 
  const refreshToken = localStorage.getItem("refreshToken"); 
  const firstName = localStorage.getItem("userFirstName"); 
  setIsLoggedIn(!!token || !!refreshToken); 
  setUserFirstName(firstName); }, []);

  const logout = () => { localStorage.removeItem("token"); 
        localStorage.removeItem("refreshToken"); 
        localStorage.removeItem("hash"); 
        localStorage.removeItem("accessTokenExpiresAt"); 
        localStorage.removeItem("refreshTokenExpiresAt"); 
        localStorage.removeItem("userFirstName"); 
        localStorage.removeItem("userEmail"); 
        setIsLoggedIn(false); 
        navigate("/"); 
    };

    return (
      <nav className="bg-white shadow px-4 py-3 flex justify-between items-center">
          <span className="text-xl font-bold text-blue-700">SmartBank</span>
            <div className="space-x-4"> {refreshToken ?
              (<> <Link to="/dashboard" className="text-gray-700 hover:text-blue-600">Dashboard</Link>
                  <Link to="/transfer" className="text-gray-700 hover:text-blue-600">Transfer</Link>
                  <Link to="/admin" className="text-gray-700 hover:text-blue-600">Admin</Link>
                  {userFirstName && (<span className="text-gray-600">Hi, {userFirstName}</span>)}
                  <button onClick={logout} className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"> 
                  Logout 
                  </button> </>)
              : (<> <Link to="/" className="text-gray-700 hover:text-blue-600">Login</Link>
                  <Link to="/register" className="text-gray-700 hover:text-blue-600">Register</Link> </>)}
  
            </div> 
        </nav>);
}
