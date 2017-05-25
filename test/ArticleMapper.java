package com.itfin.platform.dao.article;

import com.itfin.platform.domain.model.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author zhuxiujie
 * @since 2016年8月10日 下午1:10:47
 */
@Repository
public interface ArticleMapper {

	int deleteById(Integer id);

	int insert(Article record);

	Article selectById(Integer id);

	int countByCondition(
            @Param("category") Integer category,
            @Param("objType") Integer objType,
            @Param("objId") Integer objId);

	List<Article> selectByCondition(@Param("category") Integer category,
                                    @Param("objType") Integer objType,
                                    @Param("objId") Integer objId,
                                    @Param("index") Integer index,
                                    @Param("size") Integer size);

    Article selectBySingleCondition(@Param("category") Integer category,
                                    @Param("objType") Integer objType,
                                    @Param("objId") Integer objId);
	int updateById(Article record);

}