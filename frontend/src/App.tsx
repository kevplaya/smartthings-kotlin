import { useEffect, useState } from 'react'
import { ThemeProvider, createTheme } from '@mui/material/styles'
import CssBaseline from '@mui/material/CssBaseline'
import { Box, Button, Container, Typography } from '@mui/material'
import { fetchMe } from './api/user'
import { logout } from './api/auth'
import DevicesPage from './pages/DevicesPage'
import LoginPage from './pages/LoginPage'
import OAuthCallbackPage from './pages/OAuthCallbackPage'

const theme = createTheme()

function App() {
  const [loading, setLoading] = useState(true)
  const [userId, setUserId] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    fetchMe()
      .then((me) => {
        if (!cancelled) setUserId(me?.id ?? null)
      })
      .catch(() => {
        if (!cancelled) setUserId(null)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  if (window.location.pathname === '/oauth/callback') {
    return (
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Container maxWidth="lg" sx={{ py: 2 }}>
          <OAuthCallbackPage />
        </Container>
      </ThemeProvider>
    )
  }

  if (loading) {
    return (
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="40vh">
          <Typography>Loading…</Typography>
        </Box>
      </ThemeProvider>
    )
  }

  if (!userId) {
    return (
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Container maxWidth="lg" sx={{ py: 2 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            SmartThings
          </Typography>
          <LoginPage />
        </Container>
      </ThemeProvider>
    )
  }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container maxWidth="lg" sx={{ py: 2 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <Typography variant="h4" component="h1">
            SmartThings
          </Typography>
          <Button variant="outlined" size="small" onClick={() => logout()}>
            Logout
          </Button>
        </Box>
        <DevicesPage />
      </Container>
    </ThemeProvider>
  )
}

export default App
