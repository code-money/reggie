package cn.zyz.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-20 21:54
 **/


/**
 * MP字段自动填充类
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("新增自动填充");
        this.strictInsertFill(metaObject,"createTime", LocalDateTime.class,LocalDateTime.now());
        this.strictUpdateFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
        /*
            从ThreadLocal中获取放入的当前登陆的用户id
            为什么用ThreadLocal不用Session呢？
            因为请求的作用域是有范围的，在当前MP自动填充类中无法获取session
            所以用ThreadLocal扩展了当前请求的作用域，并将登陆的用户Id放到ThreadLocal中
         */
        this.strictInsertFill(metaObject,"createUser", Long.class,BaseContext.getCurrentId());
        this.strictUpdateFill(metaObject,"updateUser", Long.class,BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("修改自动填充");
        this.strictUpdateFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
        this.strictUpdateFill(metaObject,"updateUser", Long.class,BaseContext.getCurrentId());
    }
}



