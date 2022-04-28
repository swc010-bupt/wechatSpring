package com.example.test.controller;

import com.example.test.bean.Form;
import com.example.test.bean.loginForm;
import com.example.test.bean.mUser;
import com.example.test.bo.PaymentBO;
import com.example.test.service.PayService;
import com.example.test.service.formService;
import com.example.test.service.loginService;
import com.example.test.utils.*;
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
@RequestMapping("/")
public class HoganoController {
    @RequestMapping("/")
    public String hogano(){
        return "login";
    }
}
