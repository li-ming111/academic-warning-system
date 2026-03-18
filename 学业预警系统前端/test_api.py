import requests
import json

url = 'http://localhost:8081/api/ai/chat'
headers = {'Content-Type': 'application/json'}
data = {
    'userId': '1',
    'message': '你是誰'
}

try:
    response = requests.post(url, headers=headers, data=json.dumps(data))
    print(f'Status code: {response.status_code}')
    print(f'Response: {response.text}')
except Exception as e:
    print(f'Error: {e}')