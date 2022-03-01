package com.dabin.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页要展示的内容
 *
 * @author 大彬
 * @date 2021-11-25 22:07
 */
@Data
@NoArgsConstructor
public class IndexVO {

    public PageInfo<BlogDetail4UserVO> blogList;

    public List<BlogDetail4UserVO> recommendList;

    public List<BlogDetail4UserVO> clickRankList;

    public List<TagWithCountVO> categoryList;

    public List<TagWithCountVO> tagList;

}
