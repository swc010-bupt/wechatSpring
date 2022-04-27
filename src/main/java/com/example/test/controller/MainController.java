package com.example.test.controller;

import com.example.test.bean.Form;
import com.example.test.bean.loginForm;
import com.example.test.bean.mUser;
import com.example.test.bo.PaymentBO;
import com.example.test.service.PayService;
import com.example.test.service.formService;
import com.example.test.service.loginService;
import com.example.test.utils.*;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import static com.example.test.utils.pdfUtils.ZipFile;

@Controller
@RequestMapping("system")
public class MainController {
    static String path = System.getProperty("user.dir");

    @Autowired
    private loginService loginService;

    @Autowired
    private formService formService;


    //跳转到登录页面
    @RequestMapping("login")
    public String login(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return "login";
        }
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals("user_id")){
                mUser mUser = loginService.selectmUserById(cookie.getValue());

                System.out.println(cookie.getValue());
                return "stuInfo";
            }else {
                //这个方法返回false表示忽略当前请求，如果一个用户调用了需要登陆才能使用的接口，如果他没有登陆这里会直接忽略掉
                //当然你可以利用response给用户返回一些提示信息，告诉他没登陆
                System.out.println("meridenglu");
                return "login";
            }
        }
        return "login";
    }
    //*****************************学生操作相关的************************//
    //跳转到学生信息提交页面
    @RequestMapping("stuInfo")
    public String stuInfo(){
        return "stuInfo";
    }
    //跳转到免费下载页面
    @RequestMapping("free")
    public String free(){
        return "free";
    }
    //跳转到付费下载页面
    @RequestMapping("payDownload")
    public String payDownload(){
        return "payDownload";
    }
    /**
     *  跳转到支付页面
     * */
    @RequestMapping("/gotoPay")
    public String gotoPay(){
        return "payPage";
    }
    //跳转到选择是否支付页面
    @RequestMapping("switchPage")
    public String switchPage(){
        return "switchPage";
    }
    //跳转到下载成功页面
    @RequestMapping("success")
    public String success(){
        return "success";
    }
    //跳转到企业操作页面
    @RequestMapping("/forCompany")
    public String testExcel(){
        return "excelTest";
    }
    @RequestMapping("reg")
    public String reg(){
        return "register";
    }
    @RequestMapping("temp")
    public String temp(){
        return "temp";
    }
    @PostMapping("submitInfo")
    @ResponseBody
    public Map<String,String> submitInfo(Form form,HttpServletRequest request) throws IllegalAccessException, IOException {
        System.out.println(form.getOriginSchool());
        Map<String,String> map = new HashMap<>();

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNowStr = sdf.format(d);
        System.out.println("用户插入时间点：" + dateNowStr);

        form.setStuName(form.getStuName()+"; 插入时间："+dateNowStr);
        String stuName = form.getStuName();
        System.out.println(stuName);
        int flag = formService.insert(form);
        if (flag != 0){
            {
                /**********处理Form的添加位置，也就是推荐算法添加的位置**********/

                //后期用来处理学生提交的报名信息，
            }
            pdfUtils pdfUtils = new pdfUtils();
            boolean flag2 = pdfUtils.getPdfPay(form,0, request);
            boolean flag1 = pdfUtils.getPdfFree(form,0,request);
            pdfUtils = null;
            System.gc();
            if (flag1&&flag2){
                map.put("result", "true");
                map.put("view","switchPage");
            }else {
                map.put("result", "false");
            }
        }
        else {
            map.put("result", "false");
        }
        return map;
    }

    @RequestMapping("/userLogin")
    public String userLogin(@RequestParam("user_id") String userId,
                            @RequestParam("password") String password,
                            HttpServletRequest request,
                            HttpServletResponse response){
        mUser mUser = loginService.selectmUserByIdAndPSWD(userId,password);
        if (mUser == null){
            return "fail";
        }
        request.getSession().setAttribute("user_id",userId);
//
        Cookie cookie = new Cookie("user_id",userId);
        response.addCookie(cookie);
        int userType = mUser.getUserType();
        switch (userType){
            // 学生
            case 0:
                return "stuInfo";
            case 1:
                return "excelTest";
            default:
                return "temp";
        }
    }
    // 这个地方后面要写两个函数，用来表示免费下载、付费下载通道；
    @RequestMapping("/downloadPdfFree")
    @ResponseBody
    public String downloadPdfFree(HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException {
        Cookie[] cookies = request.getCookies();

        HttpSession session = request.getSession();
        String authCodeReal = (String) session.getAttribute("authCode");

        String authCodeUser = request.getParameter("authCode");
        authCodeUser = authCodeUser.toLowerCase();

        if (authCodeReal.equals(authCodeUser)){
            System.out.println("验证正确！");
            String userId = "";
            // 获取指定目录下的第一个文件
            if(cookies == null){
                return null;
            }
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user_id")){
                    userId = cookie.getValue();
                }
            }
            System.out.println(userId);
            String fileName= userId+"_free.pdf"; //下载的文件名
            // 如果文件名不为空，则进行下载
            //设置文件路径
            String realPath = path+"\\src\\main\\resources\\pdfs\\";
            authCodeReal=null;
            authCodeUser=null;
            return fileUtil.download(response,fileName,realPath);
        }else {
            return "验证码错误，请返回上级并刷新页面！";
        }
    }

    @RequestMapping("/downloadPdfPay")
    @ResponseBody
    public String downloadPdfPay(HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException {
        Cookie[] cookies = request.getCookies();

        HttpSession session = request.getSession();
        String authCodeReal = (String) session.getAttribute("authCode");

        String authCodeUser = request.getParameter("authCode");
        authCodeUser = authCodeUser.toLowerCase();

        if (authCodeReal.equals(authCodeUser)){
            System.out.println("验证正确！");
            String userId = "";
            // 获取指定目录下的第一个文件
            if(cookies == null){
                return null;
            }
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user_id")){
                    userId = cookie.getValue();
                }
            }
            System.out.println(userId);
            String fileName= userId+"_pay.pdf"; //下载的文件名
            // 如果文件名不为空，则进行下载
            //设置文件路径
            String realPath = path+"\\src\\main\\resources\\pdfs\\";
            return fileUtil.download(response,fileName,realPath);
        }else {
            return "验证码错误，请返回上级并刷新页面！";
        }
    }

    @RequestMapping("/download_zip")
    @ResponseBody
    public String download_zip(HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException {
        Cookie[] cookies = request.getCookies();
        String userId = "";
        // 获取指定目录下的第一个文件
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("user_id")){
                userId = cookie.getValue();
            }
        }
        System.out.println(userId);
        String fileName= userId+".zip"; //下载的文件名

        // 如果文件名不为空，则进行下载
        //设置文件路径
        String realPath = path+"\\src\\main\\resources\\pdfs\\";
        return fileUtil.download(response,fileName,realPath);
    }

    @RequestMapping("/downloadTxt")
    @ResponseBody
    public String downloadTxt(HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException {
        String userId = request.getParameter("stunum");
        // 获取指定目录下的第一个文件
        System.out.println(userId);
        String fileName= userId+".txt"; //下载的文件名

        // 如果文件名不为空，则进行下载
        //设置文件路径
        String realPath = path+"\\src\\main\\resources\\txt\\";
        return fileUtil.download(response,fileName,realPath);
    }

    @RequestMapping("/download_model")
    @ResponseBody
    public String download_model(HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException {
        String fileName= "model.xlsx"; //下载的文件名

        // 如果文件名不为空，则进行下载
        //设置文件路径
        String realPath = path+"\\src\\main\\resources\\model\\";
        return fileUtil.download(response,fileName,realPath);
    }

    @RequestMapping("/importExcel")
    @ResponseBody
    public String importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("file"+file.getSize());
        try {
            int userType = 1;
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
            XSSFSheet sheetAt = wb.getSheetAt(0);
            Form form;
            String[] strings = new String[13];
            int index = 0;
            for (Row row : sheetAt) {
                if (index == 0) {
                    index++;
                    continue;
                }
                for (int i = 0;i<13;i++){
                    row.getCell(i).setCellType(CellType.STRING);
                    strings[i] = row.getCell(i).getStringCellValue();
                }
                form = new Form(strings);
                form.setId((int) (10000*Math.random()));
                {
                    //后期用来批量处理学生提交的报名信息，
                }
                pdfUtils pdfUtils = new pdfUtils();
                pdfUtils.getPdfPay(form,userType,request);
                pdfUtils = null;
                System.gc();
            }

            Cookie[] cookies = request.getCookies();
            String userId = "";
            // 获取指定目录下的第一个文件
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user_id")){
                    userId = cookie.getValue();
                }
            }

            String filePath = path+"\\src\\main\\resources\\pdfs\\"+userId+"\\";//需要压缩的文件夹完整路径
            String fileName = userId;//需要压缩的文件夹名
            String outPath = path+"\\src\\main\\resources\\pdfs\\"+userId+".zip\\";//压缩完成后保存为Test.zip文件，名字随意
            OutputStream os = new FileOutputStream(outPath);//创建Test.zip文件
            CheckedOutputStream cos = new CheckedOutputStream(os, new CRC32());//检查输出流,采用CRC32算法，保证文件的一致性
            ZipOutputStream zos = new ZipOutputStream(cos);//创建zip文件的输出流
            zos.setEncoding("GBK");//设置编码，防止中文乱码
            File file2 = new File(filePath);//需要压缩的文件或文件夹对象

            ZipFile(zos,file2,fileName);//压缩文件的具体实现函数
            zos.close();
            cos.close();
            os.close();
            System.out.println("压缩完成");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "上传成功";
    }

    @Autowired
    private PayService payService;

    @ResponseBody
    @GetMapping(value = "/confirm" , produces = {"text/html;charset=UTF-8"})
    public Object pay (@RequestParam(required = false) PaymentBO bo) throws Exception {
        //这个接口其实应该是post方式的，但是我这里图方便，直接以get方式访问，
        //且返回格式是text/html，这样前端页面就能直接显示支付宝返回的html片段
        //真实场景下由post方式请求，返回code、msg、data那种格式的标准结构，让前端拿到data里的
        //html片段之后自行加载

        //由于我这里并没有真正的传参数，所以象征性的new一下，避免空指针
        bo = new PaymentBO();
        return payService.pay(bo);
    }

    private final char[] codeSequence = { 'A', '1','B', 'C', '2','D','3', 'E','4', 'F', '5','G','6', 'H', '7','I', '8','J',
            'K',   '9' ,'L', '1','M',  '2','N',  'P', '3', 'Q', '4', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z'};
    // 验证码生成程序
    @RequestMapping("/code")
    public void getCode(HttpServletResponse response, HttpSession session) throws IOException{
        int width = 63;
        int height = 37;
        Random random = new Random();
        //设置response头信息
        //禁止缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        //生成缓冲区image类
        BufferedImage image = new BufferedImage(width, height, 1);
        //产生image类的Graphics用于绘制操作
        Graphics g = image.getGraphics();
        //Graphics类的样式
        g.setColor(this.getColor(200, 250));
        g.setFont(new Font("Times New Roman",0,28));
        g.fillRect(0, 0, width, height);
        //绘制干扰线
        for(int i=0;i<40;i++){
            g.setColor(this.getColor(130, 200));
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int x1 = random.nextInt(12);
            int y1 = random.nextInt(12);
            g.drawLine(x, y, x + x1, y + y1);
        }

        //绘制字符
        StringBuilder strCode = new StringBuilder();
        for(int i=0;i<4;i++){
            String rand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            strCode.append(rand);
            g.setColor(new Color(20+random.nextInt(110),20+random.nextInt(110),20+random.nextInt(110)));
            g.drawString(rand, 13*i+6, 28);
        }
        //将字符保存到session中用于前端的验证
        session.setAttribute("authCode", strCode.toString().toLowerCase());
        g.dispose();

        ImageIO.write(image, "JPEG", response.getOutputStream());
        response.getOutputStream().flush();
    }

    public Color getColor(int fc,int bc){
        Random random = new Random();
        if(fc>255)
            fc = 255;
        if(bc>255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r,g,b);
    }

    @PostMapping("/register")
    @ResponseBody
    public Map<String,String> register(loginForm loginForm){
        Map<String,String> map = new HashMap<>();
        mUser mUser = new mUser();
        mUser.setPassword(loginForm.getPassword());
        mUser.setUserId(loginForm.getUser_id());
        mUser.setUserName(loginForm.getUser_id());
        mUser.setUserType(0);
        mUser mUser2 = loginService.selectmUserById(loginForm.getUser_id());
        if (mUser2 == null){
            loginService.insert(mUser);
            map.put("result","true");
            map.put("view","login");
        }else {
            map.put("result","false");
        }
        return map;
    }


    //获取授权码
    @GetMapping("login2")
    public String genQrConnect(HttpSession session) {

        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        // 回调地址
        String redirectUrl = "http://101.43.159.242/system/callback"; //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error");
        }

        // 防止csrf攻击（跨站请求伪造攻击）
        //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
        String state = "imhelen";//为了让大家能够使用我搭建的外网的微信回调跳转服务器，这里填写你在ngrok的前置域名
        System.out.println("state = " + state);

        // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
        //键："wechar-open-state-" + httpServletRequest.getSession().getId()
        //值：satte
        //过期时间：30分钟

        //生成qrcodeUrl
        String qrcodeUrl = String.format(
                baseUrl,
                "wx24a5ce02b6a6c154",
                redirectUrl,
                state);

        return "redirect:" + qrcodeUrl;
    }

    //扫码后回调
    /**
     * @param code
     * @param state
     * @return
     */
    @GetMapping("callback")
    public String callback(String code, String state){

        //得到授权临时票据code
        System.out.println(code);
        System.out.println(state);

        //从redis中将state获取出来，和当前传入的state作比较
        //如果一致则放行，如果不一致则抛出异常：非法访问

        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        String accessTokenUrl = String.format(baseAccessTokenUrl,
                "wx24a5ce02b6a6c154",
                "e92724fe22f0ab7ca1cd6c7b4ca0bd6e",
                code);

        String result = null;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accessToken=============" + result);
        } catch (Exception e) {
//            throw new GuliException(20001, "获取access_token失败");
            System.out.println("获取access_token失败");
        }

        //解析json字符串
        Gson gson = new Gson();
        HashMap map = gson.fromJson(result, HashMap.class);
        String accessToken = (String)map.get("access_token");
        String openid = (String)map.get("openid");

        //查询数据库当前用用户是否曾经使用过微信登录
