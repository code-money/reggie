package cn.zyz.common;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-20 11:33
 **/

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常捕获处理器
 */
@Slf4j
//任何带有RestController或Controller注解的类中的异常都将被捕获
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 指定异常处理方法
     * 当Controller中抛出SQLIntegrityConstraintViolationException异常执行该方法
     * 用于处理新增时加唯一索引的字段值不能重复的问题
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error("异常信息:{}", ex.getMessage());
        String errorMessage = ex.getMessage();
        if (errorMessage.contains("Duplicate entry")) {
            String[] split = errorMessage.split(" ");
            String msg = split[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 自定义异常处理
     * 当Controller中抛出CustomException异常执行该方法
     * 用于处理删除分类但是分类有关联套餐|菜品的问题
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error("异常信息:{}", ex.getMessage());
        return R.error(ex.getMessage());
    }
}



