package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsBaseAttrMapper extends Mapper<PmsBaseAttrInfo> {
    /**
     * 根据属性值id查询列表
     * @param set
     * @return
     */
    List<PmsBaseAttrInfo> selectAttrList(@Param("valueIdStr")String valueIdStr);
}
