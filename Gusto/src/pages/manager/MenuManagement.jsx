import { useState, useEffect } from 'react';
import {
    Plus, Edit2, Trash2, Upload, Search,
    Utensils, LayoutGrid, Filter, TrendingUp, AlertCircle
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import apiClient from '../../services/apiClient';
import { toast } from 'react-hot-toast';
import PageHeader from '../../widget/PageHeader';
import ImageUploader from '../../components/ImageUploader';
import { getImageUrl, createImageErrorHandler, validateImageFile } from '../../utils/imageUtils';
import { getImageDisplayUrl } from '../../utils/imageStorage';

const MenuManagement = () => {
    const [plats, setPlats] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('all');
    const [searchTerm, setSearchTerm] = useState('');
    const [showPlatForm, setShowPlatForm] = useState(false);
    const [showCategoryForm, setShowCategoryForm] = useState(false);
    const [editingPlat, setEditingPlat] = useState(null);
    const [loading, setLoading] = useState(true);
    const [uploadingImage, setUploadingImage] = useState(false);
    const [pendingImageFile, setPendingImageFile] = useState(null);

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

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const [platsRes, categoriesRes] = await Promise.all([
                apiClient.get('/plats'),
                apiClient.get('/categories')
            ]);
            setPlats(platsRes.data);
            setCategories(categoriesRes.data);
        } catch (error) {
            console.error('Erreur chargement données menu', error);
            toast.error('Erreur lors du chargement des données');
        } finally {
            setLoading(false);
        }
    };

    const handleCreatePlat = async (e) => {
        e.preventDefault();
        if (!platForm.category) {
            toast.error('Veuillez sélectionner une catégorie');
            return;
        }

        try {
            const data = {
                ...platForm,
                prix: parseFloat(platForm.prix),
                category: platForm.category ? parseInt(platForm.category, 10) : null,
                photoUrl: '' // Clear URL as we'll upload image separately
            };

            let platId = editingPlat?.id;
            if (editingPlat) {
                await apiClient.put(`/plats/${editingPlat.id}`, data);
                toast.success('Plat modifié');
            } else {
                const res = await apiClient.post('/plats', data);
                platId = res.data.id;
                toast.success('Plat créé');
            }

            // Upload image if pending
            if (pendingImageFile && platId) {
                await handleImageUploadForPlat(pendingImageFile, platId);
            }

            loadData();
            resetPlatForm();
        } catch (error) {
            const serverMessage = error?.response?.data?.message || error?.message || 'Erreur lors de la sauvegarde';
            toast.error(`[API Error] ${serverMessage}`);
        }
    };

    const handleImageUploadForPlat = async (file, platId, retryCount = 0) => {
        setUploadingImage(true);
        const MAX_RETRIES = 2;
        try {
            const formData = new FormData();
            formData.append('file', file);
            
            const response = await apiClient.post(`/plats/${platId}/upload-image`, formData);
            
            // Mettre à jour le plat avec la référence d'image locale
            if (response.data && response.data.photoUrl) {
                setPlatForm(prev => ({
                    ...prev,
                    photoUrl: response.data.photoUrl
                }));
                // Mettre à jour la liste des plats
                setPlats(prev => prev.map(p => 
                    p.id === platId ? { ...p, photoUrl: response.data.photoUrl } : p
                ));
            }
            
            toast.success('Image uploadée et affichée');
        } catch (error) {
            const errorMsg = error?.response?.data?.message || error?.message || 'Erreur lors de l\'upload de l\'image';
            
            // Retry pour erreurs temporaires (500, 503, timeout)
            if (retryCount < MAX_RETRIES && (error?.response?.status >= 500 || error.code === 'ECONNABORTED')) {
                console.warn(`Retry ${retryCount + 1}/${MAX_RETRIES} pour l'upload d'image...`);
                toast.info(`Nouvelle tentative... (${retryCount + 1}/${MAX_RETRIES})`);
                await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1)));
                return handleImageUploadForPlat(file, platId, retryCount + 1);
            }
            
            toast.error(`Erreur upload: ${errorMsg}`);
            console.error('Image upload error:', error);
        } finally {
            setUploadingImage(false);
            setPendingImageFile(null);
        }
    };

    const handleImageSelected = async (file) => {
        const errors = validateImageFile(file, 5);
        if (errors.length > 0) {
            toast.error(errors[0]);
            return;
        }
        // Créer une preview locale avec FileReader
        const reader = new FileReader();
        reader.onload = (e) => {
            setPlatForm(prev => ({
                ...prev,
                photoUrl: e.target.result // Stocker la data URL pour l'affichage
            }));
        };
        reader.readAsDataURL(file);
        setPendingImageFile(file);
    };

    const handleDeletePlat = async (id) => {
        if (!confirm('Supprimer ce plat ?')) return;
        try {
            await apiClient.delete(`/plats/${id}`);
            toast.success('Plat supprimé');
            loadData();
        } catch (error) {
            toast.error('Erreur suppression');
        }
    };

    const toggleDisponibilite = async (plat) => {
        try {
            await apiClient.patch(`/plats/${plat.id}/statut-disponibilite?disponible=${!plat.disponibilite}`);
            toast.success('Statut mis à jour');
            loadData();
        } catch (error) {
            toast.error('Erreur');
        }
    };

    const resetPlatForm = () => {
        setPlatForm({ nom: '', description: '', prix: '', category: '', disponibilite: true, photoUrl: '' });
        setEditingPlat(null);
        setShowPlatForm(false);
        setPendingImageFile(null);
    };

    const editPlat = (plat) => {
        setPlatForm({
            nom: plat.nom,
            description: plat.description || '',
            prix: plat.prix.toString(),
            category: plat.category?.id?.toString() || '',
            disponibilite: plat.disponibilite,
            photoUrl: plat.photoUrl || ''
        });
        setEditingPlat(plat);
        setShowPlatForm(true);
    };

    const filteredPlats = plats.filter(plat => {
        // Si une catégorie est sélectionnée, vérifier qu'elle correspond
        if (selectedCategory !== 'all') {
            // Comparer en string pour éviter les problèmes de type
            const platCatId = String(plat.category?.id || '');
            if (platCatId !== selectedCategory) return false;
        }
        
        const matchSearch = plat.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
            (plat.description && plat.description.toLowerCase().includes(searchTerm.toLowerCase()));
        return matchSearch;
    }).sort((a, b) => {
        // Tri par catégorie, puis par nom
        const catDiff = (a.category?.id || 0) - (b.category?.id || 0);
        if (catDiff !== 0) return catDiff;
        return a.nom.localeCompare(b.nom);
    });

    if (loading) return (
        <div className="flex h-screen w-full items-center justify-center">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
        </div>
    );

    return (
        <div className="min-h-screen bg-[#f8fafc] animate-in fade-in duration-500 pb-12">
            <PageHeader
                icon={Utensils}
                title="Gestion du Menu"
                subtitle="Gérez vos plats et catégories avec précision."
                actions={
                    <div className="flex items-center gap-3">
                        <button
                            onClick={() => setShowCategoryForm(true)}
                            className="flex items-center gap-2 rounded-2xl border border-slate-200 bg-white px-6 py-3 text-sm font-bold text-slate-700 shadow-sm transition hover:bg-slate-50"
                        >
                            <LayoutGrid size={18} className="text-indigo-600" /> Catégories
                        </button>
                        <button
                            onClick={() => setShowPlatForm(true)}
                            className="flex items-center gap-2 rounded-2xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white shadow-lg shadow-indigo-100 transition hover:bg-indigo-700 hover:scale-[1.02] active:scale-95"
                        >
                            <Plus size={18} /> Nouveau Plat
                        </button>
                    </div>
                }
            />

            {/* Stats Summary */}
            <div className="mb-8 grid grid-cols-2 gap-4 lg:grid-cols-4">
                {[
                    { label: 'Total Plats', val: plats.length, color: 'text-slate-600', bg: 'bg-slate-100', icon: <Utensils size={20} /> },
                    { label: 'Disponibles', val: plats.filter(p => p.disponibilite).length, color: 'text-emerald-600', bg: 'bg-emerald-100', icon: <TrendingUp size={20} /> },
                    { label: 'Indisponibles', val: plats.filter(p => !p.disponibilite).length, color: 'text-rose-600', bg: 'bg-rose-100', icon: <AlertCircle size={20} /> },
                    { label: 'Catégories', val: categories.length, color: 'text-indigo-600', bg: 'bg-indigo-100', icon: <LayoutGrid size={20} /> },
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

            {/* Filters Bar */}
            <div className="mb-8 flex flex-col gap-4 md:flex-row md:items-center">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={20} />
                    <input
                        type="text"
                        placeholder="Rechercher par nom ou description..."
                        className="w-full rounded-[20px] border-slate-100 bg-white py-4 pl-12 pr-4 text-sm font-medium text-slate-700 shadow-sm focus:border-indigo-500 focus:ring-0"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
                <div className="flex items-center gap-2 overflow-x-auto pb-2 no-scrollbar md:pb-0">
                    <button
                        onClick={() => setSelectedCategory('all')}
                        className={`whitespace-nowrap rounded-full px-6 py-2.5 text-xs font-black uppercase tracking-widest transition ${selectedCategory === 'all' ? 'bg-slate-900 text-white shadow-lg' : 'bg-white text-slate-500 border border-slate-100 hover:bg-slate-50'}`}
                    >
                        Tous
                    </button>
                    {categories.map(cat => (
                        <button
                            key={cat.id}
                            onClick={() => setSelectedCategory(cat.id.toString())}
                            className={`whitespace-nowrap rounded-full px-6 py-2.5 text-xs font-black uppercase tracking-widest transition ${selectedCategory === cat.id.toString() ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-100' : 'bg-white text-slate-500 border border-slate-100 hover:bg-slate-50'}`}
                        >
                            {cat.nom}
                        </button>
                    ))}
                </div>
            </div>

            {/* Menu Grid */}
            <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                <AnimatePresence mode="popLayout">
                    {filteredPlats.map(plat => (
                        <motion.div
                            layout
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            exit={{ opacity: 0, scale: 0.95 }}
                            key={plat.id}
                            className="group relative flex flex-col rounded-[32px] bg-white p-4 shadow-sm border border-slate-100 transition hover:shadow-2xl hover:-translate-y-2"
                        >
                            {/* Image Container */}
                            <div className="relative h-48 overflow-hidden rounded-[24px] bg-slate-100">
                                {plat.photoUrl ? (
                                    <img 
                                        src={getImageDisplayUrl(plat.photoUrl)}
                                        alt={plat.nom} 
                                        className="h-full w-full object-cover transition duration-500 group-hover:scale-110"
                                        onError={createImageErrorHandler()}
                                    />
                                ) : (
                                    <div className="flex h-full w-full items-center justify-center text-slate-300">
                                        <Utensils size={48} />
                                    </div>
                                )}

                                {/* Status Badge */}
                                <div className="absolute top-3 left-3">
                                    <button
                                        onClick={(e) => { e.stopPropagation(); toggleDisponibilite(plat); }}
                                        className={`rounded-full px-3 py-1 text-[10px] font-black uppercase tracking-widest shadow-lg ${plat.disponibilite ? 'bg-emerald-500 text-white' : 'bg-rose-500 text-white'}`}
                                    >
                                        {plat.disponibilite ? 'En Stock' : 'Épuisé'}
                                    </button>
                                </div>
                            </div>

                            {/* Content */}
                            <div className="mt-5 px-1 pb-2">
                                <div className="flex items-start justify-between">
                                    <div className="max-w-[70%]">
                                        <h3 className="text-xl font-black leading-tight text-slate-900 group-hover:text-indigo-600 transition">{plat.nom}</h3>
                                        <p className="mt-1 text-xs font-bold uppercase text-slate-400">{plat.category?.nom || 'Sans catégorie'}</p>
                                    </div>
                                    <div className="text-right">
                                        <p className="text-lg font-black text-indigo-600">{plat.prix.toLocaleString()} <span className="text-[10px] ml-0.5 italic">FCFA</span></p>
                                    </div>
                                </div>

                                <p className="mt-3 line-clamp-2 text-sm font-medium text-slate-500 leading-relaxed">
                                    {plat.description || "Aucune description fournie pour ce plat premium."}
                                </p>

                                {/* Action Buttons */}
                                <div className="mt-5 flex gap-2">
                                    <button
                                        onClick={() => editPlat(plat)}
                                        className="flex-1 rounded-[16px] bg-slate-900 py-3 text-xs font-black uppercase tracking-widest text-white transition hover:bg-indigo-600 active:scale-95"
                                    >
                                        Modifier
                                    </button>
                                    <button
                                        onClick={() => handleDeletePlat(plat.id)}
                                        className="rounded-[16px] bg-rose-50 px-4 text-rose-600 transition hover:bg-rose-600 hover:text-white active:scale-95"
                                    >
                                        <Trash2 size={18} />
                                    </button>
                                </div>
                            </div>
                        </motion.div>
                    ))}
                </AnimatePresence>
            </div>

            {/* Modal Form Plat */}
            {showPlatForm && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/40 backdrop-blur-md p-4 overflow-y-auto">
                    <motion.div
                        initial={{ scale: 0.9, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        className="my-auto w-full max-w-2xl rounded-[40px] bg-white p-8 shadow-2xl lg:p-12"
                    >
                        <h3 className="text-3xl font-black text-slate-900 mb-8">{editingPlat ? 'Modifier Plat' : 'Nouveau Plat'}</h3>
                        <form onSubmit={handleCreatePlat} className="space-y-6">
                            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Nom du Chef-d'œuvre</label>
                                    <input
                                        required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={platForm.nom} onChange={(e) => setPlatForm({ ...platForm, nom: e.target.value })}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Prix de Vente (FCFA)</label>
                                    <input
                                        type="number" required className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={platForm.prix} onChange={(e) => setPlatForm({ ...platForm, prix: e.target.value })}
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Catégorie</label>
                                    <select
                                        className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                        value={platForm.category} onChange={(e) => setPlatForm({ ...platForm, category: e.target.value })}
                                    >
                                        <option value="">Sélectionner</option>
                                        {categories.map(c => <option key={c.id} value={c.id}>{c.nom}</option>)}
                                    </select>
                                </div>
                                <div className="space-y-2">
                                    <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Disponibilité</label>
                                    <div className="flex bg-slate-50 rounded-[20px] p-2 gap-2 border border-slate-200">
                                        <button
                                            type="button"
                                            onClick={() => setPlatForm({ ...platForm, disponibilite: true })}
                                            className={`flex-1 rounded-[14px] py-2.5 text-[10px] font-black uppercase tracking-widest transition ${platForm.disponibilite ? 'bg-indigo-600 text-white shadow-md' : 'text-slate-500'}`}
                                        >
                                            En Stock
                                        </button>
                                        <button
                                            type="button"
                                            onClick={() => setPlatForm({ ...platForm, disponibilite: false })}
                                            className={`flex-1 rounded-[14px] py-2.5 text-[10px] font-black uppercase tracking-widest transition ${!platForm.disponibilite ? 'bg-rose-500 text-white shadow-md' : 'text-slate-500'}`}
                                        >
                                            Épuisé
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div className="space-y-2">
                                <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Description Savoureuse</label>
                                <textarea
                                    className="w-full rounded-[20px] border-slate-200 bg-slate-50 p-4 font-bold text-slate-900 focus:border-indigo-500 focus:ring-0"
                                    rows="3" value={platForm.description} onChange={(e) => setPlatForm({ ...platForm, description: e.target.value })}
                                />
                            </div>
                            <div className="space-y-2">
                                <label className="text-xs font-black uppercase tracking-widest text-slate-400 ml-2">Photo du Plat</label>
                                <ImageUploader 
                                    onUpload={handleImageSelected}
                                    currentImage={platForm.photoUrl}
                                    isLoading={uploadingImage}
                                    maxSizeMB={5}
                                />
                                {pendingImageFile && (
                                    <p className="text-xs text-slate-600 ml-2">
                                        📷 Image en attente: {pendingImageFile.name}
                                    </p>
                                )}
                            </div>
                            <div className="flex gap-4 pt-4">
                                <button type="button" onClick={resetPlatForm} disabled={uploadingImage} className="flex-1 rounded-2xl py-5 font-black uppercase tracking-widest text-slate-500 hover:bg-slate-50 transition disabled:opacity-50">Annuler</button>
                                <button type="submit" disabled={uploadingImage} className="flex-1 rounded-[24px] bg-indigo-600 py-5 font-black uppercase tracking-widest text-white shadow-xl shadow-indigo-100 hover:bg-indigo-700 transition active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed">
                                    {uploadingImage ? '⏳ Upload en cours...' : (editingPlat ? 'Mettre à jour' : 'Confirmer')}
                                </button>
                            </div>
                        </form>
                    </motion.div>
                </div>
            )}
        </div>
    );
};

export default MenuManagement;
