import { useState, useEffect } from 'react';
import { Settings, Save, Upload, Clock, Phone, Mail, MapPin, Globe, Percent, Coins } from 'lucide-react';
import apiClient from '../../services/apiClient';
import { toast } from 'react-hot-toast';
import PageHeader from '../../widget/PageHeader';

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
        tauxTva: 18,
        fraisService: 10,
        deviseSymbole: 'FCFA'
    });

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        fetchSettings();
    }, []);

    const fetchSettings = async () => {
        try {
            const response = await apiClient.get('/restaurant/settings');
            const data = response.data;

            const normalizedData = {
                nom: data?.nom || '',
                adresse: data?.adresse || '',
                telephone: data?.telephone || '',
                email: data?.email || '',
                description: data?.description || '',
                logo: data?.logo || '',
                heuresOuverture: (data && data.heuresOuverture && typeof data.heuresOuverture === 'object') ? data.heuresOuverture : settings.heuresOuverture,
                tva: (data?.tauxTva !== undefined && data.tauxTva !== null) ? data.tauxTva : 18,
                fraisService: (data?.fraisService !== undefined && data.fraisService !== null) ? data.fraisService : 10,
                deviseSymbole: data?.deviseSymbole || 'FCFA'
            };

            setSettings(normalizedData);
        } catch (error) {
            console.error('Erreur settings:', error);
            toast.error('Erreur lors du chargement des paramètres');
        } finally {
            setLoading(false);
        }
    };

    const handleSave = async (e) => {
        e.preventDefault();
        setSaving(true);
        try {
            const dataToSave = {
                ...settings,
                tauxTva: settings.tva
            };
            await apiClient.put('/restaurant/settings', dataToSave);
            toast.success('Configuration mise à jour');
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
            const response = await apiClient.post('/restaurant/upload-logo', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            setSettings({ ...settings, logo: response.data.url });
            toast.success('Logo mis à jour');
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
                    ...(settings.heuresOuverture[jour] || { ouvert: true, debut: '09:00', fin: '22:00' }),
                    [field]: value
                }
            }
        });
    };

    const jours = ['lundi', 'mardi', 'mercredi', 'jeudi', 'vendredi', 'samedi', 'dimanche'];

    if (loading) {
        return (
            <div className="flex h-[60vh] items-center justify-center">
                <div className="h-12 w-12 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#f8fafc] animate-in fade-in duration-500 pb-12">
            <PageHeader
                icon={Settings}
                title="Paramètres"
                subtitle="Configurez l'identité et le fonctionnement de votre restaurant"
                actions={
                    <button
                        onClick={handleSave}
                        disabled={saving}
                        className="flex items-center gap-2 bg-indigo-600 text-white px-8 py-3 rounded-2xl font-black text-sm shadow-lg shadow-indigo-100 hover:bg-indigo-700 transition active:scale-95 disabled:opacity-50"
                    >
                        <Save size={18} />
                        {saving ? 'Sauvegarde...' : 'Enregistrer'}
                    </button>
                }
            />

            <div className="px-4 max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-8">
                {/* Left: General Info */}
                <div className="lg:col-span-7 space-y-8">
                    <div className="rounded-[32px] bg-white p-8 shadow-sm border border-slate-100">
                        <div className="mb-8 flex items-center justify-between">
                            <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic">Informations Générales</h2>
                            <div className="h-10 w-10 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
                                <Globe size={20} />
                            </div>
                        </div>

                        <div className="space-y-6">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="space-y-2">
                                    <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Nom de l'établissement</label>
                                    <input
                                        type="text" required
                                        value={settings.nom}
                                        onChange={(e) => setSettings({ ...settings, nom: e.target.value })}
                                        className="w-full rounded-2xl border-slate-100 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Téléphone</label>
                                    <div className="relative">
                                        <Phone className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
                                        <input
                                            type="tel" required
                                            value={settings.telephone}
                                            onChange={(e) => setSettings({ ...settings, telephone: e.target.value })}
                                            className="w-full rounded-2xl border-slate-100 bg-slate-50 py-4 pl-12 pr-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="space-y-2">
                                    <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Email de Contact</label>
                                    <div className="relative">
                                        <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
                                        <input
                                            type="email"
                                            value={settings.email}
                                            onChange={(e) => setSettings({ ...settings, email: e.target.value })}
                                            className="w-full rounded-2xl border-slate-100 bg-slate-50 py-4 pl-12 pr-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                        />
                                    </div>
                                </div>
                                <div className="space-y-2">
                                    <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Localisation</label>
                                    <div className="relative">
                                        <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
                                        <input
                                            type="text"
                                            value={settings.adresse}
                                            onChange={(e) => setSettings({ ...settings, adresse: e.target.value })}
                                            className="w-full rounded-2xl border-slate-100 bg-slate-50 py-4 pl-12 pr-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Description / Slogan</label>
                                <textarea
                                    value={settings.description}
                                    onChange={(e) => setSettings({ ...settings, description: e.target.value })}
                                    className="w-full rounded-2xl border-slate-100 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                    rows="3"
                                />
                            </div>

                            <div className="pt-4 flex items-center gap-6">
                                <div className="h-24 w-24 rounded-[24px] bg-slate-100 border-2 border-dashed border-slate-200 flex items-center justify-center overflow-hidden">
                                    {settings.logo ? (
                                        <img src={settings.logo} alt="Logo" className="h-full w-full object-contain" />
                                    ) : (
                                        <Upload className="text-slate-300" size={32} />
                                    )}
                                </div>
                                <div>
                                    <p className="text-sm font-black text-slate-800 mb-1">Identité Visuelle</p>
                                    <p className="text-xs text-slate-400 mb-3 font-medium">Recommandé: PNG 512x512</p>
                                    <label className="cursor-pointer bg-white border border-slate-200 px-4 py-2 rounded-xl text-xs font-black uppercase tracking-widest hover:bg-slate-50 transition shadow-sm">
                                        Changer le logo
                                        <input type="file" accept="image/*" className="hidden" onChange={handleLogoUpload} />
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="rounded-[32px] bg-white p-8 shadow-sm border border-slate-100">
                        <div className="mb-8 flex items-center justify-between">
                            <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight italic">Fiscalité & Devise</h2>
                            <div className="h-10 w-10 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
                                <Coins size={20} />
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                            <div className="space-y-2">
                                <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">TVA (%)</label>
                                <div className="relative">
                                    <Percent className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400" size={14} />
                                    <input
                                        type="number" step="0.1"
                                        value={settings.tva}
                                        onChange={(e) => setSettings({ ...settings, tva: parseFloat(e.target.value) })}
                                        className="w-full rounded-2xl border-slate-100 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                    />
                                </div>
                            </div>
                            <div className="space-y-2">
                                <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Service (%)</label>
                                <input
                                    type="number" step="0.1"
                                    value={settings.fraisService}
                                    onChange={(e) => setSettings({ ...settings, fraisService: parseFloat(e.target.value) })}
                                    className="w-full rounded-2xl border-slate-100 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                />
                            </div>
                            <div className="space-y-2">
                                <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Devise</label>
                                <input
                                    type="text"
                                    value={settings.deviseSymbole}
                                    onChange={(e) => setSettings({ ...settings, deviseSymbole: e.target.value })}
                                    className="w-full rounded-2xl border-slate-100 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0 shadow-inner"
                                />
                            </div>
                        </div>
                    </div>
                </div>

                {/* Right: Horaires */}
                <div className="lg:col-span-5">
                    <div className="rounded-[32px] bg-slate-900 p-8 shadow-xl text-white">
                        <div className="mb-8 flex items-center justify-between border-b border-white/10 pb-6">
                            <div>
                                <h2 className="text-xl font-black uppercase tracking-tight italic">Horaires</h2>
                                <p className="text-xs text-indigo-400 font-bold uppercase tracking-widest mt-1">Disponibilité Client</p>
                            </div>
                            <div className="h-12 w-12 rounded-2xl bg-indigo-500/20 text-indigo-400 flex items-center justify-center">
                                <Clock size={24} />
                            </div>
                        </div>

                        <div className="space-y-4">
                            {jours.map(jour => {
                                const horaire = settings.heuresOuverture?.[jour] || { ouvert: false, debut: '09:00', fin: '22:00' };
                                return (
                                    <div key={jour} className="group relative">
                                        <div className="flex items-center gap-4 transition-all">
                                            <div className="w-24">
                                                <label className="flex items-center gap-3 cursor-pointer">
                                                    <input
                                                        type="checkbox"
                                                        checked={horaire.ouvert}
                                                        onChange={(e) => updateHoraire(jour, 'ouvert', e.target.checked)}
                                                        className="h-5 w-5 rounded-lg border-white/20 bg-white/10 text-indigo-500 focus:ring-offset-slate-900"
                                                    />
                                                    <span className={`text-xs font-black uppercase tracking-widest ${horaire.ouvert ? 'text-white' : 'text-slate-500 line-through'}`}>
                                                        {jour.slice(0, 3)}
                                                    </span>
                                                </label>
                                            </div>

                                            {horaire.ouvert ? (
                                                <div className="flex items-center gap-2 flex-1">
                                                    <input
                                                        type="time"
                                                        value={horaire.debut}
                                                        onChange={(e) => updateHoraire(jour, 'debut', e.target.value)}
                                                        className="bg-white/5 border-none rounded-xl px-3 py-2 text-xs font-bold text-white focus:ring-2 focus:ring-indigo-500 outline-none"
                                                    />
                                                    <span className="text-slate-600 text-[10px] font-bold">—</span>
                                                    <input
                                                        type="time"
                                                        value={horaire.fin}
                                                        onChange={(e) => updateHoraire(jour, 'fin', e.target.value)}
                                                        className="bg-white/5 border-none rounded-xl px-3 py-2 text-xs font-bold text-white focus:ring-2 focus:ring-indigo-500 outline-none"
                                                    />
                                                </div>
                                            ) : (
                                                <div className="flex-1 flex justify-center">
                                                    <span className="text-[10px] font-black uppercase tracking-widest text-rose-500/50 italic py-2">Fermé</span>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>

                        <div className="mt-10 p-6 rounded-2xl bg-white/5 border border-white/10">
                            <p className="text-[10px] font-bold text-slate-400 leading-relaxed italic">
                                * Ces horaires seront utilisés pour les réservations en ligne et l'affichage sur le portail client.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RestaurantSettings;
