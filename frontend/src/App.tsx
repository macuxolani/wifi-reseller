import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import './App.css'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'
const pages = ['Dashboard', 'Customers', 'Vouchers', 'Packages & Speeds', 'Payments', 'Hotspots', 'Active Sessions', 'Reports', 'Network Settings', 'Support', 'Admin Users', 'Audit Logs']
const descriptions: Record<string, string> = {
  Dashboard: 'Operations summary across every connected hotspot.', Customers: 'Customer accounts and central balance activity.', Vouchers: 'Issue, redeem, and track prepaid access codes.', 'Packages & Speeds': 'Manage products your customers can buy.', Payments: 'Payment attempts and provider events.', Hotspots: 'Connect routers, captive portals, and RADIUS endpoints.', 'Active Sessions': 'Live customer sessions across the network.', Reports: 'Export operational and revenue data.', 'Network Settings': 'RADIUS, portal, router, and alert configuration.', Support: 'Customer issues and operational follow-up.', 'Admin Users': 'Operator access and account permissions.', 'Audit Logs': 'Traceable changes across the control centre.',
}
const records: Record<string, string[][]> = {
  Customers: [['Nandi Mokoena', 'nandi@example.com', '420 min', 'Active'], ['Aiden Khumalo', 'aiden@example.com', '86 min', 'Active'], ['Mpho Langa', 'mpho@example.com', '0 min', 'Suspended']],
  Vouchers: [['VCH-APR-001', '24-hour access', 'Unused', '18 Sep 2026'], ['VCH-APR-002', '7-day access', 'Redeemed', '17 Sep 2026']],
  Payments: [['PAY-10482', 'Nandi Mokoena', 'R 49.00', 'Completed'], ['PAY-10481', 'Aiden Khumalo', 'R 19.00', 'Failed']],
  'Active Sessions': [['Nandi Mokoena', 'Sandton Office', '25 Mbps', '1h 12m'], ['Aiden Khumalo', 'Johannesburg CBD', '10 Mbps', '43m']],
  Support: [['#SUP-084', 'Cannot redeem voucher', 'Nandi Mokoena', 'Open'], ['#SUP-083', 'Slow connection', 'Aiden Khumalo', 'In progress']],
}

type Hotspot = { id: string; name: string; code: string; locationName?: string; status: string; currentUsers?: number; maxUsers?: number; radiusServer?: string }
type Guide = { hotspotId: string; hotspotCode: string; radiusServer: string; radiusAuthenticationPort: number; radiusAccountingPort: number; sharedSecretEnvironmentVariable: string; captivePortalUrl: string; status: string }
type AuthResponse = { accessToken: string; refreshToken: string; username: string; roles: string[] }
type Admin = { id: string; username: string; email: string; status: string; roles: string[] }

