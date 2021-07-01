package com.example.protect.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import javax.persistence.criteria.From;
import java.util.List;

@Mapper
public interface trackSetDao {
    @Insert("insert into track_set(account_id,start_time,end_time,track_set_json) values(#{account_id},#{start_time},#{end_time},#{track_set_json})")
    boolean addTrackSet(int account_id,long start_time,long end_time,String track_set_json);


    @Select("select track_set_json from track_set where account_id = #{account_id} and start_time = (select max(start_time) where account_id=#{account_id})")
    String  getTrackSetBySeconds(int account_id);

    @Select("select track_set_json from track_set where account_id = #{account_id} and (start_time >= #{start_time}) and (end_time <= #{end_time})")
    List<String>  getTrackSetByMin(int account_id,long start_time ,long end_time);

}
