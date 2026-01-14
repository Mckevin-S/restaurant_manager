import React, { useState, useRef } from 'react';
import { Upload, X, Check } from 'lucide-react';
import { toast } from 'react-hot-toast';

const ImageUploader = ({ onUpload, currentImage, isLoading = false, maxSizeMB = 5 }) => {
  const [preview, setPreview] = useState(currentImage || null);
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef(null);

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const validateFile = (file) => {
    // Check file type
    if (!file.type.startsWith('image/')) {
      toast.error('Le fichier doit être une image');
      return false;
    }

    // Check file size
    const sizeMB = file.size / (1024 * 1024);
    if (sizeMB > maxSizeMB) {
      toast.error(`La taille de l'image ne doit pas dépasser ${maxSizeMB}MB`);
      return false;
    }

    return true;
  };

  const handleFileSelect = async (file) => {
    if (!validateFile(file)) return;

    // Create preview
    const reader = new FileReader();
    reader.onload = (e) => {
      setPreview(e.target.result);
    };
    reader.readAsDataURL(file);

    // Call upload handler
    if (onUpload) {
      await onUpload(file);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);
    const files = e.dataTransfer.files;
    if (files.length > 0) {
      handleFileSelect(files[0]);
    }
  };

  const handleInputChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const clearPreview = () => {
    setPreview(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  return (
    <div className="w-full">
      {!preview ? (
        <div
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          onClick={() => fileInputRef.current?.click()}
          className={`relative rounded-2xl border-2 border-dashed p-8 text-center cursor-pointer transition ${
            isDragging
              ? 'border-indigo-500 bg-indigo-50'
              : 'border-slate-300 bg-slate-50 hover:border-indigo-400 hover:bg-indigo-50'
          }`}
        >
          <Upload className="mx-auto mb-3 text-slate-400" size={32} />
          <p className="font-bold text-slate-700">Cliquez ou glissez une image ici</p>
          <p className="text-xs text-slate-500 mt-1">PNG, JPG, GIF jusqu'à {maxSizeMB}MB</p>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            onChange={handleInputChange}
            className="hidden"
          />
        </div>
      ) : (
        <div className="relative rounded-2xl overflow-hidden bg-slate-100 group">
          <img
            src={preview}
            alt="Prévisualisation"
            className="w-full h-64 object-cover"
            onError={(e) => {
              e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 400"%3E%3Crect fill="%23f1f5f9" width="400" height="400"/%3E%3Ctext x="50%25" y="50%25" font-size="20" fill="%2394a3b8" text-anchor="middle" dominant-baseline="middle"%3EErreur de chargement%3C/text%3E%3C/svg%3E';
            }}
          />
          <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition flex items-center justify-center gap-2">
            <button
              onClick={() => fileInputRef.current?.click()}
              disabled={isLoading}
              className="rounded-full bg-white p-3 text-slate-700 hover:bg-slate-100 transition disabled:opacity-50"
              title="Changer l'image"
            >
              <Upload size={20} />
            </button>
            <button
              onClick={clearPreview}
              disabled={isLoading}
              className="rounded-full bg-white p-3 text-rose-600 hover:bg-slate-100 transition disabled:opacity-50"
              title="Supprimer"
            >
              <X size={20} />
            </button>
          </div>
          {isLoading && (
            <div className="absolute inset-0 bg-black/50 flex items-center justify-center">
              <div className="animate-spin rounded-full h-8 w-8 border-2 border-white border-t-transparent"></div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default ImageUploader;
