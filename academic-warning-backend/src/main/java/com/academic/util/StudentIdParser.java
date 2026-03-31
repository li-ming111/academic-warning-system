package com.academic.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学号解析工具类
 * 
 * 学号编码规则：
 * - 第1-4位：入学年份（如2023）
 * - 第5-6位：专业编码（如02代表计算机科学与技术专业）
 * - 第7-8位：专业班级编号（如06代表该专业的第6个班级）
 * - 第9-10位：学生在班级内的排名序号（如16代表班级排名第16）
 * 
 * 例：2023020616
 * - 2023年入学
 * - 专业编码为02（计算机科学与技术专业）
 * - 属于该专业第06班级
 * - 在班级内排名第16位
 */
public class StudentIdParser {

    /**
     * 学号解析结果
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentIdInfo {
        private String studentId;           // 完整学号
        private Integer enrollmentYear;     // 入学年份（位置1-4）
        private String majorCode;           // 专业编码（位置5-6）
        private String classCode;           // 班级编号（位置7-8）
        private String rankInClass;         // 班级内排名（位置9-10）
        private Boolean valid;              // 是否有效
        private String errorMessage;        // 错误信息
    }

    /**
     * 解析学号
     *
     * @param studentId 学号（10位）
     * @return 学号信息
     */
    public static StudentIdInfo parseStudentId(String studentId) {
        StudentIdInfo info = new StudentIdInfo();
        info.setStudentId(studentId);

        // 验证学号格式
        if (studentId == null || studentId.isEmpty()) {
            info.setValid(false);
            info.setErrorMessage("学号不能为空");
            return info;
        }

        if (studentId.length() != 10) {
            info.setValid(false);
            info.setErrorMessage("学号必须为10位数字");
            return info;
        }

        if (!studentId.matches("\\d{10}")) {
            info.setValid(false);
            info.setErrorMessage("学号必须由10位数字组成");
            return info;
        }

        try {
            // 解析各部分
            String yearStr = studentId.substring(0, 4);
            String majorCode = studentId.substring(4, 6);
            String classCode = studentId.substring(6, 8);
            String rankStr = studentId.substring(8, 10);

            // 验证入学年份
            int year = Integer.parseInt(yearStr);
            if (year < 1980 || year > 2100) {
                info.setValid(false);
                info.setErrorMessage("入学年份不合理（应为1980-2100）");
                return info;
            }

            // 验证专业编码（理论上应在01-99之间）
            int majorCodeInt = Integer.parseInt(majorCode);
            if (majorCodeInt < 1 || majorCodeInt > 99) {
                info.setValid(false);
                info.setErrorMessage("专业编码不合理（应为01-99）");
                return info;
            }

            // 验证班级编号（理论上应在01-99之间）
            int classCodeInt = Integer.parseInt(classCode);
            if (classCodeInt < 1 || classCodeInt > 99) {
                info.setValid(false);
                info.setErrorMessage("班级编号不合理（应为01-99）");
                return info;
            }

            // 验证班级内排名（理论上应在01-99之间）
            int rankInt = Integer.parseInt(rankStr);
            if (rankInt < 1 || rankInt > 99) {
                info.setValid(false);
                info.setErrorMessage("班级排名不合理（应为01-99）");
                return info;
            }

            // 设置解析结果
            info.setEnrollmentYear(year);
            info.setMajorCode(majorCode);
            info.setClassCode(classCode);
            info.setRankInClass(rankStr);
            info.setValid(true);

            return info;

        } catch (NumberFormatException e) {
            info.setValid(false);
            info.setErrorMessage("学号格式错误：" + e.getMessage());
            return info;
        }
    }

    /**
     * 生成学号
     *
     * @param enrollmentYear 入学年份
     * @param majorCode 专业编码（2位）
     * @param classCode 班级编号（2位）
     * @param rankInClass 班级排名（2位）
     * @return 生成的学号，格式错误则返回null
     */
    public static String generateStudentId(int enrollmentYear, String majorCode, String classCode, String rankInClass) {
        // 验证参数
        if (enrollmentYear < 1980 || enrollmentYear > 2100) {
            return null;
        }

        if (majorCode == null || majorCode.length() != 2 || !majorCode.matches("\\d{2}")) {
            return null;
        }

        if (classCode == null || classCode.length() != 2 || !classCode.matches("\\d{2}")) {
            return null;
        }

        if (rankInClass == null || rankInClass.length() != 2 || !rankInClass.matches("\\d{2}")) {
            return null;
        }

        // 拼接学号
        return enrollmentYear + majorCode + classCode + rankInClass;
    }

    /**
     * 获取学号中的入学年份
     */
    public static Integer getEnrollmentYear(String studentId) {
        StudentIdInfo info = parseStudentId(studentId);
        return info.getValid() ? info.getEnrollmentYear() : null;
    }

    /**
     * 获取学号中的专业编码
     */
    public static String getMajorCode(String studentId) {
        StudentIdInfo info = parseStudentId(studentId);
        return info.getValid() ? info.getMajorCode() : null;
    }

    /**
     * 获取学号中的班级编号
     */
    public static String getClassCode(String studentId) {
        StudentIdInfo info = parseStudentId(studentId);
        return info.getValid() ? info.getClassCode() : null;
    }

    /**
     * 获取学号中的班级排名
     */
    public static String getRankInClass(String studentId) {
        StudentIdInfo info = parseStudentId(studentId);
        return info.getValid() ? info.getRankInClass() : null;
    }

    /**
     * 验证学号是否有效
     */
    public static boolean isValidStudentId(String studentId) {
        return parseStudentId(studentId).getValid();
    }
}
