package com.scientist.lib.recyclerview.mvvm.command;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/10/16
 * Time: 10:36
 * Desc:
 */
public interface Func1<T, R> {
    R call(T t);
}
