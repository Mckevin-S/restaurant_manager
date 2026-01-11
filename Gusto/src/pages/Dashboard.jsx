import React, { useState, useEffect } from 'react';
import {
  LayoutDashboard, TrendingUp, Users, DollarSign, Clock,
  ArrowUpRight, ArrowDownRight, Package, AlertCircle,
  ChevronRight, Calendar
} from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, Cell } from 'recharts';
import apiClient from '../services/apiClient';
import RecentTransactions from '../widget/RecentTransaction';

const Dashboard = () => {
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    revenue: 0,
    ordersCount: 0,
    activeTables: '0/0',
    avgWaitTime: '0 min',
    hourlySales: [],
    categoryBreakdown: [],
    lowStockAlerts: []
  });

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [ordersTodayRes, tablesRes, ingredientsRes, statsRes] = await Promise.all([
          apiClient.get('/commandes/aujourd-hui').catch(() => ({ data: [] })),
          apiClient.get('/tables').catch(() => ({ data: [] })),
          apiClient.get('/ingredients').catch(() => ({ data: [] })),
          apiClient.get('/rapports/statistiques?period=day').catch(() => ({ data: {} }))
        ]);

        const ordersToday = ordersTodayRes.data || [];
        const tables = tablesRes.data || [];
        const ingredients = ingredientsRes.data || [];
        const globalStats = statsRes.data || {};

        // 1. Revenue & Orders (Preferez les stats globales si dispos)
        const revenue = globalStats.ca || ordersToday.reduce((sum, order) => sum + (order.total || 0), 0);
        const ordersCount = globalStats.nbCommandes || ordersToday.length;

        // 2. Active Tables (Occupation)
        const activeTablesCount = tables.filter(t => t.statut === 'OCCUPEE').length;
        const totalTables = tables.length;

        // 3. Low Stock Alerts
        const lowStock = ingredients.filter(ing => ing.stockActuel <= (ing.alerteStock || 5)).slice(0, 4);

        // 4. Hourly Distribution (Mock data for visuals if empty)
        const hourlyData = globalStats.evolutionCA?.length > 0 ? globalStats.evolutionCA : [
          { time: '10 AM', sales: 45000 },
          { time: '12 PM', sales: 120000 },
          { time: '2 PM', sales: 85000 },
          { time: '4 PM', sales: 30000 },
          { time: '6 PM', sales: 150000 },
          { time: '8 PM', sales: 210000 },
          { time: '10 PM', sales: 110000 },
        ];

        setStats({
          revenue,
          ordersCount,
          activeTables: `${activeTablesCount}/${totalTables}`,
          avgWaitTime: `15 min`, // Mock or calculate
          hourlySales: hourlyData,
          lowStockAlerts: lowStock
        });
      } catch (error) {
        console.error('Erreur chargement dashboard:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <div className="flex h-[60vh] items-center justify-center">
        <div className="h-12 w-12 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  return (
    <div className="animate-in fade-in duration-500">
      {/* Header */}
      <div className="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <h1 className="text-4xl font-black tracking-tight text-slate-900 flex items-center gap-3">
            <LayoutDashboard className="text-indigo-600" size={36} />
            Tableau de Bord
          </h1>
          <p className="mt-1 text-slate-500 font-medium">Récapitulatif des performances d'aujourd'hui</p>
        </div>
        <div className="flex items-center gap-3 bg-white p-2 rounded-2xl shadow-sm border border-slate-100">
          <Calendar className="text-indigo-600" size={20} />
          <span className="text-sm font-bold text-slate-700">
            {new Date().toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long' })}
          </span>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="mb-8 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          icon={<DollarSign size={24} />}
          label="Chiffre d'Affaires"
          value={`${stats.revenue.toLocaleString()} FCFA`}
          trend="+12%"
          trendUp={true}
          color="indigo"
        />
        <StatCard
          icon={<Package size={24} />}
          label="Commandes"
          value={stats.ordersCount.toString()}
          trend="+5%"
          trendUp={true}
          color="emerald"
        />
        <StatCard
          icon={<Users size={24} />}
          label="Taux d'Occupation"
          value={stats.activeTables}
          trend="-2%"
          trendUp={false}
          color="amber"
        />
        <StatCard
          icon={<Clock size={24} />}
          label="Attente Moyenne"
          value={stats.avgWaitTime}
          trend="-1min"
          trendUp={true}
          color="rose"
        />
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
        {/* Chart Section */}
        <div className="lg:col-span-8 space-y-6">
          <div className="rounded-[32px] bg-white p-8 shadow-sm border border-slate-100 overflow-hidden">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight">Flux des Ventes</h2>
              <div className="flex items-center gap-2 rounded-full bg-indigo-50 px-4 py-1.5 text-xs font-bold text-indigo-600">
                <TrendingUp size={14} /> Aujourd'hui
              </div>
            </div>
            <div className="h-[350px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={stats.hourlySales}>
                  <defs>
                    <linearGradient id="colorSales" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#4F46E5" stopOpacity={0.1} />
                      <stop offset="95%" stopColor="#4F46E5" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                  <XAxis
                    dataKey="time"
                    axisLine={false}
                    tickLine={false}
                    tick={{ fontSize: 12, fill: '#64748b', fontWeight: 600 }}
                    dy={10}
                  />
                  <YAxis
                    axisLine={false}
                    tickLine={false}
                    tick={{ fontSize: 12, fill: '#64748b', fontWeight: 600 }}
                    tickFormatter={(val) => `${val / 1000}k`}
                  />
                  <Tooltip
                    contentStyle={{ borderRadius: '24px', border: 'none', boxShadow: '0 10px 30px rgba(0,0,0,0.1)', padding: '15px' }}
                    itemStyle={{ fontWeight: 800, color: '#4F46E5' }}
                    labelStyle={{ fontWeight: 600, color: '#64748b', marginBottom: '5px' }}
                  />
                  <Area
                    type="monotone"
                    dataKey="sales"
                    stroke="#4F46E5"
                    strokeWidth={4}
                    fillOpacity={1}
                    fill="url(#colorSales)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>

          <RecentTransactions />
        </div>

        {/* Sidebar Section */}
        <div className="lg:col-span-4 space-y-6">
          {/* Alerts Card */}
          <div className="rounded-[32px] bg-white p-8 shadow-sm border border-slate-100">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic">Alertes</h2>
              <AlertCircle className="text-amber-500" size={24} />
            </div>
            <div className="space-y-4">
              {stats.lowStockAlerts.length > 0 ? (
                stats.lowStockAlerts.map((ing, idx) => (
                  <div key={idx} className="flex items-center gap-4 rounded-2xl bg-amber-50 p-4 border border-amber-100">
                    <div className="rounded-xl bg-white p-2 text-amber-600 shadow-sm">
                      <Package size={20} />
                    </div>
                    <div>
                      <p className="text-sm font-black text-slate-800 truncate max-w-[150px]">{ing.nom}</p>
                      <p className="text-xs font-bold text-amber-700">Stock: {ing.stockActuel} {ing.unite}</p>
                    </div>
                    <ChevronRight className="ml-auto text-amber-300" size={16} />
                  </div>
                ))
              ) : (
                <div className="text-center py-8">
                  <div className="mx-auto mb-3 flex h-16 w-16 items-center justify-center rounded-full bg-emerald-50 text-emerald-600">
                    <TrendingUp size={32} />
                  </div>
                  <p className="text-sm font-bold text-slate-600">Tous les stocks sont OK</p>
                </div>
              )}
            </div>
          </div>

          {/* Quick Access Card */}
          <div className="rounded-[32px] bg-slate-900 p-8 shadow-xl text-white overflow-hidden relative">
            <div className="relative z-10">
              <h2 className="text-2xl font-black mb-2 leading-tight">Gusto Premium Dashboard</h2>
              <p className="text-slate-400 text-sm font-medium mb-6">Optimisez la gestion de votre point de vente en temps réel.</p>
              <button className="w-full rounded-2xl bg-indigo-600 py-4 font-bold text-white hover:bg-indigo-700 transition active:scale-95">
                Accéder au POS
              </button>
            </div>
            <div className="absolute -right-10 -bottom-10 h-40 w-40 rounded-full bg-indigo-500/20 blur-3xl"></div>
            <div className="absolute -left-10 -top-10 h-40 w-40 rounded-full bg-rose-500/10 blur-3xl"></div>
          </div>
        </div>
      </div>
    </div>
  );
};

const StatCard = ({ icon, label, value, trend, trendUp, color }) => {
  const colorClasses = {
    indigo: 'bg-indigo-50 text-indigo-600 border-indigo-100',
    emerald: 'bg-emerald-50 text-emerald-600 border-emerald-100',
    amber: 'bg-amber-50 text-amber-600 border-amber-100',
    rose: 'bg-rose-50 text-rose-600 border-rose-100',
  };

  return (
    <div className="group rounded-[32px] bg-white p-8 shadow-sm border border-slate-100 transition hover:shadow-xl hover:-translate-y-1">
      <div className={`mb-6 flex h-14 w-14 items-center justify-center rounded-2xl border ${colorClasses[color]}`}>
        {icon}
      </div>
      <p className="text-xs font-bold uppercase tracking-widest text-slate-400 mb-2">{label}</p>
      <div className="flex items-end justify-between">
        <h3 className="text-2xl font-black text-slate-900">{value}</h3>
        <div className={`flex items-center gap-1 rounded-full px-2 py-0.5 text-[10px] font-black ${trendUp ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'}`}>
          {trendUp ? <ArrowUpRight size={10} /> : <ArrowDownRight size={10} />}
          {trend}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;