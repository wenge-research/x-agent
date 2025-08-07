## 配置文件
```yaml
appframe:
  yayi:
    config:
      host: https://yayi.wenge.com/saas-gateway
      appKey: 1111111
      signatureMethod: HmacSHA256
      appSecret: 1111111111
      accept: "*/*"
      contentType: "application/json; charset=utf-8"
      method: POST
    #      YAYI-13B
    generate13b:
      uri: https://yayi.wenge.com/saas-gateway/238b7e0a4459ce5dc4e8b0a79bbaf719/generate
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      maxNewTokens: 2048 # 默认256（该值 + messages 字段的 token 数不能超过上下文窗口的总 token 数）
      doSample: false # 默认为 true，如果开启，每次返回的存在多样性；如果为 false，则使用 beam search算法，每次返回唯一结果（流式输出模式时请勿使用 beam search 算法）。
      temperature: 0.3 # 可选 0.0-2.0 之间，该值越大，生成多样性越大，默认0.4。若设置为 0.0，则等同于开启 beam search，每次返回概率最高的唯一结果。
      presencePenalty: 0.1 # 浮点数 [-2, 2]，基于单词是否出现在已生成的文本中来惩罚新单词。值大于0鼓励模型探讨输入以外的新主题，而值小于0会鼓励模型在原主题范围进行探讨。默认0.1。
      repetitionPenalty: 1.2
      frequencyPenalty: 0.16 # 浮点数 [-2, 2]，基于单词在已生成文本中出现的频率来惩罚新单词。值大于0鼓励模型使用新的单词，而值小于0会鼓励模型重复使用单词。默认0.16。
      topP: 1 # 浮点数，用于控制考虑的前几个词的概率累积。必须在 (0, 1] 范围内。默认为 1.0，表示考虑所有可能的词
      topK: -1 # 整数，用于控制考虑的前几个词的数量。默认为-1，表示考虑所有可能的词
      bestOf: 1 # 仅当使用 beam search 算法（即 do_sample = false ）时有效，且需大于1。
      n: # 并行推理，默认为1。
      model: yayi # 选择模型
      # 雅意30B
    generate30b:
      uri: https://yayi.wenge.com/saas-gateway/e494f3e6c0a66b624869281f0c343c71/yayi2/generate
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      maxNewTokens: 2048 # 默认512（该值 + messages 字段的 token 数不能超过上下文窗口的总 token 数 8192）
      doSample: true # 默认为 true，如果开启，每次返回的存在多样性；如果为 false，则使用 beam search算法，每次返回唯一结果（流式输出模式时请勿使用 beam search 算法）。
      temperature: 0.3 # 可选 0.0-2.0 之间，该值越大，生成多样性越大，默认0.4。若设置为 0.0，则等同于开启 beam search，每次返回概率最高的唯一结果。
      presencePenalty: 0.1 # 默认0.1，浮点数 [-2, 2]，基于单词是否出现在已生成的文本中来惩罚新单词。值大于0鼓励模型探讨输入以外的新主题，而值小于0会鼓励模型在原主题范围进行探讨。
      frequencyPenalty: 0.16 # 默认0.16。浮点数 [-2, 2]，基于单词在已生成文本中出现的频率来惩罚新单词。值大于0鼓励模型使用新的单词，而值小于0会鼓励模型重复使用单词。
      topP: 1 # 浮点数，用于控制考虑的前几个词的概率累积。必须在 (0, 1] 范围内。默认为 1.0，表示考虑所有可能的词
      topK: -1 # 整数，用于控制考虑的前几个词的数量。默认为-1，表示考虑所有可能的词
      n: # 并行推理，默认为1。
      model: yayi # 选择模型
      bestOf: 1 #  仅当使用 beam search 算法（即 do_sample = false ）时有效，且需大于1。
    # YAYI-搜索
    promptweb:
      uri: https://yayi.wenge.com/saas-gateway/2deaeebb6f90aadd97ba018ce37465c4/analysis
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      function: web_QA
      userId: czw
      promptMaxTokens: 3000
      getNewsNum: 10
      topK: 5
      webSourceList: baidu
    # YAYI-Embedding
    embedding:
      uri: https://yayi.wenge.com/saas-gateway/29581a23913cff17d55646b28cc852ef/analysis
      function: get_embedding # ‘get_embedding’,‘recall_top_n’,‘de_get_top_n’
      model: m3e
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      data_type: query # 可填：‘query’,'para‘。默认para。
    generate128k:
      uri: https://yayi.wenge.com/saas-gateway/7dbcd08f6a14f716fe2c8bb1926fc6bb/generate
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      maxNewTokens: 2048 # 默认512（该值 + messages 字段的 token 数不能超过上下文窗口的总 token 数 8192）
      doSample: true # 默认为 true，如果开启，每次返回的存在多样性；如果为 false，则使用 beam search算法，每次返回唯一结果（流式输出模式时请勿使用 beam search 算法）。
      temperature: 0.3 # 可选 0.0-2.0 之间，该值越大，生成多样性越大，默认0.4。若设置为 0.0，则等同于开启 beam search，每次返回概率最高的唯一结果。
      presencePenalty: 0.1 # 默认0.1，浮点数 [-2, 2]，基于单词是否出现在已生成的文本中来惩罚新单词。值大于0鼓励模型探讨输入以外的新主题，而值小于0会鼓励模型在原主题范围进行探讨。
      frequencyPenalty: 0.16 # 默认0.16。浮点数 [-2, 2]，基于单词在已生成文本中出现的频率来惩罚新单词。值大于0鼓励模型使用新的单词，而值小于0会鼓励模型重复使用单词。
      topP: 1 # 浮点数，用于控制考虑的前几个词的概率累积。必须在 (0, 1] 范围内。默认为 1.0，表示考虑所有可能的词
      topK: -1 # 整数，用于控制考虑的前几个词的数量。默认为-1，表示考虑所有可能的词
      n: # 并行推理，默认为1。
      model: yayi # 选择模型
      bestOf: 1 #  仅当使用 beam search 算法（即 do_sample = false ）时有效，且需大于1。
    rearrange:
      uri: https://yayi.wenge.com/saas-gateway/0e18cc231e0ac0f2a86c5a509943380b/analysis
      n: 10
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      function: de_get_top_n
    dialogue:
      uri: https://yayi.wenge.com/saas-gateway/325e977d345f579d3e373fdcf891535a/analysis
      preprocess_type: "mixed_method"
      cosine_threshold_value: 0.7
      distance_threshold_value: 0.1
      n: 1
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
    document:
      uri: https://yayi.wenge.com/saas-gateway/8fcea6a7f02abcfca2befe4a27fcb807/analysis
      do_layout: 0
      get_bbox: 0
      table_html: 0
      watermark: 0
      faq: 0
      model: 0
    security:
      uri: https://yayi.wenge.com/saas-gateway/e2c09cd1daac57f1cd6055d215625767/analysis
      securityLevel: lowwords,lowwordsre,negetiveword,negetivere
      model: q
    question:
      uri: https://yayi.wenge.com/saas-gateway/bc96a88063a8ab55b2098d341c24b399/analysis
      model: mt5 # 选用模型,默认mt5，可填’mt5’、‘yayi’
      max_length: 60 # model=mt5时，生成最长tokens数量，默认150
      no_repeat_ngram_size: 2 # model=mt5时，去除重复，默认2
      num_beams: 2 # model=mt5时，beamsearch值，默认4
      questions_num: 1 # 生成问题个数，默认10
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
    urltext:
      uri: https://yayi.wenge.com/saas-gateway/ff509040b0149e6e8c6a3bd434c9c8cf/analysis
      model: txt # 填写“content",“markdown”,"html"中的一个，表示url中的数据输出格式。
      ignore_links: true # 是否去除markdown中的链接，True为不保留链接，False为保留。默认为True。仅对输出为markdown的格式有用
      ignore_images: true # 是否去除markdown中的图片链接，True为不保留链接，False为保留。默认为True。仅对输出为markdown的格式有用
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
    knowledge:
      uri: https://yayi.wenge.com/saas-gateway/73d9787fb007cf01a79ed33c16383c5f/analysis
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      cts_chunk_size: 700
      cts_chunk_overlap: 200
      rcts_chunk_overlap: 200
      rcts_chunk_size: 200
      merge_split_approach: "RCTS","CTS"
      return_chunk: true
    knowledgesplit:
      uri: https://yayi.wenge.com/saas-gateway/73d9787fb007cf01a79ed33c16383c5f/analysis
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      cts_chunk_size: 700
      cts_chunk_overlap: 200
      rcts_chunk_overlap: 200
      rcts_chunk_size: 200
      merge_split_approach: "RCTS","CTS"
      return_chunk: true
    contentparsing:
      uri: https://yayi.wenge.com/saas-gateway/c1c8673f3bea573649750fee4b7a2fb5/analysis
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      mode: 0
      get_ocr: 0
      watermark: 1
      xls_parse: 1
      webmode: txt
      get_caption: 0
    visual:
      uri: https://yayi.wenge.com/saas-gateway/1042b605ae119f5f1ad203fb51a94dec/generate
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      mode: yayi
      stream: false
      top_p: 0.3
      top_k: 10
      temperature: 0.5
      max_new_tokens: 2048
      do_sample: true
    translation:
      uri: https://yayi.wenge.com/saas-gateway/43300de45a5136bb6c017423b9b50830/analysis
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      src_lang: zh
      tgt_lang: en
    aiimage:
      uri: https://yayi.wenge.com/saas-gateway/29d751fe4b32e0624fd78aa394e81e46/api/generate
    plugin:
      uri: https://yayi.wenge.com/saas-gateway/f568edd668a85d4fb5c6900fe7befe85/analysis
      avaliable_plugins: # "此次请求想要触发的工具可选范围。\n此参数应为下列三种情况中的一种：\n1）此参数应为下列插件列表中的一个或多个插件的组合\n“search”, “weather”, “yayi_text2image”, “wenhai”，“yayi_128k”, “yayi”, “yayi-math”\n2）此参数设置为[“all”]。将默认设置工具触发范围为上述所有插件\n3）此参数设置为[]，将默认设置工具触发范围为[“search”, “yayi-math”, “weather”]"
        - all
      max_new_tokens: 2000 # 生成Token的最大数量，默认2000
      best_of: 1 # 可以指定需要生成的次数，模型将返回这些次数中得分最高的结果作为最终输出，默认为1
      max_window_size: 6144
      use_recommendation: true
      use_only_custom_plugins: false
    index:
      uri: https://yayi.wenge.com/saas-gateway/76bab49f3ce8a61b4a1818bf7506f093/analysis
      mode: # 该字段支持\"chat\",“file”, “chat_with_quotes”, \"multimodel\"四种，默认为file模式\nchat: 对应YaYi-Chat平台模式。流式添加索引。一次输出单句话的索引添加，输入为多句话。\nfile: 对应YaYi-File平台模式。传入多段内容，按段进行索引添加。一次性添加完成所有索引。\nchat_with_quotes: 适用于chat模式流式添加完所有索引后，提取对应引文中最相关的内容，并用em标签进行包裹，适用于引文浮窗高亮模式。\nmultimodal: 输入增加chunk_url字段，索引到相应内容后会返回相应内容中的图片。
  kimi:
    config:
      host: https://api.moonshot.cn
      appKey: sk-xxxxx
    completions:
      uri: https://api.moonshot.cn/v1/chat/completions
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      model: moonshot-v1-8k
      maxTokens: 2048
      temperature: 0.01
      topP: 1
      n: 1
      presencePenalty: 0.1
      frequencyPenalty: 0.16
      stop: null
      stream: false
  deepseek:
    config:
      host: https://api.deepseek.com
      appKey: sk-xxxxx
    completions:
      uri: https://api.deepseek.com/chat/completions
      model: deepseek-chat
      frequencyPenalty: 0.16
      maxTokens: 2048
      presencePenalty: 0.1
      stop: null
      stream: false
      temperature: 0.01
      topP: 1
      logprobs: false
      topLogprobs: 1
  wenxin:
    config:
      host: https://aip.baidubce.com
      appKey: xxxxx
      appSecret: xxxxx
    chat:
      uri: https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions
      temperature: 0.01 # 说明：  （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定;（2）默认0.8，范围 (0, 1.0]，不能为0
      top_p: 1  # （1）影响输出文本的多样性，取值越大，生成文本的多样性越强  （2）默认0.8，取值范围 [0, 1.0]
      penalty_score: 1  # 	通过对已生成的token增加惩罚，减少重复生成的现象。说明：  （1）值越大表示惩罚越大  （2）默认1.0，取值范围：[1.0, 2.0]
      stream: false  # 是否以流式接口的形式返回数据，默认false
      enable_system_memory: false  # 	是否开启系统记忆，说明：（1）false：未开启，默认false（2）true：表示开启，开启后，system_memory_id字段必填
      system_memory_id: 1 # 系统记忆ID，用于读取对应ID下的系统记忆，读取到的记忆文本内容会拼接message参与请求推理
      system: 你是一个智能问答助手 # 模型人设，主要用于人设设定，例如，你是xxx公司制作的AI助手，说明：（1）长度限制，message中的content总长度和system字段总内容不能超过20000个字符，且不能超过5120 tokens
      stop: null #       生成停止标识，当模型生成结果以stop中某个元素结尾时，停止文本生成。说明：  （1）每个元素长度不超过20字符  （2）最多4个元素
      disable_search: true # 是否强制关闭实时搜索功能，默认false，表示不关闭
      enable_citation: false # 是否开启上角标返回，说明：  （1）开启后，有概率触发搜索溯源信息search_info，search_info内容见响应参数介绍  （2）默认false，不开启
      enable_trace: false  # 是否返回搜索溯源信息，说明：  （1）如果开启，在触发了搜索增强的场景下，会返回搜索溯源信息search_info，search_info内容见响应参数介绍  （2）默认false，表示不开启
      max_output_tokens: 1024 #         指定模型最大输出token数，说明：  （1）如果设置此参数，范围[2, 2048]  （2）如果不设置此参数，最大输出token数为1024
      response_format: text #      指定响应内容的格式，说明：  （1）可选值：  · json_object：以json格式返回，可能出现不满足效果情况  · text：以文本格式返回  （2）如果不填写参数response_format值，默认为text
      userId: null # 表示最终用户的唯一标识符
  zhipu:
    config:
      host: https://open.bigmodel.cn
      appKey: xxxxx.xxxxx
    chat:
      uri: https://open.bigmodel.cn/api/paas/v4/chat/completions
      model: glm-4
      doSample: true
      stream: false
      temperature: 0.01
      topP: 0.7
      maxTokens: 1024
      stop: null
      toolChoice: null
  doubao:
    config:
      apiKey: xxxxx
      host: https://ark.cn-beijing.volces.com
      timeout: 10
      connectTimeout: 1
      region: cn-beijing
    completions:
      uri: https://ark.cn-beijing.volces.com/api/v3/chat/completions
      # 分别配置Doubao-pro-4k，Doubao-pro-32k，Doubao-pro-128k.....以此类推的模型配置，格式为模型id|最大字数，例如：ep-20241022153706-98jv2|4096
      model: ep-20241022153706-98jv2|4096
      temperature: 1
      topP: 0.7
      stop:
      maxTokens: 4096
      presencePenalty: 0
      frequencyPenalty: 0
      logprobs: false
      topLogprobs:
      repetitionPenalty:
      n:
      retryTimes: 2
      retryInterval: 3000
    image:
      uri: https://ark.cn-beijing.volces.com/api/v3/images/generations
      # 本次请求使用模型的 Model ID 或推理接入点 (Endpoint ID)，目前仅支持 doubao-seedream-3-0-t2i-250415。
      model: doubao-seedream-3-0-t2i-250415
      # 指定生成图像的返回格式。支持以下两种取值："url"：以可下载的 JPEG 图片链接形式返回；"b64_json"：以 Base64 编码字符串的 JSON 格式返回图像数据。
      response_format: url
      # 生成图像的宽高像素，要求介于 [512 x 512, 2048 x 2048] 之间。
      size: 1024x1024
      # 随机数种子，用于控制模型生成内容的随机性。取值范围为 [-1, 2147483647]。如果不提供，则算法自动生成一个随机数作为种子。如果希望生成内容保持一致，可以使用相同的 seed 参数值。
      seed: 12
      # 模型输出结果与prompt的一致程度，即生成图像的自由度；值越大，模型自由度越小，与用户输入的提示词相关性越强。取值范围：[1, 10] 之间的浮点数。
      guidance_scale: 2.5
      # 是否在生成的图片中添加水印。
      watermark: false
    video:
      uri: https://ark.cn-beijing.volces.com/api/v3/contents/generations/tasks
      model: doubao-seedance-1-0-pro-250528
      # 填写本次生成任务结果的回调通知地址。当视频生成任务有状态变化时，方舟将向此地址发送包含任务最新状态的回调请求。回调请求内容结构与查询任务API的返回体一致。回调返回的 status 包括以下状态：queued：排队中。 running：任务运行中。 succeeded： 任务成功。（如发送失败，即5秒内没有接收到成功发送的信息，回调三次） failed：任务失败。（如发送失败，即5秒内没有接收到成功发送的信息，回调三次） 
      callback_url:
      # 视频分辨率，枚举值：480p 720p 1080p
      resolution: 720p
      # 生成视频的宽高比例，支持设置 480p、720p、1080p 三种分辨率下的宽高比。21:9  16:9  4:3 1:1 3:4 9:16 9:21 keep_ratio：所生成视频的宽高比与所上传图片的宽高比保持一致。 adaptive：根据所上传图片的比例，自动选择最合适的宽高比。不同模型默认值不同，一般是16:9wan2.1-14b-i2v，默认值： keep_ratiodoubao-seedance-1-0-pro 图生视频，doubao-seedance-1-0-lite-i2v，doubao-seaweed 图生视频，默认值：adaptiv
      ratio: 16:9
      # 生成视频时长，单位：秒。枚举值：5 10
      duration: 5
      # 帧率，即一秒时间内视频画面数量。枚举值： 16 24 wan2.1-14b 默认值 16doubao-seaweed 默认值 2
      framepersecond: 16
      # 生成视频是否包含水印。枚举值： false：不含水印。 true：含有水印。
      watermark: false
      # 种子整数，用于控制生成内容的随机性。取值范围：[-1, 2^32-1]之间的整数。注意当不指定seed值或令seed取值为-1时，会使用随机数替代。 改变seed值，是相同的请求获得不同结果的一种方法。对相同的请求使用相同的seed值会产生类似的结果，但不保证完全一致。
      seed: -1
      # 是否固定摄像头。枚举值：true：固定摄像头。平台会在用户提示词中追加固定摄像头，实际效果不保证。 false：不固定摄像头。
      camerafixed: false
  # 硅基流动
  siliconflow:
    config:
      host: https://api.siliconflow.cn
      # 陈志伟的key
      appKey: sk-xxxxx
    completions:
      uri: https://api.siliconflow.cn/v1/chat/completions
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      # 各大厂商的模型版本号
      model: Qwen/QVQ-72B-Preview
      maxTokens: 2048
      stop: null
      temperature: 0.01
      topP: 0.7
      topK: 50
      frequencyPenalty: 0.5
      n: 1
      responseFormat: text
    chart: # 【YAYI-CHART】
      uri: https://yayi.wenge.com/saas-gateway/a0c8a766d91376ed3f8136194ac52074/generate
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      mode: yayi
      stream: false
      top_p: 0.3
      temperature: 0.5
      max_new_tokens: 2048
      do_sample: true
    mathmodel: # 【YAYI-数学模型】
      uri: https://yayi.wenge.com/saas-gateway/6aa30e1a148c9f0926406ec4e87a994f/generate
      # 默认重试次数
      retry_times: 10
      # 默认重试间隔时间
      retry_interval: 3000
      max_try_steps: 1
      presence_penalty: 0.85
      frequency_penalty: 0.16
      stream: false
      top_p: 0.3
      top_n: 1
      n: 1
      temperature: 0.5
      max_new_tokens: 2048
      do_sample: true
  # 火山引擎
  volcengine:
    config:
      host: https://api-knowledgebase.mlp.cn-beijing.volces.com
      appKey: xxxxx
      appSecret: xxxxx
      timeout: 10
      connectTimeout: 1
      logEnabled: false
      region: cn-north-1
      serve: air
    rearrange:
      uri: https://api-knowledgebase.mlp.cn-beijing.volces.com/api/knowledge/service/rerank
      # base-multilingual-rerank：速度快、长文本、支持70+种语言（推荐）, m3-v2-rerank（默认）：常规文本、支持100+种语言
      rerankModel: base-multilingual-rerank
  minimax:
    config:
      apiKey: xxxxx
      host: https://api.minimaxi.com/
      logEnabled: false
    completions:
      uri: https://api.minimaxi.com/v1/text/chatcompletion_v2
      # 调用的模型ID。目前支持取以下值：MiniMax-M1MiniMax-Text-01。注：MiniMax-M1为推理模型，输出tokens较多，调用时建议使用流式输出以获得更稳定的网络连接。
      model: MiniMax-M1
      max_tokens: 4096
      temperature: 0.1
      top_p: 0.95
      mask_sensitive_info: false
    image:
      uri: https://api.minimaxi.com/v1/image_generation
      model: image-01
      aspect_ratio: "1:1"
      width: 512
      height: 512
      response_format: url
      seed: 1
      n: 1
      prompt_optimizer: false
    video:
      uri: https://api.minimaxi.com/v1/video_generation
      model: MiniMax-Hailuo-02
      prompt_optimizer: true
      duration: 6
      resolution: 1080P
      callback_url:
      searchTaskUri: https://api.minimaxi.com/v1/query/video_generation
      downloadFileUri: https://api.minimaxi.com/v1/files/retrieve
    music:
      uri: https://api.minimaxi.com/v1/music_generation
      model: music-1.5
      refer_voice:
      refer_instrumental:
      refer_vocal:

```

## 依赖
```xml

<dependencies>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>

    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>

    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
    </dependency>

</dependencies>

```