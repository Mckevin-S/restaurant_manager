import React from 'react';
import { Button, Checkbox, Field, Label, Input } from '@headlessui/react';
import image1 from '../../images/cx.jpg';

const Login = () => {
  return (
    // Utilisation de flex au lieu de justify-items pour un meilleur centrage
    <div className="flex min-h-screen w-full items-center justify-center bg-slate-50 p-4">
      
      {/* Container principal avec débordement caché pour l'arrondi de l'image */}
      <div className="flex h-full max-h-[700px] w-full max-w-4xl overflow-hidden rounded-2xl bg-white shadow-2xl">
        
        {/* Section Image - Cachée sur mobile pour le responsive */}
        <div className="hidden w-1/2 lg:block">
          <img 
            src={image1} 
            alt="Authentication background" 
            className="h-full w-full object-cover" 
          />
        </div>

        {/* Section Formulaire */}
        <div className="flex w-full flex-col justify-center p-8 sm:p-12 lg:w-1/2">
          <div className="mb-10 text-center">
            <h1 className="text-3xl font-extrabold tracking-tight text-slate-900">
              Bienvenue
            </h1>
            <p className="mt-2 text-sm text-slate-500">
              Veuillez vous connecter à votre compte
            </p>
          </div>

          <form action="#" method="POST" className="space-y-6">
            {/* Champ Email */}
            <Field className="flex flex-col gap-2">
              <Label className="text-sm font-medium text-slate-700">Email</Label>
              <Input
                type="email"
                placeholder="nom@exemple.com"
                className="w-full rounded-lg border border-slate-200 p-2.5 text-sm outline-none transition focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
              />
            </Field>

            {/* Champ Password */}
            <Field className="flex flex-col gap-2">
              <Label className="text-sm font-medium text-slate-700">Mot de passe</Label>
              <Input
                type="password"
                placeholder="••••••••"
                className="w-full rounded-lg border border-slate-200 p-2.5 text-sm outline-none transition focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
              />
            </Field>

            {/* Options Supplémentaires */}
            <div className="flex items-center justify-between">
              <Field className="flex items-center gap-2">
                <Checkbox
                  className="group block size-4 rounded border border-slate-300 bg-white data-[checked]:bg-indigo-600"
                >
                  {/* Icône de check (optionnelle) */}
                  <svg className="stroke-white opacity-0 group-data-[checked]:opacity-100" viewBox="0 0 14 14" fill="none">
                    <path d="M3 8L6 11L11 3.5" strokeWidth={2} strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </Checkbox>
                <Label className="text-sm text-slate-600 cursor-pointer">Se souvenir de moi</Label>
              </Field>
              <a href="#" className="text-sm font-medium text-indigo-600 hover:text-indigo-500">
                Oublié ?
              </a>
            </div>

            {/* Bouton de Connexion */}
            <Button
              type="submit"
              className="w-full rounded-lg bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white shadow-md transition hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            >
              Se connecter
            </Button>
          </form>

          {/* Footer du formulaire */}
          <p className="mt-8 text-center text-sm text-slate-500">
            Pas encore de compte ?{' '}
            <a href="#" className="font-semibold text-indigo-600 hover:underline">
              S'inscrire
            </a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
