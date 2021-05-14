package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.user.config.TokenDecode;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageInfo;
import entity.BCrypt;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenDecode tokenDecode;

    /***
     * 增加用户积分
     * @param points
     * @return
     */
    @GetMapping(value = "/points/add")
    public Result addPoints(@RequestParam Integer points) {
        String username = tokenDecode.getUserInfo().get("username");
        userService.addPoints(username, points);
        return new Result(true, StatusCode.OK, "积分成功");
    }

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) User user, @PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页条件查询User
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页查询User
        PageInfo<User> pageInfo = userService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     *  根据邮箱/用户名/手机号校验登录
     * @param param1
     * @param password
     * @return
     */
    @GetMapping("/login")
    public Result registerCheck(@RequestParam(value = "param") String param1,
                                @RequestParam("password") String password) {
        User user = userService.registerCheck(param1,password);
        if (user == null) {
            return new Result(false, StatusCode.ERROR, "用户不存在");
        } else {
            return new Result(true, StatusCode.OK, "用户登录校验成功", user);
        }
    }

    /***
     * 多条件搜索用户数据
     * @param user
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<User>> findList(@RequestBody(required = false) User user) {
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody User user, @PathVariable String id) {
        //设置主键值
        user.setUsername(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增User数据
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody User user) {
        boolean isInsert = userService.add(user);
        if (isInsert) {
            return new Result(true, StatusCode.OK, "添加成功");
        } else return new Result(false, StatusCode.LOGINERROR, "添加失败，用户名重复");
    }

    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @GetMapping({"/{id}", "/load/{id}"})
    public Result<User> findById(@PathVariable String id) {
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
      /*  if(user==null){
            return new Result<>(false,StatusCode.ERROR,"查询无果");
        }*/
        return new Result<User>(true, StatusCode.OK, "查询成功", user);
    }

    /***
     * 查询 User全部数据
     * 只允许管理员 admin 角色访问
     * @return
     */
    @GetMapping
    //@PreAuthorize("hasRole('user')")
    public Result<List<User>> findAll() {
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    // 修改用户使用状态
    @GetMapping("/limit")
    public Result changeStatus(@RequestParam(value = "username")String username){
        userService.changeStatus(username);
        return new Result(true,StatusCode.OK,"修改用户状态成功");
    }

    @RequestMapping(value = "/login")
    public Result login(String username, String password, HttpServletResponse response) {
        User user = userService.findById(username);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {

            // 生成令牌
            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("role", "USER");
            tokenMap.put("success", "SUCCESS");
            tokenMap.put("username", username);
            String token = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(tokenMap), null);

            // 把令牌信息存入 Cookie
            Cookie cookie = new Cookie("Authorization", token);
            cookie.setDomain("localhost");
            cookie.setPath("/");
            response.addCookie(cookie);

            response.setHeader("Authorization", token);


            // 把令牌作为参数给用户
            return new Result(true, StatusCode.OK, "登录成功！", token);
        }
        return new Result(false, StatusCode.LOGINERROR, "账号或者密码错误！");
    }
}
