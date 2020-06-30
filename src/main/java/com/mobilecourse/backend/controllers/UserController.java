package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@RestController
@EnableAutoConfiguration
//设置此参数可以给这个类的所有接口都加上前缀
@RequestMapping("/user")
public class UserController extends CommonController {

    @Autowired
    private UserDao userMapper;

    // 用来给普通类调用mapper
    public static UserController userController;

    @PostConstruct
    public void init() {
        userController = this;
        // 用来给普通类调用mapper
        userController.userMapper = this.userMapper;
    }

    // 普通请求，不指定method意味着接受所有类型的请求
    @RequestMapping(value = "/hello")
    public String hello() {
        return wrapperMsg(200, "当前数据库中共有：" + userController.userMapper.userCnt() + "个注册用户！");
    }

    public User getUserByUid(int uid) {
        return userController.userMapper.getUserByID(uid);
    }

    // 根据name查找并返回用户信息（不敏感的）
    @RequestMapping(value = "/find/{name}", method = {RequestMethod.GET})
    public String findUser(@PathVariable String name) {
        try {
            User user = userController.userMapper.getUserByName(name);
            if (user == null) return wrapperMsg(403, "没有找到对应的用户！");
            JSONObject jsonObject = getJsonUserObject(user);
            return wrapperMsg(200, jsonObject.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 更新：根据uid查找并返回用户信息（不敏感的）
    @RequestMapping(value = "/findByUid/{uid}", method = {RequestMethod.GET})
    public String findUser(@PathVariable int uid) {
        try {
            User user = userController.userMapper.getUserByID(uid);
            if (user == null) return wrapperMsg(403, "没有找到对应的用户！");
            JSONObject jsonObject = getJsonUserObject(user);
            return wrapperMsg(200, jsonObject.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 查询所有数据，最好给内部加上异常捕获，方便正确处理异常
    @RequestMapping("/selectAll")
    public String selectAll() {
        try {
            JSONArray jsonArray = new JSONArray();
            List<User> list = userController.userMapper.selectAll();
            if (list.isEmpty()) return wrapperMsg(403, "数据库中暂无用户！");
            for (User u : list) {
                jsonArray.add(getJsonUserObject(u));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 注册账户，设置为只能使用PUT方式访问
    @RequestMapping(value = "/register", method = {RequestMethod.PUT})
    public String register(@RequestParam(value = "name") String name,
                           @RequestParam(value = "email") String email,
                           @RequestParam(value = "password") String password) {
        try {
            User u = userController.userMapper.getUserByName(name);
            if (u != null) {
                return wrapperMsg(403, "数据库中已存在同名用户，请更改用户名重新注册！");
            }
            if (userController.userMapper.getUserByEmail(email) != null) {
                return wrapperMsg(403, "此邮箱已被注册，请激活账户或直接登陆！");
            }

            u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPassword(password);
            u.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            u.setAvatar(Constants.DEFAULT_AVATAR_NAME);
            // 生成随机的用户验证码
            String code = UUID.randomUUID().toString().replace("-", "");
            u.setStatus(code);
            userController.userMapper.register(u);

            // 向用户发送激活邮件
            sendMail(email, code);

            return wrapperMsg(201, name + " Successfully Registered! Please check your email for activation.");
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据激活码查找对应的账户并激活（将status设置为用户名）
    @RequestMapping(value = "/activate")
    public String activate(@RequestParam(value = "code") String code) {
        User user = userController.userMapper.getUserByCode(code);
        if (user != null) {
            user.setStatus(user.getName());
            userController.userMapper.updateUserSelectiveByID(user);   // 完事以后记得还要update
            return wrapperMsg(200, "Activation Success! You may login now.");
        } else {
            return wrapperMsg(403, "找不到对应的未激活用户，可能该用户已激活或验证码有误。");
        }
    }

    // todo: 注销账户：删除当前登陆账户所有信息（相关订单、聊天记录和账户）

    // 带查询参数的GET请求
    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    public String login(HttpServletRequest request,
                        @RequestParam(value = "name") String name,
                        @RequestParam(value = "password") String password) {
        User user = userController.userMapper.getUserByName(name);
        if (user != null && user.getPassword().equals(password)) {
            if (!user.getStatus().equals(user.getName())) {
                // 用户已注册但尚未激活
                return wrapperMsg(403, "请先检查邮箱完成激活！");
            } else {
                // 登陆成功，将uid放到session中
                putInfoToSession(request, "uid", user.getUid());
                System.out.println("uid: " + user.getUid() + "登录成功！SessionId: " + request.getSession().getId());
                JSONObject wrapperMsg = new JSONObject();
                wrapperMsg.put("code", 200);
                wrapperMsg.put("msg", user.getName() + " has successfully logged in!");
                wrapperMsg.put("uid", user.getUid());
                return wrapperMsg.toJSONString();
            }
        } else {
            return wrapperMsg(403, "用户名或密码错误，请重新输入！");
        }
    }

    // 登出，接受所有类型请求
    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        // System.out.println("Logout: Session's uid: " + request.getSession().getAttribute("uid"));
        System.out.println("uid: " + request.getSession().getAttribute("uid") + "已下线！");
        removeInfoFromSession(request, "uid");
        return wrapperMsg(200, "Logout Success");
    }

    // 根据当前session中的uid更新账户信息：昵称/邮箱/密码
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public String updateUserInfo(HttpServletRequest request,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "email", required = false) String email,
                                 @RequestParam(value = "password", required = false) String password) {
        User u = new User();
        u.setUid((int) request.getSession().getAttribute("uid"));
        // 没有接收到的参数均为null，在SQL语句中会被忽略
        u.setName(name);
        u.setEmail(email);
        u.setPassword(password);
        userController.userMapper.updateUserSelectiveByID(u);
        return wrapperMsg(200, "Update Success");
    }

    // 上传/更新用户头像
    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public String uploadAvatar(HttpServletRequest request,
                               @RequestParam(value = "file") MultipartFile file) {
        // 获取当前session的uid
        int uid = (int) request.getSession().getAttribute("uid");
        User u = userController.userMapper.getUserByID(uid);
        // 图片存储路径
        File avatarPathFile = new File(Constants.AVATAR_PATH);
        if (!avatarPathFile.exists()) {
            avatarPathFile.mkdirs();
        }

        // 如果已经有头像了，删除原来的头像
        if (!u.getAvatar().equals(Constants.DEFAULT_AVATAR_NAME)) {
            File oldFile = new File(avatarPathFile.getAbsolutePath() + File.separator + u.getAvatar());
            if (oldFile.exists() && oldFile.isFile()) {
                oldFile.delete();
            }
        }

        if (file == null) return wrapperMsg(403, "请上传文件");
        // 原始文件名
        String originalFileName = file.getOriginalFilename();
        // 获取图片后缀
        assert originalFileName != null;
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 生成图片存储的名称，UUID 避免相同图片名冲突，并加上图片后缀
        String fileName = UUID.randomUUID().toString() + suffix;
        try {
            // 构建真实的文件路径（这里的绝对路径是相当于当前项目的路径而不是“容器”路径）
            File saveFile = new File(avatarPathFile.getAbsolutePath() + File.separator + fileName);
            // 将上传的文件保存到服务器文件系统
            file.transferTo(saveFile);
            // 将图片名称记录到数据库中
            u.setAvatar(fileName);
            userController.userMapper.updateUserSelectiveByID(u);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(file.getOriginalFilename() + "已存储到" + avatarPathFile.getAbsolutePath() + File.separator + fileName);
        return wrapperMsg(200, file.getOriginalFilename() + " Uploaded");
    }

    // 下载当前session用户头像
    @RequestMapping(value = "/download")
    public void downloadAvatar(HttpServletRequest request,
                               HttpServletResponse response) {
        // 获取当前session的uid
        int uid = (int) request.getSession().getAttribute("uid");
        String avatar = userController.userMapper.getUserByID(uid).getAvatar();
        File avatarFile = new File(Constants.AVATAR_PATH + File.separator + avatar);
        responseFile(response, avatarFile);
    }

    // 下载指定uid用户头像
    @RequestMapping(value = "/download/{uid}")
    public void downloadAvatarByUid(HttpServletResponse response,
                                    @PathVariable int uid) {
        User user = userController.userMapper.getUserByID(uid);
        if (user == null) {
            System.out.println("要求下载头像的用户（uid: " + uid + "）不存在！");
            return;
        }
        File avatarFile = new File(Constants.AVATAR_PATH + File.separator + user.getAvatar());
        responseFile(response, avatarFile);
    }

    private static JSONObject getJsonUserObject(User u) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid", u.getUid());
        jsonObject.put("name", u.getName());
        jsonObject.put("email", u.getEmail());
        // 将敏感信息注释掉
        // jsonObject.put("password", u.getPassword());
        jsonObject.put("createTime", u.getCreateTime());
        jsonObject.put("avatar", u.getAvatar());
        jsonObject.put("status", u.getStatus());
        return jsonObject;
    }

    /**
     * 发送激活邮件
     *
     * @param recipient 收件人邮箱地址
     * @param code      激活码
     */
    private static void sendMail(String recipient, String code) {
        try {
            Properties props = new Properties();
            props.put("username", "nhy17@mails.tsinghua.edu.cn");
            props.put("password", "1234567890");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "mails.tsinghua.edu.cn");
            props.put("mail.smtp.port", "25");

            Session mailSession = Session.getDefaultInstance(props);

            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress("nhy17@mails.tsinghua.edu.cn"));
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            msg.setSubject("清华帮帮忙激活邮件");
            // todo: 每次发布到服务器前都要把下面的ip改成106.54.118.148
            msg.setContent("<h1>此邮件为清华帮帮忙激活邮件，请点击下方链接完成激活操作：</h1>" +
                            "<h3><a href='http://106.54.118.148:8080/user/activate?code="
                            + code + "'>点此激活</a></h3>"
                    , "text/html;charset=UTF-8");
            msg.saveChanges();

            Transport transport = mailSession.getTransport("smtp");
            transport.connect(props.getProperty("mail.smtp.host"), props
                    .getProperty("username"), props.getProperty("password"));
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

}
