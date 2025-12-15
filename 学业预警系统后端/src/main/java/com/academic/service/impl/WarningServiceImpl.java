package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.AcademicWarning;
import com.academic.entity.Score;
import com.academic.entity.WarningRule;
import com.academic.mapper.AcademicWarningMapper;
import com.academic.mapper.ScoreMapper;
import com.academic.mapper.WarningRuleMapper;
import com.academic.service.WarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class WarningServiceImpl extends ServiceImpl<AcademicWarningMapper, AcademicWarning> implements WarningService {

    private final ScoreMapper scoreMapper;
    private final WarningRuleMapper warningRuleMapper;

    public WarningServiceImpl(ScoreMapper scoreMapper, WarningRuleMapper warningRuleMapper) {
        this.scoreMapper = scoreMapper;
        this.warningRuleMapper = warningRuleMapper;
    }

    @Override
    public List<AcademicWarning> getStudentWarnings(Long studentId) {
        return this.getBaseMapper().selectByStudentId(studentId);
    }

    @Override
    public List<AcademicWarning> getPendingWarnings(Long studentId) {
        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.in("status", "pending", "confirmed");
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }

    @Override
    public Long countWarnings(Long studentId) {
        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.in("status", "pending", "confirmed");
        return this.count(queryWrapper);
    }

    @Override
    public Long countWarningsByLevel(Long studentId, String level) {
        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("warning_level", level);
        queryWrapper.in("status", "pending", "confirmed");
        return this.count(queryWrapper);
    }

    @Override
    public void confirmWarning(Long warningId) {
        AcademicWarning warning = this.getById(warningId);
        if (warning != null) {
            warning.setStatus("confirmed");
            this.updateById(warning);
            log.info("预警 {} 已确认", warningId);
        }
    }

    @Override
    public Long countFailedCourses(Long studentId, String semester) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        if (semester != null && !semester.isEmpty()) {
            queryWrapper.eq("semester", semester);
        }
        queryWrapper.lt("score_total", 60);
        long count = scoreMapper.selectCount(queryWrapper);
        log.info("学生 {} 的挂科数量：{}", studentId, count);
        return count;
    }

    @Override
    public void generateWarningsByFailedCount(Long studentId, String semester) {
        Long failedCount = countFailedCourses(studentId, semester);
        if (failedCount == 0) {
            log.info("学生 {} 在学期 {} 无挂科", studentId, semester);
            return;
        }

        QueryWrapper<WarningRule> ruleQuery = new QueryWrapper<>();
        ruleQuery.eq("status", 1);
        List<WarningRule> rules = warningRuleMapper.selectList(ruleQuery);

        for (WarningRule rule : rules) {
            boolean matches = false;
            String level = rule.getLevel();

            if ("low".equals(level) && failedCount <= 3) {
                matches = true;
            } else if ("medium".equals(level) && failedCount > 3 && failedCount <= 5) {
                matches = true;
            } else if ("high".equals(level) && failedCount > 5) {
                matches = true;
            }

            if (matches) {
                QueryWrapper<AcademicWarning> warningQuery = new QueryWrapper<>();
                warningQuery.eq("student_id", studentId);
                warningQuery.eq("warning_level", level);
                warningQuery.in("status", "pending", "confirmed");
                long existingCount = this.count(warningQuery);

                if (existingCount == 0) {
                    AcademicWarning warning = new AcademicWarning();
                    warning.setStudentId(studentId);
                    warning.setWarningLevel(level);
                    warning.setDescription(String.format("学期末挨科数量：%d科，触发%s规则", failedCount, rule.getName()));
                    warning.setStatus("pending");
                    warning.setCreatedAt(LocalDateTime.now());
                    warning.setUpdatedAt(LocalDateTime.now());
                    this.save(warning);
                    log.info("为学生 {} 创建了{}  级预警", studentId, level);
                }
            }
        }
    }
}
