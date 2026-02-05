const baseUrl = import.meta.env.VITE_API_URL ?? ''

export async function getAuthorizeUrl(): Promise<string> {
  const res = await fetch(`${baseUrl}/api/oauth/authorize`, {
    credentials: 'include',
  })
  if (!res.ok) {
    throw new Error(res.statusText || 'Failed to get authorize URL')
  }
  const data = await res.json()
  return data.url as string
}

export async function logout(): Promise<void> {
  const res = await fetch(`${baseUrl}/api/oauth/logout`, {
    method: 'POST',
    credentials: 'include',
  })
  if (!res.ok) {
    throw new Error(res.statusText || 'Logout failed')
  }
  if (res.redirected) {
    window.location.href = res.url
  } else {
    window.location.href = '/'
  }
}
