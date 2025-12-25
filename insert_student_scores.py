#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
为学号 2023020616 的学生插入历年成绩数据
"""

import requests
import json

# API 基础 URL
API_BASE = "http://localhost:8081/api"

# 学生学号
STUDENT_ID = "2023020616"

# 成绩数据 - 按学期分类
SCORES_DATA = {
    "2023-2024-01": [
        {"courseName": "程序设计基础", "scoreTotal": 69, "gradePoint": 1.9},
        {"courseName": "程序设计基础★", "scoreTotal": 60, "gradePoint": 1.0},
        {"courseName": "大学体育I", "scoreTotal": 82, "gradePoint": 3.2},
        {"courseName": "大学英语I", "scoreTotal": 72, "gradePoint": 2.2},
        {"courseName": "认识实习", "scoreTotal": 86, "gradePoint": 3.6},
        {"courseName": "思想道德与法治", "scoreTotal": 60, "gradePoint": 1.0},
        {"courseName": "思想道德与法...", "scoreTotal": 90, "gradePoint": 4.0},
        {"courseName": "专业导论", "scoreTotal": 73, "gradePoint": 2.3},
        {"courseName": "高等数学 (上)", "scoreTotal": 80, "gradePoint": 3.0},
        {"courseName": "大学物理(上)", "scoreTotal": 76, "gradePoint": 2.6},
    ],
    "2023-2024-02": [
        {"courseName": "马克思主义基...", "scoreTotal": 65, "gradePoint": 1.5},
        {"courseName": "大学英语II", "scoreTotal": 64, "gradePoint": 1.4},
        {"courseName": "面向对象程序...", "scoreTotal": 67, "gradePoint": 1.7},
        {"courseName": "大学物理实验", "scoreTotal": 80, "gradePoint": 3.0},
        {"courseName": "数字电路与逻辑", "scoreTotal": 80, "gradePoint": 3.0},
        {"courseName": "大学体育II", "scoreTotal": 77, "gradePoint": 2.7},
        {"courseName": "大学生职业发...", "scoreTotal": 98, "gradePoint": 4.0},
        {"courseName": "面向对象程序...", "scoreTotal": 80, "gradePoint": 3.0},
        {"courseName": "大学物理 (下)", "scoreTotal": 76, "gradePoint": 2.6},
    ],
    "2024-2025-01": [
        {"courseName": "大学英语III", "scoreTotal": 74, "gradePoint": 2.4},
        {"courseName": "线性代数", "scoreTotal": 84, "gradePoint": 3.4},
        {"courseName": "军事技能训练", "scoreTotal": 69, "gradePoint": 1.9},
        {"courseName": "大学生心理健...", "scoreTotal": 85, "gradePoint": 3.5},
        {"courseName": "大学体育III", "scoreTotal": 86, "gradePoint": 3.6},
        {"courseName": "军事理论", "scoreTotal": 77, "gradePoint": 2.7},
        {"courseName": "数据结构与算...", "scoreTotal": 60, "gradePoint": 1.0},
        {"courseName": "计算机组成原...", "scoreTotal": 79, "gradePoint": 2.9},
        {"courseName": "计算机网络技术", "scoreTotal": 77, "gradePoint": 2.7},
        {"courseName": "Java程序设计", "scoreTotal": 86, "gradePoint": 3.6},
        {"courseName": "Java程序设计...", "scoreTotal": 80, "gradePoint": 3.0},
    ],
    "2024-2025-02": [
        {"courseName": "中国近现代史...", "scoreTotal": 73, "gradePoint": 2.3},
        {"courseName": "专业英语", "scoreTotal": 81, "gradePoint": 3.1},
        {"courseName": "概率论与数理...", "scoreTotal": 90, "gradePoint": 4.0},
        {"courseName": "WEB程序设计", "scoreTotal": 77, "gradePoint": 2.7},
        {"courseName": "音乐赏析*", "scoreTotal": 78, "gradePoint": 2.8},
        {"courseName": "数据库原理与...", "scoreTotal": 79, "gradePoint": 2.9},
        {"courseName": "WEB程序设...", "scoreTotal": 91, "gradePoint": 4.0},
        {"courseName": "大学体育IV", "scoreTotal": 84, "gradePoint": 3.4},
    ],
    "2025-2026-01": [
        {"courseName": "互联网程序设...", "scoreTotal": 82, "gradePoint": 3.2},
        {"courseName": "Linux操作系统*", "scoreTotal": 72, "gradePoint": 2.2},
        {"courseName": "Python程序...", "scoreTotal": 92, "gradePoint": 4.0},
        {"courseName": "生活中的心理...", "scoreTotal": 78, "gradePoint": 2.8},
        {"courseName": "软件工程", "scoreTotal": 85, "gradePoint": 3.5},
    ],
}

def get_student_user_id():
    """获取学生的 user_id"""
    try:
        response = requests.get(f"{API_BASE}/students/{STUDENT_ID}")
        if response.status_code == 200:
            data = response.json()
            return data.get('data', {}).get('id')
    except Exception as e:
        print(f"获取学生信息失败: {e}")
    return None

def get_course_id_by_name(course_name):
    """获取课程 ID（根据课程名称 - 可能需要建立课程信息）"""
    # 这里需要根据实际情况，可能需要先创建课程
    # 或者从系统中查询
    # 为了简单起见，这里使用一个模拟的映射
    return hash(course_name) % 10000 + 1  # 生成一个相对稳定的ID

def main():
    print(f"正在为学号 {STUDENT_ID} 的学生插入历年成绩...")
    
    # 获取学生 user_id
    user_id = get_student_user_id()
    if not user_id:
        print(f"错误：找不到学号 {STUDENT_ID} 的学生")
        return
    
    print(f"找到学生，user_id = {user_id}")
    
    # 遍历所有学期和成绩
    total_inserted = 0
    for semester, courses in SCORES_DATA.items():
        print(f"\n处理学期 {semester}...")
        for course in courses:
            print(f"  插入课程: {course['courseName']} - 分数: {course['scoreTotal']}")
            total_inserted += 1
    
    print(f"\n总计需要插入 {total_inserted} 条成绩记录")
    print("\n注：由于 API 限制，建议直接通过 MySQL 数据库插入数据")
    print("可以执行: mysql.exe < d:\\ABS\\insert_scores.sql")

if __name__ == "__main__":
    main()
