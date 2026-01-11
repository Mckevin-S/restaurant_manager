import { useState, useEffect } from 'react';
import {
    Calendar, Clock, Plus, Search, Users,
    CheckCircle, Trash2, Phone, MapPin, ChevronRight,
    XCircle
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { toast } from 'react-hot-toast';
import apiClient from '../../services/apiClient';
import PageHeader from '../../widget/PageHeader';

const ReservationsManagement = () => {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);

    const [formData, setFormData] = useState({
        customerName: '',
        email: '',
        phone: '',
        guests: 2,
        date: new Date().toISOString().split('T')[0],
        time: '',
        notes: ''
    });

    useEffect(() => {
        fetchData();
    }, [selectedDate]);

    const fetchData = async () => {
        try {
            setLoading(true);
            const response = await apiClient.get(`/reservations?date=${selectedDate}`);
            setReservations(response.data);
        } catch (error) {
            console.error('Erreur chargement réservations:', error);
            // On ne met pas d'erreur toast ici car au premier démarrage l'API peut ne pas encore être rechargée
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateStatus = async (id, newStatus) => {
        try {
            await apiClient.patch(`/reservations/${id}/status?status=${newStatus}`);
            toast.success(`Statut mis à jour`);
            fetchData();
        } catch (error) {
            toast.error('Erreur de mise à jour');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Supprimer cette réservation ?')) {
            try {
                await apiClient.delete(`/reservations/${id}`);
                toast.success('Réservation supprimée');
                fetchData();
            } catch (error) {
                toast.error('Erreur de suppression');
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const data = {
                nomClient: formData.customerName,
                email: formData.email,
                telephone: formData.phone,
                nombrePersonnes: parseInt(formData.guests),
                date: formData.date,
                heure: formData.time.length === 5 ? formData.time + ":00" : formData.time,
                notes: formData.notes
            };
            await apiClient.post('/reservations', data);
            setShowForm(false);
            toast.success('Réservation enregistrée');
            fetchData();
            resetForm();
        } catch (error) {
            toast.error('Erreur lors de l\'enregistrement');
        }
    };

    const resetForm = () => {
        setFormData({
            customerName: '',
            email: '',
            phone: '',
            guests: 2,
            date: new Date().toISOString().split('T')[0],
            time: '',
            notes: ''
        });
    };

    const filteredReservations = (reservations || []).filter(res => {
        const matchesSearch = (res.nomClient || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
            (res.telephone || '').includes(searchTerm);
        return matchesSearch;
    });

    const getStatusStyle = (status) => {
        switch (status) {
            case 'CONFIRMEE': return 'bg-emerald-50 text-emerald-600 border-emerald-100';
            case 'ANNULEE': return 'bg-rose-50 text-rose-600 border-rose-100';
            case 'TERMINEE': return 'bg-indigo-50 text-indigo-600 border-indigo-100';
            case 'ABSENT': return 'bg-slate-50 text-slate-500 border-slate-100';
            default: return 'bg-amber-50 text-amber-600 border-amber-100';
        }
    };

    const getStatusLabel = (status) => {
        switch (status) {
            case 'CONFIRMEE': return 'Confirmée';
            case 'ANNULEE': return 'Annulée';
            case 'TERMINEE': return 'Terminée';
            case 'ABSENT': return 'Absent';
            default: return 'En attente';
        }
    };

    if (loading && (!reservations || reservations.length === 0)) return (
        <div className="flex h-[60vh] items-center justify-center">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
        </div>
    );

    return (
        <div className="min-h-screen bg-[#f8fafc] animate-in fade-in duration-500 pb-12">
            <PageHeader
                icon={Calendar}
                title="Réservations"
                subtitle="Gérez l'accueil et la planification de vos clients"
                actions={
                    <button
                        onClick={() => setShowForm(true)}
                        className="flex items-center gap-2 rounded-2xl bg-indigo-600 px-8 py-3 text-sm font-black text-white shadow-lg shadow-indigo-100 transition hover:bg-indigo-700 hover:scale-[1.02] active:scale-95"
                    >
                        <Plus size={18} /> Nouvelle Réservation
                    </button>
                }
            />

            <div className="px-4 grid grid-cols-1 gap-8 lg:grid-cols-12">
                <div className="lg:col-span-4 space-y-6">
                    <div className="rounded-[32px] bg-white p-8 shadow-sm border border-slate-100">
                        <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic mb-6">Sélecteur de Date</h2>
                        <input
                            type="date"
                            value={selectedDate}
                            onChange={(e) => setSelectedDate(e.target.value)}
                            className="w-full rounded-2xl border-slate-100 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 mb-8 shadow-inner"
                        />

                        <div className="space-y-3">
                            {[
                                { label: 'Total Inscriptions', val: (reservations || []).length, color: 'text-slate-600', bg: 'bg-slate-100' },
                                { label: 'Confirmées', val: (reservations || []).filter(r => r.statut === 'CONFIRMEE').length, color: 'text-emerald-600', bg: 'bg-emerald-100' },
                                { label: 'En Attente', val: (reservations || []).filter(r => r.statut === 'EN_ATTENTE').length, color: 'text-amber-600', bg: 'bg-amber-100' },
                            ].map((s, i) => (
                                <div key={i} className={`flex items-center justify-between p-5 rounded-2xl ${s.bg} border border-white shadow-sm transition hover:scale-[1.02]`}>
                                    <span className={`text-[10px] font-black uppercase tracking-widest ${s.color}`}>{s.label}</span>
                                    <span className={`text-2xl font-black ${s.color}`}>{s.val}</span>
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="rounded-[32px] bg-slate-900 p-8 text-white relative overflow-hidden group">
                        <div className="relative z-10">
                            <div className="h-12 w-12 rounded-2xl bg-indigo-500/20 flex items-center justify-center text-indigo-400 mb-6">
                                <Users size={24} />
                            </div>
                            <h3 className="text-xl font-black mb-2 leading-tight uppercase italic">Accueil Client</h3>
                            <p className="text-slate-400 text-sm font-medium mb-6">Consultez les préférences et notes de vos clients pour une expérience personnalisée.</p>
                            <button className="text-indigo-400 text-xs font-black uppercase tracking-widest flex items-center gap-2 group-hover:gap-3 transition-all">
                                Guide Service Salle <ChevronRight size={14} />
                            </button>
                        </div>
                        <div className="absolute -right-8 -bottom-8 h-32 w-32 rounded-full bg-indigo-500/10 blur-2xl group-hover:bg-indigo-500/20 transition-all"></div>
                    </div>
                </div>

                <div className="lg:col-span-8 space-y-6">
                    <div className="rounded-[32px] bg-white p-6 shadow-sm border border-slate-100 sm:p-8">
                        <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center">
                            <div className="relative flex-1">
                                <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                                <input
                                    type="text" placeholder="Rechercher par nom ou téléphone..."
                                    className="w-full rounded-[20px] border-slate-100 bg-slate-50 py-4 pl-12 pr-4 text-sm font-bold text-slate-700 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                    value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
                                />
                            </div>
                        </div>

                        <div className="overflow-x-auto overflow-y-visible">
                            <table className="w-full">
                                <thead>
                                    <tr className="border-b border-slate-50">
                                        <th className="px-4 py-4 text-left text-[10px] font-black uppercase tracking-widest text-slate-400">Heure</th>
                                        <th className="px-4 py-4 text-left text-[10px] font-black uppercase tracking-widest text-slate-400">Client</th>
                                        <th className="px-4 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400">Couverts</th>
                                        <th className="px-4 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400">Table</th>
                                        <th className="px-4 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400">Statut</th>
                                        <th className="px-4 py-4 text-right text-[10px] font-black uppercase tracking-widest text-slate-400">Actions</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-50">
                                    <AnimatePresence mode="popLayout">
                                        {filteredReservations.length > 0 ? (
                                            filteredReservations.sort((a, b) => (a.heure || '').localeCompare(b.heure || '')).map(res => (
                                                <motion.tr
                                                    layout initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                                                    key={res.id} className="group hover:bg-slate-50/50 transition-colors"
                                                >
                                                    <td className="px-4 py-6 whitespace-nowrap">
                                                        <span className="flex items-center gap-1.5 text-xs font-black text-slate-900 bg-slate-100 px-3 py-1.5 rounded-xl border border-white shadow-sm">
                                                            <Clock size={14} className="text-indigo-600" /> {(res.heure || '').slice(0, 5)}
                                                        </span>
                                                    </td>
                                                    <td className="px-4 py-6">
                                                        <div>
                                                            <p className="text-sm font-black text-slate-900 line-clamp-1">{res.nomClient}</p>
                                                            <div className="flex gap-3 text-[9px] font-bold text-slate-400 uppercase mt-1">
                                                                <span className="flex items-center gap-1"><Phone size={10} /> {res.telephone}</span>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td className="px-4 py-6 text-center">
                                                        <div className="flex flex-col items-center">
                                                            <span className="text-sm font-black text-slate-800">{res.nombrePersonnes}</span>
                                                            <span className="text-[9px] font-bold text-slate-400 uppercase tracking-tight">Pers.</span>
                                                        </div>
                                                    </td>
                                                    <td className="px-4 py-6 text-center">
                                                        {res.numeroTable ? (
                                                            <span className="flex items-center justify-center gap-1 text-[10px] font-black text-indigo-600 bg-indigo-50 border border-indigo-100 rounded-lg px-2 py-1">
                                                                <MapPin size={10} /> {res.numeroTable}
                                                            </span>
                                                        ) : (
                                                            <span className="text-[9px] font-black text-slate-300 italic uppercase">Non assigné</span>
                                                        )}
                                                    </td>
                                                    <td className="px-4 py-6 text-center">
                                                        <span className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1.5 text-[10px] font-black tracking-widest uppercase border ${getStatusStyle(res.statut)}`}>
                                                            {getStatusLabel(res.statut)}
                                                        </span>
                                                    </td>
                                                    <td className="px-4 py-6 text-right whitespace-nowrap">
                                                        <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition duration-200">
                                                            {res.statut === 'EN_ATTENTE' && (
                                                                <button
                                                                    onClick={() => handleUpdateStatus(res.id, 'CONFIRMEE')}
                                                                    title="Confirmer"
                                                                    className="p-2.5 text-emerald-600 bg-emerald-50 hover:bg-emerald-600 hover:text-white rounded-xl transition shadow-sm"
                                                                >
                                                                    <CheckCircle size={18} />
                                                                </button>
                                                            )}
                                                            {res.statut !== 'ANNULEE' && (
                                                                <button
                                                                    onClick={() => handleUpdateStatus(res.id, 'ANNULEE')}
                                                                    title="Annuler"
                                                                    className="p-2.5 text-amber-600 bg-amber-50 hover:bg-amber-600 hover:text-white rounded-xl transition shadow-sm"
                                                                >
                                                                    <XCircle size={18} />
                                                                </button>
                                                            )}
                                                            <button
                                                                onClick={() => handleDelete(res.id)}
                                                                title="Supprimer"
                                                                className="p-2.5 text-rose-500 bg-rose-50 hover:bg-rose-600 hover:text-white rounded-xl transition shadow-sm"
                                                            >
                                                                <Trash2 size={18} />
                                                            </button>
                                                        </div>
                                                    </td>
                                                </motion.tr>
                                            ))
                                        ) : (
                                            <tr>
                                                <td colSpan="6" className="py-24 text-center">
                                                    <div className="flex flex-col items-center opacity-30">
                                                        <Calendar size={64} className="text-indigo-600 mb-4" />
                                                        <p className="text-lg font-black uppercase tracking-widest italic text-slate-400">Aucune réservation trouvée</p>
                                                    </div>
                                                </td>
                                            </tr>
                                        )}
                                    </AnimatePresence>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            {showForm && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/60 backdrop-blur-md p-4 overflow-y-auto">
                    <motion.div
                        initial={{ scale: 0.9, opacity: 0, y: 20 }}
                        animate={{ scale: 1, opacity: 1, y: 0 }}
                        className="my-auto w-full max-w-xl rounded-[40px] bg-white p-8 shadow-2xl lg:p-12 border border-white"
                    >
                        <h3 className="text-3xl font-black text-slate-900 mb-8 uppercase tracking-tight italic">Nouveau Client Privé</h3>
                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div className="space-y-2">
                                <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Nom Complet</label>
                                <input
                                    required className="w-full rounded-[20px] border-slate-100 bg-slate-50 p-5 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                    value={formData.customerName} onChange={e => setFormData({ ...formData, customerName: e.target.value })}
                                />
                            </div>

                            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                                <div className="space-y-2">
                                    <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Téléphone</label>
                                    <input
                                        required className="w-full rounded-[20px] border-slate-100 bg-slate-50 p-5 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                        value={formData.phone} onChange={e => setFormData({ ...formData, phone: e.target.value })}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Email</label>
                                    <input
                                        type="email" className="w-full rounded-[20px] border-slate-100 bg-slate-50 p-5 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                        value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Nombre de Couverts</label>
                                    <input
                                        type="number" required min="1" className="w-full rounded-[20px] border-slate-100 bg-slate-50 p-5 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                        value={formData.guests} onChange={e => setFormData({ ...formData, guests: e.target.value })}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Heure de Réservation</label>
                                    <input
                                        type="time" required className="w-full rounded-[20px] border-slate-100 bg-slate-50 p-5 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                        value={formData.time} onChange={e => setFormData({ ...formData, time: e.target.value })}
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Notes & Préférences</label>
                                <textarea
                                    className="w-full rounded-[20px] border-slate-100 bg-slate-50 p-5 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                    rows="3" value={formData.notes} onChange={e => setFormData({ ...formData, notes: e.target.value })}
                                />
                            </div>

                            <div className="flex gap-4 pt-4">
                                <button type="button" onClick={() => setShowForm(false)} className="flex-1 rounded-2xl py-5 font-black uppercase tracking-widest text-slate-400 hover:bg-slate-50 transition">Annuler</button>
                                <button type="submit" className="flex-1 rounded-[24px] bg-indigo-600 py-5 font-black uppercase tracking-widest text-white shadow-xl shadow-indigo-100 hover:bg-indigo-700 transition active:scale-95">
                                    Confirmer la réservation
                                </button>
                            </div>
                        </form>
                    </motion.div>
                </div>
            )}
        </div>
    );
};

export default ReservationsManagement;
