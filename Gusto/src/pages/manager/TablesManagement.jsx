import React, { useState, useEffect, useCallback } from 'react';
import { formatStatut, normalizeStatutKey } from '../../utils/formatters';
import { Plus, Edit2, Trash2, Users, MapPin, Layout, CheckCircle, Clock, AlertCircle } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import apiClient from '../../services/apiClient';
import { toast } from 'react-hot-toast';
import PageHeader from '../../widget/PageHeader';

const TablesManagement = () => {
  const [tables, setTables] = useState([]);
  const [zones, setZones] = useState([]);
  const [selectedZone, setSelectedZone] = useState('all');
  const [showTableForm, setShowTableForm] = useState(false);
  const [showZoneForm, setShowZoneForm] = useState(false);
  const [editingTable, setEditingTable] = useState(null);
  const [loading, setLoading] = useState(true);

  const [tableForm, setTableForm] = useState({ numero: '', capacite: 2, zoneId: '', statut: 'LIBRE' });
  const [zoneForm, setZoneForm] = useState({ nom: '', description: '' });

  // Pagination for zones
  const [zonePage, setZonePage] = useState(0);
  const zonesPerPage = 6;

  // --- FONCTIONS DE RÉCUPÉRATION (Déplacées ici pour le Hoisting) ---

  const fetchTables = useCallback(async () => {
    try {
      const response = await apiClient.get('/tables');
      setTables(response.data);
    } catch (error) {
      toast.error('Erreur lors de la récupération des tables');
    }
  }, []);

  const fetchZones = useCallback(async () => {
    try {
      const response = await apiClient.get('/zones');
      setZones(response.data);
    } catch (error) {
      console.error('Erreur lors de la récupération des zones', error);
    }
  }, []);

  // --- EFFET INITIAL ---

  useEffect(() => {
    const initData = async () => {
      setLoading(true);
      await Promise.all([fetchTables(), fetchZones()]);
      setLoading(false);
    };
    initData();
  }, [fetchTables, fetchZones]);

  // --- ACTIONS ---

  const resetTableForm = () => {
    setTableForm({ numero: '', capacite: 2, zoneId: '', statut: 'LIBRE' });
    setEditingTable(null);
    setShowTableForm(false);
  };

  const handleCreateTable = async (e) => {
    e.preventDefault();
    try {
      if (editingTable) {
        await apiClient.put(`/tables/${editingTable.id}`, tableForm);
        toast.success('Table mise à jour');
      } else {
        await apiClient.post('/tables', tableForm);
        toast.success('Nouvelle table ajoutée');
      }
      fetchTables();
      resetTableForm();
    } catch (error) {
      toast.error('Échec de l\'opération');
    }
  };

  const getStatusStyles = (statut) => {
    const styles = {
      LIBRE: {
        bg: 'bg-emerald-50', text: 'text-emerald-700', border: 'border-emerald-200',
        icon: <CheckCircle size={14} />, dot: 'bg-emerald-500'
      },
      OCCUPEE: {
        bg: 'bg-rose-50', text: 'text-rose-700', border: 'border-rose-200',
        icon: <Users size={14} />, dot: 'bg-rose-500'
      },
      RESERVEE: {
        bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200',
        icon: <Clock size={14} />, dot: 'bg-amber-500'
      },
      A_NETTOYER: {
        bg: 'bg-slate-50', text: 'text-slate-700', border: 'border-slate-200',
        icon: <AlertCircle size={14} />, dot: 'bg-slate-500'
      }
    };
    return styles[statut] || styles.LIBRE;
  };

  if (loading) return (
      <div className="flex h-screen w-full items-center justify-center bg-gray-50">
        <div className="h-12 w-12 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
      </div>
  );

  const totalZonePages = Math.max(1, Math.ceil(zones.length / zonesPerPage));
  const pagedZones = zones.slice(zonePage * zonesPerPage, (zonePage + 1) * zonesPerPage);

  return (
      <div className="min-h-screen bg-[#f8fafc] animate-in fade-in duration-500 pb-12">
        <PageHeader
            icon={Layout}
            title="Plan de Salle"
            subtitle="Configurez l'agencement et suivez l'état de vos tables."
            actions={
              <>
                <button
                    onClick={() => setShowZoneForm(true)}
                    className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-5 py-2.5 text-sm font-bold text-slate-700 shadow-sm transition hover:bg-slate-50"
                >
                  <MapPin size={18} className="text-indigo-600" /> Ajouter une Zone
                </button>
                <button
                    onClick={() => setShowTableForm(true)}
                    className="flex items-center gap-2 rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-bold text-white shadow-lg shadow-indigo-200 transition hover:bg-indigo-700 hover:scale-[1.02] active:scale-95"
                >
                  <Plus size={18} /> Nouvelle Table
                </button>
              </>
            }
        />

        {/* STATS STRIP */}
        <div className="mb-8 grid grid-cols-2 gap-4 lg:grid-cols-4">
          {[
            { label: 'Total', val: tables.length, color: 'text-slate-600', bg: 'bg-slate-100', icon: <Layout size={20} /> },
            { label: 'Libres', val: tables.filter(t => normalizeStatutKey(t.statut) === 'LIBRE').length, color: 'text-emerald-600', bg: 'bg-emerald-100', icon: <CheckCircle size={20} /> },
            { label: 'Occupées', val: tables.filter(t => normalizeStatutKey(t.statut) === 'OCCUPEE').length, color: 'text-rose-600', bg: 'bg-rose-100', icon: <Users size={20} /> },
            { label: 'Zones', val: zones.length, color: 'text-indigo-600', bg: 'bg-indigo-100', icon: <MapPin size={20} /> },
          ].map((s, i) => (
              <div key={i} className="flex items-center gap-4 rounded-2xl bg-white p-4 shadow-sm border border-slate-100">
                <div className={`rounded-xl ${s.bg} ${s.color} p-3`}>{s.icon}</div>
                <div>
                  <p className="text-xs font-bold uppercase tracking-wider text-slate-400">{s.label}</p>
                  <p className="text-2xl font-black text-slate-800">{s.val}</p>
                </div>
              </div>
          ))}
        </div>

        {/* FILTERS */}
        <div className="mb-4">
          <div className="mb-2 flex items-center gap-2 overflow-x-auto pb-2 no-scrollbar">
            <button
                onClick={() => { setSelectedZone('all'); setZonePage(0); }}
                className={`whitespace-nowrap rounded-full px-6 py-2 text-sm font-bold transition ${selectedZone === 'all' ? 'bg-slate-900 text-white shadow-md' : 'bg-white text-slate-600 hover:bg-slate-50'}`}
            >
              Toutes les zones
            </button>
            {zones.slice(zonePage * zonesPerPage, (zonePage + 1) * zonesPerPage).map(z => (
                <button
                    key={z.id}
                    onClick={() => setSelectedZone(z.id.toString())}
                    className={`whitespace-nowrap rounded-full px-6 py-2 text-sm font-bold transition ${selectedZone === z.id.toString() ? 'bg-indigo-600 text-white shadow-md' : 'bg-white text-slate-600 hover:bg-slate-50'}`}
                >
                  {z.nom}
                </button>
            ))}
          </div>

          <div className="flex items-center gap-2 justify-end">
            <button onClick={() => setZonePage(Math.max(0, zonePage - 1))} disabled={zonePage === 0} className="px-3 py-1 rounded-md bg-white border">Préc.</button>
            <div className="text-sm text-slate-600">Page {zonePage + 1} / {totalZonePages}</div>
            <button onClick={() => setZonePage(Math.min(totalZonePages - 1, zonePage + 1))} disabled={zonePage >= totalZonePages - 1} className="px-3 py-1 rounded-md bg-white border">Suiv.</button>
          </div>
        </div>

        {/* ZONES & TABLES GRID */}
        <div className="space-y-10">
          {pagedZones.filter(z => selectedZone === 'all' || z.id.toString() === selectedZone).map(zone => {
            const zoneTables = tables.filter(t => String(((t.zone && t.zone.id) || t.zoneId)) === String(zone.id));
            return (
                <section key={zone.id}>
                  <div className="mb-5 flex items-center gap-3">
                    <h2 className="text-xl font-black text-slate-800 uppercase tracking-tight">{zone.nom}</h2>
                    <div className="h-px flex-1 bg-slate-200"></div>
                    <span className="rounded-md bg-slate-200 px-2 py-0.5 text-xs font-bold text-slate-500 uppercase">
                  {zoneTables.length} Tables
                </span>
                  </div>

                  <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 xl:grid-cols-8">
                    <AnimatePresence mode="popLayout">
                      {zoneTables.map(table => {
                        const style = getStatusStyles(table.statut);
                        return (
                            <motion.div
                                layout
                                initial={{ opacity: 0, scale: 0.9 }}
                                animate={{ opacity: 1, scale: 1 }}
                                exit={{ opacity: 0, scale: 0.9 }}
                                key={table.id}
                                className={`group relative flex flex-col items-center justify-center rounded-3xl border-2 ${style.bg} ${style.border} p-6 shadow-sm transition hover:shadow-xl hover:-translate-y-1`}
                            >
                              <div className="absolute top-3 left-3 flex items-center gap-1.5">
                                <span className={`h-2 w-2 animate-pulse rounded-full ${style.dot}`}></span>
                              </div>
                              <div className="absolute top-3 right-3 flex items-center gap-1 text-[10px] font-bold text-slate-400">
                                <Users size={10} /> {table.capacite}
                              </div>
                              <span className={`text-3xl font-black ${style.text}`}>{table.numero}</span>
                              <span className={`mt-1 text-[10px] font-black uppercase tracking-widest ${style.text} opacity-70`}>
                          {formatStatut(table.statut)}
                        </span>
                              <div className="absolute inset-0 flex items-center justify-center gap-2 rounded-3xl bg-white/80 opacity-0 backdrop-blur-[2px] transition group-hover:opacity-100">
                                <button
                                    onClick={() => {
                                      setTableForm({ numero: table.numero, capacite: table.capacite, zoneId: table.zone?.id || '', statut: table.statut });
                                      setEditingTable(table);
                                      setShowTableForm(true);
                                    }}
                                    className="rounded-full bg-slate-900 p-2 text-white hover:bg-indigo-600 transition"
                                >
                                  <Edit2 size={16} />
                                </button>
                                <button
                                    onClick={() => { if (confirm('Supprimer ?')) apiClient.delete(`/tables/${table.id}`).then(() => fetchTables()) }}
                                    className="rounded-full bg-rose-600 p-2 text-white hover:bg-rose-700 transition"
                                >
                                  <Trash2 size={16} />
                                </button>
                              </div>
                            </motion.div>
                        );
                      })}
                    </AnimatePresence>
                  </div>
                </section>
            );
          })}
        </div>

        {showTableForm && (
            <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-900/40 backdrop-blur-md p-4">
              <motion.div
                  initial={{ scale: 0.9, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  className="w-full max-w-md rounded-[32px] bg-white p-8 shadow-2xl"
              >
                <h3 className="text-2xl font-black text-slate-800 mb-6">
                  {editingTable ? 'Modifier Table' : 'Nouvelle Table'}
                </h3>
                <form onSubmit={handleCreateTable} className="space-y-5">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Numéro</label>
                      <input
                          className="mt-1 w-full rounded-2xl border-slate-200 bg-slate-50 p-3 font-bold focus:border-indigo-500 focus:ring-0"
                          value={tableForm.numero}
                          onChange={e => setTableForm({ ...tableForm, numero: e.target.value })}
                          placeholder="T-01" required
                      />
                    </div>
                    <div>
                      <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Capacité</label>
                      <input
                          type="number" className="mt-1 w-full rounded-2xl border-slate-200 bg-slate-50 p-3 font-bold focus:border-indigo-500 focus:ring-0"
                          value={tableForm.capacite}
                          onChange={e => setTableForm({ ...tableForm, capacite: e.target.value })}
                      />
                    </div>
                  </div>
                  <div>
                    <label className="text-xs font-bold uppercase tracking-wider text-slate-400 ml-1">Zone</label>
                    <select
                        className="mt-1 w-full rounded-2xl border-slate-200 bg-slate-50 p-3 font-bold focus:border-indigo-500 focus:ring-0"
                        value={tableForm.zoneId} onChange={e => setTableForm({ ...tableForm, zoneId: e.target.value })}
                    >
                      <option value="">Sélectionner une zone</option>
                      {zones.map(z => <option key={z.id} value={z.id}>{z.nom}</option>)}
                    </select>
                  </div>
                  <div className="flex gap-3 pt-4">
                    <button type="button" onClick={resetTableForm} className="flex-1 rounded-2xl py-4 font-bold text-slate-500 hover:bg-slate-50 transition">Annuler</button>
                    <button type="submit" className="flex-1 rounded-2xl bg-indigo-600 py-4 font-bold text-white shadow-lg shadow-indigo-100 hover:bg-indigo-700 transition">
                      {editingTable ? 'Mettre à jour' : 'Confirmer'}
                    </button>
                  </div>
                </form>
              </motion.div>
            </div>
        )}
      </div>
  );
};

export default TablesManagement;