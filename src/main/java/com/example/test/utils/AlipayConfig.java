package com.example.test.utils;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *作者：AAA_有梦想一起实现
 */

public class AlipayConfig{

    // ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016092800612797";//例：2016082600317257

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhNYC0eZo/B2LXBYhQ3sLChgVHLmwAfKI7UMGz8WjzjJg7bvld3bBCt1w4k5YjBNBgau6xZ7m85s9CRg8DVe5qfwlXSs0diN2PQpd5Gb0AoGfd2gFzYnqzo4K5JPuyx06O1sf99Ox40EcIFBP3p3rjlcpELw/m6CEGn3E0whcX3Nn1QX5v+t0fCfi4khmz5xdEyiF8oRCq296xxJSq4G8avAfChMPUIiK8bNIqrKZ3u15+J9flkfxClw/SplHhRPNmFA2eizMP+weuTDiGByxeAryAo9scGVrybSkPPT77pzgLXgB3RQIOP1eODVaJ3aYPH5eAugH9i2nSVdGJAld6QIDAQAB";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm
    // 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxozIquNWmXxI/+FDTZdyaj3P3wxCapINzX0AmS9sJQnSjtn0sw4uSw8KLFsMoMcCc/nLSWBY9XSg+VO0yC7x/KZcsPZ79+6H+UJtGXcBveWaUu4QuXJ7c/EkAg5oNFRNORsnLqqz3nlbt8kemEI8FdwfLspv49DfA79NK/EGpMM1i3Kj0VZKcUWekMESR/834d9Jb6GL4SqjtypVbK9XA9+cznw5yhpATYZUzIiB5C1B4pmxzygRAd3vLww4/OR3+FjfqxvtvR20e6K0N0g3L3pVOARoyC5dYR3lue4TJ096XrkS7gnHC6TzEe8fDGzCAF8cVKTXwJcSNk9+8biv5QIDAQAB";
    // 服务器异步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    /**
     * 返回的时候此页面不会返回到用户页面，只会执行你写到控制器里的地址
     */
    public static String notify_url = "39.105.161.237:8081/hello/success";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    /**
     * 此页面是同步返回用户页面，也就是用户支付后看到的页面，上面的notify_url是异步返回商家操作，谢谢
     * 要是看不懂就找度娘，或者多读几遍，或者去看支付宝第三方接口API，不看API直接拿去就用，遇坑不怪别人
     */
    public static String return_url = "39.105.161.237:8081/hello/fail";
    // 签名方式
    public static String sign_type = "RSA2";
    // 字符编码格式
    public static String charset = "gbk";
    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
    // 日志地址
    public static String log_path = "C:/logs/";
    // ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     *
     * @param sWord
     *            要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_"
                    + System.currentTimeMillis() + ".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
