const avatarColors = [
  { bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', text: '#ffffff' },
  { bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', text: '#ffffff' },
  { bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', text: '#ffffff' },
  { bg: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', text: '#ffffff' },
  { bg: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)', text: '#ffffff' },
  { bg: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)', text: '#ffffff' },
  { bg: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)', text: '#ffffff' },
  { bg: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)', text: '#ffffff' },
  { bg: 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)', text: '#1d1d1f' },
  { bg: 'linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%)', text: '#1d1d1f' },
  { bg: 'linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)', text: '#1d1d1f' },
  { bg: 'linear-gradient(135deg, #cfd9df 0%, #e2ebf0 100%)', text: '#1d1d1f' }
]

export function getAvatarColor(name) {
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  const index = Math.abs(hash) % avatarColors.length
  return avatarColors[index]
}

export function getInitials(name) {
  if (!name) return '?'
  const firstChar = name.charAt(0)
  if (/[\u4e00-\u9fa5]/.test(firstChar)) {
    return firstChar
  }
  return name.substring(0, 2).toUpperCase()
}

export function getAvatarGradient(name) {
  const color = getAvatarColor(name)
  return {
    background: color.bg,
    color: color.text
  }
}
