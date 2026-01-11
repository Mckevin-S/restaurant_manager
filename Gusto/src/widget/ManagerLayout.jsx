import React from 'react';
import { Box } from '@mui/material';
import { Outlet, useNavigate } from 'react-router-dom';
import SideBar from './SideBar';

const ManagerLayout = () => {
    const navigate = useNavigate();
    const usr = localStorage.getItem('role') || 'Manager';

    const handleLogout = () => {
        localStorage.removeItem('userToken');
        localStorage.removeItem('role');
        navigate('/login');
    };

    return (
        <Box sx={{ display: 'flex', bgcolor: '#F8F9FA', minHeight: '100vh', width: "100%" }}>
            <SideBar userRole={usr} onLogout={handleLogout} />

            <Box component="main" sx={{
                flexGrow: 1,
                p: { xs: 2, md: 4 },
                width: "100%",
                overflowX: 'hidden',
                backgroundColor: '#F8F9FA'
            }}>
                <Outlet />
            </Box>
        </Box>
    );
};

export default ManagerLayout;
