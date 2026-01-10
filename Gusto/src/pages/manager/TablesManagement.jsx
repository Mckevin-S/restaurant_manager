import { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Users, MapPin } from 'lucide-react';
import apiClient from '../../services/apiClient';
import { toast } from 'react-hot-toast';


const TablesManagement = () => {
  const [tables, setTables] = useState([]);
  const [zones, setZones] = useState([]);
  const [selectedZone, setSelectedZone] = useState('all');
  const [showTableForm, setShowTableForm] = useState(false);
  const [showZoneForm, setShowZoneForm] = useState(false);
  const [editingTable, setEditingTable] = useState(null);
  const [loading, setLoading] = useState(true);

  const [tableForm, setTableForm] = useState({
    numero: '',
    capacite: 2,
    zoneId: '',
    statut: 'LIBRE'
  });

  const [zoneForm, setZoneForm] = useState({
    nom: '',
    description: ''
  });


  // Charger les tables et zones
  useEffect(() => {
    fetchTables();
    fetchZones();
  }, []);

  const fetchTables = async () => {
    try {
      const response = await apiClient.get('/tables');
      setTables(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des tables');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };


  const fetchZones = async () => {
    try {
      const response = await apiClient.get('/zones');
      setZones(response.data);
    } catch (error) {
      console.error('Erreur zones:', error);
    }
  };


  const handleCreateTable = async (e) => {
    e.preventDefault();
    try {
      if (editingTable) {
        await apiClient.put(`/tables/${editingTable.id}`, tableForm);
        toast.success('Table modifiée avec succès');
      } else {
        await apiClient.post('/tables', tableForm);
        toast.success('Table créée avec succès');
      }
      fetchTables();
      resetTableForm();
    } catch (error) {
      toast.error('Erreur lors de la sauvegarde');
      console.error(error);
    }
  };


  const handleDeleteTable = async (id) => {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cette table ?')) return;

    try {
      await apiClient.delete(`/tables/${id}`);
      toast.success('Table supprimée');
      fetchTables();
    } catch (error) {
      toast.error('Erreur lors de la suppression');
    }
  };


  const handleCreateZone = async (e) => {
    e.preventDefault();
    try {
      await apiClient.post('/zones', zoneForm);
      toast.success('Zone créée avec succès');
      fetchZones();
      setShowZoneForm(false);
      setZoneForm({ nom: '', description: '' });
    } catch (error) {
      toast.error('Erreur lors de la création de la zone');
    }
  };


  const resetTableForm = () => {
    setTableForm({ numero: '', capacite: 2, zoneId: '', statut: 'LIBRE' });
    setEditingTable(null);
    setShowTableForm(false);
  };

  const editTable = (table) => {
    setTableForm({
      numero: table.numero,
      capacite: table.capacite,
      zoneId: table.zone?.id || '',
      statut: table.statut
    });
    setEditingTable(table);
    setShowTableForm(true);
  };

  const filteredTables = selectedZone === 'all'
    ? tables
    : tables.filter(t => t.zone?.id === parseInt(selectedZone));

  const getStatusColor = (statut) => {
    const colors = {
      LIBRE: 'bg-green-100 text-green-800 border-green-300',
      OCCUPEE: 'bg-red-100 text-red-800 border-red-300',
      RESERVEE: 'bg-blue-100 text-blue-800 border-blue-300',
      A_NETTOYER: 'bg-yellow-100 text-yellow-800 border-yellow-300'
    };
    return colors[statut] || 'bg-gray-100 text-gray-800';
  };

  const getStatusLabel = (statut) => {
    const labels = {
      LIBRE: 'Libre',
      OCCUPEE: 'Occupée',
      RESERVEE: 'Réservée',
      A_NETTOYER: 'À nettoyer'
    };
    return labels[statut] || statut;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Gestion des Tables</h1>
        <p className="text-gray-600">Gérez les tables et zones de votre restaurant</p>
      </div>

      {/* Actions Bar */}
      <div className="bg-white rounded-lg shadow-sm p-4 mb-6 flex flex-wrap gap-4 items-center justify-between">
        <div className="flex gap-3">
          <button
            onClick={() => setShowTableForm(true)}
            className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition"
          >
            <Plus size={20} />
            Nouvelle Table
          </button>
          <button
            onClick={() => setShowZoneForm(true)}
            className="flex items-center gap-2 bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition"
          >
            <MapPin size={20} />
            Nouvelle Zone
          </button>
        </div>

        {/* Filtre par zone */}
        <select
          value={selectedZone}
          onChange={(e) => setSelectedZone(e.target.value)}
          className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
        >
          <option value="all">Toutes les zones</option>
          {zones.map(zone => (
            <option key={zone.id} value={zone.id}>{zone.nom}</option>
          ))}
        </select>
      </div>

      {/* Statistiques */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Tables</p>
              <p className="text-2xl font-bold text-gray-900">{tables.length}</p>
            </div>
            <div className="bg-indigo-100 p-3 rounded-full">
              <Users className="text-indigo-600" size={24} />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Libres</p>
              <p className="text-2xl font-bold text-green-600">
                {tables.filter(t => t.statut === 'LIBRE').length}
              </p>
            </div>
            <div className="bg-green-100 p-3 rounded-full">
              <div className="w-6 h-6 bg-green-600 rounded-full"></div>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Occupées</p>
              <p className="text-2xl font-bold text-red-600">
                {tables.filter(t => t.statut === 'OCCUPEE').length}
              </p>
            </div>
            <div className="bg-red-100 p-3 rounded-full">
              <div className="w-6 h-6 bg-red-600 rounded-full"></div>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Zones</p>
              <p className="text-2xl font-bold text-gray-900">{zones.length}</p>
            </div>
            <div className="bg-gray-100 p-3 rounded-full">
              <MapPin className="text-gray-600" size={24} />
            </div>
          </div>
        </div>
      </div>

      {/* Liste des tables par zone */}
      <div className="space-y-6">
        {zones.filter(zone => selectedZone === 'all' || zone.id === parseInt(selectedZone)).map(zone => {
          const zoneTables = tables.filter(t => t.zone?.id === zone.id);
          if (zoneTables.length === 0 && selectedZone !== 'all') return null;

          return (
            <div key={zone.id} className="bg-white rounded-lg shadow-sm p-6">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                    <MapPin size={20} className="text-indigo-600" />
                    {zone.nom}
                  </h2>
                  {zone.description && (
                    <p className="text-sm text-gray-600 mt-1">{zone.description}</p>
                  )}
                </div>
                <span className="text-sm text-gray-500">
                  {zoneTables.length} table{zoneTables.length > 1 ? 's' : ''}
                </span>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
                {zoneTables.map(table => (
                  <div
                    key={table.id}
                    className={`relative border-2 rounded-lg p-4 transition-all hover:shadow-md ${getStatusColor(table.statut)}`}
                  >
                    <div className="text-center">
                      <div className="text-2xl font-bold mb-1">
                        {table.numero}
                      </div>
                      <div className="flex items-center justify-center gap-1 text-sm mb-2">
                        <Users size={14} />
                        <span>{table.capacite}</span>
                      </div>
                      <div className="text-xs font-medium">
                        {getStatusLabel(table.statut)}
                      </div>
                    </div>

                    {/* Actions */}
                    <div className="absolute top-2 right-2 flex gap-1">
                      <button
                        onClick={() => editTable(table)}
                        className="p-1 bg-white rounded-full shadow-sm hover:bg-gray-50"
                      >
                        <Edit2 size={14} className="text-gray-600" />
                      </button>
                      <button
                        onClick={() => handleDeleteTable(table.id)}
                        className="p-1 bg-white rounded-full shadow-sm hover:bg-gray-50"
                      >
                        <Trash2 size={14} className="text-red-600" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          );
        })}

        {/* Tables sans zone */}
        {tables.filter(t => !t.zone).length > 0 && (
          <div className="bg-white rounded-lg shadow-sm p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Sans zone</h2>
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
              {tables.filter(t => !t.zone).map(table => (
                <div
                  key={table.id}
                  className={`relative border-2 rounded-lg p-4 ${getStatusColor(table.statut)}`}
                >
                  <div className="text-center">
                    <div className="text-2xl font-bold mb-1">{table.numero}</div>
                    <div className="flex items-center justify-center gap-1 text-sm mb-2">
                      <Users size={14} />
                      <span>{table.capacite}</span>
                    </div>
                    <div className="text-xs font-medium">
                      {getStatusLabel(table.statut)}
                    </div>
                  </div>
                  <div className="absolute top-2 right-2 flex gap-1">
                    <button
                      onClick={() => editTable(table)}
                      className="p-1 bg-white rounded-full shadow-sm"
                    >
                      <Edit2 size={14} className="text-gray-600" />
                    </button>
                    <button
                      onClick={() => handleDeleteTable(table.id)}
                      className="p-1 bg-white rounded-full shadow-sm"
                    >
                      <Trash2 size={14} className="text-red-600" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Modal Formulaire Table */}
      {showTableForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-md w-full p-6">
            <h3 className="text-xl font-bold mb-4">
              {editingTable ? 'Modifier la table' : 'Nouvelle table'}
            </h3>
            <form onSubmit={handleCreateTable} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Numéro de table *
                </label>
                <input
                  type="text"
                  required
                  value={tableForm.numero}
                  onChange={(e) => setTableForm({ ...tableForm, numero: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                  placeholder="Ex: T1, A1, VIP1..."
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Capacité *
                </label>
                <input
                  type="number"
                  required
                  min="1"
                  max="20"
                  value={tableForm.capacite}
                  onChange={(e) => setTableForm({ ...tableForm, capacite: parseInt(e.target.value) })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Zone
                </label>
                <select
                  value={tableForm.zoneId}
                  onChange={(e) => setTableForm({ ...tableForm, zoneId: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="">Sans zone</option>
                  {zones.map(zone => (
                    <option key={zone.id} value={zone.id}>{zone.nom}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Statut
                </label>
                <select
                  value={tableForm.statut}
                  onChange={(e) => setTableForm({ ...tableForm, statut: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="LIBRE">Libre</option>
                  <option value="OCCUPEE">Occupée</option>
                  <option value="RESERVEE">Réservée</option>
                  <option value="A_NETTOYER">À nettoyer</option>
                </select>
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={resetTableForm}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Annuler
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
                >
                  {editingTable ? 'Modifier' : 'Créer'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Formulaire Zone */}
      {showZoneForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-md w-full p-6">
            <h3 className="text-xl font-bold mb-4">Nouvelle zone</h3>
            <form onSubmit={handleCreateZone} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nom de la zone *
                </label>
                <input
                  type="text"
                  required
                  value={zoneForm.nom}
                  onChange={(e) => setZoneForm({ ...zoneForm, nom: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                  placeholder="Ex: Terrasse, Salle principale, VIP..."
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Description
                </label>
                <textarea
                  value={zoneForm.description}
                  onChange={(e) => setZoneForm({ ...zoneForm, description: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                  rows="3"
                  placeholder="Description de la zone..."
                />
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowZoneForm(false);
                    setZoneForm({ nom: '', description: '' });
                  }}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Annuler
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                >
                  Créer
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default TablesManagement;
