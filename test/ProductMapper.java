package com.itfin.platform.dao.product;

import com.itfin.platform.domain.model.OrderForm;
import com.itfin.platform.domain.model.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author zhuxiujie
 * @date 2016年8月26日 下午2:46:05
 */
@Repository
public interface ProductMapper {
	
	int deleteById(Integer id);

	int insert(Product record);
	
	Product selectById(Integer id);

	int updateById(Product record);
	
	List<Product> selectAll();
	
	List<Product> selectByActivateTime(Date date);
	
	int recycleOrderForm(OrderForm orderForm);
	
	List<Product> selectByStatus(Integer status);

	/**
	 * 下单，减库存
	 * @param targetAmount
	 * @param id
	 * @return
	 */
	int orderProduct(@Param("amount") Integer targetAmount, @Param("id") Integer id);
	
	List<Product> selectByCondition(@Param("status") Integer status,
                                    @Param("type") Integer type,
                                    @Param("name") String name,
                                    @Param("interestModeType") Integer interestModeType,
									@Param("interestTimeType") Integer interestTimeType,
                                    @Param("startTime") Date startTime,
                                    @Param("endTime") Date endTime,
                                    @Param("index") Integer index,
                                    @Param("size") Integer size);
	
	int countByCondition(@Param("status") Integer status,
                         @Param("type") Integer type,
                         @Param("name") String name,
						 @Param("interestModeType") Integer interestModeType,
						 @Param("interestTimeType") Integer interestTimeType,
                         @Param("startTime") Date startTime,
                         @Param("endTime") Date endTime);
	
	List<Product> selectByStatusesAndType(@Param("statuses") List<Integer> statuses, @Param("type")Integer type);

    int countProductToatl();
}