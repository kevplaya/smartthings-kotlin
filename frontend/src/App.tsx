import { ThemeProvider, createTheme } from '@mui/material/styles'
import CssBaseline from '@mui/material/CssBaseline'
import { Container, Typography } from '@mui/material'
import DevicesPage from './pages/DevicesPage'

const theme = createTheme()

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container maxWidth="lg" sx={{ py: 2 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          SmartThings
        </Typography>
        <DevicesPage />
      </Container>
    </ThemeProvider>
  )
}

export default App
