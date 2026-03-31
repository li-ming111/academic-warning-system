package com.academic.controller;

import com.academic.common.ApiResponse;
import com.academic.dto.StudentRegisterRequest;
import com.academic.entity.StudentProfile;
import com.academic.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterSuccess() {
        // 准备测试数据
        StudentRegisterRequest request = new StudentRegisterRequest();
        request.setStudentId("2021010101");
        request.setName("张三");
        request.setPassword("123456");
        request.setPhone("13800138000");
        request.setEmail("zhangsan@example.com");

        // 模拟服务层方法
        // studentService.register 方法无返回值，这里不需要设置返回值

        // 调用控制器方法
        ApiResponse<String> response = studentController.register(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("注册成功", response.getMessage());
    }

    @Test
    public void testRegisterFailure() {
        // 准备测试数据
        StudentRegisterRequest request = new StudentRegisterRequest();
        request.setStudentId("2021010101");
        request.setName("张三");
        request.setPassword("123456");
        request.setPhone("13800138000");
        request.setEmail("zhangsan@example.com");

        // 模拟服务层方法抛出异常
        doThrow(new RuntimeException("注册失败：学号已存在")).when(studentService).register(request);

        // 调用控制器方法
        ApiResponse<String> response = studentController.register(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(500, response.getCode());
        assertEquals("注册失败：学号已存在", response.getMessage());
    }

    @Test
    public void testGetStudentInfoSuccess() {
        // 准备测试数据
        String studentId = "2021010101";
        StudentProfile student = new StudentProfile();
        student.setId(1L);
        student.setStudentId(studentId);
        student.setName("张三");

        // 模拟服务层方法
        when(studentService.getByStudentId(studentId)).thenReturn(student);

        // 调用控制器方法
        ApiResponse<StudentProfile> response = studentController.getStudentInfo(studentId);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals(studentId, response.getData().getStudentId());
        assertEquals("张三", response.getData().getName());
    }

    @Test
    public void testGetStudentInfoNotFound() {
        // 准备测试数据
        String studentId = "2021010101";

        // 模拟服务层方法返回 null
        when(studentService.getByStudentId(studentId)).thenReturn(null);

        // 调用控制器方法
        ApiResponse<StudentProfile> response = studentController.getStudentInfo(studentId);

        // 验证结果
        assertNotNull(response);
        assertEquals(404, response.getCode());
        assertEquals("学生不存在", response.getMessage());
    }
}
