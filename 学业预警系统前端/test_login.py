import requests
import json

# 测试登录
login_url = 'http://localhost:8081/api/auth/login'
login_data = {
    'username': 'admin',
    'password': '123456'
}

try:
    login_response = requests.post(login_url, json=login_data)
    print(f'Login status code: {login_response.status_code}')
    print(f'Login response: {login_response.text}')
    
    # 如果登录成功，获取token
    if login_response.status_code == 200:
        login_data = login_response.json()
        token = login_data.get('data', {}).get('token')
        user_id = login_data.get('data', {}).get('userId')
        
        if token and user_id:
            print(f'Token: {token}')
            print(f'User ID: {user_id}')
            
            # 测试AI助手API
            ai_url = 'http://localhost:8081/api/ai/chat'
            ai_data = {
                'userId': str(user_id),
                'message': '你是谁'
            }
            headers = {
                'Content-Type': 'application/json',
                'Authorization': f'Bearer {token}'
            }
            
            ai_response = requests.post(ai_url, json=ai_data, headers=headers)
            print(f'AI API status code: {ai_response.status_code}')
            print(f'AI API response: {ai_response.text}')
        else:
            print('Login successful but no token or user_id found')
except Exception as e:
    print(f'Error: {e}')