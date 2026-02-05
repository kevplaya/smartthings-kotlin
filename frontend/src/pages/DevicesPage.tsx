import { useEffect, useState } from 'react'
import {
  Alert,
  Box,
  CircularProgress,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material'
import { fetchDevices } from '../api/devices'
import type { Device } from '../types/device'

export default function DevicesPage() {
  const [devices, setDevices] = useState<Device[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)
    fetchDevices()
      .then((data) => {
        if (!cancelled) setDevices(data)
      })
      .catch((e) => {
        if (!cancelled) setError(e instanceof Error ? e.message : 'Unknown error')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="40vh">
        <CircularProgress />
      </Box>
    )
  }

  if (error) {
    return (
      <Box p={2}>
        <Alert severity="error">{error}</Alert>
      </Box>
    )
  }

  return (
    <Box p={2}>
      <Typography variant="h5" gutterBottom>
        Devices
      </Typography>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Device ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Label</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Manufacturer</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {devices.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  No devices
                </TableCell>
              </TableRow>
            ) : (
              devices.map((d) => (
                <TableRow key={d.deviceId}>
                  <TableCell>{d.deviceId}</TableCell>
                  <TableCell>{d.name}</TableCell>
                  <TableCell>{d.label}</TableCell>
                  <TableCell>{d.typeName ?? d.type ?? '-'}</TableCell>
                  <TableCell>{d.manufacturerCode ?? '-'}</TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  )
}
