import { useState, useEffect } from 'react';
import { Settings, Save, Upload, Clock, Phone, Mail, MapPin } from 'lucide-react';
import axios from 'axios';
import { toast } from 'react-hot-toast';

const RestaurantSettings = () => {
    const [settings, setSettings] = useState({
        nom: '',
        adresse: '',
        telephone: '',
        email: '',
        description: '',
        logo: '',
        heuresOuverture: {
            lundi: { ouvert: true, debut: '09:00', fin: '22:00' },
            mardi: { ouvert: true, debut: '09:00', fin: '22:00' },
            mercredi: { ouvert: true, debut: '09:00', fin: '22:00' },
            jeudi: { ouvert: true, debut: '09:00', fin: '22:00' },
            vendredi: { ouvert: true, debut: '09:00', fin: '23:00' },
            samedi: { ouvert: true, debut: '10:00', fin: '23:00' },
            dimanche: { ouvert: false, debut: '10:00', fin: '22:00' }
        },
        tva: 18,
        fraisService: 10,
        deviseSymbole: 'FCFA'
    });

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:3006/api';

    useEffect(() => {
        fetchSettings();
    }, []);

    const fetchSettings = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`${API_URL}/restaurant/settings`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSettings(response.data);
        } catch (error) {
            console.error('Erreur settings:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSave = async (e) => {
        e.preventDefault();
        setSaving(true);
        try {
            const token = localStorage.getItem('token');
            await axios.put(`${API_URL}/restaurant/settings`, settings, {
                headers: { Authorization: `Bearer ${token}` }
            });
            toast.success('Paramètres sauvegardés avec succès');
        } catch (error) {
            toast.error('Erreur lors de la sauvegarde');
        } finally {
            setSaving(false);
        }
    };

    const handleLogoUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        try {
            const token = localStorage.getItem('token');
            const response = await axios.post(`${API_URL}/restaurant/upload-logo`, formData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data'
                }
            });
            setSettings({ ...settings, logo: response.data.url });
            toast.success('Logo uploadé avec succès');
        } catch (error) {
            toast.error('Erreur lors de l\'upload');
        }
    };

    const updateHoraire = (jour, field, value) => {
        setSettings({
            ...settings,
            heuresOuverture: {
                ...settings.heuresOuverture,
                [jour]: {
                    ...settings.heuresOuverture[jour],
                    [field]: value
                }
            }
        });
    };

    const jours = ['lundi', 'mardi', 'mercredi', 'jeudi', 'vendredi', 'samedi', 'dimanche'];

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>;
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-4xl mx-auto">
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-2 flex items-center gap-3">
                        <Settings size={32} />
                        Paramètres du Restaurant
                    </h1>
                    <p className="text-gray-600">Configurez les informations de votre établissement</p>
                </div>

                <form onSubmit={handleSave} className="space-y-6">
                    {/* Informations générales */}
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <h2 className="text-xl font-bold mb-4">Informations Générales</h2>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Nom du restaurant *
                                </label>
                                <input
                                    type="text"
                                    required
                                    value={settings.nom}
                                    onChange={(e) => setSettings({ ...settings, nom: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    <Phone size={16} className="inline mr-1" />
                                    Téléphone *
                                </label>
                                <input
                                    type="tel"
                                    required
                                    value={settings.telephone}
                                    onChange={(e) => setSettings({ ...settings, telephone: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    <Mail size={16} className="inline mr-1" />
                                    Email
                                </label>
                                <input
                                    type="email"
                                    value={settings.email}
                                    onChange={(e) => setSettings({ ...settings, email: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    <MapPin size={16} className="inline mr-1" />
                                    Adresse
                                </label>
                                <input
                                    type="text"
                                    value={settings.adresse}
                                    onChange={(e) => setSettings({ ...settings, adresse: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>
                        </div>

                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Description
                            </label>
                            <textarea
                                value={settings.description}
                                onChange={(e) => setSettings({ ...settings, description: e.target.value })}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                rows="3"
                                placeholder="Décrivez votre restaurant..."
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Logo du restaurant
                            </label>
                            <div className="flex items-center gap-4">
                                {settings.logo && (
                                    <img src={settings.logo} alt="Logo" className="h-20 w-20 object-contain rounded-lg border" />
                                )}
                                <label className="flex items-center gap-2 bg-indigo-50 text-indigo-600 px-4 py-2 rounded-lg cursor-pointer hover:bg-indigo-100">
                                    <Upload size={20} />
                                    Changer le logo
                                    <input
                                        type="file"
                                        accept="image/*"
                                        className="hidden"
                                        onChange={handleLogoUpload}
                                    />
                                </label>
                            </div>
                        </div>
                    </div>

                    {/* Horaires d'ouverture */}
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
                            <Clock size={24} />
                            Horaires d'Ouverture
                        </h2>

                        <div className="space-y-3">
                            {jours.map(jour => (
                                <div key={jour} className="flex items-center gap-4 p-3 bg-gray-50 rounded-lg">
                                    <div className="w-24">
                                        <label className="flex items-center gap-2">
                                            <input
                                                type="checkbox"
                                                checked={settings.heuresOuverture[jour]?.ouvert}
                                                onChange={(e) => updateHoraire(jour, 'ouvert', e.target.checked)}
                                                className="rounded text-indigo-600 focus:ring-indigo-500"
                                            />
                                            <span className="font-medium capitalize">{jour}</span>
                                        </label>
                                    </div>

                                    {settings.heuresOuverture[jour]?.ouvert ? (
                                        <div className="flex items-center gap-2 flex-1">
                                            <input
                                                type="time"
                                                value={settings.heuresOuverture[jour]?.debut}
                                                onChange={(e) => updateHoraire(jour, 'debut', e.target.value)}
                                                className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                            />
                                            <span className="text-gray-500">à</span>
                                            <input
                                                type="time"
                                                value={settings.heuresOuverture[jour]?.fin}
                                                onChange={(e) => updateHoraire(jour, 'fin', e.target.value)}
                                                className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                            />
                                        </div>
                                    ) : (
                                        <span className="text-gray-500 italic">Fermé</span>
                                    )}
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Paramètres financiers */}
                    <div className="bg-white rounded-lg shadow-sm p-6">
                        <h2 className="text-xl font-bold mb-4">Paramètres Financiers</h2>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    TVA (%)
                                </label>
                                <input
                                    type="number"
                                    min="0"
                                    max="100"
                                    step="0.1"
                                    value={settings.tva}
                                    onChange={(e) => setSettings({ ...settings, tva: parseFloat(e.target.value) })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Frais de service (%)
                                </label>
                                <input
                                    type="number"
                                    min="0"
                                    max="100"
                                    step="0.1"
                                    value={settings.fraisService}
                                    onChange={(e) => setSettings({ ...settings, fraisService: parseFloat(e.target.value) })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Symbole devise
                                </label>
                                <input
                                    type="text"
                                    value={settings.deviseSymbole}
                                    onChange={(e) => setSettings({ ...settings, deviseSymbole: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>
                        </div>
                    </div>

                    {/* Bouton de sauvegarde */}
                    <div className="flex justify-end">
                        <button
                            type="submit"
                            disabled={saving}
                            className="flex items-center gap-2 bg-indigo-600 text-white px-6 py-3 rounded-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            <Save size={20} />
                            {saving ? 'Sauvegarde...' : 'Sauvegarder les paramètres'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default RestaurantSettings;
