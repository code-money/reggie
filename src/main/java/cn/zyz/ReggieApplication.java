package cn.zyz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-17 14:59
 **/


@Slf4j
@SpringBootApplication
@ServletComponentScan //开启此注解使过滤器生效
@EnableTransactionManagement //开启注解事务功能
@EnableCaching //开启注解缓存功能
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功...");
    }
}



