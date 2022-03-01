package com.dabin.vo;

import com.dabin.common.utils.DateTimeUtil;
import com.dabin.entity.Tag;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

/**
 * 标签的bo类
 *
 * @author 大彬
 * @date 2021-11-18 17:08
 **/
@Setter
@Getter
@NoArgsConstructor
public class TagVO {

    private Integer id;

    private String tagName;

    private Integer tagType;

    private String tagDesc;

    private String createTime;

    private String updateTime;

    /**
     * 构建vo
     */
    public static TagVO createFrom(Tag tag) {
        Preconditions.checkNotNull(tag, "构建vo时参数不能为null");
        TagVO tagVO = new TagVO();
        BeanUtils.copyProperties(tag, tagVO);
        tagVO.setCreateTime(DateTimeUtil.dateToStr(tag.getCreateTime()));
        tagVO.setUpdateTime(DateTimeUtil.dateToStr(tag.getUpdateTime()));

        return tagVO;
    }
}
