import { useState, useEffect } from 'react';
import {
    Calendar, Clock, Plus, Search, Users,
    CheckCircle, Trash2, Mail, Phone, MapPin, ChevronRight, Filter
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { toast } from 'react-hot-toast';
import apiClient from '../../services/apiClient';

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
    }, []);

    const fetchData = async () => {
        try {
            // Mock data for now until backend is ready
            const mockRes = [
                { id: 1, customerName: 'Jean Dupont', phone: '0123456789', guests: 4, date: new Date().toISOString().split('T')[0], time: '19:30', status: 'CONFIRMED', table: 'T12' },
                { id: 2, customerName: 'Marie Martin', phone: '0987654321', guests: 2, date: new Date().toISOString().split('T')[0], time: '20:00', status: 'PENDING', table: null }
            ];
            setReservations(mockRes);
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateStatus = (id, newStatus) => {
        setReservations(reservations.map(res =>
            res.id === id ? { ...res, status: newStatus } : res
        ));
        toast.success(`Statut : ${newStatus}`);
    };

    const handleDelete = (id) => {
        if (window.confirm('Supprimer cette réservation ?')) {
            setReservations(reservations.filter(res => res.id !== id));
            toast.success('Réservation supprimée');
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const newReservation = {
            id: Date.now(),
            ...formData,
            status: 'PENDING',
            table: null
        };
        setReservations([...reservations, newReservation]);
        setShowForm(false);
        toast.success('Réservation ajoutée');
    };

    const filteredReservations = reservations.filter(res => {
        const matchesDate = res.date === selectedDate;
        const matchesSearch = (res.customerName || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
            (res.phone || '').includes(searchTerm);
        return matchesDate && matchesSearch;
    });

    if (loading) return (
        <div className="flex h-screen w-full items-center justify-center">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
        </div>
    );

    return (
        <div className="min-h-screen bg-[#f8fafc] animate-in fade-in duration-500 px-4 py-8">
            {/* Header Section */}
            <div className="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end">
                <div>
                    <h1 className="text-4xl font-black tracking-tight text-slate-900 flex items-center gap-3">
                        <Calendar className="text-indigo-600" size={36} />
                        Réservations
                    </h1>
                    <p className="mt-1 text-slate-500 font-medium">Planifiez l'accueil de vos clients VIP.</p>
                </div>
                <div className="flex items-center gap-3">
                    <button
                        onClick={() => setShowForm(true)}
                        className="flex items-center gap-2 rounded-2xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white shadow-lg shadow-indigo-100 transition hover:bg-indigo-700 hover:scale-[1.02] active:scale-95"
                    >
                        <Plus size={18} /> Nouvelle Réservation
                    </button>
                </div>
            </div>

            <div className="grid grid-cols-1 gap-8 lg:grid-cols-12">
                {/* Left: Day Selector & Stats */}
                <div className="lg:col-span-4 space-y-6">
                    <div className="rounded-[32px] bg-white p-8 shadow-sm border border-slate-100">
                        <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic mb-6">Sélecteur de Date</h2>
                        <input
                            type="date"
                            value={selectedDate}
                            onChange={(e) => setSelectedDate(e.target.value)}
                            className="w-full rounded-2xl border-slate-100 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 mb-6"
                        />

                        <div className="space-y-3">
                            {[
                                { label: 'Total Inscriptions', val: filteredReservations.length, color: 'text-slate-600', bg: 'bg-slate-100' },
                                { label: 'Confirmées', val: filteredReservations.filter(r => r.status === 'CONFIRMED').length, color: 'text-emerald-600', bg: 'bg-emerald-100' },
                                { label: 'En Attente', val: filteredReservations.filter(r => r.status === 'PENDING').length, color: 'text-amber-600', bg: 'bg-amber-100' },
                            ].map((s, i) => (
                                <div key={i} className={`flex items-center justify-between p-4 rounded-2xl ${s.bg} border border-white`}>
                                    <span className={`text-[10px] font-black uppercase tracking-widest ${s.color}`}>{s.label}</span>
                                    <span className={`text-xl font-black ${s.color}`}>{s.val}</span>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Proactive Help */}
                    <div className="rounded-[32px] bg-slate-900 p-8 text-white">
                        <div className="h-12 w-12 rounded-2xl bg-indigo-500/20 flex items-center justify-center text-indigo-400 mb-4">
                            <Users size={24} />
                        </div>
                        <h3 className="text-xl font-black mb-2 leading-tight">Optimisation Salles</h3>
                        <p className="text-slate-400 text-sm font-medium mb-4">Attribuez les tables stratégiquement pour maximiser la capacité de rotation.</p>
                        <button className="text-indigo-400 text-xs font-black uppercase tracking-widest flex items-center gap-2 hover:gap-3 transition-all">
                            Voir Plan de Salle <ChevronRight size={14} />
                        </button>
                    </div>
                </div>

                {/* Right: Reservations List */}
                <div className="lg:col-span-8 space-y-6">
                    <div className="rounded-[32px] bg-white p-6 shadow-sm border border-slate-100 sm:p-8">
                        <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center">
                            <div className="relative flex-1">
                                <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                                <input
                                    type="text" placeholder="Rechercher client ou téléphone..."
                                    className="w-full rounded-[20px] border-slate-100 bg-slate-50 py-3.5 pl-12 pr-4 text-sm font-bold text-slate-700 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                    value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
                                />
                            </div>
                        </div>

                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead>
                                    <tr className="border-b border-slate-50">
                                        <th className="px-4 py-4 text-left text-[10px] font-black uppercase tracking-widest text-slate-400">Heure</th>
                                        <th className="px-4 py-4 text-left text-[10px] font-black uppercase tracking-widest text-slate-400">Client</th>
                                        <th className="px-4 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400">Couverts</th>
                                        <th className="px-4 py-4 text-center text-[10px] font-black uppercase tracking-widest text-slate-400">Table</th>
                                        <th className="px-4 py-4 text-right text-[10px] font-black uppercase tracking-widest text-slate-400">Statut</th>
                                        <th className="px-4 py-4 text-right text-[10px] font-black uppercase tracking-widest text-slate-400">Actions</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-50">
                                    <AnimatePresence mode="popLayout">
                                        {filteredReservations.length > 0 ? (
                                            filteredReservations.sort((a, b) => a.time.localeCompare(b.time)).map(res => (
                                                <motion.tr
                                                    layout initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                                                    key={res.id} className="group hover:bg-slate-50 transition-colors"
                                                >
                                                    <td className="px-4 py-5 whitespace-nowrap">
                                                        <span className="flex items-center gap-1.5 text-sm font-black text-slate-900 bg-slate-100 px-3 py-1 rounded-lg">
                                                            <Clock size={14} className="text-indigo-600" /> {res.time}
                                                        </span>
                                                    </td>
                                                    <td className="px-4 py-5">
                                                        <div>
                                                            <p className="text-sm font-black text-slate-900 line-clamp-1">{res.customerName}</p>
                                                            <div className="flex gap-2 text-[8px] font-bold text-slate-400 uppercase mt-0.5">
                                                                <span className="flex items-center gap-0.5"><Phone size={8} /> {res.phone}</span>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td className="px-4 py-5 text-center">
                                                        <div className="flex flex-col items-center">
                                                            <span className="text-sm font-black text-slate-800">{res.guests}</span>
                                                            <span className="text-[8px] font-black text-slate-400 uppercase tracking-tighter">Personnes</span>
                                                        </div>
                                                    </td>
                                                    <td className="px-4 py-5 text-center">
                                                        {res.table ? (
                                                            <span className="flex items-center justify-center gap-1 text-[10px] font-black text-indigo-600 bg-indigo-50 border border-indigo-100 rounded-lg px-2 py-1">
                                                                <MapPin size={10} /> {res.table}
                                                            </span>
                                                        ) : (
                                                            <span className="text-[9px] font-black text-slate-300 italic uppercase">Non assigné</span>
                                                        )}
                                                    </td>
                                                    <td className="px-4 py-5 text-right">
                                                        <span className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-[9px] font-black tracking-widest uppercase ${res.status === 'CONFIRMED' ? 'bg-emerald-50 text-emerald-600' :
                                                                res.status === 'PENDING' ? 'bg-amber-50 text-amber-600' : 'bg-rose-50 text-rose-600'
                                                            }`}>
                                                            {res.status === 'CONFIRMED' ? 'Confirmé' : res.status === 'PENDING' ? 'Attente' : 'Annulé'}
                                                        </span>
                                                    </td>
                                                    <td className="px-4 py-5 text-right whitespace-nowrap">
                                                        <div className="flex items-center justify-end gap-1 opacity-0 group-hover:opacity-100 transition">
                                                            {res.status === 'PENDING' && (
                                                                <button onClick={() => handleUpdateStatus(res.id, 'CONFIRMED')} className="p-2 text-emerald-600 hover:bg-emerald-50 rounded-xl transition">
                                                                    <CheckCircle size={18} />
                                                                </button>
                                                            )}
                                                            <button onClick={() => handleDelete(res.id)} className="p-2 text-rose-500 hover:bg-rose-50 rounded-xl transition">
                                                                <Trash2 size={18} />
                                                            </button>
                                                        </div>
                                                    </td>
                                                </motion.tr>
                                            ))
                                        ) : (
                                            <tr>
                                                <td colSpan="6" className="py-20 text-center">
                                                    <div className="flex flex-col items-center opacity-20">
                                                        <Calendar size={64} className="text-slate-900 mb-4" />
                                                        <p className="text-lg font-black uppercase tracking-widest italic">Aucun Client attendu</p>
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

            {/* Modal Form */}
            {showForm && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/40 backdrop-blur-md p-4 overflow-y-auto">
                    <motion.div
                        initial={{ scale: 0.9, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        className="my-auto w-full max-w-xl rounded-[40px] bg-white p-8 shadow-2xl lg:p-12"
                    >
                        <h3 className="text-3xl font-black text-slate-900 mb-8 uppercase tracking-tight italic">Nouveau Client VIP</h3>
                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div className="space-y-2">
                                <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Nom du Client</label>
                                <input
                                    required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                    value={formData.customerName} onChange={e => setFormData({ ...formData, customerName: e.target.value })}
                                />
                            </div>

                            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Téléphone</label>
                                    <input
                                        required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={formData.phone} onChange={e => setFormData({ ...formData, phone: e.target.value })}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Couverts</label>
                                    <input
                                        type="number" required min="1" className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={formData.guests} onChange={e => setFormData({ ...formData, guests: e.target.value })}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Date</label>
                                    <input
                                        type="date" required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={formData.date} onChange={e => setFormData({ ...formData, date: e.target.value })}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Heure</label>
                                    <input
                                        type="time" required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={formData.time} onChange={e => setFormData({ ...formData, time: e.target.value })}
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Notes Particulières</label>
                                <textarea
                                    className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                    rows="2" value={formData.notes} onChange={e => setFormData({ ...formData, notes: e.target.value })}
                                />
                            </div>

                            <div className="flex gap-4 pt-4">
                                <button type="button" onClick={() => setShowForm(false)} className="flex-1 rounded-2xl py-5 font-black uppercase tracking-widest text-slate-500 hover:bg-slate-50 transition">Fermer</button>
                                <button type="submit" className="flex-1 rounded-[24px] bg-indigo-600 py-5 font-black uppercase tracking-widest text-white shadow-xl shadow-indigo-100 hover:bg-indigo-700 transition active:scale-95">
                                    Confirmer
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
