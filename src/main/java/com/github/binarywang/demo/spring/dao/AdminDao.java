package com.github.binarywang.demo.spring.dao;

import com.github.binarywang.demo.spring.domain.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wen on 2017/4/30.
 */
@Service
public class AdminDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private AdminUser getUserByName(String userName){
        List<AdminUser> user = jdbcTemplate.query("select * from admin_user where user_name = ?", new Object[]{userName},
                new RowMapper<AdminUser>() {
                    @Override
                    public AdminUser mapRow(ResultSet resultSet, int i) throws SQLException {
                        AdminUser adminUser = new AdminUser();
                        adminUser.setUserName(resultSet.getString("user_name"));
                        adminUser.setPassWord(resultSet.getString("password"));
                        return adminUser;
                    }
                });
        if (user.size() == 0)
            return null;
        return user.get(0);
    }

    public boolean validate(String userName, String passWord){
        AdminUser adminUser = getUserByName(userName);
        if (null == adminUser){
            return false;
        }
        return adminUser.getPassWord().equals(passWord);
    }

    public boolean addUser(String userName, String passWord){
        AdminUser user = getUserByName(userName);
        if (user != null)
            return false;
        this.jdbcTemplate.update("insert into admin_user (user_name, password) values (?,?)",
                new Object[]{userName, passWord});
        return true;
    }
}
