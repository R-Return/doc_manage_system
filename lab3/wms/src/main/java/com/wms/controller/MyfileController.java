package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Myfile;
import com.wms.entity.Sharefile;
import com.wms.entity.ShareFileDto;
import com.wms.entity.User;
import com.wms.service.MyfileService;
import com.wms.service.SharefileService;
import com.wms.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/comp")
public class MyfileController {
    @Autowired
    private MyfileService myfileService;

    @Autowired
    private UserService userService;

    @Autowired
    private SharefileService sharefileService;


    private String uploadPath = System.getProperty("user.dir") + "\\"; // 上传文件存储的路径
    //新增
    @PostMapping("/save")
    public Result save(@RequestBody Myfile file){
        return myfileService.save(file)?Result.suc():Result.fail();
    }

    @PostMapping("/saveShare")
    public void saveShare(@RequestBody ShareFileDto shareFileDto){
        Sharefile shareFile = new Sharefile();
        shareFileDto.setShareFrom(shareFileDto.getUserid());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(User::getName,shareFileDto.getUserName());
        int userId = userService.list(lambdaQueryWrapper).stream().map(User::getId).collect(Collectors.toList()).get(0);
        shareFileDto.setUserid(userId);
        BeanUtils.copyProperties(shareFileDto,shareFile);
        sharefileService.save(shareFile);
    }


    //上传
    @RequestMapping("/uploadResult")
    public Result upload(@RequestParam("file") MultipartFile file, @RequestParam("userId") Integer userId,@RequestParam("label") String label){
        System.out.println("上传文件");
        String fileName = file.getOriginalFilename();
        // 获取文件名
        File userDirectory = new File(uploadPath + userId);
        if(!userDirectory.exists()){
            userDirectory.mkdir();
        }
        File dest = new File(uploadPath +userId+"\\"+fileName);
        try{
            file.transferTo(dest);
            System.out.println("上传成功");
            save(new Myfile(userId,fileName, dest.getAbsolutePath(),label));
        }catch(Exception e){
            return Result.fail(); // 上传失败就返回前端上传失败的信息
        }
        return Result.suc(); // 上传成功就返回前端上传成功的信息。
    }

    //下载
    @PostMapping("getResult")
    public void getCompResult(@RequestBody Map<String, String> map, HttpServletResponse response) throws Exception {
        System.out.println("下载文件");
        String owner = map.get("userId");
        if(map.get("shareFrom")!=null && !map.get("shareFrom").equals("0")){
            owner = map.get("shareFrom");
        }
        String path = uploadPath +owner+"\\"+map.get("resultfile"); // 这里的path是文件存储的路径
        InputStream inputStream = new FileInputStream(path);
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(map.get("resultfile"), "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int len;
        while((len = inputStream.read(b)) > 0){
            outputStream.write(b, 0, len);
        }
        System.out.println("下载成功");
        inputStream.close();
    }

    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        File dest = new File(myfileService.getById(id).getFileurl());
        if(dest.exists()){
            // 上传的文件被保存了
            dest.delete();
            myfileService.removeById(id);
            return Result.suc();
        }

        return Result.fail();
    }

    //删除
    @GetMapping("/del1")
    public Result del1(@RequestParam String id){
            sharefileService.removeById(id);
            return Result.suc();
    }

    

    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String fileName = (String)param.get("fileName");
        Integer userId = (Integer)param.get("userId");
        List<String> list = (List) param.get("label");
        Page<Myfile> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Myfile> lambdaQueryWrapper = new LambdaQueryWrapper();
        for(String label:list){
            lambdaQueryWrapper.or().like(Myfile::getLabel,label);
        }
        if(StringUtils.isNotBlank(fileName) && !"null".equals(fileName)){
            lambdaQueryWrapper.like(Myfile::getFilename,fileName);
        }
        lambdaQueryWrapper.eq(Myfile::getUserid,userId);

        //IPage result = userService.pageC(page);
        IPage result = myfileService.pageCC(page,lambdaQueryWrapper);

        System.out.println("total=="+result.getTotal());

        return Result.suc(result.getRecords(),result.getTotal());
    }
    @PostMapping("/listPageC2")
    public Result listPageC2(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String fileName = (String)param.get("fileName");
        Integer userId = (Integer)param.get("userId");
        List<String> list = (List) param.get("label");
        Page<Sharefile> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Sharefile> lambdaQueryWrapper = new LambdaQueryWrapper();
        for(String label:list){
            lambdaQueryWrapper.or().like(Sharefile::getLabel,label);
        }
        if(StringUtils.isNotBlank(fileName) && !"null".equals(fileName)){
            lambdaQueryWrapper.like(Sharefile::getFilename,fileName);
        }
        lambdaQueryWrapper.eq(Sharefile::getUserid,userId);

        //IPage result = userService.pageC(page);
        IPage result = sharefileService.page(page,lambdaQueryWrapper);
        System.out.println("total=="+result.getTotal());

        return Result.suc(result.getRecords(),result.getTotal());
    }




}
