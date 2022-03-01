package com.dabin.vo;

import com.dabin.dto.CatalogItem;
import com.dabin.entity.Blog;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 博客类， 查看博客详情时使用
 *
 * @author 大彬
 * @date 2021-09-27 0:17
 */
@Data
@NoArgsConstructor
@ApiModel("博客vo")
public class BlogDetail4UserVO extends Blog {

    List<CatalogItem> catalogs;

    public String calcTime;

    public String categoryName;

    public Integer categoryId;

    public List<TagVO> tagList;

    public BlogDetail4AdminVO last;

    public BlogDetail4AdminVO next;

    PageInfo<CommentListItemVO> comments;
}
