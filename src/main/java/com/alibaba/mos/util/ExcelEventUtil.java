package com.alibaba.mos.util;

import com.alibaba.mos.api.ExcelReadHandler;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * 读取超大量Excel数据操作类
 */
public class ExcelEventUtil {

    /**
     * 读取Excel
     *
     * @param filename
     * @param excelReadHandler
     */
    public static void processAllSheets(String filename, ExcelReadHandler excelReadHandler) {
        Iterator<InputStream> sheets = null;
        XMLReader parser = null;
        try {
            OPCPackage pkg = OPCPackage.open(filename);
            XSSFReader reader = new XSSFReader(pkg);
            SharedStringsTable sst = reader.getSharedStringsTable();
            StylesTable styleTable = reader.getStylesTable();
            parser = fetchSheetParser(sst, styleTable, excelReadHandler);
            sheets = reader.getSheetsData();
        } catch (IOException | OpenXML4JException | SAXException | ParserConfigurationException e) {
            throw new ExcelReadException("读取Excel报错");
        }

        while (sheets != null && sheets.hasNext()) {
            try (InputStream sheet = sheets.next();) {
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
            } catch (IOException | SAXException e) {
                throw new ExcelReadException("读取Excel中sheet表报错");
            }
        }
    }

    /**
     * @param sst
     * @param styleTable
     * @param excelReadHandler
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static XMLReader fetchSheetParser(SharedStringsTable sst, StylesTable styleTable, ExcelReadHandler excelReadHandler) throws SAXException, ParserConfigurationException {
        XMLReader parser = SAXHelper.newXMLReader();
        ContentHandler handler = new SheetHandlerUtil(sst, styleTable, excelReadHandler);
        parser.setContentHandler(handler);
        return parser;
    }

    private static class SheetHandlerUtil extends DefaultHandler {
        /**
         * 单元格中的数据可能的数据类型
         */
        enum CellDataType {
            BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
        }

        /**
         *
         */
        private final DataFormatter formatter = new DataFormatter();

        /**
         * excel中，若单元格内的内容是字符串，那么这些字符串都存在这个变量中
         */
        private SharedStringsTable sst;

        /**
         * 用于获取时间类型单元格的时间格式
         */
        private StylesTable styleTable;

        /**
         * 当前单元格的内容
         */
        private String currentContents;

        /**
         * 当前单元格的位置
         */
        private String ref;

        /**
         * 当前单元格的类型
         */
        private CellDataType cellDataType;

        /**
         * 当前单元格为时间时的格式索引
         */
        private short formatIndex;

        /**
         * 当前单元格为时间时的格式
         */
        private String formatString;

        /**
         *
         */
        private LinkedHashMap<String, String> result = new LinkedHashMap<>();

        /**
         * 读取一行的回调
         */
        private ExcelReadHandler excelReadHandler;

        /**
         * 构造方法
         *
         * @param sst
         * @param styleTable
         * @param excelReadHandler
         */
        private SheetHandlerUtil(SharedStringsTable sst, StylesTable styleTable, ExcelReadHandler excelReadHandler) {
            this.sst = sst;
            this.styleTable = styleTable;
            this.excelReadHandler = excelReadHandler;
        }

        /**
         * 这个方法在遇到一个xml文件的元素开始之前被触发，取出单元格内存放的内容的类型
         *
         * @param uri
         * @param localName
         * @param name
         * @param attributes
         * @throws SAXException
         */
        @Override
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // name为c表示遇到了单元格
            if (name.equals("c")) {
                ref = attributes.getValue("r");
                setNextDataType(attributes);
            }
            // 即将获取单元格的内容，所以置空该变量
            currentContents = "";
        }


        /**
         * 处理数据类型
         *
         * @param attributes 单元格参数
         */
        private void setNextDataType(Attributes attributes) {
            cellDataType = CellDataType.NUMBER; //cellType为空，则表示该单元格类型为数字
            formatIndex = -1;
            formatString = null;
            String cellType = attributes.getValue("t"); //单元格类型
            String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType)) { //处理布尔值
                cellDataType = CellDataType.BOOL;
            } else if ("e".equals(cellType)) {  //处理错误
                cellDataType = CellDataType.ERROR;
            } else if ("inlineStr".equals(cellType)) {
                cellDataType = CellDataType.INLINESTR;
            } else if ("s".equals(cellType)) { //处理字符串
                cellDataType = CellDataType.SSTINDEX;
            } else if ("str".equals(cellType)) {
                cellDataType = CellDataType.FORMULA;
            }
            if (cellStyleStr != null) { //处理日期
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = styleTable.getStyleAt(styleIndex);
                formatIndex = style.getDataFormat();
                formatString = style.getDataFormatString();

                if (formatString == null) {
                    cellDataType = CellDataType.NULL;
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }

                if (formatString.contains("m/d/yy")) {
                    cellDataType = CellDataType.DATE;
                    formatString = "yyyy-MM-dd hh:mm:ss";
                }
            }
        }

        /**
         * @param value
         * @return
         */
        private String getDataValue(String value) {
            String thisStr;
            switch (cellDataType) {
                case BOOL:
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;
                case ERROR:
                    thisStr = "\"ERROR:" + value.toString() + '"';
                    break;
                case FORMULA:
                    thisStr = '"' + value.toString() + '"';
                    break;
                case INLINESTR:
                    XSSFRichTextString rtsi = new XSSFRichTextString(value);
                    thisStr = rtsi.toString();
                    break;
                case SSTINDEX:
                    String sstIndex = value.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(sst.getEntryAt(idx));//根据idx索引值获取内容值
                        thisStr = rtss.toString();
                        rtss = null;
                    } catch (NumberFormatException ex) {
                        thisStr = value.toString();
                    }
                    break;
                case NUMBER:
                    if (formatString != null) {
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();
                    } else {
                        thisStr = value;
                    }
                    thisStr = thisStr.replace("_", "").trim();
                    break;
                case DATE:
                    thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
                    thisStr = thisStr.replace("T", " ");
                    break;
                default:
                    thisStr = " ";
                    break;
            }
            return thisStr;
        }

        /**
         * 存储当前单元格的内容
         *
         * @param ch
         * @param start
         * @param length
         */
        @Override
        public void characters(char[] ch, int start, int length) {
            currentContents = new String(ch, start, length);
        }

        /**
         * 读取完单元格的内容后被执行
         *
         * @param uri
         * @param localName
         * @param name
         * @throws SAXException
         */
        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            if (name.equals("v")) {
                result.put(ref, getDataValue(currentContents));
            }
            if (name.equals("row")) {
                excelReadHandler.processOneRow(result);
                result.clear();
            }
        }
    }
}
