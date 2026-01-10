import { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Tag, Calendar, Clock, Percent, DollarSign, Search, CheckCircle, XCircle } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { getPromotions, createPromotion, updatePromotion, deletePromotion } from '../../services/api';

const PromotionsManagement = () => {
    const [promotions, setPromotions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [editingPromo, setEditingPromo] = useState(null);

    const [formData, setFormData] = useState({
        name: '',
        description: '',
        type: 'PERCENTAGE', // PERCENTAGE, FIXED_AMOUNT, MENU_SPECIAL
        value: '',
        startDate: '',
        endDate: '',
        startTime: '',
        endTime: '',
        active: true,
        daysOfWeek: [], // ['MONDAY', 'TUESDAY', ...]
        conditions: ''
    });

    useEffect(() => {
        fetchPromotions();
    }, []);

    const fetchPromotions = async () => {
        try {
            const response = await getPromotions();
            setPromotions(response.data);
        } catch (error) {
            toast.error('Erreur lors du chargement des promotions');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const data = { ...formData, value: parseFloat(formData.value) };

            if (editingPromo) {
                await updatePromotion(editingPromo.id, data);
                toast.success('Promotion modifiée avec succès');
            } else {
                await createPromotion(data);
                toast.success('Promotion créée avec succès');
            }
            fetchPromotions();
            resetForm();
        } catch (error) {
            toast.error('Erreur lors de la sauvegarde');
            console.error(error);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette promotion ?')) return;

        try {
            await deletePromotion(id);
            toast.success('Promotion supprimée');
            fetchPromotions();
        } catch (error) {
            toast.error('Erreur lors de la suppression');
        }
    };

    const toggleStatus = async (promo) => {
        try {
            const updatedPromo = { ...promo, active: !promo.active };
            await updatePromotion(promo.id, updatedPromo);
            toast.success(updatedPromo.active ? 'Promotion activée' : 'Promotion désactivée');
            fetchPromotions(); // Refresh to keep state consistent
        } catch (error) {
            toast.error('Erreur lors du changement de statut');
        }
    };

    const resetForm = () => {
        setFormData({
            name: '',
            description: '',
            type: 'PERCENTAGE',
            value: '',
            startDate: '',
            endDate: '',
            startTime: '',
            endTime: '',
            active: true,
            daysOfWeek: [],
            conditions: ''
        });
        setEditingPromo(null);
        setShowForm(false);
    };

    const handleEdit = (promo) => {
        setFormData({
            ...promo,
            value: promo.value.toString()
        });
        setEditingPromo(promo);
        setShowForm(true);
    };

    const handleDayToggle = (day) => {
        const days = formData.daysOfWeek.includes(day)
            ? formData.daysOfWeek.filter(d => d !== day)
            : [...formData.daysOfWeek, day];
        setFormData({ ...formData, daysOfWeek: days });
    };

    const filteredPromotions = promotions.filter(p =>
        p.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.description.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const DAYS = [
        { key: 'MONDAY', label: 'Lun' },
        { key: 'TUESDAY', label: 'Mar' },
        { key: 'WEDNESDAY', label: 'Mer' },
        { key: 'THURSDAY', label: 'Jeu' },
        { key: 'FRIDAY', label: 'Ven' },
        { key: 'SATURDAY', label: 'Sam' },
        { key: 'SUNDAY', label: 'Dim' }
    ];

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
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Gestion des Promotions</h1>
                <p className="text-gray-600">Configurez les offres spéciales et réductions</p>
            </div>

            {/* Actions Bar */}
            <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
                <div className="flex flex-wrap gap-4 items-center justify-between">
                    <button
                        onClick={() => setShowForm(true)}
                        className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition"
                    >
                        <Plus size={20} />
                        Nouvelle Promotion
                    </button>

                    <div className="relative flex-1 max-w-md">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                        <input
                            type="text"
                            placeholder="Rechercher une promotion..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        />
                    </div>
                </div>
            </div>

            {/* Stats Overview */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="bg-white p-4 rounded-lg shadow-sm border-l-4 border-indigo-500">
                    <div className="text-gray-500 text-sm">Total Promotions</div>
                    <div className="text-2xl font-bold">{promotions.length}</div>
                </div>
                <div className="bg-white p-4 rounded-lg shadow-sm border-l-4 border-green-500">
                    <div className="text-gray-500 text-sm">Actives</div>
                    <div className="text-2xl font-bold">{promotions.filter(p => p.active).length}</div>
                </div>
                <div className="bg-white p-4 rounded-lg shadow-sm border-l-4 border-gray-500">
                    <div className="text-gray-500 text-sm">Inactives</div>
                    <div className="text-2xl font-bold">{promotions.filter(p => !p.active).length}</div>
                </div>
            </div>

            {/* Liste des promotions */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {filteredPromotions.map(promo => (
                    <div key={promo.id} className={`bg-white rounded-lg shadow-sm p-6 border transition hover:shadow-md ${promo.active ? 'border-green-200' : 'border-gray-200 opacity-75'}`}>
                        <div className="flex justify-between items-start mb-4">
                            <div className="flex items-start gap-3">
                                <div className={`p-3 rounded-full ${promo.type === 'PERCENTAGE' ? 'bg-blue-100 text-blue-600' : 'bg-purple-100 text-purple-600'}`}>
                                    {promo.type === 'PERCENTAGE' ? <Percent size={24} /> : <DollarSign size={24} />}
                                </div>
                                <div>
                                    <h3 className="font-bold text-lg text-gray-900">{promo.name}</h3>
                                    <p className="text-gray-600 text-sm">{promo.description}</p>
                                </div>
                            </div>
                            <div className="flex items-center gap-2">
                                <button
                                    onClick={() => toggleStatus(promo)}
                                    className={`p-2 rounded-full transition ${promo.active ? 'bg-green-100 text-green-600 hover:bg-green-200' : 'bg-gray-100 text-gray-400 hover:bg-gray-200'}`}
                                    title={promo.active ? 'Désactiver' : 'Activer'}
                                >
                                    {promo.active ? <CheckCircle size={20} /> : <XCircle size={20} />}
                                </button>
                                <button
                                    onClick={() => handleEdit(promo)}
                                    className="p-2 rounded-full bg-indigo-50 text-indigo-600 hover:bg-indigo-100 transition"
                                >
                                    <Edit2 size={20} />
                                </button>
                                <button
                                    onClick={() => handleDelete(promo.id)}
                                    className="p-2 rounded-full bg-red-50 text-red-600 hover:bg-red-100 transition"
                                >
                                    <Trash2 size={20} />
                                </button>
                            </div>
                        </div>

                        <div className="space-y-3">
                            <div className="flex items-center gap-2 text-sm text-gray-600">
                                <Tag size={16} />
                                <span className="font-medium">
                                    {promo.type === 'PERCENTAGE' ? `-${promo.value}%` : `-${promo.value} FCFA`}
                                </span>
                            </div>

                            {(promo.startDate || promo.endDate) && (
                                <div className="flex items-center gap-2 text-sm text-gray-600">
                                    <Calendar size={16} />
                                    <span>
                                        {promo.startDate ? new Date(promo.startDate).toLocaleDateString() : '...'}
                                        {' à '}
                                        {promo.endDate ? new Date(promo.endDate).toLocaleDateString() : '...'}
                                    </span>
                                </div>
                            )}

                            {(promo.startTime || promo.endTime) && (
                                <div className="flex items-center gap-2 text-sm text-gray-600">
                                    <Clock size={16} />
                                    <span>{promo.startTime} - {promo.endTime}</span>
                                </div>
                            )}

                            {promo.daysOfWeek && promo.daysOfWeek.length > 0 && (
                                <div className="flex flex-wrap gap-1 mt-2">
                                    {promo.daysOfWeek.map(day => {
                                        const dayObj = DAYS.find(d => d.key === day);
                                        return (
                                            <span key={day} className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded">
                                                {dayObj ? dayObj.label : day}
                                            </span>
                                        );
                                    })}
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            {/* Modal Form */}
            {showForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50 overflow-y-auto">
                    <div className="bg-white rounded-lg max-w-2xl w-full p-6 my-8">
                        <h3 className="text-xl font-bold mb-4">
                            {editingPromo ? 'Modifier la promotion' : 'Nouvelle promotion'}
                        </h3>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Nom *</label>
                                    <input
                                        type="text"
                                        required
                                        value={formData.name}
                                        onChange={e => setFormData({ ...formData, name: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                        placeholder="Ex: Happy Hour"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Type *</label>
                                    <select
                                        value={formData.type}
                                        onChange={e => setFormData({ ...formData, type: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    >
                                        <option value="PERCENTAGE">Pourcentage (%)</option>
                                        <option value="FIXED_AMOUNT">Montant fixe (FCFA)</option>
                                        <option value="MENU_SPECIAL">Menu Spécial</option>
                                    </select>
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Valeur *</label>
                                <input
                                    type="number"
                                    required
                                    min="0"
                                    value={formData.value}
                                    onChange={e => setFormData({ ...formData, value: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    placeholder={formData.type === 'PERCENTAGE' ? "Ex: 20" : "Ex: 1000"}
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                                <textarea
                                    value={formData.description}
                                    onChange={e => setFormData({ ...formData, description: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    rows="2"
                                />
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Date début</label>
                                    <input
                                        type="date"
                                        value={formData.startDate}
                                        onChange={e => setFormData({ ...formData, startDate: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Date fin</label>
                                    <input
                                        type="date"
                                        value={formData.endDate}
                                        onChange={e => setFormData({ ...formData, endDate: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Heure début</label>
                                    <input
                                        type="time"
                                        value={formData.startTime}
                                        onChange={e => setFormData({ ...formData, startTime: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Heure fin</label>
                                    <input
                                        type="time"
                                        value={formData.endTime}
                                        onChange={e => setFormData({ ...formData, endTime: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Jours d'application</label>
                                <div className="flex flex-wrap gap-2">
                                    {DAYS.map(day => (
                                        <button
                                            key={day.key}
                                            type="button"
                                            onClick={() => handleDayToggle(day.key)}
                                            className={`px-3 py-1 rounded-full text-sm font-medium transition ${formData.daysOfWeek.includes(day.key)
                                                ? 'bg-indigo-600 text-white'
                                                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                                                }`}
                                        >
                                            {day.label}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <div className="flex gap-3 pt-4">
                                <button
                                    type="button"
                                    onClick={resetForm}
                                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                                >
                                    Annuler
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
                                >
                                    {editingPromo ? 'Modifier' : 'Créer'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PromotionsManagement;
