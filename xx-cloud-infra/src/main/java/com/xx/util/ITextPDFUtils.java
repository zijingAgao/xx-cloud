package com.xx.util;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 合并pdf工具类 使用依赖IText
 *
 * @author Agao
 * @date 2024/7/15 10:26
 */
@Slf4j
public class ITextPDFUtils {
  /** 图片后缀 jpg */
  public static String JPG = ".jpg";

  /** 图片后缀 jpeg */
  public static String JPEG = ".jpeg";

  /** 图片后缀 png */
  public static String PNG = ".png";

  /** pdf后缀 */
  public static String PDF = ".pdf";

  /** 缓存文件-临时文件名字前缀 */
  public static final String TEMP_DOC_PREFIX = "temp-";

  /**
   * 合并pdf 输出到某个位置
   *
   * @param sourcePathList 完整的路径包含文件名字
   * @param destPath 完整的路径包含文件名字.pdf结尾
   */
  public static void mergePdf(List<String> sourcePathList, String destPath) {
    if (CollectionUtils.isEmpty(sourcePathList)) {
      log.warn("ITextPDFUtils-mergePdf: pathList is null");
      return;
    }
    if (!StringUtils.hasText(destPath)) {
      log.warn("ITextPDFUtils-mergePdf: destPath is null");
      return;
    }
    PdfMerger merger = null;
    try {
      List<PdfDocument> fileList = new ArrayList<>();
      for (String sourcePath : sourcePathList) {
        fileList.add(new PdfDocument(new PdfReader(sourcePath)));
      }
      PdfDocument tempPdfDoc = new PdfDocument(new PdfWriter(destPath));
      merger = new PdfMerger(tempPdfDoc);
      // 合并完成后自动关闭源文档
      merger.setCloseSourceDocuments(true);
      // 合并pdf
      for (PdfDocument doc : fileList) {
        merger.merge(doc, 1, doc.getNumberOfPages());
      }

    } catch (IOException e) {
      log.warn("ITextPDFUtils-mergePdf: mergePdf error", e);
    } finally {
      if (merger != null) {
        merger.close();
      }
    }
  }

  /**
   * 合并pdf-将合并的pdf返回前端
   *
   * @param inputStreamList pdf文件流
   * @param resp
   * @throws IOException
   */
  public static void mergePdf(List<InputStream> inputStreamList, String fileName, HttpServletResponse resp) {
    if (CollectionUtils.isEmpty(inputStreamList)) {
      log.warn("ITextPDFUtils-mergePdf: inputStreamList is null");
      return;
    }

    String tempFileName = TEMP_DOC_PREFIX + System.currentTimeMillis() + PDF;
    File outFile = new File(tempFileName);

    try (PdfDocument tempPdfDoc = new PdfDocument(new PdfWriter(tempFileName))) {
      // 加载pdf资源
      List<PdfDocument> fileList = new ArrayList<>();
      for (InputStream in : inputStreamList) {
        fileList.add(new PdfDocument(new PdfReader(in)));
      }
      // 合并pdf
      PdfMerger merger = new PdfMerger(tempPdfDoc);
      try {
        merger.setCloseSourceDocuments(true);
        for (PdfDocument doc : fileList) {
          merger.merge(doc, 1, doc.getNumberOfPages());
        }
      } finally {
        merger.close();
      }

      try (InputStream inStream = Files.newInputStream(outFile.toPath());
           OutputStream osStream = resp.getOutputStream()) {

        String encodeFileName = URLEncoder.encode(fileName, "utf-8");
        resp.reset();
        resp.setHeader("Content-Disposition", "attachment;filename=" + encodeFileName);
        resp.setContentType("application/pdf");

        byte[] buff = new byte[1024];
        int len;
        while ((len = inStream.read(buff)) > 0) {
          osStream.write(buff, 0, len);
        }
        osStream.flush();
      }

    } catch (IOException e) {
      log.warn("ITextPDFUtils-mergePdf: mergePdf error", e);
    } finally {
      // 删除临时文件
      if (outFile.exists() && !outFile.delete()) {
        log.warn("ITextPDFUtils-mergePdf: delete temp file error");
      }
    }
  }

  /**
   * 合并pdf或者img
   *
   * @param sourcePathList 源路径 完整文件路径包含文件后缀
   * @param destPath 目标路径 完整文件路径包含文件后缀
   */
  public static void mergePdfOrImg(List<String> sourcePathList, String destPath) {
    if (CollectionUtils.isEmpty(sourcePathList)) {
      log.warn("ITextPDFUtils-batchImgConvertPdf: path is null;");
      return;
    }
    if (!StringUtils.hasText(destPath)) {
      log.warn("ITextPDFUtils-batchImgConvertPdf: destPath is null;");
      return;
    }

    List<PdfDocument> fileList = new ArrayList<>();
    List<File> tempList = new ArrayList<>();
    PdfMerger merger = null;
    try {
      for (String sourcePath : sourcePathList) {
        if (sourcePath.endsWith(PDF)) {
          fileList.add(new PdfDocument(new PdfReader(sourcePath)));
        }

        if (sourcePath.endsWith(JPG) || sourcePath.endsWith(JPEG) || sourcePath.endsWith(PNG)) {
          // 先图片转pdf生成一个临时文件
          String tempFileName = TEMP_DOC_PREFIX + System.currentTimeMillis() + PDF;
          imgConvertToPdf(sourcePath, tempFileName);
          // 缓存临时文件，后面使用完之后删除
          tempList.add(new File(tempFileName));

          fileList.add(new PdfDocument(new PdfReader(tempFileName)));
        }
      }

      // 合并所有的pdf
      PdfDocument tempPdfDoc = new PdfDocument(new PdfWriter(destPath));
      merger = new PdfMerger(tempPdfDoc);
      // 合并完成后自动关闭源文档
      merger.setCloseSourceDocuments(true);
      // 合并pdf
      for (PdfDocument doc : fileList) {
        merger.merge(doc, 1, doc.getNumberOfPages());
      }

    } catch (IOException e) {
      log.warn("ITextPDFUtils-mergePdf: mergePdf error", e);
    } finally {
      if (merger != null) {
        merger.close();
      }
      // 删除临时文件
      if (!CollectionUtils.isEmpty(tempList)) {
        tempList.forEach(File::delete);
      }
    }
  }

