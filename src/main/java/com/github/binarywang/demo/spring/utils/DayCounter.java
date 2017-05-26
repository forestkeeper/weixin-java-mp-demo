package com.github.binarywang.demo.spring.utils;

import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wen on 2017/5/24.
 */
public class DayCounter {

    private Map<String,String> map = new ConcurrentHashMap<String,String>();

    public boolean isHoliday(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            return true;
        if (map.containsKey(getStringFormat(date)))
            return true;
        return false;
    }

    /**
     * 相隔多少工作日
     * @param startDate
     * @param endDate
     * @return
     */
    public int getCount(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        start.add(Calendar.DATE, 1);
        int count = 0;
        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            // Do your job here with `date`.
            String time = getStringFormat(date);
            if (map.containsKey(time)){
                //do nothing
            }else{
                if (start.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY &&
                        start.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY ){
                    ++count;
                }
            }
        }
        return count;
    }

    public void init() throws IOException {
        String ret = Request.Get("http://tool.bitefu.net/jiari/data/2017.txt")
                .execute().returnContent().asString();
        for (String date : ret.split("\r\n"))
        if (!date.trim().equals("")){
            map.put("2017" + date,"xxx");
        }
    }

    public String getStringFormat(Date date){
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(date);
    }

    public static void main(String[] args) throws IOException {
        DayCounter dayCounter = new DayCounter();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        Date d = c.getTime();
        System.out.println(dayCounter.getCount(Calendar.getInstance().getTime(),d));
    }
}
