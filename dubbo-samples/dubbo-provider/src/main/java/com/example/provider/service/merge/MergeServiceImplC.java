package com.example.provider.service.merge;

import com.example.client.merge.MergeService;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.List;

@DubboService(group = "mergeC")
public class MergeServiceImplC implements MergeService {
    @Override
    public List<String> mergeResult() {
        List<String> menus = new ArrayList<>();
        menus.add("group-C.1");
        menus.add("group-C.2");
        return menus;
    }

}
