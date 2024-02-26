/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.example.client.stub;

public class StubServiceStub implements StubService {
    private final StubService demoService;

    // 构造函数传入真正的远程代理对象
    public StubServiceStub(StubService demoService) {
        this.demoService = demoService;
    }

    @Override
    public String sayHello(String name) {
        try {
            String result = demoService.sayHello(name);
            return "stub - " + result;
        } catch (Exception e) {
            return null;
        }
    }
}