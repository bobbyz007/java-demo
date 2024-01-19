/*
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
 */

package com.example.client.mock;

/**
 * 如果mock参数不是以 return 或 throw 开头， 则默认寻找服务名+Mock的类作为Mock实现
 * 参考 {@link org.apache.dubbo.rpc.support.MockInvoker MockInvoker} 实现
 */
public class DemoServiceMock implements DemoService {
    public String sayHello(String name) {
        return "mock " + name;
    }
}
