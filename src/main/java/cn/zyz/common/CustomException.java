package cn.zyz.common;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-21 11:43
 **/

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}



