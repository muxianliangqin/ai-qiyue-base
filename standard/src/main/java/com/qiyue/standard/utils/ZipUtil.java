package com.qiyue.standard.utils;

import com.qiyue.standard.constant.Constant;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class ZipUtil {

    public static String gzip(String str) throws Exception {
        String zip = "";
        if (BaseUtil.isEmpty(str)) {
            log.debug(str);
            return zip;
        }
        try (ByteArrayOutputStream bas = new ByteArrayOutputStream();
             GZIPOutputStream gizmos = new GZIPOutputStream(bas)) {
            gizmos.write(str.getBytes(StandardCharsets.UTF_8));
            gizmos.finish();
            zip = new String(Base64.getEncoder().encode(bas.toByteArray()));
        } catch (IOException e) {
            throw new Exception("压缩失败", e);
        }
        return zip;
    }

    /**
     * Description:使用gzip进行解压缩
     *
     * @param gzipStr
     * @return
     * @throws Exception
     */
    public static String unGzip(String gzipStr) throws Exception {
        String unGzip = "";
        if (BaseUtil.isEmpty(gzipStr)) {
            throw new Exception("str不能为空");
        }
        try (ByteArrayOutputStream bas = new ByteArrayOutputStream()) {
            byte[] zipArr = Base64.getDecoder().decode(gzipStr.getBytes(StandardCharsets.UTF_8));
            try (ByteArrayInputStream bis = new ByteArrayInputStream(zipArr);
                 GZIPInputStream gzip = new GZIPInputStream(bis)) {
                byte[] buffer = new byte[10 * 1024];
                int offset = -1;
                while ((offset = gzip.read(buffer)) != -1) {
                    bas.write(buffer, 0, offset);
                }
                unGzip = bas.toString(Constant.ENCODE_UTF8);
            } catch (IOException e) {
                throw new Exception("压缩失败", e);
            }
        }
        return unGzip;
    }
}
