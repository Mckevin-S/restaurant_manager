import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip,Paper,Typography } from '@mui/material';

const transactions = [
  { id: '#ORD-8391', table: 'Table 4', amount: '$74.59', status: 'Completed', time: '12:31 PM' },
  { id: '#ORD-8392', table: 'Table 4', amount: '$88.29', status: 'Completed', time: '12:32 PM' },
  { id: '#ORD-8393', table: 'Table 10', amount: '$94.07', status: 'Completed', time: '12:33 PM' },
  { id: '#ORD-8394', table: 'Table 10', amount: '$66.29', status: 'Completed', time: '12:34 PM' },
  { id: '#ORD-8395', table: 'Table 3', amount: '$22.26', status: 'Completed', time: '12:35 PM' },
];

const RecentTransactions = () => (
  <Paper sx={{ p: 3, borderRadius: 4, boxShadow: 'none', border: '1px solid #E0E0E0', mt: 3,width:'100%' }}>
    <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>Recent Transactions</Typography>
    <TableContainer>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>ORDER ID</TableCell>
            <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>TABLE</TableCell>
            <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>AMOUNT</TableCell>
            <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>STATUS</TableCell>
            <TableCell sx={{ color: 'text.secondary', fontWeight: 600, fontSize: '0.75rem' }}>TIME</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {transactions.map((row) => (
            <TableRow key={row.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
              <TableCell sx={{ fontWeight: 500 }}>{row.id}</TableCell>
              <TableCell>{row.table}</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>{row.amount}</TableCell>
              <TableCell>
                <Chip 
                  label={row.status} 
                  size="small" 
                  sx={{ 
                    bgcolor: '#E8F5E9', 
                    color: '#2E7D32', 
                    fontWeight: 'bold',
                    fontSize: '0.7rem' 
                  }} 
                />
              </TableCell>
              <TableCell color="text.secondary">{row.time}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  </Paper>
);

export default RecentTransactions