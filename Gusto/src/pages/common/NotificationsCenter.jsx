import { useState, useEffect } from 'react';
import { Bell, Check, X, Filter } from 'lucide-react';
import axios from 'axios';
import { toast } from 'react-hot-toast';

const NotificationsCenter = () => {
    const [notifications, setNotifications] = useState([]);
    const [filter, setFilter] = useState('all');
    const [loading, setLoading] = useState(true);

    const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:3006/api';

    useEffect(() => {
        fetchNotifications();

        // WebSocket pour temps réel
        const ws = new WebSocket('ws://localhost:3006/ws/notifications');

        ws.onmessage = (event) => {
            const notification = JSON.parse(event.data);
            setNotifications(prev => [notification, ...prev]);
            toast.success(notification.message);
        };

        return () => ws.close();
    }, []);

    const fetchNotifications = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`${API_URL}/notifications`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setNotifications(response.data);
        } catch (error) {
            console.error('Erreur notifications:', error);
        } finally {
            setLoading(false);
        }
    };

    const markAsRead = async (id) => {
        try {
            const token = localStorage.getItem('token');
            await axios.patch(`${API_URL}/notifications/${id}/read`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setNotifications(prev => prev.map(n => n.id === id ? { ...n, lu: true } : n));
        } catch (error) {
            toast.error('Erreur');
        }
    };

    const markAllAsRead = async () => {
        try {
            const token = localStorage.getItem('token');
            await axios.patch(`${API_URL}/notifications/read-all`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setNotifications(prev => prev.map(n => ({ ...n, lu: true })));
            toast.success('Toutes les notifications marquées comme lues');
        } catch (error) {
            toast.error('Erreur');
        }
    };

    const deleteNotification = async (id) => {
        try {
            const token = localStorage.getItem('token');
            await axios.delete(`${API_URL}/notifications/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setNotifications(prev => prev.filter(n => n.id !== id));
            toast.success('Notification supprimée');
        } catch (error) {
            toast.error('Erreur');
        }
    };

    const getNotificationIcon = (type) => {
        const icons = {
            commande: '🍽️',
            stock: '📦',
            paiement: '💰',
            alerte: '⚠️',
            info: 'ℹ️'
        };
        return icons[type] || '📢';
    };

    const getNotificationColor = (type) => {
        const colors = {
            commande: 'bg-blue-100 border-blue-300',
            stock: 'bg-yellow-100 border-yellow-300',
            paiement: 'bg-green-100 border-green-300',
            alerte: 'bg-red-100 border-red-300',
            info: 'bg-gray-100 border-gray-300'
        };
        return colors[type] || 'bg-gray-100 border-gray-300';
    };

    const filteredNotifications = notifications.filter(n => {
        if (filter === 'all') return true;
        if (filter === 'unread') return !n.lu;
        return n.type === filter;
    });

    const unreadCount = notifications.filter(n => !n.lu).length;

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>;
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-4xl mx-auto">
                <div className="mb-8 flex items-center justify-between">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-900 mb-2 flex items-center gap-3">
                            <Bell size={32} />
                            Notifications
                            {unreadCount > 0 && (
                                <span className="bg-red-600 text-white text-sm px-3 py-1 rounded-full">
                                    {unreadCount}
                                </span>
                            )}
                        </h1>
                        <p className="text-gray-600">Restez informé de l'activité de votre restaurant</p>
                    </div>

                    {unreadCount > 0 && (
                        <button
                            onClick={markAllAsRead}
                            className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700"
                        >
                            <Check size={20} />
                            Tout marquer comme lu
                        </button>
                    )}
                </div>

                {/* Filtres */}
                <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
                    <div className="flex items-center gap-2 flex-wrap">
                        <Filter size={20} className="text-gray-600" />
                        <button
                            onClick={() => setFilter('all')}
                            className={`px-4 py-2 rounded-lg transition ${filter === 'all' ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                }`}
                        >
                            Toutes ({notifications.length})
                        </button>
                        <button
                            onClick={() => setFilter('unread')}
                            className={`px-4 py-2 rounded-lg transition ${filter === 'unread' ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                }`}
                        >
                            Non lues ({unreadCount})
                        </button>
                        <button
                            onClick={() => setFilter('commande')}
                            className={`px-4 py-2 rounded-lg transition ${filter === 'commande' ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                }`}
                        >
                            🍽️ Commandes
                        </button>
                        <button
                            onClick={() => setFilter('stock')}
                            className={`px-4 py-2 rounded-lg transition ${filter === 'stock' ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                }`}
                        >
                            📦 Stock
                        </button>
                        <button
                            onClick={() => setFilter('paiement')}
                            className={`px-4 py-2 rounded-lg transition ${filter === 'paiement' ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                }`}
                        >
                            💰 Paiements
                        </button>
                    </div>
                </div>

                {/* Liste des notifications */}
                <div className="space-y-3">
                    {filteredNotifications.length === 0 ? (
                        <div className="bg-white rounded-lg shadow-sm p-12 text-center">
                            <Bell size={48} className="mx-auto text-gray-300 mb-4" />
                            <p className="text-gray-500">Aucune notification</p>
                        </div>
                    ) : (
                        filteredNotifications.map(notif => (
                            <div
                                key={notif.id}
                                className={`bg-white rounded-lg shadow-sm border-l-4 p-4 transition hover:shadow-md ${getNotificationColor(notif.type)
                                    } ${!notif.lu ? 'bg-opacity-50' : ''}`}
                            >
                                <div className="flex items-start justify-between">
                                    <div className="flex items-start gap-3 flex-1">
                                        <div className="text-2xl">{getNotificationIcon(notif.type)}</div>
                                        <div className="flex-1">
                                            <div className="flex items-center gap-2 mb-1">
                                                <h3 className="font-bold text-gray-900">{notif.titre}</h3>
                                                {!notif.lu && (
                                                    <span className="bg-indigo-600 text-white text-xs px-2 py-0.5 rounded-full">
                                                        Nouveau
                                                    </span>
                                                )}
                                            </div>
                                            <p className="text-gray-700 mb-2">{notif.message}</p>
                                            <p className="text-xs text-gray-500">
                                                {new Date(notif.dateHeure).toLocaleString('fr-FR')}
                                            </p>
                                        </div>
                                    </div>

                                    <div className="flex gap-2">
                                        {!notif.lu && (
                                            <button
                                                onClick={() => markAsRead(notif.id)}
                                                className="p-2 text-green-600 hover:bg-green-50 rounded-lg transition"
                                                title="Marquer comme lu"
                                            >
                                                <Check size={20} />
                                            </button>
                                        )}
                                        <button
                                            onClick={() => deleteNotification(notif.id)}
                                            className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition"
                                            title="Supprimer"
                                        >
                                            <X size={20} />
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default NotificationsCenter;
