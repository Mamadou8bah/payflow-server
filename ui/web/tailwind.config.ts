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
        'accent-2': '#f97316',
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
