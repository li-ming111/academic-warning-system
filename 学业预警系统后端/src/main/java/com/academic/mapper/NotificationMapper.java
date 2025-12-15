package com.academic.mapper;

import com.academic.entity.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 获取用户未读通知列表
     */
    List<Notification> getUnreadNotifications(Long userId);
    
    @Select("SELECT id,user_id,content,type,warning_id,push_channel,is_read,is_deleted,created_at,updated_at FROM notifications WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Notification> selectNotificationsByUser(Long userId, int limit, int offset);
    
    @Select("SELECT id,user_id,content,type,warning_id,push_channel,is_read,is_deleted,created_at,updated_at FROM notifications WHERE user_id = #{userId} AND is_read = 0 AND is_deleted = 0 ORDER BY created_at DESC")
    List<Notification> selectUnreadNotifications(Long userId);
    
    /**
     * 标记通知为已读
     */
    int markAsRead(Long notificationId);
    
    /**
     * 标记多个通知为已读
     */
    int markMultipleAsRead(List<Long> notificationIds);
    
    /**
     * 删除通知
     */
    int deleteNotification(Long notificationId);
    
    /**
     * 获取未读通知数量
     */
    int getUnreadCount(Long userId);
}
