package com.example.consumer.merge;

import com.example.client.direct.DirectService;
import com.example.client.merge.MergeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 合并来自同一个接口的多个实现的结果。 dubbo内部实现各种类型的Merger，如ArrayMerger。 如有自定制需求，可扩展Merger SPI
 */
@RestController
@RequestMapping("merge")
public class MergeController {
    private static Logger logger = LoggerFactory.getLogger(MergeController.class);

    // 指定merger参数，合并group中的多个实现的调用结果
    @DubboReference(group = "*",merger = "true")
    private MergeService mergeService;

    // 指定合并来自mergeA，mergeC分组的调用结果
    @DubboReference(group = "mergeA,mergeC",merger = "true")
    private MergeService mergePartService;

    @GetMapping("/all")
    public String call() {
        return mergeService.mergeResult().toString();
    }

    @GetMapping("/part")
    public String callPart() {
        return mergePartService.mergeResult().toString();
    }
}
