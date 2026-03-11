const API_GATEWAY_URL = 'https://gateway-production-f43a.up.railway.app';

// ============================================
// API Service functions
// ============================================

const apiService = {
  
  async request(endpoint, options = {}) {
    const url = `${API_GATEWAY_URL}${endpoint}`;
    
    // Add auth header if token exists
    const token = localStorage.getItem('jwt_token');
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };
    
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const config = {
      ...options,
      headers
    };
    
    try {
      console.log(`[API] Fetching ${url}`, config);
      const response = await fetch(url, config);
      
      const isJson = response.headers.get('content-type')?.includes('application/json');
      const data = isJson ? await response.json() : null;
      
      if (!response.ok) {
        throw {
          status: response.status,
          message: data?.message || response.statusText,
          data
        };
      }
      return data;
    } catch (error) {
       console.error(`[API] Error on ${endpoint}:`, error);
       throw error;
    }
  },
  
  // Auth API
  async login(email, password) {
    const res = await this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
    // Store token and user details inside local storage on success
    if (res && res.token) {
       localStorage.setItem('jwt_token', res.token);
       localStorage.setItem('user', JSON.stringify({
          id: res.userId,
          username: res.username,
          email: res.email
       }));
    }
    return res;
  },
  
  async register(username, email, password) {
    const res = await this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, email, password })
    });
    if (res && res.token) {
       localStorage.setItem('jwt_token', res.token);
       localStorage.setItem('user', JSON.stringify({
          id: res.userId,
          username: res.username,
          email: res.email
       }));
    }
    return res;
  },
  
  logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user');
    window.location.href = 'auth.html';
  },

  isAuthenticated() {
      return !!localStorage.getItem('jwt_token');
  },

  getCurrentUser() {
      const user = localStorage.getItem('user');
      return user ? JSON.parse(user) : null;
  },

  // Event API
  async getEvents() {
      return await this.request('/events');
  },

  async getEventById(id) {
      return await this.request(`/events/${id}`);
  },

  // Inscription API
  async createInscription(eventId) {
     const user = this.getCurrentUser();
     if(!user) throw new Error("Not authenticated");

     return await this.request('/inscriptions', {
         method: 'POST',
         body: JSON.stringify({
             userId: user.id,
             eventId: eventId
         })
     });
  },

  async getMyInscriptions() {
     const user = this.getCurrentUser();
     if(!user) throw new Error("Not authenticated");

     return await this.request(`/inscriptions/user/${user.id}`);
  }
};
