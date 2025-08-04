export default [
    {
        "id": 408,
        "componentId": "9645f8f6b9094cba8a1002585cfb922c",
        "componentName": "111",
        "componentDesc": "1",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-27 10:23:51",
        "updateTime": null,
        "updateUser": null,
        "tenantId": "",
        "nodes": [
            {
                "id": 19197,
                "nodeId": "15dfce9453e24286a77b4257011edb81",
                "nodeName": "开始",
                "componentId": "9645f8f6b9094cba8a1002585cfb922c",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 33760,
                        "paramId": "dc5c45376e304e04819e0805dd902795",
                        "label": "rawQuery",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": null,
                        "nodeId": "15dfce9453e24286a77b4257011edb81",
                        "referenceNodeId": "开始",
                        "value": null,
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 19198,
                "nodeId": "f54d1065a3884832b983089fcbc0ab69",
                "nodeName": "api",
                "componentId": "9645f8f6b9094cba8a1002585cfb922c",
                "nodeType": 2,
                "input": [
                    {
                        "id": 33761,
                        "paramId": "12a19ebc875148f0bc71ab43de7579ba",
                        "label": "msg",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": "API节点的默认入参",
                        "nodeId": "f54d1065a3884832b983089fcbc0ab69",
                        "referenceNodeId": "15dfce9453e24286a77b4257011edb81",
                        "value": "rawQuery",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 33762,
                        "paramId": "8aac6bd578ee4a2d906e006831aeb812",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "f54d1065a3884832b983089fcbc0ab69",
                        "referenceNodeId": "api",
                        "value": "data",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": ``,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": "",
                    "contentType": "application/json",
                    "requestBody": "",
                    "responseBody": "",
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 19199,
                "nodeId": "c2e5aa6afc1445299400f1267fd8c2a8",
                "nodeName": "结束",
                "componentId": "9645f8f6b9094cba8a1002585cfb922c",
                "nodeType": 10,
                "input": [
                    {
                        "id": 33763,
                        "paramId": "e3f610e1868b4a2daccaf467b87d59a7",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "结束节点的默认入参",
                        "nodeId": "c2e5aa6afc1445299400f1267fd8c2a8",
                        "referenceNodeId": "f54d1065a3884832b983089fcbc0ab69",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 14037,
                "componentId": "9645f8f6b9094cba8a1002585cfb922c",
                "sourceNodeId": "15dfce9453e24286a77b4257011edb81",
                "targetNodeId": "f54d1065a3884832b983089fcbc0ab69",
                "caseId": null
            },
            {
                "id": 14038,
                "componentId": "9645f8f6b9094cba8a1002585cfb922c",
                "sourceNodeId": "f54d1065a3884832b983089fcbc0ab69",
                "targetNodeId": "c2e5aa6afc1445299400f1267fd8c2a8",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 33760,
                "paramId": "dc5c45376e304e04819e0805dd902795",
                "label": "rawQuery",
                "variable": "rawQuery",
                "type": "string",
                "desc": null,
                "nodeId": "15dfce9453e24286a77b4257011edb81",
                "referenceNodeId": "开始",
                "value": null,
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 33761,
                "paramId": "12a19ebc875148f0bc71ab43de7579ba",
                "label": "msg",
                "variable": "rawQuery",
                "type": "string",
                "desc": "API节点的默认入参",
                "nodeId": "f54d1065a3884832b983089fcbc0ab69",
                "referenceNodeId": "15dfce9453e24286a77b4257011edb81",
                "value": "rawQuery",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 1
            },
            {
                "id": 33762,
                "paramId": "8aac6bd578ee4a2d906e006831aeb812",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "f54d1065a3884832b983089fcbc0ab69",
                "referenceNodeId": "api",
                "value": "data",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 33763,
                "paramId": "e3f610e1868b4a2daccaf467b87d59a7",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "结束节点的默认入参",
                "nodeId": "c2e5aa6afc1445299400f1267fd8c2a8",
                "referenceNodeId": "f54d1065a3884832b983089fcbc0ab69",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 400,
        "componentId": "17e420d3bb9445f392c091f9364d4192",
        "componentName": "YAYI-Visual-Prompt",
        "componentDesc": "YAYI-Visual-Prompt",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-23 17:49:06",
        "updateTime": "2024-12-23 18:25:56",
        "updateUser": "admin-demo",
        "tenantId": "",
        "nodes": [
            {
                "id": 9750,
                "nodeId": "7a603d69f4714ae6a4f2e54232880487",
                "nodeName": "开始",
                "componentId": "17e420d3bb9445f392c091f9364d4192",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 16902,
                        "paramId": "9f09a917315a482e8fe6a71a52d211e4",
                        "label": "req",
                        "variable": "req",
                        "type": "string",
                        "desc": "req",
                        "nodeId": "7a603d69f4714ae6a4f2e54232880487",
                        "referenceNodeId": "开始",
                        "value": "https://localhost/wos/data-center-a3f5de5e3/ef160abac99d3a9bd85836b83a355837.jpg",
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 9751,
                "nodeId": "47ab8e5ecd154db29f9501fa24072869",
                "nodeName": "api",
                "componentId": "17e420d3bb9445f392c091f9364d4192",
                "nodeType": 2,
                "input": [
                    {
                        "id": 16903,
                        "paramId": "9f09a917315a482e8fe6a71a52d211e4",
                        "label": "req",
                        "variable": "req",
                        "type": "string",
                        "desc": "req",
                        "nodeId": "47ab8e5ecd154db29f9501fa24072869",
                        "referenceNodeId": "7a603d69f4714ae6a4f2e54232880487",
                        "value": "req",
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "COMPLETE",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 16904,
                        "paramId": "d3c87b23345a4e888880abe18b53045b",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "47ab8e5ecd154db29f9501fa24072869",
                        "referenceNodeId": "api",
                        "value": "${data}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": `${process.env.VUE_APP_BASE_API}/smartApi/llmSmart/yayi`,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": [],
                    "contentType": "application/json",
                    "requestBody": [
                        {
                            "name": "param",
                            "type": "object",
                            "value": "",
                            "paramId": "m50uuylp-k6641ej6o",
                            "children": [
                                {
                                    "name": "id",
                                    "type": "string",
                                    "value": "9ad10cb4-daab-415d-87ba-c2fbaa2d34ed",
                                    "paramId": "m50uv8lx-h3lz4z4bq",
                                    "children": []
                                },
                                {
                                    "name": "retryInterval",
                                    "type": "number",
                                    "value": "3000",
                                    "paramId": "m50uvq0s-6okewuurr",
                                    "children": []
                                },
                                {
                                    "name": "retryTimes",
                                    "type": "number",
                                    "value": "1",
                                    "paramId": "m50uw3ct-uxd0fe29n",
                                    "children": []
                                },
                                {
                                    "name": "uri",
                                    "type": "string",
                                    "value": "https://localhost/6fa4a9a2711f94f7a2f4f26f0876dad6/analysis",
                                    "paramId": "m50uwufh-2x79cnx17",
                                    "children": []
                                },
                                {
                                    "name": "img_url",
                                    "type": "string",
                                    "value": "${req}",
                                    "paramId": "m50uxg4d-wgxywsab1",
                                    "children": []
                                },
                                {
                                    "name": "appKey",
                                    "type": "string",
                                    "value": "fe29e0c5f3e24ead8df7bc7961ca4b20",
                                    "paramId": "m50w1fvp-1cukomems",
                                    "children": []
                                },
                                {
                                    "name": "appSecret",
                                    "type": "string",
                                    "value": "931ef08c41874db2a5c798e582fc0642",
                                    "paramId": "m50w1g71-m70ipfyf3",
                                    "children": []
                                }
                            ]
                        },
                        {
                            "name": "header",
                            "type": "object",
                            "value": "",
                            "paramId": "m50uuzpp-m1lzyz8cr",
                            "children": [
                                {
                                    "name": "Content-Type",
                                    "type": "string",
                                    "value": "application/json",
                                    "paramId": "m50uvaqt-20gghv8rk",
                                    "children": []
                                },
                                {
                                    "name": "Accept",
                                    "type": "string",
                                    "value": "*/*",
                                    "paramId": "m50uyfa4-xm02cwyo0",
                                    "children": []
                                }
                            ]
                        }
                    ],
                    "responseBody": [
                        {
                            "name": "id",
                            "type": "string",
                            "value": "",
                            "paramId": "m50uybkd-55rvintcu",
                            "children": []
                        },
                        {
                            "name": "code",
                            "type": "number",
                            "value": "",
                            "paramId": "m50uycst-lk5b1t06w",
                            "children": []
                        },
                        {
                            "name": "msg",
                            "type": "string",
                            "value": "",
                            "paramId": "m50v0wgd-nzcimwpmn",
                            "children": []
                        },
                        {
                            "name": "data",
                            "type": "string",
                            "value": "",
                            "paramId": "m50v0ww6-cw2g7p0re",
                            "children": []
                        },
                        {
                            "name": "time",
                            "type": "number",
                            "value": "",
                            "paramId": "m50v0xcl-18uijy7p4",
                            "children": []
                        }
                    ],
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 9752,
                "nodeId": "d60735f6d7b94b00b43dbbf2bb523ebd",
                "nodeName": "结束",
                "componentId": "17e420d3bb9445f392c091f9364d4192",
                "nodeType": 10,
                "input": [
                    {
                        "id": 16905,
                        "paramId": "d3c87b23345a4e888880abe18b53045b",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "d60735f6d7b94b00b43dbbf2bb523ebd",
                        "referenceNodeId": "47ab8e5ecd154db29f9501fa24072869",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 6606,
                "componentId": "17e420d3bb9445f392c091f9364d4192",
                "sourceNodeId": "7a603d69f4714ae6a4f2e54232880487",
                "targetNodeId": "47ab8e5ecd154db29f9501fa24072869",
                "caseId": null
            },
            {
                "id": 6607,
                "componentId": "17e420d3bb9445f392c091f9364d4192",
                "sourceNodeId": "47ab8e5ecd154db29f9501fa24072869",
                "targetNodeId": "d60735f6d7b94b00b43dbbf2bb523ebd",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 16902,
                "paramId": "9f09a917315a482e8fe6a71a52d211e4",
                "label": "req",
                "variable": "req",
                "type": "string",
                "desc": "req",
                "nodeId": "7a603d69f4714ae6a4f2e54232880487",
                "referenceNodeId": "开始",
                "value": "https://localhost/wos/data-center-a3f5de5e3/ef160abac99d3a9bd85836b83a355837.jpg",
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 16903,
                "paramId": "9f09a917315a482e8fe6a71a52d211e4",
                "label": "req",
                "variable": "req",
                "type": "string",
                "desc": "req",
                "nodeId": "47ab8e5ecd154db29f9501fa24072869",
                "referenceNodeId": "7a603d69f4714ae6a4f2e54232880487",
                "value": "req",
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "COMPLETE",
                "nodeType": 1
            },
            {
                "id": 16904,
                "paramId": "d3c87b23345a4e888880abe18b53045b",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "47ab8e5ecd154db29f9501fa24072869",
                "referenceNodeId": "api",
                "value": "${data}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 16905,
                "paramId": "d3c87b23345a4e888880abe18b53045b",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "d60735f6d7b94b00b43dbbf2bb523ebd",
                "referenceNodeId": "47ab8e5ecd154db29f9501fa24072869",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 399,
        "componentId": "1f41415b8bb6487ab7076ddf2215a98f",
        "componentName": "AI作画_关键词生成",
        "componentDesc": "AI作画_关键词生成",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-23 17:07:06",
        "updateTime": "2024-12-23 17:38:31",
        "updateUser": "admin-demo",
        "tenantId": "",
        "nodes": [
            {
                "id": 9747,
                "nodeId": "11e5accfb38a47c3a5184b04006e3c23",
                "nodeName": "开始",
                "componentId": "1f41415b8bb6487ab7076ddf2215a98f",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 16844,
                        "paramId": "6a2b5ff3e9a24e8c9b54274bc83f6db1",
                        "label": "content",
                        "variable": "content",
                        "type": "string",
                        "desc": "content",
                        "nodeId": "11e5accfb38a47c3a5184b04006e3c23",
                        "referenceNodeId": null,
                        "value": "zh,5",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 9748,
                "nodeId": "dfd0f6f538b04c06afdffdb136638a08",
                "nodeName": "api",
                "componentId": "1f41415b8bb6487ab7076ddf2215a98f",
                "nodeType": 2,
                "input": [
                    {
                        "id": 16845,
                        "paramId": "8586ae56e3ca4e918971141977669fd6",
                        "label": "content",
                        "variable": "content",
                        "type": "string",
                        "desc": "content",
                        "nodeId": "dfd0f6f538b04c06afdffdb136638a08",
                        "referenceNodeId": "11e5accfb38a47c3a5184b04006e3c23",
                        "value": "content",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 16846,
                        "paramId": "4087ea1af2ea4692b29ef592896ed399",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "dfd0f6f538b04c06afdffdb136638a08",
                        "referenceNodeId": "api",
                        "value": "${data}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": `${process.env.VUE_APP_BASE_API}/smartApi/llmSmart/yayi`,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": [],
                    "contentType": "application/json",
                    "requestBody": [
                        {
                            "name": "param",
                            "type": "object",
                            "value": "",
                            "paramId": "m50ti7xx-xdowxxu0e",
                            "children": [
                                {
                                    "name": "appKey",
                                    "type": "string",
                                    "value": "1871120255427780609",
                                    "paramId": "m50tiz5h-judjxit1i",
                                    "children": []
                                },
                                {
                                    "name": "appSecret",
                                    "type": "string",
                                    "value": "57774e5083784316b76cc9c1e7794ce5",
                                    "paramId": "m50tkout-1an717ni9",
                                    "children": []
                                },
                                {
                                    "name": "id",
                                    "type": "string",
                                    "value": "b0275111-7ab7-4b60-97e5-ba8f85376188",
                                    "paramId": "m50tl9bw-vldz4ase4",
                                    "children": []
                                },
                                {
                                    "name": "retryInterval",
                                    "type": "number",
                                    "value": "3000",
                                    "paramId": "m50tl9lg-i04jgt2n9",
                                    "children": []
                                },
                                {
                                    "name": "retryTimes",
                                    "type": "number",
                                    "value": "1",
                                    "paramId": "m50tl9ql-okstwe4ll",
                                    "children": []
                                },
                                {
                                    "name": "content",
                                    "type": "object",
                                    "value": "",
                                    "paramId": "m50tl9us-8mdqb3y7w",
                                    "children": [
                                        {
                                            "name": "lang",
                                            "type": "string",
                                            "value": "zh",
                                            "paramId": "m50tynjg-auk8gw3d6",
                                            "children": []
                                        },
                                        {
                                            "name": "num",
                                            "type": "number",
                                            "value": "1",
                                            "paramId": "m50tz7x9-yaxobyc29",
                                            "children": []
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "name": "header",
                            "type": "object",
                            "value": "",
                            "paramId": "m50tistw-wr8fe4el2",
                            "children": [
                                {
                                    "name": "Content-Type",
                                    "type": "string",
                                    "value": "application/json",
                                    "paramId": "m50u3mth-fjtvsgqf9",
                                    "children": []
                                },
                                {
                                    "name": "Accept",
                                    "type": "string",
                                    "value": "*/*",
                                    "paramId": "m50u3nu5-zf0k8crsy",
                                    "children": []
                                }
                            ]
                        }
                    ],
                    "responseBody": [
                        {
                            "name": "id",
                            "type": "string",
                            "value": "",
                            "paramId": "m50u5pfo-x3lbx6c02",
                            "children": []
                        },
                        {
                            "name": "code",
                            "type": "number",
                            "value": "",
                            "paramId": "m50u5pws-9f1pm2d1m",
                            "children": []
                        },
                        {
                            "name": "data",
                            "type": "object",
                            "value": "",
                            "paramId": "m50u5qb1-6xl0qrahz",
                            "children": []
                        },
                        {
                            "name": "msg",
                            "type": "string",
                            "value": "",
                            "paramId": "m50u5qud-yamo6yzki",
                            "children": []
                        },
                        {
                            "name": "time",
                            "type": "string",
                            "value": "",
                            "paramId": "m50u60b1-j99jf8hev",
                            "children": []
                        }
                    ],
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 9749,
                "nodeId": "d9fae3112f2c45c7bdb3533722d0998c",
                "nodeName": "结束",
                "componentId": "1f41415b8bb6487ab7076ddf2215a98f",
                "nodeType": 10,
                "input": [
                    {
                        "id": 16847,
                        "paramId": "4087ea1af2ea4692b29ef592896ed399",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "d9fae3112f2c45c7bdb3533722d0998c",
                        "referenceNodeId": "dfd0f6f538b04c06afdffdb136638a08",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 6604,
                "componentId": "1f41415b8bb6487ab7076ddf2215a98f",
                "sourceNodeId": "11e5accfb38a47c3a5184b04006e3c23",
                "targetNodeId": "dfd0f6f538b04c06afdffdb136638a08",
                "caseId": null
            },
            {
                "id": 6605,
                "componentId": "1f41415b8bb6487ab7076ddf2215a98f",
                "sourceNodeId": "dfd0f6f538b04c06afdffdb136638a08",
                "targetNodeId": "d9fae3112f2c45c7bdb3533722d0998c",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 16844,
                "paramId": "6a2b5ff3e9a24e8c9b54274bc83f6db1",
                "label": "content",
                "variable": "content",
                "type": "string",
                "desc": "content",
                "nodeId": "11e5accfb38a47c3a5184b04006e3c23",
                "referenceNodeId": null,
                "value": "zh,5",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 16845,
                "paramId": "8586ae56e3ca4e918971141977669fd6",
                "label": "content",
                "variable": "content",
                "type": "string",
                "desc": "content",
                "nodeId": "dfd0f6f538b04c06afdffdb136638a08",
                "referenceNodeId": "11e5accfb38a47c3a5184b04006e3c23",
                "value": "content",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 1
            },
            {
                "id": 16846,
                "paramId": "4087ea1af2ea4692b29ef592896ed399",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "dfd0f6f538b04c06afdffdb136638a08",
                "referenceNodeId": "api",
                "value": "${data}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 16847,
                "paramId": "4087ea1af2ea4692b29ef592896ed399",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "d9fae3112f2c45c7bdb3533722d0998c",
                "referenceNodeId": "dfd0f6f538b04c06afdffdb136638a08",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 385,
        "componentId": "42a3b50209c9497bb16cfeaa6359bf4e",
        "componentName": "夸克检索引擎",
        "componentDesc": "夸克检索引擎",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-19 19:57:08",
        "updateTime": "2024-12-19 20:05:18",
        "updateUser": "admin-demo",
        "tenantId": "",
        "nodes": [
            {
                "id": 7012,
                "nodeId": "586d527e90b04ce28f9053531cee72f0",
                "nodeName": "开始",
                "componentId": "42a3b50209c9497bb16cfeaa6359bf4e",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 12786,
                        "paramId": "10a35b87b552462bb232c14c6f896373",
                        "label": "req",
                        "variable": "req",
                        "type": "string",
                        "desc": "req",
                        "nodeId": "586d527e90b04ce28f9053531cee72f0",
                        "referenceNodeId": null,
                        "value": "北京",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 7013,
                "nodeId": "55eb2dd3b40b48d59eede027d2151952",
                "nodeName": "api",
                "componentId": "42a3b50209c9497bb16cfeaa6359bf4e",
                "nodeType": 2,
                "input": [
                    {
                        "id": 12787,
                        "paramId": "10a35b87b552462bb232c14c6f896373",
                        "label": "req",
                        "variable": "req",
                        "type": "string",
                        "desc": "req",
                        "nodeId": "55eb2dd3b40b48d59eede027d2151952",
                        "referenceNodeId": "586d527e90b04ce28f9053531cee72f0",
                        "value": "req",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 12788,
                        "paramId": "0457a6b8332940f0a345094b53b2f0bb",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "55eb2dd3b40b48d59eede027d2151952",
                        "referenceNodeId": "api",
                        "value": "${data.res_info}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": `${process.env.VUE_APP_BASE_API}/smartApi/llmSmart/yayi`,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": [],
                    "contentType": "application/json",
                    "requestBody": [
                        {
                            "name": "param",
                            "type": "object",
                            "value": "",
                            "paramId": "m4v9p5sc-cd9ypds7p",
                            "children": [
                                {
                                    "name": "appKey",
                                    "type": "string",
                                    "value": "091e10fb8d244523912b3463da05652a",
                                    "paramId": "m4v9qva4-0zdvs401z",
                                    "children": []
                                },
                                {
                                    "name": "appSecret",
                                    "type": "string",
                                    "value": "af982cc0fbbd41b0bb2009ddadbb992b",
                                    "paramId": "m4v9qvht-xn24tscv2",
                                    "children": []
                                },
                                {
                                    "name": "id",
                                    "type": "string",
                                    "value": "a2a7ac56a70cd9f59c7caeff1957b270",
                                    "paramId": "m4v9qvob-5yrjhbdty",
                                    "children": []
                                },
                                {
                                    "name": "retryInterval",
                                    "type": "number",
                                    "value": "3000",
                                    "paramId": "m4v9r6s6-5m8a6kjnv",
                                    "children": []
                                },
                                {
                                    "name": "retryTimes",
                                    "type": "number",
                                    "value": "1",
                                    "paramId": "m4v9r6zk-vg3s6zy7v",
                                    "children": []
                                },
                                {
                                    "name": "uri",
                                    "type": "string",
                                    "value": "https://localhost/2deaeebb6f90aadd97ba018ce37465c4/analysis",
                                    "paramId": "m4v9r76f-yrcexs720",
                                    "children": []
                                },
                                {
                                    "name": "content",
                                    "type": "object",
                                    "value": "",
                                    "paramId": "m4v9r7bb-houmywyr9",
                                    "children": [
                                        {
                                            "name": "function",
                                            "type": "string",
                                            "value": "web_QA",
                                            "paramId": "m4v9rm9j-2clhz8elu",
                                            "children": []
                                        },
                                        {
                                            "name": "gen_max_tokens",
                                            "type": "number",
                                            "value": "4000",
                                            "paramId": "m4v9rmos-kf52kn4xf",
                                            "children": []
                                        },
                                        {
                                            "name": "gen_mind_map",
                                            "type": "boolean",
                                            "value": "false",
                                            "paramId": "m4v9rn34-t47u9rc45",
                                            "children": []
                                        },
                                        {
                                            "name": "get_news_num",
                                            "type": "number",
                                            "value": "3",
                                            "paramId": "m4v9rnid-rzlkyflrf",
                                            "children": []
                                        },
                                        {
                                            "name": "prompt_max_tokens",
                                            "type": "number",
                                            "value": "4000",
                                            "paramId": "m4v9s3h9-ipfivtarl",
                                            "children": []
                                        },
                                        {
                                            "name": "time_limit",
                                            "type": "number",
                                            "value": "8",
                                            "paramId": "m4v9s3wc-xin7pzakw",
                                            "children": []
                                        },
                                        {
                                            "name": "top_k",
                                            "type": "number",
                                            "value": "3",
                                            "paramId": "m4v9s4t2-t28a17olq",
                                            "children": []
                                        },
                                        {
                                            "name": "user_message",
                                            "type": "string",
                                            "value": "${req}",
                                            "paramId": "m4v9sgdw-1qk931cn2",
                                            "children": []
                                        },
                                        {
                                            "name": "web_source_list",
                                            "type": "array",
                                            "value": "[                 \"quark\"             ]",
                                            "paramId": "m4v9sguw-6hnawwghx",
                                            "children": []
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "name": "header",
                            "type": "object",
                            "value": "",
                            "paramId": "m4v9xsb5-iytnmtmxb",
                            "children": [
                                {
                                    "name": "Content-Type",
                                    "type": "string",
                                    "value": "application/json",
                                    "paramId": "m4v9y63z-r6532udil",
                                    "children": []
                                },
                                {
                                    "name": "Accept",
                                    "type": "string",
                                    "value": "*/*",
                                    "paramId": "m4v9y6jp-giqe6elw4",
                                    "children": []
                                }
                            ]
                        }
                    ],
                    "responseBody": [
                        {
                            "name": "code",
                            "type": "number",
                            "value": "",
                            "paramId": "m4v9u0y4-qofoqnksl",
                            "children": []
                        },
                        {
                            "name": "id",
                            "type": "string",
                            "value": "",
                            "paramId": "m4v9u1bu-d3jilw34h",
                            "children": []
                        },
                        {
                            "name": "msg",
                            "type": "string",
                            "value": "",
                            "paramId": "m4v9u1n4-ffivc34im",
                            "children": []
                        },
                        {
                            "name": "time",
                            "type": "number",
                            "value": "",
                            "paramId": "m4v9u1yk-9749hsk9v",
                            "children": []
                        },
                        {
                            "name": "data",
                            "type": "object",
                            "value": "",
                            "paramId": "m4v9ue53-7rst0hdg1",
                            "children": [
                                {
                                    "name": "res_info",
                                    "type": "object",
                                    "value": "",
                                    "paramId": "m4v9ui51-q8qnihup1",
                                    "children": []
                                }
                            ]
                        }
                    ],
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 7014,
                "nodeId": "38cd882d85564a4282673b7e86eadca6",
                "nodeName": "结束",
                "componentId": "42a3b50209c9497bb16cfeaa6359bf4e",
                "nodeType": 10,
                "input": [
                    {
                        "id": 12789,
                        "paramId": "0457a6b8332940f0a345094b53b2f0bb",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "38cd882d85564a4282673b7e86eadca6",
                        "referenceNodeId": "55eb2dd3b40b48d59eede027d2151952",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 5105,
                "componentId": "42a3b50209c9497bb16cfeaa6359bf4e",
                "sourceNodeId": "586d527e90b04ce28f9053531cee72f0",
                "targetNodeId": "55eb2dd3b40b48d59eede027d2151952",
                "caseId": null
            },
            {
                "id": 5106,
                "componentId": "42a3b50209c9497bb16cfeaa6359bf4e",
                "sourceNodeId": "55eb2dd3b40b48d59eede027d2151952",
                "targetNodeId": "38cd882d85564a4282673b7e86eadca6",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 12786,
                "paramId": "10a35b87b552462bb232c14c6f896373",
                "label": "req",
                "variable": "req",
                "type": "string",
                "desc": "req",
                "nodeId": "586d527e90b04ce28f9053531cee72f0",
                "referenceNodeId": null,
                "value": "北京",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 12787,
                "paramId": "10a35b87b552462bb232c14c6f896373",
                "label": "req",
                "variable": "req",
                "type": "string",
                "desc": "req",
                "nodeId": "55eb2dd3b40b48d59eede027d2151952",
                "referenceNodeId": "586d527e90b04ce28f9053531cee72f0",
                "value": "req",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 1
            },
            {
                "id": 12788,
                "paramId": "0457a6b8332940f0a345094b53b2f0bb",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "55eb2dd3b40b48d59eede027d2151952",
                "referenceNodeId": "api",
                "value": "${data.res_info}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 12789,
                "paramId": "0457a6b8332940f0a345094b53b2f0bb",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "38cd882d85564a4282673b7e86eadca6",
                "referenceNodeId": "55eb2dd3b40b48d59eede027d2151952",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 379,
        "componentId": "3ce8a837583540a49b31be799c7319d0",
        "componentName": "YAYI-30B-8K-COMPLETIONS",
        "componentDesc": "雅意30B服务",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-19 14:17:19",
        "updateTime": null,
        "updateUser": null,
        "tenantId": "",
        "nodes": [
            {
                "id": 5770,
                "nodeId": "8ba585b025ab4cc1906cd873d7dfe716",
                "nodeName": "开始",
                "componentId": "3ce8a837583540a49b31be799c7319d0",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 11023,
                        "paramId": "793a26f641da4b7db6f6b545c4fef964",
                        "label": "rawQuery",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": null,
                        "nodeId": "8ba585b025ab4cc1906cd873d7dfe716",
                        "referenceNodeId": "开始",
                        "value": null,
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5771,
                "nodeId": "8d26283d7fe44340b6ec187acc12c687",
                "nodeName": "api",
                "componentId": "3ce8a837583540a49b31be799c7319d0",
                "nodeType": 2,
                "input": [
                    {
                        "id": 11024,
                        "paramId": "793a26f641da4b7db6f6b545c4fef964",
                        "label": "rawQuery",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": null,
                        "nodeId": "8d26283d7fe44340b6ec187acc12c687",
                        "referenceNodeId": "8ba585b025ab4cc1906cd873d7dfe716",
                        "value": "rawQuery",
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "COMPLETE",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 11025,
                        "paramId": "6cb6baf7c38b4f88bebdbf370e32d87a",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "8d26283d7fe44340b6ec187acc12c687",
                        "referenceNodeId": "api",
                        "value": "${data}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": `${process.env.VUE_APP_BASE_API}/smartApi/llmSmart/yayi`,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": [],
                    "contentType": "application/json",
                    "requestBody": [
                        {
                            "name": "param",
                            "type": "object",
                            "value": "",
                            "paramId": "m4v0fs3x-uxdg3m1rv",
                            "children": [
                                {
                                    "name": "appKey",
                                    "type": "",
                                    "value": "",
                                    "paramId": "m4v0fz03-wx57bdjvu",
                                    "children": []
                                },
                                {
                                    "name": "appSecret",
                                    "type": "",
                                    "value": "",
                                    "paramId": "m4v0fzr9-9pcqr2l6v",
                                    "children": []
                                },
                                {
                                    "name": "id",
                                    "type": "",
                                    "value": "a2a7ac56a70cd9f59c7caeff1957b270",
                                    "paramId": "m4v0g00c-jyjmcf35s",
                                    "children": []
                                },
                                {
                                    "name": "retryInterval",
                                    "type": "",
                                    "value": "3000",
                                    "paramId": "m4v0g06r-972dmwfg4",
                                    "children": []
                                },
                                {
                                    "name": "retryTimes",
                                    "type": "",
                                    "value": "1",
                                    "paramId": "m4v0g0dv-jl93y017j",
                                    "children": []
                                },
                                {
                                    "name": "uri",
                                    "type": "",
                                    "value": "https://localhost/2deaeebb6f90aadd97ba018ce37465c4/analysis",
                                    "paramId": "m4v0gmbv-fb3w3mn0g",
                                    "children": []
                                }
                            ]
                        },
                        {
                            "name": "function",
                            "type": "string",
                            "value": "web_QA",
                            "paramId": "m4v0h1p0-7iscbu4bw",
                            "children": []
                        },
                        {
                            "name": "gen_max_tokens",
                            "type": "string",
                            "value": "4000",
                            "paramId": "m4v0hajg-cc5udwifa",
                            "children": []
                        },
                        {
                            "name": "gen_mind_map",
                            "type": "string",
                            "value": "false",
                            "paramId": "m4v0hjt0-9gwwy7ojm",
                            "children": []
                        },
                        {
                            "name": "get_news_num",
                            "type": "string",
                            "value": "3",
                            "paramId": "m4v0hrv0-3tr4rr1ji",
                            "children": []
                        },
                        {
                            "name": "prompt_max_tokens",
                            "type": "string",
                            "value": "4000",
                            "paramId": "m4v0hzf8-nji60r10k",
                            "children": []
                        },
                        {
                            "name": "time_limit",
                            "type": "string",
                            "value": "8",
                            "paramId": "m4v0i5k3-k3mzehjsy",
                            "children": []
                        },
                        {
                            "name": "top_k",
                            "type": "string",
                            "value": "3",
                            "paramId": "m4v0i9kz-12xwcw64r",
                            "children": []
                        },
                        {
                            "name": "user_message",
                            "type": "string",
                            "value": "${rawQuery}",
                            "paramId": "m4v0ig8b-ouv6w9zvu",
                            "children": []
                        },
                        {
                            "name": "header",
                            "type": "object",
                            "value": "",
                            "paramId": "m4v0iw0k-k1f8yxwyu",
                            "children": [
                                {
                                    "name": "Content-Type",
                                    "type": "string",
                                    "value": "application/json",
                                    "paramId": "m4v0j331-gpqgfgx7j",
                                    "children": []
                                },
                                {
                                    "name": "Accept",
                                    "type": "string",
                                    "value": "*/*",
                                    "paramId": "m4v0j9jy-39if5nrjt",
                                    "children": []
                                }
                            ]
                        }
                    ],
                    "responseBody": [
                        {
                            "name": "code",
                            "type": "number",
                            "value": "",
                            "paramId": "m4v0jykc-h14a1cz85",
                            "children": []
                        },
                        {
                            "name": "id",
                            "type": "string",
                            "value": "",
                            "paramId": "m4v0k1jy-k743rflvp",
                            "children": []
                        },
                        {
                            "name": "msg",
                            "type": "string",
                            "value": "",
                            "paramId": "m4v0k5yk-73fpqddzv",
                            "children": []
                        }
                    ],
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5772,
                "nodeId": "9521bf414fa445169c1f90e2aa06918e",
                "nodeName": "结束",
                "componentId": "3ce8a837583540a49b31be799c7319d0",
                "nodeType": 10,
                "input": [
                    {
                        "id": 11026,
                        "paramId": "6cb6baf7c38b4f88bebdbf370e32d87a",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "9521bf414fa445169c1f90e2aa06918e",
                        "referenceNodeId": "8d26283d7fe44340b6ec187acc12c687",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 4372,
                "componentId": "3ce8a837583540a49b31be799c7319d0",
                "sourceNodeId": "8ba585b025ab4cc1906cd873d7dfe716",
                "targetNodeId": "8d26283d7fe44340b6ec187acc12c687",
                "caseId": null
            },
            {
                "id": 4373,
                "componentId": "3ce8a837583540a49b31be799c7319d0",
                "sourceNodeId": "8d26283d7fe44340b6ec187acc12c687",
                "targetNodeId": "9521bf414fa445169c1f90e2aa06918e",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 11023,
                "paramId": "793a26f641da4b7db6f6b545c4fef964",
                "label": "rawQuery",
                "variable": "rawQuery",
                "type": "string",
                "desc": null,
                "nodeId": "8ba585b025ab4cc1906cd873d7dfe716",
                "referenceNodeId": "开始",
                "value": null,
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 11024,
                "paramId": "793a26f641da4b7db6f6b545c4fef964",
                "label": "rawQuery",
                "variable": "rawQuery",
                "type": "string",
                "desc": null,
                "nodeId": "8d26283d7fe44340b6ec187acc12c687",
                "referenceNodeId": "8ba585b025ab4cc1906cd873d7dfe716",
                "value": "rawQuery",
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "COMPLETE",
                "nodeType": 1
            },
            {
                "id": 11025,
                "paramId": "6cb6baf7c38b4f88bebdbf370e32d87a",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "8d26283d7fe44340b6ec187acc12c687",
                "referenceNodeId": "api",
                "value": "${data}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 11026,
                "paramId": "6cb6baf7c38b4f88bebdbf370e32d87a",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "9521bf414fa445169c1f90e2aa06918e",
                "referenceNodeId": "8d26283d7fe44340b6ec187acc12c687",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 371,
        "componentId": "7567de5f1d8d49a48964ec934826b701",
        "componentName": "车辆限行限号",
        "componentDesc": "车辆限行限号",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-18 16:07:10",
        "updateTime": null,
        "updateUser": null,
        "tenantId": "",
        "nodes": [
            {
                "id": 5237,
                "nodeId": "041a126caa30428da8e0498687c3eb0a",
                "nodeName": "开始",
                "componentId": "7567de5f1d8d49a48964ec934826b701",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 9772,
                        "paramId": "3e2c8286766443eea4151c7d2f6136c7",
                        "label": "areacode",
                        "variable": "areacode",
                        "type": "string",
                        "desc": "参数描述",
                        "nodeId": "041a126caa30428da8e0498687c3eb0a",
                        "referenceNodeId": null,
                        "value": "101010100",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5238,
                "nodeId": "feb275333a1c4d17865d7467a9b9e75a",
                "nodeName": "api",
                "componentId": "7567de5f1d8d49a48964ec934826b701",
                "nodeType": 2,
                "input": [
                    {
                        "id": 9773,
                        "paramId": "3e2c8286766443eea4151c7d2f6136c7",
                        "label": "areacode",
                        "variable": "areacode",
                        "type": "string",
                        "desc": "参数描述",
                        "nodeId": "feb275333a1c4d17865d7467a9b9e75a",
                        "referenceNodeId": "041a126caa30428da8e0498687c3eb0a",
                        "value": "areacode",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 9774,
                        "paramId": "cdfdaf691f8d4adaa515b023d8a8b535",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "feb275333a1c4d17865d7467a9b9e75a",
                        "referenceNodeId": "api",
                        "value": "${result.traffic}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": "https://eolink.o.apispace.com/5345645/lives_geo/v001/xianxing",
                    "Accept": "*/*",
                    "method": "GET",
                    "headers": [
                        {
                            "name": "X-APISpace-Token",
                            "type": "string",
                            "value": "16yqd1oguqo12ih4thhl4l8lc7kb3fn6",
                            "paramId": "m4tmd2ha-pcaktiajf",
                            "children": []
                        }
                    ],
                    "contentType": "application/json",
                    "requestBody": [
                        {
                            "name": "days",
                            "type": "number",
                            "value": "15",
                            "paramId": "m4tm8ksf-zwln5nn32",
                            "children": []
                        },
                        {
                            "name": "areacode",
                            "type": "string",
                            "value": "101010100",
                            "paramId": "m4tm8l4w-pxkfbvlc8",
                            "children": []
                        }
                    ],
                    "responseBody": [
                        {
                            "name": "status",
                            "type": "number",
                            "value": "",
                            "paramId": "m4tm99gt-rx9xqpf87",
                            "children": []
                        },
                        {
                            "name": "result",
                            "type": "object",
                            "value": "",
                            "paramId": "m4tm99x0-k386y735b",
                            "children": [
                                {
                                    "name": "traffic",
                                    "type": "object",
                                    "value": "",
                                    "paramId": "m4tm9jhl-gu0nhf3y7",
                                    "children": [
                                        {
                                            "name": "limitArea",
                                            "type": "string",
                                            "value": "",
                                            "paramId": "m4tm9se6-0ylwyhqzt",
                                            "children": []
                                        },
                                        {
                                            "name": "limitRule",
                                            "type": "string",
                                            "value": "",
                                            "paramId": "m4tm9w8f-vkzm158qi",
                                            "children": []
                                        },
                                        {
                                            "name": "limits",
                                            "type": "array",
                                            "value": "",
                                            "paramId": "m4tm9wip-keqww7jkg",
                                            "children": []
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5239,
                "nodeId": "3471b6d7f1304ce19a926ba608a489b9",
                "nodeName": "结束",
                "componentId": "7567de5f1d8d49a48964ec934826b701",
                "nodeType": 10,
                "input": [
                    {
                        "id": 9775,
                        "paramId": "cdfdaf691f8d4adaa515b023d8a8b535",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "3471b6d7f1304ce19a926ba608a489b9",
                        "referenceNodeId": "feb275333a1c4d17865d7467a9b9e75a",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 3933,
                "componentId": "7567de5f1d8d49a48964ec934826b701",
                "sourceNodeId": "041a126caa30428da8e0498687c3eb0a",
                "targetNodeId": "feb275333a1c4d17865d7467a9b9e75a",
                "caseId": null
            },
            {
                "id": 3934,
                "componentId": "7567de5f1d8d49a48964ec934826b701",
                "sourceNodeId": "feb275333a1c4d17865d7467a9b9e75a",
                "targetNodeId": "3471b6d7f1304ce19a926ba608a489b9",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 9772,
                "paramId": "3e2c8286766443eea4151c7d2f6136c7",
                "label": "areacode",
                "variable": "areacode",
                "type": "string",
                "desc": "参数描述",
                "nodeId": "041a126caa30428da8e0498687c3eb0a",
                "referenceNodeId": null,
                "value": "101010100",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9773,
                "paramId": "3e2c8286766443eea4151c7d2f6136c7",
                "label": "areacode",
                "variable": "areacode",
                "type": "string",
                "desc": "参数描述",
                "nodeId": "feb275333a1c4d17865d7467a9b9e75a",
                "referenceNodeId": "041a126caa30428da8e0498687c3eb0a",
                "value": "areacode",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 1
            },
            {
                "id": 9774,
                "paramId": "cdfdaf691f8d4adaa515b023d8a8b535",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "feb275333a1c4d17865d7467a9b9e75a",
                "referenceNodeId": "api",
                "value": "${result.traffic}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9775,
                "paramId": "cdfdaf691f8d4adaa515b023d8a8b535",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "3471b6d7f1304ce19a926ba608a489b9",
                "referenceNodeId": "feb275333a1c4d17865d7467a9b9e75a",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 370,
        "componentId": "a8fab85235db4821b49ff0c913fa6d60",
        "componentName": "天气查询",
        "componentDesc": "天气查询",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-18 15:46:39",
        "updateTime": "2024-12-20 16:17:14",
        "updateUser": "admin-demo",
        "tenantId": "",
        "nodes": [
            {
                "id": 5216,
                "nodeId": "9bd570265f23478195a3a2d45bfa24df",
                "nodeName": "开始",
                "componentId": "a8fab85235db4821b49ff0c913fa6d60",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 14699,
                        "paramId": "08bca731fb184bef8c7ee83e9ca271bc",
                        "label": "address",
                        "variable": "address",
                        "type": "string",
                        "desc": "参数描述",
                        "nodeId": "9bd570265f23478195a3a2d45bfa24df",
                        "referenceNodeId": null,
                        "value": null,
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5217,
                "nodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                "nodeName": "api",
                "componentId": "a8fab85235db4821b49ff0c913fa6d60",
                "nodeType": 2,
                "input": [
                    {
                        "id": 14700,
                        "paramId": "08bca731fb184bef8c7ee83e9ca271bc",
                        "label": "address",
                        "variable": "address",
                        "type": "string",
                        "desc": "参数描述",
                        "nodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                        "referenceNodeId": "9bd570265f23478195a3a2d45bfa24df",
                        "value": "address",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 14701,
                        "paramId": "da0e44f721f8439189bae0a295323c4c",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                        "referenceNodeId": "api",
                        "value": "${results}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    },
                    {
                        "id": 14702,
                        "paramId": "94115d103f05401584e17ddcba34768f",
                        "label": "parameter33",
                        "variable": "parameter",
                        "type": "string",
                        "desc": "参数描述",
                        "nodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                        "referenceNodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                        "value": "sfsafa",
                        "valueType": "string",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "settings": {
                    "url": "https://api.seniverse.com/v3/weather/daily.json",
                    "Accept": "*/*",
                    "method": "GET",
                    "headers": [],
                    "contentType": "application/json",
                    "requestBody": [
                        {
                            "name": "key",
                            "type": "string",
                            "value": "S78k_qg8fl3Cyw12P",
                            "paramId": "m4tlj9k1-yfkz5d2d8",
                            "children": []
                        },
                        {
                            "name": "location",
                            "type": "string",
                            "value": "beijing",
                            "paramId": "m4tlja03-0n7gglf4b",
                            "children": []
                        },
                        {
                            "name": "language",
                            "type": "string",
                            "value": "zh-Hans",
                            "paramId": "m4tljadu-p7kxafdjn",
                            "children": []
                        },
                        {
                            "name": "unit",
                            "type": "string",
                            "value": "c",
                            "paramId": "m4tljarh-ygs1c0kmk",
                            "children": []
                        },
                        {
                            "name": "start",
                            "type": "string",
                            "value": "0",
                            "paramId": "m4tljb4v-w7k7hosup",
                            "children": []
                        },
                        {
                            "name": "days",
                            "type": "string",
                            "value": "15",
                            "paramId": "m4tlkd8y-8b4yaht0d",
                            "children": []
                        }
                    ],
                    "responseBody": [
                        {
                            "name": "results",
                            "type": "array",
                            "value": "",
                            "paramId": "m4tlkqpm-e99vtbiqe",
                            "children": []
                        }
                    ],
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5218,
                "nodeId": "4cf1eadf5905459c8294433bb2d2df27",
                "nodeName": "结束",
                "componentId": "a8fab85235db4821b49ff0c913fa6d60",
                "nodeType": 10,
                "input": [
                    {
                        "id": 14703,
                        "paramId": "da0e44f721f8439189bae0a295323c4c",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "4cf1eadf5905459c8294433bb2d2df27",
                        "referenceNodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    },
                    {
                        "id": 14704,
                        "paramId": "94115d103f05401584e17ddcba34768f",
                        "label": "parameter33",
                        "variable": "parameter",
                        "type": "string",
                        "desc": "参数描述",
                        "nodeId": "4cf1eadf5905459c8294433bb2d2df27",
                        "referenceNodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                        "value": "parameter33",
                        "valueType": "string",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "COMPLETE",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 3916,
                "componentId": "a8fab85235db4821b49ff0c913fa6d60",
                "sourceNodeId": "9bd570265f23478195a3a2d45bfa24df",
                "targetNodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                "caseId": null
            },
            {
                "id": 3917,
                "componentId": "a8fab85235db4821b49ff0c913fa6d60",
                "sourceNodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                "targetNodeId": "4cf1eadf5905459c8294433bb2d2df27",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 14699,
                "paramId": "08bca731fb184bef8c7ee83e9ca271bc",
                "label": "address",
                "variable": "address",
                "type": "string",
                "desc": "参数描述",
                "nodeId": "9bd570265f23478195a3a2d45bfa24df",
                "referenceNodeId": null,
                "value": null,
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 14700,
                "paramId": "08bca731fb184bef8c7ee83e9ca271bc",
                "label": "address",
                "variable": "address",
                "type": "string",
                "desc": "参数描述",
                "nodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                "referenceNodeId": "9bd570265f23478195a3a2d45bfa24df",
                "value": "address",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 1
            },
            {
                "id": 14701,
                "paramId": "da0e44f721f8439189bae0a295323c4c",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                "referenceNodeId": "api",
                "value": "${results}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 14702,
                "paramId": "94115d103f05401584e17ddcba34768f",
                "label": "parameter33",
                "variable": "parameter",
                "type": "string",
                "desc": "参数描述",
                "nodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                "referenceNodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                "value": "sfsafa",
                "valueType": "string",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": 2
            },
            {
                "id": 14703,
                "paramId": "da0e44f721f8439189bae0a295323c4c",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "4cf1eadf5905459c8294433bb2d2df27",
                "referenceNodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            },
            {
                "id": 14704,
                "paramId": "94115d103f05401584e17ddcba34768f",
                "label": "parameter33",
                "variable": "parameter",
                "type": "string",
                "desc": "参数描述",
                "nodeId": "4cf1eadf5905459c8294433bb2d2df27",
                "referenceNodeId": "db79bf601fa448aca4b4fbbfc67a1f2e",
                "value": "parameter33",
                "valueType": "string",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "COMPLETE",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 367,
        "componentId": "fea797b246304474bd36f305fde1bd0b",
        "componentName": "百度检索",
        "componentDesc": "百度检索",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-18 14:18:50",
        "updateTime": null,
        "updateUser": null,
        "tenantId": "",
        "nodes": [
            {
                "id": 5173,
                "nodeId": "36d4a74aec364e92a90ff1b6f67d6b82",
                "nodeName": "开始",
                "componentId": "fea797b246304474bd36f305fde1bd0b",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 10995,
                        "paramId": "7c67fba79098405ea8f76e7d67971af9",
                        "label": "req",
                        "variable": "req",
                        "type": "string",
                        "desc": "参数描述req",
                        "nodeId": "36d4a74aec364e92a90ff1b6f67d6b82",
                        "referenceNodeId": null,
                        "value": "今天天气怎么样",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5174,
                "nodeId": "0dff0796f404455cb98abe7ab45a494a",
                "nodeName": "api",
                "componentId": "fea797b246304474bd36f305fde1bd0b",
                "nodeType": 2,
                "input": [
                    {
                        "id": 10996,
                        "paramId": "7c67fba79098405ea8f76e7d67971af9",
                        "label": "req",
                        "variable": "req",
                        "type": "string",
                        "desc": "参数描述req",
                        "nodeId": "0dff0796f404455cb98abe7ab45a494a",
                        "referenceNodeId": "36d4a74aec364e92a90ff1b6f67d6b82",
                        "value": "req",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 10997,
                        "paramId": "64d051f3843e48ea8c772a62b7929013",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "0dff0796f404455cb98abe7ab45a494a",
                        "referenceNodeId": "api",
                        "value": "${data.res_info}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": `${process.env.VUE_APP_BASE_API}/smartApi/llmSmart/yayi`,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": [],
                    "contentType": "application/json",
                    "requestBody": [
                        {
                            "name": "param",
                            "type": "object",
                            "value": "",
                            "paramId": "m4ti5bw6-pb8iatcas",
                            "children": [
                                {
                                    "name": "appKey",
                                    "type": "string",
                                    "value": "091e10fb8d244523912b3463da05652a",
                                    "paramId": "m4ti7o1n-xigf4aa9n",
                                    "children": []
                                },
                                {
                                    "name": "appSecret",
                                    "type": "string",
                                    "value": "af982cc0fbbd41b0bb2009ddadbb992b",
                                    "paramId": "m4ti7ufg-bvve8vr18",
                                    "children": []
                                },
                                {
                                    "name": "id",
                                    "type": "string",
                                    "value": "a2a7ac56a70cd9f59c7caeff1957b270",
                                    "paramId": "m4ti7up0-nyshs2o5m",
                                    "children": []
                                },
                                {
                                    "name": "retryInterval",
                                    "type": "string",
                                    "value": "3000",
                                    "paramId": "m4ti83f1-um7kk9f5t",
                                    "children": []
                                },
                                {
                                    "name": "retryTimes",
                                    "type": "string",
                                    "value": "1",
                                    "paramId": "m4ti83o9-lhaiq6cpg",
                                    "children": []
                                },
                                {
                                    "name": "uri",
                                    "type": "string",
                                    "value": "https://localhost/2deaeebb6f90aadd97ba018ce37465c4/analysis",
                                    "paramId": "m4ti8czg-x2edkn7y8",
                                    "children": []
                                },
                                {
                                    "name": "content",
                                    "type": "object",
                                    "value": "",
                                    "paramId": "m4ti8k8l-xwi4qvws2",
                                    "children": [
                                        {
                                            "name": "function",
                                            "type": "string",
                                            "value": "web_QA",
                                            "paramId": "m4ti8qdu-n30fnujml",
                                            "children": []
                                        },
                                        {
                                            "name": "gen_max_tokens",
                                            "type": "string",
                                            "value": "4000",
                                            "paramId": "m4ti8r6d-kovmkc0ov",
                                            "children": []
                                        },
                                        {
                                            "name": "gen_mind_map",
                                            "type": "string",
                                            "value": "false",
                                            "paramId": "m4ti8rv7-zg7dmga7u",
                                            "children": []
                                        },
                                        {
                                            "name": "get_news_num",
                                            "type": "string",
                                            "value": "3",
                                            "paramId": "m4ti8s5t-laeqocqvt",
                                            "children": []
                                        },
                                        {
                                            "name": "prompt_max_tokens",
                                            "type": "string",
                                            "value": "4000",
                                            "paramId": "m4ti8sg8-mu0b640j1",
                                            "children": []
                                        },
                                        {
                                            "name": "time_limit",
                                            "type": "string",
                                            "value": "8",
                                            "paramId": "m4ti9g9x-1c1t9925n",
                                            "children": []
                                        },
                                        {
                                            "name": "top_k",
                                            "type": "string",
                                            "value": "3",
                                            "paramId": "m4ti9gnv-1rx1rf43a",
                                            "children": []
                                        },
                                        {
                                            "name": "user_message",
                                            "type": "string",
                                            "value": "${req}",
                                            "paramId": "m4ti9gya-9z17gms61",
                                            "children": []
                                        },
                                        {
                                            "name": "web_source_list",
                                            "type": "array",
                                            "value": "[                 \"baidu\"             ]",
                                            "paramId": "m4ti9zno-ccnf6jaic",
                                            "children": []
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "name": "header",
                            "type": "object",
                            "value": "",
                            "paramId": "m4ti5cb6-7kvuw10yp",
                            "children": [
                                {
                                    "name": "Content-Type",
                                    "type": "string",
                                    "value": "application/json",
                                    "paramId": "m4ti76rb-b5hfdd80h",
                                    "children": []
                                },
                                {
                                    "name": "Accept",
                                    "type": "string",
                                    "value": "*/*",
                                    "paramId": "m4ti772m-o28j0nmj0",
                                    "children": []
                                }
                            ]
                        }
                    ],
                    "responseBody": [
                        {
                            "name": "code",
                            "type": "number",
                            "value": "",
                            "paramId": "m4tiav70-7d7976vre",
                            "children": []
                        },
                        {
                            "name": "id",
                            "type": "string",
                            "value": "",
                            "paramId": "m4tib62i-9q9wccz37",
                            "children": []
                        },
                        {
                            "name": "msg",
                            "type": "string",
                            "value": "",
                            "paramId": "m4tib6f5-h9zweefw4",
                            "children": []
                        },
                        {
                            "name": "time",
                            "type": "number",
                            "value": "",
                            "paramId": "m4tib6qj-trft4yd6s",
                            "children": []
                        },
                        {
                            "name": "data",
                            "type": "object",
                            "value": "",
                            "paramId": "m4tib71b-tgqfedzrg",
                            "children": [
                                {
                                    "name": "res_info",
                                    "type": "object",
                                    "value": "",
                                    "paramId": "m4tibup4-hdxt1q1g5",
                                    "children": [
                                        {
                                            "name": "1",
                                            "type": "object",
                                            "value": "",
                                            "paramId": "m4tic0zc-ynonnclk9",
                                            "children": [
                                                {
                                                    "name": "title",
                                                    "type": "string",
                                                    "value": "",
                                                    "paramId": "m4tic6e6-9tqufprdu",
                                                    "children": []
                                                },
                                                {
                                                    "name": "context",
                                                    "type": "string",
                                                    "value": "",
                                                    "paramId": "m4ticb12-78tpr7rhd",
                                                    "children": []
                                                },
                                                {
                                                    "name": "url",
                                                    "type": "string",
                                                    "value": "",
                                                    "paramId": "m4ticf5v-aste6u6fz",
                                                    "children": []
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5175,
                "nodeId": "0aef868cee374114883abf8eb4a95c40",
                "nodeName": "结束",
                "componentId": "fea797b246304474bd36f305fde1bd0b",
                "nodeType": 10,
                "input": [
                    {
                        "id": 10998,
                        "paramId": "64d051f3843e48ea8c772a62b7929013",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "0aef868cee374114883abf8eb4a95c40",
                        "referenceNodeId": "0dff0796f404455cb98abe7ab45a494a",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 3879,
                "componentId": "fea797b246304474bd36f305fde1bd0b",
                "sourceNodeId": "36d4a74aec364e92a90ff1b6f67d6b82",
                "targetNodeId": "0dff0796f404455cb98abe7ab45a494a",
                "caseId": null
            },
            {
                "id": 3880,
                "componentId": "fea797b246304474bd36f305fde1bd0b",
                "sourceNodeId": "0dff0796f404455cb98abe7ab45a494a",
                "targetNodeId": "0aef868cee374114883abf8eb4a95c40",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 10995,
                "paramId": "7c67fba79098405ea8f76e7d67971af9",
                "label": "req",
                "variable": "req",
                "type": "string",
                "desc": "参数描述req",
                "nodeId": "36d4a74aec364e92a90ff1b6f67d6b82",
                "referenceNodeId": null,
                "value": "今天天气怎么样",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 10996,
                "paramId": "7c67fba79098405ea8f76e7d67971af9",
                "label": "req",
                "variable": "req",
                "type": "string",
                "desc": "参数描述req",
                "nodeId": "0dff0796f404455cb98abe7ab45a494a",
                "referenceNodeId": "36d4a74aec364e92a90ff1b6f67d6b82",
                "value": "req",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 1
            },
            {
                "id": 10997,
                "paramId": "64d051f3843e48ea8c772a62b7929013",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "0dff0796f404455cb98abe7ab45a494a",
                "referenceNodeId": "api",
                "value": "${data.res_info}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 10998,
                "paramId": "64d051f3843e48ea8c772a62b7929013",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "0aef868cee374114883abf8eb4a95c40",
                "referenceNodeId": "0dff0796f404455cb98abe7ab45a494a",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 366,
        "componentId": "31c642969dc742e590eb6cf7258d8df6",
        "componentName": "dee",
        "componentDesc": "eee",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-18 12:59:29",
        "updateTime": null,
        "updateUser": null,
        "tenantId": "",
        "nodes": [
            {
                "id": 5148,
                "nodeId": "e7a93f97d621499a96cc8b20da7c7e18",
                "nodeName": "开始",
                "componentId": "31c642969dc742e590eb6cf7258d8df6",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 9428,
                        "paramId": "5c4e693367fa471c87b7ef972a509625",
                        "label": "rawQuery",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": null,
                        "nodeId": "e7a93f97d621499a96cc8b20da7c7e18",
                        "referenceNodeId": "开始",
                        "value": null,
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5149,
                "nodeId": "3902b85fec1d445294c86ffccbc33edc",
                "nodeName": "api",
                "componentId": "31c642969dc742e590eb6cf7258d8df6",
                "nodeType": 2,
                "input": [
                    {
                        "id": 9429,
                        "paramId": "5c4e693367fa471c87b7ef972a509625",
                        "label": "rawQuery",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": null,
                        "nodeId": "3902b85fec1d445294c86ffccbc33edc",
                        "referenceNodeId": "e7a93f97d621499a96cc8b20da7c7e18",
                        "value": "rawQuery",
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "COMPLETE",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 9430,
                        "paramId": "29a69ae093e842abad02c2af9e6fc625",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "3902b85fec1d445294c86ffccbc33edc",
                        "referenceNodeId": "api",
                        "value": "${data}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    },
                    {
                        "id": 9431,
                        "paramId": "1625722674a442149d2368f58b076c91",
                        "label": "parameter33",
                        "variable": "parameter",
                        "type": "string",
                        "desc": "参数描述",
                        "nodeId": "3902b85fec1d445294c86ffccbc33edc",
                        "referenceNodeId": "3902b85fec1d445294c86ffccbc33edc",
                        "value": "333",
                        "valueType": "string",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "settings": {
                    "url": ``,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": [],
                    "contentType": "application/json",
                    "requestBody": [
                        {
                            "name": "sdf",
                            "type": "object",
                            "value": "sdf",
                            "paramId": "m4tfb6o1-32qm6nqqh",
                            "children": [
                                {
                                    "name": "df",
                                    "type": "string",
                                    "value": "sdfs",
                                    "paramId": "m4tfb9ih-nr1m0q2zu",
                                    "children": []
                                }
                            ]
                        }
                    ],
                    "responseBody": [],
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5150,
                "nodeId": "4bed1d8135ae4f9e926f184280bc47ae",
                "nodeName": "结束",
                "componentId": "31c642969dc742e590eb6cf7258d8df6",
                "nodeType": 10,
                "input": [
                    {
                        "id": 9432,
                        "paramId": "29a69ae093e842abad02c2af9e6fc625",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "4bed1d8135ae4f9e926f184280bc47ae",
                        "referenceNodeId": "3902b85fec1d445294c86ffccbc33edc",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    },
                    {
                        "id": 9433,
                        "paramId": "5742d857f93a4151bd51872588d07e9a",
                        "label": "parameter33",
                        "variable": "parameter",
                        "type": "string",
                        "desc": "参数描述",
                        "nodeId": "4bed1d8135ae4f9e926f184280bc47ae",
                        "referenceNodeId": "3902b85fec1d445294c86ffccbc33edc",
                        "value": "parameter33",
                        "valueType": "string",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "COMPLETE",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 3858,
                "componentId": "31c642969dc742e590eb6cf7258d8df6",
                "sourceNodeId": "e7a93f97d621499a96cc8b20da7c7e18",
                "targetNodeId": "3902b85fec1d445294c86ffccbc33edc",
                "caseId": null
            },
            {
                "id": 3859,
                "componentId": "31c642969dc742e590eb6cf7258d8df6",
                "sourceNodeId": "3902b85fec1d445294c86ffccbc33edc",
                "targetNodeId": "4bed1d8135ae4f9e926f184280bc47ae",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 9428,
                "paramId": "5c4e693367fa471c87b7ef972a509625",
                "label": "rawQuery",
                "variable": "rawQuery",
                "type": "string",
                "desc": null,
                "nodeId": "e7a93f97d621499a96cc8b20da7c7e18",
                "referenceNodeId": "开始",
                "value": null,
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9429,
                "paramId": "5c4e693367fa471c87b7ef972a509625",
                "label": "rawQuery",
                "variable": "rawQuery",
                "type": "string",
                "desc": null,
                "nodeId": "3902b85fec1d445294c86ffccbc33edc",
                "referenceNodeId": "e7a93f97d621499a96cc8b20da7c7e18",
                "value": "rawQuery",
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "COMPLETE",
                "nodeType": 1
            },
            {
                "id": 9430,
                "paramId": "29a69ae093e842abad02c2af9e6fc625",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "3902b85fec1d445294c86ffccbc33edc",
                "referenceNodeId": "api",
                "value": "${data}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9431,
                "paramId": "1625722674a442149d2368f58b076c91",
                "label": "parameter33",
                "variable": "parameter",
                "type": "string",
                "desc": "参数描述",
                "nodeId": "3902b85fec1d445294c86ffccbc33edc",
                "referenceNodeId": "3902b85fec1d445294c86ffccbc33edc",
                "value": "333",
                "valueType": "string",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": 2
            },
            {
                "id": 9432,
                "paramId": "29a69ae093e842abad02c2af9e6fc625",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "4bed1d8135ae4f9e926f184280bc47ae",
                "referenceNodeId": "3902b85fec1d445294c86ffccbc33edc",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            },
            {
                "id": 9433,
                "paramId": "5742d857f93a4151bd51872588d07e9a",
                "label": "parameter33",
                "variable": "parameter",
                "type": "string",
                "desc": "参数描述",
                "nodeId": "4bed1d8135ae4f9e926f184280bc47ae",
                "referenceNodeId": "3902b85fec1d445294c86ffccbc33edc",
                "value": "parameter33",
                "valueType": "string",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "COMPLETE",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 362,
        "componentId": "a8a8795e2b7143de9f999aa8c5f60de9",
        "componentName": "ddd333",
        "componentDesc": "ddd",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-18 11:25:45",
        "updateTime": null,
        "updateUser": null,
        "tenantId": "",
        "nodes": [
            {
                "id": 5130,
                "nodeId": "975dd96f1e744b92848d32b1cc8e2f73",
                "nodeName": "开始",
                "componentId": "a8a8795e2b7143de9f999aa8c5f60de9",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 9404,
                        "paramId": "a9735ab0c1f744fcbd658bd831837777",
                        "label": "rawQuery",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": null,
                        "nodeId": "975dd96f1e744b92848d32b1cc8e2f73",
                        "referenceNodeId": "开始",
                        "value": null,
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5131,
                "nodeId": "7bc8ac881efa4a5581267540373530b1",
                "nodeName": "api",
                "componentId": "a8a8795e2b7143de9f999aa8c5f60de9",
                "nodeType": 2,
                "input": [
                    {
                        "id": 9405,
                        "paramId": "a9735ab0c1f744fcbd658bd831837777",
                        "label": "rawQuery",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": null,
                        "nodeId": "7bc8ac881efa4a5581267540373530b1",
                        "referenceNodeId": "975dd96f1e744b92848d32b1cc8e2f73",
                        "value": "rawQuery",
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "COMPLETE",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 9406,
                        "paramId": "75db7336edee4e868d908972b0f1aae1",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "7bc8ac881efa4a5581267540373530b1",
                        "referenceNodeId": "api",
                        "value": "${data}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": ``,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": [],
                    "contentType": "application/json",
                    "requestBody": [
                        {
                            "name": "aaa",
                            "type": "string",
                            "value": "",
                            "paramId": "m4td7cgc-nyqsgn2wo",
                            "children": []
                        },
                        {
                            "name": "bbb",
                            "type": "object",
                            "value": "",
                            "paramId": "m4td7etj-v29e0x2wz",
                            "children": [
                                {
                                    "name": "ccc",
                                    "type": "string",
                                    "value": "sss",
                                    "paramId": "m4td7i0b-sbta1fwys",
                                    "children": []
                                }
                            ]
                        }
                    ],
                    "responseBody": [],
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5132,
                "nodeId": "f7b300b440ee48f9b137a16b963221b8",
                "nodeName": "结束",
                "componentId": "a8a8795e2b7143de9f999aa8c5f60de9",
                "nodeType": 10,
                "input": [
                    {
                        "id": 9407,
                        "paramId": "75db7336edee4e868d908972b0f1aae1",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "f7b300b440ee48f9b137a16b963221b8",
                        "referenceNodeId": "7bc8ac881efa4a5581267540373530b1",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 3845,
                "componentId": "a8a8795e2b7143de9f999aa8c5f60de9",
                "sourceNodeId": "975dd96f1e744b92848d32b1cc8e2f73",
                "targetNodeId": "7bc8ac881efa4a5581267540373530b1",
                "caseId": null
            },
            {
                "id": 3846,
                "componentId": "a8a8795e2b7143de9f999aa8c5f60de9",
                "sourceNodeId": "7bc8ac881efa4a5581267540373530b1",
                "targetNodeId": "f7b300b440ee48f9b137a16b963221b8",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 9404,
                "paramId": "a9735ab0c1f744fcbd658bd831837777",
                "label": "rawQuery",
                "variable": "rawQuery",
                "type": "string",
                "desc": null,
                "nodeId": "975dd96f1e744b92848d32b1cc8e2f73",
                "referenceNodeId": "开始",
                "value": null,
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9405,
                "paramId": "a9735ab0c1f744fcbd658bd831837777",
                "label": "rawQuery",
                "variable": "rawQuery",
                "type": "string",
                "desc": null,
                "nodeId": "7bc8ac881efa4a5581267540373530b1",
                "referenceNodeId": "975dd96f1e744b92848d32b1cc8e2f73",
                "value": "rawQuery",
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "COMPLETE",
                "nodeType": 1
            },
            {
                "id": 9406,
                "paramId": "75db7336edee4e868d908972b0f1aae1",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "7bc8ac881efa4a5581267540373530b1",
                "referenceNodeId": "api",
                "value": "${data}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9407,
                "paramId": "75db7336edee4e868d908972b0f1aae1",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "f7b300b440ee48f9b137a16b963221b8",
                "referenceNodeId": "7bc8ac881efa4a5581267540373530b1",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 361,
        "componentId": "c9e4b4791c034dee96a47c1caa0c79a2",
        "componentName": "ddd",
        "componentDesc": "ddd",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-18 11:19:04",
        "updateTime": null,
        "updateUser": null,
        "tenantId": "",
        "nodes": [
            {
                "id": 5127,
                "nodeId": "2c030c4eb83a4e62835a31913b9ca131",
                "nodeName": "开始",
                "componentId": "c9e4b4791c034dee96a47c1caa0c79a2",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 9356,
                        "paramId": "95c3d4f3cfbd4c2790998137dcf2dc2d",
                        "label": "parameter333",
                        "variable": "parameter333",
                        "type": "string",
                        "desc": "参数描述",
                        "nodeId": "2c030c4eb83a4e62835a31913b9ca131",
                        "referenceNodeId": null,
                        "value": null,
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5128,
                "nodeId": "d3086faffb9f489f9f3fec6af2a13ac4",
                "nodeName": "api",
                "componentId": "c9e4b4791c034dee96a47c1caa0c79a2",
                "nodeType": 2,
                "input": [
                    {
                        "id": 9357,
                        "paramId": "aea2e4909dd1488c82cfd54c68aafc9a",
                        "label": "msg",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": "API节点的默认入参",
                        "nodeId": "d3086faffb9f489f9f3fec6af2a13ac4",
                        "referenceNodeId": "2c030c4eb83a4e62835a31913b9ca131",
                        "value": "rawQuery",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 9358,
                        "paramId": "bb7bae19e1d64ebe81016d2fe27cc145",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "d3086faffb9f489f9f3fec6af2a13ac4",
                        "referenceNodeId": "api",
                        "value": "${data}",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": ``,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": "",
                    "contentType": "application/json",
                    "requestBody": "",
                    "responseBody": "",
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5129,
                "nodeId": "90d9579f4e664ea78bbd378352d4314c",
                "nodeName": "结束",
                "componentId": "c9e4b4791c034dee96a47c1caa0c79a2",
                "nodeType": 10,
                "input": [
                    {
                        "id": 9359,
                        "paramId": "bb7bae19e1d64ebe81016d2fe27cc145",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "90d9579f4e664ea78bbd378352d4314c",
                        "referenceNodeId": "d3086faffb9f489f9f3fec6af2a13ac4",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 3843,
                "componentId": "c9e4b4791c034dee96a47c1caa0c79a2",
                "sourceNodeId": "2c030c4eb83a4e62835a31913b9ca131",
                "targetNodeId": "d3086faffb9f489f9f3fec6af2a13ac4",
                "caseId": null
            },
            {
                "id": 3844,
                "componentId": "c9e4b4791c034dee96a47c1caa0c79a2",
                "sourceNodeId": "d3086faffb9f489f9f3fec6af2a13ac4",
                "targetNodeId": "90d9579f4e664ea78bbd378352d4314c",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 9356,
                "paramId": "95c3d4f3cfbd4c2790998137dcf2dc2d",
                "label": "parameter333",
                "variable": "parameter333",
                "type": "string",
                "desc": "参数描述",
                "nodeId": "2c030c4eb83a4e62835a31913b9ca131",
                "referenceNodeId": null,
                "value": null,
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9357,
                "paramId": "aea2e4909dd1488c82cfd54c68aafc9a",
                "label": "msg",
                "variable": "rawQuery",
                "type": "string",
                "desc": "API节点的默认入参",
                "nodeId": "d3086faffb9f489f9f3fec6af2a13ac4",
                "referenceNodeId": "2c030c4eb83a4e62835a31913b9ca131",
                "value": "rawQuery",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 1
            },
            {
                "id": 9358,
                "paramId": "bb7bae19e1d64ebe81016d2fe27cc145",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "d3086faffb9f489f9f3fec6af2a13ac4",
                "referenceNodeId": "api",
                "value": "${data}",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9359,
                "paramId": "bb7bae19e1d64ebe81016d2fe27cc145",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "90d9579f4e664ea78bbd378352d4314c",
                "referenceNodeId": "d3086faffb9f489f9f3fec6af2a13ac4",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    },
    {
        "id": 359,
        "componentId": "01bcbabce4924c738f8c3d501c96b2bc",
        "componentName": "sdfsdf",
        "componentDesc": "sdfsdf",
        "status": 0,
        "type": 1,
        "icon": "static/img/applicationlogo.654dd245.svg",
        "canvas": null,
        "createUser": "admin-demo",
        "createTime": "2024-12-18 11:15:06",
        "updateTime": null,
        "updateUser": null,
        "tenantId": "",
        "nodes": [
            {
                "id": 5121,
                "nodeId": "86d22d5f89ad43999b128b2bc19100cd",
                "nodeName": "开始",
                "componentId": "01bcbabce4924c738f8c3d501c96b2bc",
                "nodeType": 1,
                "input": [],
                "output": [
                    {
                        "id": 9344,
                        "paramId": "0b333d1780b74e0d8501dce93877ca74",
                        "label": "rawQuery",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": null,
                        "nodeId": "86d22d5f89ad43999b128b2bc19100cd",
                        "referenceNodeId": "开始",
                        "value": null,
                        "valueType": null,
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": null,
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5122,
                "nodeId": "d3db6af46a614e6a9b64e93890fbe4ce",
                "nodeName": "api",
                "componentId": "01bcbabce4924c738f8c3d501c96b2bc",
                "nodeType": 2,
                "input": [
                    {
                        "id": 9345,
                        "paramId": "b887361172bc47bda75e7eb35cf5d4de",
                        "label": "msg",
                        "variable": "rawQuery",
                        "type": "string",
                        "desc": "API节点的默认入参",
                        "nodeId": "d3db6af46a614e6a9b64e93890fbe4ce",
                        "referenceNodeId": "86d22d5f89ad43999b128b2bc19100cd",
                        "value": "rawQuery",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 1
                    }
                ],
                "output": [
                    {
                        "id": 9346,
                        "paramId": "fb118bf6bbb3442c8e0351307345c720",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "API节点的默认出参",
                        "nodeId": "d3db6af46a614e6a9b64e93890fbe4ce",
                        "referenceNodeId": "api",
                        "value": "data",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 1,
                        "status": "UNINITIALIZED",
                        "nodeType": null
                    }
                ],
                "settings": {
                    "url": ``,
                    "Accept": "*/*",
                    "method": "POST",
                    "headers": "",
                    "contentType": "application/json",
                    "requestBody": "",
                    "responseBody": "",
                    "responseType": false
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            },
            {
                "id": 5123,
                "nodeId": "95ceab50412943808af8109817e1803a",
                "nodeName": "结束",
                "componentId": "01bcbabce4924c738f8c3d501c96b2bc",
                "nodeType": 10,
                "input": [
                    {
                        "id": 9347,
                        "paramId": "ad4cabe5aeb644bfa610bf62b75c8ab4",
                        "label": "result",
                        "variable": "result",
                        "type": "string",
                        "desc": "结束节点的默认入参",
                        "nodeId": "95ceab50412943808af8109817e1803a",
                        "referenceNodeId": "d3db6af46a614e6a9b64e93890fbe4ce",
                        "value": "result",
                        "valueType": "reference",
                        "maxLength": 20,
                        "required": true,
                        "direction": 0,
                        "status": "UNINITIALIZED",
                        "nodeType": 2
                    }
                ],
                "output": [],
                "settings": {
                    "responseModel": 0,
                    "responseTemplate": "本次请求响应result为:${result}"
                },
                "next": null,
                "sseClientId": null,
                "clientType": null,
                "tokenUser": null
            }
        ],
        "componentNodes": null,
        "nodeRel": [
            {
                "id": 3839,
                "componentId": "01bcbabce4924c738f8c3d501c96b2bc",
                "sourceNodeId": "86d22d5f89ad43999b128b2bc19100cd",
                "targetNodeId": "d3db6af46a614e6a9b64e93890fbe4ce",
                "caseId": null
            },
            {
                "id": 3840,
                "componentId": "01bcbabce4924c738f8c3d501c96b2bc",
                "sourceNodeId": "d3db6af46a614e6a9b64e93890fbe4ce",
                "targetNodeId": "95ceab50412943808af8109817e1803a",
                "caseId": null
            }
        ],
        "metaParams": [
            {
                "id": 9344,
                "paramId": "0b333d1780b74e0d8501dce93877ca74",
                "label": "rawQuery",
                "variable": "rawQuery",
                "type": "string",
                "desc": null,
                "nodeId": "86d22d5f89ad43999b128b2bc19100cd",
                "referenceNodeId": "开始",
                "value": null,
                "valueType": null,
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9345,
                "paramId": "b887361172bc47bda75e7eb35cf5d4de",
                "label": "msg",
                "variable": "rawQuery",
                "type": "string",
                "desc": "API节点的默认入参",
                "nodeId": "d3db6af46a614e6a9b64e93890fbe4ce",
                "referenceNodeId": "86d22d5f89ad43999b128b2bc19100cd",
                "value": "rawQuery",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 1
            },
            {
                "id": 9346,
                "paramId": "fb118bf6bbb3442c8e0351307345c720",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "API节点的默认出参",
                "nodeId": "d3db6af46a614e6a9b64e93890fbe4ce",
                "referenceNodeId": "api",
                "value": "data",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 1,
                "status": "UNINITIALIZED",
                "nodeType": null
            },
            {
                "id": 9347,
                "paramId": "ad4cabe5aeb644bfa610bf62b75c8ab4",
                "label": "result",
                "variable": "result",
                "type": "string",
                "desc": "结束节点的默认入参",
                "nodeId": "95ceab50412943808af8109817e1803a",
                "referenceNodeId": "d3db6af46a614e6a9b64e93890fbe4ce",
                "value": "result",
                "valueType": "reference",
                "maxLength": 20,
                "required": true,
                "direction": 0,
                "status": "UNINITIALIZED",
                "nodeType": 2
            }
        ],
        "sseClientId": null,
        "applicationInfo": null,
        "clientType": null
    }
    ]