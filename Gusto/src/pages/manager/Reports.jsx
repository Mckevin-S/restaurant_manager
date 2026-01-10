import { useState, useEffect } from 'react';
import { BarChart, Bar, LineChart, Line, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { TrendingUp, DollarSign, Users, Download } from 'lucide-react';
import axios from 'axios';
import { toast } from 'react-hot-toast';
import jsPDF from 'jspdf';
import 'jspdf-autotable';

const Reports = () => {
    const [stats, setStats] = useState(null);
    const [topPlats, setTopPlats] = useState([]);
    const [period, setPeriod] = useState('month');
    const [loading, setLoading] = useState(true);

    const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:3006/api';

    useEffect(() => {
        fetchStats();
        fetchTopPlats();
    }, [period]);

    const fetchStats = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`${API_URL}/rapports/statistiques?period=${period}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setStats(response.data);
        } catch (error) {
            toast.error('Erreur lors du chargement des statistiques');
        } finally {
            setLoading(false);
        }
    };

    const fetchTopPlats = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`${API_URL}/plats/plus-vendus`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setTopPlats(response.data);
        } catch (error) {
            console.error('Erreur top plats:', error);
        }
    };

    const exportPDF = () => {
        const doc = new jsPDF();

        doc.setFontSize(20);
        doc.text('Rapport Restaurant', 14, 22);
        doc.setFontSize(11);
        doc.text(`Période: ${period === 'day' ? 'Aujourd\'hui' : period === 'week' ? 'Cette semaine' : 'Ce mois'}`, 14, 30);

        // Statistiques
        doc.autoTable({
            startY: 40,
            head: [['Métrique', 'Valeur']],
            body: [
                ['Chiffre d\'affaires', `${stats?.ca || 0} FCFA`],
                ['Nombre de commandes', stats?.nbCommandes || 0],
                ['Ticket moyen', `${stats?.ticketMoyen || 0} FCFA`],
            ],
        });

        // Top plats
        doc.autoTable({
            startY: doc.lastAutoTable.finalY + 10,
            head: [['Plat', 'Quantité vendue']],
            body: topPlats.map(p => [p.nom, p.quantite]),
        });

        doc.save(`rapport-${period}-${new Date().toISOString().split('T')[0]}.pdf`);
        toast.success('Rapport exporté');
    };

    const COLORS = ['#4F46E5', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>;
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="mb-8 flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900 mb-2">Rapports & Statistiques</h1>
                    <p className="text-gray-600">Analysez les performances de votre restaurant</p>
                </div>

                <div className="flex gap-3">
                    <select
                        value={period}
                        onChange={(e) => setPeriod(e.target.value)}
                        className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                    >
                        <option value="day">Aujourd'hui</option>
                        <option value="week">Cette semaine</option>
                        <option value="month">Ce mois</option>
                        <option value="year">Cette année</option>
                    </select>

                    <button
                        onClick={exportPDF}
                        className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700"
                    >
                        <Download size={20} />
                        Exporter PDF
                    </button>
                </div>
            </div>

            {/* KPIs */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600">Chiffre d'Affaires</p>
                            <p className="text-2xl font-bold text-gray-900">{stats?.ca?.toLocaleString() || 0} FCFA</p>
                            <p className="text-xs text-green-600 mt-1">+12% vs période précédente</p>
                        </div>
                        <DollarSign className="text-green-600" size={32} />
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600">Commandes</p>
                            <p className="text-2xl font-bold text-gray-900">{stats?.nbCommandes || 0}</p>
                            <p className="text-xs text-blue-600 mt-1">+8% vs période précédente</p>
                        </div>
                        <TrendingUp className="text-blue-600" size={32} />
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600">Ticket Moyen</p>
                            <p className="text-2xl font-bold text-gray-900">{stats?.ticketMoyen?.toLocaleString() || 0} FCFA</p>
                            <p className="text-xs text-indigo-600 mt-1">+5% vs période précédente</p>
                        </div>
                        <DollarSign className="text-indigo-600" size={32} />
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600">Clients Servis</p>
                            <p className="text-2xl font-bold text-gray-900">{stats?.nbClients || 0}</p>
                            <p className="text-xs text-purple-600 mt-1">+15% vs période précédente</p>
                        </div>
                        <Users className="text-purple-600" size={32} />
                    </div>
                </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
                {/* Évolution CA */}
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <h2 className="text-lg font-bold mb-4">Évolution du Chiffre d'Affaires</h2>
                    <ResponsiveContainer width="100%" height={300}>
                        <LineChart data={stats?.evolutionCA || []}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="date" />
                            <YAxis />
                            <Tooltip />
                            <Legend />
                            <Line type="monotone" dataKey="ca" stroke="#4F46E5" strokeWidth={2} name="CA (FCFA)" />
                        </LineChart>
                    </ResponsiveContainer>
                </div>

                {/* Top Plats */}
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <h2 className="text-lg font-bold mb-4">Plats les Plus Vendus</h2>
                    <ResponsiveContainer width="100%" height={300}>
                        <PieChart>
                            <Pie
                                data={topPlats}
                                cx="50%"
                                cy="50%"
                                labelLine={false}
                                label={(entry) => entry.nom}
                                outerRadius={80}
                                fill="#8884d8"
                                dataKey="quantite"
                            >
                                {topPlats.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                ))}
                            </Pie>
                            <Tooltip />
                        </PieChart>
                    </ResponsiveContainer>
                </div>
            </div>

            {/* Tableau détaillé */}
            <div className="bg-white rounded-lg shadow-sm overflow-hidden">
                <div className="p-4 border-b">
                    <h2 className="text-lg font-bold">Détails des Ventes</h2>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Plat</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Quantité</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">CA Généré</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">% du Total</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                            {topPlats.map((plat, idx) => (
                                <tr key={idx} className="hover:bg-gray-50">
                                    <td className="px-6 py-4 whitespace-nowrap font-medium">{plat.nom}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">{plat.quantite}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">{plat.ca?.toLocaleString()} FCFA</td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="flex items-center">
                                            <div className="w-16 bg-gray-200 rounded-full h-2 mr-2">
                                                <div
                                                    className="bg-indigo-600 h-2 rounded-full"
                                                    style={{ width: `${plat.pourcentage}%` }}
                                                ></div>
                                            </div>
                                            <span className="text-sm">{plat.pourcentage}%</span>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default Reports;
