package cn.zyz.common;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-21 11:43
 **/

/**
 * 自定义异常处理类
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}



