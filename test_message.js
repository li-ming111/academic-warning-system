const axios = require('axios');

// 测试发送消息
async function testSendMessage() {
  try {
    // 直接使用教师ID 28，尝试发送消息
    const response = await axios.post('http://localhost:8081/api/students/messages', {
      studentId: 1, // 尝试使用学生ID 1
      teacherId: 28,
      content: '测试消息',
      type: 'question'
    });
    console.log('发送消息成功:', response.data);
  } catch (error) {
    console.error('发送消息失败:', error.response ? error.response.data : error.message);
  }
}

// 测试教师接收消息
async function testTeacherReceiveMessage() {
  try {
    // 测试教师获取消息列表
    const response = await axios.get('http://localhost:8081/api/teachers/messages/28');
    console.log('教师消息列表:', response.data);
  } catch (error) {
    console.error('获取教师消息失败:', error.response ? error.response.data : error.message);
  }
}

// 运行测试
async function runTests() {
  console.log('=== 测试学生发送消息 ===');
  await testSendMessage();
  
  console.log('\n=== 测试教师接收消息 ===');
  await testTeacherReceiveMessage();
}

runTests();