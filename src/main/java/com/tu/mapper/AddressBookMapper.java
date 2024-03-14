package com.tu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tu.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 地址管理 Mapper 接口
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}
