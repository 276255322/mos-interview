package com.alibaba.mos.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.mos.data.ChannelInventoryDO;
import com.alibaba.mos.data.SkuDO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelConvertUtil {

    /**
     * Excel转换为SkuDO
     *
     * @param row
     * @param skuDO
     */
    public static void excelToSkuDO(LinkedHashMap<String, String> row, SkuDO skuDO) {
        int index = 0;
        boolean isRow = false;
        for (String value : row.values()) {
            if (index == 0 && isInteger(value)) {
                isRow = true;
            }
            if (isRow) {
                switch (index) {
                    case 1:
                        skuDO.setName(value);
                        break;
                    case 2:
                        skuDO.setArtNo(value);
                        break;
                    case 3:
                        skuDO.setSpuId(value);
                        break;
                    case 4:
                        skuDO.setSkuType(value);
                        break;
                    case 5:
                        skuDO.setPrice(new BigDecimal(value));
                        break;
                    case 6:
                        List<ChannelInventoryDO> list = new ArrayList<>();
                        JSONArray jarray = JSON.parseArray(value);
                        for (int i = 0; i < jarray.size(); i++) {
                            JSONObject jobj = jarray.getJSONObject(i);
                            ChannelInventoryDO cido = new ChannelInventoryDO();
                            cido.setSkuId(skuDO.getId());
                            cido.setChannelCode(jobj.getString("channelCode"));
                            cido.setInventory(new BigDecimal(jobj.getString("inventory")));
                            list.add(cido);
                        }
                        skuDO.setInventoryList(list);
                        break;
                    default:
                        skuDO.setId(value);
                        break;
                }
            }
            index++;
        }
    }

    /***
     * 判断 String 是否int
     *
     * @param input
     * @return
     */
    public static boolean isInteger(String input) {
        Matcher mer = Pattern.compile("^[0-9]+$").matcher(input);
        return mer.find();
    }
}
