import { useState, useEffect } from 'react';
import { Package, AlertTriangle, TrendingDown, Plus, Search } from 'lucide-react';
import apiClient from '../../services/apiClient';
import { toast } from 'react-hot-toast';


const StockManagement = () => {
    const [ingredients, setIngredients] = useState([]);
    const [movements, setMovements] = useState([]);
    const [showMovementForm, setShowMovementForm] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterAlert, setFilterAlert] = useState('all');
    const [loading, setLoading] = useState(true);

    const [movementForm, setMovementForm] = useState({
        ingredientId: '',
        type: 'ENTREE',
        quantite: '',
        motif: ''
    });


    useEffect(() => {
        fetchIngredients();
        fetchMovements();
    }, []);

    const fetchIngredients = async () => {
        try {
            const response = await apiClient.get('/ingredients');
            setIngredients(response.data);
        } catch (error) {
            //ApiClient gère déjà les erreurs
        } finally {
            setLoading(false);
        }
    };


    const fetchMovements = async () => {
        try {
            const response = await apiClient.get('/stock-movements');
            setMovements(response.data.slice(0, 10)); // 10 derniers mouvements
        } catch (error) {
            console.error('Erreur mouvements:', error);
        }
    };


    const handleCreateMovement = async (e) => {
        e.preventDefault();
        try {
            await apiClient.post('/stock-movements', {
                ...movementForm,
                quantite: parseFloat(movementForm.quantite)
            });
            toast.success('Mouvement enregistré');
            fetchIngredients();
            fetchMovements();
            setShowMovementForm(false);
            setMovementForm({ ingredientId: '', type: 'ENTREE', quantite: '', motif: '' });
        } catch (error) {
            toast.error('Erreur lors de l\'enregistrement');
        }
    };


    const getStockStatus = (ingredient) => {
        if (ingredient.quantiteStock <= ingredient.seuilAlerte) {
            return { label: 'Critique', color: 'bg-red-100 text-red-800 border-red-300' };
        } else if (ingredient.quantiteStock <= ingredient.seuilAlerte * 1.5) {
            return { label: 'Faible', color: 'bg-yellow-100 text-yellow-800 border-yellow-300' };
        }
        return { label: 'Normal', color: 'bg-green-100 text-green-800 border-green-300' };
    };

    const filteredIngredients = ingredients.filter(ing => {
        const matchSearch = ing.nom.toLowerCase().includes(searchTerm.toLowerCase());
        const matchFilter = filterAlert === 'all' ||
            (filterAlert === 'alert' && ing.quantiteStock <= ing.seuilAlerte) ||
            (filterAlert === 'low' && ing.quantiteStock > ing.seuilAlerte && ing.quantiteStock <= ing.seuilAlerte * 1.5);
        return matchSearch && matchFilter;
    });

    const alertCount = ingredients.filter(i => i.quantiteStock <= i.seuilAlerte).length;
    const lowCount = ingredients.filter(i => i.quantiteStock > i.seuilAlerte && i.quantiteStock <= i.seuilAlerte * 1.5).length;

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>;
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Gestion du Stock</h1>
                <p className="text-gray-600">Suivez vos ingrédients et mouvements de stock</p>
            </div>

            {/* Actions */}
            <div className="bg-white rounded-lg shadow-sm p-4 mb-6 flex flex-wrap gap-4 items-center justify-between">
                <button
                    onClick={() => setShowMovementForm(true)}
                    className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700"
                >
                    <Plus size={20} />
                    Nouveau Mouvement
                </button>

                <div className="flex gap-3 flex-1 max-w-2xl">
                    <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                        <input
                            type="text"
                            placeholder="Rechercher un ingrédient..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>

                    <select
                        value={filterAlert}
                        onChange={(e) => setFilterAlert(e.target.value)}
                        className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                    >
                        <option value="all">Tous</option>
                        <option value="alert">Critique</option>
                        <option value="low">Faible</option>
                    </select>
                </div>
            </div>

            {/* Statistiques */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600">Total Ingrédients</p>
                            <p className="text-2xl font-bold text-gray-900">{ingredients.length}</p>
                        </div>
                        <Package className="text-indigo-600" size={32} />
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600">Stock Critique</p>
                            <p className="text-2xl font-bold text-red-600">{alertCount}</p>
                        </div>
                        <AlertTriangle className="text-red-600" size={32} />
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600">Stock Faible</p>
                            <p className="text-2xl font-bold text-yellow-600">{lowCount}</p>
                        </div>
                        <TrendingDown className="text-yellow-600" size={32} />
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600">Mouvements (7j)</p>
                            <p className="text-2xl font-bold text-gray-900">{movements.length}</p>
                        </div>
                        <Package className="text-gray-600" size={32} />
                    </div>
                </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Liste des ingrédients */}
                <div className="lg:col-span-2">
                    <div className="bg-white rounded-lg shadow-sm overflow-hidden">
                        <div className="p-4 border-b">
                            <h2 className="text-lg font-bold">Ingrédients</h2>
                        </div>
                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead className="bg-gray-50">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Ingrédient</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Unité</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Seuil</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-200">
                                    {filteredIngredients.map(ing => {
                                        const status = getStockStatus(ing);
                                        return (
                                            <tr key={ing.id} className="hover:bg-gray-50">
                                                <td className="px-6 py-4 whitespace-nowrap">
                                                    <div className="font-medium text-gray-900">{ing.nom}</div>
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap">
                                                    <div className="text-sm font-bold text-gray-900">{ing.quantiteStock}</div>
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                    {ing.unite}
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                    {ing.seuilAlerte}
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap">
                                                    <span className={`px-2 py-1 text-xs font-medium rounded-full ${status.color}`}>
                                                        {status.label}
                                                    </span>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                {/* Derniers mouvements */}
                <div className="bg-white rounded-lg shadow-sm overflow-hidden">
                    <div className="p-4 border-b">
                        <h2 className="text-lg font-bold">Derniers Mouvements</h2>
                    </div>
                    <div className="p-4 space-y-3">
                        {movements.map((mov, idx) => (
                            <div key={idx} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                                <div className="flex-1">
                                    <p className="font-medium text-sm">{mov.ingredient?.nom}</p>
                                    <p className="text-xs text-gray-500">{mov.motif}</p>
                                </div>
                                <div className={`text-right ${mov.type === 'ENTREE' ? 'text-green-600' : 'text-red-600'}`}>
                                    <p className="font-bold">{mov.type === 'ENTREE' ? '+' : '-'}{mov.quantite}</p>
                                    <p className="text-xs">{new Date(mov.dateHeure).toLocaleDateString()}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Modal Mouvement */}
            {showMovementForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg max-w-md w-full p-6">
                        <h3 className="text-xl font-bold mb-4">Nouveau Mouvement de Stock</h3>
                        <form onSubmit={handleCreateMovement} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Ingrédient *</label>
                                <select
                                    required
                                    value={movementForm.ingredientId}
                                    onChange={(e) => setMovementForm({ ...movementForm, ingredientId: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                >
                                    <option value="">Sélectionner un ingrédient</option>
                                    {ingredients.map(ing => (
                                        <option key={ing.id} value={ing.id}>{ing.nom} ({ing.quantiteStock} {ing.unite})</option>
                                    ))}
                                </select>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Type *</label>
                                <select
                                    value={movementForm.type}
                                    onChange={(e) => setMovementForm({ ...movementForm, type: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                >
                                    <option value="ENTREE">Entrée (+)</option>
                                    <option value="SORTIE">Sortie (-)</option>
                                </select>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Quantité *</label>
                                <input
                                    type="number"
                                    required
                                    min="0"
                                    step="0.01"
                                    value={movementForm.quantite}
                                    onChange={(e) => setMovementForm({ ...movementForm, quantite: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Motif *</label>
                                <textarea
                                    required
                                    value={movementForm.motif}
                                    onChange={(e) => setMovementForm({ ...movementForm, motif: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    rows="3"
                                    placeholder="Raison du mouvement..."
                                />
                            </div>

                            <div className="flex gap-3 pt-4">
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowMovementForm(false);
                                        setMovementForm({ ingredientId: '', type: 'ENTREE', quantite: '', motif: '' });
                                    }}
                                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                                >
                                    Annuler
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
                                >
                                    Enregistrer
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default StockManagement;
