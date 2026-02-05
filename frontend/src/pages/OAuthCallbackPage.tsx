import { useEffect } from 'react'
import { Box, Typography } from '@mui/material'

export default function OAuthCallbackPage() {
  useEffect(() => {
    const t = setTimeout(() => {
      window.location.href = '/'
    }, 1500)
    return () => clearTimeout(t)
  }, [])

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      minHeight="40vh"
    >
      <Typography variant="h6">Login successful. Redirecting…</Typography>
    </Box>
  )
}
