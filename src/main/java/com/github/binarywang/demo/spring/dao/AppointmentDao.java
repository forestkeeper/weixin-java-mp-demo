package com.github.binarywang.demo.spring.dao;

import com.github.binarywang.demo.spring.domain.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wen on 2017/4/22.
 *
 * 1: 预约中
 * 2: pass 预约
 * 3: not pass 预约
 * 4: 预约未出现
 */
@Service
public class AppointmentDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public long count(){
        return jdbcTemplate.queryForObject("select count(id) from appointment", new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getLong(1);
            }
        });
    }

    public List<Appointment> findAllByTime(Date from , Date to){
        return jdbcTemplate.query("select * from appointment where date >= ? and date <= ?",
        new Object[]{from, to}, new AppointmentRowMapper());
    }

    public void save(Appointment appointment){
        jdbcTemplate.update("insert into appointment " +
                        "(real_name, open_id, name,chepai,date,driver_license, tel, book_time, server_id, time) " +
                        "values (?,?,?,?,?,?,?,now(),?,?)",
                appointment.getRealName(), appointment.getOpenId(), appointment.getName(), appointment.getChepai(), appointment.getDate(), appointment.getDriverLicense(),
                appointment.getTel(),appointment.getServerId(), appointment.getTime());
    }

    public boolean isInBlackList(String tel, String openId){
        int count = jdbcTemplate.queryForObject("select count(id) from appointment where tel = ? and status = 4", new Object[]{tel}, Integer.class);
        if (count >= 3)
            return true;
        count = jdbcTemplate.queryForObject("select count(id) from appointment where open_id = ? and status = 4", new Object[]{openId}, Integer.class);
        if (count >= 3)
            return true;
        return false;
    }

    public Appointment find(long id){
        String sql = "select * from appointment where id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new AppointmentRowMapper());
    }

    public List<Appointment> list(int offset, int limit){
        return jdbcTemplate.query("select * from appointment where status = 1 limit ?,?", new Object[]{offset , limit}, new AppointmentRowMapper());
    }

    public List<Appointment> history(int offset, int limit){
        return jdbcTemplate.query("select * from appointment where status != 1 limit ?,?", new Object[]{offset , limit}, new AppointmentRowMapper());
    }

    public void updateStatus(long id, int status){
        jdbcTemplate.update("update appointment set status = ? where id = ?", status, id);
    }

    /**
     *
     * @param openId
     * @return
     */
    public List<Appointment> findInOrderingAppointmentByOpenId(String openId){
        return jdbcTemplate.query("select * from appointment where open_id = ? and status = 1", new Object[]{openId}, new AppointmentRowMapper()
        );
    }

    public int findCountByOpenId(String openId){
        return jdbcTemplate.queryForObject("select count(id) from appointment where open_id = ?", new Object[]{openId}, Integer.class);
    }
    public int findCountByTel(String tel){
        return jdbcTemplate.queryForObject("select count(id) from appointment where tel = ?", new Object[]{tel}, Integer.class);
    }

    public List<Appointment> findPassedAppointmentByOpenId(String openId){
        return jdbcTemplate.query("select * from appointment where open_id = ? and status = 2", new Object[]{openId}, new AppointmentRowMapper());
    }

    public Long countForDay(String date, int time){
        return jdbcTemplate.queryForObject("select count(id) from appointment where date = ? and time = ?", new Object[]{date, time}, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getLong(1);
            }
        });
    }

    public class AppointmentRowMapper implements RowMapper<Appointment> {

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
            appointment.setServerId(resultSet.getString("server_id"));
            appointment.setTime(resultSet.getInt("time"));
            appointment.setStatus(resultSet.getInt("status"));
            return appointment;
        }
    }
}
