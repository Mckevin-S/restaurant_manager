import { useState, useEffect } from 'react';
import { Package, AlertTriangle, RefreshCw, Search } from 'lucide-react';
import apiClient from '../../services/apiClient';
import { toast } from 'react-hot-toast';

const KitchenInventory = () => {
    const [ingredients, setIngredients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        fetchInventory();
    }, []);

    const fetchInventory = async () => {
        try {
            const response = await apiClient.get('/ingredients');
            setIngredients(response.data);
        } catch (error) {
            // L'apiClient gère déjà le toast d'erreur par défaut,
            // mais on peut en ajouter un spécifique si besoin.
        } finally {
            setLoading(false);
        }
    };


    const handleSignalLowStock = async (id, currentStock) => {
        // Dans un cas réel, cela enverrait une notification au manager
        toast.success('Signalement envoyé au manager !');
    };

    const filteredIngredients = ingredients.filter(ing =>
        ing.nom.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return <div className="p-8 text-center">Chargement...</div>;

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="mb-8">
                <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                    <Package className="text-orange-600" />
                    Inventaire Cuisine
                </h1>
                <p className="text-gray-600">Vue rapide des stocks pour la production</p>
            </div>

            <div className="bg-white rounded-lg shadow-sm p-4 mb-6 sticky top-0 z-10">
                <div className="relative">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                    <input
                        type="text"
                        placeholder="Rechercher un ingrédient..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-10 pr-4 py-3 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none"
                    />
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {filteredIngredients.map(ing => {
                    const isLow = ing.quantiteStock <= ing.seuilAlerte;
                    return (
                        <div key={ing.id} className={`bg-white p-4 rounded-lg border-l-4 shadow-sm ${isLow ? 'border-red-500' : 'border-green-500'}`}>
                            <div className="flex justify-between items-start mb-2">
                                <h3 className="font-bold text-lg">{ing.nom}</h3>
                                {isLow && <AlertTriangle className="text-red-500" size={20} />}
                            </div>

                            <div className="flex justify-between items-end">
                                <div>
                                    <p className="text-sm text-gray-500">En stock</p>
                                    <p className={`text-2xl font-bold ${isLow ? 'text-red-600' : 'text-gray-800'}`}>
                                        {ing.quantiteStock} <span className="text-sm font-normal text-gray-500">{ing.unite}</span>
                                    </p>
                                </div>

                                <button
                                    onClick={() => handleSignalLowStock(ing.id, ing.quantiteStock)}
                                    className="bg-orange-50 text-orange-600 px-3 py-1.5 rounded-lg text-sm font-medium hover:bg-orange-100 transition-colors flex items-center gap-1"
                                >
                                    <RefreshCw size={14} />
                                    Signaler
                                </button>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default KitchenInventory;
