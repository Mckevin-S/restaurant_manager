import { useState, useEffect } from 'react';
import { Calendar, Clock, Plus, Search, User, Users, Phone, Mail, CheckCircle, XCircle, Trash2, Edit2, Ban } from 'lucide-react';
import { toast } from 'react-hot-toast';

const ReservationsManagement = () => {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);

    // Mock data
    const mockReservations = [
        {
            id: 1,
            customerName: 'Jean Dupont',
            email: 'jean.dupont@email.com',
            phone: '0123456789',
            guests: 4,
            date: new Date().toISOString().split('T')[0],
            time: '19:30',
            status: 'CONFIRMED', // CONFIRMED, PENDING, CANCELLED, NOSHOW
            table: 'T12',
            notes: 'Anniversaire'
        },
        {
            id: 2,
            customerName: 'Marie Martin',
            email: 'marie.m@email.com',
            phone: '0987654321',
            guests: 2,
            date: new Date().toISOString().split('T')[0],
            time: '20:00',
            status: 'PENDING',
            table: null,
            notes: ''
        }
    ];

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
        // Simulate fetch
        setTimeout(() => {
            setReservations(mockReservations);
            setLoading(false);
        }, 500);
    }, []);

    const handleUpdateStatus = (id, newStatus) => {
        // NOTE: Le backend n'a pas encore de endpoint Réservation.
        // Simulons la mise à jour locale.
        setReservations(reservations.map(res =>
            res.id === id ? { ...res, status: newStatus } : res
        ));
        toast.success(`Statut mis à jour : ${newStatus}`);
    };

    const handleDelete = (id) => {
        if (confirm('Supprimer cette réservation ?')) {
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
        setFormData({
            customerName: '',
            email: '',
            phone: '',
            guests: 2,
            date: selectedDate,
            time: '',
            notes: ''
        });
    };

    const filteredReservations = reservations.filter(res => {
        const matchesDate = res.date === selectedDate;
        const matchesSearch = res.customerName.toLowerCase().includes(searchTerm.toLowerCase()) ||
            res.phone.includes(searchTerm);
        return matchesDate && matchesSearch;
    });

    const getStatusBadge = (status) => {
        switch (status) {
            case 'CONFIRMED': return <span className="bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs font-semibold">Confirmée</span>;
            case 'PENDING': return <span className="bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full text-xs font-semibold">En attente</span>;
            case 'CANCELLED': return <span className="bg-red-100 text-red-800 px-2 py-1 rounded-full text-xs font-semibold">Annulée</span>;
            case 'NOSHOW': return <span className="bg-gray-100 text-gray-800 px-2 py-1 rounded-full text-xs font-semibold">No-Show</span>;
            default: return null;
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Gestion des Réservations</h1>
                <p className="text-gray-600">Suivi des réservations et attributions de tables</p>
            </div>

            <div className="flex flex-col md:flex-row gap-6">
                {/* Sidebar Calendrier (Simplified) */}
                <div className="md:w-1/4">
                    <div className="bg-white rounded-lg shadow-sm p-4">
                        <h3 className="font-bold text-lg mb-4 flex items-center gap-2">
                            <Calendar size={20} />
                            Date
                        </h3>
                        <input
                            type="date"
                            value={selectedDate}
                            onChange={(e) => setSelectedDate(e.target.value)}
                            className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 mb-4"
                        />

                        <div className="space-y-2">
                            <div className="text-sm font-medium text-gray-500">Statistiques du jour</div>
                            <div className="flex justify-between items-center bg-indigo-50 p-2 rounded">
                                <span>Total</span>
                                <span className="font-bold text-indigo-700">{filteredReservations.length}</span>
                            </div>
                            <div className="flex justify-between items-center bg-green-50 p-2 rounded">
                                <span>Confirmées</span>
                                <span className="font-bold text-green-700">
                                    {filteredReservations.filter(r => r.status === 'CONFIRMED').length}
                                </span>
                            </div>
                            <div className="flex justify-between items-center bg-yellow-50 p-2 rounded">
                                <span>En attente</span>
                                <span className="font-bold text-yellow-700">
                                    {filteredReservations.filter(r => r.status === 'PENDING').length}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Main Content */}
                <div className="md:w-3/4">
                    <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
                        <div className="flex justify-between items-center mb-4">
                            <div className="relative flex-1 max-w-md">
                                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                                <input
                                    type="text"
                                    placeholder="Rechercher client ou téléphone..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>
                            <button
                                onClick={() => setShowForm(true)}
                                className="ml-4 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition flex items-center gap-2"
                            >
                                <Plus size={20} />
                                Nouvelle Réservation
                            </button>
                        </div>

                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 border-b">
                                    <tr>
                                        <th className="px-4 py-3 text-sm font-semibold text-gray-600">Heure</th>
                                        <th className="px-4 py-3 text-sm font-semibold text-gray-600">Client</th>
                                        <th className="px-4 py-3 text-sm font-semibold text-gray-600">Couverts</th>
                                        <th className="px-4 py-3 text-sm font-semibold text-gray-600">Table</th>
                                        <th className="px-4 py-3 text-sm font-semibold text-gray-600">Statut</th>
                                        <th className="px-4 py-3 text-sm font-semibold text-gray-600">Actions</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {filteredReservations.length > 0 ? (
                                        filteredReservations.sort((a, b) => a.time.localeCompare(b.time)).map(res => (
                                            <tr key={res.id} className="hover:bg-gray-50 transition">
                                                <td className="px-4 py-3 font-medium text-gray-900">
                                                    <div className="flex items-center gap-1">
                                                        <Clock size={16} className="text-gray-400" />
                                                        {res.time}
                                                    </div>
                                                </td>
                                                <td className="px-4 py-3">
                                                    <div className="font-medium text-gray-900">{res.customerName}</div>
                                                    <div className="text-sm text-gray-500 flex flex-col">
                                                        <span className="flex items-center gap-1"><Phone size={12} /> {res.phone}</span>
                                                        {res.email && <span className="flex items-center gap-1"><Mail size={12} /> {res.email}</span>}
                                                    </div>
                                                </td>
                                                <td className="px-4 py-3">
                                                    <div className="flex items-center gap-1 text-gray-600">
                                                        <Users size={16} />
                                                        {res.guests}
                                                    </div>
                                                </td>
                                                <td className="px-4 py-3">
                                                    {res.table ? (
                                                        <span className="bg-indigo-100 text-indigo-700 px-2 py-1 rounded text-sm font-medium">
                                                            {res.table}
                                                        </span>
                                                    ) : (
                                                        <span className="text-gray-400 text-sm italic">Non attribuée</span>
                                                    )}
                                                </td>
                                                <td className="px-4 py-3">
                                                    {getStatusBadge(res.status)}
                                                </td>
                                                <td className="px-4 py-3">
                                                    <div className="flex items-center gap-2">
                                                        {res.status === 'PENDING' && (
                                                            <>
                                                                <button
                                                                    onClick={() => handleUpdateStatus(res.id, 'CONFIRMED')}
                                                                    className="p-1 text-green-600 hover:bg-green-50 rounded" title="Confirmer"
                                                                >
                                                                    <CheckCircle size={18} />
                                                                </button>
                                                                <button
                                                                    onClick={() => handleUpdateStatus(res.id, 'CANCELLED')}
                                                                    className="p-1 text-red-600 hover:bg-red-50 rounded" title="Annuler"
                                                                >
                                                                    <XCircle size={18} />
                                                                </button>
                                                            </>
                                                        )}
                                                        {res.status === 'CONFIRMED' && (
                                                            <button
                                                                onClick={() => handleUpdateStatus(res.id, 'NOSHOW')}
                                                                className="p-1 text-gray-600 hover:bg-gray-50 rounded" title="Marquer No-Show"
                                                            >
                                                                <Ban size={18} />
                                                            </button>
                                                        )}
                                                        <button
                                                            onClick={() => handleDelete(res.id)}
                                                            className="p-1 text-gray-400 hover:text-red-500 rounded" title="Supprimer"
                                                        >
                                                            <Trash2 size={18} />
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        ))
                                    ) : (
                                        <tr>
                                            <td colSpan="6" className="px-4 py-12 text-center text-gray-500">
                                                Aucune réservation pour cette date
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            {/* Modal de création */}
            {showForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg max-w-md w-full p-6">
                        <h3 className="text-xl font-bold mb-4">Nouvelle Réservation</h3>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Client</label>
                                <div className="flex items-center border rounded-lg overflow-hidden focus-within:ring-2 focus-within:ring-indigo-500">
                                    <div className="bg-gray-50 p-2 border-r"><User size={20} className="text-gray-500" /></div>
                                    <input
                                        type="text"
                                        required
                                        className="w-full px-3 py-2 outline-none"
                                        placeholder="Nom complet"
                                        value={formData.customerName}
                                        onChange={e => setFormData({ ...formData, customerName: e.target.value })}
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Téléphone</label>
                                    <input
                                        type="tel"
                                        required
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                        placeholder="06..."
                                        value={formData.phone}
                                        onChange={e => setFormData({ ...formData, phone: e.target.value })}
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Couverts</label>
                                    <input
                                        type="number"
                                        min="1"
                                        required
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                        value={formData.guests}
                                        onChange={e => setFormData({ ...formData, guests: e.target.value })}
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Date</label>
                                    <input
                                        type="date"
                                        required
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                        value={formData.date}
                                        onChange={e => setFormData({ ...formData, date: e.target.value })}
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Heure</label>
                                    <input
                                        type="time"
                                        required
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                        value={formData.time}
                                        onChange={e => setFormData({ ...formData, time: e.target.value })}
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Email (Optionnel)</label>
                                <input
                                    type="email"
                                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    placeholder="client@email.com"
                                    value={formData.email}
                                    onChange={e => setFormData({ ...formData, email: e.target.value })}
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Notes</label>
                                <textarea
                                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                                    placeholder="Allergies, anniversaire, etc."
                                    rows="2"
                                    value={formData.notes}
                                    onChange={e => setFormData({ ...formData, notes: e.target.value })}
                                />
                            </div>

                            <div className="flex gap-3 pt-4">
                                <button
                                    type="button"
                                    onClick={() => setShowForm(false)}
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

export default ReservationsManagement;
