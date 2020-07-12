/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alibaba.mos.service;

import com.alibaba.mos.api.SkuReadService;
import com.alibaba.mos.data.SkuDO;
import com.alibaba.mos.util.ExcelConvertUtil;
import com.alibaba.mos.util.ExcelEventUtil;
import org.springframework.stereotype.Service;

/**
 * TODO: 实现
 *
 * @author superchao
 * @version $Id: SkuReadServiceImpl.java, v 0.1 2019年10月28日 10:49 AM superchao Exp $
 */
@Service
public class SkuReadServiceImpl implements SkuReadService {

    /**
     * 这里假设excel数据量很大无法一次性加载到内存中
     *
     * @param handler
     */
    @Override
    public void loadSkus(SkuHandler handler) {
        String fileName = this.getClass().getClassLoader().getResource("data/skus.xls").getPath();
        ExcelEventUtil.processAllSheets(fileName, (result) -> {
            SkuDO skuDO = new SkuDO();
            ExcelConvertUtil.excelToSkuDO(result, skuDO);
            if (skuDO.getId() != null) {
                handler.handleSku(skuDO);
            }
        });
    }
}