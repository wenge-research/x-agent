export const INFORMATION_TYPE = {
  "title": "查询医保卡故障原因",
  "message": "查询个人医保卡故障原因需填写姓名、身份证号、联系方式等信息，并需居民手持身份证拍照留存，工作人员将在确认您的身份后告知是否属于常规原因导致医保卡无法实时结算例如单位欠费、个人断交医疗保险或未选择该定点医疗机构等。",
  "matterGuide": {
    "id": 9,
    "applicationId": "00759c5a4ae74622971ee51142287e2d",
    "matterId": "53d130cfc70d4e2b815c68a62baf2521",
    "matterName": "查询医保卡异常原因",
    "matterRoute": null,
    "matterType": "办理指南",
    "prompt": "您好，我是关芯客服，很高兴为您服务，请问我有什么可以帮助您的？",
    "extraSystemPrompt": "现在填写医保卡故障原因，你需要向用户收集真实姓名、身份证号、联系电话和手持身份证照片信息，你将会遵守相关法律法规，保护用户的个人隐私和信息安全。",
    "enterTip": "您可点击下方按钮进行个人信息登记",
    "fillTip": "查询个人医保卡故障原因需填写姓名、身份证号、联系方式等信息，并需居民手持身份证拍照留存，工作人员将在确认您的身份后告知是否属于常规原因导致医保卡无法实时结算例如单位欠费、个人断缴医疗保险或未选择该定点医疗机构等。",
    "completeFillTip": "工作人员将会在一个工作日内跟您联系，请保持电话畅通。",
    "iconUrl": "https://dibrain.localhost/smart-agent-api-demo/wos/file/download?fileKey=登记.png",
    "statement": "# 个人信息收集声明\n## 1. 引言\n我们非常重视您的个人信息保护。为了使您更好地了解我们如何收集、使用、存储和保护您的个人信息，我们特此制定本个人信息收集声明。本声明将详细说明我们收集的信息类型、收集目的、使用方式、存储期限以及您的权利。\n## 2. 信息收集\n我们可能会收集以下类型的个人信息：\n- 基本个人信息：包括但不限于姓名、性别、出生日期、联系方式（如电话、电子邮件地址）等。\n- 身份验证信息：用于验证您的身份，可能包括身份证号码、护照号码等。\n- 位置信息：通过GPS或其他定位服务获取的地理位置信息。\n- 行为信息：您在使用我们的服务时产生的浏览记录、点击记录等。\n- 其他信息：根据具体服务需求可能收集的其他信息。\n## 3. 信息使用\n我们收集的个人信息将用于以下目的：\n- 提供和改进我们的服务。\n- 进行用户身份验证。\n- 进行客户服务和支持。\n- 遵守相关法律法规的要求。\n## 4. 信息存储\n我们承诺将您的个人信息存储在安全的服务器上，并采取适当的安全措施保护您的信息不被未经授权的访问、使用或泄露。我们不会无故保留您的个人信息超过必要的时间。\n## 5. 信息共享\n我们不会将您的个人信息出售、交换或以其他方式提供给任何第三方，除非：\n- 获得您的明确同意。\n- 为了提供服务或满足法律法规的要求。\n## 6. 用户权利\n您有权：\n- 访问您的个人信息。\n- 更正或删除您的个人信息。\n- 撤回您对个人信息收集和使用的同意。\n- 对个人信息的处理提出异议。\n## 7. 安全措施\n我们采取了以下措施来保护您的个人信息：\n- 加密传输。\n- 访问控制。\n- 定期的安全审计。\n## 8. 未成年人保护\n我们非常重视未成年人的个人信息保护，不会在未经监护人同意的情况下收集未成年人的个人信息。\n## 9. 声明更新\n我们可能会不时更新本个人信息收集声明。任何更新将通过我们的网站或其他适当的方式通知您。\n",
    "showFlag": "是",
    "subject": "在讨论医保卡故障原因",
    "pkValues": [
      9
    ]
  },
  "forms": [
    {
      "basicInfo": "基本信息",
      "type": "form",
      "filedName": "basicInfo",
      "configs": [
        {
          "filedId": "61",
          "label": "姓名",
          "type": "input",
          "field": "peopleName",
          "placeholder": "请输入",
          "prompt": null,
          "checkRuleCode": null,
          "options": null
        },
        {
          "filedId": "62",
          "label": "身份证号",
          "type": "input",
          "field": "idCard",
          "placeholder": "请输入",
          "prompt": null,
          "checkRuleCode": "1001",
          "options": null
        },
        {
          "filedId": "63",
          "label": "联系电话",
          "type": "input",
          "field": "phone",
          "placeholder": "请输入",
          "prompt": null,
          "checkRuleCode": "2001",
          "options": null
        },
        {
          "filedId": "e7e1f71fbbef4e3c8a6dcf7b789dd60f",
          "label": "就诊日期",
          "type": "datePicker",
          "field": "dateOfVisit",
          "placeholder": "请选择日期",
          "prompt": null,
          "checkRuleCode": null,
          "options": null
        },
        {
          "filedId": "65efb8289e1f4a4f8d3c416680046ef4",
          "label": "就诊医院",
          "type": "input",
          "field": "visitingHospital",
          "placeholder": "请输入就诊医院",
          "prompt": null,
          "checkRuleCode": null,
          "options": null
        },
        {
          "filedId": "64",
          "label": "身份证明照片",
          "type": "file",
          "field": "idCardFrontPic",
          "placeholder": "请上传手持身份证照片",
          "prompt": "需采集面部和身份证件信息，同时露脸和身份证",
          "checkRuleCode": null,
          "options": null
        }
      ]
    }
  ],
  "params": {
    "peopleName": "",
    "idCard": "",
    "phone": "",
    "dateOfVisit": "",
    "visitingHospital": "",
    "idCardFrontPic": []
  }
}
export const ADDFORM_TYPE_FORM = {
  addFormGzjl: {
    type: '工作经历',
    configs: [
      {
        filedId: '1',
        label: "公司名称",
        type: "input",
        field: "companyName",
        placeholder: "请输入公司名称",
        options: null
      },
      {
        filedId: '2',
        label: "职业",
        type: "input",
        field: "occupation",
        placeholder: "请输入职业",
        options: null
      },
      {
        filedId: '3',
        label: "任职时间",
        type: "date",
        field: "employmentPeriod",
        placeholder: "请选择任职时间",
        options: null
      },
      {
        filedId: '4',
        label: "工作内容",
        type: "input",
        field: "jobContent",
        placeholder: "请输入工作内容",
        options: null
      }
    ],
    params: {
      companyName: '', // 公司名称
      occupation: '', // 职业 
      employmentPeriod: '', // 任职时间
      jobContent: '' // 工作内容
    }
  },
  addFormZyjn: {
    type: '职业技能',
    configs: [
      {
        filedId: '5',
        label: "技能职称",
        type: "input",
        field: "technicalTitle",
        placeholder: "请输入技能职称",
        options: null
      },
      {
        filedId: '6',
        label: "级别",
        type: "input",
        field: "level",
        placeholder: "请输入级别",
        options: null
      },
      {
        filedId: '7',
        label: "掌握时间",
        type: "date",
        field: "manageTime",
        placeholder: "请输入掌握时间",
        options: null
      },
    ],
    params: {
      technicalTitle: '', // 职业技能 
      level: '', // 技能级别
      manageTime: '', // 技能时间
    },
  },
  addFormWytc: {
    type: '外语特长',
    configs: [
      {
        filedId: '8',
        label: "外语语种",
        type: "input",
        field: "language",
        placeholder: "请输入技能时间",
        options: null
      },
      {
        filedId: '9',
        label: "外语等级",
        type: "input",
        field: "level",
        placeholder: "请输入技能时间",
        options: null
      },
    ],
    params: {
      language: '', // 语种 
      level: '', // 等级
    },
  },
  addFormJsjdjzs: {
    type: '计算机等级证书',
    configs: [
      {
        filedId: '10',
        label: "计算机等级",
        type: "input",
        field: "language",
        placeholder: "请输入计算机等级",
        options: null
      },
      {
        filedId: '11',
        label: "掌握程度",
        type: "input",
        field: "level",
        placeholder: "请输入掌握程度",
        options: null
      },
    ],
    params: {
      language: '', // 计算机等级 
      level: '', // 掌握程度
    },
  }
}
export const TEMPORARY_OPTIONS = [
  {
    "filedId": "73",
    "id": null,
    "label": null
  },
  {
    "filedId": "73",
    "id": null,
    "label": null
  },
  {
    "filedId": "73",
    "id": null,
    "label": null
  },
]