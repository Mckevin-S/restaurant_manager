import { useState, useEffect } from 'react';
import { formatStatut } from '../../utils/formatters';
import { Users, Clock, ChefHat, CheckCircle, AlertCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import apiClient from '../../services/apiClient';

const FloorPlan = () => {
    const [tables, setTables] = useState([]);
    const [zones, setZones] = useState([]);
    const [selectedZone, setSelectedZone] = useState('all');
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchData();
        // Refresh toutes les 30 secondes pour le temps réel "simple"
        const interval = setInterval(fetchData, 30000);
        return () => clearInterval(interval);
    }, []);

    const fetchData = async () => {
        try {
            const [tablesRes, zonesRes] = await Promise.all([
                apiClient.get('/tables'),
                apiClient.get('/zones')
            ]);
            setTables(tablesRes.data);
            setZones(zonesRes.data);
        } catch (error) {
            console.error('Erreur chargement:', error);
            toast.error('Impossible de charger le plan de salle');
        } finally {
            setLoading(false);
        }
    };

    const handleTableClick = (table) => {
        if (table.statut?.toString().toUpperCase() === 'LIBRE') {
            // Démarrer une nouvelle commande
            navigate(`/serveur/pos?tableId=${table.id}`);
        } else {
            // Voir la commande en cours
            navigate(`/serveur/pos?tableId=${table.id}&mode=view`);
        }
    };

    const getStatusColor = (statut) => {
        switch (statut) {
            case 'LIBRE': return 'bg-green-100 border-green-400 text-green-800 hover:bg-green-200';
            case 'OCCUPEE': return 'bg-red-100 border-red-400 text-red-800 hover:bg-red-200';
            case 'RESERVEE': return 'bg-blue-100 border-blue-400 text-blue-800 hover:bg-blue-200';
            case 'A_NETTOYER': return 'bg-yellow-100 border-yellow-400 text-yellow-800 hover:bg-yellow-200';
            default: return 'bg-gray-100 border-gray-400 text-gray-800';
        }
    };

    const getStatusIcon = (statut) => {
        switch (statut) {
            case 'LIBRE': return <CheckCircle size={16} />;
            case 'OCCUPEE': return <Users size={16} />;
            case 'RESERVEE': return <Clock size={16} />;
            case 'A_NETTOYER': return <AlertCircle size={16} />;
            default: return null;
        }
    };

    if (loading) return <div className="flex justify-center items-center h-screen">Chargement...</div>;

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="mb-6 flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Plan de Salle</h1>
                    <p className="text-gray-600">Sélectionnez une table pour gérer les commandes</p>
                </div>

                {/* Légende */}
                <div className="flex gap-4 text-sm bg-white p-3 rounded-lg shadow-sm">
                    <div className="flex items-center gap-2">
                        <div className="w-3 h-3 rounded-full bg-green-500"></div> {formatStatut('LIBRE')}
                    </div>
                    <div className="flex items-center gap-2">
                        <div className="w-3 h-3 rounded-full bg-red-500"></div> {formatStatut('OCCUPEE')}
                    </div>
                    <div className="flex items-center gap-2">
                        <div className="w-3 h-3 rounded-full bg-blue-500"></div> {formatStatut('RESERVEE')}
                    </div>
                    <div className="flex items-center gap-2">
                        <div className="w-3 h-3 rounded-full bg-yellow-500"></div> {formatStatut('A_NETTOYER')}
                    </div>
                </div>
            </div>

            {/* Filtres Zones */}
            <div className="flex gap-2 overflow-x-auto pb-4 mb-4">
                <button
                    onClick={() => setSelectedZone('all')}
                    className={`px-4 py-2 rounded-full whitespace-nowrap transition-colors ${selectedZone === 'all'
                        ? 'bg-indigo-600 text-white'
                        : 'bg-white text-gray-700 hover:bg-gray-100 border'
                        }`}
                >
                    Tout le restaurant
                </button>
                {zones.map(zone => (
                    <button
                        key={zone.id}
                        onClick={() => setSelectedZone(zone.id)}
                        className={`px-4 py-2 rounded-full whitespace-nowrap transition-colors ${selectedZone === zone.id
                            ? 'bg-indigo-600 text-white'
                            : 'bg-white text-gray-700 hover:bg-gray-100 border'
                            }`}
                    >
                        {zone.nom}
                    </button>
                ))}
            </div>

            {/* Grille des Tables */}
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-6">
                {tables
                    .filter(t => selectedZone === 'all' || t.zone?.id === selectedZone)
                    .map(table => (
                        <button
                            key={table.id}
                            onClick={() => handleTableClick(table)}
                            className={`
                relative aspect-square rounded-2xl p-4 border-2 transition-all transform hover:scale-105 shadow-sm
                flex flex-col items-center justify-center gap-2
                ${getStatusColor(table.statut)}
              `}
                        >
                            <div className="text-3xl font-bold">{table.numero}</div>

                            <div className="flex items-center gap-1 text-sm font-medium opacity-75">
                                <Users size={14} />
                                <span>{table.capacite} pers.</span>
                            </div>

                            <div className="absolute top-3 right-3 opacity-75">
                                {getStatusIcon(table.statut)}
                            </div>

                            {/* Indicateur si commande en cours (simulé ici, à connecter au backend) */}
                            {table.statut?.toString().toUpperCase() === 'OCCUPEE' && (
                                <div className="absolute bottom-3 left-1/2 transform -translate-x-1/2 flex gap-1">
                                    <div className="w-2 h-2 rounded-full bg-orange-400 animate-pulse" title="En attente cuisine"></div>
                                </div>
                            )}
                        </button>
                    ))}
            </div>
        </div>
    );
};

export default FloorPlan;
