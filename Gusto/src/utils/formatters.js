export function formatStatut(statut) {
  if (!statut) return '';
  const key = normalizeStatutKey(statut);
  switch (key) {
    case 'LIBRE': return 'Libre';
    case 'OCCUPEE': return 'Occupée';
    case 'RESERVEE': return 'Réservée';
    case 'A_NETTOYER': return 'À nettoyer';
    default: return statut.toString().replace(/_/g, ' ');
  }
}

export function normalizeStatutKey(statut) {
  if (!statut) return '';
  return statut.toString()
    .normalize('NFD')
    .replace(/\p{Diacritic}/gu, '')
    .toUpperCase()
    .replace(/\s+/g, '_');
}
