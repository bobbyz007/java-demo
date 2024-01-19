package com.example.provider.service.merge;

import com.example.client.merge.MergeService;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.List;

@DubboService(group = "mergeA")
public class MergeServiceImplA implements MergeService {
    @Override
    public List<String> mergeResult() {
        List<String> menus = new ArrayList<>();
        menus.add("group-A.1");
        menus.add("group-A.2");
        return menus;
    }
}
