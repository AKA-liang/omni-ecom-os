const API_BASE_URL = 'http://localhost:10004/api';

const api = {
  async getBoss() {
    const response = await fetch(`${API_BASE_URL}/boss`);
    if (!response.ok) throw new Error('获取Boss信息失败');
    return response.json();
  },

  async updateBoss(bossData) {
    const response = await fetch(`${API_BASE_URL}/boss`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(bossData),
    });
    if (!response.ok) throw new Error('更新Boss信息失败');
    return response.json();
  },

  async uploadAvatar(file) {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE_URL}/files/upload`, {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) throw new Error('上传头像失败');
    const fileUrl = await response.text();
    
    const bossData = { avatar: fileUrl };
    await this.updateBoss(bossData);
    
    return fileUrl;
  },

  async uploadEmployeeAvatar(file) {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE_URL}/files/upload`, {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) throw new Error('上传头像失败');
    return response.text();
  },

  async getEmployees(category = '') {
    const url = category 
      ? `${API_BASE_URL}/employees?category=${category}`
      : `${API_BASE_URL}/employees`;
    
    const response = await fetch(url);
    if (!response.ok) throw new Error('获取员工列表失败');
    return response.json();
  },

  async createEmployee(employeeData) {
    const response = await fetch(`${API_BASE_URL}/employees`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(employeeData),
    });
    if (!response.ok) throw new Error('创建员工失败');
    return response.json();
  },

  async updateEmployee(id, employeeData) {
    const response = await fetch(`${API_BASE_URL}/employees/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(employeeData),
    });
    if (!response.ok) throw new Error('更新员工失败');
    return response.json();
  },

  async deleteEmployee(id) {
    const response = await fetch(`${API_BASE_URL}/employees/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) throw new Error('删除员工失败');
  },

  async getSiteConfig() {
    const response = await fetch(`${API_BASE_URL}/site-config`);
    if (!response.ok) throw new Error('获取站点配置失败');
    return response.json();
  },

  async updateSiteConfig(configData) {
    const response = await fetch(`${API_BASE_URL}/site-config`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(configData),
    });
    if (!response.ok) throw new Error('更新站点配置失败');
    return response.json();
  },

  getFileUrl(filename) {
    return `${API_BASE_URL}/files/${filename}`;
  },

  async getCategories() {
    const response = await fetch(`${API_BASE_URL}/categories`);
    if (!response.ok) throw new Error('获取分类列表失败');
    return response.json();
  },

  async createCategory(categoryData) {
    const response = await fetch(`${API_BASE_URL}/categories`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(categoryData),
    });
    if (!response.ok) throw new Error('创建分类失败');
    return response.json();
  },

  async updateCategory(id, categoryData) {
    const response = await fetch(`${API_BASE_URL}/categories/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(categoryData),
    });
    if (!response.ok) throw new Error('更新分类失败');
    return response.json();
  },

  async deleteCategory(id) {
    const response = await fetch(`${API_BASE_URL}/categories/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) throw new Error('删除分类失败');
  }
};

export default api;
