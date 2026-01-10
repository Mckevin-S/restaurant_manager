import { useState, useEffect } from 'react';
import { Search, Calendar, Filter, FileText } from 'lucide-react';
import axios from 'axios';
import { toast } from 'react-hot-toast';

const OrderHistory = () => {
    const [commandes, setCommandes] = useState([]);
    const [filteredCommandes, setFilteredCommandes] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [statusFilter, setStatusFilter] = useState('all');
    const [dateFilter, setDateFilter] = useState('all');
    const [loading, setLoading] = useState(true);

    const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:3006/api';

    useEffect(() => {
        fetchCommandes();
    }, []);

    useEffect(() => {
        filterCommandes();
    }, [searchTerm, statusFilter, dateFilter, commandes]);

    const fetchCommandes = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`${API_URL}/commandes`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setCommandes(response.data);
        } catch (error) {
            toast.error('Erreur lors du chargement');
        } finally {
            setLoading(false);
        }
    };

    const filterCommandes = () => {
        let filtered = [...commandes];

        // Filtre par recherche
        if (searchTerm) {
            filtered = filtered.filter(c =>
                c.table?.numero.toLowerCase().includes(searchTerm.toLowerCase()) ||
                c.id.toString().includes(searchTerm)
            );
        }

        // Filtre par statut
        if (statusFilter !== 'all') {
            filtered = filtered.filter(c => c.statut === statusFilter);
        }

        // Filtre par date
        if (dateFilter !== 'all') {
            const now = new Date();
            filtered = filtered.filter(c => {
                const commandeDate = new Date(c.dateHeureCommande);
                switch (dateFilter) {
                    case 'today':
                        return commandeDate.toDateString() === now.toDateString();
                    case 'week':
                        const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
                        return commandeDate >= weekAgo;
                    case 'month':
                        return commandeDate.getMonth() === now.getMonth();
                    default:
                        return true;
                }
            });
        }

        setFilteredCommandes(filtered);
    };

    const getStatusColor = (statut) => {
        const colors = {
            EN_ATTENTE: 'bg-yellow-100 text-yellow-800',
            EN_PREPARATION: 'bg-blue-100 text-blue-800',
            PRETE: 'bg-green-100 text-green-800',
            SERVIE: 'bg-gray-100 text-gray-800',
            ANNULEE: 'bg-red-100 text-red-800'
        };
        return colors[statut] || 'bg-gray-100 text-gray-800';
    };

    const getStatusLabel = (statut) => {
        const labels = {
            EN_ATTENTE: 'En attente',
            EN_PREPARATION: 'En préparation',
            PRETE: 'Prête',
            SERVIE: 'Servie',
            ANNULEE: 'Annulée'
        };
        return labels[statut] || statut;
    };

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>;
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Historique des Commandes</h1>
                <p className="text-gray-600">Consultez toutes vos commandes passées</p>
            </div>

            {/* Filtres */}
            <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                        <input
                            type="text"
                            placeholder="Rechercher par table ou numéro..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>

                    <select
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                        className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                    >
                        <option value="all">Tous les statuts</option>
                        <option value="EN_ATTENTE">En attente</option>
                        <option value="EN_PREPARATION">En préparation</option>
                        <option value="PRETE">Prête</option>
                        <option value="SERVIE">Servie</option>
                        <option value="ANNULEE">Annulée</option>
                    </select>

                    <select
                        value={dateFilter}
                        onChange={(e) => setDateFilter(e.target.value)}
                        className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                    >
                        <option value="all">Toutes les dates</option>
                        <option value="today">Aujourd'hui</option>
                        <option value="week">Cette semaine</option>
                        <option value="month">Ce mois</option>
                    </select>
                </div>
            </div>

            {/* Statistiques */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <p className="text-sm text-gray-600">Total Commandes</p>
                    <p className="text-2xl font-bold text-gray-900">{filteredCommandes.length}</p>
                </div>
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <p className="text-sm text-gray-600">Servies</p>
                    <p className="text-2xl font-bold text-green-600">
                        {filteredCommandes.filter(c => c.statut === 'SERVIE').length}
                    </p>
                </div>
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <p className="text-sm text-gray-600">En cours</p>
                    <p className="text-2xl font-bold text-blue-600">
                        {filteredCommandes.filter(c => ['EN_ATTENTE', 'EN_PREPARATION', 'PRETE'].includes(c.statut)).length}
                    </p>
                </div>
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <p className="text-sm text-gray-600">Annulées</p>
                    <p className="text-2xl font-bold text-red-600">
                        {filteredCommandes.filter(c => c.statut === 'ANNULEE').length}
                    </p>
                </div>
            </div>

            {/* Liste des commandes */}
            <div className="bg-white rounded-lg shadow-sm overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Commande</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Table</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date & Heure</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                            {filteredCommandes.map(commande => (
                                <tr key={commande.id} className="hover:bg-gray-50">
                                    <td className="px-6 py-4 whitespace-nowrap font-medium">#{commande.id}</td>
                                    <td className="px-6 py-4 whitespace-nowrap">{commande.table?.numero}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                        {new Date(commande.dateHeureCommande).toLocaleString('fr-FR')}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap font-bold">
                                        {commande.totalTtc?.toLocaleString()} FCFA
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(commande.statut)}`}>
                                            {getStatusLabel(commande.statut)}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <button
                                            className="text-indigo-600 hover:text-indigo-900 flex items-center gap-1"
                                            onClick={() => toast.info('Détails de la commande')}
                                        >
                                            <FileText size={16} />
                                            Détails
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                {filteredCommandes.length === 0 && (
                    <div className="text-center py-12">
                        <p className="text-gray-500">Aucune commande trouvée</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default OrderHistory;
