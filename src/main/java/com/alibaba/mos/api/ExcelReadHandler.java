package com.alibaba.mos.api;

import java.util.LinkedHashMap;

public interface ExcelReadHandler {

    void processOneRow(LinkedHashMap<String, String> row);

}
