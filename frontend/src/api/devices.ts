import type { Device } from '../types/device'

const baseUrl = import.meta.env.VITE_API_URL ?? ''

export async function fetchDevices(): Promise<Device[]> {
  const res = await fetch(`${baseUrl}/api/devices`, {
    credentials: 'include',
  })
  if (!res.ok) {
    throw new Error(res.statusText || 'Failed to fetch devices')
  }
  return res.json()
}
