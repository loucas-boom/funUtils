package com.loucas.funnyUtils.common;


import com.github.pagehelper.PageInfo;

import java.util.List;

public class CommonPage<T> {

    private Integer pageNum;
    private Integer pageSize;
    private Integer pages;
    private long total;
    private List<T> list;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    // 将pageHelper分页后的list转化为分页信息
    public static <T> CommonPage<T> restPage(List<T> list) {
        CommonPage<T> page = new CommonPage<>();
        PageInfo<T> pageInfo = new PageInfo<>();
        page.setPages(pageInfo.getPages());
        page.setPageNum(pageInfo.getPageNum());
        page.setPageSize(pageInfo.getPageSize());
        page.setTotal(pageInfo.getTotal());
        page.setList(pageInfo.getList());
        return page;
    }
}
