package com.tu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tu.entity.AddressBook;
import com.tu.mapper.AddressBookMapper;
import com.tu.service.IAddressBookService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 地址管理 服务实现类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {

}
