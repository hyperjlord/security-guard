package com.example.protect.dao;

import com.example.protect.entities.boundarySet;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Rex
 */
@Mapper
public interface boundarySetDao {

    @Insert("insert into boundary_set(account_id,boundary_id,boundary_set_json) values(#{account_id},#{boundary_id},#{boundary_set_json})")
    public boolean addBoundarySet(int account_id,int boundary_id,String boundary_set_json);

    @Delete("delete from boundary_set where account_id=#{account_id}")
    public boolean deleteBoundaryById(int account_id);

    @Select("select * from boundary_set where account_id=#{account_id}")
    public List<boundarySet> getBoundaryById(int account_id);

}
