package com.example.test.service.dataProcessor.impl;

import com.aliyun.tea.utils.StringUtils;
import com.example.test.bean.User;
import com.example.test.service.dataProcessor.FreeService;
import com.example.test.service.dataProcessor.RechargeableService;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class FreeServiceImpl implements FreeService {
    public static Map<Integer,Double> proportionFir = new HashMap<>();
    public static Map<Integer,Double> proportionSec = new HashMap<>();
    static {
        proportionFir.put(-4,0.0);proportionFir.put(-3,0.0625);proportionFir.put(-2,0.125);
        proportionFir.put(-1,0.25);proportionFir.put(0,0.5);proportionFir.put(1,0.75);
        proportionFir.put(2,0.875);proportionFir.put(3,0.9375);proportionFir.put(4,1.0);

        proportionSec.put(-4,0.0);proportionSec.put(-3,0.0125);proportionSec.put(-2,0.075);
        proportionSec.put(-1,0.2);proportionSec.put(0,0.5);proportionSec.put(1,0.8);
        proportionSec.put(2,0.925);proportionSec.put(3,0.9875);proportionSec.put(4,1.0);
    }

    public double guiFan(double res){
        if (res < 0) return 0;
        else if (res > 1) return 1;
        return res;
    }
    @Override
    public String free(User user) {
        RechargeableServiceImpl rechargeableService = new RechargeableServiceImpl();
        user = rechargeableService.addInformation(user);
        System.out.println(user);
        DecimalFormat    df   = new DecimalFormat("######0.00");
        String res = "";
        res += "国家线A过线率："+ df.format(guoXianLv(user,"A")*100)+"%\n";
        res += "国家线B过线率："+ df.format(guoXianLv(user,"B")*100)+"%\n";
        res += ";一志愿复试率："+ df.format(fuShiLv(user)*100)+"%\n";


        double shangAnLv = shangAnLv(user);

        res += "一志愿上岸率："+ df.format(shangAnLv*100)+"%\n";



        double tiaoJI = tiaoJI(user, "A");
        double tiaoJI1 = tiaoJI(user, "B");
        res += "A区调剂上岸率："+ df.format(tiaoJI*100)+"%\n";
        res += ";B区调剂上岸率："+ df.format(tiaoJI1*100)+"%\n";
        double baoGuoFir = (shangAnLv+1)/2;
        double baoGuoSec = (tiaoJI1 + 1) /2;
        res += "一志愿包过率：："+ df.format(baoGuoFir*100)+"%\n";
        res += "调剂包过率："+ df.format(baoGuoSec*100)+"%\n";
//        if (shangAnLv < 0.8)
//        res += "一志愿包过率：80%\n";
//        if (tiaoJI < 0.9 && tiaoJI1 < 0.9)
//        res += "调剂包过率：90%\n";
        return res;
    }

    @Override
    public double guoXianLv(User user, String type) {
        double x = user.getTotalPoints();
        double y1 = user.getPoliticalPoints() ;
        double y2 = user.getForeignLanguages();
        double z1 = user.getBusinessFir();
        double z2 = user.getBusinessSec();
//        if (y1 == 0 && z2 == 0){
//            y = user.getForeignLanguages();
//            z = user.getBusinessFir();
//        }
//        else if (user.getBusinessSec() == 0){
//            y = (user.getPoliticalPoints() + user.getForeignLanguages()) / 2;
//            z = user.getBusinessFir();
//        }
//        else {
//            y = (user.getPoliticalPoints() + user.getForeignLanguages()) / 2;
//            z = (user.getBusinessFir() + user.getBusinessSec()) / 2;
//        }
        String majorNum = user.getMajorNum();
        String academicDegree = user.getAcademicDegree()?"学硕":"专硕";
        int[] numSize = {6,4,2};
        for (int i : numSize) {
            for (String[] item : RechargeableServiceImpl.passScore){
                if (StringUtils.isEmpty(item[0]) || StringUtils.isEmpty(item[6])
                        || StringUtils.isEmpty(item[7]) || StringUtils.isEmpty(item[8])) continue;
                if (majorNum.substring(0,i).equals(item[0].substring(0,i)) && academicDegree.equals(item[12])){
                    if (type.equals("A")){
                        if (y1 == 0 && z2 == 0){
                            if (x - Double.parseDouble(item[6]) < -4 || y2 - Double.parseDouble(item[7]) < -4
                                    || z1 - Double.parseDouble(item[8]) < -4 ) return 0;
                            Double fir = proportionFir.getOrDefault((int) (x - Double.parseDouble(item[6])), 1.0);
                            Double sec = proportionSec.getOrDefault((int) (y2 - Double.parseDouble(item[7])), 1.0);
                            Double thr = proportionSec.getOrDefault((int) (z1 - Double.parseDouble(item[8])), 1.0);
                            return guiFan(fir*sec*thr);
                        }
                        else if (z2 == 0){
                            if (x - Double.parseDouble(item[6]) < -4 || y1 - Double.parseDouble(item[7]) < -4 || y2 - Double.parseDouble(item[7]) < -4
                                    || z1 - Double.parseDouble(item[8]) < -4 ) return 0;
                            Double fir = proportionFir.getOrDefault((int) (x - Double.parseDouble(item[6])), 1.0);
                            Double sec1 = proportionSec.getOrDefault((int) (y1 - Double.parseDouble(item[7])), 1.0);
                            Double sec2 = proportionSec.getOrDefault((int) (y2 - Double.parseDouble(item[7])), 1.0);
                            Double thr = proportionSec.getOrDefault((int) (z1 - Double.parseDouble(item[8])), 1.0);
                            return guiFan(fir*sec1*sec2*thr);
                        }
                        else {
                            if (x - Double.parseDouble(item[6]) < -4 || y1 - Double.parseDouble(item[7]) < -4 || y2 - Double.parseDouble(item[7]) < -4
                                    || z1 - Double.parseDouble(item[8]) < -4 || z2 - Double.parseDouble(item[8]) < -4) return 0;
                            Double fir = proportionFir.getOrDefault((int) (x - Double.parseDouble(item[6])), 1.0);
                            Double sec1 = proportionSec.getOrDefault((int) (y1 - Double.parseDouble(item[7])), 1.0);
                            Double sec2 = proportionSec.getOrDefault((int) (y2 - Double.parseDouble(item[7])), 1.0);
                            Double thr1 = proportionSec.getOrDefault((int) (z1 - Double.parseDouble(item[8])), 1.0);
                            Double thr2 = proportionSec.getOrDefault((int) (z2 - Double.parseDouble(item[8])), 1.0);
                            return guiFan(fir*sec1*thr1*sec2*thr2);
                        }
                    }
                    else {
                        if (y1 == 0 && z2 == 0){
                            if (x - Double.parseDouble(item[9]) < -4 || y2 - Double.parseDouble(item[10]) < -4
                                    || z1 - Double.parseDouble(item[11]) < -4 ) return 0;
                            Double fir = proportionFir.getOrDefault((int) (x - Double.parseDouble(item[9])), 1.0);
                            Double sec = proportionSec.getOrDefault((int) (y2 - Double.parseDouble(item[10])), 1.0);
                            Double thr = proportionSec.getOrDefault((int) (z1 - Double.parseDouble(item[11])), 1.0);
                            return guiFan(fir*sec*thr);
                        }
                        else if (z2 == 0){
                            if (x - Double.parseDouble(item[9]) < -4 || y1 - Double.parseDouble(item[10]) < -4 || y2 - Double.parseDouble(item[10]) < -4
                                    || z1 - Double.parseDouble(item[11]) < -4 ) return 0;
                            Double fir = proportionFir.getOrDefault((int) (x - Double.parseDouble(item[9])), 1.0);
                            Double sec1 = proportionSec.getOrDefault((int) (y1 - Double.parseDouble(item[10])), 1.0);
                            Double sec2 = proportionSec.getOrDefault((int) (y2 - Double.parseDouble(item[10])), 1.0);
                            Double thr = proportionSec.getOrDefault((int) (z1 - Double.parseDouble(item[11])), 1.0);
                            return guiFan(fir*sec1*sec2*thr);
                        }
                        else {
                            if (x - Double.parseDouble(item[9]) < -4 || y1 - Double.parseDouble(item[10]) < -4 || y2 - Double.parseDouble(item[10]) < -4
                                    || z1 - Double.parseDouble(item[11]) < -4 || z2 - Double.parseDouble(item[11]) < -4) return 0;
                            Double fir = proportionFir.getOrDefault((int) (x - Double.parseDouble(item[9])), 1.0);
                            Double sec1 = proportionSec.getOrDefault((int) (y1 - Double.parseDouble(item[10])), 1.0);
                            Double sec2 = proportionSec.getOrDefault((int) (y2 - Double.parseDouble(item[10])), 1.0);
                            Double thr1 = proportionSec.getOrDefault((int) (z1 - Double.parseDouble(item[11])), 1.0);
                            Double thr2 = proportionSec.getOrDefault((int) (z2 - Double.parseDouble(item[11])), 1.0);
                            return guiFan(fir*sec1*thr1*sec2*thr2);
                        }
//                        if (x - Double.parseDouble(item[9]) < -4 || y - Double.parseDouble(item[10]) < -4
//                                || z - Double.parseDouble(item[11]) < -4) return 0;
//                        Double fir = proportionFir.getOrDefault((int) (x - Double.parseDouble(item[9])), 1.0);
//                        Double sec = proportionSec.getOrDefault((int) (y - Double.parseDouble(item[10])),1.0);
//                        Double thr = proportionSec.getOrDefault((int) (z - Double.parseDouble(item[11])),1.0);
//                        return fir*sec*thr;
                    }
                }
            }
        }



        return 0;
    }

    @Override
    public double fuShiLv(User user) {
//        Double x = user.getTotalPoints();
//        double xp = getXP(user);
//        System.out.println("xp:"+xp);
//        if (x - xp < -4) return 0;
//        return proportionFir.getOrDefault((int)(x - xp),1.0);
        Double x = user.getTotalPoints();
        double xf = 0;
        String schoolName = user.getSchoolName();
        String majorNum = user.getMajorNum();
        String full = user.getFullTime()?"全日制":"非全日制";
        String academicDegree = user.getAcademicDegree()?"学硕":"专硕";
        String type = user.getSchoolLocation();
        for (String[] item : RechargeableServiceImpl.firTestInform) {
            if (schoolName.equals(item[0]) && majorNum.equals(item[4])
                    && full.equals(item[13]) && academicDegree.equals(item[12])){
                xf = Double.parseDouble(item[6]);
                break;
            }
        }
        System.out.println("xf:"+xf);
        if (xf == 0){
            int[] numSize = {6,4,2};
            for (int i : numSize) {
                for (String[] item : RechargeableServiceImpl.passScore){
                    if (StringUtils.isEmpty(item[0]) || StringUtils.isEmpty(item[6])
                            || StringUtils.isEmpty(item[7]) || StringUtils.isEmpty(item[8])) continue;
                    if (majorNum.substring(0,i).equals(item[0].substring(0,i)) && academicDegree.equals(item[12])){
                        if (type.equals("A")){
                            xf = Double.parseDouble(item[6]);
                        }
                        else {
                            xf = Double.parseDouble(item[9]);
                        }
                    }
                }
            }
        }
        if (x - xf < -4) return 0;
        return guiFan(proportionFir.getOrDefault((int)(x - xf),1.0));
    }

    @Override
    public double shangAnLv(User user) {
        Double x = user.getTotalPoints();
        double xp = getXP(user);
        System.out.println("xp:"+xp);
        double xf = 0;
        String schoolName = user.getSchoolName();
        String majorNum = user.getMajorNum();
        String full = user.getFullTime()?"全日制":"非全日制";
        String academicDegree = user.getAcademicDegree()?"学硕":"专硕";
        String type = user.getSchoolLocation();

        for (String[] item : RechargeableServiceImpl.firTestInform) {
            if (schoolName.equals(item[0]) && majorNum.equals(item[4])
                    && full.equals(item[13]) && academicDegree.equals(item[12])){
                xf = Double.parseDouble(item[6]);
                break;
            }
        }
        System.out.println("xf:"+xf);
        if (xf == 0){
            int[] numSize = {6,4,2};
            for (int i : numSize) {
                for (String[] item : RechargeableServiceImpl.passScore){
                    if (StringUtils.isEmpty(item[0]) || StringUtils.isEmpty(item[6])
                            || StringUtils.isEmpty(item[7]) || StringUtils.isEmpty(item[8])) continue;
                    if (majorNum.substring(0,i).equals(item[0].substring(0,i)) && academicDegree.equals(item[12])){
                        if (type.equals("A")){
                            xf = Double.parseDouble(item[6]);
                        }
                        else {
                            xf = Double.parseDouble(item[9]);
                        }
                    }
                }
            }
        }
        if (x < xf ) return 0;
        double res = Math.abs((x-xf)/(2*(xp - xf)));
        if (user.getSchoolLocation().equals("A")) res = res*guoXianLv(user,"A");
        else res *= guoXianLv(user,"B");
        res *= fuShiLv(user);
        return guiFan(res);
    }

    @Override
    public double tiaoJI(User user, String type) {
        String majorNum = user.getMajorNum();
        String academicDegree = user.getAcademicDegree()?"学硕":"专硕";
        Double x = user.getTotalPoints();
        double xab = 0;
        double xpab = 0;
        int[] numSize = {6,4,2};
        for (int i : numSize) {
            for (String[] item : RechargeableServiceImpl.passScore){
                if (StringUtils.isEmpty(item[0]) || StringUtils.isEmpty(item[6])
                        || StringUtils.isEmpty(item[7]) || StringUtils.isEmpty(item[8])) continue;
                if (majorNum.substring(0,i).equals(item[0].substring(0,i)) && academicDegree.equals(item[12])){
                    if (type.equals("A")){
                        xab = Double.parseDouble(item[6]);
                        xpab = StringUtils.isEmpty(item[13])?xab+35:Double.parseDouble(item[13]);
                    }
                    else {
                        xab = Double.parseDouble(item[9]);
                        xpab = StringUtils.isEmpty(item[15])?xab+35:Double.parseDouble(item[15]);
                    }
                }
            }
        }
        if (x < xab) return 0.1;
        else if (x >= xab && xpab <= xab){
            return guiFan(proportionFir.getOrDefault((int)(x - xab),1.0));
        }
        else if ((x - xab) *2 / (xpab - xab) > 1) return 0.99;
        return guiFan((x - xab) *2 / (xpab - xab));
    }

    @Override
    public double getXP(User user) {
        user = new RechargeableServiceImpl().addInformation(user);
        String schoolLocation = user.getSchoolLocation();
        String full = user.getFullTime()?"全日制":"非全日制";
        String majorNum = user.getMajorNum();
        String academicDegree = user.getAcademicDegree()?"学硕":"专硕";
        int[] numSize = {6,4,2};
        for (int i : numSize) {
            for (String[] item : RechargeableServiceImpl.passScore){
                if (StringUtils.isEmpty(item[0]) || StringUtils.isEmpty(item[12])) continue;
                if (majorNum.substring(0,i).equals(item[0].substring(0,i)) && academicDegree.equals(item[12])){
                    if (schoolLocation.equals("A") && full.equals("全日制")){
                        if (StringUtils.isEmpty(item[14])) break;
                        return Double.parseDouble(item[14]);
                    }
                    if (schoolLocation.equals("B") && full.equals("全日制")){
                        if (StringUtils.isEmpty(item[16])) break;
                        return Double.parseDouble(item[16]);
                    }
                    if (schoolLocation.equals("A") && full.equals("非全日制")){
                        if (StringUtils.isEmpty(item[18])) break;
                        return Double.parseDouble(item[18]);
                    }
                    if (schoolLocation.equals("B") && full.equals("非全日制")){
                        if (StringUtils.isEmpty(item[20])) break;
                        return Double.parseDouble(item[20]);
                    }
                }
            }
        }

        for (int i : numSize) {
            for (String[] item : RechargeableServiceImpl.passScore){
                if (StringUtils.isEmpty(item[0]) || StringUtils.isEmpty(item[12])) continue;
                if (majorNum.substring(0,i).equals(item[0].substring(0,i)) && academicDegree.equals(item[12])){
                    if (schoolLocation.equals("A")) return Double.parseDouble(item[6])+25;
                    else return Double.parseDouble(item[9])+25;
                }
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        FreeServiceImpl fs = new FreeServiceImpl();
        User user = new User("","浙江师范大学","035101","",true,false,88.0,88.0,88.0,88.0,352.0);
//        System.out.println(new RechargeableServiceImpl().addInformation(user));
        System.out.println(fs.free(user));
//        System.out.println(fs.guoXianLv(user, "A"));
//        double v = fs.shangAnLv(user);
//        System.out.println(v);
    }

    static String path = System.getProperty("user.dir");
    String realPath = path+"\\src\\main\\resources\\txt\\";
    public void genTxt(String content,String userId) throws FileNotFoundException {
        File fp=new File(realPath+userId+".txt");
        PrintWriter pfp= new PrintWriter(fp);
        pfp.print(content);
        pfp.close();
    }
}
