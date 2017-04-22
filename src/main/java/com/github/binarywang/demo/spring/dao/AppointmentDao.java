package com.github.binarywang.demo.spring.dao;

import com.github.binarywang.demo.spring.domain.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by wen on 2017/4/22.
 */
@Service
public class AppointmentDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void save(Appointment appointment){
        jdbcTemplate.update("insert into appointment (name,chepai,) values (?,?)", appointment.getName(), appointment.getChepai());
    }
}
