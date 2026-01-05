import React from 'react';
import SideBar from '../widget/SideBar';
import { Box, Grid, Typography, Paper, Avatar, Stack, LinearProgress, Alert } from '@mui/material';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

// Icônes
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import PeopleIcon from '@mui/icons-material/People';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import RecentTransactions from '../widget/RecentTransaction';

const data = [
  { time: '10 AM', sales: 400 }, { time: '11 AM', sales: 800 },
  { time: '12 PM', sales: 1300 }, { time: '1 PM', sales: 1400 },
  { time: '2 PM', sales: 900 }, { time: '3 PM', sales: 600 },
  { time: '4 PM', sales: 1100 }, { time: '5 PM', sales: 1600 },
  { time: '6 PM', sales: 2200 },
];

const Dashboard = () => {
  const usr = localStorage.getItem('role')
  


  return (
    <Box sx={{ display: 'flex', bgcolor: '#F8F9FA', minHeight: '100vh', width: "100%" }}>
      <SideBar userRole={usr} />

      <Box component="main" sx={{ flexGrow: 1, p: 4, width: "100%", overflowX: 'hidden' }}>

        {/* HEADER */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4, alignItems: 'center' }}>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#1A1C1E', textTransform: 'capitalize' }}>
              {usr} Dashboard
            </Typography>
            <Typography variant="body2" color="text.secondary">Overview of restaurant performance for today</Typography>
          </Box>
          <Paper sx={{ p: 1, px: 2, borderRadius: 2, bgcolor: 'white', border: '1px solid #E0E0E0', boxShadow: 'none' }}>
            <Typography variant="body2" sx={{ fontWeight: 500 }}>{new Date().toDateString()}</Typography>
          </Paper>
        </Box>

        {/* TOP CARDS - Spacing ramené à 3 */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <StatCard icon={<AttachMoneyIcon sx={{ color: '#4caf50' }} />} label="Total Revenue" value="$4,289.50" trend="+12.5%" />
          <StatCard icon={<ShoppingBagIcon sx={{ color: '#2196f3' }} />} label="Total Orders" value="142" trend="+4.3%" />
          <StatCard icon={<PeopleIcon sx={{ color: '#ED6C02' }} />} label="Active Tables" value="8/12" trend="66%" />
          <StatCard icon={<AccessTimeIcon sx={{ color: '#9C27B0' }} />} label="Avg. Wait Time" value="18 min" trend="-2 min" trendColor="error.main" />
        </Grid>

        <Grid container spacing={3}>
          {/* GAUCHE: CHART + TRANSACTIONS */}
          <Grid item xs={12} md={8}>
            <Stack spacing={3}>
              <Paper sx={{ p: 3, borderRadius: 4, height: '400px', boxShadow: 'none', border: '1px solid #E0E0E0' }}>
                <Typography variant="h6" sx={{ mb: 3, fontWeight: 'bold' }}>Hourly Sales Trend</Typography>
                <ResponsiveContainer width="100%" height="80%">
                  <AreaChart data={data}>
                    <defs>
                      <linearGradient id="colorSales" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#4caf50" stopOpacity={0.3} />
                        <stop offset="95%" stopColor="#4caf50" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#eee" />
                    <XAxis dataKey="time" axisLine={false} tickLine={false} tick={{ fontSize: 12, fill: '#9e9e9e' }} />
                    <YAxis axisLine={false} tickLine={false} tick={{ fontSize: 12, fill: '#9e9e9e' }} />
                    <Tooltip contentStyle={{ borderRadius: '10px', border: 'none', boxShadow: '0 4px 20px rgba(0,0,0,0.1)' }} />
                    <Area type="monotone" dataKey="sales" stroke="#4caf50" strokeWidth={3} fill="url(#colorSales)" />
                  </AreaChart>
                </ResponsiveContainer>
              </Paper>
              
              {/* Le tableau est maintenant bien aligné sous le graphique */}
              <RecentTransactions />
            </Stack>
          </Grid>

          {/* DROITE: BREAKDOWN + ALERT */}
          <Grid item xs={12} md={4}>
            <Paper sx={{ p: 3, borderRadius: 4, mb: 3, boxShadow: 'none', border: '1px solid #E0E0E0' }}>
              <Typography variant="h6" sx={{ mb: 3, fontWeight: 'bold' }}>Category Breakdown</Typography>
              <Stack spacing={3}>
                <CategoryItem label="Starters" value={65} color="#3b82f6" />
                <CategoryItem label="Mains" value={85} color="#3b82f6" />
                <CategoryItem label="Desserts" value={30} color="#3b82f6" />
                <CategoryItem label="Drinks" value={25} color="#3b82f6" />
              </Stack>
            </Paper>

            <Alert 
              severity="error" 
              icon={false}
              sx={{ 
                borderRadius: 4, 
                bgcolor: '#FFF1F1', 
                color: '#D32F2F', 
                border: '1px solid #FFD6D6',
                '& .MuiAlert-message': { width: '100%' }
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                <Box sx={{ width: 8, height: 8, bgcolor: '#D32F2F', borderRadius: '50%' }} />
                <Typography variant="subtitle2" sx={{ fontWeight: 'bold' }}>Low Stock Alert</Typography>
              </Box>
              <Typography variant="caption">Miso Cod (ID: #7) is out of stock. Please restock immediately or update menu.</Typography>
            </Alert>
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
};

// COMPOSANT STAT CARD (Optimisé)
const StatCard = ({ icon, label, value, trend, trendColor = "success.main" }) => (
  <Grid item xs={12} sm={6} md={3}>
    <Paper sx={{ 
      p: 2.5, 
      borderRadius: 4, 
      display: 'flex', 
      alignItems: 'center', 
      gap: 2, 
      boxShadow: 'none', 
      border: '1px solid #E0E0E0',
      bgcolor: 'white'
    }}>
      <Avatar sx={{ bgcolor: '#F8F9FA', width: 48, height: 48, border: '1px solid #F0F2F5' }}>
        {icon}
      </Avatar>
      <Box>
        <Typography variant="caption" sx={{ color: '#757575', fontWeight: 600, display: 'block' }}>{label}</Typography>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Typography variant="h6" sx={{ fontWeight: 800, color: '#1A1C1E' }}>{value}</Typography>
          <Typography variant="caption" sx={{ 
            color: trendColor, 
            bgcolor: trendColor === 'success.main' ? '#E8F5E9' : '#FFEBEE',
            px: 0.8, 
            py: 0.2, 
            borderRadius: 1,
            fontWeight: 'bold' 
          }}>
            {trend}
          </Typography>
        </Box>
      </Box>
    </Paper>
  </Grid>
);

// COMPOSANT CATEGORY (Optimisé)
const CategoryItem = ({ label, value, color }) => (
  <Box>
    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
      <Typography variant="body2" sx={{ fontWeight: 600, color: '#1A1C1E' }}>{label}</Typography>
      <Typography variant="body2" sx={{ color: '#757575', fontWeight: 500 }}>{value}%</Typography>
    </Box>
    <LinearProgress variant="determinate" value={value} sx={{
      height: 8, 
      borderRadius: 5, 
      bgcolor: '#F0F2F5',
      '& .MuiLinearProgress-bar': { bgcolor: color, borderRadius: 5 }
    }} />
  </Box>
);

export default Dashboard;