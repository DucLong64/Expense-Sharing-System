import { Link } from 'react-router-dom'

interface AppLogoProps {
  /** `long` — header / trang auth; `icon` — favicon hoặc chỗ hẹp */
  variant?: 'long' | 'icon'
  className?: string
  linkTo?: string
}

const logoSrc = {
  long: '/logoLong.svg',
  icon: '/logoLong.svg',
} as const

export function AppLogo({ variant = 'long', className = '', linkTo = '/' }: AppLogoProps) {
  const image = (
    <img
      src={logoSrc[variant]}
      alt="Chia chi"
      className={`object-contain object-left ${variant === 'long' ? 'h-8 w-auto max-w-[10rem] sm:max-w-[12rem]' : 'h-9 w-9'} ${className}`}
    />
  )

  if (!linkTo) {
    return image
  }

  return (
    <Link to={linkTo} className="inline-flex shrink-0 items-center">
      {image}
    </Link>
  )
}
