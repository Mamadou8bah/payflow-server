import "./globals.css";

export const metadata = {
  title: "Payflow",
  description: "Payflow web platform for consumers, merchants, admins, and developers"
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
