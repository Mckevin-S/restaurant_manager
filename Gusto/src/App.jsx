import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './index.css'
import Login from './pages/Auth/Login'
import { Route, Routes } from 'react-router-dom'
import Dashboard from './pages/Dashboard'

function App() {

  return (
    <>
      <Routes>
        <Route path='/login' element={<Login />} />
      </Routes>
      {/* <Routes>
        <Route path='/dashbord' element={<Dashboard />} />
      </Routes> */}

    </>
  )
}

export default App
