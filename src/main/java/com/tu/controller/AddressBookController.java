package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.tu.common.BaseContext;
import com.tu.common.R;
import com.tu.entity.AddressBook;
import com.tu.service.IAddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 地址管理 前端控制器
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    /**
     * 查询用户地址列表
     *
     * @return List
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        log.info("查询用户地址列表");

        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        lqw.orderByDesc(AddressBook::getIsDefault);
        List<AddressBook> addressBooks = addressBookService.list(lqw);

        return R.success(addressBooks);
    }

    /**
     * 新增地址
     *
     * @param addressBook 地址表
     * @return String
     */
    @PostMapping
    public R<String> addAddress(@RequestBody AddressBook addressBook) {
        log.info("开始新增地址");

        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(0);

        addressBookService.save(addressBook);
        return R.success("新增成功！");
    }

    /**
     * 查询单个地址
     *
     * @param id userId
     * @return AddressBook
     */
    @GetMapping("/{id}")
    public R<AddressBook> addressFindOne(@PathVariable Long id) {
        log.info("开始查询单个地址");

        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getId, id);
        AddressBook address = addressBookService.getOne(lqw);
        return R.success(address);
    }

    /**
     * 修改地址
     *
     * @param addressBook 地址表
     * @return String
     */
    @PutMapping
    public R<String> updateAddress(@RequestBody AddressBook addressBook) {
        log.info("开始修改地址");

        addressBookService.updateById(addressBook);
        return R.success("修改成功！");
    }

    /**
     * 设置默认地址
     *
     * @param addressBook 里面只有id
     * @return String
     */
    @PutMapping("/default")
    public R<String> setDefaultAddress(@RequestBody AddressBook addressBook) {
        log.info("开始设置默认地址");

        LambdaUpdateWrapper<AddressBook> luw = new LambdaUpdateWrapper<>();
        luw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        luw.set(AddressBook::getIsDefault, 0);
        addressBookService.update(luw);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success("设置默认地址成功！");
    }

    /**
     * 删除地址
     *
     * @param ids id
     * @return String
     */
    @DeleteMapping
    public R<String> deleteAddress(@RequestParam Long ids) {
        log.info("开始删除地址 ids:{}", ids);

        addressBookService.removeById(ids);
        return R.success("删除成功！");
    }

    /**
     * 获取默认地址
     *
     * @return AddressBook
     */
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress() {
        log.info("开始获取默认地址");

        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getIsDefault, 1);
        AddressBook address = addressBookService.getOne(lqw);
        return R.success(address);
    }
}
