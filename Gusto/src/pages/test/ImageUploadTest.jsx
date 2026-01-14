import React, { useState } from 'react';
import { toast } from 'react-hot-toast';
import apiClient from '../../services/apiClient';

const ImageUploadTest = () => {
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [loading, setLoading] = useState(false);
    const [uploadResult, setUploadResult] = useState(null);

    const handleFileSelect = (e) => {
        const selectedFile = e.target.files[0];
        if (!selectedFile) return;

        // Afficher preview
        const reader = new FileReader();
        reader.onload = (event) => {
            setPreview(event.target.result);
        };
        reader.readAsDataURL(selectedFile);
        setFile(selectedFile);
    };

    const handleUpload = async () => {
        if (!file) {
            toast.error('Veuillez sélectionner une image');
            return;
        }

        setLoading(true);
        try {
            // Créer un plat d'abord
            const platResponse = await apiClient.post('/plats', {
                nom: 'Plat Test - ' + Date.now(),
                description: 'Image de test',
                prix: 5000,
                category: 1,
                disponibilite: true,
                photoUrl: ''
            });

            const platId = platResponse.data.id;
            console.log('Plat créé:', platId);

            // Upload l'image
            const formData = new FormData();
            formData.append('file', file);

            const uploadResponse = await apiClient.post(`/plats/${platId}/upload-image`, formData);
            console.log('Upload response:', uploadResponse.data);
            
            setUploadResult({
                success: true,
                platId,
                photoUrl: uploadResponse.data.photoUrl,
                message: 'Image uploadée avec succès!'
            });
            toast.success('Upload réussi!');
        } catch (error) {
            console.error('Erreur:', error);
            setUploadResult({
                success: false,
                error: error.message,
                details: error.response?.data
            });
            toast.error('Erreur lors de l\'upload');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-8 max-w-2xl mx-auto">
            <h1 className="text-3xl font-bold mb-8">Test Image Upload</h1>

            <div className="bg-white rounded-lg shadow-md p-6">
                <div className="mb-6">
                    <label className="block text-sm font-medium mb-2">Sélectionner une image:</label>
                    <input
                        type="file"
                        accept="image/*"
                        onChange={handleFileSelect}
                        className="block w-full text-sm border rounded-lg p-2"
                    />
                </div>

                {preview && (
                    <div className="mb-6">
                        <p className="text-sm font-medium mb-2">Aperçu:</p>
                        <img src={preview} alt="Preview" className="w-64 h-auto rounded-lg" />
                    </div>
                )}

                <button
                    onClick={handleUpload}
                    disabled={!file || loading}
                    className="bg-indigo-600 text-white px-6 py-2 rounded-lg disabled:opacity-50"
                >
                    {loading ? '⏳ Upload en cours...' : 'Uploader'}
                </button>
            </div>

            {uploadResult && (
                <div className="mt-8 bg-gray-50 rounded-lg p-6">
                    <h2 className="text-xl font-bold mb-4">Résultat:</h2>
                    {uploadResult.success ? (
                        <div className="text-green-600">
                            <p className="mb-2"> {uploadResult.message}</p>
                            <p className="text-sm">Plat ID: {uploadResult.platId}</p>
                            <p className="text-sm">Photo URL: {uploadResult.photoUrl}</p>
                        </div>
                    ) : (
                        <div className="text-red-600">
                            <p className="mb-2"> {uploadResult.error}</p>
                            <pre className="text-xs bg-white p-3 rounded mt-2 overflow-auto">
                                {JSON.stringify(uploadResult.details, null, 2)}
                            </pre>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default ImageUploadTest;
