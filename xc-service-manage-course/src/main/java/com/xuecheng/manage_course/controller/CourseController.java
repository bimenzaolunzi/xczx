package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.serivce.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi{
    @Autowired
    CourseService courseService;

    @GetMapping("/teachplan/list/{courseId}")
    @Override
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }

    @PostMapping("/teachplan/add")
    @Override
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }
    @PostMapping("/coursepic/add")
    @Override
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId,@RequestParam("pic")String pic) {
        return courseService.addCoursePic(courseId,pic);
    }
    @GetMapping("/coursepic/list/{courseId}")
    @Override
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePic(courseId);
    }
@DeleteMapping("/coursepic/delete")
    @Override
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }


}
