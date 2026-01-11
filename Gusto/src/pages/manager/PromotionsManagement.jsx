import { useState, useEffect } from 'react';
import {
    Plus, Edit2, Trash2, Tag, Calendar, Clock,
    Percent, DollarSign, Search, CheckCircle, XCircle, ChevronRight
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { toast } from 'react-hot-toast';
import apiClient from '../../services/apiClient';

const PromotionsManagement = () => {
    const [promotions, setPromotions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [editingPromo, setEditingPromo] = useState(null);

    const [formData, setFormData] = useState({
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

    useEffect(() => {
        fetchPromotions();
    }, []);

    const fetchPromotions = async () => {
        try {
            const response = await apiClient.get('/promotions');
            setPromotions(response.data || []);
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const data = { ...formData, value: parseFloat(formData.value) };
            if (editingPromo) {
                await apiClient.put(`/promotions/${editingPromo.id}`, data);
                toast.success('Promotion mise à jour');
            } else {
                await apiClient.post('/promotions', data);
                toast.success('Promotion créée');
            }
            fetchPromotions();
            resetForm();
        } catch (error) { }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Supprimer cette promotion ?')) return;
        try {
            await apiClient.delete(`/promotions/${id}`);
            toast.success('Supprimée');
            fetchPromotions();
        } catch (error) { }
    };

    const toggleStatus = async (promo) => {
        try {
            const updatedPromo = { ...promo, active: !promo.active };
            await apiClient.put(`/promotions/${promo.id}`, updatedPromo);
            toast.success(updatedPromo.active ? 'Activée' : 'Désactivée');
            fetchPromotions();
        } catch (error) { }
    };

    const resetForm = () => {
        setFormData({
            name: '', description: '', type: 'PERCENTAGE', value: '',
            startDate: '', endDate: '', startTime: '', endTime: '',
            active: true, daysOfWeek: [], conditions: ''
        });
        setEditingPromo(null);
        setShowForm(false);
    };

    const filteredPromotions = promotions.filter(p =>
        (p.name || '').toLowerCase().includes(searchTerm.toLowerCase())
    );

    const DAYS = [
        { key: 'MONDAY', label: 'Lun' }, { key: 'TUESDAY', label: 'Mar' },
        { key: 'WEDNESDAY', label: 'Mer' }, { key: 'THURSDAY', label: 'Jeu' },
        { key: 'FRIDAY', label: 'Ven' }, { key: 'SATURDAY', label: 'Sam' },
        { key: 'SUNDAY', label: 'Dim' }
    ];

    if (loading) return (
        <div className="flex h-screen w-full items-center justify-center">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
        </div>
    );

    return (
        <div className="min-h-screen bg-[#f8fafc] animate-in fade-in duration-500 px-4 py-8 lg:px-8">
            {/* Header Section */}
            <div className="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end">
                <div>
                    <h1 className="text-4xl font-black tracking-tight text-slate-900 flex items-center gap-3">
                        <Tag className="text-indigo-600" size={36} />
                        Offres & Promos
                    </h1>
                    <p className="mt-1 text-slate-500 font-medium">Attirez plus de clients avec des campagnes ciblées.</p>
                </div>
                <div className="flex items-center gap-3">
                    <button
                        onClick={() => setShowForm(true)}
                        className="flex items-center gap-2 rounded-2xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white shadow-lg shadow-indigo-100 transition hover:bg-indigo-700 hover:scale-[1.02] active:scale-95"
                    >
                        <Plus size={18} /> Nouvelle Campagne
                    </button>
                </div>
            </div>

            {/* Stats Overview */}
            <div className="mb-10 grid grid-cols-2 gap-4 lg:grid-cols-4">
                {[
                    { label: 'Total', val: promotions.length, color: 'text-slate-600', bg: 'bg-slate-100', icon: <Tag size={20} /> },
                    { label: 'Actives', val: promotions.filter(p => p.active).length, color: 'text-emerald-600', bg: 'bg-emerald-100', icon: <CheckCircle size={20} /> },
                    { label: 'Inactives', val: promotions.filter(p => !p.active).length, color: 'text-rose-600', bg: 'bg-rose-100', icon: <XCircle size={20} /> },
                    { label: 'Engagement', val: '24%', color: 'text-indigo-600', bg: 'bg-indigo-100', icon: <Percent size={20} /> },
                ].map((s, i) => (
                    <div key={i} className="flex items-center gap-4 rounded-[32px] bg-white p-6 shadow-sm border border-slate-50 transition hover:shadow-md">
                        <div className={`rounded-2xl ${s.bg} ${s.color} p-4`}>{s.icon}</div>
                        <div>
                            <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">{s.label}</p>
                            <p className="text-2xl font-black text-slate-900">{s.val}</p>
                        </div>
                    </div>
                ))}
            </div>

            {/* Main Content */}
            <div className="space-y-6">
                <div className="flex items-center justify-between gap-4">
                    <div className="relative flex-1 max-w-md">
                        <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                        <input
                            type="text" placeholder="Rechercher une offre..."
                            className="w-full rounded-2xl border-slate-100 bg-white py-3.5 pl-12 pr-4 text-sm font-bold text-slate-700 focus:border-indigo-500 focus:ring-0 shadow-sm"
                            value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>

                <div className="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
                    <AnimatePresence mode="popLayout">
                        {filteredPromotions.map(promo => (
                            <motion.div
                                layout initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.95 }}
                                key={promo.id}
                                className={`group relative rounded-[32px] border-2 bg-white p-8 transition shadow-sm hover:shadow-xl ${promo.active ? 'border-indigo-50' : 'border-slate-100 opacity-60 grayscale'}`}
                            >
                                <div className="mb-6 flex items-start justify-between">
                                    <div className={`rounded-2xl p-4 ${promo.type === 'PERCENTAGE' ? 'bg-indigo-50 text-indigo-600' : 'bg-emerald-50 text-emerald-600'}`}>
                                        {promo.type === 'PERCENTAGE' ? <Percent size={28} /> : <DollarSign size={28} />}
                                    </div>
                                    <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-all translate-y-2 group-hover:translate-y-0">
                                        <button onClick={() => toggleStatus(promo)} className="p-2 rounded-xl border border-slate-100 hover:bg-slate-50 transition text-slate-600">
                                            {promo.active ? <XCircle size={18} /> : <CheckCircle size={18} />}
                                        </button>
                                        <button
                                            onClick={() => {
                                                setFormData({ ...promo, value: promo.value?.toString() });
                                                setEditingPromo(promo);
                                                setShowForm(true);
                                            }}
                                            className="p-2 rounded-xl bg-slate-900 text-white hover:bg-indigo-600 transition"
                                        >
                                            <Edit2 size={18} />
                                        </button>
                                        <button onClick={() => handleDelete(promo.id)} className="p-2 rounded-xl bg-rose-50 text-rose-600 hover:bg-rose-600 hover:text-white transition">
                                            <Trash2 size={18} />
                                        </button>
                                    </div>
                                </div>

                                <h3 className="text-xl font-black text-slate-900 mb-2 leading-tight">{promo.name}</h3>
                                <p className="text-sm font-medium text-slate-500 line-clamp-2 mb-6">{promo.description}</p>

                                <div className="space-y-3 pt-6 border-t border-slate-50">
                                    <div className="flex items-center justify-between text-[10px] font-black uppercase tracking-widest">
                                        <span className="text-slate-400">Réduction</span>
                                        <span className="text-indigo-600 text-lg">
                                            {promo.type === 'PERCENTAGE' ? `-${promo.value}%` : `-${promo.value} FCFA`}
                                        </span>
                                    </div>
                                    <div className="flex items-center justify-between text-[10px] font-black uppercase tracking-widest text-slate-400">
                                        <span className="flex items-center gap-1.5"><Calendar size={12} /> Validité</span>
                                        <span className="text-slate-800">{promo.endDate ? new Date(promo.endDate).toLocaleDateString() : 'Illimité'}</span>
                                    </div>
                                </div>
                            </motion.div>
                        ))}
                    </AnimatePresence>
                </div>
            </div>

            {/* Modal Form */}
            {showForm && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/40 backdrop-blur-md p-4 overflow-y-auto">
                    <motion.div
                        initial={{ scale: 0.9, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        className="my-auto w-full max-w-2xl rounded-[40px] bg-white p-8 shadow-2xl lg:p-12"
                    >
                        <h3 className="text-3xl font-black text-slate-900 mb-8 uppercase tracking-tight italic">
                            {editingPromo ? 'Cibler Campagne' : 'Nouvelle Promo'}
                        </h3>
                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Titre de l'offre</label>
                                    <input
                                        required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })}
                                        placeholder="Ex: Gold Happy Hour"
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Type de remise</label>
                                    <select
                                        className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={formData.type} onChange={e => setFormData({ ...formData, type: e.target.value })}
                                    >
                                        <option value="PERCENTAGE">Pourcentage (%)</option>
                                        <option value="FIXED_AMOUNT">Montant fixe (FCFA)</option>
                                    </select>
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Valeur de la réduction</label>
                                <div className="relative">
                                    <input
                                        type="number" required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={formData.value} onChange={e => setFormData({ ...formData, value: e.target.value })}
                                    />
                                    <div className="absolute right-6 top-1/2 -translate-y-1/2 text-slate-400 font-black">
                                        {formData.type === 'PERCENTAGE' ? '%' : 'FCFA'}
                                    </div>
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Description accrocheuse</label>
                                <textarea
                                    className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                    rows="2" value={formData.description} onChange={e => setFormData({ ...formData, description: e.target.value })}
                                />
                            </div>

                            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Date début</label>
                                    <input
                                        type="date" className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={formData.startDate} onChange={e => setFormData({ ...formData, startDate: e.target.value })}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Date Fin</label>
                                    <input
                                        type="date" className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={formData.endDate} onChange={e => setFormData({ ...formData, endDate: e.target.value })}
                                    />
                                </div>
                            </div>

                            <div className="flex gap-4 pt-4">
                                <button type="button" onClick={resetForm} className="flex-1 rounded-2xl py-5 font-black uppercase tracking-widest text-slate-500 hover:bg-slate-50 transition">Fermer</button>
                                <button type="submit" className="flex-1 rounded-[24px] bg-slate-900 py-5 font-black uppercase tracking-widest text-white shadow-xl shadow-slate-200 hover:bg-indigo-600 transition active:scale-95">
                                    {editingPromo ? 'Mettre à jour' : 'Lancer Campagne'}
                                </button>
                            </div>
                        </form>
                    </motion.div>
                </div>
            )}
        </div>
    );
};

export default PromotionsManagement;
