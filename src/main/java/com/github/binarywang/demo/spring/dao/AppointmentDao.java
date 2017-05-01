package com.github.binarywang.demo.spring.dao;

import com.github.binarywang.demo.spring.domain.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wen on 2017/4/22.
 */
@Service
public class AppointmentDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void save(Appointment appointment){
        jdbcTemplate.update("insert into appointment (real_name, open_id, name,chepai,date,driver_license, tel, book_time) values (?,?,?,?,?,?,?,now())",
                appointment.getRealName(), appointment.getOpenId(), appointment.getName(), appointment.getChepai(), appointment.getDate(), appointment.getDriverLicense(),
                appointment.getTel());
    }

    public List<Appointment> list(int offset, int limit){
        return jdbcTemplate.query("select * from appointment limit ?,?", new Object[]{offset , limit}, new RowMapper<Appointment>() {
            @Override
            public Appointment mapRow(ResultSet resultSet, int i) throws SQLException {
                Appointment appointment = new Appointment();
                appointment.setId(resultSet.getLong("id"));
                appointment.setOpenId(resultSet.getString("open_id"));
                appointment.setRealName(resultSet.getString("real_name"));
                appointment.setName(resultSet.getString("name"));
                appointment.setTel(resultSet.getString("tel"));
                appointment.setDriverLicense(resultSet.getString("driver_license"));
                appointment.setDate(resultSet.getDate("date"));
                appointment.setChepai(resultSet.getString("chepai"));

                return appointment;
            }
        });
    }
}
