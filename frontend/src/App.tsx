import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import './App.css'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

type AuthResponse = {
  accessToken: string
  refreshToken: string
  tokenType: string
  username: string
  roles: string[]
}

type CustomerProfile = {
  id: string
  firstName: string
  lastName: string
  phoneNumber: string
  email: string
  username: string
  status: string
  accountCreatedAt: string
  lastLoginAt: string | null
  lastSeenAt: string | null
}

type BalanceResponse = {
  remainingMinutes: number
}

type PackageOption = {
  id: string
  name: string
  durationMinutes: number
  downloadSpeedMbps: number
  uploadSpeedMbps: number
  price: string
  currency: string
  description: string
  status: string
}

const customerTokenKey = 'yourwifi_customer_access_token'
const customerRefreshKey = 'yourwifi_customer_refresh_token'
const customerRolesKey = 'yourwifi_customer_roles'

function App() {
  const [auth, setAuth] = useState<AuthResponse | null>(() => {
    const saved = localStorage.getItem(customerTokenKey)
    if (!saved) return null

    return {
      accessToken: saved,
      refreshToken: localStorage.getItem(customerRefreshKey) ?? '',
      tokenType: 'Bearer',
      username: localStorage.getItem('yourwifi_customer_username') ?? 'customer',
      roles: JSON.parse(localStorage.getItem(customerRolesKey) ?? '[]'),
    }
  })
  const [customerView, setCustomerView] = useState<'login' | 'register' | 'forgot' | 'dashboard'>('login')
  const [profile, setProfile] = useState<CustomerProfile | null>(null)
  const [packages, setPackages] = useState<PackageOption[]>([])
  const [balanceMinutes, setBalanceMinutes] = useState(0)
  const [loading, setLoading] = useState(false)
  const [notice, setNotice] = useState('')
  const [voucherCode, setVoucherCode] = useState('')
  const [paymentPackageId, setPaymentPackageId] = useState<string | null>(null)
  const [paymentAmount, setPaymentAmount] = useState('15.00')

  const clearCustomerAuth = () => {
    localStorage.removeItem(customerTokenKey)
    localStorage.removeItem(customerRefreshKey)
    localStorage.removeItem(customerRolesKey)
    localStorage.removeItem('yourwifi_customer_username')
    setAuth(null)
    setProfile(null)
    setCustomerView('login')
  }

  const fetchCustomerData = async (token: string) => {
    setLoading(true)
    try {
      const [profileResponse, packagesResponse, balanceResponse] = await Promise.all([
        fetch(`${apiBaseUrl}/api/customer/me`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
        fetch(`${apiBaseUrl}/api/packages`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
        fetch(`${apiBaseUrl}/api/customer/me/balance`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
      ])

      if (profileResponse.ok) {
        const customer = (await profileResponse.json()) as CustomerProfile
        setProfile(customer)
      }

      if (packagesResponse.ok) {
        const packageList = (await packagesResponse.json()) as PackageOption[]
        setPackages(packageList)
      }

      if (balanceResponse.ok) {
        const balance = (await balanceResponse.json()) as BalanceResponse
        setBalanceMinutes(balance.remainingMinutes)
      }
    } catch {
      setNotice('Could not load the customer profile or package catalog.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (auth?.accessToken) {
      void fetchCustomerData(auth.accessToken)
    }
  }, [auth?.accessToken])

  const handleCustomerLogin = async (username: string, password: string) => {
    const response = await fetch(`${apiBaseUrl}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    })

    if (!response.ok) {
      throw new Error('Invalid username or password.')
    }

    const payload = (await response.json()) as AuthResponse
    localStorage.setItem(customerTokenKey, payload.accessToken)
    localStorage.setItem(customerRefreshKey, payload.refreshToken)
    localStorage.setItem(customerRolesKey, JSON.stringify(payload.roles ?? ['CUSTOMER']))
    localStorage.setItem('yourwifi_customer_username', payload.username)
    setAuth(payload)
    setCustomerView('dashboard')
  }

  const handleCustomerRegister = async (payload: Record<string, string>) => {
    const response = await fetch(`${apiBaseUrl}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        firstName: payload.firstName,
        lastName: payload.lastName,
        phoneNumber: payload.phoneNumber,
        email: payload.email,
        username: payload.username,
        password: payload.password,
      }),
    })

    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || 'Could not create the customer account.')
    }

    const authResponse = (await response.json()) as AuthResponse
    localStorage.setItem(customerTokenKey, authResponse.accessToken)
    localStorage.setItem(customerRefreshKey, authResponse.refreshToken)
    localStorage.setItem(customerRolesKey, JSON.stringify(authResponse.roles ?? ['CUSTOMER']))
    localStorage.setItem('yourwifi_customer_username', authResponse.username)
    setAuth(authResponse)
    setCustomerView('dashboard')
  }

  const redeemVoucher = async () => {
    if (!voucherCode.trim() || !auth?.accessToken) return

    try {
      const response = await fetch(`${apiBaseUrl}/api/customer/vouchers/redeem`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${auth.accessToken}`,
        },
        body: JSON.stringify({ code: voucherCode.trim(), username: auth.username }),
      })

      if (!response.ok) {
        const message = await response.text()
        throw new Error(message || 'Voucher could not be redeemed.')
      }

      const payload = await response.json()
      setNotice(`Voucher redeemed successfully: ${payload.message ?? 'Your balance was updated.'}`)
      setVoucherCode('')
      void fetchCustomerData(auth.accessToken)
    } catch (error) {
      setNotice(error instanceof Error ? error.message : 'Voucher redemption failed.')
    }
  }

  const processOnlinePayment = async () => {
    if (!paymentPackageId || !auth?.accessToken) return

    try {
      const selectedPackage = packages.find((item) => item.id === paymentPackageId)
      if (!selectedPackage) {
        throw new Error('Please choose a package before paying.')
      }

      const response = await fetch(`${apiBaseUrl}/api/customer/payments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${auth.accessToken}`,
        },
        body: JSON.stringify({
          customerId: null,
          packageId: selectedPackage.id,
          amount: selectedPackage.price,
          currency: selectedPackage.currency,
          provider: 'SIMULATED',
        }),
      })

      if (!response.ok) {
        const message = await response.text()
        throw new Error(message || 'The payment could not be processed.')
      }

      const payload = await response.json()
      setNotice(`Payment initiated. Reference: ${payload.reference}. It will be credited after verification.`)
      setPaymentPackageId(null)
      void fetchCustomerData(auth.accessToken)
    } catch (error) {
      setNotice(error instanceof Error ? error.message : 'Online payment failed.')
    }
  }

  const preferredPackage = useMemo(() => {
    if (!packages.length) return null
    return packages.reduce((best, current) => {
      if (!best) return current
      if (current.price && best.price) {
        return Number(current.price) < Number(best.price) ? current : best
      }
      return best
    }, packages[0])
  }, [packages])

  const connected = false

  if (!auth?.accessToken) {
    return (
      <div className="customer-shell">
        <div className="customer-brand-row">
          <div className="brand-mark">Y</div>
          <div>
            <div className="brand-name">YourWiFi</div>
            <div className="brand-subtitle">Connect anywhere. Pay for what you use.</div>
          </div>
        </div>

        <CustomerAuthCard
          mode={customerView}
          onModeChange={setCustomerView}
          onLogin={handleCustomerLogin}
          onRegister={handleCustomerRegister}
          onForgot={() => setNotice('Password reset is OTP-ready. Connect the backend reset endpoint to complete this flow.')}
        />

        {notice && <div className="toast" role="status">{notice}</div>}
      </div>
    )
  }

  return (
    <div className="portal-shell">
      <header className="portal-header">
        <div>
          <p className="eyebrow">WELCOME TO YOURWIFI</p>
          <h1>{profile ? `Hey ${profile.firstName}` : 'YourWiFi'}</h1>
        </div>
        <button className="logout-btn" onClick={clearCustomerAuth}>Log out</button>
      </header>

      {notice && <div className="toast" role="status">{notice}</div>}

      <main className="portal-main">
        <section className="hero-card">
          <div className="balance-title">WIFI BALANCE</div>
          <div className="balance-value">{formatMinutes(balanceMinutes)}</div>
          <div className="balance-meta">
            <span>Purchased: 2h 00m</span>
            <span>Used: 36m</span>
            <span>Remaining: {formatMinutes(balanceMinutes)}</span>
          </div>
          <div className="action-row">
            <button className="primary-btn" onClick={() => setPaymentPackageId(preferredPackage?.id ?? null)}>TOP UP</button>
            <button className="secondary-btn" onClick={() => setNotice('Use the voucher box below to redeem a code.')}>REDEEM VOUCHER</button>
            <button className="secondary-btn">CONNECT</button>
          </div>
        </section>

        <section className="panel-card">
          <div className="panel-header">
            <h3>TOP UP YOUR WIFI</h3>
            <span className="muted">Voucher or online payment</span>
          </div>
          <div className="topup-grid">
            <div className="voucher-box">
              <label>
                Enter voucher code
                <input value={voucherCode} onChange={(event) => setVoucherCode(event.target.value)} placeholder="ABC123XYZ456" />
              </label>
              <button className="primary-btn" onClick={() => void redeemVoucher()}>REDEEM VOUCHER</button>
            </div>
            <div className="voucher-box">
              <label>
                Package
                <select value={paymentPackageId ?? ''} onChange={(event) => setPaymentPackageId(event.target.value)}>
                  <option value="">Choose a package</option>
                  {packages.map((pkg) => (
                    <option key={pkg.id} value={pkg.id}>{pkg.name} — {formatCurrency(pkg.price, pkg.currency)}</option>
                  ))}
                </select>
              </label>
              <label>
                Amount
                <input value={paymentAmount} onChange={(event) => setPaymentAmount(event.target.value)} />
              </label>
              <button className="primary-btn" onClick={() => void processOnlinePayment()} disabled={!paymentPackageId}>BUY ONLINE</button>
            </div>
          </div>
        </section>

        <section className="info-grid">
          <article className="panel-card session-card">
            <div className="panel-header">
              <h3>{connected ? 'CURRENT SESSION' : 'READY TO CONNECT?'}</h3>
              <span className={connected ? 'status-pill live' : 'status-pill idle'}>{connected ? 'CONNECTED' : 'ONLINE'}</span>
            </div>
            {connected ? (
              <div className="session-stack">
                <div><span>Hotspot</span><strong>YourWiFi — Location 1</strong></div>
                <div><span>Package</span><strong>Premium</strong></div>
                <div><span>Speed</span><strong>25 Mbps</strong></div>
                <div><span>Time remaining</span><strong>52 min</strong></div>
                <button className="danger-btn">DISCONNECT</button>
              </div>
            ) : (
              <div className="session-stack">
                <div><span>Balance</span><strong>{formatMinutes(balanceMinutes)}</strong></div>
                <div><span>Available speed</span><strong>{preferredPackage ? `${preferredPackage.downloadSpeedMbps} Mbps` : '25 Mbps'}</strong></div>
                <div><span>Current hotspot</span><strong>YourWiFi — Location 1</strong></div>
                <button className="primary-btn">CONNECT</button>
              </div>
            )}
          </article>

          <article className="panel-card">
            <div className="panel-header">
              <h3>YOUR ACCOUNT</h3>
            </div>
            <div className="mini-list">
              <div><span>Customer</span><strong>{profile ? `${profile.firstName} ${profile.lastName}` : auth.username}</strong></div>
              <div><span>Email</span><strong>{profile?.email ?? 'customer@example.com'}</strong></div>
              <div><span>Phone</span><strong>{profile?.phoneNumber ?? '+27 00 000 0000'}</strong></div>
              <div><span>Status</span><strong>{profile?.status ?? 'ACTIVE'}</strong></div>
            </div>
          </article>
        </section>

        <section className="panel-card">
          <div className="panel-header">
            <h3>AVAILABLE PACKAGES</h3>
            <span className="muted">Live catalog</span>
          </div>
          <div className="package-grid">
            {loading ? (
              <p className="muted">Loading your packages...</p>
            ) : packages.length ? (
              packages.map((pkg) => (
                <article key={pkg.id} className="package-card">
                  <div className="package-name">{pkg.name}</div>
                  <div className="package-speed">{pkg.downloadSpeedMbps} Mbps</div>
                  <div className="package-meta">{formatDuration(pkg.durationMinutes)} • {pkg.uploadSpeedMbps} Mbps</div>
                  <div className="package-price">{formatCurrency(pkg.price, pkg.currency)}</div>
                  <p>{pkg.description}</p>
                  <button className="primary-btn tiny" onClick={() => {
                    setPaymentPackageId(pkg.id)
                    setPaymentAmount(pkg.price)
                    setNotice(`Selected ${pkg.name}. Ready to purchase.`)
                  }}>BUY</button>
                </article>
              ))
            ) : (
              <div className="empty-state">
                <strong>No packages available right now.</strong>
                <p>Once the backend catalog is active, they will appear here.</p>
              </div>
            )}
          </div>
        </section>
      </main>
    </div>
  )
}

function CustomerAuthCard({
  mode,
  onModeChange,
  onLogin,
  onRegister,
  onForgot,
}: {
  mode: 'login' | 'register' | 'forgot' | 'dashboard'
  onModeChange: (mode: 'login' | 'register' | 'forgot' | 'dashboard') => void
  onLogin: (username: string, password: string) => Promise<void>
  onRegister: (payload: Record<string, string>) => Promise<void>
  onForgot: () => void
}) {
  const [username, setUsername] = useState('janedoe')
  const [password, setPassword] = useState('secret123')
  const [firstName, setFirstName] = useState('Jane')
  const [lastName, setLastName] = useState('Doe')
  const [phoneNumber, setPhoneNumber] = useState('+27720000000')
  const [email, setEmail] = useState('jane@example.com')
  const [confirmPassword, setConfirmPassword] = useState('secret123')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const submitAuth = async (event: FormEvent) => {
    event.preventDefault()
    setSubmitting(true)
    setError('')

    try {
      if (mode === 'register') {
        if (password !== confirmPassword) {
          throw new Error('Passwords do not match.')
        }

        await onRegister({
          firstName,
          lastName,
          phoneNumber,
          email,
          username,
          password,
        })
      } else if (mode === 'forgot') {
        onForgot()
      } else {
        await onLogin(username, password)
      }
    } catch (loginError) {
      setError(loginError instanceof Error ? loginError.message : 'Authentication failed.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="customer-card">
      <div className="auth-tabs">
        <button className={mode === 'login' ? 'selected' : ''} onClick={() => onModeChange('login')}>Login</button>
        <button className={mode === 'register' ? 'selected' : ''} onClick={() => onModeChange('register')}>Register</button>
        <button className={mode === 'forgot' ? 'selected' : ''} onClick={() => onModeChange('forgot')}>Forgot</button>
      </div>

      <h2>{mode === 'login' ? 'Welcome back' : mode === 'register' ? 'Create your account' : 'Reset password'}</h2>
      <p className="form-copy">{mode === 'login' ? 'Sign in to continue with your central WiFi balance.' : mode === 'register' ? 'Set up your account and start using YourWiFi anywhere.' : 'Enter your email or username to begin the secure password reset flow.'}</p>

      <form className="customer-form" onSubmit={submitAuth}>
        {mode === 'register' && (
          <>
            <div className="split-row">
              <label>
                First name
                <input value={firstName} onChange={(event) => setFirstName(event.target.value)} required />
              </label>
              <label>
                Last name
                <input value={lastName} onChange={(event) => setLastName(event.target.value)} required />
              </label>
            </div>
            <div className="split-row">
              <label>
                Phone number
                <input value={phoneNumber} onChange={(event) => setPhoneNumber(event.target.value)} required />
              </label>
              <label>
                Email
                <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required />
              </label>
            </div>
          </>
        )}

        {(mode === 'login' || mode === 'register') && (
          <label>
            Username or email
            <input value={username} onChange={(event) => setUsername(event.target.value)} required />
          </label>
        )}

        {mode === 'forgot' && (
          <label>
            Phone or email
            <input value={username} onChange={(event) => setUsername(event.target.value)} required />
          </label>
        )}

        {mode !== 'forgot' && (
          <label>
            Password
            <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} required />
          </label>
        )}

        {mode === 'register' && (
          <label>
            Confirm password
            <input type="password" value={confirmPassword} onChange={(event) => setConfirmPassword(event.target.value)} required />
          </label>
        )}

        {mode === 'register' && (
          <label className="checkbox-row">
            <input type="checkbox" required />
            <span>I accept the terms and privacy policy.</span>
          </label>
        )}

        {error && <p className="form-error" role="alert">{error}</p>}

        <button type="submit" className="primary-btn auth-submit" disabled={submitting}>
          {submitting ? 'Processing...' : mode === 'login' ? 'LOGIN' : mode === 'register' ? 'REGISTER' : 'RESET PASSWORD'}
        </button>
      </form>
    </section>
  )
}

function formatMinutes(totalMinutes: number) {
  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60
  return `${hours}h ${minutes.toString().padStart(2, '0')}m`
}

function formatDuration(minutes: number) {
  const hours = Math.floor(minutes / 60)
  const remainder = minutes % 60
  if (hours === 0) return `${remainder} min`
  if (remainder === 0) return `${hours}h`
  return `${hours}h ${remainder}m`
}

function formatCurrency(value: string, currency: string) {
  const amount = Number(value)
  return new Intl.NumberFormat('en-ZA', {
    style: 'currency',
    currency: currency || 'ZAR',
    minimumFractionDigits: 2,
  }).format(amount)
}

export default App
