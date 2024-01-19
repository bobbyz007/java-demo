package com.example.provider.service.merge;

import com.example.client.merge.MergeService;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.List;

@DubboService(group = "mergeB")
public class MergeServiceImplB implements MergeService {
    @Override
    public List<String> mergeResult() {
        List<String> menus = new ArrayList<>();
        menus.add("group-B.1");
        menus.add("group-B.2");
        return menus;
    }

}
