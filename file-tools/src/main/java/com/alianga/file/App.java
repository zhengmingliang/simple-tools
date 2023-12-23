package com.alianga.file;

import lombok.extern.slf4j.Slf4j;
import top.wys.utils.DataUtils;
import top.wys.utils.IOUtils;
import top.wys.utils.StringUtils;
import top.wys.utils.convert.ConvertUtils;

import java.io.*;
import java.util.Scanner;

@Slf4j
public class App {

    public static void main( String[] args ) throws FileNotFoundException {

        Scanner sc = new Scanner(System.in);

        // 校验文件路径输入
        File file = getFile(args, sc);
        if (file == null) {
            return;
        }

        // 校验输入的size大小
        long size = getSize(args, sc);

        String name = file.getName();
        int pointPosition = name.lastIndexOf('.');
        String outputFileName;
        if(pointPosition != -1){
            outputFileName = name.substring(0,pointPosition) + "_new" + name.substring(pointPosition);
        } else {
            outputFileName = name + "_new";
        }
        File outputFile = new File(file.getParentFile(), outputFileName);
        byte[] bytes = new byte[4096];


        FileInputStream is = new FileInputStream(file);
        try(FileOutputStream bos = new FileOutputStream(outputFile,true)) {
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            bos.flush();
        } catch (IOException e) {
            log.error("输入流转字节异常",e);
        }


        // 初始化 要增大的默认buffer大小
        int defaultSize = 1024;
        if(defaultSize > size){
            defaultSize = (int)size;
        }
        byte[] buffer = new byte[defaultSize];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = 0;
        }

        try(FileOutputStream bos = new FileOutputStream(outputFile,true)) {
            for (long i = 0; i < size; ) {
                bos.write(buffer);
                i += buffer.length;
            }


            bos.flush();
        } catch (IOException e) {
            log.error("输入流转字节异常",e);
        }
        IOUtils.close(is);
        log.info("新文件：{},大小：{}",outputFile.getAbsolutePath(), DataUtils.getSize(outputFile.length()));
    }

    private static long getSize(String[] args, Scanner sc) {
        long size = 0;
        if (args.length < 2) {
            log.warn("请输入要进行增大的文件大小（默认单位为B,支持KB/MB/GB单位）");
            String number = sc.nextLine();
            size = getSize(number);
            while (size <= 0) {
                log.warn("请输入要进行增大的文件大小（默认单位为B,支持KB/MB/GB单位）");
                number = sc.nextLine();
                size = getSize(number);

            }
        }
        if(size == 0){
            size = getSize(args[1]);
            if(size <= 0){
                while (size <= 0) {
                    log.warn("请输入正确的要进行增大的文件大小（默认单位为B,支持KB/MB/GB单位）");
                    String number = sc.nextLine();
                    size = getSize(number);

                }
            }
        }
        return size;
    }

    private static File getFile(String[] args, Scanner sc) {
        String filePath = null;
        if (args.length < 1) {
            log.warn("请输入要进行增大的文件路径");
            filePath = sc.nextLine();
            while (StringUtils.isEmpty(filePath))
            {
                log.warn("请输入要进行增大的文件路径");
                filePath = sc.nextLine();
            }
        }
        if (filePath == null) {
            filePath = args[0];
        }

        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("{}文件不存在!", filePath);
            return null;
        }

        if (file.isDirectory()) {
            log.warn("{}是目录，不能进行操作", filePath);
            return null;
        }
        return file;
    }

    public static final Long KB = 1024L;
    public static final Long MB = 1024 * KB;

    public static final Long GB = 1024 * MB;

    /**
     * 将字符串形式的大小转换为长整型的大小值。根据字符串中的单位（如GB、MB、KB），将其转换为相应的大小值并返回。如果字符串为空，则返回0。
     * @param size
     * @return
     */
    public static long getSize(String size) {
        if (StringUtils.isEmpty(size)) {
            return 0L;
        }
        long sizeNumber ;
        if (size.contains("GB") || size.contains("gb") || size.contains("G") || size.contains("g")) {
            Double tmp = ConvertUtils.toDouble(size, 0.0);
            sizeNumber = (long) (tmp * GB);
        } else if (size.contains("MB") || size.contains("mb") || size.contains("M") || size.contains("m")){
            Double tmp = ConvertUtils.toDouble(size, 0.0);
            sizeNumber = (long) (tmp * MB);
        } else if (size.contains("KB") || size.contains("kb") || size.contains("K") || size.contains("k")){
            Double tmp = ConvertUtils.toDouble(size, 0.0);
            sizeNumber = (long) (tmp * KB);
        } else {
            sizeNumber = ConvertUtils.toLong(size, 0L);
        }

      return sizeNumber;
    }

}
