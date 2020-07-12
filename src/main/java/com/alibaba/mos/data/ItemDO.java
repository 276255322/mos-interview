/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alibaba.mos.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author superchao
 * @version $Id: ItemDO.java, v 0.1 2019年10月28日 11:02 AM superchao Exp $
 */
@Data
public class ItemDO implements Serializable {

    /**
     * 商品名称
     */
    private String name;

    /**
     * 货号
     */
    private String artNo;

    /**
     * itemid
     */
    private String spuId;

    /**
     * 库存数量, 保留小数点后2位
     */
    private BigDecimal inventory;

    /**
     * 最大价格, 保留小数点后2位
     */
    private BigDecimal maxPrice;

    /**
     * 最小价格, 保留小数点后2位
     */
    private BigDecimal minPrice;

    /**
     * 该item下的sku id列表
     */
    private List<String> skuIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtNo() {
        return artNo;
    }

    public void setArtNo(String artNo) {
        this.artNo = artNo;
    }

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public BigDecimal getInventory() {
        return inventory;
    }

    public void setInventory(BigDecimal inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public List<String> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(List<String> skuIds) {
        this.skuIds = skuIds;
    }
}