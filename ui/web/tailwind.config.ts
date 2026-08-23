import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './app/**/*.{js,ts,jsx,tsx,mdx}',
    './components/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        'dark': '#050b14',
        'bg': '#07111f',
        'bg-alt': '#0d1b30',
        'card': 'rgba(13, 27, 48, 0.85)',
        'text': '#eef4ff',
        'muted': '#a8b7d1',
        'accent': '#4fd1c5',
        'accent-2': '#debb30',
        orange: {
          50: '#fbf8e8',
          100: '#f6efc9',
          200: '#edde93',
          300: '#e4cd5d',
          400: '#e2c441',
          500: '#debb30',
          600: '#debb30',
          700: '#b89620',
          800: '#8a6f18',
          900: '#5c4a10',
          950: '#3a2e0a',
        },
      },
      fontFamily: {
        sans: ['Roboto', 'ui-sans-serif', 'system-ui', '-apple-system', 'Segoe UI', 'Helvetica Neue', 'Arial', 'sans-serif'],
        heading: ['Roboto', 'ui-sans-serif', 'system-ui'],
      },
      backgroundColor: {
        'dark': '#050b14',
      },
      borderColor: {
        'light': 'rgba(255, 255, 255, 0.08)',
      },
      backdropFilter: {
        'blur-sm': 'blur(18px)',
      },
    },
  },
  plugins: [],
}

export default config
