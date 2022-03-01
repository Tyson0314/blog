package com.dabin.vo;

import com.dabin.entity.Tag;
import com.google.common.base.Preconditions;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 标签vo
 *
 * @author 大彬
 * @date 2021-07-06 19:06
 **/
@Data
public class TagWithCountVO {

    private Integer id;

    private String tagName;

    private Integer blogCount;

    /**
     * 构建vo
     * @param tag
     * @return
     */
    public static TagWithCountVO createFrom(Tag tag) {
        Preconditions.checkNotNull(tag, "构建vo时参数不能为null");
        TagWithCountVO tagVO = new TagWithCountVO();
        BeanUtils.copyProperties(tag, tagVO);
        return tagVO;
    }
}