//        Member member = memberService.getByOpenid(openid);
        System.out.println("新用户注册");

        //访问微信的资源服务器，获取用户信息
        String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                "?access_token=%s" +
                "&openid=%s";
        String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
        String resultUserInfo = null;
        try {
            resultUserInfo = HttpClientUtils.get(userInfoUrl);
            System.out.println("resultUserInfo==========" + resultUserInfo);
        } catch (Exception e) {
//                throw new GuliException(20001, "获取用户信息失败");
            System.out.println("获取用户信息失败");
        }

        //解析json
        HashMap<String, Object> mapUserInfo = gson.fromJson(resultUserInfo, HashMap.class);
        String nickname = (String)mapUserInfo.get("nickname");
        String headimgurl = (String)mapUserInfo.get("headimgurl");

        //向数据库中插入一条记录
//            member = new Member();
//            member.setNickname(nickname);
//            member.setOpenid(openid);
//            member.setAvatar(headimgurl);
//            memberService.save(member);


//        if(member == null){
//
//        }

        //TODO 登录

        return "redirect:http://101.43.159.242/system/login";
    }

    @RequestMapping("/weChatToken")
    public void weChat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isGet = request.getMethod().toLowerCase().equals("get");
        if (isGet) {
            // 微信加密签名
            String signature = request.getParameter("signature");
            // 时间戳
            String timestamp = request.getParameter("timestamp");
            // 随机数
            String nonce = request.getParameter("nonce");
            // 随机字符串
            String echostr = request.getParameter("echostr");
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (signature != null && CheckoutUtil.checkSignature(signature, timestamp, nonce)) {
                try {
                    PrintWriter print = response.getWriter();
                    print.write(echostr);
                    print.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
