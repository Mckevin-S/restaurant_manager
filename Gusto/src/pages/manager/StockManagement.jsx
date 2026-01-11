import { useState, useEffect } from 'react';
import {
    Package, AlertTriangle, TrendingDown, Plus, Search,
    ChevronRight, ArrowUpRight, ArrowDownRight, History, Filter
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
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
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const [ingRes, movRes] = await Promise.all([
                apiClient.get('/ingredients'),
                apiClient.get('/stock-movements')
            ]);
            setIngredients(ingRes.data);
            setMovements(movRes.data.slice(0, 15));
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateMovement = async (e) => {
        e.preventDefault();
        try {
            await apiClient.post('/stock-movements', {
                ...movementForm,
                quantite: parseFloat(movementForm.quantite)
            });
            toast.success('Stock mis à jour');
            fetchData();
            setShowMovementForm(false);
            setMovementForm({ ingredientId: '', type: 'ENTREE', quantite: '', motif: '' });
        } catch (error) {
            toast.error('Erreur enregistrement');
        }
    };

    const getStockStatus = (ingredient) => {
        if (ingredient.stockActuel <= (ingredient.alerteStock || 5)) {
            return { label: 'CRITIQUE', bg: 'bg-rose-50', text: 'text-rose-600', dot: 'bg-rose-500' };
        } else if (ingredient.stockActuel <= (ingredient.alerteStock || 5) * 1.5) {
            return { label: 'FAIBLE', bg: 'bg-amber-50', text: 'text-amber-600', dot: 'bg-amber-500' };
        }
        return { label: 'OPTIMAL', bg: 'bg-emerald-50', text: 'text-emerald-600', dot: 'bg-emerald-500' };
    };

    const filteredIngredients = ingredients.filter(ing => {
        const matchSearch = ing.nom.toLowerCase().includes(searchTerm.toLowerCase());
        const matchFilter = filterAlert === 'all' ||
            (filterAlert === 'alert' && ing.stockActuel <= (ing.alerteStock || 5)) ||
            (filterAlert === 'low' && ing.stockActuel > (ing.alerteStock || 5) && ing.stockActuel <= (ing.alerteStock || 5) * 1.5);
        return matchSearch && matchFilter;
    });

    const categories = Array.from(new Set(ingredients.map(i => i.categorie))).filter(Boolean);

    if (loading) return (
        <div className="flex h-screen w-full items-center justify-center">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
        </div>
    );

    return (
        <div className="min-h-screen bg-[#f8fafc] animate-in fade-in duration-500 px-4 py-8">
            {/* Header */}
            <div className="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end">
                <div>
                    <h1 className="text-4xl font-black tracking-tight text-slate-900 flex items-center gap-3">
                        <Package className="text-indigo-600" size={36} />
                        Inventaire Stock
                    </h1>
                    <p className="mt-1 text-slate-500 font-medium">Suivi des ingrédients et flux logistiques.</p>
                </div>
                <div className="flex items-center gap-3">
                    <button
                        onClick={() => setShowMovementForm(true)}
                        className="flex items-center gap-2 rounded-2xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white shadow-lg shadow-indigo-100 transition hover:bg-indigo-700 hover:scale-[1.02] active:scale-95"
                    >
                        <Plus size={18} /> Mouvement de Stock
                    </button>
                </div>
            </div>

            {/* Stats Summary */}
            <div className="mb-8 grid grid-cols-2 gap-4 lg:grid-cols-4">
                {[
                    { label: 'Total Ingrédients', val: ingredients.length, color: 'text-slate-600', bg: 'bg-slate-100', icon: <Package size={20} /> },
                    { label: 'Alertes Critique', val: ingredients.filter(i => i.stockActuel <= (i.alerteStock || 5)).length, color: 'text-rose-600', bg: 'bg-rose-100', icon: <AlertTriangle size={20} /> },
                    { label: 'Stock Faible', val: ingredients.filter(i => i.stockActuel > (i.alerteStock || 5) && i.stockActuel <= (i.alerteStock || 5) * 1.5).length, color: 'text-amber-600', bg: 'bg-amber-100', icon: <TrendingDown size={20} /> },
                    { label: 'Mouvements', val: movements.length, color: 'text-indigo-600', bg: 'bg-indigo-100', icon: <History size={20} /> },
                ].map((s, i) => (
                    <div key={i} className="flex items-center gap-4 rounded-[24px] bg-white p-5 shadow-sm border border-slate-100">
                        <div className={`rounded-xl ${s.bg} ${s.color} p-3`}>{s.icon}</div>
                        <div>
                            <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">{s.label}</p>
                            <p className="text-2xl font-black text-slate-800">{s.val}</p>
                        </div>
                    </div>
                ))}
            </div>

            {/* Main Content Grid */}
            <div className="grid grid-cols-1 gap-8 lg:grid-cols-12">
                {/* Inventory Table Section */}
                <div className="lg:col-span-8 space-y-6">
                    <div className="rounded-[32px] bg-white p-6 shadow-sm border border-slate-100 overflow-hidden">
                        <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-center justify-between">
                            <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic">Liste des Produits</h2>
                            <div className="flex gap-3 flex-1 max-w-md">
                                <div className="relative flex-1">
                                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
                                    <input
                                        type="text" placeholder="Rechercher..."
                                        className="w-full rounded-xl border-slate-100 bg-slate-50 py-2.5 pl-9 pr-4 text-xs font-bold text-slate-700 focus:border-indigo-500 focus:ring-0"
                                        value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
                                    />
                                </div>
                                <select
                                    className="rounded-xl border-slate-100 bg-slate-50 px-3 py-2.5 text-xs font-bold text-slate-700 outline-none"
                                    value={filterAlert} onChange={(e) => setFilterAlert(e.target.value)}
                                >
                                    <option value="all">Tous les Statuts</option>
                                    <option value="alert">Critique</option>
                                    <option value="low">Faible</option>
                                </select>
                            </div>
                        </div>

                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead>
                                    <tr className="border-b border-slate-50">
                                        <th className="px-6 py-4 text-left text-[10px] font-black uppercase tracking-widest text-slate-400">Article</th>
                                        <th className="px-6 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400">Quantité Actuelle</th>
                                        <th className="px-6 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400">Seuil Alerte</th>
                                        <th className="px-6 py-4 text-right text-[10px] font-black uppercase tracking-widest text-slate-400">Statut</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-50">
                                    <AnimatePresence>
                                        {filteredIngredients.map(ing => {
                                            const status = getStockStatus(ing);
                                            return (
                                                <motion.tr
                                                    initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                                                    key={ing.id} className="group hover:bg-slate-50 transition-colors"
                                                >
                                                    <td className="px-6 py-5">
                                                        <div>
                                                            <p className="text-sm font-black text-slate-900">{ing.nom}</p>
                                                            <p className="text-[10px] font-bold text-slate-400 uppercase">{ing.categorie || 'Ingrédient'}</p>
                                                        </div>
                                                    </td>
                                                    <td className="px-6 py-5 text-center">
                                                        <span className="text-sm font-black text-slate-800 bg-slate-100 px-3 py-1 rounded-lg">
                                                            {ing.stockActuel} <span className="text-[10px] text-slate-400 ml-1 italic">{ing.unite}</span>
                                                        </span>
                                                    </td>
                                                    <td className="px-6 py-5 text-center">
                                                        <span className="text-xs font-bold text-slate-500">
                                                            {ing.alerteStock || 5} {ing.unite}
                                                        </span>
                                                    </td>
                                                    <td className="px-6 py-5 text-right">
                                                        <span className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-[10px] font-black tracking-widest uppercase ${status.bg} ${status.text}`}>
                                                            <span className={`h-1.5 w-1.5 rounded-full ${status.dot} ${status.label !== 'OPTIMAL' ? 'animate-pulse' : ''}`}></span>
                                                            {status.label}
                                                        </span>
                                                    </td>
                                                </motion.tr>
                                            );
                                        })}
                                    </AnimatePresence>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                {/* Movements Sidebar */}
                <div className="lg:col-span-4 space-y-6">
                    <div className="rounded-[32px] bg-white p-8 shadow-sm border border-slate-100">
                        <div className="mb-6 flex items-center justify-between">
                            <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic">Journal des Flux</h2>
                            <History className="text-indigo-600" size={24} />
                        </div>
                        <div className="space-y-4">
                            {movements.length > 0 ? (
                                movements.map((mov, idx) => (
                                    <div key={idx} className="flex items-center gap-4 rounded-2xl bg-slate-50 p-4 border border-slate-100 group hover:border-indigo-200 transition">
                                        <div className={`rounded-xl p-2 shadow-sm ${mov.type === 'ENTREE' ? 'bg-emerald-100 text-emerald-600' : 'bg-rose-100 text-rose-600'}`}>
                                            {mov.type === 'ENTREE' ? <ArrowUpRight size={20} /> : <ArrowDownRight size={20} />}
                                        </div>
                                        <div className="flex-1 overflow-hidden">
                                            <p className="text-sm font-black text-slate-800 truncate">{mov.ingredient?.nom || 'Article Inconnu'}</p>
                                            <p className="text-[10px] font-bold text-slate-400 uppercase truncate">{mov.motif || 'Aucun motif'}</p>
                                        </div>
                                        <div className="text-right">
                                            <p className={`text-sm font-black ${mov.type === 'ENTREE' ? 'text-emerald-600' : 'text-rose-600'}`}>
                                                {mov.type === 'ENTREE' ? '+' : '-'}{mov.quantite}
                                            </p>
                                            <p className="text-[8px] font-black text-slate-400 uppercase whitespace-nowrap">
                                                {new Date(mov.dateHeure || Date.now()).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' })}
                                            </p>
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <div className="text-center py-8 text-slate-400 font-bold italic">
                                    Aucun mouvement enregistré.
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Proactive Tip Card */}
                    <div className="rounded-[32px] bg-slate-900 p-8 shadow-xl text-white overflow-hidden relative">
                        <div className="relative z-10">
                            <h2 className="text-2xl font-black mb-2 leading-tight">Optimisation IA</h2>
                            <p className="text-slate-400 text-sm font-medium mb-6">Le stock de viandes sera épuisé dans 48h selon vos prévisions de ventes.</p>
                            <button className="w-full rounded-2xl bg-indigo-600 py-4 font-bold text-white hover:bg-indigo-700 transition active:scale-95">
                                Passer Commande
                            </button>
                        </div>
                        <div className="absolute -right-10 -bottom-10 h-40 w-40 rounded-full bg-indigo-500/20 blur-3xl"></div>
                    </div>
                </div>
            </div>

            {/* Modal Form Movement */}
            {showMovementForm && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/40 backdrop-blur-md p-4 overflow-y-auto">
                    <motion.div
                        initial={{ scale: 0.9, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        className="my-auto w-full max-w-lg rounded-[40px] bg-white p-8 shadow-2xl lg:p-12"
                    >
                        <div className="mb-8 flex items-center justify-between">
                            <h3 className="text-3xl font-black text-slate-900">Nouveau Flux</h3>
                            <div className="rounded-2xl bg-indigo-50 p-3 text-indigo-600">
                                <Package size={24} />
                            </div>
                        </div>

                        <form onSubmit={handleCreateMovement} className="space-y-6">
                            <div className="space-y-2">
                                <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Sélection Ingrédient</label>
                                <select
                                    required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                    value={movementForm.ingredientId} onChange={(e) => setMovementForm({ ...movementForm, ingredientId: e.target.value })}
                                >
                                    <option value="">Sélectionner</option>
                                    {ingredients.map(ing => (
                                        <option key={ing.id} value={ing.id}>{ing.nom} ({ing.stockActuel} {ing.unite})</option>
                                    ))}
                                </select>
                            </div>

                            <div className="grid grid-cols-2 gap-6">
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Type d'Opération</label>
                                    <div className="flex bg-slate-50 rounded-[20px] p-2 gap-2 border border-slate-200">
                                        <button
                                            type="button"
                                            onClick={() => setMovementForm({ ...movementForm, type: 'ENTREE' })}
                                            className={`flex-1 rounded-[14px] py-3 text-[10px] font-black uppercase tracking-widest transition ${movementForm.type === 'ENTREE' ? 'bg-emerald-600 text-white shadow-md' : 'text-slate-500'}`}
                                        >
                                            Entrée (+)
                                        </button>
                                        <button
                                            type="button"
                                            onClick={() => setMovementForm({ ...movementForm, type: 'SORTIE' })}
                                            className={`flex-1 rounded-[14px] py-3 text-[10px] font-black uppercase tracking-widest transition ${movementForm.type === 'SORTIE' ? 'bg-rose-500 text-white shadow-md' : 'text-slate-500'}`}
                                        >
                                            Sortie (-)
                                        </button>
                                    </div>
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Quantité</label>
                                    <input
                                        type="number" required step="0.01" className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        placeholder="0.00" value={movementForm.quantite} onChange={(e) => setMovementForm({ ...movementForm, quantite: e.target.value })}
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Motif / Commentaire</label>
                                <textarea
                                    required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                    rows="2" placeholder="Réapprovisionnement hebdomadaire, perte, etc."
                                    value={movementForm.motif} onChange={(e) => setMovementForm({ ...movementForm, motif: e.target.value })}
                                />
                            </div>

                            <div className="flex gap-4 pt-4">
                                <button
                                    type="button"
                                    onClick={() => setShowMovementForm(false)}
                                    className="flex-1 rounded-2xl py-5 font-black uppercase tracking-widest text-slate-500 hover:bg-slate-50 transition"
                                >
                                    Fermer
                                </button>
                                <button type="submit" className="flex-1 rounded-[24px] bg-indigo-600 py-5 font-black uppercase tracking-widest text-white shadow-xl shadow-indigo-100 hover:bg-indigo-700 transition active:scale-95">
                                    Enregistrer
                                </button>
                            </div>
                        </form>
                    </motion.div>
                </div>
            )}
        </div>
    );
};

export default StockManagement;
