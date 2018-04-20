package com.jd.jvm.jmonitor.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Http {

    /**
     * @return 请求路径
     */
    String value();

    /**
     * 期待的请求方法
     * 目前只支持HTTP的GET方法和POST方法，默认是两者都支持
     *
     * @return 请求方法
     */
    Method[] method() default {Method.GET, Method.POST};

    /**
     * HTTP请求方法
     */
    enum Method {

        /**
         * HTTP's GET method
         */
        GET,

        /**
         * HTTP's POST method
         */
        POST
    }

}
