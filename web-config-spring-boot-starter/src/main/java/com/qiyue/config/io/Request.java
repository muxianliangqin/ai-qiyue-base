package com.qiyue.config.io;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 前端json请求参数
 *
 * @param <T>
 */
@Data
public class Request<T> {

    private Page page;

    private T content;

    public PageRequest getPage() {
        return getPageable(this.page);
    }


    /**
     * 前端传参，分页参数
     */
    @Data
    public static class Page {
        private int currentPage;

        private int pageSize;

        /**
         * 排序参数 ["updateTime,ASC"]
         */
        private List<String> orders;
    }

    public static PageRequest getPageable(Page page) {
        if (null == page) {
            return null;
        }
        List<String> orders = page.getOrders();
        if (null == orders) {
            return PageRequest.of(page.getCurrentPage(), page.getPageSize());
        }
        List<Sort.Order> orderList = orders.stream()
                .filter(k -> k.matches("^[A-z]+(,(ASC|asc|DESC|desc))?$"))
                .map(k -> {
                    String[] split = k.split(",");
                    if (split.length > 1) {
                        return new Sort.Order(Sort.Direction.valueOf(split[1].toUpperCase()), split[0]);
                    } else {
                        return new Sort.Order(Sort.Direction.DESC, split[0]);
                    }
                }).collect(Collectors.toList());
        return PageRequest.of(page.getCurrentPage(), page.getPageSize(), Sort.by(orderList));
    }
}
