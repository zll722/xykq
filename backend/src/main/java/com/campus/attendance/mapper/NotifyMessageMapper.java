package com.campus.attendance.mapper;

import com.campus.attendance.domain.NotifyMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotifyMessageMapper {
    List<NotifyMessage> listByUserId(@Param("userId") Long userId);

    int markRead(@Param("id") Long id, @Param("userId") Long userId);

    int markAllRead(@Param("userId") Long userId);

    int insert(NotifyMessage notifyMessage);
}
