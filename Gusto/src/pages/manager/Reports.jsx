import { useState, useEffect, useCallback } from 'react';
import {
    BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
    XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
    AreaChart, Area
} from 'recharts';
import { TrendingUp, DollarSign, Users, Download, Calendar, Pizza, ShoppingBag } from 'lucide-react';
import apiClient from '../../services/apiClient';
import { getRestaurantSettings } from '../../services/api';
import { toast } from 'react-hot-toast';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import PageHeader from '../../widget/PageHeader';
import StatCard from '../../widget/StatCard';

const Reports = () => {
    const [stats, setStats] = useState(null);
    const [topPlats, setTopPlats] = useState([]);
    const [period, setPeriod] = useState('month');
    const [loading, setLoading] = useState(true);
    const [restaurantInfo, setRestaurantInfo] = useState(null);

    // --- FONCTIONS DE RÉCUPÉRATION (Déplacées ici pour le Hoisting) ---

    const fetchRestaurant = useCallback(async () => {
        try {
            const resp = await getRestaurantSettings();
            setRestaurantInfo(resp.data);
        } catch (e) {
            console.warn('Impossible de récupérer infos restaurant', e);
        }
    }, []);

    const fetchStats = useCallback(async () => {
        try {
            const response = await apiClient.get(`/rapports/statistiques?period=${period}`);
            setStats(response.data);
        } catch (error) {
            console.error('Erreur stats:', error);
            setStats({
                ca: 0,
                nbCommandes: 0,
                ticketMoyen: 0,
                nbClients: 0,
                evolutionCA: []
            });
        }
    }, [period]);

    const fetchTopPlats = useCallback(async () => {
        try {
            const response = await apiClient.get('/plats/plus-vendus');
            setTopPlats(response.data || []);
        } catch (error) {
            console.error('Erreur top plats:', error);
        }
    }, []);

    // --- EFFETS ---

    useEffect(() => {
        const loadData = async () => {
            setLoading(true);
            await Promise.all([
                fetchStats(),
                fetchTopPlats(),
                fetchRestaurant()
            ]);
            setLoading(false);
        };
        loadData();
    }, [period, fetchStats, fetchTopPlats, fetchRestaurant]);

    // --- AUTRES LOGIQUES ---

    const exportPDF = () => {
        try {
            const doc = new jsPDF();

            // Titre et période
            doc.setFontSize(20);
            doc.text(`Rapport ${restaurantInfo?.nom || 'Restaurant'}`, 14, 22);
            doc.setFontSize(11);
            doc.text(`Période: ${period === 'day' ? 'Aujourd\'hui' : period === 'week' ? 'Cette semaine' : period === 'month' ? 'Ce mois' : 'Cette année'}`, 14, 30);

            // Première table - Statistiques
            autoTable(doc, {
                startY: 40,
                head: [['Métrique', 'Valeur']],
                body: [
                    ['Chiffre d\'affaires', `${(stats?.ca || 0).toLocaleString()} FCFA`],
                    ['Nombre de commandes', stats?.nbCommandes || 0],
                    ['Ticket moyen', `${(stats?.ticketMoyen || 0).toLocaleString()} FCFA`],
                    ['Clients servis', stats?.nbClients || 0],
                ],
                theme: 'grid',
                headStyles: { fillColor: [79, 70, 229], textColor: 255 },
                styles: { fontSize: 10 }
            });

            // Deuxième table - Top plats
            if (topPlats && topPlats.length > 0) {
                const lastY = doc.lastAutoTable?.finalY || 60;
                autoTable(doc, {
                    startY: lastY + 10,
                    head: [['Plat', 'Quantité vendue', 'CA Généré']],
                    body: topPlats.slice(0, 10).map(p => [
                        p.nom || 'N/A',
                        p.quantite || 0,
                        `${(p.ca || 0).toLocaleString()} FCFA`
                    ]),
                    theme: 'grid',
                    headStyles: { fillColor: [79, 70, 229], textColor: 255 },
                    styles: { fontSize: 10 }
                });
            }

            doc.save(`rapport-${period}-${new Date().toISOString().split('T')[0]}.pdf`);
            toast.success('Rapport exporté avec succès');
        } catch (error) {
            console.error('Erreur export PDF:', error);
            toast.error('Erreur lors de l\'export du PDF: ' + error.message);
        }
    };

    const computeTrend = (series = []) => {
        if (!Array.isArray(series) || series.length < 2) return { label: '', value: '' };
        const last = series[series.length - 1]?.ca ?? series[series.length - 1];
        const prev = series[series.length - 2]?.ca ?? series[series.length - 2];
        if (prev === 0 || prev === undefined) return { label: '', value: '' };
        const pct = ((last - prev) / Math.abs(prev)) * 100;
        const sign = pct >= 0 ? '+' : '';
        return { label: `${sign}${Math.round(pct)}%`, value: pct >= 0 };
    };

    const COLORS = ['#4F46E5', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

    if (loading) {
        return (
            <div className="flex h-[60vh] items-center justify-center">
                <div className="h-12 w-12 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
            </div>
        );
    }

    const headerActions = (
        <div className="flex items-center gap-3">
            <div className="relative">
                <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 text-indigo-600" size={18} />
                <select
                    value={period}
                    onChange={(e) => setPeriod(e.target.value)}
                    className="pl-10 pr-4 py-2.5 bg-white border border-slate-100 rounded-2xl text-sm font-bold text-slate-700 shadow-sm focus:ring-2 focus:ring-indigo-500 outline-none appearance-none cursor-pointer min-w-[160px]"
                >
                    <option value="day">Aujourd'hui</option>
                    <option value="week">Cette semaine</option>
                    <option value="month">Ce mois</option>
                    <option value="year">Cette année</option>
                </select>
            </div>

            <button
                onClick={exportPDF}
                className="flex items-center gap-2 bg-slate-900 text-white px-6 py-2.5 rounded-2xl font-bold text-sm shadow-lg hover:bg-indigo-600 transition active:scale-95"
            >
                <Download size={18} />
                Exporter PDF
            </button>
        </div>
    );

    return (
        <div className="min-h-screen bg-[#f8fafc] animate-in fade-in duration-500 pb-12">
            <PageHeader
                icon={TrendingUp}
                title="Rapports & Statistiques"
                subtitle="Analysez et optimisez les performances de votre établissement"
                actions={headerActions}
            />

            <div className="px-4">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                    {(() => {
                        const trendCA = computeTrend(stats?.evolutionCA);
                        const trendCmd = computeTrend(stats?.evolutionCA);
                        const trendTicket = computeTrend(stats?.evolutionCA);
                        const trendClients = computeTrend(stats?.evolutionCA);
                        return (
                            <>
                                <StatCard
                                    icon={<DollarSign size={24} />}
                                    label="Chiffre d'Affaires"
                                    value={`${(stats?.ca || 0).toLocaleString()} FCFA`}
                                    trend={trendCA.label}
                                    trendUp={trendCA.value}
                                    color="indigo"
                                />
                                <StatCard
                                    icon={<ShoppingBag size={24} />}
                                    label="Commandes"
                                    value={(stats?.nbCommandes || 0).toString()}
                                    trend={trendCmd.label}
                                    trendUp={trendCmd.value}
                                    color="emerald"
                                />
                                <StatCard
                                    icon={<TrendingUp size={24} />}
                                    label="Ticket Moyen"
                                    value={`${(stats?.ticketMoyen || 0).toLocaleString()} FCFA`}
                                    trend={trendTicket.label}
                                    trendUp={trendTicket.value}
                                    color="amber"
                                />
                                <StatCard
                                    icon={<Users size={24} />}
                                    label="Clients Servis"
                                    value={(stats?.nbClients || 0).toString()}
                                    trend={trendClients.label}
                                    trendUp={trendClients.value}
                                    color="rose"
                                />
                            </>
                        );
                    })()}
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 mb-8">
                    <div className="lg:col-span-8 rounded-[32px] bg-white p-8 shadow-sm border border-slate-100 overflow-hidden">
                        <div className="mb-8 flex items-center justify-between">
                            <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic">Évolution du Chiffre d'Affaires</h2>
                            <div className="flex items-center gap-2 rounded-full bg-indigo-50 px-4 py-1.5 text-[10px] font-black uppercase text-indigo-600 tracking-widest">
                                <TrendingUp size={12} /> Live Data
                            </div>
                        </div>
                        <div className="h-[400px] w-full">
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart data={stats?.evolutionCA || []}>
                                    <defs>
                                        <linearGradient id="colorCA" x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="5%" stopColor="#4F46E5" stopOpacity={0.1} />
                                            <stop offset="95%" stopColor="#4F46E5" stopOpacity={0} />
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                                    <XAxis
                                        dataKey="date"
                                        axisLine={false}
                                        tickLine={false}
                                        tick={{ fontSize: 11, fill: '#64748b', fontWeight: 800 }}
                                        dy={10}
                                    />
                                    <YAxis
                                        axisLine={false}
                                        tickLine={false}
                                        tick={{ fontSize: 11, fill: '#64748b', fontWeight: 800 }}
                                        tickFormatter={(val) => `${(val / 1000).toFixed(0)}k`}
                                    />
                                    <Tooltip
                                        contentStyle={{ borderRadius: '24px', border: 'none', boxShadow: '0 10px 30px rgba(0,0,0,0.1)', padding: '15px' }}
                                        itemStyle={{ fontWeight: 900, color: '#4F46E5' }}
                                        labelStyle={{ fontWeight: 700, color: '#64748b', marginBottom: '5px' }}
                                    />
                                    <Area
                                        type="monotone"
                                        dataKey="ca"
                                        stroke="#4F46E5"
                                        strokeWidth={4}
                                        fillOpacity={1}
                                        fill="url(#colorCA)"
                                        name="CA (FCFA)"
                                    />
                                </AreaChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    <div className="lg:col-span-4 rounded-[32px] bg-white p-8 shadow-sm border border-slate-100">
                        <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic mb-8">Performance Plats</h2>
                        <div className="h-[300px] w-full">
                            <ResponsiveContainer width="100%" height="100%">
                                <PieChart>
                                    <Pie
                                        data={topPlats}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={100}
                                        paddingAngle={5}
                                        dataKey="quantite"
                                    >
                                        {topPlats.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} cornerRadius={8} />
                                        ))}
                                    </Pie>
                                    <Tooltip
                                        contentStyle={{ borderRadius: '20px', border: 'none', boxShadow: '0 10px 20px rgba(0,0,0,0.05)' }}
                                        itemStyle={{ fontWeight: 800 }}
                                    />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                        <div className="mt-6 space-y-3">
                            {topPlats.slice(0, 3).map((plat, idx) => (
                                <div key={idx} className="flex items-center justify-between">
                                    <div className="flex items-center gap-2">
                                        <div className="h-2 w-2 rounded-full" style={{ backgroundColor: COLORS[idx % COLORS.length] }}></div>
                                        <span className="text-xs font-black text-slate-700 truncate max-w-[120px]">{plat.nom}</span>
                                    </div>
                                    <span className="text-xs font-bold text-slate-400">{plat.quantite} ventes</span>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                <div className="rounded-[32px] bg-white shadow-sm border border-slate-100 overflow-hidden">
                    <div className="p-8 border-b border-slate-50 flex items-center justify-between bg-slate-50/50">
                        <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic">Analyse Détaillée par Produit</h2>
                        <div className="h-10 w-10 rounded-2xl bg-white shadow-sm flex items-center justify-center text-indigo-600">
                            <Pizza size={20} />
                        </div>
                    </div>
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead>
                            <tr className="bg-white">
                                <th className="px-8 py-5 text-left text-[10px] font-black uppercase tracking-widest text-slate-400">Produit</th>
                                <th className="px-8 py-5 text-center text-[10px] font-black uppercase tracking-widest text-slate-400">Volume</th>
                                <th className="px-8 py-5 text-right text-[10px] font-black uppercase tracking-widest text-slate-400">Revenu Généré</th>
                                <th className="px-8 py-5 text-right text-[10px] font-black uppercase tracking-widest text-slate-400">Contribution</th>
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-50">
                            {topPlats.length > 0 ? topPlats.map((plat, idx) => (
                                <tr key={idx} className="hover:bg-indigo-50/30 transition-colors group">
                                    <td className="px-8 py-6 whitespace-nowrap">
                                        <span className="text-sm font-black text-slate-900 group-hover:text-indigo-600 transition">{plat.nom}</span>
                                    </td>
                                    <td className="px-8 py-6 text-center">
                                            <span className="inline-flex items-center px-3 py-1 rounded-full bg-slate-100 text-slate-600 text-[10px] font-black uppercase tracking-tight">
                                                {plat.quantite}
                                            </span>
                                    </td>
                                    <td className="px-8 py-6 text-right font-black text-slate-900 text-sm">
                                        {(plat.ca || 0).toLocaleString()} <span className="text-[10px] ml-1 text-slate-400 italic">FCFA</span>
                                    </td>
                                    <td className="px-8 py-6 text-right">
                                        <div className="flex items-center justify-end gap-3">
                                            <div className="w-24 bg-slate-100 rounded-full h-1.5 overflow-hidden">
                                                <div
                                                    className="bg-indigo-600 h-full rounded-full transition-all duration-1000"
                                                    style={{ width: `${plat.pourcentage}%` }}
                                                ></div>
                                            </div>
                                            <span className="text-xs font-black text-indigo-600 min-w-[40px]">{plat.pourcentage}%</span>
                                        </div>
                                    </td>
                                </tr>
                            )) : (
                                <tr>
                                    <td colSpan="4" className="py-20 text-center opacity-20 italic font-black uppercase tracking-widest">
                                        Aucune donnée disponible
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Reports;