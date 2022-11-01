package com.example.controller;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.example.common.Result;
import com.example.service.UserService;
import com.example.entity.Category;
import com.example.service.CategoryService;
import com.example.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserService userService;

    public User getUser() {
        String token = request.getHeader("token");
        String username = JWT.decode(token).getAudience().get(0);
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    @PostMapping
    public Result<?> save(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @GetMapping
    public Result<?> findAll() {
        List<Category> list = categoryService.list();
        return Result.success(list);
    }

    @GetMapping("/page") //
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
                                                                                                 //实体::属性 -> 直接获取对象属性 相当于this.属性
        LambdaQueryWrapper<Category> query = Wrappers.<Category>lambdaQuery().orderByDesc(Category::getId);
        /*当Str为空白或者null时，isNotBlank返回false,当Str的length>0时，isNotBlank返回true*/
        if (StrUtil.isNotBlank(name)) {
            //条件构造器like,进行模糊查询
            query.like(Category::getName, name);
        }
        //实现分页
        IPage<Category> page = categoryService.page(new Page<>(pageNum, pageSize), query);
        return Result.success(page);
    }

}
