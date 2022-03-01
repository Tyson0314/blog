package com.dabin.service;

import com.dabin.common.base.Result;
import com.dabin.entity.Vote;
import com.dabin.entity.VoteExample;

import java.util.List;

/**
 * @author: 程序员大彬
 * @time: 2021-12-23 22:21
 */
public interface VoteService {
    Result<Integer> vote(Integer id, String userId, String type);

    int blogVoteCount(Integer blogId, String type);

    List<Vote> queryVote(VoteExample example);
}
