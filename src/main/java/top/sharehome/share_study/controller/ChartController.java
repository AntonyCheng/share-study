package top.sharehome.share_study.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.model.dto.chart.ChartCollegeLocationDto;
import top.sharehome.share_study.model.dto.chart.ChartCollegeScoreDto;
import top.sharehome.share_study.model.dto.chart.ChartResourceCollectDto;
import top.sharehome.share_study.model.dto.chart.ChartTeacherScoreDto;
import top.sharehome.share_study.model.entity.Resource;
import top.sharehome.share_study.model.entity.Teacher;
import top.sharehome.share_study.service.CollegeService;
import top.sharehome.share_study.service.ResourceService;
import top.sharehome.share_study.service.TeacherService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EChart可视化接口
 *
 * @author AntonyCheng
 */
@RestController
@Api(tags = "EChart可视化接口")
@RequestMapping("/chart")
@CrossOrigin
public class ChartController {
    @javax.annotation.Resource
    private CollegeService collegeService;

    @javax.annotation.Resource
    private TeacherService teacherService;

    @javax.annotation.Resource
    private ResourceService resourceService;

    /**
     * 高校坐标人数接口
     *
     * @return 高校坐标人数数据
     */
    @ApiOperation("高校坐标人数TOP5接口")
    @GetMapping("/college_location_number")
    @CrossOrigin
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public R<List<ChartCollegeLocationDto>> chartCollegeLocationAndTeacherNumber() {
        List<ChartCollegeLocationDto> results = collegeService.list().stream().map(college -> {
            ChartCollegeLocationDto chartCollegeLocationDto = new ChartCollegeLocationDto();

            LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teacherLambdaQueryWrapper.eq(Teacher::getBelong, college.getId());
            long count = teacherService.count(teacherLambdaQueryWrapper);

            chartCollegeLocationDto.setLocation(college.getLocation());
            chartCollegeLocationDto.setName(college.getName());
            chartCollegeLocationDto.setNumber(Math.toIntExact(count));

            return chartCollegeLocationDto;
        }).collect(Collectors.toList());

        List<ChartCollegeLocationDto> returnResult = results.stream()
                .sorted(Comparator.comparing(ChartCollegeLocationDto::getNumber).reversed())
                .collect(Collectors.toList());

        return R.success(returnResult);
    }

    /**
     * 资料收藏TOP5接口
     *
     * @return 资料收藏数据TOP5数据
     */
    @ApiOperation("资料收藏TOP5接口")
    @GetMapping("/resource_collect")
    @CrossOrigin
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public R<List<ChartResourceCollectDto>> chartResourceCollect() {
        LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceLambdaQueryWrapper
                .orderByDesc(Resource::getScore)
                .last("limit 5");
        List<Resource> result = resourceService.list(resourceLambdaQueryWrapper);
        List<ChartResourceCollectDto> returnResult = result.stream().map(resource -> {
            ChartResourceCollectDto chartResourceCollectDto = new ChartResourceCollectDto();
            chartResourceCollectDto.setBelongName(teacherService.getById(resource.getBelong()).getName());
            chartResourceCollectDto.setScore(resource.getScore());
            chartResourceCollectDto.setName(resource.getName());
            return chartResourceCollectDto;
        }).collect(Collectors.toList());

        return R.success(returnResult);
    }

    /**
     * 高校贡献TOP5接口
     *
     * @return 高校贡献TOP5数据
     */
    @ApiOperation("高校贡献TOP5接口")
    @GetMapping("/college_score")
    @CrossOrigin
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public R<List<ChartCollegeScoreDto>> chartCollegeScore() {

        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
        wrapper.select("teacher_belong as belong, sum(teacher_score) as score")
                .groupBy("teacher_belong");
        List<Teacher> result = teacherService.list(wrapper);

        List<ChartCollegeScoreDto> returnResult = result.stream().sorted(Comparator.comparing(Teacher::getScore).reversed()).limit(3).map(teacher -> {
            ChartCollegeScoreDto chartCollegeScoreDto = new ChartCollegeScoreDto();
            chartCollegeScoreDto.setName(collegeService.getById(teacher.getBelong()).getName());
            chartCollegeScoreDto.setScore(teacher.getScore());
            return chartCollegeScoreDto;
        }).collect(Collectors.toList());

        if (returnResult.size() > 3) {
            QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("sum(teacher_score) as score");
            Teacher one = teacherService.getOne(queryWrapper);
            Integer score = one.getScore();
            for (int i = 0; i < 3; i++) {
                score -= returnResult.get(i).getScore();
            }
            if (score != 0) {
                returnResult.add(new ChartCollegeScoreDto("other", score));
            }
        }

        return R.success(returnResult);
    }

    /**
     * 教师贡献TOP5接口
     *
     * @return 教师贡献TOP5数据
     */
    @ApiOperation("教师贡献TOP5接口")
    @GetMapping("/teacher_score")
    @CrossOrigin
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public R<List<ChartTeacherScoreDto>> chartTeacherScore() {
        LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper
                .orderByDesc(Teacher::getScore)
                .last("limit 5");
        List<Teacher> result = teacherService.list(teacherLambdaQueryWrapper);
        List<ChartTeacherScoreDto> returnResult = result.stream().map(teacher -> {
            ChartTeacherScoreDto chartTeacherScoreDto = new ChartTeacherScoreDto();
            chartTeacherScoreDto.setName(teacher.getName());
            chartTeacherScoreDto.setScore(teacher.getScore());
            return chartTeacherScoreDto;
        }).collect(Collectors.toList());
        return R.success(returnResult);
    }

    /**
     * 活跃度接口
     *
     * @return 活跃度数据
     */
    @ApiOperation("活跃度接口")
    @GetMapping("/activity")
    @CrossOrigin
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public R<Map<String, Integer>> chartActivity() {
        List<Integer> list = Arrays.asList(342, 256, 125, 643, 534, 235, 211, 321, 452, 243);
        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();
        for (int i = -9; i <= 0; i++) {
            hashMap.put(LocalDate.now().plusDays(i).toString(), list.get(i + 9));
        }
        return R.success(hashMap);
    }
}
