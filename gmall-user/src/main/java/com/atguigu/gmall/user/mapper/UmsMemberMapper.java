package com.atguigu.gmall.user.mapper;


import com.atguigu.gmall.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author lvlei
 * create on 2019-12-22-12:52
 */
public interface UmsMemberMapper  extends Mapper<UmsMember> {
     List<UmsMember> selectAllUser();
}
