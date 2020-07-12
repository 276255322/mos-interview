/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alibaba.mos.api;

/**
 *
 * @author superchao
 * @version $Id: ItemAggregationProviderConsumer.java, v 0.1 2019年10月28日 12:00 PM superchao Exp $
 */
public interface ProviderConsumer<T> {
    /**
     * 执行生产者消费者并通过callback返回结果
     * @param handler
     */
    void execute(ResultHandler<T> handler);


    interface ResultHandler<T> {

        /**
         * 处理读取的sku信息
         * @param result
         * @return
         */
        T handleResult(T result);
    }
}