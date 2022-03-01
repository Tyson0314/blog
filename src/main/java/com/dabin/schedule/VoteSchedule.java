package com.dabin.schedule;

import com.dabin.service.BlogService;
import com.dabin.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: 程序员大彬
 * @time: 2021-12-25 00:00
 */
@Component
@Configuration
@EnableScheduling
@Slf4j
public class VoteSchedule {

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    /**
     * 1秒钟执行一次
     */
    @Scheduled(fixedRate = 1000 * 60)
    private void handleVoteDataTask() {
        log.debug("start handleVoteData task");
        blogService.transVoteDataFromRedis2DB();
        commentService.transVoteDataFromRedis2DB();
        log.debug("end handleVoteData task");
    }

}
