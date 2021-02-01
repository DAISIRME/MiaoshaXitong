package org.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springboot.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {
    List<User> queryUserList();
    int addUser(User user);
    int deleteUser(User user);
}
