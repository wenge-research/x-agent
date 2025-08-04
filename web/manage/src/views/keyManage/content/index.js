import app from '../../../main';

export const TAG_MAP = [app.$t('legalIssue'), app.$t('marriageIssue'), app.$t('civilCode'), app.$t('policyDocument') ]

export const OPERATE_OPTIONS = [
  {
    id: '1',
    label: app.$t('qaUpload'),
    code: 'qaData',
  },
  {
    id: '2',
    label: app.$t('documentData'),
    code: 'docData'
  },
  {
    id: '3',
    label: app.$t('urlUpload'),
    code: 'urlData'
  },
  // {
  //   id: '4',
  //   label: app.$t('apiCollection'),
  //   code: 'apiData'
  // },
  {
    id: '5',
    label: app.$t('structuredData'),
    code: 'structData'
  },
  {
    id: '6',
    label: app.$t('multimedia'),
    code: 'multi-media'
  },
  // {
  //   id: '7',
  //   label: app.$t('yiYiDocumentLibrary'),
  //   code: 'yayiDocData'
  // }
]

export const SPLIT_OPTIONS = [
  {
    id: 1,
    label: app.$t('intelligentSegmentation'),
    message: app.$t('yiYiModelJudgment')
  },
  {
    id: 2,
    label: app.$t('customSegmentationStrategy'),
    message: app.$t('sentenceSplittingAndSegmentation')
  }
]

export const MARKING_OPTIONS = [
  {
    id: 1,
    label: app.$t('chineseComma'),
    con: '，'
  },
  {
    id: 2,
    label: app.$t('chinesePeriod'),
    con: '。'
  }
]

export const MODULE_OPTIONS = [
  {
    id: 1,
    label: app.$t('all')
  },
  {
    id: 2,
    label: app.$t('policyQa')
  },
  {
    id: 3,
    label: app.$t('policyLink')
  }
]

export const SUGGEST_OPTIONS = [
  {
    id: 1,
    label: app.$t('recommend')
  },
  {
    id: 2,
    label: app.$t('notRecommend')
  }
]

export const ACCURATE_OPTIONS =[
  {
    id: 1,
    label: app.$t('yes')
  },
  {
    id: 2,
    label: app.$t('no')
  }
]

export const IMPORT_OPTIONS = [
  {
    id: 1,
    label: app.$t('overwrite'),
    con: '是'
  },
  {
    id: 2,
    label: app.$t('append'),
    con: '否'
  }
]

export const SLICE_OPTIONS = [
  {
    id: 1,
    label: app.$t('all')
  },
  {
    id: 2,
    label: app.$t('originalTextSlice')
  },
  {
    id: 3,
    label: app.$t('newSlice')
  },
  {
    id: 4,
    label: app.$t('copySlice')
  }
]

export const FREQUENCY_OPTIONS = [
  {
    id: 1,
    label: app.$t('autoUpdate')
  },
  {
    id: 2,
    label: app.$t('noAutoUpdate')
  }
]

export const FILE_TYP = [
  // 0 上传中；1 解析中；2 解析成功；3 解析失败，4 上传失败,5 上传成功
  {
    label: app.$t('uploading'),
    color: '#295BF6'
  },
  {
    label: app.$t('parsing'),
    color: '#1747E5'
  },
  {
    label: app.$t('parseSuccess'),
    color: '#3FB816'
  },
  {
    label: app.$t('parseFailure'),
    color: '#D33A22'
  },
  {
    label: app.$t('uploadFailure'),
    color: '#D33A22'
  },
  {
    label: app.$t('uploadSuccess'),
    color: '#F9A602'
  },
  {
    label: app.$t('decryptionFailure'),
    color: '#D13130'
  }
]

export const URL_TYP = [
  {
    label: app.$t('parsing'),
    color: '#295BF6'
  },
  {
    label: app.$t('parseSuccess'),
    color: '#18BF84'
  },
  {
    label: app.$t('parseFailure'),
    color: '#D13130'
  },
]

export const TASK_TYP = [
  {
    label: app.$t('notParsed'),
    color: '#295BF6'
  },
  {
    label: app.$t('parsingNow'),
    color: '#295BF6'
  },
  {
    label: app.$t('parseSuccess'),
    color: '#18BF84'
  },
  {
    label: app.$t('parseFailure'),
    color: '#D13130'
  },
]

export const TASK_TYPE_OPTIONS = [
  {
    id: 1,
    icon: require('@/assets/images/xlsx.png'),
    bgColor: '#DDF3E8',
    label: app.$t('excelUpload')
  },
  {
    id: 2,
    icon: require('@/assets/images/sql.png'),
    bgColor: '#DFEDFF',
    label: app.$t('databaseConnection')
  }
]

export const DETAILS_FIELD_MAP = [
  {
    id: 1,
    label: app.$t('fileLinkName'),
    field: 'desc'
  },
  {
    id: 2,
    label: app.$t('hostNameOrIpAddress'),
    field: 'jdbcUrl'
  },
  // {
  //   id: 3,
  //   label: app.$t('port'),
  //   field: ''
  // },
  {
    id: 4,
    label: app.$t('username'),
    field: 'jdbcName'
  },
  {
    id: 5,
    label: app.$t('password'),
    field: 'jdbcPassword'
  },
]