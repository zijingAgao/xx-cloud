package com.xx.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.google.common.collect.Lists;
import com.xx.dto.HutoolDto;
import com.xx.vo.HutoolVo;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Agao
 * @date 2024/10/14 15:35
 */
public class HutoolTest {

  // ------------1. bean 属性 转换---------------------------------------------------------

  /**
   * dto -> vo属性转换 要导 cglib 依赖
   *
   * @return
   */
  public static HutoolVo dtoToVo() {
    HutoolDto dto = new HutoolDto();
    dto.setId(1);
    dto.setName("Agao");
    dto.setDeptIdList(Lists.newArrayList(1, 2));

    return CglibUtil.copy(dto, HutoolVo.class);
  }

  // ------------2.文件读写---------------------------------------------------------

  /** 拷贝文件 */
  public static void copyFile() {
    BufferedInputStream in = FileUtil.getInputStream("d:/test.txt");
    BufferedOutputStream out = FileUtil.getOutputStream("d:/test2.txt");
    IoUtil.copy(in, out, IoUtil.DEFAULT_BUFFER_SIZE);
  }

  public static void readWriteFile() {
    String a = "D:\\Work\\xx-cloud\\xx-cloud-business\\xx-cloud-hutool\\src\\main\\resources\\a.txt";
    String b = "D:\\Work\\xx-cloud\\xx-cloud-business\\xx-cloud-hutool\\src\\main\\resources\\b.txt";

    List<String> list = FileUtil.readUtf8Lines(a);
    FileUtil.writeLines(list, b, StandardCharsets.UTF_8);

    // 流写出
    FileUtil.writeFromStream(FileUtil.getInputStream("d:/test.txt"), "d:/test2.txt");
  }

  // ------------3.随机数---------------------------------------------------------

  public static void random() {
    // [10, 100)的随机数
    System.out.println(RandomUtil.randomInt(10, 100));
    // 数字+ 字母
    System.out.println(RandomUtil.randomString(10));
    // 数字字符串
    System.out.println(RandomUtil.randomNumbers(10));
  }

  // ------------4.唯一id---------------------------------------------------------

  // 注意要确保 Snowflake 单例
  public final static Snowflake snowflake = IdUtil.getSnowflake(1, 1);
  public static void getOnlyId() {
    // UUID
    System.out.println(IdUtil.simpleUUID());
    // mongo objectId
    System.out.println(IdUtil.objectId());

    // Snowflake
    // 参数1为终端ID
    // 参数2为数据中心ID

    System.out.println(snowflake.nextIdStr());
    // 简单使用
    System.out.println(IdUtil.getSnowflakeNextIdStr());
  }

  public static void main(String[] args) {
    //    System.out.println(dtoToVo());
    //    readWriteFile();
    //    random();
    getOnlyId();

  }
}
