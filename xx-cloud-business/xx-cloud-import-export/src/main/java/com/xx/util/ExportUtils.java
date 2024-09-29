package com.xx.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Agao
 * @date 2024/9/24 15:30
 */
@Slf4j
public class ExportUtils {

  public static <T> void exportExcel(String fileName, String sheetName, List<T> dataList, Class<T> clazz, HttpServletResponse resp) {
    try {
      setExcelRespProp(resp, fileName);
      EasyExcel.write(resp.getOutputStream())
          .head(clazz)
          .excelType(ExcelTypeEnum.XLSX)
          .sheet(sheetName)
          .doWrite(dataList);
    } catch (IOException e) {
      log.warn("导出excel失败", e);
    }
  }

  public static <T> void exportExcel(String filename, List<T> dataList, Class<T> clazz, HttpServletResponse resp) {
    exportExcel(filename, null, dataList, clazz, resp);
  }

  /** 设置excel下载响应头属性 */
  private static void setExcelRespProp(HttpServletResponse resp, String fileName)
      throws UnsupportedEncodingException {
    String encodeFileName = URLEncoder.encode(fileName + ".xlsx", "UTF-8");
    resp.setHeader("Content-disposition", "attachment;fileName=" + encodeFileName);
    resp.setCharacterEncoding("utf-8");
    resp.setContentType("application/vnd.ms-excel;charset=UTF-8");
  }
}
