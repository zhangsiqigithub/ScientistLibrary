package com.scientist.lib.recyclerview.paging;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/9/6
 * Time: 17:38
 * Desc: PageListAdapter 在对比更新时候需要用来辨别异同的方法
 */
public interface Different {
    Object uniqueMark();
    Object contentUniqueMark();
}
