import { useState } from 'react'
import { Box, Button, CircularProgress, Typography } from '@mui/material'
import { getAuthorizeUrl } from '../api/auth'

export default function LoginPage() {
  const [loading, setLoading] = useState(false)

  const handleLogin = async () => {
    setLoading(true)
    try {
      const url = await getAuthorizeUrl()
      window.location.href = url
    } catch (e) {
      setLoading(false)
      console.error(e)
    }
  }

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      minHeight="50vh"
      gap={2}
    >
      <Typography variant="h6">Sign in with SmartThings to continue</Typography>
      <Button
        variant="contained"
        size="large"
        onClick={handleLogin}
        disabled={loading}
        startIcon={loading ? <CircularProgress size={20} color="inherit" /> : null}
      >
        {loading ? 'Redirecting…' : 'Login with SmartThings'}
      </Button>
    </Box>
  )
}
