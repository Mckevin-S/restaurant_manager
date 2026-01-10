import { useState } from 'react';
import { Search, HelpCircle, Book, Video, Mail, Phone, ChevronDown, ChevronUp, FileText } from 'lucide-react';

const Help = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [expandedFaq, setExpandedFaq] = useState(null);

    const faqs = [
        {
            id: 1,
            question: "Comment ajouter un nouvel utilisateur ?",
            answer: "Allez dans Gestion > Utilisateurs, puis cliquez sur le bouton 'Ajouter' en haut à droite. Remplissez le formulaire avec les informations de l'employé et attribuez-lui un rôle."
        },
        {
            id: 2,
            question: "Comment modifier le menu ?",
            answer: "Dans le tableau de bord Manager, sélectionnez 'Menu'. Vous pouvez modifier les prix, ajouter des plats ou désactiver temporairement un article en rupture de stock."
        },
        {
            id: 3,
            question: "Que faire en cas d'erreur d'impression ?",
            answer: "Vérifiez que l'imprimante est bien connectée au réseau. Si le problème persiste, redémarrez l'application ou contactez le support technique."
        },
        {
            id: 4,
            question: "Comment exporter les rapports ?",
            answer: "Dans la section Rapports, sélectionnez la période souhaitée, puis cliquez sur le bouton 'Exporter PDF' ou 'Excel' en haut à droite des graphiques."
        }
    ];

    const filteredFaqs = faqs.filter(f =>
        f.question.toLowerCase().includes(searchTerm.toLowerCase()) ||
        f.answer.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-4xl mx-auto">
                <div className="text-center mb-12">
                    <h1 className="text-4xl font-extrabold text-gray-900 mb-4">Centre d'Aide</h1>
                    <p className="text-xl text-gray-600">Comment pouvons-nous vous aider aujourd'hui ?</p>

                    <div className="mt-8 max-w-2xl mx-auto relative">
                        <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                        <input
                            type="text"
                            placeholder="Rechercher une réponse..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-12 pr-4 py-4 rounded-full shadow-lg border-2 border-transparent focus:border-indigo-500 focus:outline-none transition text-lg"
                        />
                    </div>
                </div>

                <div className="grid md:grid-cols-3 gap-6 mb-12">
                    {/* Documentation Card */}
                    <div className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition cursor-pointer border border-gray-100">
                        <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-lg flex items-center justify-center mb-4">
                            <Book size={24} />
                        </div>
                        <h3 className="text-lg font-bold text-gray-900 mb-2">Guides Utilisateur</h3>
                        <p className="text-gray-600 text-sm">Documentation détaillée pour chaque fonctionnalité de la plateforme.</p>
                        <a href="#" className="mt-4 inline-flex items-center text-blue-600 text-sm font-medium hover:underline">
                            Voir les guides <ChevronRight size={16} />
                        </a>
                    </div>

                    {/* Tutorials Card */}
                    <div className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition cursor-pointer border border-gray-100">
                        <div className="w-12 h-12 bg-red-100 text-red-600 rounded-lg flex items-center justify-center mb-4">
                            <Video size={24} />
                        </div>
                        <h3 className="text-lg font-bold text-gray-900 mb-2">Tutoriels Vidéo</h3>
                        <p className="text-gray-600 text-sm">Vidéos courtes pour apprendre à utiliser l'application rapidement.</p>
                        <a href="#" className="mt-4 inline-flex items-center text-red-600 text-sm font-medium hover:underline">
                            Regarder <ChevronRight size={16} />
                        </a>
                    </div>

                    {/* Support Card */}
                    <div className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition cursor-pointer border border-gray-100">
                        <div className="w-12 h-12 bg-green-100 text-green-600 rounded-lg flex items-center justify-center mb-4">
                            <HelpCircle size={24} />
                        </div>
                        <h3 className="text-lg font-bold text-gray-900 mb-2">Support Technique</h3>
                        <p className="text-gray-600 text-sm">Une équipe dédiée disponible 24/7 pour résoudre vos problèmes.</p>
                        <a href="#" className="mt-4 inline-flex items-center text-green-600 text-sm font-medium hover:underline">
                            Contacter <ChevronRight size={16} />
                        </a>
                    </div>
                </div>

                {/* FAQ Section */}
                <div className="bg-white rounded-2xl shadow-sm p-8">
                    <h2 className="text-2xl font-bold text-gray-900 mb-6">Questions Fréquentes</h2>
                    <div className="space-y-4">
                        {filteredFaqs.length > 0 ? (
                            filteredFaqs.map((faq) => (
                                <div
                                    key={faq.id}
                                    className="border border-gray-200 rounded-lg overflow-hidden"
                                >
                                    <button
                                        onClick={() => setExpandedFaq(expandedFaq === faq.id ? null : faq.id)}
                                        className="w-full px-6 py-4 text-left bg-gray-50 hover:bg-gray-100 transition flex items-center justify-between font-medium text-gray-900"
                                    >
                                        {faq.question}
                                        {expandedFaq === faq.id ? <ChevronUp size={20} className="text-gray-500" /> : <ChevronDown size={20} className="text-gray-500" />}
                                    </button>
                                    {expandedFaq === faq.id && (
                                        <div className="px-6 py-4 bg-white text-gray-600 leading-relaxed border-t border-gray-100">
                                            {faq.answer}
                                        </div>
                                    )}
                                </div>
                            ))
                        ) : (
                            <div className="text-center py-8 text-gray-500">
                                Aucune question trouvée pour "{searchTerm}"
                            </div>
                        )}
                    </div>
                </div>

                {/* Contact Footer */}
                <div className="mt-12 text-center">
                    <p className="text-gray-600 mb-4">Vous ne trouvez pas ce que vous cherchez ?</p>
                    <div className="flex justify-center gap-4">
                        <button className="flex items-center gap-2 px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition shadow-sm font-medium">
                            <Mail size={18} /> Envoyer un email
                        </button>
                        <button className="flex items-center gap-2 px-6 py-3 bg-white text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition font-medium">
                            <Phone size={18} /> 01 23 45 67 89
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

const ChevronRight = ({ size }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <polyline points="9 18 15 12 9 6"></polyline>
    </svg>
);

export default Help;
