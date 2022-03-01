package com.dabin.dao;

import com.dabin.entity.Vote;
import com.dabin.entity.VoteExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VoteMapper {
    long countByExample(VoteExample example);

    int deleteByExample(VoteExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Vote record);

    int insertSelective(Vote record);

    List<Vote> selectByExample(VoteExample example);

    Vote selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Vote record, @Param("example") VoteExample example);

    int updateByExample(@Param("record") Vote record, @Param("example") VoteExample example);

    int updateByPrimaryKeySelective(Vote record);

    int updateByPrimaryKey(Vote record);

    /**
     * 自定义sql
     */
    int blogVoteCount(@Param("targetId") Integer targetId, @Param("type") String type);

    int batchInsert(@Param("voteList") List<Vote> voteList);

    List<Vote> selectByTargetIds(@Param("targetIds") List<Integer> targetIds, @Param("type") String type);
}
