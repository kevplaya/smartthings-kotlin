export interface MeResponse {
  id: string
}

const baseUrl = import.meta.env.VITE_API_URL ?? ''

export async function fetchMe(): Promise<MeResponse | null> {
  const res = await fetch(`${baseUrl}/api/user/me`, {
    credentials: 'include',
  })
  if (res.status === 401 || !res.ok) {
    return null
  }
  const data = await res.json() as MeResponse
  return data.id ? data : null
}
