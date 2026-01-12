import React from 'react';
import { Box } from '@mui/material';
import { Outlet, useNavigate } from 'react-router-dom';
import SideBar from './SideBar';

const ManagerLayout = () => {
    const navigate = useNavigate();
    const role = localStorage.getItem('role') || 'ROLE_MANAGER';

    // Adaptation du thème en fonction du rôle (ex: Cuisine est plus sombre)
    const isKitchen = role === 'ROLE_CUISINIER';
    const bgColor = isKitchen ? '#0f172a' : '#F8F9FA';

    const handleLogout = () => {
        localStorage.removeItem('token'); // Match LoginSlice
        localStorage.removeItem('role');
        navigate('/login');
    };

    return (
        <Box sx={{ display: 'flex', bgcolor: bgColor, minHeight: '100vh', width: "100%" }}>
            <SideBar userRole={role} onLogout={handleLogout} />

            <Box component="main" sx={{
                flexGrow: 1,
                p: { xs: 2, md: 4 },
                width: "100%",
                overflowX: 'hidden',
                backgroundColor: bgColor
            }}>
                <Outlet />
            </Box>
        </Box>
    );
};

export default ManagerLayout;
