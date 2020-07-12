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
 * @author superchao
 * @version $Id: SkuEntity.java, v 0.1 2019年10月28日 10:36 AM superchao Exp $
 */
@Data
public class SkuDO implements Serializable {

    /**
     * sku id
     */
    private String id;

    /**
     * sku 名称
     */
    private String name;

    /**
     * 货号
     */
    private String artNo;

    /**
     * 商品id
     */
    private String spuId;

    /**
     * sku 类型
     */
    private String skuType;

    /**
     * 价格 分为单位
     */
    private BigDecimal price;

    /**
     * 渠道库存
     */
    private List<ChannelInventoryDO> inventoryList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getSkuType() {
        return skuType;
    }

    public void setSkuType(String skuType) {
        this.skuType = skuType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<ChannelInventoryDO> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<ChannelInventoryDO> inventoryList) {
        this.inventoryList = inventoryList;
    }
}