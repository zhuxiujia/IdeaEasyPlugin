package com.itfin.platform.dao.withdrawal;

import com.itfin.platform.domain.model.UserWithdrawal;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface UserWithdrawalMapper {

    int deleteById(Integer id);

    int insert(UserWithdrawal record);

    UserWithdrawal selectById(Integer id);

    int updateById(UserWithdrawal record);

    List<UserWithdrawal> selectAll(@Param("userId") int userId);

    UserWithdrawal selectBySerialNumber(@Param("serialNumber") String serialNumber);

    List<UserWithdrawal> selectByCondition(@Param("userId") int userId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("index") int index, @Param("size") int size);

    int countByCondition(@Param("userId") Integer userId,
                         @Param("startTime") Date startTime,
                         @Param("endTime") Date endTime);
}