function App() {
  const [page, setPage] = useState('Dashboard')
  const [hotspots, setHotspots] = useState<Hotspot[]>([])
  const [search, setSearch] = useState('')
  const [notice, setNotice] = useState('')
  const [loading, setLoading] = useState(false)
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [guide, setGuide] = useState<Guide | null>(null)
  const [token, setToken] = useState(() => localStorage.getItem('yourwifi_access_token') ?? '')
  const [roles, setRoles] = useState<string[]>(() => JSON.parse(localStorage.getItem('yourwifi_roles') ?? '[]'))

  const logout = () => {
    localStorage.removeItem('yourwifi_access_token')
    localStorage.removeItem('yourwifi_refresh_token')
    localStorage.removeItem('yourwifi_roles')
    setToken('')
    setRoles([])
  }

  if (!token) return <LoginScreen onAuthenticated={(auth) => {
    localStorage.setItem('yourwifi_access_token', auth.accessToken)
    localStorage.setItem('yourwifi_refresh_token', auth.refreshToken)
    localStorage.setItem('yourwifi_roles', JSON.stringify(auth.roles))
    setToken(auth.accessToken)
    setRoles(auth.roles)
  }} />

  const isOperator = roles.some((role) => ['SUPER_ADMIN', 'NETWORK_ADMIN', 'SUPPORT_AGENT', 'FINANCE_ADMIN', 'REPORT_VIEWER'].includes(role))
  const visiblePages = isOperator ? pages : ['Dashboard', 'Active Sessions']

  const refreshHotspots = async () => {
    setLoading(true)
    try {
      const response = await fetch(`${apiBaseUrl}/api/hotspots/available`)
      if (!response.ok) throw new Error()
      setHotspots(await response.json())
      setNotice('Hotspot data refreshed')
    } catch { setNotice('The API is unavailable. Check the backend health endpoint.') } finally { setLoading(false) }
  }
  useEffect(() => { void refreshHotspots() }, [])
  const visibleHotspots = useMemo(() => hotspots.filter((item) => `${item.name} ${item.code} ${item.locationName ?? ''}`.toLowerCase().includes(search.toLowerCase())), [hotspots, search])
  const notify = (message: string) => { setNotice(message); window.setTimeout(() => setNotice(''), 3500) }
  const saveToken = () => { const value = window.prompt('Paste an operator access token:', token); if (value !== null) { localStorage.setItem('yourwifi_access_token', value.trim()); setToken(value.trim()); notify('Access token saved in this browser') } }
  const exportData = () => { const data = page === 'Hotspots' ? visibleHotspots.map((item) => [item.name, item.code, item.locationName ?? '', item.status]) : records[page] ?? []; const csv = data.map((row) => Array.isArray(row) ? row.join(',') : Object.values(row).join(',')).join('\n'); const link = document.createElement('a'); link.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv' })); link.download = `${page.toLowerCase().replaceAll(' ', '-')}.csv`; link.click(); URL.revokeObjectURL(link.href); notify(`${page} export downloaded`) }

  return <div className="app-shell">
    <aside className="sidebar"><div className="brand"><div className="brand-mark">Y</div><div><strong>YourWiFi</strong><small>Control Centre</small></div></div><nav aria-label="Main navigation"><p className="nav-header">Operations</p>{visiblePages.map((item) => <button key={item} className={`nav-item ${item === page ? 'active' : ''}`} onClick={() => setPage(item)}>{item}</button>)}</nav><div className="sidebar-footer"><button className="token-link" onClick={saveToken}>Update token</button><button className="logout-btn" onClick={logout}>Sign out</button><small>{roles.length ? roles.join(' / ') : 'Customer account'}</small></div></aside>
    <main className="main-panel"><header className="topbar"><div><p className="eyebrow">YourWiFi / Operations</p><h1>{page}</h1><p className="page-description">{descriptions[page]}</p></div><div className="topbar-actions"><button className="ghost-btn" onClick={exportData}>Export</button><button className="primary-btn" onClick={() => page === 'Hotspots' ? setDrawerOpen(true) : notify(`${page} action queued`)}>+ Quick action</button></div></header>{notice && <div className="toast" role="status">{notice}</div>}{page === 'Dashboard' && <Dashboard hotspots={hotspots} onOpen={() => isOperator ? setPage('Hotspots') : notify('Hotspot management is available to operators only')} />}{page === 'Hotspots' && <Hotspots hotspots={visibleHotspots} search={search} loading={loading} onSearch={setSearch} onRefresh={() => void refreshHotspots()} onConnect={() => setDrawerOpen(true)} onGuide={setGuide} />}{page === 'Admin Users' && <AdminPage token={token} canManage={roles.includes('SUPER_ADMIN')} onNotice={notify} />}{page !== 'Dashboard' && page !== 'Hotspots' && page !== 'Admin Users' && <DataPage page={page} search={search} onSearch={setSearch} onAction={() => notify(`${page} action queued`)} />}</main>{drawerOpen && <ProvisionDrawer token={token} onClose={() => setDrawerOpen(false)} onSaved={(newGuide) => { setGuide(newGuide); setDrawerOpen(false); void refreshHotspots() }} onNotice={notify} />}{guide && <GuideDialog guide={guide} onClose={() => setGuide(null)} />}</div>
}

function LoginScreen({ onAuthenticated }: { onAuthenticated: (auth: AuthResponse) => void }) {
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const submit = async (event: FormEvent) => {
    event.preventDefault()
    setSubmitting(true)
    setError('')
    try {
      const response = await fetch(`${apiBaseUrl}/api/auth/admin/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      })
      if (!response.ok) throw new Error('Invalid username or password.')
      onAuthenticated(await response.json())
    } catch (loginError) {
      setError(loginError instanceof Error ? loginError.message : 'Login failed.')
    } finally {
      setSubmitting(false)
    }
  }

  return <main className="login-shell"><section className="login-card"><div className="brand login-brand"><div className="brand-mark">Y</div><div><strong>YourWiFi</strong><small>Control Centre</small></div></div><p className="eyebrow">Secure admin access</p><h1>Admin login</h1><p className="muted">This console is for authorized YourWiFi administrators only. Customers use the captive portal to sign in.</p><form className="form-grid" onSubmit={submit}><label>Username<input value={username} onChange={(event) => setUsername(event.target.value)} required autoComplete="username" /></label><label>Password<input type="password" value={password} onChange={(event) => setPassword(event.target.value)} required autoComplete="current-password" /></label>{error && <p className="form-error" role="alert">{error}</p>}<button className="primary-btn login-btn" disabled={submitting}>{submitting ? 'Signing in...' : 'Sign in as admin'}</button></form></section></main>
}

function Dashboard({ hotspots, onOpen }: { hotspots: Hotspot[]; onOpen: () => void }) { return <><section className="stats-grid">{[['Connected hotspots', hotspots.length || '0', 'Live registry'], ['Online users', '1,248', '+12.5% today'], ['Customers', '8,420', '+3.2% this month'], ["Today's revenue", 'R 42,560', '+9.4% today'], ['Data usage', '1.8 TB', '+14.0% today']].map(([label, value, trend]) => <article className="stat-card" key={label}><p>{label}</p><h2>{value}</h2><span>{trend}</span></article>)}</section><section className="content-grid"><div className="panel panel-wide"><div className="panel-header"><h3>Hotspot overview</h3><button className="text-btn" onClick={onOpen}>Manage hotspots</button></div>{hotspots.length ? <HotspotTable hotspots={hotspots} onGuide={() => undefined} /> : <EmptyState title="No hotspots connected" action="Connect a hotspot" onAction={onOpen} />}</div><div className="panel"><div className="panel-header"><h3>System status</h3><span className="chip blue">Healthy</span></div><div className="mini-stack"><div><span>RADIUS</span><strong>Ready</strong></div><div><span>Database</span><strong>Nominal</strong></div><div><span>API</span><strong>Operational</strong></div><div><span>Portal</span><strong>Online</strong></div></div></div></section><section className="content-grid lower-grid"><div className="panel"><div className="panel-header"><h3>Active sessions</h3><span className="chip neutral">Live</span></div><ul className="session-list">{[['Nandi Mokoena', 'Sandton Office', '25 Mbps', '1h 12m'], ['Aiden Khumalo', 'Johannesburg CBD', '10 Mbps', '43m']].map((row) => <li key={row[0]}><div><strong>{row[0]}</strong><small>{row[1]}</small></div><div><small>{row[2]}</small><strong>{row[3]}</strong></div><span className="status active">Active</span></li>)}</ul></div><div className="panel"><div className="panel-header"><h3>Recent activity</h3><span className="chip purple">Today</span></div><ul className="activity-list">{['New voucher batch generated', 'Hotspot bandwidth threshold exceeded', 'Customer purchased a 24-hour package', 'RADIUS health alert cleared'].map((item) => <li key={item}>{item}</li>)}</ul></div></section></> }

function Hotspots({ hotspots, search, loading, onSearch, onRefresh, onConnect, onGuide }: { hotspots: Hotspot[]; search: string; loading: boolean; onSearch: (value: string) => void; onRefresh: () => void; onConnect: () => void; onGuide: (guide: Guide) => void }) { return <section className="panel page-panel"><div className="panel-header"><div><h3>Registered hotspots</h3><p className="muted">Each router is an endpoint. Customer balances remain central.</p></div><button className="primary-btn" onClick={onConnect}>Connect hotspot</button></div><div className="toolbar"><input aria-label="Search hotspots" placeholder="Search name, code, or location" value={search} onChange={(event) => onSearch(event.target.value)} /><button className="ghost-btn" onClick={onRefresh}>{loading ? 'Refreshing...' : 'Refresh'}</button></div>{hotspots.length ? <HotspotTable hotspots={hotspots} onGuide={onGuide} /> : <EmptyState title="No matching hotspots" action="Register the first hotspot" onAction={onConnect} />}</section> }
function HotspotTable({ hotspots, onGuide }: { hotspots: Hotspot[]; onGuide: (guide: Guide) => void }) { return <div className="table-wrap"><table><thead><tr><th>Name</th><th>Location</th><th>Code</th><th>Users</th><th>Status</th><th>Action</th></tr></thead><tbody>{hotspots.map((item) => <tr key={item.id}><td><strong>{item.name}</strong></td><td>{item.locationName ?? 'Not set'}</td><td><code>{item.code}</code></td><td>{item.currentUsers ?? 0}{item.maxUsers ? ` / ${item.maxUsers}` : ''}</td><td><span className={`status ${item.status.toLowerCase()}`}>{item.status}</span></td><td><button className="text-btn" onClick={() => onGuide({ hotspotId: item.id, hotspotCode: item.code, radiusServer: item.radiusServer ?? 'Configured server', radiusAuthenticationPort: 1812, radiusAccountingPort: 1813, sharedSecretEnvironmentVariable: 'RADIUS_SHARED_SECRET', captivePortalUrl: window.location.origin, status: item.status })}>Connection guide</button></td></tr>)}</tbody></table></div> }
function AdminPage({ token, canManage, onNotice }: { token: string; canManage: boolean; onNotice: (message: string) => void }) {
  const [admins, setAdmins] = useState<Admin[]>([])
  const [loading, setLoading] = useState(true)
  const load = async () => { try { const response = await fetch(`${apiBaseUrl}/api/admins`, { headers: { Authorization: `Bearer ${token}` } }); if (!response.ok) throw new Error('Admin list unavailable'); setAdmins(await response.json()) } catch (error) { onNotice(error instanceof Error ? error.message : 'Admin list unavailable') } finally { setLoading(false) } }
  useEffect(() => { void load() }, [])
  const addAdmin = async () => { const username = window.prompt('Admin username:'); const email = username ? window.prompt('Admin email:') : null; const password = email ? window.prompt('Temporary password:') : null; if (!username || !email || !password) return; const response = await fetch(`${apiBaseUrl}/api/admins`, { method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` }, body: JSON.stringify({ username, email, password, roles: ['SUPPORT_AGENT'] }) }); if (!response.ok) { onNotice('Could not create admin'); return } onNotice('Admin created with SUPPORT_AGENT role'); void load() }
  const removeAdmin = async (id: string) => { if (!window.confirm('Remove this admin account?')) return; const response = await fetch(`${apiBaseUrl}/api/admins/${id}`, { method: 'DELETE', headers: { Authorization: `Bearer ${token}` } }); if (!response.ok) { onNotice('Could not remove admin'); return } onNotice('Admin removed'); void load() }
  return <section className="panel page-panel"><div className="panel-header"><div><h3>Admin users</h3><p className="muted">Only SUPER_ADMIN can add or remove administrators.</p></div>{canManage && <button className="primary-btn" onClick={() => void addAdmin()}>Add admin</button>}</div>{loading ? <p className="muted">Loading administrators...</p> : <div className="table-wrap"><table><thead><tr><th>Username</th><th>Email</th><th>Roles</th><th>Status</th><th>Action</th></tr></thead><tbody>{admins.map((admin) => <tr key={admin.id}><td><strong>{admin.username}</strong></td><td>{admin.email}</td><td>{admin.roles.join(', ')}</td><td><span className="status active">{admin.status}</span></td><td>{canManage && admin.username !== 'admin' && <button className="text-btn danger-btn" onClick={() => void removeAdmin(admin.id)}>Remove</button>}</td></tr>)}</tbody></table></div>}</section>
}
function DataPage({ page, search, onSearch, onAction }: { page: string; search: string; onSearch: (value: string) => void; onAction: () => void }) { const rows = records[page] ?? []; return <section className="panel page-panel"><div className="panel-header"><div><h3>{page}</h3><p className="muted">This view is ready for live data from the corresponding API.</p></div><button className="primary-btn" onClick={onAction}>Create new</button></div><div className="toolbar"><input aria-label={`Search ${page}`} placeholder={`Search ${page.toLowerCase()}`} value={search} onChange={(event) => onSearch(event.target.value)} /><button className="ghost-btn" onClick={() => onSearch('')}>Clear</button></div>{rows.length ? <div className="table-wrap"><table><thead><tr>{rows[0].map((_, index) => <th key={index}>{['Record', 'Details', 'Value', 'Status'][index] ?? 'Details'}</th>)}</tr></thead><tbody>{rows.filter((row) => row.join(' ').toLowerCase().includes(search.toLowerCase())).map((row) => <tr key={row[0]}>{row.map((cell, index) => <td key={cell}>{index === 0 ? <strong>{cell}</strong> : index === row.length - 1 ? <span className={`status ${cell.toLowerCase().replaceAll(' ', '-')}`}>{cell}</span> : cell}</td>)}</tr>)}</tbody></table></div> : <EmptyState title="No records yet" action="Create a record" onAction={onAction} />}</section> }
function ProvisionDrawer({ token, onClose, onSaved, onNotice }: { token: string; onClose: () => void; onSaved: (guide: Guide) => void; onNotice: (message: string) => void }) { const [form, setForm] = useState({ name: '', code: '', locationName: '', address: '', routerIp: '', routerApiPort: '8728', maxUsers: '100' }); const [saving, setSaving] = useState(false); const submit = async (event: FormEvent) => { event.preventDefault(); setSaving(true); try { const response = await fetch(`${apiBaseUrl}/api/hotspots`, { method: 'POST', headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) }, body: JSON.stringify({ ...form, routerApiPort: Number(form.routerApiPort), maxUsers: Number(form.maxUsers) }) }); if (response.status === 401 || response.status === 403) throw new Error('Set a valid operator access token first.'); if (!response.ok) throw new Error('Hotspot could not be registered.'); onSaved(await response.json()) } catch (error) { onNotice(error instanceof Error ? error.message : 'Hotspot could not be registered.') } finally { setSaving(false) } }; return <div className="drawer-backdrop" onClick={onClose}><aside className="drawer" onClick={(event) => event.stopPropagation()}><div className="panel-header"><div><p className="eyebrow">Network setup</p><h2>Connect a hotspot</h2></div><button className="icon-btn" aria-label="Close" onClick={onClose}>×</button></div><p className="muted">Register the router first. The next screen gives you its RADIUS and captive portal settings.</p><form onSubmit={submit} className="form-grid">{[['name', 'Name', 'Sandton Office'], ['code', 'Code', 'SANDTON-01'], ['locationName', 'Location', 'Sandton'], ['address', 'Address', 'Optional'], ['routerIp', 'Router IP', '10.10.0.1'], ['routerApiPort', 'Router API port', '8728'], ['maxUsers', 'Maximum users', '100']].map(([key, label, placeholder]) => <label key={key}>{label}<input required={key === 'name' || key === 'code'} placeholder={placeholder} value={form[key as keyof typeof form]} onChange={(event) => setForm({ ...form, [key]: event.target.value })} /></label>)}<div className="form-actions"><button type="button" className="ghost-btn" onClick={onClose}>Cancel</button><button className="primary-btn" disabled={saving}>{saving ? 'Registering...' : 'Register hotspot'}</button></div></form></aside></div> }
function GuideDialog({ guide, onClose }: { guide: Guide; onClose: () => void }) { const copy = async () => { await navigator.clipboard.writeText(`RADIUS server: ${guide.radiusServer}\nAuth port: ${guide.radiusAuthenticationPort}\nAccounting port: ${guide.radiusAccountingPort}\nCaptive portal: ${guide.captivePortalUrl}\nShared secret: ${guide.sharedSecretEnvironmentVariable}`); onClose() }; return <div className="drawer-backdrop" onClick={onClose}><div className="dialog" onClick={(event) => event.stopPropagation()}><div className="panel-header"><div><p className="eyebrow">{guide.hotspotCode}</p><h2>Connection guide</h2></div><button className="icon-btn" aria-label="Close" onClick={onClose}>×</button></div><div className="guide-list"><div><span>RADIUS server</span><strong>{guide.radiusServer}</strong></div><div><span>Authentication</span><strong>UDP {guide.radiusAuthenticationPort}</strong></div><div><span>Accounting</span><strong>UDP {guide.radiusAccountingPort}</strong></div><div><span>Captive portal</span><strong>{guide.captivePortalUrl}</strong></div><div><span>Secret source</span><strong>{guide.sharedSecretEnvironmentVariable}</strong></div></div><p className="muted">The shared secret is never returned by the API. Store it in your deployment secret manager and configure the same value on the router.</p><div className="form-actions"><button className="ghost-btn" onClick={onClose}>Close</button><button className="primary-btn" onClick={() => void copy()}>Copy settings</button></div></div></div> }
function EmptyState({ title, action, onAction }: { title: string; action: string; onAction: () => void }) { return <div className="empty-state"><strong>{title}</strong><p>There is nothing here yet.</p><button className="primary-btn" onClick={onAction}>{action}</button></div> }

export default App
