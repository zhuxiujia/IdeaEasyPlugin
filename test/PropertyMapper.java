package com.itfin.platform.dao.property;

import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.itfin.platform.domain.model.Property;

/**
 * 
 * @author zhuxiujie
 * @since 2016年8月10日 下午1:13:12
 */
@Repository
public interface PropertyMapper {

	public int deleteById(Integer id);

	public int insert(Property property);

	public Property selectById(Integer id);

	public int updateById(Property property);

	public Property selectByUserId(@Param("userId") Integer userId);

	public int addTotalAmount(@Param("amount") Integer amount, @Param("updateTime") Date updateTime,
                              @Param("userId") Integer userId);

    public int removeTotalAmount(@Param("amount") Integer amount, @Param("updateTime") Date updateTime,
                                 @Param("userId") Integer userId);

	public int addCanUseAmount(@Param("amount") Integer amount, @Param("updateTime") Date updateTime,
                               @Param("userId") Integer userId);

	public int reduceCanUseAmount(@Param("amount") Integer amount, @Param("updateTime") Date updateTime,
                                  @Param("userId") Integer userId);


    int addPoolAmount(@Param("amount") Integer amount, @Param("updateTime") Date updateTime,
						  @Param("userId") Integer userId);

	int reducePoolAmount(@Param("amount") Integer amount, @Param("updateTime") Date updateTime,
							 @Param("userId") Integer userId);

	int addPoolProfit(@Param("amount")Integer amount, @Param("userId") Integer userId,@Param("updateTime") Date updateTime);

	int addPoolTotalAmount(@Param("amount")Integer amount,@Param("userId") Integer userId,@Param("updateTime") Date updateTime);

	int reducePoolTotalAmount(@Param("amount")Integer amount,@Param("userId") Integer userId,@Param("updateTime") Date updateTime);
}