package com.xuecheng.manage_course.serivce;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CoursePicRepository;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CoursePicRepository coursePicRepository;

    //课程计划查询
    public TeachplanNode findTeachplanList(String courseId) {
        return teachplanMapper.selectList(courseId);
    }

    // 添加课程计划
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        // 课程计划
        String courseid = teachplan.getCourseid();
        // parentId
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            // 取出该课程的根节点
            parentid = this.getTeachplanRoot(courseid);

        }
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        Teachplan parentNode = optional.get();
        // 父节点的级别
        String grade = parentNode.getGrade();
        //新节点
        Teachplan teachplanNew = new Teachplan();
        //将页面提交teachplan信息拷贝到teachplanNew对象中
        BeanUtils.copyProperties(teachplan, teachplanNew);
        teachplanNew.setParentid(parentid);
        teachplanNew.setCourseid(courseid);
        if ("1".equals(grade)) {
            teachplanNew.setGrade("2"); //级别,根据父节点的界别来设置
            teachplan.setGrade("1");
        } else {
            teachplanNew.setGrade("3"); //级别,根据父节点的界别来设置
        }
        teachplanRepository.save(teachplanNew);
        // 处理parentId
        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 查询课程的根节点,如果查询不到要自动添加根节点
    private String getTeachplanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        // 课程信息
        CourseBase courseBase = optional.get();
        // 查询课程的根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() <= 0) {
            // 查询不到,要自动添加跟节点
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        // 返回根节点ID
        return teachplanList.get(0).getId();
    }

    //想课程管理数据库添加课程与图片的关联信息
    public ResponseResult addCoursePic(String courseId, String pic) {
        //课程图片信息
        CoursePic coursePic = null;
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()) {
            coursePic = picOptional.get();
        }
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        coursePic.setPic(pic);
        coursePic.setCourseid(courseId);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查询课程图片
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            return coursePic;
        }
        return null;
    }

    //删除课程图片
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {

        long num = coursePicRepository.deleteByCourseid(courseId);
        if (num>0){
           return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
}
