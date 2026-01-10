import { Link } from 'react-router-dom';
import { Home, ArrowLeft } from 'lucide-react';

const NotFound = () => {
    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-purple-50 flex items-center justify-center p-6">
            <div className="text-center">
                <div className="mb-8">
                    <h1 className="text-9xl font-bold text-indigo-600 mb-4">404</h1>
                    <h2 className="text-3xl font-bold text-gray-900 mb-2">Page non trouvée</h2>
                    <p className="text-gray-600 mb-8">
                        Désolé, la page que vous recherchez n'existe pas ou a été déplacée.
                    </p>
                </div>

                <div className="flex gap-4 justify-center">
                    <Link
                        to="/"
                        className="flex items-center gap-2 bg-indigo-600 text-white px-6 py-3 rounded-lg hover:bg-indigo-700 transition"
                    >
                        <Home size={20} />
                        Retour à l'accueil
                    </Link>
                    <button
                        onClick={() => window.history.back()}
                        className="flex items-center gap-2 bg-gray-200 text-gray-700 px-6 py-3 rounded-lg hover:bg-gray-300 transition"
                    >
                        <ArrowLeft size={20} />
                        Page précédente
                    </button>
                </div>

                <div className="mt-12">
                    <img
                        src="/404-illustration.svg"
                        alt="404"
                        className="mx-auto max-w-md opacity-50"
                        onError={(e) => e.target.style.display = 'none'}
                    />
                </div>
            </div>
        </div>
    );
};

export default NotFound;
