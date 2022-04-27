package com.example.test.utils;

import com.example.test.bean.Form;
import com.example.test.bean.User;
import com.example.test.service.dataProcessor.RechargeableService;
import com.example.test.service.dataProcessor.impl.FreeServiceImpl;
import com.example.test.service.dataProcessor.impl.RechargeableServiceImpl;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class pdfUtils {
    static String path = System.getProperty("user.dir");
    public boolean getPdfFree(Form form2, int userType, HttpServletRequest request) throws IOException, IllegalAccessException {
        // 模板路径
        Cookie[] cookies = request.getCookies();
        String userId = "";
        // 获取指定目录下的第一个文件
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("user_id")){
                userId = cookie.getValue();
            }
        }
        try {
            Map<String,String> map = new HashMap();
            Class clazz = form2.getClass();
            Field[] fields = clazz.getDeclaredFields();
            int sum = form2.getEnglishScore()+form2.getPoliticalScore()+form2.getpOneScore()+form2.getpTwoScore();
            String Sum = Integer.toString(sum);

            FreeServiceImpl fs = new FreeServiceImpl();
            boolean fullTime = form2.getLearnType().equals("全日制");
            boolean acaDe = form2.getPreferCity().equals("学硕");
            User user = new User("","成都中医药大学","030503","",true,true,44.0,44.0,66.0,66.0,395.0);
            user = new User("",form2.getvSchool(),form2.getpCode(),"",fullTime,acaDe,form2.getPoliticalScore().doubleValue(),form2.getEnglishScore().doubleValue(),form2.getpOneScore().doubleValue(),form2.getpTwoScore().doubleValue(),((Integer)sum).doubleValue());

            String free = fs.free(user);

            String[] res = free.split(";");

//        String recharge = re.rechargeable(user);
            System.out.println(res[0]);
            map.put("info1",res[0]);
            map.put("info2",res[1]);
            map.put("info3",res[2]);
            form2.setId(1);

            for (Field field:fields){
                field.setAccessible(true);
                Object obj = field.get(form2);
                if (obj.getClass().toString().equals("java.lang.Integer")){
                    String s = Integer.toString((int)obj);
                    map.put(field.getName(),s);
                    continue;
                }
                map.put(field.getName(), obj.toString());
            }
            map.put("sum",Sum);
            Map<String,Object> o=new HashMap();
            o.put("datemap",map);
            String templatePath = path+"\\src\\main\\resources\\pdfs\\freeModel.pdf";
            // 生成的新文件路径
            String newPDFPath;

            fields = null;
            fs = null;
            user=null;

            if (userType==0){
                newPDFPath = path+"\\src\\main\\resources\\pdfs\\"+userId+"_free.pdf";
            }else {
                String filePath = path+"\\src\\main\\resources\\pdfs\\"+userId+"\\";
                newPDFPath = path+"\\src\\main\\resources\\pdfs\\"+userId+"\\"+form2.getStuName()+".pdf";
                File file = new File(filePath);
                file.mkdir();
            }
            PdfReader reader;
            FileOutputStream out;
            ByteArrayOutputStream bos;
            PdfStamper stamper;
            try {
                BaseFont bf = BaseFont.createFont("c://windows//fonts//simsun.ttc,1" , BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                out = new FileOutputStream(newPDFPath);// 输出流
                reader = new PdfReader(templatePath);// 读取pdf模板
                bos = new ByteArrayOutputStream();
                stamper = new PdfStamper(reader, bos);
                AcroFields form = stamper.getAcroFields();
                //文字类的内容处理
                Map<String,String> datemap = (Map<String,String>)o.get("datemap");
                form.addSubstitutionFont(bf);
                for(String key : datemap.keySet()){
                    String value = datemap.get(key);
                    form.setField(key,value);
                }
                stamper.setFormFlattening(true);// 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
                stamper.close();
                Document doc = new Document();
                PdfCopy copy = new PdfCopy(doc, out);
                doc.open();
                PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
                copy.addPage(importPage);
                doc.close();

                datemap = null;
                doc = null;
                copy = null;
                reader = null;
                out = null;
                bos = null;
                stamper = null;
                System.gc();
            } catch (IOException e) {
                reader = null;
                out = null;
                bos = null;
                stamper = null;
                System.gc();
                System.out.println(e);
            } catch (DocumentException e) {
                reader = null;
                out = null;
                bos = null;
                stamper = null;
                System.gc();
                System.out.println(e);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean getPdfPay(Form form2, int userType, HttpServletRequest request) throws IllegalAccessException, FileNotFoundException {
        // 模板路径
        Cookie[] cookies = request.getCookies();
        String userId = "";
        // 获取指定目录下的第一个文件
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("user_id")){
                userId = cookie.getValue();
            }
        }
        try {
            Map<String,String> map = new HashMap();
            Class clazz = form2.getClass();
            Field[] fields = clazz.getDeclaredFields();
            if (form2.getEnglishScore() == null){
                form2.setEnglishScore(0);
            }
            if (form2.getPoliticalScore() == null){
                form2.setPoliticalScore(0);
            }
            if (form2.getpOneScore() == null){
                form2.setpOneScore(0);
            }
            if (form2.getpTwoScore() == null){
                form2.setpTwoScore(0);
            }
            int sum = form2.getEnglishScore()+form2.getPoliticalScore()+form2.getpOneScore()+form2.getpTwoScore();
            String Sum = Integer.toString(sum);

            FreeServiceImpl fs = new FreeServiceImpl();
            RechargeableService re = new RechargeableServiceImpl();
            boolean fullTime = form2.getLearnType().equals("全日制");
            boolean acaDe = form2.getPreferCity().equals("学硕");
            User user = new User(form2.getStuName(),form2.getvSchool(),form2.getpCode(),"",fullTime,acaDe,form2.getPoliticalScore().doubleValue(),form2.getEnglishScore().doubleValue(),form2.getpOneScore().doubleValue(),form2.getpTwoScore().doubleValue(),((Integer)sum).doubleValue());
            String recharge = re.rechargeable(user);
            fs.genTxt(recharge,form2.getStuNum());

            form2.setId(1);

            String stuName = form2.getStuName();
            int index = stuName.indexOf(";");
            form2.setStuName(stuName.substring(0,index));

            for (Field field:fields){
                field.setAccessible(true);
                Object obj = field.get(form2);
                if (obj.getClass().toString().equals("java.lang.Integer")){
                    String s = Integer.toString((int)obj);
                    map.put(field.getName(),s);
                    continue;
                }
                map.put(field.getName(), obj.toString());
            }
            fields = null;
            re = null;
            fs = null;
            user=null;

            map.put("sum",Sum);
            String downloadURL = "http://139.196.242.203/system/downloadTxt?stunum="+form2.getStuNum();
            map.put("downloadURL",downloadURL);
            Map<String,Object> o=new HashMap();
            o.put("datemap",map);
            String templatePath = path+"\\src\\main\\resources\\pdfs\\payModel.pdf";
            // 生成的新文件路径
            String newPDFPath;
            if (userType==0){
                newPDFPath = path+"\\src\\main\\resources\\pdfs\\"+userId+"_pay.pdf";
            }else {
                String filePath = path+"\\src\\main\\resources\\pdfs\\"+userId+"\\";
                newPDFPath = path+"\\src\\main\\resources\\pdfs\\"+userId+"\\"+form2.getStuName()+".pdf";
                File file = new File(filePath);
                file.mkdir();
            }
            PdfReader reader;
            FileOutputStream out;
            ByteArrayOutputStream bos;
            PdfStamper stamper;
            try {
                BaseFont bf = BaseFont.createFont("c://windows//fonts//simsun.ttc,1" , BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                out = new FileOutputStream(newPDFPath);// 输出流
                reader = new PdfReader(templatePath);// 读取pdf模板
                bos = new ByteArrayOutputStream();
                stamper = new PdfStamper(reader, bos);
                AcroFields form = stamper.getAcroFields();
                //文字类的内容处理
                Map<String,String> datemap = (Map<String,String>)o.get("datemap");
                form.addSubstitutionFont(bf);
                for(String key : datemap.keySet()){
                    String value = datemap.get(key);
                    form.setField(key,value);
                }
                stamper.setFormFlattening(true);// 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
                stamper.close();
                Document doc = new Document();
                PdfCopy copy = new PdfCopy(doc, out);
                doc.open();
                PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
                copy.addPage(importPage);
                doc.close();
                datemap = null;
                doc = null;
                copy = null;
                reader = null;
                out = null;
                bos = null;
                stamper = null;
                System.gc();
            } catch (IOException e) {
                reader = null;
                out = null;
                bos = null;
                stamper = null;
                System.gc();
                System.out.println(e);
            } catch (DocumentException e) {
                reader = null;
                out = null;
                bos = null;
                stamper = null;
                System.gc();
                System.out.println(e);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
    //递归，获取需要压缩的文件夹下面的所有子文件,然后创建对应目录与文件,对文件进行压缩
    public static void ZipFile(ZipOutputStream zos,File file,String fileName) throws Exception
    {
        if(file.isDirectory())
        {
            //创建压缩文件的目录结构
            zos.putNextEntry(new ZipEntry(file.getPath().substring(file.getPath().indexOf(fileName))+File.separator));

            for(File f : file.listFiles())
            {
                ZipFile(zos,f,fileName);
            }
        }
        else
        {
            //打印输出正在压缩的文件
            System.out.println("正在压缩文件:"+file.getName());
            //创建压缩文件
            zos.putNextEntry(new ZipEntry(file.getPath().substring(file.getPath().indexOf(fileName))));
            //用字节方式读取源文件
            InputStream is = new FileInputStream(file.getPath());
            //创建一个缓存区
            BufferedInputStream bis = new BufferedInputStream(is);
            //字节数组,每次读取1024个字节
            byte [] b = new byte[1024];
            //循环读取，边读边写
            while(bis.read(b)!=-1)
            {
                zos.write(b);//写入压缩文件
            }
            //关闭流
            bis.close();
            is.close();
        }
    }

}
