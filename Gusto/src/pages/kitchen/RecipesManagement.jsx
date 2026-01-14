import { useState, useEffect } from 'react';
import { Search, Plus, Clock, Users, BookOpen, ChevronRight, Hash, Flame, AlertTriangle, Image as ImageIcon } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { getImageUrl, createImageErrorHandler } from '../../utils/imageUtils';
import { getImageDisplayUrl } from '../../utils/imageStorage';

const RecipesManagement = () => {
    const [recipes, setRecipes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedRecipe, setSelectedRecipe] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [viewMode, setViewMode] = useState('list'); // 'list' or 'grid'

    // Mock Data
    const mockRecipes = [
        {
            id: 1,
            name: 'Pizza Margherita',
            category: 'Plats',
            prepTime: '20 min',
            cookTime: '15 min',
            servings: 1,
            difficulty: 'Moyen',
            allergens: ['Gluten', 'Lactose'],
            ingredients: [
                { name: 'Pâte à pizza', quantity: '250g', unit: 'g' },
                { name: 'Sauce tomate', quantity: '100ml', unit: 'ml' },
                { name: 'Mozzarella', quantity: '150g', unit: 'g' },
                { name: 'Basilic', quantity: '10g', unit: 'g' },
                { name: 'Huile d\'olive', quantity: '10ml', unit: 'ml' }
            ],
            steps: [
                { step: 1, description: 'Étaler la pâte à pizza sur un plan de travail fariné.' },
                { step: 2, description: 'Étaler la sauce tomate uniformément sur la pâte.' },
                { step: 3, description: 'Disposer la mozzarella coupée en tranches.' },
                { step: 4, description: 'Cuire au four à 220°C pendant 10-15 minutes.' },
                { step: 5, description: 'Ajouter le basilic frais et un filet d\'huile d\'olive à la sortie du four.' }
            ],
            image: null
        },
        {
            id: 2,
            name: 'Tiramisu',
            category: 'Desserts',
            prepTime: '30 min',
            cookTime: '0 min',
            servings: 6,
            difficulty: 'Facile',
            allergens: ['Lactose', 'Oeufs', 'Coféine'],
            ingredients: [
                { name: 'Mascarpone', quantity: '500g', unit: 'g' },
                { name: 'Oeufs', quantity: '4', unit: 'pcs' },
                { name: 'Sucre', quantity: '100g', unit: 'g' },
                { name: 'Café', quantity: '200ml', unit: 'ml' },
                { name: 'Biscuits cuillère', quantity: '30', unit: 'pcs' }
            ],
            steps: [
                { step: 1, description: 'Séparer les blancs des jaunes d\'oeufs.' },
                { step: 2, description: 'Fouetter les jaunes avec le sucre et le mascarpone.' },
                { step: 3, description: 'Monter les blancs en neige et les incorporer délicatement.' },
                { step: 4, description: 'Tremper les biscuits dans le café et tapisser le plat.' },
                { step: 5, description: 'Alterner couches de crème et biscuits. Réserver au frais 4h.' }
            ],
            image: null
        }
    ];

    useEffect(() => {
        // Simulate fetch
        setTimeout(() => {
            setRecipes(mockRecipes);
            setLoading(false);
        }, 500);
    }, []);

    const filteredRecipes = recipes.filter(r =>
        r.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        r.ingredients.some(i => i.name.toLowerCase().includes(searchTerm.toLowerCase()))
    );

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col md:flex-row h-screen overflow-hidden">

            {/* List Sidebar */}
            <div className={`w-full md:w-1/3 lg:w-1/4 bg-white border-r border-gray-200 flex flex-col ${selectedRecipe ? 'hidden md:flex' : 'flex'}`}>
                <div className="p-4 border-b">
                    <h2 className="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
                        <BookOpen className="text-indigo-600" /> Recettes
                    </h2>
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                        <input
                            type="text"
                            placeholder="Rechercher une recette..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-9 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:bg-white transition"
                        />
                    </div>
                </div>

                <div className="flex-1 overflow-y-auto">
                    {filteredRecipes.map(recipe => (
                        <div
                            key={recipe.id}
                            onClick={() => setSelectedRecipe(recipe)}
                            className={`p-4 border-b cursor-pointer hover:bg-indigo-50 transition relative ${selectedRecipe?.id === recipe.id ? 'bg-indigo-50 border-indigo-200' : ''}`}
                        >
                            <h3 className="font-semibold text-gray-900 mb-1">{recipe.name}</h3>
                            <div className="flex items-center gap-4 text-xs text-gray-500">
                                <span className="flex items-center gap-1"><Clock size={12} /> {recipe.prepTime}</span>
                                <span className="flex items-center gap-1"><Hash size={12} /> {recipe.ingredients.length} ingrédients</span>
                            </div>
                            <span className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-300">
                                <ChevronRight size={20} />
                            </span>
                        </div>
                    ))}

                    {filteredRecipes.length === 0 && (
                        <div className="p-8 text-center text-gray-400">
                            Aucune recette trouvée
                        </div>
                    )}
                </div>

                <div className="p-4 border-t bg-gray-50">
                    <button className="w-full py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition flex items-center justify-center gap-2 shadow-sm">
                        <Plus size={18} /> Nouvelle Recette
                    </button>
                    {/* Note: Create functionality not implemented in this mock view */}
                </div>
            </div>

            {/* Detail View */}
            <div className={`flex-1 overflow-y-auto bg-gray-50 p-6 ${!selectedRecipe ? 'hidden md:flex items-center justify-center' : ''}`}>

                {selectedRecipe ? (
                    <div className="max-w-4xl mx-auto w-full">
                        <button
                            className="md:hidden mb-4 text-indigo-600 font-medium flex items-center gap-1"
                            onClick={() => setSelectedRecipe(null)}
                        >
                            ← Retour à la liste
                        </button>

                        <div className="bg-white rounded-xl shadow-sm overflow-hidden mb-8">
                            {/* Header Image */}
                            <div className="h-64 bg-gray-200 relative">
                                {selectedRecipe.image ? (
                                    <img 
                                        src={getImageDisplayUrl(selectedRecipe.image)} 
                                        alt={selectedRecipe.name} 
                                        onError={createImageErrorHandler()}
                                        className="w-full h-full object-cover" 
                                    />
                                ) : (
                                    <div className="w-full h-full flex flex-col items-center justify-center text-gray-400 bg-gray-100">
                                        <ImageIcon size={64} className="mb-2 opacity-50" />
                                        <span>Aucune photo disponible</span>
                                    </div>
                                )}
                                <div className="absolute top-4 right-4">
                                    <span className={`px-3 py-1 rounded-full text-sm font-semibold shadow-sm ${selectedRecipe.difficulty === 'Facile' ? 'bg-green-100 text-green-800' :
                                            selectedRecipe.difficulty === 'Moyen' ? 'bg-yellow-100 text-yellow-800' : 'bg-red-100 text-red-800'
                                        }`}>
                                        {selectedRecipe.difficulty}
                                    </span>
                                </div>
                            </div>

                            {/* Content */}
                            <div className="p-8">
                                <div className="flex flex-wrap justify-between items-start mb-6 gap-4">
                                    <div>
                                        <h1 className="text-3xl font-bold text-gray-900 mb-2">{selectedRecipe.name}</h1>
                                        <div className="flex items-center gap-6 text-gray-600">
                                            <span className="flex items-center gap-2"><Clock size={18} /> Prépa: {selectedRecipe.prepTime}</span>
                                            <span className="flex items-center gap-2"><Flame size={18} /> Cuisson: {selectedRecipe.cookTime}</span>
                                            <span className="flex items-center gap-2"><Users size={18} /> Portions: {selectedRecipe.servings}</span>
                                        </div>
                                    </div>
                                    <button className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 text-gray-700 transition">
                                        Modifier
                                    </button>
                                </div>

                                {selectedRecipe.allergens && selectedRecipe.allergens.length > 0 && (
                                    <div className="mb-8 bg-orange-50 p-4 rounded-lg border border-orange-100 flex items-start gap-3">
                                        <AlertTriangle className="text-orange-500 shrink-0 mt-0.5" size={20} />
                                        <div>
                                            <h4 className="font-semibold text-orange-800 mb-1">Allergènes</h4>
                                            <p className="text-orange-700 text-sm">{selectedRecipe.allergens.join(', ')}</p>
                                        </div>
                                    </div>
                                )}

                                <div className="grid md:grid-cols-3 gap-8">
                                    {/* Ingrédients */}
                                    <div className="md:col-span-1">
                                        <h3 className="text-xl font-bold text-gray-900 mb-4 border-b pb-2">Ingrédients</h3>
                                        <ul className="space-y-3">
                                            {selectedRecipe.ingredients.map((ing, idx) => (
                                                <li key={idx} className="flex justify-between items-center text-gray-700 py-1 border-b border-gray-50 last:border-0 hover:bg-gray-50 px-2 rounded -mx-2">
                                                    <span>{ing.name}</span>
                                                    <span className="font-semibold text-gray-900">{ing.quantity}</span>
                                                </li>
                                            ))}
                                        </ul>
                                    </div>

                                    {/* Instructions */}
                                    <div className="md:col-span-2">
                                        <h3 className="text-xl font-bold text-gray-900 mb-4 border-b pb-2">Préparation</h3>
                                        <div className="space-y-6">
                                            {selectedRecipe.steps.map((step, idx) => (
                                                <div key={idx} className="flex gap-4">
                                                    <div className="flex-shrink-0 w-8 h-8 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center font-bold text-sm">
                                                        {step.step}
                                                    </div>
                                                    <div className="pt-1">
                                                        <p className="text-gray-700 leading-relaxed">{step.description}</p>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                ) : (
                    <div className="text-center text-gray-400">
                        <BookOpen size={64} className="mx-auto mb-4 opacity-30" />
                        <p className="text-xl font-medium">Sélectionnez une recette pour voir les détails</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default RecipesManagement;