  /**
   * 图片转pdf 一对一
   * 输出默认位置是：源路径下
   * @param sourcePath 源路径 完整文件路径包含文件后缀
   *
   */
  public static void imgConvertToPdf(String sourcePath) {
    String defaultDestPath = null;
    if (StringUtils.hasText(sourcePath)) {
      defaultDestPath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + PDF;
    }
    imgConvertToPdf(sourcePath, defaultDestPath);
  }

  /**
   * 图片转pdf 一对一
   *
   * @param sourcePath 源路径 完整文件路径包含文件后缀
   * @param destPath 目标路径 完整文件路径包含文件后缀
   */
  public static void imgConvertToPdf(String sourcePath, String destPath) {
    if (!StringUtils.hasText(sourcePath)) {
      log.warn("ITextPDFUtils-imgConvertPdf path is null;");
    }
    if (!StringUtils.hasText(destPath)) {
      log.warn("ITextPDFUtils-imgConvertPdf: destPath is null;");
    }
    Document doc = null;
    try {
      Image image = new Image(ImageDataFactory.create(sourcePath));
      float width = image.getImageWidth();
      float height = image.getImageHeight();
      PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destPath));
      doc = new Document(pdfDoc, new PageSize(width,height));
      pdfDoc.addNewPage(new PageSize(width,height));
      doc.add(image);
    } catch (MalformedURLException e) {
      log.warn("ITextPDFUtils-imgConvertPdf: imgConvertPdf MalformedURLException", e);
    } catch (FileNotFoundException e) {
      log.warn("ITextPDFUtils-imgConvertPdf: imgConvertPdf FileNotFoundException", e);
    } finally {
      if (doc != null) {
        doc.close();
      }
    }
  }

  /**
   * 将多张图片转到一个pdf中
   *
   * @param sourcePathList 源路径 完整文件路径包含文件后缀
   * @param destPath 目标路径 完整文件路径包含文件后缀
   */
  public static void batchImgConvertToPdf(List<String> sourcePathList, String destPath) {
    if (CollectionUtils.isEmpty(sourcePathList)) {
      log.warn("ITextPDFUtils-batchImgConvertPdf: path is null;");
      return;
    }
    if (!StringUtils.hasText(destPath)) {
      log.warn("ITextPDFUtils-batchImgConvertPdf: destPath is null;");
      return;
    }

    Document doc = null;
    try {
      PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destPath));
      doc = new Document(pdfDoc);
      for (int i = 0; i < sourcePathList.size(); i++) {
        Image image = new Image(ImageDataFactory.create(sourcePathList.get(i)));
        pdfDoc.addNewPage(new PageSize(image.getImageWidth(), image.getImageHeight()));
        image.setFixedPosition(i + 1, 0, 0);
        doc.add(image);
      }
    } catch (MalformedURLException e) {
      log.warn("ITextPDFUtils-imgConvertPdf: imgConvertPdf MalformedURLException", e);
    } catch (FileNotFoundException e) {
      log.warn("ITextPDFUtils-imgConvertPdf: imgConvertPdf FileNotFoundException", e);
    } finally {
      if (doc != null) {
        doc.close();
      }
    }
  }

  public static void main(String[] args) {
    //    ArrayList<String> list = new ArrayList<>();
    //    list.add("D:\\Work\\boot-demo\\src\\main\\resources\\Java开发手册(黄山版).pdf");
    //    list.add("D:\\Work\\boot-demo\\src\\main\\resources\\Java开发手册(黄山版).pdf");
    //    ITextPDFUtils.mergePdf(list,
    //    "D:\\Work\\boot-demo\\src\\main\\resources\\Java开发手册(黄山版)-merge.pdf");

    //    String imgSourcePath = "D:\\Work\\boot-demo\\src\\main\\resources\\111.jpg";
    //    imgConvertToPdf(imgSourcePath);

    String imgSourcePath1 = "D:\\Work\\boot-demo\\src\\main\\resources\\111.jpg";
    String imgSourcePath2 = "D:\\Work\\boot-demo\\src\\main\\resources\\王也.png";
    String dest = "D:\\Work\\boot-demo\\src\\main\\resources\\王也-111.pdf";
    batchImgConvertToPdf(Arrays.asList(imgSourcePath1, imgSourcePath2), dest);
  }
}
