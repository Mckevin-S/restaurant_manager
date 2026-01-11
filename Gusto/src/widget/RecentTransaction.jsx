import { useState, useEffect } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Paper, Typography, CircularProgress } from '@mui/material';
import apiClient from '../services/apiClient';

const RecentTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const response = await apiClient.get('/commandes');
        // On prend les 5 plus récentes
        const sorted = response.data
          .sort((a, b) => new Date(b.dateHeure) - new Date(a.dateHeure))
          .slice(0, 5);
        setTransactions(sorted);
      } catch (error) {
        console.error('Erreur lors du chargement des transactions', error);
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, []);

  const getStatusStyle = (status) => {
    switch (status) {
      case 'PAYEE':
        return { bgcolor: '#E8F5E9', color: '#2E7D32' };
      case 'EN_PREPARATION':
        return { bgcolor: '#FFF3E0', color: '#EF6C00' };
      case 'EN_ATTENTE':
        return { bgcolor: '#E3F2FD', color: '#1565C0' };
      case 'PRETE':
        return { bgcolor: '#F3E5F5', color: '#7B1FA2' };
      case 'ANNULEE':
        return { bgcolor: '#FFEBEE', color: '#C62828' };
      default:
        return { bgcolor: '#F5F5F5', color: '#757575' };
    }
  };

  if (loading) {
    return (
      <Paper sx={{ p: 3, borderRadius: 4, display: 'flex', justifyContent: 'center', mt: 3 }}>
        <CircularProgress size={24} />
      </Paper>
    );
  }

  return (
    <Paper sx={{ p: 3, borderRadius: 4, boxShadow: 'none', border: '1px solid #E0E0E0', mt: 3, width: '100%' }}>
      <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>Transactions Récentes</Typography>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>ID</TableCell>
              <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>TABLE / TYPE</TableCell>
              <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>MONTANT</TableCell>
              <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>STATUT</TableCell>
              <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>HEURE</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {transactions.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} align="center">Aucune transaction récente</TableCell>
              </TableRow>
            ) : (
              transactions.map((row) => (
                <TableRow key={row.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                  <TableCell sx={{ fontWeight: 500 }}>#{row.id}</TableCell>
                  <TableCell>
                    {row.tableId ? `Table ${row.tableId}` : row.typeCommande}
                  </TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>{(row.total || 0).toLocaleString()} FCFA</TableCell>
                  <TableCell>
                    <Chip
                      label={row.statut}
                      size="small"
                      sx={{
                        ...getStatusStyle(row.statut),
                        fontWeight: 'bold',
                        fontSize: '0.7rem'
                      }}
                    />
                  </TableCell>
                  <TableCell color="text.secondary">
                    {new Date(row.dateHeure).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );
};

export default RecentTransactions;