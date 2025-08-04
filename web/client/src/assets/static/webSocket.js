class heartCheck {
  constructor(param, url) {
    this.param = param
    this.url = url

    this.webSocket = null
    this.isConnect = false //连接状态
    this.reConnectNum = 0 //重连次数
    this.globalCallback = function (e) {
    } //定义外部接收数据的回调函数
    this.heart = 'channelCheck' //心跳包
    this.timeout = 50 * 1000 //每段时间发送一次心跳包 这里设置为60s
    this.heartbeat = null //延时发送消息对象(启动心跳新建这个对象，收到消息后重置对象)
    this.start = function () {
      this.heartbeat = setInterval(() => {
        if (this.isConnect) {
          // console.log("发送心跳")
          this.webSocketSend(this.heart)
        } else {
          this.clear()
        }
      }, this.timeout)
    }
    this.reset = function () {
      clearInterval(this.heartbeat)
      this.start()
    }
    this.clear = function () {
      clearInterval(this.heartbeat)
    }
  }

  //初始化websocket
  initWebSocket(callback) {
    let that = this;
    var websocketUrl = this.url
    //var websocketUrl = this.url + `${this.param.channelType}/${this.param.shareCode}`
    // var websocketUrl = this.url + `${this.param.channelType}`
    //此callback为在其他地方调用时定义的接收socket数据的函数
    if (callback) {
      if (typeof callback == 'function') {
        this.globalCallback = callback
      } else {
        throw new Error('callback is not a function')
      }
    }
    if ('WebSocket' in window) {
      this.webSocket = new WebSocket(websocketUrl)
    } else {
   
      alert('该浏览器不支持websocket!')
      return
    }
    //打开
    this.webSocket.onopen = function (e) {
      that.webSocketOpen(e)
    }
    //收信
    this.webSocket.onmessage = function (e) {
      that.webSocketOnMessage(e)
    }
    //关闭
    this.webSocket.onclose = function (e) {
      that.webSocketOnClose(e)
    }
    //连接发生错误的回调方法
    this.webSocket.onerror = function (e) {
      that.webSocketonError(e)
    }
  }

  //连接socket建立时触发
  webSocketOpen(e) {
    // console.log('WebSocket连接成功')
    localStorage.setItem('wsState',200)
    //首次握手
    // webSocketSend(heartCheck.heart)
    this.isConnect = true
    this.start()
    this.reConnectNum = 0
  }

  //客户端接收服务端数据时触发,e为接受的数据对象
  webSocketOnMessage(e) {
    const data = JSON.parse(e.data) //根据自己的需要对接收到的数据进行格式化
    this.globalCallback(data) //将data传给在外定义的接收数据的函数，至关重要。
  }

  //socket关闭时触发
  webSocketOnClose(e) {
    this.clear()
    this.isConnect = false //断开后修改标识
    // console.log('webSocket已经关闭 (code：' + e.code + ')')
    //被动断开，重新连接
    localStorage.setItem('wsState',e.code)
    if (e.code == 1006) {
      if (this.reConnectNum < 60) {
       // this.initWebSocket() 断开自动连接
        ++this.reConnectNum
      } else {
       alert("websocket连接不上，请刷新页面或联系开发人员!")
      }
    }
  }

  //连接发生错误的回调方法
  webSocketonError(e) {
    this.clear()
    this.isConnect = false //断开后修改标识
    console.log('WebSocket连接发生错误:')
  }

  //发送数据
  webSocketSend(data) {
    this.webSocket.send(data) //在这里根据自己的需要转换数据格式
  }
  //在其他需要socket地方主动关闭socket
  closeWebSocket(e) {
    if (this.isConnect) {
      this.webSocket.close()
      this.clear()
      this.isConnect = false
      this.reConnectNum = 0
    }
  }
  //在其他需要socket地方接受数据
  getSock(callback) {
    this.globalCallback = callback
  }
  //在其他需要socket地方调用的函数，用来发送数据及接受数据
  sendSock(agentData) {
    // console.log(agentData, this.webSocket)
    //下面的判断主要是考虑到socket连接可能中断或者其他的因素，可以重新发送此条消息。
    switch (this.webSocket.readyState) {
      //CONNECTING：值为0，表示正在连接。
      case this.webSocket.CONNECTING:
        setTimeout(function () {
          this.sendSock(agentData, callback)
        }, 1000)
        break
      //OPEN：值为1，表示连接成功，可以通信了。
      case this.webSocket.OPEN:
        this.webSocketSend(agentData)
        break
      //CLOSING：值为2，表示连接正在关闭。
      case this.webSocket.CLOSING:
        setTimeout(function () {
          this.sendSock(agentData, callback)
        }, 1000)
        break
      //CLOSED：值为3，表示连接已经关闭，或者打开连接失败。
      case this.webSocket.CLOSED:
        // do something
        break
      default:
        // this never happens
        break
    }
  }
}
