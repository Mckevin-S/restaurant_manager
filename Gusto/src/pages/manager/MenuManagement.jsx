import { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Upload, Eye, EyeOff, Search } from 'lucide-react';
import axios from 'axios';
import { toast } from 'react-hot-toast';

const MenuManagement = () => {
    const [plats, setPlats] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('all');
    const [searchTerm, setSearchTerm] = useState('');
    const [showPlatForm, setShowPlatForm] = useState(false);
    const [showCategoryForm, setShowCategoryForm] = useState(false);
    const [editingPlat, setEditingPlat] = useState(null);
    const [loading, setLoading] = useState(true);
    const [imagePreview, setImagePreview] = useState(null);

    const [platForm, setPlatForm] = useState({
        nom: '',
        description: '',
        prix: '',
        category: '',
        disponibilite: true,
        photoUrl: ''
    });

    const [categoryForm, setCategoryForm] = useState({
        nom: '',
        description: ''
    });

    const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:3006/api';

    useEffect(() => {
        fetchPlats();
        fetchCategories();
    }, []);

    const fetchPlats = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`${API_URL}/plats`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setPlats(response.data);
        } catch (error) {
            toast.error('Erreur lors du chargement des plats');
        } finally {
            setLoading(false);
        }
    };

    const fetchCategories = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`${API_URL}/categories`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setCategories(response.data);
        } catch (error) {
            console.error('Erreur catégories:', error);
        }
    };

    const handleCreatePlat = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');
            const data = {
                ...platForm,
                prix: parseFloat(platForm.prix)
            };

            if (editingPlat) {
                await axios.put(`${API_URL}/plats/${editingPlat.id}`, data, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                toast.success('Plat modifié avec succès');
            } else {
                await axios.post(`${API_URL}/plats`, data, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                toast.success('Plat créé avec succès');
            }
            fetchPlats();
            resetPlatForm();
        } catch (error) {
            toast.error('Erreur lors de la sauvegarde');
            console.error(error);
        }
    };

    const handleImageUpload = async (e, platId) => {
        const file = e.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        try {
            const token = localStorage.getItem('token');
            const response = await axios.post(
                `${API_URL}/plats/${platId}/upload-image`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data'
                    }
                }
            );
            toast.success('Image uploadée avec succès');
            fetchPlats();
        } catch (error) {
            toast.error('Erreur lors de l\'upload');
        }
    };

    const handleDeletePlat = async (id) => {
        if (!confirm('Êtes-vous sûr de vouloir supprimer ce plat ?')) return;

        try {
            const token = localStorage.getItem('token');
            await axios.delete(`${API_URL}/plats/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            toast.success('Plat supprimé');
            fetchPlats();
        } catch (error) {
            toast.error('Erreur lors de la suppression');
        }
    };

    const toggleDisponibilite = async (plat) => {
        try {
            const token = localStorage.getItem('token');
            await axios.patch(
                `${API_URL}/plats/${plat.id}/statut-disponibilite?disponible=${!plat.disponibilite}`,
                {},
                { headers: { Authorization: `Bearer ${token}` } }
            );
            toast.success('Disponibilité mise à jour');
            fetchPlats();
        } catch (error) {
            toast.error('Erreur lors de la mise à jour');
        }
    };

    const handleCreateCategory = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');
            await axios.post(`${API_URL}/categories`, categoryForm, {
                headers: { Authorization: `Bearer ${token}` }
            });
            toast.success('Catégorie créée avec succès');
            fetchCategories();
            setShowCategoryForm(false);
            setCategoryForm({ nom: '', description: '' });
        } catch (error) {
            toast.error('Erreur lors de la création');
        }
    };

    const resetPlatForm = () => {
        setPlatForm({
            nom: '',
            description: '',
            prix: '',
            category: '',
            disponibilite: true,
            photoUrl: ''
        });
        setEditingPlat(null);
        setShowPlatForm(false);
        setImagePreview(null);
    };

    const editPlat = (plat) => {
        setPlatForm({
            nom: plat.nom,
            description: plat.description || '',
            prix: plat.prix.toString(),
            category: plat.category || '',
            disponibilite: plat.disponibilite,
            photoUrl: plat.photoUrl || ''
        });
        setEditingPlat(plat);
        setImagePreview(plat.photoUrl);
        setShowPlatForm(true);
    };

    const filteredPlats = plats.filter(plat => {
        const matchCategory = selectedCategory === 'all' || plat.category === parseInt(selectedCategory);
        const matchSearch = plat.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
            (plat.description && plat.description.toLowerCase().includes(searchTerm.toLowerCase()));
        return matchCategory && matchSearch;
    });

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            {/* Header */}
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Gestion du Menu</h1>
                <p className="text-gray-600">Gérez les plats et catégories de votre carte</p>
            </div>

            {/* Actions Bar */}
            <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
                <div className="flex flex-wrap gap-4 items-center justify-between">
                    <div className="flex gap-3">
                        <button
                            onClick={() => setShowPlatForm(true)}
                            className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition"
                        >
                            <Plus size={20} />
                            Nouveau Plat
                        </button>
                        <button
                            onClick={() => setShowCategoryForm(true)}
                            className="flex items-center gap-2 bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition"
                        >
                            <Plus size={20} />
                            Nouvelle Catégorie
                        </button>
                    </div>

                    <div className="flex gap-3 flex-1 max-w-2xl">
                        <div className="relative flex-1">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                            <input
                                type="text"
                                placeholder="Rechercher un plat..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                            />
                        </div>

                        <select
                            value={selectedCategory}
                            onChange={(e) => setSelectedCategory(e.target.value)}
                            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        >
                            <option value="all">Toutes catégories</option>
                            {categories.map(cat => (
                                <option key={cat.id} value={cat.id}>{cat.nom}</option>
                            ))}
                        </select>
                    </div>
                </div>
            </div>

            {/* Statistiques */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <p className="text-sm text-gray-600">Total Plats</p>
                    <p className="text-2xl font-bold text-gray-900">{plats.length}</p>
                </div>
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <p className="text-sm text-gray-600">Disponibles</p>
                    <p className="text-2xl font-bold text-green-600">
                        {plats.filter(p => p.disponibilite).length}
                    </p>
                </div>
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <p className="text-sm text-gray-600">Indisponibles</p>
                    <p className="text-2xl font-bold text-red-600">
                        {plats.filter(p => !p.disponibilite).length}
                    </p>
                </div>
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <p className="text-sm text-gray-600">Catégories</p>
                    <p className="text-2xl font-bold text-gray-900">{categories.length}</p>
                </div>
            </div>

            {/* Liste des plats */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {filteredPlats.map(plat => (
                    <div key={plat.id} className="bg-white rounded-lg shadow-sm overflow-hidden hover:shadow-md transition">
                        {/* Image */}
                        <div className="relative h-48 bg-gray-200">
                            {plat.photoUrl ? (
                                <img
                                    src={plat.photoUrl}
                                    alt={plat.nom}
                                    className="w-full h-full object-cover"
                                />
                            ) : (
                                <div className="flex items-center justify-center h-full text-gray-400">
                                    <Upload size={48} />
                                </div>
                            )}

                            {/* Badge disponibilité */}
                            <div className="absolute top-2 right-2">
                                <button
                                    onClick={() => toggleDisponibilite(plat)}
                                    className={`px-3 py-1 rounded-full text-xs font-medium ${plat.disponibilite
                                            ? 'bg-green-100 text-green-800'
                                            : 'bg-red-100 text-red-800'
                                        }`}
                                >
                                    {plat.disponibilite ? (
                                        <span className="flex items-center gap-1">
                                            <Eye size={14} /> Disponible
                                        </span>
                                    ) : (
                                        <span className="flex items-center gap-1">
                                            <EyeOff size={14} /> Indisponible
                                        </span>
                                    )}
                                </button>
                            </div>

                            {/* Upload image */}
                            <label className="absolute bottom-2 right-2 bg-white p-2 rounded-full shadow-lg cursor-pointer hover:bg-gray-50">
                                <Upload size={16} className="text-gray-600" />
                                <input
                                    type="file"
                                    accept="image/*"
                                    className="hidden"
                                    onChange={(e) => handleImageUpload(e, plat.id)}
                                />
                            </label>
                        </div>

                        {/* Contenu */}
                        <div className="p-4">
                            <h3 className="font-bold text-lg text-gray-900 mb-1">{plat.nom}</h3>
                            {plat.description && (
                                <p className="text-sm text-gray-600 mb-3 line-clamp-2">{plat.description}</p>
                            )}

                            <div className="flex items-center justify-between mb-3">
                                <span className="text-2xl font-bold text-indigo-600">
                                    {plat.prix.toFixed(2)} FCFA
                                </span>
                                {categories.find(c => c.id === plat.category) && (
                                    <span className="text-xs bg-gray-100 text-gray-700 px-2 py-1 rounded">
                                        {categories.find(c => c.id === plat.category).nom}
                                    </span>
                                )}
                            </div>

                            {/* Actions */}
                            <div className="flex gap-2">
                                <button
                                    onClick={() => editPlat(plat)}
                                    className="flex-1 flex items-center justify-center gap-2 px-3 py-2 bg-indigo-50 text-indigo-600 rounded-lg hover:bg-indigo-100 transition"
                                >
                                    <Edit2 size={16} />
                                    Modifier
                                </button>
                                <button
                                    onClick={() => handleDeletePlat(plat.id)}
                                    className="flex items-center justify-center px-3 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition"
                                >
                                    <Trash2 size={16} />
                                </button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {filteredPlats.length === 0 && (
                <div className="text-center py-12">
                    <p className="text-gray-500">Aucun plat trouvé</p>
                </div>
            )}

            {/* Modal Formulaire Plat */}
            {showPlatForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50 overflow-y-auto">
                    <div className="bg-white rounded-lg max-w-2xl w-full p-6 my-8">
                        <h3 className="text-xl font-bold mb-4">
                            {editingPlat ? 'Modifier le plat' : 'Nouveau plat'}
                        </h3>
                        <form onSubmit={handleCreatePlat} className="space-y-4">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Nom du plat *
                                    </label>
                                    <input
                                        type="text"
                                        required
                                        value={platForm.nom}
                                        onChange={(e) => setPlatForm({ ...platForm, nom: e.target.value })}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                        placeholder="Ex: Pizza Margherita"
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Prix (FCFA) *
                                    </label>
                                    <input
                                        type="number"
                                        required
                                        min="0"
                                        step="0.01"
                                        value={platForm.prix}
                                        onChange={(e) => setPlatForm({ ...platForm, prix: e.target.value })}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                        placeholder="5000"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Description
                                </label>
                                <textarea
                                    value={platForm.description}
                                    onChange={(e) => setPlatForm({ ...platForm, description: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    rows="3"
                                    placeholder="Description du plat..."
                                />
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Catégorie *
                                    </label>
                                    <select
                                        required
                                        value={platForm.category}
                                        onChange={(e) => setPlatForm({ ...platForm, category: e.target.value })}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    >
                                        <option value="">Sélectionner une catégorie</option>
                                        {categories.map(cat => (
                                            <option key={cat.id} value={cat.id}>{cat.nom}</option>
                                        ))}
                                    </select>
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Disponibilité
                                    </label>
                                    <select
                                        value={platForm.disponibilite}
                                        onChange={(e) => setPlatForm({ ...platForm, disponibilite: e.target.value === 'true' })}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    >
                                        <option value="true">Disponible</option>
                                        <option value="false">Indisponible</option>
                                    </select>
                                </div>
                            </div>

                            <div className="flex gap-3 pt-4">
                                <button
                                    type="button"
                                    onClick={resetPlatForm}
                                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                                >
                                    Annuler
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
                                >
                                    {editingPlat ? 'Modifier' : 'Créer'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Modal Formulaire Catégorie */}
            {showCategoryForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg max-w-md w-full p-6">
                        <h3 className="text-xl font-bold mb-4">Nouvelle catégorie</h3>
                        <form onSubmit={handleCreateCategory} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Nom de la catégorie *
                                </label>
                                <input
                                    type="text"
                                    required
                                    value={categoryForm.nom}
                                    onChange={(e) => setCategoryForm({ ...categoryForm, nom: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    placeholder="Ex: Entrées, Plats, Desserts..."
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Description
                                </label>
                                <textarea
                                    value={categoryForm.description}
                                    onChange={(e) => setCategoryForm({ ...categoryForm, description: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    rows="3"
                                />
                            </div>

                            <div className="flex gap-3 pt-4">
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowCategoryForm(false);
                                        setCategoryForm({ nom: '', description: '' });
                                    }}
                                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                                >
                                    Annuler
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                                >
                                    Créer
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default MenuManagement;
