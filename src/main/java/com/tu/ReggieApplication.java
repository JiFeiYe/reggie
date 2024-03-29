package com.tu;

import com.tu.entity.Dish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: jfy
 * @Date: 2024/3/4
 */
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@Slf4j
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动");


        Dish dish = new Dish();

    }
}
