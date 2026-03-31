const axios = require('axios');

async function testLogin() {
  try {
    const response = await axios.post('http://localhost:8081/api/auth/login', {
      username: 'admin',
      password: 'admin123'
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    console.log('登录成功:', response.data);
  } catch (error) {
    console.error('登录失败:', error.response ? error.response.data : error.message);
  }
}

testLogin();