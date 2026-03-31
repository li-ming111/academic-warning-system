package com.academic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.academic.entity.ScoreAppeal;
import com.academic.mapper.ScoreAppealMapper;
import com.academic.service.ScoreAppealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ScoreAppealServiceImpl extends ServiceImpl<ScoreAppealMapper, ScoreAppeal> implements ScoreAppealService {

    @Override
    public boolean submitAppeal(ScoreAppeal appeal) {
        appeal.setStatus("pending");
        return this.save(appeal);
    }

    @Override
    public List<ScoreAppeal> getStudentAppeals(Long studentId) {
        QueryWrapper<ScoreAppeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warning_id", studentId);
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }

    @Override
    public List<ScoreAppeal> getPendingAppeals() {
        QueryWrapper<ScoreAppeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "pending");
        queryWrapper.orderByAsc("created_at");
        return this.list(queryWrapper);
    }

    @Override
    public boolean replyAppeal(Long appealId, String message, String status) {
        ScoreAppeal appeal = this.getById(appealId);
        if (appeal == null) {
            return false;
        }
        appeal.setStatus(status);
        return this.updateById(appeal);
    }

    @Override
    public ScoreAppeal getAppealDetail(Long appealId) {
        return this.getById(appealId);
    }

}
