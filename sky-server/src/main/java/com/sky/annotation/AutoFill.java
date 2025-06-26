package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，实现自动填充
 */
@Target(ElementType.METHOD) // 表示该注解用于方法上
@Retention(RetentionPolicy.RUNTIME) // 表示该注解在运行时保留
public @interface AutoFill {
    /**
     * 指定数据库操作类型 UPDATE INSERT
     */
    OperationType value();
}
