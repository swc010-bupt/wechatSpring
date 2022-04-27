package com.example.test.service.dataProcessor.impl;


import com.aliyun.tea.utils.StringUtils;
import com.csvreader.CsvReader;
import com.example.test.bean.User;
import com.example.test.service.dataProcessor.RechargeableService;
import com.example.test.utils.tablePrint.ConsoleTable;
import com.example.test.utils.tablePrint.enums.Align;
import com.example.test.utils.tablePrint.table.Cell;
import com.example.test.vo.SchoolAvail;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class RechargeableServiceImpl implements RechargeableService {
    public static List<String[]> firTestInform;    //初试考试信息表.csv
    public static List<String[]> admissionsStatistics;  //录取分析统计表.csv
    public static List<String[]> admissionList;     //录取名单
    public static List<String[]> passScore;         //国家线.csv

    public static List<String> schoolLocationRank;
    public static List<String> majorStrengthRank;
    public static List<String> schoolTypeRank;

    static {
        try {
            // 用来保存数据
            firTestInform = new LinkedList<>();
            admissionsStatistics = new LinkedList<>();
            admissionList = new LinkedList<>();
            passScore = new LinkedList<>();
            schoolLocationRank = new ArrayList<>();
            majorStrengthRank =  new ArrayList<>();
            schoolTypeRank = new ArrayList<>();

            // 定义一个CSV路径
            String pathFir = "data/初试考试信息表.csv";
            String pathSec = "data/录取分析统计表.csv";
            String pathThr = "data/调剂录取名单.csv";
//            String pathFor = "data/国家线.csv";
            String pathFor ="data/平均分.csv";
            // 创建CSV读对象 例如:CsvReader(文件路径，分隔符，编码格式);
            CsvReader reader1 = new CsvReader(pathFir, ',', Charset.forName("UTF-8"));
            CsvReader reader2 = new CsvReader(pathSec, ',', Charset.forName("UTF-8"));
            CsvReader reader3 = new CsvReader(pathThr, ',', Charset.forName("UTF-8"));
            CsvReader reader4 = new CsvReader(pathFor, ',', Charset.forName("UTF-8"));
            // 跳过表头 如果需要表头的话，这句可以忽略
            reader1.readHeaders(); reader2.readHeaders();
            reader3.readHeaders();reader4.readHeaders();
            // 逐行读入除表头的数据
            while (reader1.readRecord()) {
                firTestInform.add(reader1.getValues());
            }
            reader1.close();
            while (reader2.readRecord()) {
                admissionsStatistics.add(reader2.getValues());
            }
            reader2.close();
            while (reader3.readRecord()) {
                admissionList.add(reader3.getValues());
            }
            reader3.close();
            while (reader4.readRecord()) {
                passScore.add(reader4.getValues());
            }
            reader4.close();
            schoolLocationRank.add("B");schoolLocationRank.add("A");
            majorStrengthRank.add("C-");majorStrengthRank.add("C");majorStrengthRank.add("C+");
            majorStrengthRank.add("B-");majorStrengthRank.add("B");majorStrengthRank.add("B+");
            majorStrengthRank.add("A-");majorStrengthRank.add("A");majorStrengthRank.add("A+");
            schoolTypeRank.add("二本");schoolTypeRank.add("一本");
            schoolTypeRank.add("211、双一流");schoolTypeRank.add("211、985、双一流");
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSchoolType(String schoolName) {
        for (String[] strings : firTestInform) {
            if (strings[0].equals(schoolName)){
                return strings[11];     //学校类型  11
            }
        }
        return "一本";
    }

    /**
     * 补充用户信息：学校类型，学校位置类型，专业强度
     */
    @Override
    public User addInformation(User user) {
        if (StringUtils.isEmpty(user.getSchoolType()) || StringUtils.isEmpty(user.getSchoolLocation()) || StringUtils.isEmpty(user.getMajorStrength())){
            String schoolName = user.getSchoolName();
            String majorNum = user.getMajorNum();
            user.setSchoolType(getSchoolType(schoolName));
            for (String[] strings : admissionsStatistics) {
                if (strings[1].equals(schoolName) && strings[3].equals(majorNum)){
                    if (StringUtils.isEmpty(strings[18])) {
                        user.setMajorStrength("B-");
                    }
                    else user.setMajorStrength(strings[18]);
                    user.setSchoolLocation(strings[20]);
                    break;
                }
            }
        }
        return user;
    }

    @Override
    public List<SchoolAvail> getSchoolAvail(User user,int adjust) {
        int specialEstimation = specialMajor(user);
        ArrayList <SchoolAvail> sal = new ArrayList<>();
        //筛选参数
        Double totalPoints = user.getTotalPoints();
        String schoolLocation = user.getSchoolLocation();
        String majorStrength = user.getMajorStrength();
        Boolean fullTime = user.getFullTime();
        Boolean academicDegree = user.getAcademicDegree();
        String schoolType = user.getSchoolType();

        for (String[] strings : admissionsStatistics) {
            if (StringUtils.isEmpty(strings[6]) ||  StringUtils.isEmpty(strings[17])) continue;
            String majorNum = strings[3];
            if (specialEstimation == 1){
                if (!(majorNum.equals("125100") || majorNum.equals("125200") || majorNum.equals("125400") || majorNum.equals("125600")
                        || majorNum.equals("125300") || majorNum.equals("125500") || majorNum.equals("025700")))
                    continue;
            }
            if (specialEstimation == 2){
                if (!majorNum.equals("035101")) continue;
            }
            if (specialEstimation == 3){
                if (!majorNum.substring(0,4).equals("1051")) continue;
            }
            double averagePoints =Double.parseDouble(StringUtils.isEmpty(strings[10])?"0":strings[10]);
            String schoolLocationInExcel = strings[20];
            String academicDegreeInExcel = strings[19];
            String majorStrengthInExcel = StringUtils.isEmpty(strings[18])?"B-":strings[18];
            String fullTimeInExcel = strings[17];
            String schoolTypeInExcel = getSchoolType(strings[1]);


            if ((!fullTime && fullTimeInExcel.equals("全日制")) || (!academicDegree && academicDegreeInExcel.equals("学硕")))
                continue;

            if (averagePoints - 10 < totalPoints && averagePoints + 10 > totalPoints){
                if (schoolLocationRank.indexOf(schoolLocationInExcel) <= schoolLocationRank.indexOf(schoolLocation)){
                    if (majorStrengthRank.indexOf(majorStrengthInExcel) <= majorStrengthRank.indexOf(majorStrength)-adjust){
                        if (schoolTypeRank.indexOf(schoolTypeInExcel) <= schoolTypeRank.indexOf(schoolType)-adjust){
//                            System.out.println("发现可用学校：");
//                            System.out.println(Arrays.toString(strings));
                            boolean full = fullTimeInExcel.equals("全日制");
                            boolean academic = academicDegreeInExcel.equals("学硕");
                            sal.add(new SchoolAvail(strings[1],strings[3],schoolTypeInExcel,majorStrengthInExcel,full,academic,schoolLocationInExcel));
                        }
                    }
                }
            }
        }
//        System.out.println("=============参数选择后============");
//        for (SchoolAvail avail : sal) {
//            System.out.println(avail);
//        }
        return sal;
    }

    @Override
    public List<SchoolAvail> adjustType(User user,int type, List<SchoolAvail> schoolAvails) {
        int sameNum = 0;
        if (type == 1) sameNum = 6;
        else if (type == 2) sameNum = 4;
        else sameNum = 2;
        String majorNum = user.getMajorNum().substring(0,sameNum);
        List<SchoolAvail> res = new ArrayList<>();

        for (SchoolAvail schoolAvail : schoolAvails) {
            String majorNumTest = schoolAvail.getMajorNum().substring(0, sameNum);
            if (majorNum.equals(majorNumTest)) res.add(schoolAvail);
        }

//        if (res.size() == 0) System.out.println("未匹配到合适学校");
        res.sort(new Comparator<SchoolAvail>() {
            @Override
            public int compare(SchoolAvail o1, SchoolAvail o2) {
                if (o1.equals(o2)) return 0;

                if (schoolTypeRank.indexOf(o1.getSchoolType()) > schoolTypeRank.indexOf(o2.getSchoolType()))
                    return 1;
                if (schoolTypeRank.indexOf(o1.getSchoolType()) < schoolTypeRank.indexOf(o2.getSchoolType()))
                    return -1;

                if (majorStrengthRank.indexOf(o1.getMajorStrength()) > majorStrengthRank.indexOf(o2.getMajorStrength()))
                    return 1;
                if (majorStrengthRank.indexOf(o1.getMajorStrength()) < majorStrengthRank.indexOf(o2.getMajorStrength()))
                    return -1;

                if (o1.getFullTime() && !o2.getFullTime()) return 1;
                if (!o1.getFullTime() && o2.getFullTime()) return -1;

                if (o1.getAcademicDegree() && !o2.getAcademicDegree()) return 1;
                if (!o1.getAcademicDegree() && o2.getAcademicDegree()) return -1;

                if (schoolLocationRank.indexOf(o1.getSchoolLocation()) > schoolLocationRank.indexOf(o2.getSchoolLocation()))
                    return 1;
                if (schoolLocationRank.indexOf(o1.getSchoolLocation()) < schoolLocationRank.indexOf(o2.getSchoolLocation()))
                    return -1;

                return 0;
            }
        });
//        System.out.println("=============专业代码限制后============");
//        for (SchoolAvail re : res) {
//            System.out.println(re);
//        }
        return res;
    }

    @Override
    public String rechargeable(User user) {
        String res = "";
        int type = typeEstimation(user);
        if (type == 1)  res = typeFir(user);
        else if (type == 2) res = typeSec(user);
        else res = typeThr(user);
        return res;
    }
    @Override
    public String free(User user) {
        return null;
    }




    @Override
    public int typeEstimation(User user) {
        String majorNum = user.getMajorNum();
        String academicDegree = user.getAcademicDegree()?"学硕":"专硕";
        Double politicalPoints = user.getPoliticalPoints();
        Double foreignLanguages = user.getForeignLanguages();
        Double businessFir = user.getBusinessFir();
        Double businessSec = user.getBusinessSec();
        Double totalPoints = user.getTotalPoints();
        for (String[] item : passScore) {
            if (majorNum.equals(item[0]) && academicDegree.equals(item[12])){
                if (politicalPoints < Double.parseDouble(item[10]) || foreignLanguages < Double.parseDouble(item[10])
                        || businessFir < Double.parseDouble(item[11]) || businessSec < Double.parseDouble(item[11])
                        || totalPoints < Double.parseDouble(item[9]))
                    return 1;
                if (politicalPoints < Double.parseDouble(item[7]) || foreignLanguages < Double.parseDouble(item[7])
                        || businessFir < Double.parseDouble(item[8]) || businessSec < Double.parseDouble(item[8])
                        || totalPoints < Double.parseDouble(item[6]))
                    return 2;
            }
        }
        return 3;
    }

    @Override
    public String typeFir(User user) {
        String res = "总分或单科低于国家线B，综合评定：特别危险\n";
        addInformation(user);
        ArrayList <SchoolAvail> sal = new ArrayList<>();
        String majorNum = user.getMajorNum();
        for (String[] strings : admissionsStatistics) {
            if (StringUtils.isEmpty(strings[6]) || StringUtils.isEmpty(strings[17])) continue;
            if (majorNum.equals(strings[3]) && strings[20].equals("B")){
                String schoolLocationInExcel = strings[20];
                String academicDegreeInExcel = strings[19];
                String majorStrengthInExcel = StringUtils.isEmpty(strings[18])?"B-":strings[18];
                String fullTimeInExcel = strings[17];
                String schoolTypeInExcel = getSchoolType(strings[1]);
                boolean full = fullTimeInExcel.equals("全日制");
                boolean academic = academicDegreeInExcel.equals("学硕");
                sal.add(new SchoolAvail(strings[1],strings[3],schoolTypeInExcel,majorStrengthInExcel,full,academic,schoolLocationInExcel));
            }
        }
        String[]title = {"Result-精准专业调剂","Result-一级学科内调剂","Result-学科大类内调剂"};
        res += printTemplate(user,sal,title,false);

        return res;

    }

    @Override
    public String typeSec(User user) {
        String res = "总分或单科低于国家线A，综合评定：比较危险\n";
        addInformation(user);
        ArrayList <SchoolAvail> sal = new ArrayList<>();
        String majorNum = user.getMajorNum();
        for (String[] strings : admissionsStatistics) {
            if (StringUtils.isEmpty(strings[6])  || StringUtils.isEmpty(strings[17])) continue;
            if (majorNum.equals(strings[3]) && strings[20].equals("B")){
                String schoolLocationInExcel = strings[20];
                String academicDegreeInExcel = strings[19];
                String majorStrengthInExcel = StringUtils.isEmpty(strings[18])?"B-":strings[18];
                String fullTimeInExcel = strings[17];
                String schoolTypeInExcel = getSchoolType(strings[1]);
                boolean full = fullTimeInExcel.equals("全日制");
                boolean academic = academicDegreeInExcel.equals("学硕");
                sal.add(new SchoolAvail(strings[1],strings[3],schoolTypeInExcel,majorStrengthInExcel,full,academic,schoolLocationInExcel));
            }
        }
        String[]title = {"Result-精准专业调剂","Result-一级学科内调剂","Result-学科大类内调剂"};
        res += printTemplate(user,sal,title,false);

        return res;
    }

    @Override
    public String typeThr(User user) {
        String res = "";
        addInformation(user);
        List<SchoolAvail> schoolAvails = getSchoolAvail(user, 0);    //非滑档
        String[]title = {"Result-非滑档调剂-精准专业调剂","Result-非滑档调剂-一级学科内调剂","Result-非滑档调剂-学科大类内调剂"};
        res += printTemplate(user,schoolAvails,title,true);
        schoolAvails = getSchoolAvail(user, 1);    //滑档
//        for (SchoolAvail schoolAvail : schoolAvails) {
//            System.out.println("test"+schoolAvail);
//        }
        title = new String[]{"Result-滑档调剂-精准专业调剂","Result-滑档调剂-一级学科内调剂","Result-滑档调剂-学科大类内调剂"};
        res += printTemplate(user,schoolAvails,title,true);
        return res;


//        String res = "";
//        addInformation(user);
//        List<Cell> headerSchool = new ArrayList<Cell>(){{
//            add(new Cell(Align.CENTER,"学校代码"));
//            add(new Cell(Align.CENTER,"学校名称"));
//            add(new Cell(Align.CENTER,"学院"));
//            add(new Cell(Align.CENTER,"专业代码"));
//            add(new Cell(Align.CENTER,"专业名称"));
//            add(new Cell(Align.CENTER,"录取人数"));
//            add(new Cell(Align.CENTER,"调剂录取人数"));
//            add(new Cell(Align.CENTER,"一志愿录取人数"));
//            add(new Cell(Align.CENTER,"调试初试最高分"));
//            add(new Cell(Align.CENTER,"调试初试最低分"));
//            add(new Cell(Align.CENTER,"调试初试平均分"));
//            add(new Cell(Align.CENTER,"一志愿初试最高分"));
//            add(new Cell(Align.CENTER,"一志愿初试最低分"));
//            add(new Cell(Align.CENTER,"一志愿初试平均分"));
//            add(new Cell(Align.CENTER,"调剂生来源"));
//            add(new Cell(Align.CENTER,"复试方案"));
//            add(new Cell(Align.CENTER,"调剂公告"));
//            add(new Cell(Align.CENTER,"学习方式"));
//            add(new Cell(Align.CENTER,"评估类型"));
//            add(new Cell(Align.CENTER,"学位类型"));
//            add(new Cell(Align.CENTER,"国家线类型"));
//            add(new Cell(Align.CENTER,"地区"));
//        }};     //录取分析统计表.csv
//        List<Cell> headerStudent = new ArrayList<Cell>(){{
//            add(new Cell("姓名"));
//            add(new Cell("准考证号"));
//            add(new Cell("一志愿学校"));
//            add(new Cell("学校名称"));
//            add(new Cell("学校代码"));
//            add(new Cell("学院名称"));
//            add(new Cell("专业名称"));
//            add(new Cell("专业代码"));
//            add(new Cell("初试成绩"));
//            add(new Cell("复试成绩"));
//            add(new Cell("总成绩"));
//            add(new Cell("学习方式"));
//        }};     //调剂录取名单.csv
//        List<Cell> headerTest = new ArrayList<Cell>(){{
//            add(new Cell("学校名"));
//            add(new Cell("学院名"));
//            add(new Cell("学院代码"));
//            add(new Cell("专业名"));
//            add(new Cell("专业代码"));
//            add(new Cell("初试科目"));
//            add(new Cell("总分"));
//            add(new Cell("复试线=100"));
//            add(new Cell("复试线>100"));
//            add(new Cell("复试科目"));
//            add(new Cell("加试科目"));
//            add(new Cell("学校类型"));
//            add(new Cell("学位类型"));
//            add(new Cell("学习方式"));
//            add(new Cell("评估类型"));
//            add(new Cell("关键字"));
//            add(new Cell("地区"));
//            add(new Cell("备注"));
//        }};         //初试考试信息表.csv
//        List<SchoolAvail> schoolAvails = getSchoolAvail(user, 0);    //非滑档
//        //
//        for (int k = 0; k < 3; k++) {
//            if (k==0)res += "Result-非滑档调剂-精准专业调剂\n";
//            else if (k==1) res += "Result-非滑档调剂-一级学科内调剂\n";
//            else  res += "Result-非滑档调剂-学科大类内调剂\n";
//
//            List<SchoolAvail> itemsAvail = adjustType(user, k+1, schoolAvails);
//            for (int i = 0; i < itemsAvail.size(); i++) {
//                if (i >= 3) break;
//                String schoolName = itemsAvail.get(i).getSchoolName();
//                String majorNum = itemsAvail.get(i).getMajorNum();
//                String full = itemsAvail.get(i).getFullTime()?"全日制":"非全日制";
//                String academic = itemsAvail.get(i).getAcademicDegree()?"学硕":"专硕";
//                for (String[] item : admissionsStatistics) {
//                    if (schoolName.equals(item[1]) && majorNum.equals(item[3]) && full.equals(item[17]) && academic.equals(item[19])){
//                        ArrayList<Cell> cellList = new ArrayList<>();
//                        for (int j = 0; j < item.length; j++) {
//                            cellList.add(new Cell(Align.CENTER,item[j]));
//                        }
////                        body.add(cellList);
//                        res += "Item"+ (i+1) + "\n" + "调剂情况分析：\n";
//                        res += new ConsoleTable.ConsoleTableBuilder().addHeaders(headerSchool).addRow(cellList).build().getContent() + "\n";
//                        break;
//                    }
//                }
//                List<List<Cell>> body = new ArrayList<List<Cell>>();
//                for (String[] item : admissionList) {
//                    if (body.size() >= 5) break;
//                    if (schoolName.equals(item[3])  && majorNum.equals(item[7]) && full.equals(item[11])){
//                        ArrayList<Cell> cellList = new ArrayList<>();
//                        for (int j = 0; j < item.length; j++) {
//                            cellList.add(new Cell(Align.CENTER,item[j]));
//                        }
//                        body.add(cellList);
//                    }
//                }
//                if (body.size()>0){
//                    res += "录取名单显示：\n";
//                    res += new ConsoleTable.ConsoleTableBuilder().addHeaders(headerStudent).addRows(body).build().getContent() + "\n";
//                }
//
//                for (String[] item : firTestInform) {
//                    if (schoolName.equals(item[0]) && majorNum.equals(item[4])){
//                        ArrayList<Cell> cellList = new ArrayList<>();
//                        for (int j = 0; j < item.length; j++) {
//                            cellList.add(new Cell(Align.CENTER,item[j]));
//                        }
////                        body.add(cellList);
//                        res += "该专业相关复试与初试的考试情况：\n";
//                        res += new ConsoleTable.ConsoleTableBuilder().addHeaders(headerTest).addRow(cellList).build().getContent() + "\n";
//                        break;
//                    }
//                }
//            }
//        }
//        return res;
    }

    @Override
    public String printTemplate(User user,List<SchoolAvail> schoolAvails,String[]title,boolean order) {
        String res = "";
//        addInformation(user);
        List<Cell> headerSchool = new ArrayList<Cell>(){{
            add(new Cell(Align.CENTER,"学校代码"));
            add(new Cell(Align.CENTER,"学校名称"));
            add(new Cell(Align.CENTER,"学院"));
            add(new Cell(Align.CENTER,"专业代码"));
            add(new Cell(Align.CENTER,"专业名称"));
            add(new Cell(Align.CENTER,"录取人数"));
            add(new Cell(Align.CENTER,"调剂录取人数"));
            add(new Cell(Align.CENTER,"一志愿录取人数"));
            add(new Cell(Align.CENTER,"调试初试最高分"));
            add(new Cell(Align.CENTER,"调试初试最低分"));
            add(new Cell(Align.CENTER,"调试初试平均分"));
            add(new Cell(Align.CENTER,"一志愿初试最高分"));
            add(new Cell(Align.CENTER,"一志愿初试最低分"));
            add(new Cell(Align.CENTER,"一志愿初试平均分"));
            add(new Cell(Align.CENTER,"调剂生来源"));
            add(new Cell(Align.CENTER,"复试方案"));
            add(new Cell(Align.CENTER,"调剂公告"));
            add(new Cell(Align.CENTER,"学习方式"));
            add(new Cell(Align.CENTER,"评估类型"));
            add(new Cell(Align.CENTER,"学位类型"));
            add(new Cell(Align.CENTER,"国家线类型"));
            add(new Cell(Align.CENTER,"地区"));
        }};     //录取分析统计表.csv
        List<Cell> headerStudent = new ArrayList<Cell>(){{
            add(new Cell("姓名"));
            add(new Cell("准考证号"));
            add(new Cell("一志愿学校"));
            add(new Cell("学校名称"));
            add(new Cell("学校代码"));
            add(new Cell("学院名称"));
            add(new Cell("专业名称"));
            add(new Cell("专业代码"));
            add(new Cell("初试成绩"));
            add(new Cell("复试成绩"));
            add(new Cell("总成绩"));
            add(new Cell("学习方式"));
        }};     //调剂录取名单.csv
        List<Cell> headerTest = new ArrayList<Cell>(){{
            add(new Cell("学校名"));
            add(new Cell("学院名"));
            add(new Cell("学院代码"));
            add(new Cell("专业名"));
            add(new Cell("专业代码"));
            add(new Cell("初试科目"));
            add(new Cell("总分"));
            add(new Cell("复试线=100"));
            add(new Cell("复试线>100"));
            add(new Cell("复试科目"));
            add(new Cell("加试科目"));
            add(new Cell("学校类型"));
            add(new Cell("学位类型"));
            add(new Cell("学习方式"));
            add(new Cell("评估类型"));
            add(new Cell("关键字"));
            add(new Cell("地区"));
            add(new Cell("备注"));
        }};         //初试考试信息表.csv
        for (int k = 0; k < 3; k++) {
            if (k==0)res += title[0]+"\n";
            else if (k==1) res += title[1]+"\n";
            else  res += title[2]+"\n";
            List<SchoolAvail> itemsAvail = adjustType(user, k+1, schoolAvails);
            for (int i = 0; i < itemsAvail.size(); i++) {
                if (i >= 10) break;
                int index = order?i: itemsAvail.size()-1-i;
                String schoolName = itemsAvail.get(index).getSchoolName();
                String majorNum = itemsAvail.get(index).getMajorNum();
                String full = itemsAvail.get(index).getFullTime()?"全日制":"非全日制";
                String academic = itemsAvail.get(index).getAcademicDegree()?"学硕":"专硕";
                for (String[] item : admissionsStatistics) {
                    if (schoolName.equals(item[1]) && majorNum.equals(item[3]) && full.equals(item[17]) && academic.equals(item[19])){
                        ArrayList<Cell> cellList = new ArrayList<>();
                        for (int j = 0; j < item.length; j++) {
                            cellList.add(new Cell(Align.CENTER,item[j]));
                        }
//                        body.add(cellList);
                        res += "Item"+ (i+1) + "\n" + "调剂情况分析：\n";
                        res += new ConsoleTable.ConsoleTableBuilder().addHeaders(headerSchool).addRow(cellList).build().getContent() + "\n";
                        break;
                    }
                }
                List<List<Cell>> body = new ArrayList<List<Cell>>();
                for (String[] item : admissionList) {
                    if (body.size() >= 5) break;
                    if (schoolName.equals(item[3])  && majorNum.equals(item[7]) && full.equals(item[11])){
                        ArrayList<Cell> cellList = new ArrayList<>();
                        for (int j = 0; j < item.length; j++) {
                            cellList.add(new Cell(Align.CENTER,item[j]));
                        }
                        body.add(cellList);
                    }
                }
                if (body.size()>0){
                    res += "录取名单显示：\n";
                    res += new ConsoleTable.ConsoleTableBuilder().addHeaders(headerStudent).addRows(body).build().getContent() + "\n";
                }

                for (String[] item : firTestInform) {
                    if (schoolName.equals(item[0]) && majorNum.equals(item[4])){
                        ArrayList<Cell> cellList = new ArrayList<>();
                        for (int j = 0; j < item.length; j++) {
                            cellList.add(new Cell(Align.CENTER,item[j]));
                        }
//                        body.add(cellList);
                        res += "该专业相关复试与初试的考试情况：\n";
                        res += new ConsoleTable.ConsoleTableBuilder().addHeaders(headerTest).addRow(cellList).build().getContent() + "\n";
                        break;
                    }
                }
            }

        }
        return res;
    }

    @Override
    public int specialMajor(User user) {
        String majorNum = user.getMajorNum();
        if (majorNum.equals("125100") || majorNum.equals("125200") || majorNum.equals("125400") || majorNum.equals("125600")
                || majorNum.equals("125300") || majorNum.equals("125500") || majorNum.equals("025700"))
            return 1;
        if (majorNum.equals("035101")) return 2;
        if (majorNum.substring(0,4).equals("1051")) return 3;
        return 4;
    }


    public static void main(String[] args) {
        RechargeableServiceImpl rs = new RechargeableServiceImpl();
        User user = new User("","内蒙古工业大学","085500","",true,false,54.0,38.0,88.0,88.0,268.0);
//        System.out.println(rs.addInformation(user));
        System.out.println(rs.rechargeable(user));




    }
}
