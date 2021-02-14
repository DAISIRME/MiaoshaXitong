package org.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springboot.entity.Goods ;
import org.springboot.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface KillMapper {
    List<Goods> queryallgoods();
    int Order(int id);
    Goods querygoods(int id);
    int insertgoods(@Param("id")int id, @Param("name")String name);
}
