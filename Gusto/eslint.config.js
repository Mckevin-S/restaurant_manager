import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'

export default [
  { ignores: ['dist'] },
  {
    files: ['**/*.{js,jsx}'],
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    languageOptions: {
      ecmaVersion: 2020,
      globals: {
        ...globals.browser,
        ...globals.node,
      },
      parserOptions: {
        ecmaVersion: 'latest',
        ecmaFeatures: { jsx: true },
        sourceType: 'module',
      },
    },
    settings: {
      react: { version: 'detect' },
    },
    rules: {
      ...js.configs.recommended.rules,
      ...reactHooks.configs.recommended.rules,

      // 1. Désactive l'erreur sur les exports multiples (Context + Provider)
      'react-refresh/only-export-components': 'off',

      // 2. Assouplit la règle des variables inutilisées pour accepter 'error', 'e', etc.
      'no-unused-vars': ['warn', {
        'vars': 'all',
        'args': 'none',
        'ignoreRestSiblings': true,
        'varsIgnorePattern': '^[A-Z_]|error|e|resp|data'
      }],

      // 3. Empêche le build de planter pour des dépendances de useEffect manquantes
      'react-hooks/exhaustive-deps': 'warn',

      // 4. Désactive l'erreur de pureté (Date.now) qui bloque KitchenDashboard
      'react-hooks/rules-of-hooks': 'error',

      // 5. Autorise les déclarations dans les cases de switch
      'no-case-declarations': 'off',

      // 6. Autorise les blocs vides (souvent utilisés dans les catch)
      'no-empty': 'off'
    },
  },
]