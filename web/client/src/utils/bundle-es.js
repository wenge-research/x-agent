function ownKeys(e, r) {
  var t = Object.keys(e);
  if (Object.getOwnPropertySymbols) {
    var o = Object.getOwnPropertySymbols(e);
    r && (o = o.filter(function (r) {
      return Object.getOwnPropertyDescriptor(e, r).enumerable;
    })), t.push.apply(t, o);
  }
  return t;
}
function _objectSpread2(e) {
  for (var r = 1; r < arguments.length; r++) {
    var t = null != arguments[r] ? arguments[r] : {};
    r % 2 ? ownKeys(Object(t), !0).forEach(function (r) {
      _defineProperty(e, r, t[r]);
    }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(t)) : ownKeys(Object(t)).forEach(function (r) {
      Object.defineProperty(e, r, Object.getOwnPropertyDescriptor(t, r));
    });
  }
  return e;
}
function _regeneratorRuntime() {
  _regeneratorRuntime = function () {
    return e;
  };
  var t,
    e = {},
    r = Object.prototype,
    n = r.hasOwnProperty,
    o = Object.defineProperty || function (t, e, r) {
      t[e] = r.value;
    },
    i = "function" == typeof Symbol ? Symbol : {},
    a = i.iterator || "@@iterator",
    c = i.asyncIterator || "@@asyncIterator",
    u = i.toStringTag || "@@toStringTag";
  function define(t, e, r) {
    return Object.defineProperty(t, e, {
      value: r,
      enumerable: !0,
      configurable: !0,
      writable: !0
    }), t[e];
  }
  try {
    define({}, "");
  } catch (t) {
    define = function (t, e, r) {
      return t[e] = r;
    };
  }
  function wrap(t, e, r, n) {
    var i = e && e.prototype instanceof Generator ? e : Generator,
      a = Object.create(i.prototype),
      c = new Context(n || []);
    return o(a, "_invoke", {
      value: makeInvokeMethod(t, r, c)
    }), a;
  }
  function tryCatch(t, e, r) {
    try {
      return {
        type: "normal",
        arg: t.call(e, r)
      };
    } catch (t) {
      return {
        type: "throw",
        arg: t
      };
    }
  }
  e.wrap = wrap;
  var h = "suspendedStart",
    l = "suspendedYield",
    f = "executing",
    s = "completed",
    y = {};
  function Generator() {}
  function GeneratorFunction() {}
  function GeneratorFunctionPrototype() {}
  var p = {};
  define(p, a, function () {
    return this;
  });
  var d = Object.getPrototypeOf,
    v = d && d(d(values([])));
  v && v !== r && n.call(v, a) && (p = v);
  var g = GeneratorFunctionPrototype.prototype = Generator.prototype = Object.create(p);
  function defineIteratorMethods(t) {
    ["next", "throw", "return"].forEach(function (e) {
      define(t, e, function (t) {
        return this._invoke(e, t);
      });
    });
  }
  function AsyncIterator(t, e) {
    function invoke(r, o, i, a) {
      var c = tryCatch(t[r], t, o);
      if ("throw" !== c.type) {
        var u = c.arg,
          h = u.value;
        return h && "object" == typeof h && n.call(h, "__await") ? e.resolve(h.__await).then(function (t) {
          invoke("next", t, i, a);
        }, function (t) {
          invoke("throw", t, i, a);
        }) : e.resolve(h).then(function (t) {
          u.value = t, i(u);
        }, function (t) {
          return invoke("throw", t, i, a);
        });
      }
      a(c.arg);
    }
    var r;
    o(this, "_invoke", {
      value: function (t, n) {
        function callInvokeWithMethodAndArg() {
          return new e(function (e, r) {
            invoke(t, n, e, r);
          });
        }
        return r = r ? r.then(callInvokeWithMethodAndArg, callInvokeWithMethodAndArg) : callInvokeWithMethodAndArg();
      }
    });
  }
  function makeInvokeMethod(e, r, n) {
    var o = h;
    return function (i, a) {
      if (o === f) throw new Error("Generator is already running");
      if (o === s) {
        if ("throw" === i) throw a;
        return {
          value: t,
          done: !0
        };
      }
      for (n.method = i, n.arg = a;;) {
        var c = n.delegate;
        if (c) {
          var u = maybeInvokeDelegate(c, n);
          if (u) {
            if (u === y) continue;
            return u;
          }
        }
        if ("next" === n.method) n.sent = n._sent = n.arg;else if ("throw" === n.method) {
          if (o === h) throw o = s, n.arg;
          n.dispatchException(n.arg);
        } else "return" === n.method && n.abrupt("return", n.arg);
        o = f;
        var p = tryCatch(e, r, n);
        if ("normal" === p.type) {
          if (o = n.done ? s : l, p.arg === y) continue;
          return {
            value: p.arg,
            done: n.done
          };
        }
        "throw" === p.type && (o = s, n.method = "throw", n.arg = p.arg);
      }
    };
  }
  function maybeInvokeDelegate(e, r) {
    var n = r.method,
      o = e.iterator[n];
    if (o === t) return r.delegate = null, "throw" === n && e.iterator.return && (r.method = "return", r.arg = t, maybeInvokeDelegate(e, r), "throw" === r.method) || "return" !== n && (r.method = "throw", r.arg = new TypeError("The iterator does not provide a '" + n + "' method")), y;
    var i = tryCatch(o, e.iterator, r.arg);
    if ("throw" === i.type) return r.method = "throw", r.arg = i.arg, r.delegate = null, y;
    var a = i.arg;
    return a ? a.done ? (r[e.resultName] = a.value, r.next = e.nextLoc, "return" !== r.method && (r.method = "next", r.arg = t), r.delegate = null, y) : a : (r.method = "throw", r.arg = new TypeError("iterator result is not an object"), r.delegate = null, y);
  }
  function pushTryEntry(t) {
    var e = {
      tryLoc: t[0]
    };
    1 in t && (e.catchLoc = t[1]), 2 in t && (e.finallyLoc = t[2], e.afterLoc = t[3]), this.tryEntries.push(e);
  }
  function resetTryEntry(t) {
    var e = t.completion || {};
    e.type = "normal", delete e.arg, t.completion = e;
  }
  function Context(t) {
    this.tryEntries = [{
      tryLoc: "root"
    }], t.forEach(pushTryEntry, this), this.reset(!0);
  }
  function values(e) {
    if (e || "" === e) {
      var r = e[a];
      if (r) return r.call(e);
      if ("function" == typeof e.next) return e;
      if (!isNaN(e.length)) {
        var o = -1,
          i = function next() {
            for (; ++o < e.length;) if (n.call(e, o)) return next.value = e[o], next.done = !1, next;
            return next.value = t, next.done = !0, next;
          };
        return i.next = i;
      }
    }
    throw new TypeError(typeof e + " is not iterable");
  }
  return GeneratorFunction.prototype = GeneratorFunctionPrototype, o(g, "constructor", {
    value: GeneratorFunctionPrototype,
    configurable: !0
  }), o(GeneratorFunctionPrototype, "constructor", {
    value: GeneratorFunction,
    configurable: !0
  }), GeneratorFunction.displayName = define(GeneratorFunctionPrototype, u, "GeneratorFunction"), e.isGeneratorFunction = function (t) {
    var e = "function" == typeof t && t.constructor;
    return !!e && (e === GeneratorFunction || "GeneratorFunction" === (e.displayName || e.name));
  }, e.mark = function (t) {
    return Object.setPrototypeOf ? Object.setPrototypeOf(t, GeneratorFunctionPrototype) : (t.__proto__ = GeneratorFunctionPrototype, define(t, u, "GeneratorFunction")), t.prototype = Object.create(g), t;
  }, e.awrap = function (t) {
    return {
      __await: t
    };
  }, defineIteratorMethods(AsyncIterator.prototype), define(AsyncIterator.prototype, c, function () {
    return this;
  }), e.AsyncIterator = AsyncIterator, e.async = function (t, r, n, o, i) {
    void 0 === i && (i = Promise);
    var a = new AsyncIterator(wrap(t, r, n, o), i);
    return e.isGeneratorFunction(r) ? a : a.next().then(function (t) {
      return t.done ? t.value : a.next();
    });
  }, defineIteratorMethods(g), define(g, u, "Generator"), define(g, a, function () {
    return this;
  }), define(g, "toString", function () {
    return "[object Generator]";
  }), e.keys = function (t) {
    var e = Object(t),
      r = [];
    for (var n in e) r.push(n);
    return r.reverse(), function next() {
      for (; r.length;) {
        var t = r.pop();
        if (t in e) return next.value = t, next.done = !1, next;
      }
      return next.done = !0, next;
    };
  }, e.values = values, Context.prototype = {
    constructor: Context,
    reset: function (e) {
      if (this.prev = 0, this.next = 0, this.sent = this._sent = t, this.done = !1, this.delegate = null, this.method = "next", this.arg = t, this.tryEntries.forEach(resetTryEntry), !e) for (var r in this) "t" === r.charAt(0) && n.call(this, r) && !isNaN(+r.slice(1)) && (this[r] = t);
    },
    stop: function () {
      this.done = !0;
      var t = this.tryEntries[0].completion;
      if ("throw" === t.type) throw t.arg;
      return this.rval;
    },
    dispatchException: function (e) {
      if (this.done) throw e;
      var r = this;
      function handle(n, o) {
        return a.type = "throw", a.arg = e, r.next = n, o && (r.method = "next", r.arg = t), !!o;
      }
      for (var o = this.tryEntries.length - 1; o >= 0; --o) {
        var i = this.tryEntries[o],
          a = i.completion;
        if ("root" === i.tryLoc) return handle("end");
        if (i.tryLoc <= this.prev) {
          var c = n.call(i, "catchLoc"),
            u = n.call(i, "finallyLoc");
          if (c && u) {
            if (this.prev < i.catchLoc) return handle(i.catchLoc, !0);
            if (this.prev < i.finallyLoc) return handle(i.finallyLoc);
          } else if (c) {
            if (this.prev < i.catchLoc) return handle(i.catchLoc, !0);
          } else {
            if (!u) throw new Error("try statement without catch or finally");
            if (this.prev < i.finallyLoc) return handle(i.finallyLoc);
          }
        }
      }
    },
    abrupt: function (t, e) {
      for (var r = this.tryEntries.length - 1; r >= 0; --r) {
        var o = this.tryEntries[r];
        if (o.tryLoc <= this.prev && n.call(o, "finallyLoc") && this.prev < o.finallyLoc) {
          var i = o;
          break;
        }
      }
      i && ("break" === t || "continue" === t) && i.tryLoc <= e && e <= i.finallyLoc && (i = null);
      var a = i ? i.completion : {};
      return a.type = t, a.arg = e, i ? (this.method = "next", this.next = i.finallyLoc, y) : this.complete(a);
    },
    complete: function (t, e) {
      if ("throw" === t.type) throw t.arg;
      return "break" === t.type || "continue" === t.type ? this.next = t.arg : "return" === t.type ? (this.rval = this.arg = t.arg, this.method = "return", this.next = "end") : "normal" === t.type && e && (this.next = e), y;
    },
    finish: function (t) {
      for (var e = this.tryEntries.length - 1; e >= 0; --e) {
        var r = this.tryEntries[e];
        if (r.finallyLoc === t) return this.complete(r.completion, r.afterLoc), resetTryEntry(r), y;
      }
    },
    catch: function (t) {
      for (var e = this.tryEntries.length - 1; e >= 0; --e) {
        var r = this.tryEntries[e];
        if (r.tryLoc === t) {
          var n = r.completion;
          if ("throw" === n.type) {
            var o = n.arg;
            resetTryEntry(r);
          }
          return o;
        }
      }
      throw new Error("illegal catch attempt");
    },
    delegateYield: function (e, r, n) {
      return this.delegate = {
        iterator: values(e),
        resultName: r,
        nextLoc: n
      }, "next" === this.method && (this.arg = t), y;
    }
  }, e;
}
function asyncGeneratorStep(gen, resolve, reject, _next, _throw, key, arg) {
  try {
    var info = gen[key](arg);
    var value = info.value;
  } catch (error) {
    reject(error);
    return;
  }
  if (info.done) {
    resolve(value);
  } else {
    Promise.resolve(value).then(_next, _throw);
  }
}
function _asyncToGenerator(fn) {
  return function () {
    var self = this,
      args = arguments;
    return new Promise(function (resolve, reject) {
      var gen = fn.apply(self, args);
      function _next(value) {
        asyncGeneratorStep(gen, resolve, reject, _next, _throw, "next", value);
      }
      function _throw(err) {
        asyncGeneratorStep(gen, resolve, reject, _next, _throw, "throw", err);
      }
      _next(undefined);
    });
  };
}
function _classCallCheck(instance, Constructor) {
  if (!(instance instanceof Constructor)) {
    throw new TypeError("Cannot call a class as a function");
  }
}
function _defineProperties(target, props) {
  for (var i = 0; i < props.length; i++) {
    var descriptor = props[i];
    descriptor.enumerable = descriptor.enumerable || false;
    descriptor.configurable = true;
    if ("value" in descriptor) descriptor.writable = true;
    Object.defineProperty(target, _toPropertyKey(descriptor.key), descriptor);
  }
}
function _createClass(Constructor, protoProps, staticProps) {
  if (protoProps) _defineProperties(Constructor.prototype, protoProps);
  if (staticProps) _defineProperties(Constructor, staticProps);
  Object.defineProperty(Constructor, "prototype", {
    writable: false
  });
  return Constructor;
}
function _defineProperty(obj, key, value) {
  key = _toPropertyKey(key);
  if (key in obj) {
    Object.defineProperty(obj, key, {
      value: value,
      enumerable: true,
      configurable: true,
      writable: true
    });
  } else {
    obj[key] = value;
  }
  return obj;
}
function _inherits(subClass, superClass) {
  if (typeof superClass !== "function" && superClass !== null) {
    throw new TypeError("Super expression must either be null or a function");
  }
  subClass.prototype = Object.create(superClass && superClass.prototype, {
    constructor: {
      value: subClass,
      writable: true,
      configurable: true
    }
  });
  Object.defineProperty(subClass, "prototype", {
    writable: false
  });
  if (superClass) _setPrototypeOf(subClass, superClass);
}
function _getPrototypeOf(o) {
  _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf.bind() : function _getPrototypeOf(o) {
    return o.__proto__ || Object.getPrototypeOf(o);
  };
  return _getPrototypeOf(o);
}
function _setPrototypeOf(o, p) {
  _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function _setPrototypeOf(o, p) {
    o.__proto__ = p;
    return o;
  };
  return _setPrototypeOf(o, p);
}
function _isNativeReflectConstruct() {
  if (typeof Reflect === "undefined" || !Reflect.construct) return false;
  if (Reflect.construct.sham) return false;
  if (typeof Proxy === "function") return true;
  try {
    Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {}));
    return true;
  } catch (e) {
    return false;
  }
}
function _construct(Parent, args, Class) {
  if (_isNativeReflectConstruct()) {
    _construct = Reflect.construct.bind();
  } else {
    _construct = function _construct(Parent, args, Class) {
      var a = [null];
      a.push.apply(a, args);
      var Constructor = Function.bind.apply(Parent, a);
      var instance = new Constructor();
      if (Class) _setPrototypeOf(instance, Class.prototype);
      return instance;
    };
  }
  return _construct.apply(null, arguments);
}
function _isNativeFunction(fn) {
  try {
    return Function.toString.call(fn).indexOf("[native code]") !== -1;
  } catch (e) {
    return typeof fn === "function";
  }
}
function _wrapNativeSuper(Class) {
  var _cache = typeof Map === "function" ? new Map() : undefined;
  _wrapNativeSuper = function _wrapNativeSuper(Class) {
    if (Class === null || !_isNativeFunction(Class)) return Class;
    if (typeof Class !== "function") {
      throw new TypeError("Super expression must either be null or a function");
    }
    if (typeof _cache !== "undefined") {
      if (_cache.has(Class)) return _cache.get(Class);
      _cache.set(Class, Wrapper);
    }
    function Wrapper() {
      return _construct(Class, arguments, _getPrototypeOf(this).constructor);
    }
    Wrapper.prototype = Object.create(Class.prototype, {
      constructor: {
        value: Wrapper,
        enumerable: false,
        writable: true,
        configurable: true
      }
    });
    return _setPrototypeOf(Wrapper, Class);
  };
  return _wrapNativeSuper(Class);
}
function _objectWithoutPropertiesLoose(source, excluded) {
  if (source == null) return {};
  var target = {};
  var sourceKeys = Object.keys(source);
  var key, i;
  for (i = 0; i < sourceKeys.length; i++) {
    key = sourceKeys[i];
    if (excluded.indexOf(key) >= 0) continue;
    target[key] = source[key];
  }
  return target;
}
function _objectWithoutProperties(source, excluded) {
  if (source == null) return {};
  var target = _objectWithoutPropertiesLoose(source, excluded);
  var key, i;
  if (Object.getOwnPropertySymbols) {
    var sourceSymbolKeys = Object.getOwnPropertySymbols(source);
    for (i = 0; i < sourceSymbolKeys.length; i++) {
      key = sourceSymbolKeys[i];
      if (excluded.indexOf(key) >= 0) continue;
      if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue;
      target[key] = source[key];
    }
  }
  return target;
}
function _assertThisInitialized(self) {
  if (self === void 0) {
    throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
  }
  return self;
}
function _possibleConstructorReturn(self, call) {
  if (call && (typeof call === "object" || typeof call === "function")) {
    return call;
  } else if (call !== void 0) {
    throw new TypeError("Derived constructors may only return object or undefined");
  }
  return _assertThisInitialized(self);
}
function _createSuper(Derived) {
  var hasNativeReflectConstruct = _isNativeReflectConstruct();
  return function _createSuperInternal() {
    var Super = _getPrototypeOf(Derived),
      result;
    if (hasNativeReflectConstruct) {
      var NewTarget = _getPrototypeOf(this).constructor;
      result = Reflect.construct(Super, arguments, NewTarget);
    } else {
      result = Super.apply(this, arguments);
    }
    return _possibleConstructorReturn(this, result);
  };
}
function _toPrimitive(input, hint) {
  if (typeof input !== "object" || input === null) return input;
  var prim = input[Symbol.toPrimitive];
  if (prim !== undefined) {
    var res = prim.call(input, hint || "default");
    if (typeof res !== "object") return res;
    throw new TypeError("@@toPrimitive must return a primitive value.");
  }
  return (hint === "string" ? String : Number)(input);
}
function _toPropertyKey(arg) {
  var key = _toPrimitive(arg, "string");
  return typeof key === "symbol" ? key : String(key);
}

async function getBytes(stream, onChunk) {
    const reader = stream.getReader();
    let result;
    while (!(result = await reader.read()).done) {
        onChunk(result.value);
    }
}
function getLines(onLine) {
    let buffer;
    let position;
    let fieldLength;
    let discardTrailingNewline = false;
    return function onChunk(arr) {
        if (buffer === undefined) {
            buffer = arr;
            position = 0;
            fieldLength = -1;
        }
        else {
            buffer = concat(buffer, arr);
        }
        const bufLength = buffer.length;
        let lineStart = 0;
        while (position < bufLength) {
            if (discardTrailingNewline) {
                if (buffer[position] === 10) {
                    lineStart = ++position;
                }
                discardTrailingNewline = false;
            }
            let lineEnd = -1;
            for (; position < bufLength && lineEnd === -1; ++position) {
                switch (buffer[position]) {
                    case 58:
                        if (fieldLength === -1) {
                            fieldLength = position - lineStart;
                        }
                        break;
                    case 13:
                        discardTrailingNewline = true;
                    case 10:
                        lineEnd = position;
                        break;
                }
            }
            if (lineEnd === -1) {
                break;
            }
            onLine(buffer.subarray(lineStart, lineEnd), fieldLength);
            lineStart = position;
            fieldLength = -1;
        }
        if (lineStart === bufLength) {
            buffer = undefined;
        }
        else if (lineStart !== 0) {
            buffer = buffer.subarray(lineStart);
            position -= lineStart;
        }
    };
}
function getMessages(onId, onRetry, onMessage) {
    let message = newMessage();
    const decoder = new TextDecoder();
    return function onLine(line, fieldLength) {
        if (line.length === 0) {
            onMessage === null || onMessage === void 0 ? void 0 : onMessage(message);
            message = newMessage();
        }
        else if (fieldLength > 0) {
            const field = decoder.decode(line.subarray(0, fieldLength));
            const valueOffset = fieldLength + (line[fieldLength + 1] === 32 ? 2 : 1);
            const value = decoder.decode(line.subarray(valueOffset));
            switch (field) {
                case 'data':
                    message.data = message.data
                        ? message.data + '\n' + value
                        : value;
                    break;
                case 'event':
                    message.event = value;
                    break;
                case 'id':
                    onId(message.id = value);
                    break;
                case 'retry':
                    const retry = parseInt(value, 10);
                    if (!isNaN(retry)) {
                        onRetry(message.retry = retry);
                    }
                    break;
            }
        }
    };
}
function concat(a, b) {
    const res = new Uint8Array(a.length + b.length);
    res.set(a);
    res.set(b, a.length);
    return res;
}
function newMessage() {
    return {
        data: '',
        event: '',
        id: '',
        retry: undefined,
    };
}

var __rest = (undefined && undefined.__rest) || function (s, e) {
    var t = {};
    for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p) && e.indexOf(p) < 0)
        t[p] = s[p];
    if (s != null && typeof Object.getOwnPropertySymbols === "function")
        for (var i = 0, p = Object.getOwnPropertySymbols(s); i < p.length; i++) {
            if (e.indexOf(p[i]) < 0 && Object.prototype.propertyIsEnumerable.call(s, p[i]))
                t[p[i]] = s[p[i]];
        }
    return t;
};
const EventStreamContentType = 'text/event-stream';
const DefaultRetryInterval = 1000;
const LastEventId = 'last-event-id';
function fetchEventSource(input, _a) {
    var { signal: inputSignal, headers: inputHeaders, onopen: inputOnOpen, onmessage, onclose, onerror, openWhenHidden, fetch: inputFetch } = _a, rest = __rest(_a, ["signal", "headers", "onopen", "onmessage", "onclose", "onerror", "openWhenHidden", "fetch"]);
    return new Promise((resolve, reject) => {
        const headers = Object.assign({}, inputHeaders);
        if (!headers.accept) {
            headers.accept = EventStreamContentType;
        }
        let curRequestController;
        function onVisibilityChange() {
            curRequestController.abort();
            if (!document.hidden) {
                create();
            }
        }
        if (!openWhenHidden) {
            document.addEventListener('visibilitychange', onVisibilityChange);
        }
        let retryInterval = DefaultRetryInterval;
        let retryTimer = 0;
        function dispose() {
            document.removeEventListener('visibilitychange', onVisibilityChange);
            window.clearTimeout(retryTimer);
            curRequestController.abort();
        }
        inputSignal === null || inputSignal === void 0 ? void 0 : inputSignal.addEventListener('abort', () => {
            dispose();
            resolve();
        });
        const fetch = inputFetch !== null && inputFetch !== void 0 ? inputFetch : window.fetch;
        const onopen = inputOnOpen !== null && inputOnOpen !== void 0 ? inputOnOpen : defaultOnOpen;
        async function create() {
            var _a;
            curRequestController = new AbortController();
            try {
                const response = await fetch(input, Object.assign(Object.assign({}, rest), { headers, signal: curRequestController.signal }));
                await onopen(response);
                await getBytes(response.body, getLines(getMessages(id => {
                    if (id) {
                        headers[LastEventId] = id;
                    }
                    else {
                        delete headers[LastEventId];
                    }
                }, retry => {
                    retryInterval = retry;
                }, onmessage)));
                onclose === null || onclose === void 0 ? void 0 : onclose();
                dispose();
                resolve();
            }
            catch (err) {
                if (!curRequestController.signal.aborted) {
                    try {
                        const interval = (_a = onerror === null || onerror === void 0 ? void 0 : onerror(err)) !== null && _a !== void 0 ? _a : retryInterval;
                        window.clearTimeout(retryTimer);
                        retryTimer = window.setTimeout(create, interval);
                    }
                    catch (innerErr) {
                        dispose();
                        reject(innerErr);
                    }
                }
            }
        }
        create();
    });
}
function defaultOnOpen(response) {
    const contentType = response.headers.get('content-type');
    if (!(contentType === null || contentType === void 0 ? void 0 : contentType.startsWith(EventStreamContentType))) {
        throw new Error(`Expected content-type to be ${EventStreamContentType}, Actual: ${contentType}`);
    }
}

var _excluded$1 = ["data", "onMessage", "onError", "onCancel"],
  _excluded2$1 = ["data", "onMessage", "onError", "onCancel"],
  _excluded3$1 = ["data", "onMessage", "onEnd", "onError", "onCancel"],
  _excluded4$1 = ["data", "onMessage", "onError", "onCancel"];
var FatalError = /*#__PURE__*/function (_Error) {
  _inherits(FatalError, _Error);
  var _super = _createSuper(FatalError);
  function FatalError() {
    _classCallCheck(this, FatalError);
    return _super.apply(this, arguments);
  }
  return _createClass(FatalError);
}( /*#__PURE__*/_wrapNativeSuper(Error));
function chatStreamApi(url, customOptions) {
  // 解构请求参数
  var data = customOptions.data,
    onMessage = customOptions.onMessage,
    onError = customOptions.onError,
    onCancel = customOptions.onCancel,
    options = _objectWithoutProperties(customOptions, _excluded$1);
  var abortController = new AbortController();
  fetchEventSource(url, _objectSpread2(_objectSpread2({}, options), {}, {
    headers: {
      'accept': 'text/event-stream',
      'content-type': 'application/json; charset=UTF-8',
      'timeout': "".concat(10 * 60 * 1000)
    },
    method: 'POST',
    body: data ? JSON.stringify(data) : null,
    signal: abortController.signal,
    openWhenHidden: true,
    onmessage: function onmessage(event) {
      onMessage && onMessage(event);
    },
    onerror: function onerror(err) {
      onError && onError(err);
      abortController.abort();
      if (err instanceof FatalError) {
        throw err;
      } else {
        throw err;
      }
    }
  }));
  onCancel && onCancel(abortController);
}
var chatSync = /*#__PURE__*/function () {
  var _ref = _asyncToGenerator( /*#__PURE__*/_regeneratorRuntime().mark(function _callee(url, customOptions) {
    var data, onMessage, onError, onCancel, options, abortController;
    return _regeneratorRuntime().wrap(function _callee$(_context) {
      while (1) switch (_context.prev = _context.next) {
        case 0:
          data = customOptions.data, onMessage = customOptions.onMessage, onError = customOptions.onError, onCancel = customOptions.onCancel, options = _objectWithoutProperties(customOptions, _excluded2$1);
          abortController = new AbortController();
          return _context.abrupt("return", new Promise(function (r, j) {
            fetch(url, _objectSpread2({
              method: 'post',
              headers: {
                'Content-Type': 'application/json; charset=UTF-8'
              },
              responseType: 'json',
              body: JSON.stringify(data)
            }, options), {
              signal: abortController.signal
            }).then(function (res) {
              return res.json();
            }).then(function (res) {
              onMessage && onMessage(res);
              r(res);
            })["catch"](function (err) {
              onError && onError(err);
              j(err);
            });
            onCancel && onCancel(abortController);
          }));
        case 3:
        case "end":
          return _context.stop();
      }
    }, _callee);
  }));
  return function chatSync(_x, _x2) {
    return _ref.apply(this, arguments);
  };
}();
var ttsStream = /*#__PURE__*/function () {
  var _ref2 = _asyncToGenerator( /*#__PURE__*/_regeneratorRuntime().mark(function _callee3(url, customOptions) {
    var data, onMessage, onEnd, onError, onCancel, options, abortController;
    return _regeneratorRuntime().wrap(function _callee3$(_context3) {
      while (1) switch (_context3.prev = _context3.next) {
        case 0:
          data = customOptions.data, onMessage = customOptions.onMessage, onEnd = customOptions.onEnd, onError = customOptions.onError, onCancel = customOptions.onCancel, options = _objectWithoutProperties(customOptions, _excluded3$1);
          abortController = new AbortController();
          return _context3.abrupt("return", new Promise(function (r, j) {
            fetch(url, _objectSpread2({
              method: 'post',
              headers: {
                'Content-Type': 'application/json; charset=UTF-8'
              },
              body: JSON.stringify(data)
            }, options), {
              signal: abortController.signal
            }).then( /*#__PURE__*/function () {
              var _ref3 = _asyncToGenerator( /*#__PURE__*/_regeneratorRuntime().mark(function _callee2(response) {
                var contentType, reader;
                return _regeneratorRuntime().wrap(function _callee2$(_context2) {
                  while (1) switch (_context2.prev = _context2.next) {
                    case 0:
                      contentType = response.headers.get('Content-Type') || '';
                      if (!contentType.includes('application/json')) {
                        _context2.next = 10;
                        break;
                      }
                      _context2.t0 = onMessage;
                      if (!_context2.t0) {
                        _context2.next = 9;
                        break;
                      }
                      _context2.t1 = onMessage;
                      _context2.next = 7;
                      return response.json();
                    case 7:
                      _context2.t2 = _context2.sent;
                      (0, _context2.t1)(_context2.t2);
                    case 9:
                      return _context2.abrupt("return");
                    case 10:
                      reader = response.body.getReader();
                      reader.read().then(function processText(_ref4) {
                        var done = _ref4.done,
                          value = _ref4.value;
                        if (done) {
                          onEnd && onEnd();
                          r(done);
                          return;
                        }
                        try {
                          onMessage && onMessage(value);
                        } catch (e) {}
                        return reader.read().then(processText);
                      });
                    case 12:
                    case "end":
                      return _context2.stop();
                  }
                }, _callee2);
              }));
              return function (_x5) {
                return _ref3.apply(this, arguments);
              };
            }())["catch"](function (err) {
              onError && onError(err);
              j(err);
            });
            onCancel && onCancel(abortController);
          }));
        case 3:
        case "end":
          return _context3.stop();
      }
    }, _callee3);
  }));
  return function ttsStream(_x3, _x4) {
    return _ref2.apply(this, arguments);
  };
}();
var ttsSync = /*#__PURE__*/function () {
  var _ref5 = _asyncToGenerator( /*#__PURE__*/_regeneratorRuntime().mark(function _callee5(url, customOptions) {
    var data, onMessage, onError, onCancel, options, abortController;
    return _regeneratorRuntime().wrap(function _callee5$(_context5) {
      while (1) switch (_context5.prev = _context5.next) {
        case 0:
          data = customOptions.data, onMessage = customOptions.onMessage, onError = customOptions.onError, onCancel = customOptions.onCancel, options = _objectWithoutProperties(customOptions, _excluded4$1);
          abortController = new AbortController();
          onCancel && onCancel(abortController);
          return _context5.abrupt("return", new Promise(function (r, j) {
            fetch(url, _objectSpread2({
              method: 'post',
              headers: {
                'Content-Type': 'application/json; charset=UTF-8'
              },
              body: JSON.stringify(data)
            }, options), {
              signal: abortController.signal
            }).then( /*#__PURE__*/function () {
              var _ref6 = _asyncToGenerator( /*#__PURE__*/_regeneratorRuntime().mark(function _callee4(res) {
                var contentType;
                return _regeneratorRuntime().wrap(function _callee4$(_context4) {
                  while (1) switch (_context4.prev = _context4.next) {
                    case 0:
                      contentType = res.headers.get('Content-Type') || '';
                      if (!contentType.includes('application/json')) {
                        _context4.next = 3;
                        break;
                      }
                      return _context4.abrupt("return", res.json());
                    case 3:
                      return _context4.abrupt("return", res.arrayBuffer());
                    case 4:
                    case "end":
                      return _context4.stop();
                  }
                }, _callee4);
              }));
              return function (_x8) {
                return _ref6.apply(this, arguments);
              };
            }()).then(function (res) {
              onMessage && onMessage(res);
              r(res);
            })["catch"](function (err) {
              onError(err);
              j(err);
            });
          }));
        case 4:
        case "end":
          return _context5.stop();
      }
    }, _callee5);
  }));
  return function ttsSync(_x6, _x7) {
    return _ref5.apply(this, arguments);
  };
}();

var global$1 = (typeof global !== "undefined" ? global :
  typeof self !== "undefined" ? self :
  typeof window !== "undefined" ? window : {});

// shim for using process in browser
// based off https://github.com/defunctzombie/node-process/blob/master/browser.js

function defaultSetTimout() {
    throw new Error('setTimeout has not been defined');
}
function defaultClearTimeout () {
    throw new Error('clearTimeout has not been defined');
}
var cachedSetTimeout = defaultSetTimout;
var cachedClearTimeout = defaultClearTimeout;
if (typeof global$1.setTimeout === 'function') {
    cachedSetTimeout = setTimeout;
}
if (typeof global$1.clearTimeout === 'function') {
    cachedClearTimeout = clearTimeout;
}

function runTimeout(fun) {
    if (cachedSetTimeout === setTimeout) {
        //normal enviroments in sane situations
        return setTimeout(fun, 0);
    }
    // if setTimeout wasn't available but was latter defined
    if ((cachedSetTimeout === defaultSetTimout || !cachedSetTimeout) && setTimeout) {
        cachedSetTimeout = setTimeout;
        return setTimeout(fun, 0);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedSetTimeout(fun, 0);
    } catch(e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't trust the global object when called normally
            return cachedSetTimeout.call(null, fun, 0);
        } catch(e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error
            return cachedSetTimeout.call(this, fun, 0);
        }
    }


}
function runClearTimeout(marker) {
    if (cachedClearTimeout === clearTimeout) {
        //normal enviroments in sane situations
        return clearTimeout(marker);
    }
    // if clearTimeout wasn't available but was latter defined
    if ((cachedClearTimeout === defaultClearTimeout || !cachedClearTimeout) && clearTimeout) {
        cachedClearTimeout = clearTimeout;
        return clearTimeout(marker);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedClearTimeout(marker);
    } catch (e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't  trust the global object when called normally
            return cachedClearTimeout.call(null, marker);
        } catch (e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error.
            // Some versions of I.E. have different rules for clearTimeout vs setTimeout
            return cachedClearTimeout.call(this, marker);
        }
    }



}
var queue = [];
var draining = false;
var currentQueue;
var queueIndex = -1;

function cleanUpNextTick() {
    if (!draining || !currentQueue) {
        return;
    }
    draining = false;
    if (currentQueue.length) {
        queue = currentQueue.concat(queue);
    } else {
        queueIndex = -1;
    }
    if (queue.length) {
        drainQueue();
    }
}

function drainQueue() {
    if (draining) {
        return;
    }
    var timeout = runTimeout(cleanUpNextTick);
    draining = true;

    var len = queue.length;
    while(len) {
        currentQueue = queue;
        queue = [];
        while (++queueIndex < len) {
            if (currentQueue) {
                currentQueue[queueIndex].run();
            }
        }
        queueIndex = -1;
        len = queue.length;
    }
    currentQueue = null;
    draining = false;
    runClearTimeout(timeout);
}
function nextTick(fun) {
    var args = new Array(arguments.length - 1);
    if (arguments.length > 1) {
        for (var i = 1; i < arguments.length; i++) {
            args[i - 1] = arguments[i];
        }
    }
    queue.push(new Item(fun, args));
    if (queue.length === 1 && !draining) {
        runTimeout(drainQueue);
    }
}
// v8 likes predictible objects
function Item(fun, array) {
    this.fun = fun;
    this.array = array;
}
Item.prototype.run = function () {
    this.fun.apply(null, this.array);
};
var title = 'browser';
var platform = 'browser';
var browser = true;
var env = {};
var argv = [];
var version = ''; // empty string to avoid regexp issues
var versions = {};
var release = {};
var config = {};

function noop() {}

var on = noop;
var addListener = noop;
var once = noop;
var off = noop;
var removeListener = noop;
var removeAllListeners = noop;
var emit = noop;

function binding(name) {
    throw new Error('process.binding is not supported');
}

function cwd () { return '/' }
function chdir (dir) {
    throw new Error('process.chdir is not supported');
}function umask() { return 0; }

// from https://github.com/kumavis/browser-process-hrtime/blob/master/index.js
var performance = global$1.performance || {};
var performanceNow =
  performance.now        ||
  performance.mozNow     ||
  performance.msNow      ||
  performance.oNow       ||
  performance.webkitNow  ||
  function(){ return (new Date()).getTime() };

// generate timestamp or delta
// see http://nodejs.org/api/process.html#process_process_hrtime
function hrtime(previousTimestamp){
  var clocktime = performanceNow.call(performance)*1e-3;
  var seconds = Math.floor(clocktime);
  var nanoseconds = Math.floor((clocktime%1)*1e9);
  if (previousTimestamp) {
    seconds = seconds - previousTimestamp[0];
    nanoseconds = nanoseconds - previousTimestamp[1];
    if (nanoseconds<0) {
      seconds--;
      nanoseconds += 1e9;
    }
  }
  return [seconds,nanoseconds]
}

var startTime = new Date();
function uptime() {
  var currentTime = new Date();
  var dif = currentTime - startTime;
  return dif / 1000;
}

var browser$1 = {
  nextTick: nextTick,
  title: title,
  browser: browser,
  env: env,
  argv: argv,
  version: version,
  versions: versions,
  on: on,
  addListener: addListener,
  once: once,
  off: off,
  removeListener: removeListener,
  removeAllListeners: removeAllListeners,
  emit: emit,
  binding: binding,
  cwd: cwd,
  chdir: chdir,
  umask: umask,
  hrtime: hrtime,
  platform: platform,
  release: release,
  config: config,
  uptime: uptime
};

var commonjsGlobal = typeof globalThis !== 'undefined' ? globalThis : typeof window !== 'undefined' ? window : typeof global !== 'undefined' ? global : typeof self !== 'undefined' ? self : {};

function getDefaultExportFromCjs (x) {
	return x && x.__esModule && Object.prototype.hasOwnProperty.call(x, 'default') ? x['default'] : x;
}

function getAugmentedNamespace(n) {
  if (n.__esModule) return n;
  var f = n.default;
	if (typeof f == "function") {
		var a = function a () {
			if (this instanceof a) {
        return Reflect.construct(f, arguments, this.constructor);
			}
			return f.apply(this, arguments);
		};
		a.prototype = f.prototype;
  } else a = {};
  Object.defineProperty(a, '__esModule', {value: true});
	Object.keys(n).forEach(function (k) {
		var d = Object.getOwnPropertyDescriptor(n, k);
		Object.defineProperty(a, k, d.get ? d : {
			enumerable: true,
			get: function () {
				return n[k];
			}
		});
	});
	return a;
}

var md5$1 = {exports: {}};

var _polyfillNode_crypto = {};

var _polyfillNode_crypto$1 = /*#__PURE__*/Object.freeze({
  __proto__: null,
  default: _polyfillNode_crypto
});

var require$$0 = /*@__PURE__*/getAugmentedNamespace(_polyfillNode_crypto$1);

var lookup = [];
var revLookup = [];
var Arr = typeof Uint8Array !== 'undefined' ? Uint8Array : Array;
var inited = false;
function init () {
  inited = true;
  var code = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
  for (var i = 0, len = code.length; i < len; ++i) {
    lookup[i] = code[i];
    revLookup[code.charCodeAt(i)] = i;
  }

  revLookup['-'.charCodeAt(0)] = 62;
  revLookup['_'.charCodeAt(0)] = 63;
}

function toByteArray (b64) {
  if (!inited) {
    init();
  }
  var i, j, l, tmp, placeHolders, arr;
  var len = b64.length;

  if (len % 4 > 0) {
    throw new Error('Invalid string. Length must be a multiple of 4')
  }

  // the number of equal signs (place holders)
  // if there are two placeholders, than the two characters before it
  // represent one byte
  // if there is only one, then the three characters before it represent 2 bytes
  // this is just a cheap hack to not do indexOf twice
  placeHolders = b64[len - 2] === '=' ? 2 : b64[len - 1] === '=' ? 1 : 0;

  // base64 is 4/3 + up to two characters of the original data
  arr = new Arr(len * 3 / 4 - placeHolders);

  // if there are placeholders, only get up to the last complete 4 chars
  l = placeHolders > 0 ? len - 4 : len;

  var L = 0;

  for (i = 0, j = 0; i < l; i += 4, j += 3) {
    tmp = (revLookup[b64.charCodeAt(i)] << 18) | (revLookup[b64.charCodeAt(i + 1)] << 12) | (revLookup[b64.charCodeAt(i + 2)] << 6) | revLookup[b64.charCodeAt(i + 3)];
    arr[L++] = (tmp >> 16) & 0xFF;
    arr[L++] = (tmp >> 8) & 0xFF;
    arr[L++] = tmp & 0xFF;
  }

  if (placeHolders === 2) {
    tmp = (revLookup[b64.charCodeAt(i)] << 2) | (revLookup[b64.charCodeAt(i + 1)] >> 4);
    arr[L++] = tmp & 0xFF;
  } else if (placeHolders === 1) {
    tmp = (revLookup[b64.charCodeAt(i)] << 10) | (revLookup[b64.charCodeAt(i + 1)] << 4) | (revLookup[b64.charCodeAt(i + 2)] >> 2);
    arr[L++] = (tmp >> 8) & 0xFF;
    arr[L++] = tmp & 0xFF;
  }

  return arr
}

function tripletToBase64 (num) {
  return lookup[num >> 18 & 0x3F] + lookup[num >> 12 & 0x3F] + lookup[num >> 6 & 0x3F] + lookup[num & 0x3F]
}

function encodeChunk (uint8, start, end) {
  var tmp;
  var output = [];
  for (var i = start; i < end; i += 3) {
    tmp = (uint8[i] << 16) + (uint8[i + 1] << 8) + (uint8[i + 2]);
    output.push(tripletToBase64(tmp));
  }
  return output.join('')
}

function fromByteArray (uint8) {
  if (!inited) {
    init();
  }
  var tmp;
  var len = uint8.length;
  var extraBytes = len % 3; // if we have 1 byte left, pad 2 bytes
  var output = '';
  var parts = [];
  var maxChunkLength = 16383; // must be multiple of 3

  // go through the array every three bytes, we'll deal with trailing stuff later
  for (var i = 0, len2 = len - extraBytes; i < len2; i += maxChunkLength) {
    parts.push(encodeChunk(uint8, i, (i + maxChunkLength) > len2 ? len2 : (i + maxChunkLength)));
  }

  // pad the end with zeros, but make sure to not forget the extra bytes
  if (extraBytes === 1) {
    tmp = uint8[len - 1];
    output += lookup[tmp >> 2];
    output += lookup[(tmp << 4) & 0x3F];
    output += '==';
  } else if (extraBytes === 2) {
    tmp = (uint8[len - 2] << 8) + (uint8[len - 1]);
    output += lookup[tmp >> 10];
    output += lookup[(tmp >> 4) & 0x3F];
    output += lookup[(tmp << 2) & 0x3F];
    output += '=';
  }

  parts.push(output);

  return parts.join('')
}

function read (buffer, offset, isLE, mLen, nBytes) {
  var e, m;
  var eLen = nBytes * 8 - mLen - 1;
  var eMax = (1 << eLen) - 1;
  var eBias = eMax >> 1;
  var nBits = -7;
  var i = isLE ? (nBytes - 1) : 0;
  var d = isLE ? -1 : 1;
  var s = buffer[offset + i];

  i += d;

  e = s & ((1 << (-nBits)) - 1);
  s >>= (-nBits);
  nBits += eLen;
  for (; nBits > 0; e = e * 256 + buffer[offset + i], i += d, nBits -= 8) {}

  m = e & ((1 << (-nBits)) - 1);
  e >>= (-nBits);
  nBits += mLen;
  for (; nBits > 0; m = m * 256 + buffer[offset + i], i += d, nBits -= 8) {}

  if (e === 0) {
    e = 1 - eBias;
  } else if (e === eMax) {
    return m ? NaN : ((s ? -1 : 1) * Infinity)
  } else {
    m = m + Math.pow(2, mLen);
    e = e - eBias;
  }
  return (s ? -1 : 1) * m * Math.pow(2, e - mLen)
}

function write (buffer, value, offset, isLE, mLen, nBytes) {
  var e, m, c;
  var eLen = nBytes * 8 - mLen - 1;
  var eMax = (1 << eLen) - 1;
  var eBias = eMax >> 1;
  var rt = (mLen === 23 ? Math.pow(2, -24) - Math.pow(2, -77) : 0);
  var i = isLE ? 0 : (nBytes - 1);
  var d = isLE ? 1 : -1;
  var s = value < 0 || (value === 0 && 1 / value < 0) ? 1 : 0;

  value = Math.abs(value);

  if (isNaN(value) || value === Infinity) {
    m = isNaN(value) ? 1 : 0;
    e = eMax;
  } else {
    e = Math.floor(Math.log(value) / Math.LN2);
    if (value * (c = Math.pow(2, -e)) < 1) {
      e--;
      c *= 2;
    }
    if (e + eBias >= 1) {
      value += rt / c;
    } else {
      value += rt * Math.pow(2, 1 - eBias);
    }
    if (value * c >= 2) {
      e++;
      c /= 2;
    }

    if (e + eBias >= eMax) {
      m = 0;
      e = eMax;
    } else if (e + eBias >= 1) {
      m = (value * c - 1) * Math.pow(2, mLen);
      e = e + eBias;
    } else {
      m = value * Math.pow(2, eBias - 1) * Math.pow(2, mLen);
      e = 0;
    }
  }

  for (; mLen >= 8; buffer[offset + i] = m & 0xff, i += d, m /= 256, mLen -= 8) {}

  e = (e << mLen) | m;
  eLen += mLen;
  for (; eLen > 0; buffer[offset + i] = e & 0xff, i += d, e /= 256, eLen -= 8) {}

  buffer[offset + i - d] |= s * 128;
}

var toString = {}.toString;

var isArray = Array.isArray || function (arr) {
  return toString.call(arr) == '[object Array]';
};

/*!
 * The buffer module from node.js, for the browser.
 *
 * @author   Feross Aboukhadijeh <feross@feross.org> <http://feross.org>
 * @license  MIT
 */
/* eslint-disable no-proto */


var INSPECT_MAX_BYTES = 50;

/**
 * If `Buffer.TYPED_ARRAY_SUPPORT`:
 *   === true    Use Uint8Array implementation (fastest)
 *   === false   Use Object implementation (most compatible, even IE6)
 *
 * Browsers that support typed arrays are IE 10+, Firefox 4+, Chrome 7+, Safari 5.1+,
 * Opera 11.6+, iOS 4.2+.
 *
 * Due to various browser bugs, sometimes the Object implementation will be used even
 * when the browser supports typed arrays.
 *
 * Note:
 *
 *   - Firefox 4-29 lacks support for adding new properties to `Uint8Array` instances,
 *     See: https://bugzilla.mozilla.org/show_bug.cgi?id=695438.
 *
 *   - Chrome 9-10 is missing the `TypedArray.prototype.subarray` function.
 *
 *   - IE10 has a broken `TypedArray.prototype.subarray` function which returns arrays of
 *     incorrect length in some situations.

 * We detect these buggy browsers and set `Buffer.TYPED_ARRAY_SUPPORT` to `false` so they
 * get the Object implementation, which is slower but behaves correctly.
 */
Buffer.TYPED_ARRAY_SUPPORT = global$1.TYPED_ARRAY_SUPPORT !== undefined
  ? global$1.TYPED_ARRAY_SUPPORT
  : true;

/*
 * Export kMaxLength after typed array support is determined.
 */
var _kMaxLength = kMaxLength();

function kMaxLength () {
  return Buffer.TYPED_ARRAY_SUPPORT
    ? 0x7fffffff
    : 0x3fffffff
}

function createBuffer (that, length) {
  if (kMaxLength() < length) {
    throw new RangeError('Invalid typed array length')
  }
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    // Return an augmented `Uint8Array` instance, for best performance
    that = new Uint8Array(length);
    that.__proto__ = Buffer.prototype;
  } else {
    // Fallback: Return an object instance of the Buffer class
    if (that === null) {
      that = new Buffer(length);
    }
    that.length = length;
  }

  return that
}

/**
 * The Buffer constructor returns instances of `Uint8Array` that have their
 * prototype changed to `Buffer.prototype`. Furthermore, `Buffer` is a subclass of
 * `Uint8Array`, so the returned instances will have all the node `Buffer` methods
 * and the `Uint8Array` methods. Square bracket notation works as expected -- it
 * returns a single octet.
 *
 * The `Uint8Array` prototype remains unmodified.
 */

function Buffer (arg, encodingOrOffset, length) {
  if (!Buffer.TYPED_ARRAY_SUPPORT && !(this instanceof Buffer)) {
    return new Buffer(arg, encodingOrOffset, length)
  }

  // Common case.
  if (typeof arg === 'number') {
    if (typeof encodingOrOffset === 'string') {
      throw new Error(
        'If encoding is specified then the first argument must be a string'
      )
    }
    return allocUnsafe(this, arg)
  }
  return from(this, arg, encodingOrOffset, length)
}

Buffer.poolSize = 8192; // not used by this implementation

// TODO: Legacy, not needed anymore. Remove in next major version.
Buffer._augment = function (arr) {
  arr.__proto__ = Buffer.prototype;
  return arr
};

function from (that, value, encodingOrOffset, length) {
  if (typeof value === 'number') {
    throw new TypeError('"value" argument must not be a number')
  }

  if (typeof ArrayBuffer !== 'undefined' && value instanceof ArrayBuffer) {
    return fromArrayBuffer(that, value, encodingOrOffset, length)
  }

  if (typeof value === 'string') {
    return fromString(that, value, encodingOrOffset)
  }

  return fromObject(that, value)
}

/**
 * Functionally equivalent to Buffer(arg, encoding) but throws a TypeError
 * if value is a number.
 * Buffer.from(str[, encoding])
 * Buffer.from(array)
 * Buffer.from(buffer)
 * Buffer.from(arrayBuffer[, byteOffset[, length]])
 **/
Buffer.from = function (value, encodingOrOffset, length) {
  return from(null, value, encodingOrOffset, length)
};

if (Buffer.TYPED_ARRAY_SUPPORT) {
  Buffer.prototype.__proto__ = Uint8Array.prototype;
  Buffer.__proto__ = Uint8Array;
  if (typeof Symbol !== 'undefined' && Symbol.species &&
      Buffer[Symbol.species] === Buffer) ;
}

function assertSize (size) {
  if (typeof size !== 'number') {
    throw new TypeError('"size" argument must be a number')
  } else if (size < 0) {
    throw new RangeError('"size" argument must not be negative')
  }
}

function alloc (that, size, fill, encoding) {
  assertSize(size);
  if (size <= 0) {
    return createBuffer(that, size)
  }
  if (fill !== undefined) {
    // Only pay attention to encoding if it's a string. This
    // prevents accidentally sending in a number that would
    // be interpretted as a start offset.
    return typeof encoding === 'string'
      ? createBuffer(that, size).fill(fill, encoding)
      : createBuffer(that, size).fill(fill)
  }
  return createBuffer(that, size)
}

/**
 * Creates a new filled Buffer instance.
 * alloc(size[, fill[, encoding]])
 **/
Buffer.alloc = function (size, fill, encoding) {
  return alloc(null, size, fill, encoding)
};

function allocUnsafe (that, size) {
  assertSize(size);
  that = createBuffer(that, size < 0 ? 0 : checked(size) | 0);
  if (!Buffer.TYPED_ARRAY_SUPPORT) {
    for (var i = 0; i < size; ++i) {
      that[i] = 0;
    }
  }
  return that
}

/**
 * Equivalent to Buffer(num), by default creates a non-zero-filled Buffer instance.
 * */
Buffer.allocUnsafe = function (size) {
  return allocUnsafe(null, size)
};
/**
 * Equivalent to SlowBuffer(num), by default creates a non-zero-filled Buffer instance.
 */
Buffer.allocUnsafeSlow = function (size) {
  return allocUnsafe(null, size)
};

function fromString (that, string, encoding) {
  if (typeof encoding !== 'string' || encoding === '') {
    encoding = 'utf8';
  }

  if (!Buffer.isEncoding(encoding)) {
    throw new TypeError('"encoding" must be a valid string encoding')
  }

  var length = byteLength(string, encoding) | 0;
  that = createBuffer(that, length);

  var actual = that.write(string, encoding);

  if (actual !== length) {
    // Writing a hex string, for example, that contains invalid characters will
    // cause everything after the first invalid character to be ignored. (e.g.
    // 'abxxcd' will be treated as 'ab')
    that = that.slice(0, actual);
  }

  return that
}

function fromArrayLike (that, array) {
  var length = array.length < 0 ? 0 : checked(array.length) | 0;
  that = createBuffer(that, length);
  for (var i = 0; i < length; i += 1) {
    that[i] = array[i] & 255;
  }
  return that
}

function fromArrayBuffer (that, array, byteOffset, length) {
  array.byteLength; // this throws if `array` is not a valid ArrayBuffer

  if (byteOffset < 0 || array.byteLength < byteOffset) {
    throw new RangeError('\'offset\' is out of bounds')
  }

  if (array.byteLength < byteOffset + (length || 0)) {
    throw new RangeError('\'length\' is out of bounds')
  }

  if (byteOffset === undefined && length === undefined) {
    array = new Uint8Array(array);
  } else if (length === undefined) {
    array = new Uint8Array(array, byteOffset);
  } else {
    array = new Uint8Array(array, byteOffset, length);
  }

  if (Buffer.TYPED_ARRAY_SUPPORT) {
    // Return an augmented `Uint8Array` instance, for best performance
    that = array;
    that.__proto__ = Buffer.prototype;
  } else {
    // Fallback: Return an object instance of the Buffer class
    that = fromArrayLike(that, array);
  }
  return that
}

function fromObject (that, obj) {
  if (internalIsBuffer(obj)) {
    var len = checked(obj.length) | 0;
    that = createBuffer(that, len);

    if (that.length === 0) {
      return that
    }

    obj.copy(that, 0, 0, len);
    return that
  }

  if (obj) {
    if ((typeof ArrayBuffer !== 'undefined' &&
        obj.buffer instanceof ArrayBuffer) || 'length' in obj) {
      if (typeof obj.length !== 'number' || isnan(obj.length)) {
        return createBuffer(that, 0)
      }
      return fromArrayLike(that, obj)
    }

    if (obj.type === 'Buffer' && isArray(obj.data)) {
      return fromArrayLike(that, obj.data)
    }
  }

  throw new TypeError('First argument must be a string, Buffer, ArrayBuffer, Array, or array-like object.')
}

function checked (length) {
  // Note: cannot use `length < kMaxLength()` here because that fails when
  // length is NaN (which is otherwise coerced to zero.)
  if (length >= kMaxLength()) {
    throw new RangeError('Attempt to allocate Buffer larger than maximum ' +
                         'size: 0x' + kMaxLength().toString(16) + ' bytes')
  }
  return length | 0
}

function SlowBuffer (length) {
  if (+length != length) { // eslint-disable-line eqeqeq
    length = 0;
  }
  return Buffer.alloc(+length)
}
Buffer.isBuffer = isBuffer;
function internalIsBuffer (b) {
  return !!(b != null && b._isBuffer)
}

Buffer.compare = function compare (a, b) {
  if (!internalIsBuffer(a) || !internalIsBuffer(b)) {
    throw new TypeError('Arguments must be Buffers')
  }

  if (a === b) return 0

  var x = a.length;
  var y = b.length;

  for (var i = 0, len = Math.min(x, y); i < len; ++i) {
    if (a[i] !== b[i]) {
      x = a[i];
      y = b[i];
      break
    }
  }

  if (x < y) return -1
  if (y < x) return 1
  return 0
};

Buffer.isEncoding = function isEncoding (encoding) {
  switch (String(encoding).toLowerCase()) {
    case 'hex':
    case 'utf8':
    case 'utf-8':
    case 'ascii':
    case 'latin1':
    case 'binary':
    case 'base64':
    case 'ucs2':
    case 'ucs-2':
    case 'utf16le':
    case 'utf-16le':
      return true
    default:
      return false
  }
};

Buffer.concat = function concat (list, length) {
  if (!isArray(list)) {
    throw new TypeError('"list" argument must be an Array of Buffers')
  }

  if (list.length === 0) {
    return Buffer.alloc(0)
  }

  var i;
  if (length === undefined) {
    length = 0;
    for (i = 0; i < list.length; ++i) {
      length += list[i].length;
    }
  }

  var buffer = Buffer.allocUnsafe(length);
  var pos = 0;
  for (i = 0; i < list.length; ++i) {
    var buf = list[i];
    if (!internalIsBuffer(buf)) {
      throw new TypeError('"list" argument must be an Array of Buffers')
    }
    buf.copy(buffer, pos);
    pos += buf.length;
  }
  return buffer
};

function byteLength (string, encoding) {
  if (internalIsBuffer(string)) {
    return string.length
  }
  if (typeof ArrayBuffer !== 'undefined' && typeof ArrayBuffer.isView === 'function' &&
      (ArrayBuffer.isView(string) || string instanceof ArrayBuffer)) {
    return string.byteLength
  }
  if (typeof string !== 'string') {
    string = '' + string;
  }

  var len = string.length;
  if (len === 0) return 0

  // Use a for loop to avoid recursion
  var loweredCase = false;
  for (;;) {
    switch (encoding) {
      case 'ascii':
      case 'latin1':
      case 'binary':
        return len
      case 'utf8':
      case 'utf-8':
      case undefined:
        return utf8ToBytes(string).length
      case 'ucs2':
      case 'ucs-2':
      case 'utf16le':
      case 'utf-16le':
        return len * 2
      case 'hex':
        return len >>> 1
      case 'base64':
        return base64ToBytes(string).length
      default:
        if (loweredCase) return utf8ToBytes(string).length // assume utf8
        encoding = ('' + encoding).toLowerCase();
        loweredCase = true;
    }
  }
}
Buffer.byteLength = byteLength;

function slowToString (encoding, start, end) {
  var loweredCase = false;

  // No need to verify that "this.length <= MAX_UINT32" since it's a read-only
  // property of a typed array.

  // This behaves neither like String nor Uint8Array in that we set start/end
  // to their upper/lower bounds if the value passed is out of range.
  // undefined is handled specially as per ECMA-262 6th Edition,
  // Section 13.3.3.7 Runtime Semantics: KeyedBindingInitialization.
  if (start === undefined || start < 0) {
    start = 0;
  }
  // Return early if start > this.length. Done here to prevent potential uint32
  // coercion fail below.
  if (start > this.length) {
    return ''
  }

  if (end === undefined || end > this.length) {
    end = this.length;
  }

  if (end <= 0) {
    return ''
  }

  // Force coersion to uint32. This will also coerce falsey/NaN values to 0.
  end >>>= 0;
  start >>>= 0;

  if (end <= start) {
    return ''
  }

  if (!encoding) encoding = 'utf8';

  while (true) {
    switch (encoding) {
      case 'hex':
        return hexSlice(this, start, end)

      case 'utf8':
      case 'utf-8':
        return utf8Slice(this, start, end)

      case 'ascii':
        return asciiSlice(this, start, end)

      case 'latin1':
      case 'binary':
        return latin1Slice(this, start, end)

      case 'base64':
        return base64Slice(this, start, end)

      case 'ucs2':
      case 'ucs-2':
      case 'utf16le':
      case 'utf-16le':
        return utf16leSlice(this, start, end)

      default:
        if (loweredCase) throw new TypeError('Unknown encoding: ' + encoding)
        encoding = (encoding + '').toLowerCase();
        loweredCase = true;
    }
  }
}

// The property is used by `Buffer.isBuffer` and `is-buffer` (in Safari 5-7) to detect
// Buffer instances.
Buffer.prototype._isBuffer = true;

function swap (b, n, m) {
  var i = b[n];
  b[n] = b[m];
  b[m] = i;
}

Buffer.prototype.swap16 = function swap16 () {
  var len = this.length;
  if (len % 2 !== 0) {
    throw new RangeError('Buffer size must be a multiple of 16-bits')
  }
  for (var i = 0; i < len; i += 2) {
    swap(this, i, i + 1);
  }
  return this
};

Buffer.prototype.swap32 = function swap32 () {
  var len = this.length;
  if (len % 4 !== 0) {
    throw new RangeError('Buffer size must be a multiple of 32-bits')
  }
  for (var i = 0; i < len; i += 4) {
    swap(this, i, i + 3);
    swap(this, i + 1, i + 2);
  }
  return this
};

Buffer.prototype.swap64 = function swap64 () {
  var len = this.length;
  if (len % 8 !== 0) {
    throw new RangeError('Buffer size must be a multiple of 64-bits')
  }
  for (var i = 0; i < len; i += 8) {
    swap(this, i, i + 7);
    swap(this, i + 1, i + 6);
    swap(this, i + 2, i + 5);
    swap(this, i + 3, i + 4);
  }
  return this
};

Buffer.prototype.toString = function toString () {
  var length = this.length | 0;
  if (length === 0) return ''
  if (arguments.length === 0) return utf8Slice(this, 0, length)
  return slowToString.apply(this, arguments)
};

Buffer.prototype.equals = function equals (b) {
  if (!internalIsBuffer(b)) throw new TypeError('Argument must be a Buffer')
  if (this === b) return true
  return Buffer.compare(this, b) === 0
};

Buffer.prototype.inspect = function inspect () {
  var str = '';
  var max = INSPECT_MAX_BYTES;
  if (this.length > 0) {
    str = this.toString('hex', 0, max).match(/.{2}/g).join(' ');
    if (this.length > max) str += ' ... ';
  }
  return '<Buffer ' + str + '>'
};

Buffer.prototype.compare = function compare (target, start, end, thisStart, thisEnd) {
  if (!internalIsBuffer(target)) {
    throw new TypeError('Argument must be a Buffer')
  }

  if (start === undefined) {
    start = 0;
  }
  if (end === undefined) {
    end = target ? target.length : 0;
  }
  if (thisStart === undefined) {
    thisStart = 0;
  }
  if (thisEnd === undefined) {
    thisEnd = this.length;
  }

  if (start < 0 || end > target.length || thisStart < 0 || thisEnd > this.length) {
    throw new RangeError('out of range index')
  }

  if (thisStart >= thisEnd && start >= end) {
    return 0
  }
  if (thisStart >= thisEnd) {
    return -1
  }
  if (start >= end) {
    return 1
  }

  start >>>= 0;
  end >>>= 0;
  thisStart >>>= 0;
  thisEnd >>>= 0;

  if (this === target) return 0

  var x = thisEnd - thisStart;
  var y = end - start;
  var len = Math.min(x, y);

  var thisCopy = this.slice(thisStart, thisEnd);
  var targetCopy = target.slice(start, end);

  for (var i = 0; i < len; ++i) {
    if (thisCopy[i] !== targetCopy[i]) {
      x = thisCopy[i];
      y = targetCopy[i];
      break
    }
  }

  if (x < y) return -1
  if (y < x) return 1
  return 0
};

// Finds either the first index of `val` in `buffer` at offset >= `byteOffset`,
// OR the last index of `val` in `buffer` at offset <= `byteOffset`.
//
// Arguments:
// - buffer - a Buffer to search
// - val - a string, Buffer, or number
// - byteOffset - an index into `buffer`; will be clamped to an int32
// - encoding - an optional encoding, relevant is val is a string
// - dir - true for indexOf, false for lastIndexOf
function bidirectionalIndexOf (buffer, val, byteOffset, encoding, dir) {
  // Empty buffer means no match
  if (buffer.length === 0) return -1

  // Normalize byteOffset
  if (typeof byteOffset === 'string') {
    encoding = byteOffset;
    byteOffset = 0;
  } else if (byteOffset > 0x7fffffff) {
    byteOffset = 0x7fffffff;
  } else if (byteOffset < -0x80000000) {
    byteOffset = -0x80000000;
  }
  byteOffset = +byteOffset;  // Coerce to Number.
  if (isNaN(byteOffset)) {
    // byteOffset: it it's undefined, null, NaN, "foo", etc, search whole buffer
    byteOffset = dir ? 0 : (buffer.length - 1);
  }

  // Normalize byteOffset: negative offsets start from the end of the buffer
  if (byteOffset < 0) byteOffset = buffer.length + byteOffset;
  if (byteOffset >= buffer.length) {
    if (dir) return -1
    else byteOffset = buffer.length - 1;
  } else if (byteOffset < 0) {
    if (dir) byteOffset = 0;
    else return -1
  }

  // Normalize val
  if (typeof val === 'string') {
    val = Buffer.from(val, encoding);
  }

  // Finally, search either indexOf (if dir is true) or lastIndexOf
  if (internalIsBuffer(val)) {
    // Special case: looking for empty string/buffer always fails
    if (val.length === 0) {
      return -1
    }
    return arrayIndexOf(buffer, val, byteOffset, encoding, dir)
  } else if (typeof val === 'number') {
    val = val & 0xFF; // Search for a byte value [0-255]
    if (Buffer.TYPED_ARRAY_SUPPORT &&
        typeof Uint8Array.prototype.indexOf === 'function') {
      if (dir) {
        return Uint8Array.prototype.indexOf.call(buffer, val, byteOffset)
      } else {
        return Uint8Array.prototype.lastIndexOf.call(buffer, val, byteOffset)
      }
    }
    return arrayIndexOf(buffer, [ val ], byteOffset, encoding, dir)
  }

  throw new TypeError('val must be string, number or Buffer')
}

function arrayIndexOf (arr, val, byteOffset, encoding, dir) {
  var indexSize = 1;
  var arrLength = arr.length;
  var valLength = val.length;

  if (encoding !== undefined) {
    encoding = String(encoding).toLowerCase();
    if (encoding === 'ucs2' || encoding === 'ucs-2' ||
        encoding === 'utf16le' || encoding === 'utf-16le') {
      if (arr.length < 2 || val.length < 2) {
        return -1
      }
      indexSize = 2;
      arrLength /= 2;
      valLength /= 2;
      byteOffset /= 2;
    }
  }

  function read (buf, i) {
    if (indexSize === 1) {
      return buf[i]
    } else {
      return buf.readUInt16BE(i * indexSize)
    }
  }

  var i;
  if (dir) {
    var foundIndex = -1;
    for (i = byteOffset; i < arrLength; i++) {
      if (read(arr, i) === read(val, foundIndex === -1 ? 0 : i - foundIndex)) {
        if (foundIndex === -1) foundIndex = i;
        if (i - foundIndex + 1 === valLength) return foundIndex * indexSize
      } else {
        if (foundIndex !== -1) i -= i - foundIndex;
        foundIndex = -1;
      }
    }
  } else {
    if (byteOffset + valLength > arrLength) byteOffset = arrLength - valLength;
    for (i = byteOffset; i >= 0; i--) {
      var found = true;
      for (var j = 0; j < valLength; j++) {
        if (read(arr, i + j) !== read(val, j)) {
          found = false;
          break
        }
      }
      if (found) return i
    }
  }

  return -1
}

Buffer.prototype.includes = function includes (val, byteOffset, encoding) {
  return this.indexOf(val, byteOffset, encoding) !== -1
};

Buffer.prototype.indexOf = function indexOf (val, byteOffset, encoding) {
  return bidirectionalIndexOf(this, val, byteOffset, encoding, true)
};

Buffer.prototype.lastIndexOf = function lastIndexOf (val, byteOffset, encoding) {
  return bidirectionalIndexOf(this, val, byteOffset, encoding, false)
};

function hexWrite (buf, string, offset, length) {
  offset = Number(offset) || 0;
  var remaining = buf.length - offset;
  if (!length) {
    length = remaining;
  } else {
    length = Number(length);
    if (length > remaining) {
      length = remaining;
    }
  }

  // must be an even number of digits
  var strLen = string.length;
  if (strLen % 2 !== 0) throw new TypeError('Invalid hex string')

  if (length > strLen / 2) {
    length = strLen / 2;
  }
  for (var i = 0; i < length; ++i) {
    var parsed = parseInt(string.substr(i * 2, 2), 16);
    if (isNaN(parsed)) return i
    buf[offset + i] = parsed;
  }
  return i
}

function utf8Write (buf, string, offset, length) {
  return blitBuffer(utf8ToBytes(string, buf.length - offset), buf, offset, length)
}

function asciiWrite (buf, string, offset, length) {
  return blitBuffer(asciiToBytes(string), buf, offset, length)
}

function latin1Write (buf, string, offset, length) {
  return asciiWrite(buf, string, offset, length)
}

function base64Write (buf, string, offset, length) {
  return blitBuffer(base64ToBytes(string), buf, offset, length)
}

function ucs2Write (buf, string, offset, length) {
  return blitBuffer(utf16leToBytes(string, buf.length - offset), buf, offset, length)
}

Buffer.prototype.write = function write (string, offset, length, encoding) {
  // Buffer#write(string)
  if (offset === undefined) {
    encoding = 'utf8';
    length = this.length;
    offset = 0;
  // Buffer#write(string, encoding)
  } else if (length === undefined && typeof offset === 'string') {
    encoding = offset;
    length = this.length;
    offset = 0;
  // Buffer#write(string, offset[, length][, encoding])
  } else if (isFinite(offset)) {
    offset = offset | 0;
    if (isFinite(length)) {
      length = length | 0;
      if (encoding === undefined) encoding = 'utf8';
    } else {
      encoding = length;
      length = undefined;
    }
  // legacy write(string, encoding, offset, length) - remove in v0.13
  } else {
    throw new Error(
      'Buffer.write(string, encoding, offset[, length]) is no longer supported'
    )
  }

  var remaining = this.length - offset;
  if (length === undefined || length > remaining) length = remaining;

  if ((string.length > 0 && (length < 0 || offset < 0)) || offset > this.length) {
    throw new RangeError('Attempt to write outside buffer bounds')
  }

  if (!encoding) encoding = 'utf8';

  var loweredCase = false;
  for (;;) {
    switch (encoding) {
      case 'hex':
        return hexWrite(this, string, offset, length)

      case 'utf8':
      case 'utf-8':
        return utf8Write(this, string, offset, length)

      case 'ascii':
        return asciiWrite(this, string, offset, length)

      case 'latin1':
      case 'binary':
        return latin1Write(this, string, offset, length)

      case 'base64':
        // Warning: maxLength not taken into account in base64Write
        return base64Write(this, string, offset, length)

      case 'ucs2':
      case 'ucs-2':
      case 'utf16le':
      case 'utf-16le':
        return ucs2Write(this, string, offset, length)

      default:
        if (loweredCase) throw new TypeError('Unknown encoding: ' + encoding)
        encoding = ('' + encoding).toLowerCase();
        loweredCase = true;
    }
  }
};

Buffer.prototype.toJSON = function toJSON () {
  return {
    type: 'Buffer',
    data: Array.prototype.slice.call(this._arr || this, 0)
  }
};

function base64Slice (buf, start, end) {
  if (start === 0 && end === buf.length) {
    return fromByteArray(buf)
  } else {
    return fromByteArray(buf.slice(start, end))
  }
}

function utf8Slice (buf, start, end) {
  end = Math.min(buf.length, end);
  var res = [];

  var i = start;
  while (i < end) {
    var firstByte = buf[i];
    var codePoint = null;
    var bytesPerSequence = (firstByte > 0xEF) ? 4
      : (firstByte > 0xDF) ? 3
      : (firstByte > 0xBF) ? 2
      : 1;

    if (i + bytesPerSequence <= end) {
      var secondByte, thirdByte, fourthByte, tempCodePoint;

      switch (bytesPerSequence) {
        case 1:
          if (firstByte < 0x80) {
            codePoint = firstByte;
          }
          break
        case 2:
          secondByte = buf[i + 1];
          if ((secondByte & 0xC0) === 0x80) {
            tempCodePoint = (firstByte & 0x1F) << 0x6 | (secondByte & 0x3F);
            if (tempCodePoint > 0x7F) {
              codePoint = tempCodePoint;
            }
          }
          break
        case 3:
          secondByte = buf[i + 1];
          thirdByte = buf[i + 2];
          if ((secondByte & 0xC0) === 0x80 && (thirdByte & 0xC0) === 0x80) {
            tempCodePoint = (firstByte & 0xF) << 0xC | (secondByte & 0x3F) << 0x6 | (thirdByte & 0x3F);
            if (tempCodePoint > 0x7FF && (tempCodePoint < 0xD800 || tempCodePoint > 0xDFFF)) {
              codePoint = tempCodePoint;
            }
          }
          break
        case 4:
          secondByte = buf[i + 1];
          thirdByte = buf[i + 2];
          fourthByte = buf[i + 3];
          if ((secondByte & 0xC0) === 0x80 && (thirdByte & 0xC0) === 0x80 && (fourthByte & 0xC0) === 0x80) {
            tempCodePoint = (firstByte & 0xF) << 0x12 | (secondByte & 0x3F) << 0xC | (thirdByte & 0x3F) << 0x6 | (fourthByte & 0x3F);
            if (tempCodePoint > 0xFFFF && tempCodePoint < 0x110000) {
              codePoint = tempCodePoint;
            }
          }
      }
    }

    if (codePoint === null) {
      // we did not generate a valid codePoint so insert a
      // replacement char (U+FFFD) and advance only 1 byte
      codePoint = 0xFFFD;
      bytesPerSequence = 1;
    } else if (codePoint > 0xFFFF) {
      // encode to utf16 (surrogate pair dance)
      codePoint -= 0x10000;
      res.push(codePoint >>> 10 & 0x3FF | 0xD800);
      codePoint = 0xDC00 | codePoint & 0x3FF;
    }

    res.push(codePoint);
    i += bytesPerSequence;
  }

  return decodeCodePointsArray(res)
}

// Based on http://stackoverflow.com/a/22747272/680742, the browser with
// the lowest limit is Chrome, with 0x10000 args.
// We go 1 magnitude less, for safety
var MAX_ARGUMENTS_LENGTH = 0x1000;

function decodeCodePointsArray (codePoints) {
  var len = codePoints.length;
  if (len <= MAX_ARGUMENTS_LENGTH) {
    return String.fromCharCode.apply(String, codePoints) // avoid extra slice()
  }

  // Decode in chunks to avoid "call stack size exceeded".
  var res = '';
  var i = 0;
  while (i < len) {
    res += String.fromCharCode.apply(
      String,
      codePoints.slice(i, i += MAX_ARGUMENTS_LENGTH)
    );
  }
  return res
}

function asciiSlice (buf, start, end) {
  var ret = '';
  end = Math.min(buf.length, end);

  for (var i = start; i < end; ++i) {
    ret += String.fromCharCode(buf[i] & 0x7F);
  }
  return ret
}

function latin1Slice (buf, start, end) {
  var ret = '';
  end = Math.min(buf.length, end);

  for (var i = start; i < end; ++i) {
    ret += String.fromCharCode(buf[i]);
  }
  return ret
}

function hexSlice (buf, start, end) {
  var len = buf.length;

  if (!start || start < 0) start = 0;
  if (!end || end < 0 || end > len) end = len;

  var out = '';
  for (var i = start; i < end; ++i) {
    out += toHex(buf[i]);
  }
  return out
}

function utf16leSlice (buf, start, end) {
  var bytes = buf.slice(start, end);
  var res = '';
  for (var i = 0; i < bytes.length; i += 2) {
    res += String.fromCharCode(bytes[i] + bytes[i + 1] * 256);
  }
  return res
}

Buffer.prototype.slice = function slice (start, end) {
  var len = this.length;
  start = ~~start;
  end = end === undefined ? len : ~~end;

  if (start < 0) {
    start += len;
    if (start < 0) start = 0;
  } else if (start > len) {
    start = len;
  }

  if (end < 0) {
    end += len;
    if (end < 0) end = 0;
  } else if (end > len) {
    end = len;
  }

  if (end < start) end = start;

  var newBuf;
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    newBuf = this.subarray(start, end);
    newBuf.__proto__ = Buffer.prototype;
  } else {
    var sliceLen = end - start;
    newBuf = new Buffer(sliceLen, undefined);
    for (var i = 0; i < sliceLen; ++i) {
      newBuf[i] = this[i + start];
    }
  }

  return newBuf
};

/*
 * Need to make sure that buffer isn't trying to write out of bounds.
 */
function checkOffset (offset, ext, length) {
  if ((offset % 1) !== 0 || offset < 0) throw new RangeError('offset is not uint')
  if (offset + ext > length) throw new RangeError('Trying to access beyond buffer length')
}

Buffer.prototype.readUIntLE = function readUIntLE (offset, byteLength, noAssert) {
  offset = offset | 0;
  byteLength = byteLength | 0;
  if (!noAssert) checkOffset(offset, byteLength, this.length);

  var val = this[offset];
  var mul = 1;
  var i = 0;
  while (++i < byteLength && (mul *= 0x100)) {
    val += this[offset + i] * mul;
  }

  return val
};

Buffer.prototype.readUIntBE = function readUIntBE (offset, byteLength, noAssert) {
  offset = offset | 0;
  byteLength = byteLength | 0;
  if (!noAssert) {
    checkOffset(offset, byteLength, this.length);
  }

  var val = this[offset + --byteLength];
  var mul = 1;
  while (byteLength > 0 && (mul *= 0x100)) {
    val += this[offset + --byteLength] * mul;
  }

  return val
};

Buffer.prototype.readUInt8 = function readUInt8 (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 1, this.length);
  return this[offset]
};

Buffer.prototype.readUInt16LE = function readUInt16LE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 2, this.length);
  return this[offset] | (this[offset + 1] << 8)
};

Buffer.prototype.readUInt16BE = function readUInt16BE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 2, this.length);
  return (this[offset] << 8) | this[offset + 1]
};

Buffer.prototype.readUInt32LE = function readUInt32LE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length);

  return ((this[offset]) |
      (this[offset + 1] << 8) |
      (this[offset + 2] << 16)) +
      (this[offset + 3] * 0x1000000)
};

Buffer.prototype.readUInt32BE = function readUInt32BE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length);

  return (this[offset] * 0x1000000) +
    ((this[offset + 1] << 16) |
    (this[offset + 2] << 8) |
    this[offset + 3])
};

Buffer.prototype.readIntLE = function readIntLE (offset, byteLength, noAssert) {
  offset = offset | 0;
  byteLength = byteLength | 0;
  if (!noAssert) checkOffset(offset, byteLength, this.length);

  var val = this[offset];
  var mul = 1;
  var i = 0;
  while (++i < byteLength && (mul *= 0x100)) {
    val += this[offset + i] * mul;
  }
  mul *= 0x80;

  if (val >= mul) val -= Math.pow(2, 8 * byteLength);

  return val
};

Buffer.prototype.readIntBE = function readIntBE (offset, byteLength, noAssert) {
  offset = offset | 0;
  byteLength = byteLength | 0;
  if (!noAssert) checkOffset(offset, byteLength, this.length);

  var i = byteLength;
  var mul = 1;
  var val = this[offset + --i];
  while (i > 0 && (mul *= 0x100)) {
    val += this[offset + --i] * mul;
  }
  mul *= 0x80;

  if (val >= mul) val -= Math.pow(2, 8 * byteLength);

  return val
};

Buffer.prototype.readInt8 = function readInt8 (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 1, this.length);
  if (!(this[offset] & 0x80)) return (this[offset])
  return ((0xff - this[offset] + 1) * -1)
};

Buffer.prototype.readInt16LE = function readInt16LE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 2, this.length);
  var val = this[offset] | (this[offset + 1] << 8);
  return (val & 0x8000) ? val | 0xFFFF0000 : val
};

Buffer.prototype.readInt16BE = function readInt16BE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 2, this.length);
  var val = this[offset + 1] | (this[offset] << 8);
  return (val & 0x8000) ? val | 0xFFFF0000 : val
};

Buffer.prototype.readInt32LE = function readInt32LE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length);

  return (this[offset]) |
    (this[offset + 1] << 8) |
    (this[offset + 2] << 16) |
    (this[offset + 3] << 24)
};

Buffer.prototype.readInt32BE = function readInt32BE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length);

  return (this[offset] << 24) |
    (this[offset + 1] << 16) |
    (this[offset + 2] << 8) |
    (this[offset + 3])
};

Buffer.prototype.readFloatLE = function readFloatLE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length);
  return read(this, offset, true, 23, 4)
};

Buffer.prototype.readFloatBE = function readFloatBE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 4, this.length);
  return read(this, offset, false, 23, 4)
};

Buffer.prototype.readDoubleLE = function readDoubleLE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 8, this.length);
  return read(this, offset, true, 52, 8)
};

Buffer.prototype.readDoubleBE = function readDoubleBE (offset, noAssert) {
  if (!noAssert) checkOffset(offset, 8, this.length);
  return read(this, offset, false, 52, 8)
};

function checkInt (buf, value, offset, ext, max, min) {
  if (!internalIsBuffer(buf)) throw new TypeError('"buffer" argument must be a Buffer instance')
  if (value > max || value < min) throw new RangeError('"value" argument is out of bounds')
  if (offset + ext > buf.length) throw new RangeError('Index out of range')
}

Buffer.prototype.writeUIntLE = function writeUIntLE (value, offset, byteLength, noAssert) {
  value = +value;
  offset = offset | 0;
  byteLength = byteLength | 0;
  if (!noAssert) {
    var maxBytes = Math.pow(2, 8 * byteLength) - 1;
    checkInt(this, value, offset, byteLength, maxBytes, 0);
  }

  var mul = 1;
  var i = 0;
  this[offset] = value & 0xFF;
  while (++i < byteLength && (mul *= 0x100)) {
    this[offset + i] = (value / mul) & 0xFF;
  }

  return offset + byteLength
};

Buffer.prototype.writeUIntBE = function writeUIntBE (value, offset, byteLength, noAssert) {
  value = +value;
  offset = offset | 0;
  byteLength = byteLength | 0;
  if (!noAssert) {
    var maxBytes = Math.pow(2, 8 * byteLength) - 1;
    checkInt(this, value, offset, byteLength, maxBytes, 0);
  }

  var i = byteLength - 1;
  var mul = 1;
  this[offset + i] = value & 0xFF;
  while (--i >= 0 && (mul *= 0x100)) {
    this[offset + i] = (value / mul) & 0xFF;
  }

  return offset + byteLength
};

Buffer.prototype.writeUInt8 = function writeUInt8 (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 1, 0xff, 0);
  if (!Buffer.TYPED_ARRAY_SUPPORT) value = Math.floor(value);
  this[offset] = (value & 0xff);
  return offset + 1
};

function objectWriteUInt16 (buf, value, offset, littleEndian) {
  if (value < 0) value = 0xffff + value + 1;
  for (var i = 0, j = Math.min(buf.length - offset, 2); i < j; ++i) {
    buf[offset + i] = (value & (0xff << (8 * (littleEndian ? i : 1 - i)))) >>>
      (littleEndian ? i : 1 - i) * 8;
  }
}

Buffer.prototype.writeUInt16LE = function writeUInt16LE (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 2, 0xffff, 0);
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value & 0xff);
    this[offset + 1] = (value >>> 8);
  } else {
    objectWriteUInt16(this, value, offset, true);
  }
  return offset + 2
};

Buffer.prototype.writeUInt16BE = function writeUInt16BE (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 2, 0xffff, 0);
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value >>> 8);
    this[offset + 1] = (value & 0xff);
  } else {
    objectWriteUInt16(this, value, offset, false);
  }
  return offset + 2
};

function objectWriteUInt32 (buf, value, offset, littleEndian) {
  if (value < 0) value = 0xffffffff + value + 1;
  for (var i = 0, j = Math.min(buf.length - offset, 4); i < j; ++i) {
    buf[offset + i] = (value >>> (littleEndian ? i : 3 - i) * 8) & 0xff;
  }
}

Buffer.prototype.writeUInt32LE = function writeUInt32LE (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 4, 0xffffffff, 0);
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset + 3] = (value >>> 24);
    this[offset + 2] = (value >>> 16);
    this[offset + 1] = (value >>> 8);
    this[offset] = (value & 0xff);
  } else {
    objectWriteUInt32(this, value, offset, true);
  }
  return offset + 4
};

Buffer.prototype.writeUInt32BE = function writeUInt32BE (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 4, 0xffffffff, 0);
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value >>> 24);
    this[offset + 1] = (value >>> 16);
    this[offset + 2] = (value >>> 8);
    this[offset + 3] = (value & 0xff);
  } else {
    objectWriteUInt32(this, value, offset, false);
  }
  return offset + 4
};

Buffer.prototype.writeIntLE = function writeIntLE (value, offset, byteLength, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) {
    var limit = Math.pow(2, 8 * byteLength - 1);

    checkInt(this, value, offset, byteLength, limit - 1, -limit);
  }

  var i = 0;
  var mul = 1;
  var sub = 0;
  this[offset] = value & 0xFF;
  while (++i < byteLength && (mul *= 0x100)) {
    if (value < 0 && sub === 0 && this[offset + i - 1] !== 0) {
      sub = 1;
    }
    this[offset + i] = ((value / mul) >> 0) - sub & 0xFF;
  }

  return offset + byteLength
};

Buffer.prototype.writeIntBE = function writeIntBE (value, offset, byteLength, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) {
    var limit = Math.pow(2, 8 * byteLength - 1);

    checkInt(this, value, offset, byteLength, limit - 1, -limit);
  }

  var i = byteLength - 1;
  var mul = 1;
  var sub = 0;
  this[offset + i] = value & 0xFF;
  while (--i >= 0 && (mul *= 0x100)) {
    if (value < 0 && sub === 0 && this[offset + i + 1] !== 0) {
      sub = 1;
    }
    this[offset + i] = ((value / mul) >> 0) - sub & 0xFF;
  }

  return offset + byteLength
};

Buffer.prototype.writeInt8 = function writeInt8 (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 1, 0x7f, -0x80);
  if (!Buffer.TYPED_ARRAY_SUPPORT) value = Math.floor(value);
  if (value < 0) value = 0xff + value + 1;
  this[offset] = (value & 0xff);
  return offset + 1
};

Buffer.prototype.writeInt16LE = function writeInt16LE (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 2, 0x7fff, -0x8000);
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value & 0xff);
    this[offset + 1] = (value >>> 8);
  } else {
    objectWriteUInt16(this, value, offset, true);
  }
  return offset + 2
};

Buffer.prototype.writeInt16BE = function writeInt16BE (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 2, 0x7fff, -0x8000);
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value >>> 8);
    this[offset + 1] = (value & 0xff);
  } else {
    objectWriteUInt16(this, value, offset, false);
  }
  return offset + 2
};

Buffer.prototype.writeInt32LE = function writeInt32LE (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 4, 0x7fffffff, -0x80000000);
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value & 0xff);
    this[offset + 1] = (value >>> 8);
    this[offset + 2] = (value >>> 16);
    this[offset + 3] = (value >>> 24);
  } else {
    objectWriteUInt32(this, value, offset, true);
  }
  return offset + 4
};

Buffer.prototype.writeInt32BE = function writeInt32BE (value, offset, noAssert) {
  value = +value;
  offset = offset | 0;
  if (!noAssert) checkInt(this, value, offset, 4, 0x7fffffff, -0x80000000);
  if (value < 0) value = 0xffffffff + value + 1;
  if (Buffer.TYPED_ARRAY_SUPPORT) {
    this[offset] = (value >>> 24);
    this[offset + 1] = (value >>> 16);
    this[offset + 2] = (value >>> 8);
    this[offset + 3] = (value & 0xff);
  } else {
    objectWriteUInt32(this, value, offset, false);
  }
  return offset + 4
};

function checkIEEE754 (buf, value, offset, ext, max, min) {
  if (offset + ext > buf.length) throw new RangeError('Index out of range')
  if (offset < 0) throw new RangeError('Index out of range')
}

function writeFloat (buf, value, offset, littleEndian, noAssert) {
  if (!noAssert) {
    checkIEEE754(buf, value, offset, 4);
  }
  write(buf, value, offset, littleEndian, 23, 4);
  return offset + 4
}

Buffer.prototype.writeFloatLE = function writeFloatLE (value, offset, noAssert) {
  return writeFloat(this, value, offset, true, noAssert)
};

Buffer.prototype.writeFloatBE = function writeFloatBE (value, offset, noAssert) {
  return writeFloat(this, value, offset, false, noAssert)
};

function writeDouble (buf, value, offset, littleEndian, noAssert) {
  if (!noAssert) {
    checkIEEE754(buf, value, offset, 8);
  }
  write(buf, value, offset, littleEndian, 52, 8);
  return offset + 8
}

Buffer.prototype.writeDoubleLE = function writeDoubleLE (value, offset, noAssert) {
  return writeDouble(this, value, offset, true, noAssert)
};

Buffer.prototype.writeDoubleBE = function writeDoubleBE (value, offset, noAssert) {
  return writeDouble(this, value, offset, false, noAssert)
};

// copy(targetBuffer, targetStart=0, sourceStart=0, sourceEnd=buffer.length)
Buffer.prototype.copy = function copy (target, targetStart, start, end) {
  if (!start) start = 0;
  if (!end && end !== 0) end = this.length;
  if (targetStart >= target.length) targetStart = target.length;
  if (!targetStart) targetStart = 0;
  if (end > 0 && end < start) end = start;

  // Copy 0 bytes; we're done
  if (end === start) return 0
  if (target.length === 0 || this.length === 0) return 0

  // Fatal error conditions
  if (targetStart < 0) {
    throw new RangeError('targetStart out of bounds')
  }
  if (start < 0 || start >= this.length) throw new RangeError('sourceStart out of bounds')
  if (end < 0) throw new RangeError('sourceEnd out of bounds')

  // Are we oob?
  if (end > this.length) end = this.length;
  if (target.length - targetStart < end - start) {
    end = target.length - targetStart + start;
  }

  var len = end - start;
  var i;

  if (this === target && start < targetStart && targetStart < end) {
    // descending copy from end
    for (i = len - 1; i >= 0; --i) {
      target[i + targetStart] = this[i + start];
    }
  } else if (len < 1000 || !Buffer.TYPED_ARRAY_SUPPORT) {
    // ascending copy from start
    for (i = 0; i < len; ++i) {
      target[i + targetStart] = this[i + start];
    }
  } else {
    Uint8Array.prototype.set.call(
      target,
      this.subarray(start, start + len),
      targetStart
    );
  }

  return len
};

// Usage:
//    buffer.fill(number[, offset[, end]])
//    buffer.fill(buffer[, offset[, end]])
//    buffer.fill(string[, offset[, end]][, encoding])
Buffer.prototype.fill = function fill (val, start, end, encoding) {
  // Handle string cases:
  if (typeof val === 'string') {
    if (typeof start === 'string') {
      encoding = start;
      start = 0;
      end = this.length;
    } else if (typeof end === 'string') {
      encoding = end;
      end = this.length;
    }
    if (val.length === 1) {
      var code = val.charCodeAt(0);
      if (code < 256) {
        val = code;
      }
    }
    if (encoding !== undefined && typeof encoding !== 'string') {
      throw new TypeError('encoding must be a string')
    }
    if (typeof encoding === 'string' && !Buffer.isEncoding(encoding)) {
      throw new TypeError('Unknown encoding: ' + encoding)
    }
  } else if (typeof val === 'number') {
    val = val & 255;
  }

  // Invalid ranges are not set to a default, so can range check early.
  if (start < 0 || this.length < start || this.length < end) {
    throw new RangeError('Out of range index')
  }

  if (end <= start) {
    return this
  }

  start = start >>> 0;
  end = end === undefined ? this.length : end >>> 0;

  if (!val) val = 0;

  var i;
  if (typeof val === 'number') {
    for (i = start; i < end; ++i) {
      this[i] = val;
    }
  } else {
    var bytes = internalIsBuffer(val)
      ? val
      : utf8ToBytes(new Buffer(val, encoding).toString());
    var len = bytes.length;
    for (i = 0; i < end - start; ++i) {
      this[i + start] = bytes[i % len];
    }
  }

  return this
};

// HELPER FUNCTIONS
// ================

var INVALID_BASE64_RE = /[^+\/0-9A-Za-z-_]/g;

function base64clean (str) {
  // Node strips out invalid characters like \n and \t from the string, base64-js does not
  str = stringtrim(str).replace(INVALID_BASE64_RE, '');
  // Node converts strings with length < 2 to ''
  if (str.length < 2) return ''
  // Node allows for non-padded base64 strings (missing trailing ===), base64-js does not
  while (str.length % 4 !== 0) {
    str = str + '=';
  }
  return str
}

function stringtrim (str) {
  if (str.trim) return str.trim()
  return str.replace(/^\s+|\s+$/g, '')
}

function toHex (n) {
  if (n < 16) return '0' + n.toString(16)
  return n.toString(16)
}

function utf8ToBytes (string, units) {
  units = units || Infinity;
  var codePoint;
  var length = string.length;
  var leadSurrogate = null;
  var bytes = [];

  for (var i = 0; i < length; ++i) {
    codePoint = string.charCodeAt(i);

    // is surrogate component
    if (codePoint > 0xD7FF && codePoint < 0xE000) {
      // last char was a lead
      if (!leadSurrogate) {
        // no lead yet
        if (codePoint > 0xDBFF) {
          // unexpected trail
          if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD);
          continue
        } else if (i + 1 === length) {
          // unpaired lead
          if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD);
          continue
        }

        // valid lead
        leadSurrogate = codePoint;

        continue
      }

      // 2 leads in a row
      if (codePoint < 0xDC00) {
        if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD);
        leadSurrogate = codePoint;
        continue
      }

      // valid surrogate pair
      codePoint = (leadSurrogate - 0xD800 << 10 | codePoint - 0xDC00) + 0x10000;
    } else if (leadSurrogate) {
      // valid bmp char, but last char was a lead
      if ((units -= 3) > -1) bytes.push(0xEF, 0xBF, 0xBD);
    }

    leadSurrogate = null;

    // encode utf8
    if (codePoint < 0x80) {
      if ((units -= 1) < 0) break
      bytes.push(codePoint);
    } else if (codePoint < 0x800) {
      if ((units -= 2) < 0) break
      bytes.push(
        codePoint >> 0x6 | 0xC0,
        codePoint & 0x3F | 0x80
      );
    } else if (codePoint < 0x10000) {
      if ((units -= 3) < 0) break
      bytes.push(
        codePoint >> 0xC | 0xE0,
        codePoint >> 0x6 & 0x3F | 0x80,
        codePoint & 0x3F | 0x80
      );
    } else if (codePoint < 0x110000) {
      if ((units -= 4) < 0) break
      bytes.push(
        codePoint >> 0x12 | 0xF0,
        codePoint >> 0xC & 0x3F | 0x80,
        codePoint >> 0x6 & 0x3F | 0x80,
        codePoint & 0x3F | 0x80
      );
    } else {
      throw new Error('Invalid code point')
    }
  }

  return bytes
}

function asciiToBytes (str) {
  var byteArray = [];
  for (var i = 0; i < str.length; ++i) {
    // Node's code seems to be doing this and not & 0x7F..
    byteArray.push(str.charCodeAt(i) & 0xFF);
  }
  return byteArray
}

function utf16leToBytes (str, units) {
  var c, hi, lo;
  var byteArray = [];
  for (var i = 0; i < str.length; ++i) {
    if ((units -= 2) < 0) break

    c = str.charCodeAt(i);
    hi = c >> 8;
    lo = c % 256;
    byteArray.push(lo);
    byteArray.push(hi);
  }

  return byteArray
}


function base64ToBytes (str) {
  return toByteArray(base64clean(str))
}

function blitBuffer (src, dst, offset, length) {
  for (var i = 0; i < length; ++i) {
    if ((i + offset >= dst.length) || (i >= src.length)) break
    dst[i + offset] = src[i];
  }
  return i
}

function isnan (val) {
  return val !== val // eslint-disable-line no-self-compare
}


// the following is from is-buffer, also by Feross Aboukhadijeh and with same lisence
// The _isBuffer check is for Safari 5-7 support, because it's missing
// Object.prototype.constructor. Remove this eventually
function isBuffer(obj) {
  return obj != null && (!!obj._isBuffer || isFastBuffer(obj) || isSlowBuffer(obj))
}

function isFastBuffer (obj) {
  return !!obj.constructor && typeof obj.constructor.isBuffer === 'function' && obj.constructor.isBuffer(obj)
}

// For Node v0.10 support. Remove this eventually.
function isSlowBuffer (obj) {
  return typeof obj.readFloatLE === 'function' && typeof obj.slice === 'function' && isFastBuffer(obj.slice(0, 0))
}

var _polyfillNode_buffer = /*#__PURE__*/Object.freeze({
  __proto__: null,
  Buffer: Buffer,
  INSPECT_MAX_BYTES: INSPECT_MAX_BYTES,
  SlowBuffer: SlowBuffer,
  isBuffer: isBuffer,
  kMaxLength: _kMaxLength
});

var require$$1 = /*@__PURE__*/getAugmentedNamespace(_polyfillNode_buffer);

(function (module) {
	(function () {

	  var INPUT_ERROR = 'input is invalid type';
	  var FINALIZE_ERROR = 'finalize already called';
	  var WINDOW = typeof window === 'object';
	  var root = WINDOW ? window : {};
	  if (root.JS_MD5_NO_WINDOW) {
	    WINDOW = false;
	  }
	  var WEB_WORKER = !WINDOW && typeof self === 'object';
	  var NODE_JS = !root.JS_MD5_NO_NODE_JS && typeof browser$1 === 'object' && browser$1.versions && browser$1.versions.node;
	  if (NODE_JS) {
	    root = commonjsGlobal;
	  } else if (WEB_WORKER) {
	    root = self;
	  }
	  var COMMON_JS = !root.JS_MD5_NO_COMMON_JS && 'object' === 'object' && module.exports;
	  var ARRAY_BUFFER = !root.JS_MD5_NO_ARRAY_BUFFER && typeof ArrayBuffer !== 'undefined';
	  var HEX_CHARS = '0123456789abcdef'.split('');
	  var EXTRA = [128, 32768, 8388608, -2147483648];
	  var SHIFT = [0, 8, 16, 24];
	  var OUTPUT_TYPES = ['hex', 'array', 'digest', 'buffer', 'arrayBuffer', 'base64'];
	  var BASE64_ENCODE_CHAR = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'.split('');

	  var blocks = [], buffer8;
	  if (ARRAY_BUFFER) {
	    var buffer = new ArrayBuffer(68);
	    buffer8 = new Uint8Array(buffer);
	    blocks = new Uint32Array(buffer);
	  }

	  var isArray = Array.isArray;
	  if (root.JS_MD5_NO_NODE_JS || !isArray) {
	    isArray = function (obj) {
	      return Object.prototype.toString.call(obj) === '[object Array]';
	    };
	  }

	  var isView = ArrayBuffer.isView;
	  if (ARRAY_BUFFER && (root.JS_MD5_NO_ARRAY_BUFFER_IS_VIEW || !isView)) {
	    isView = function (obj) {
	      return typeof obj === 'object' && obj.buffer && obj.buffer.constructor === ArrayBuffer;
	    };
	  }

	  // [message: string, isString: bool]
	  var formatMessage = function (message) {
	    var type = typeof message;
	    if (type === 'string') {
	      return [message, true];
	    }
	    if (type !== 'object' || message === null) {
	      throw new Error(INPUT_ERROR);
	    }
	    if (ARRAY_BUFFER && message.constructor === ArrayBuffer) {
	      return [new Uint8Array(message), false];
	    }
	    if (!isArray(message) && !isView(message)) {
	      throw new Error(INPUT_ERROR);
	    }
	    return [message, false];
	  };

	  /**
	   * @method hex
	   * @memberof md5
	   * @description Output hash as hex string
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {String} Hex string
	   * @example
	   * md5.hex('The quick brown fox jumps over the lazy dog');
	   * // equal to
	   * md5('The quick brown fox jumps over the lazy dog');
	   */
	  /**
	   * @method digest
	   * @memberof md5
	   * @description Output hash as bytes array
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {Array} Bytes array
	   * @example
	   * md5.digest('The quick brown fox jumps over the lazy dog');
	   */
	  /**
	   * @method array
	   * @memberof md5
	   * @description Output hash as bytes array
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {Array} Bytes array
	   * @example
	   * md5.array('The quick brown fox jumps over the lazy dog');
	   */
	  /**
	   * @method arrayBuffer
	   * @memberof md5
	   * @description Output hash as ArrayBuffer
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {ArrayBuffer} ArrayBuffer
	   * @example
	   * md5.arrayBuffer('The quick brown fox jumps over the lazy dog');
	   */
	  /**
	   * @method buffer
	   * @deprecated This maybe confuse with Buffer in node.js. Please use arrayBuffer instead.
	   * @memberof md5
	   * @description Output hash as ArrayBuffer
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {ArrayBuffer} ArrayBuffer
	   * @example
	   * md5.buffer('The quick brown fox jumps over the lazy dog');
	   */
	  /**
	   * @method base64
	   * @memberof md5
	   * @description Output hash as base64 string
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {String} base64 string
	   * @example
	   * md5.base64('The quick brown fox jumps over the lazy dog');
	   */
	  var createOutputMethod = function (outputType) {
	    return function (message) {
	      return new Md5(true).update(message)[outputType]();
	    };
	  };

	  /**
	   * @method create
	   * @memberof md5
	   * @description Create Md5 object
	   * @returns {Md5} Md5 object.
	   * @example
	   * var hash = md5.create();
	   */
	  /**
	   * @method update
	   * @memberof md5
	   * @description Create and update Md5 object
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {Md5} Md5 object.
	   * @example
	   * var hash = md5.update('The quick brown fox jumps over the lazy dog');
	   * // equal to
	   * var hash = md5.create();
	   * hash.update('The quick brown fox jumps over the lazy dog');
	   */
	  var createMethod = function () {
	    var method = createOutputMethod('hex');
	    if (NODE_JS) {
	      method = nodeWrap(method);
	    }
	    method.create = function () {
	      return new Md5();
	    };
	    method.update = function (message) {
	      return method.create().update(message);
	    };
	    for (var i = 0; i < OUTPUT_TYPES.length; ++i) {
	      var type = OUTPUT_TYPES[i];
	      method[type] = createOutputMethod(type);
	    }
	    return method;
	  };

	  var nodeWrap = function (method) {
	    var crypto = require$$0;
	    var Buffer = require$$1.Buffer;
	    var bufferFrom;
	    if (Buffer.from && !root.JS_MD5_NO_BUFFER_FROM) {
	      bufferFrom = Buffer.from;
	    } else {
	      bufferFrom = function (message) {
	        return new Buffer(message);
	      };
	    }
	    var nodeMethod = function (message) {
	      if (typeof message === 'string') {
	        return crypto.createHash('md5').update(message, 'utf8').digest('hex');
	      } else {
	        if (message === null || message === undefined) {
	          throw new Error(INPUT_ERROR);
	        } else if (message.constructor === ArrayBuffer) {
	          message = new Uint8Array(message);
	        }
	      }
	      if (isArray(message) || isView(message) ||
	        message.constructor === Buffer) {
	        return crypto.createHash('md5').update(bufferFrom(message)).digest('hex');
	      } else {
	        return method(message);
	      }
	    };
	    return nodeMethod;
	  };

	  /**
	   * @namespace md5.hmac
	   */
	  /**
	   * @method hex
	   * @memberof md5.hmac
	   * @description Output hash as hex string
	   * @param {String|Array|Uint8Array|ArrayBuffer} key key
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {String} Hex string
	   * @example
	   * md5.hmac.hex('key', 'The quick brown fox jumps over the lazy dog');
	   * // equal to
	   * md5.hmac('key', 'The quick brown fox jumps over the lazy dog');
	   */

	  /**
	   * @method digest
	   * @memberof md5.hmac
	   * @description Output hash as bytes array
	   * @param {String|Array|Uint8Array|ArrayBuffer} key key
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {Array} Bytes array
	   * @example
	   * md5.hmac.digest('key', 'The quick brown fox jumps over the lazy dog');
	   */
	  /**
	   * @method array
	   * @memberof md5.hmac
	   * @description Output hash as bytes array
	   * @param {String|Array|Uint8Array|ArrayBuffer} key key
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {Array} Bytes array
	   * @example
	   * md5.hmac.array('key', 'The quick brown fox jumps over the lazy dog');
	   */
	  /**
	   * @method arrayBuffer
	   * @memberof md5.hmac
	   * @description Output hash as ArrayBuffer
	   * @param {String|Array|Uint8Array|ArrayBuffer} key key
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {ArrayBuffer} ArrayBuffer
	   * @example
	   * md5.hmac.arrayBuffer('key', 'The quick brown fox jumps over the lazy dog');
	   */
	  /**
	   * @method buffer
	   * @deprecated This maybe confuse with Buffer in node.js. Please use arrayBuffer instead.
	   * @memberof md5.hmac
	   * @description Output hash as ArrayBuffer
	   * @param {String|Array|Uint8Array|ArrayBuffer} key key
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {ArrayBuffer} ArrayBuffer
	   * @example
	   * md5.hmac.buffer('key', 'The quick brown fox jumps over the lazy dog');
	   */
	  /**
	   * @method base64
	   * @memberof md5.hmac
	   * @description Output hash as base64 string
	   * @param {String|Array|Uint8Array|ArrayBuffer} key key
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {String} base64 string
	   * @example
	   * md5.hmac.base64('key', 'The quick brown fox jumps over the lazy dog');
	   */
	  var createHmacOutputMethod = function (outputType) {
	    return function (key, message) {
	      return new HmacMd5(key, true).update(message)[outputType]();
	    };
	  };

	  /**
	   * @method create
	   * @memberof md5.hmac
	   * @description Create HmacMd5 object
	   * @param {String|Array|Uint8Array|ArrayBuffer} key key
	   * @returns {HmacMd5} HmacMd5 object.
	   * @example
	   * var hash = md5.hmac.create('key');
	   */
	  /**
	   * @method update
	   * @memberof md5.hmac
	   * @description Create and update HmacMd5 object
	   * @param {String|Array|Uint8Array|ArrayBuffer} key key
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {HmacMd5} HmacMd5 object.
	   * @example
	   * var hash = md5.hmac.update('key', 'The quick brown fox jumps over the lazy dog');
	   * // equal to
	   * var hash = md5.hmac.create('key');
	   * hash.update('The quick brown fox jumps over the lazy dog');
	   */
	  var createHmacMethod = function () {
	    var method = createHmacOutputMethod('hex');
	    method.create = function (key) {
	      return new HmacMd5(key);
	    };
	    method.update = function (key, message) {
	      return method.create(key).update(message);
	    };
	    for (var i = 0; i < OUTPUT_TYPES.length; ++i) {
	      var type = OUTPUT_TYPES[i];
	      method[type] = createHmacOutputMethod(type);
	    }
	    return method;
	  };

	  /**
	   * Md5 class
	   * @class Md5
	   * @description This is internal class.
	   * @see {@link md5.create}
	   */
	  function Md5(sharedMemory) {
	    if (sharedMemory) {
	      blocks[0] = blocks[16] = blocks[1] = blocks[2] = blocks[3] =
	      blocks[4] = blocks[5] = blocks[6] = blocks[7] =
	      blocks[8] = blocks[9] = blocks[10] = blocks[11] =
	      blocks[12] = blocks[13] = blocks[14] = blocks[15] = 0;
	      this.blocks = blocks;
	      this.buffer8 = buffer8;
	    } else {
	      if (ARRAY_BUFFER) {
	        var buffer = new ArrayBuffer(68);
	        this.buffer8 = new Uint8Array(buffer);
	        this.blocks = new Uint32Array(buffer);
	      } else {
	        this.blocks = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
	      }
	    }
	    this.h0 = this.h1 = this.h2 = this.h3 = this.start = this.bytes = this.hBytes = 0;
	    this.finalized = this.hashed = false;
	    this.first = true;
	  }

	  /**
	   * @method update
	   * @memberof Md5
	   * @instance
	   * @description Update hash
	   * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	   * @returns {Md5} Md5 object.
	   * @see {@link md5.update}
	   */
	  Md5.prototype.update = function (message) {
	    if (this.finalized) {
	      throw new Error(FINALIZE_ERROR);
	    }

	    var result = formatMessage(message);
	    message = result[0];
	    var isString = result[1];
	    var code, index = 0, i, length = message.length, blocks = this.blocks;
	    var buffer8 = this.buffer8;

	    while (index < length) {
	      if (this.hashed) {
	        this.hashed = false;
	        blocks[0] = blocks[16];
	        blocks[16] = blocks[1] = blocks[2] = blocks[3] =
	        blocks[4] = blocks[5] = blocks[6] = blocks[7] =
	        blocks[8] = blocks[9] = blocks[10] = blocks[11] =
	        blocks[12] = blocks[13] = blocks[14] = blocks[15] = 0;
	      }

	      if (isString) {
	        if (ARRAY_BUFFER) {
	          for (i = this.start; index < length && i < 64; ++index) {
	            code = message.charCodeAt(index);
	            if (code < 0x80) {
	              buffer8[i++] = code;
	            } else if (code < 0x800) {
	              buffer8[i++] = 0xc0 | (code >>> 6);
	              buffer8[i++] = 0x80 | (code & 0x3f);
	            } else if (code < 0xd800 || code >= 0xe000) {
	              buffer8[i++] = 0xe0 | (code >>> 12);
	              buffer8[i++] = 0x80 | ((code >>> 6) & 0x3f);
	              buffer8[i++] = 0x80 | (code & 0x3f);
	            } else {
	              code = 0x10000 + (((code & 0x3ff) << 10) | (message.charCodeAt(++index) & 0x3ff));
	              buffer8[i++] = 0xf0 | (code >>> 18);
	              buffer8[i++] = 0x80 | ((code >>> 12) & 0x3f);
	              buffer8[i++] = 0x80 | ((code >>> 6) & 0x3f);
	              buffer8[i++] = 0x80 | (code & 0x3f);
	            }
	          }
	        } else {
	          for (i = this.start; index < length && i < 64; ++index) {
	            code = message.charCodeAt(index);
	            if (code < 0x80) {
	              blocks[i >>> 2] |= code << SHIFT[i++ & 3];
	            } else if (code < 0x800) {
	              blocks[i >>> 2] |= (0xc0 | (code >>> 6)) << SHIFT[i++ & 3];
	              blocks[i >>> 2] |= (0x80 | (code & 0x3f)) << SHIFT[i++ & 3];
	            } else if (code < 0xd800 || code >= 0xe000) {
	              blocks[i >>> 2] |= (0xe0 | (code >>> 12)) << SHIFT[i++ & 3];
	              blocks[i >>> 2] |= (0x80 | ((code >>> 6) & 0x3f)) << SHIFT[i++ & 3];
	              blocks[i >>> 2] |= (0x80 | (code & 0x3f)) << SHIFT[i++ & 3];
	            } else {
	              code = 0x10000 + (((code & 0x3ff) << 10) | (message.charCodeAt(++index) & 0x3ff));
	              blocks[i >>> 2] |= (0xf0 | (code >>> 18)) << SHIFT[i++ & 3];
	              blocks[i >>> 2] |= (0x80 | ((code >>> 12) & 0x3f)) << SHIFT[i++ & 3];
	              blocks[i >>> 2] |= (0x80 | ((code >>> 6) & 0x3f)) << SHIFT[i++ & 3];
	              blocks[i >>> 2] |= (0x80 | (code & 0x3f)) << SHIFT[i++ & 3];
	            }
	          }
	        }
	      } else {
	        if (ARRAY_BUFFER) {
	          for (i = this.start; index < length && i < 64; ++index) {
	            buffer8[i++] = message[index];
	          }
	        } else {
	          for (i = this.start; index < length && i < 64; ++index) {
	            blocks[i >>> 2] |= message[index] << SHIFT[i++ & 3];
	          }
	        }
	      }
	      this.lastByteIndex = i;
	      this.bytes += i - this.start;
	      if (i >= 64) {
	        this.start = i - 64;
	        this.hash();
	        this.hashed = true;
	      } else {
	        this.start = i;
	      }
	    }
	    if (this.bytes > 4294967295) {
	      this.hBytes += this.bytes / 4294967296 << 0;
	      this.bytes = this.bytes % 4294967296;
	    }
	    return this;
	  };

	  Md5.prototype.finalize = function () {
	    if (this.finalized) {
	      return;
	    }
	    this.finalized = true;
	    var blocks = this.blocks, i = this.lastByteIndex;
	    blocks[i >>> 2] |= EXTRA[i & 3];
	    if (i >= 56) {
	      if (!this.hashed) {
	        this.hash();
	      }
	      blocks[0] = blocks[16];
	      blocks[16] = blocks[1] = blocks[2] = blocks[3] =
	      blocks[4] = blocks[5] = blocks[6] = blocks[7] =
	      blocks[8] = blocks[9] = blocks[10] = blocks[11] =
	      blocks[12] = blocks[13] = blocks[14] = blocks[15] = 0;
	    }
	    blocks[14] = this.bytes << 3;
	    blocks[15] = this.hBytes << 3 | this.bytes >>> 29;
	    this.hash();
	  };

	  Md5.prototype.hash = function () {
	    var a, b, c, d, bc, da, blocks = this.blocks;

	    if (this.first) {
	      a = blocks[0] - 680876937;
	      a = (a << 7 | a >>> 25) - 271733879 << 0;
	      d = (-1732584194 ^ a & 2004318071) + blocks[1] - 117830708;
	      d = (d << 12 | d >>> 20) + a << 0;
	      c = (-271733879 ^ (d & (a ^ -271733879))) + blocks[2] - 1126478375;
	      c = (c << 17 | c >>> 15) + d << 0;
	      b = (a ^ (c & (d ^ a))) + blocks[3] - 1316259209;
	      b = (b << 22 | b >>> 10) + c << 0;
	    } else {
	      a = this.h0;
	      b = this.h1;
	      c = this.h2;
	      d = this.h3;
	      a += (d ^ (b & (c ^ d))) + blocks[0] - 680876936;
	      a = (a << 7 | a >>> 25) + b << 0;
	      d += (c ^ (a & (b ^ c))) + blocks[1] - 389564586;
	      d = (d << 12 | d >>> 20) + a << 0;
	      c += (b ^ (d & (a ^ b))) + blocks[2] + 606105819;
	      c = (c << 17 | c >>> 15) + d << 0;
	      b += (a ^ (c & (d ^ a))) + blocks[3] - 1044525330;
	      b = (b << 22 | b >>> 10) + c << 0;
	    }

	    a += (d ^ (b & (c ^ d))) + blocks[4] - 176418897;
	    a = (a << 7 | a >>> 25) + b << 0;
	    d += (c ^ (a & (b ^ c))) + blocks[5] + 1200080426;
	    d = (d << 12 | d >>> 20) + a << 0;
	    c += (b ^ (d & (a ^ b))) + blocks[6] - 1473231341;
	    c = (c << 17 | c >>> 15) + d << 0;
	    b += (a ^ (c & (d ^ a))) + blocks[7] - 45705983;
	    b = (b << 22 | b >>> 10) + c << 0;
	    a += (d ^ (b & (c ^ d))) + blocks[8] + 1770035416;
	    a = (a << 7 | a >>> 25) + b << 0;
	    d += (c ^ (a & (b ^ c))) + blocks[9] - 1958414417;
	    d = (d << 12 | d >>> 20) + a << 0;
	    c += (b ^ (d & (a ^ b))) + blocks[10] - 42063;
	    c = (c << 17 | c >>> 15) + d << 0;
	    b += (a ^ (c & (d ^ a))) + blocks[11] - 1990404162;
	    b = (b << 22 | b >>> 10) + c << 0;
	    a += (d ^ (b & (c ^ d))) + blocks[12] + 1804603682;
	    a = (a << 7 | a >>> 25) + b << 0;
	    d += (c ^ (a & (b ^ c))) + blocks[13] - 40341101;
	    d = (d << 12 | d >>> 20) + a << 0;
	    c += (b ^ (d & (a ^ b))) + blocks[14] - 1502002290;
	    c = (c << 17 | c >>> 15) + d << 0;
	    b += (a ^ (c & (d ^ a))) + blocks[15] + 1236535329;
	    b = (b << 22 | b >>> 10) + c << 0;
	    a += (c ^ (d & (b ^ c))) + blocks[1] - 165796510;
	    a = (a << 5 | a >>> 27) + b << 0;
	    d += (b ^ (c & (a ^ b))) + blocks[6] - 1069501632;
	    d = (d << 9 | d >>> 23) + a << 0;
	    c += (a ^ (b & (d ^ a))) + blocks[11] + 643717713;
	    c = (c << 14 | c >>> 18) + d << 0;
	    b += (d ^ (a & (c ^ d))) + blocks[0] - 373897302;
	    b = (b << 20 | b >>> 12) + c << 0;
	    a += (c ^ (d & (b ^ c))) + blocks[5] - 701558691;
	    a = (a << 5 | a >>> 27) + b << 0;
	    d += (b ^ (c & (a ^ b))) + blocks[10] + 38016083;
	    d = (d << 9 | d >>> 23) + a << 0;
	    c += (a ^ (b & (d ^ a))) + blocks[15] - 660478335;
	    c = (c << 14 | c >>> 18) + d << 0;
	    b += (d ^ (a & (c ^ d))) + blocks[4] - 405537848;
	    b = (b << 20 | b >>> 12) + c << 0;
	    a += (c ^ (d & (b ^ c))) + blocks[9] + 568446438;
	    a = (a << 5 | a >>> 27) + b << 0;
	    d += (b ^ (c & (a ^ b))) + blocks[14] - 1019803690;
	    d = (d << 9 | d >>> 23) + a << 0;
	    c += (a ^ (b & (d ^ a))) + blocks[3] - 187363961;
	    c = (c << 14 | c >>> 18) + d << 0;
	    b += (d ^ (a & (c ^ d))) + blocks[8] + 1163531501;
	    b = (b << 20 | b >>> 12) + c << 0;
	    a += (c ^ (d & (b ^ c))) + blocks[13] - 1444681467;
	    a = (a << 5 | a >>> 27) + b << 0;
	    d += (b ^ (c & (a ^ b))) + blocks[2] - 51403784;
	    d = (d << 9 | d >>> 23) + a << 0;
	    c += (a ^ (b & (d ^ a))) + blocks[7] + 1735328473;
	    c = (c << 14 | c >>> 18) + d << 0;
	    b += (d ^ (a & (c ^ d))) + blocks[12] - 1926607734;
	    b = (b << 20 | b >>> 12) + c << 0;
	    bc = b ^ c;
	    a += (bc ^ d) + blocks[5] - 378558;
	    a = (a << 4 | a >>> 28) + b << 0;
	    d += (bc ^ a) + blocks[8] - 2022574463;
	    d = (d << 11 | d >>> 21) + a << 0;
	    da = d ^ a;
	    c += (da ^ b) + blocks[11] + 1839030562;
	    c = (c << 16 | c >>> 16) + d << 0;
	    b += (da ^ c) + blocks[14] - 35309556;
	    b = (b << 23 | b >>> 9) + c << 0;
	    bc = b ^ c;
	    a += (bc ^ d) + blocks[1] - 1530992060;
	    a = (a << 4 | a >>> 28) + b << 0;
	    d += (bc ^ a) + blocks[4] + 1272893353;
	    d = (d << 11 | d >>> 21) + a << 0;
	    da = d ^ a;
	    c += (da ^ b) + blocks[7] - 155497632;
	    c = (c << 16 | c >>> 16) + d << 0;
	    b += (da ^ c) + blocks[10] - 1094730640;
	    b = (b << 23 | b >>> 9) + c << 0;
	    bc = b ^ c;
	    a += (bc ^ d) + blocks[13] + 681279174;
	    a = (a << 4 | a >>> 28) + b << 0;
	    d += (bc ^ a) + blocks[0] - 358537222;
	    d = (d << 11 | d >>> 21) + a << 0;
	    da = d ^ a;
	    c += (da ^ b) + blocks[3] - 722521979;
	    c = (c << 16 | c >>> 16) + d << 0;
	    b += (da ^ c) + blocks[6] + 76029189;
	    b = (b << 23 | b >>> 9) + c << 0;
	    bc = b ^ c;
	    a += (bc ^ d) + blocks[9] - 640364487;
	    a = (a << 4 | a >>> 28) + b << 0;
	    d += (bc ^ a) + blocks[12] - 421815835;
	    d = (d << 11 | d >>> 21) + a << 0;
	    da = d ^ a;
	    c += (da ^ b) + blocks[15] + 530742520;
	    c = (c << 16 | c >>> 16) + d << 0;
	    b += (da ^ c) + blocks[2] - 995338651;
	    b = (b << 23 | b >>> 9) + c << 0;
	    a += (c ^ (b | ~d)) + blocks[0] - 198630844;
	    a = (a << 6 | a >>> 26) + b << 0;
	    d += (b ^ (a | ~c)) + blocks[7] + 1126891415;
	    d = (d << 10 | d >>> 22) + a << 0;
	    c += (a ^ (d | ~b)) + blocks[14] - 1416354905;
	    c = (c << 15 | c >>> 17) + d << 0;
	    b += (d ^ (c | ~a)) + blocks[5] - 57434055;
	    b = (b << 21 | b >>> 11) + c << 0;
	    a += (c ^ (b | ~d)) + blocks[12] + 1700485571;
	    a = (a << 6 | a >>> 26) + b << 0;
	    d += (b ^ (a | ~c)) + blocks[3] - 1894986606;
	    d = (d << 10 | d >>> 22) + a << 0;
	    c += (a ^ (d | ~b)) + blocks[10] - 1051523;
	    c = (c << 15 | c >>> 17) + d << 0;
	    b += (d ^ (c | ~a)) + blocks[1] - 2054922799;
	    b = (b << 21 | b >>> 11) + c << 0;
	    a += (c ^ (b | ~d)) + blocks[8] + 1873313359;
	    a = (a << 6 | a >>> 26) + b << 0;
	    d += (b ^ (a | ~c)) + blocks[15] - 30611744;
	    d = (d << 10 | d >>> 22) + a << 0;
	    c += (a ^ (d | ~b)) + blocks[6] - 1560198380;
	    c = (c << 15 | c >>> 17) + d << 0;
	    b += (d ^ (c | ~a)) + blocks[13] + 1309151649;
	    b = (b << 21 | b >>> 11) + c << 0;
	    a += (c ^ (b | ~d)) + blocks[4] - 145523070;
	    a = (a << 6 | a >>> 26) + b << 0;
	    d += (b ^ (a | ~c)) + blocks[11] - 1120210379;
	    d = (d << 10 | d >>> 22) + a << 0;
	    c += (a ^ (d | ~b)) + blocks[2] + 718787259;
	    c = (c << 15 | c >>> 17) + d << 0;
	    b += (d ^ (c | ~a)) + blocks[9] - 343485551;
	    b = (b << 21 | b >>> 11) + c << 0;

	    if (this.first) {
	      this.h0 = a + 1732584193 << 0;
	      this.h1 = b - 271733879 << 0;
	      this.h2 = c - 1732584194 << 0;
	      this.h3 = d + 271733878 << 0;
	      this.first = false;
	    } else {
	      this.h0 = this.h0 + a << 0;
	      this.h1 = this.h1 + b << 0;
	      this.h2 = this.h2 + c << 0;
	      this.h3 = this.h3 + d << 0;
	    }
	  };

	  /**
	   * @method hex
	   * @memberof Md5
	   * @instance
	   * @description Output hash as hex string
	   * @returns {String} Hex string
	   * @see {@link md5.hex}
	   * @example
	   * hash.hex();
	   */
	  Md5.prototype.hex = function () {
	    this.finalize();

	    var h0 = this.h0, h1 = this.h1, h2 = this.h2, h3 = this.h3;

	    return HEX_CHARS[(h0 >>> 4) & 0x0F] + HEX_CHARS[h0 & 0x0F] +
	      HEX_CHARS[(h0 >>> 12) & 0x0F] + HEX_CHARS[(h0 >>> 8) & 0x0F] +
	      HEX_CHARS[(h0 >>> 20) & 0x0F] + HEX_CHARS[(h0 >>> 16) & 0x0F] +
	      HEX_CHARS[(h0 >>> 28) & 0x0F] + HEX_CHARS[(h0 >>> 24) & 0x0F] +
	      HEX_CHARS[(h1 >>> 4) & 0x0F] + HEX_CHARS[h1 & 0x0F] +
	      HEX_CHARS[(h1 >>> 12) & 0x0F] + HEX_CHARS[(h1 >>> 8) & 0x0F] +
	      HEX_CHARS[(h1 >>> 20) & 0x0F] + HEX_CHARS[(h1 >>> 16) & 0x0F] +
	      HEX_CHARS[(h1 >>> 28) & 0x0F] + HEX_CHARS[(h1 >>> 24) & 0x0F] +
	      HEX_CHARS[(h2 >>> 4) & 0x0F] + HEX_CHARS[h2 & 0x0F] +
	      HEX_CHARS[(h2 >>> 12) & 0x0F] + HEX_CHARS[(h2 >>> 8) & 0x0F] +
	      HEX_CHARS[(h2 >>> 20) & 0x0F] + HEX_CHARS[(h2 >>> 16) & 0x0F] +
	      HEX_CHARS[(h2 >>> 28) & 0x0F] + HEX_CHARS[(h2 >>> 24) & 0x0F] +
	      HEX_CHARS[(h3 >>> 4) & 0x0F] + HEX_CHARS[h3 & 0x0F] +
	      HEX_CHARS[(h3 >>> 12) & 0x0F] + HEX_CHARS[(h3 >>> 8) & 0x0F] +
	      HEX_CHARS[(h3 >>> 20) & 0x0F] + HEX_CHARS[(h3 >>> 16) & 0x0F] +
	      HEX_CHARS[(h3 >>> 28) & 0x0F] + HEX_CHARS[(h3 >>> 24) & 0x0F];
	  };

	  /**
	   * @method toString
	   * @memberof Md5
	   * @instance
	   * @description Output hash as hex string
	   * @returns {String} Hex string
	   * @see {@link md5.hex}
	   * @example
	   * hash.toString();
	   */
	  Md5.prototype.toString = Md5.prototype.hex;

	  /**
	   * @method digest
	   * @memberof Md5
	   * @instance
	   * @description Output hash as bytes array
	   * @returns {Array} Bytes array
	   * @see {@link md5.digest}
	   * @example
	   * hash.digest();
	   */
	  Md5.prototype.digest = function () {
	    this.finalize();

	    var h0 = this.h0, h1 = this.h1, h2 = this.h2, h3 = this.h3;
	    return [
	      h0 & 0xFF, (h0 >>> 8) & 0xFF, (h0 >>> 16) & 0xFF, (h0 >>> 24) & 0xFF,
	      h1 & 0xFF, (h1 >>> 8) & 0xFF, (h1 >>> 16) & 0xFF, (h1 >>> 24) & 0xFF,
	      h2 & 0xFF, (h2 >>> 8) & 0xFF, (h2 >>> 16) & 0xFF, (h2 >>> 24) & 0xFF,
	      h3 & 0xFF, (h3 >>> 8) & 0xFF, (h3 >>> 16) & 0xFF, (h3 >>> 24) & 0xFF
	    ];
	  };

	  /**
	   * @method array
	   * @memberof Md5
	   * @instance
	   * @description Output hash as bytes array
	   * @returns {Array} Bytes array
	   * @see {@link md5.array}
	   * @example
	   * hash.array();
	   */
	  Md5.prototype.array = Md5.prototype.digest;

	  /**
	   * @method arrayBuffer
	   * @memberof Md5
	   * @instance
	   * @description Output hash as ArrayBuffer
	   * @returns {ArrayBuffer} ArrayBuffer
	   * @see {@link md5.arrayBuffer}
	   * @example
	   * hash.arrayBuffer();
	   */
	  Md5.prototype.arrayBuffer = function () {
	    this.finalize();

	    var buffer = new ArrayBuffer(16);
	    var blocks = new Uint32Array(buffer);
	    blocks[0] = this.h0;
	    blocks[1] = this.h1;
	    blocks[2] = this.h2;
	    blocks[3] = this.h3;
	    return buffer;
	  };

	  /**
	   * @method buffer
	   * @deprecated This maybe confuse with Buffer in node.js. Please use arrayBuffer instead.
	   * @memberof Md5
	   * @instance
	   * @description Output hash as ArrayBuffer
	   * @returns {ArrayBuffer} ArrayBuffer
	   * @see {@link md5.buffer}
	   * @example
	   * hash.buffer();
	   */
	  Md5.prototype.buffer = Md5.prototype.arrayBuffer;

	  /**
	   * @method base64
	   * @memberof Md5
	   * @instance
	   * @description Output hash as base64 string
	   * @returns {String} base64 string
	   * @see {@link md5.base64}
	   * @example
	   * hash.base64();
	   */
	  Md5.prototype.base64 = function () {
	    var v1, v2, v3, base64Str = '', bytes = this.array();
	    for (var i = 0; i < 15;) {
	      v1 = bytes[i++];
	      v2 = bytes[i++];
	      v3 = bytes[i++];
	      base64Str += BASE64_ENCODE_CHAR[v1 >>> 2] +
	        BASE64_ENCODE_CHAR[(v1 << 4 | v2 >>> 4) & 63] +
	        BASE64_ENCODE_CHAR[(v2 << 2 | v3 >>> 6) & 63] +
	        BASE64_ENCODE_CHAR[v3 & 63];
	    }
	    v1 = bytes[i];
	    base64Str += BASE64_ENCODE_CHAR[v1 >>> 2] +
	      BASE64_ENCODE_CHAR[(v1 << 4) & 63] +
	      '==';
	    return base64Str;
	  };

	  /**
	   * HmacMd5 class
	   * @class HmacMd5
	   * @extends Md5
	   * @description This is internal class.
	   * @see {@link md5.hmac.create}
	   */
	  function HmacMd5(key, sharedMemory) {
	    var i, result = formatMessage(key);
	    key = result[0];
	    if (result[1]) {
	      var bytes = [], length = key.length, index = 0, code;
	      for (i = 0; i < length; ++i) {
	        code = key.charCodeAt(i);
	        if (code < 0x80) {
	          bytes[index++] = code;
	        } else if (code < 0x800) {
	          bytes[index++] = (0xc0 | (code >>> 6));
	          bytes[index++] = (0x80 | (code & 0x3f));
	        } else if (code < 0xd800 || code >= 0xe000) {
	          bytes[index++] = (0xe0 | (code >>> 12));
	          bytes[index++] = (0x80 | ((code >>> 6) & 0x3f));
	          bytes[index++] = (0x80 | (code & 0x3f));
	        } else {
	          code = 0x10000 + (((code & 0x3ff) << 10) | (key.charCodeAt(++i) & 0x3ff));
	          bytes[index++] = (0xf0 | (code >>> 18));
	          bytes[index++] = (0x80 | ((code >>> 12) & 0x3f));
	          bytes[index++] = (0x80 | ((code >>> 6) & 0x3f));
	          bytes[index++] = (0x80 | (code & 0x3f));
	        }
	      }
	      key = bytes;
	    }

	    if (key.length > 64) {
	      key = (new Md5(true)).update(key).array();
	    }

	    var oKeyPad = [], iKeyPad = [];
	    for (i = 0; i < 64; ++i) {
	      var b = key[i] || 0;
	      oKeyPad[i] = 0x5c ^ b;
	      iKeyPad[i] = 0x36 ^ b;
	    }

	    Md5.call(this, sharedMemory);

	    this.update(iKeyPad);
	    this.oKeyPad = oKeyPad;
	    this.inner = true;
	    this.sharedMemory = sharedMemory;
	  }
	  HmacMd5.prototype = new Md5();

	  HmacMd5.prototype.finalize = function () {
	    Md5.prototype.finalize.call(this);
	    if (this.inner) {
	      this.inner = false;
	      var innerHash = this.array();
	      Md5.call(this, this.sharedMemory);
	      this.update(this.oKeyPad);
	      this.update(innerHash);
	      Md5.prototype.finalize.call(this);
	    }
	  };

	  var exports = createMethod();
	  exports.md5 = exports;
	  exports.md5.hmac = createHmacMethod();

	  if (COMMON_JS) {
	    module.exports = exports;
	  } else {
	    /**
	     * @method md5
	     * @description Md5 hash function, export to global in browsers.
	     * @param {String|Array|Uint8Array|ArrayBuffer} message message to hash
	     * @returns {String} md5 hashes
	     * @example
	     * md5(''); // d41d8cd98f00b204e9800998ecf8427e
	     * md5('The quick brown fox jumps over the lazy dog'); // 9e107d9d372bb6826bd81d3542a419d6
	     * md5('The quick brown fox jumps over the lazy dog.'); // e4d909c290d0fb1ca068ffaddf22cbd0
	     *
	     * // It also supports UTF-8 encoding
	     * md5('中文'); // a7bac2239fcdcb3a067903d8077c4a07
	     *
	     * // It also supports byte `Array`, `Uint8Array`, `ArrayBuffer`
	     * md5([]); // d41d8cd98f00b204e9800998ecf8427e
	     * md5(new Uint8Array([])); // d41d8cd98f00b204e9800998ecf8427e
	     */
	    root.md5 = exports;
	  }
	})(); 
} (md5$1));

var md5Exports = md5$1.exports;
var md5 = /*@__PURE__*/getDefaultExportFromCjs(md5Exports);

var _excluded = ["data"],
  _excluded2 = ["data"],
  _excluded3 = ["data"],
  _excluded4 = ["data"];
// import { fetchStream, fetchStream2 } from './fetchEventSourceCustom.js'
var openApiRequest = /*#__PURE__*/function () {
  function openApiRequest(params) {
    _classCallCheck(this, openApiRequest);
    _defineProperty(this, "urlText", 'https://open-ka.mobvoi.com');
    _defineProperty(this, "urlTts", 'https://open.mobvoi.com');
    _defineProperty(this, "AppKey", '31247536ACA2A4911FD5ED8D7A6F3814');
    _defineProperty(this, "AppSecret", 'A1DC85F156B053794686B8FD5F602DC5');
    if (!(params !== null && params !== void 0 && params.AppKey) || !(params !== null && params !== void 0 && params.AppSecret) || !(params !== null && params !== void 0 && params.urlText) || !(params !== null && params !== void 0 && params.urlTts)) ;
    this.AppKey = (params === null || params === void 0 ? void 0 : params.AppKey) || this.AppKey;
    this.AppSecret = (params === null || params === void 0 ? void 0 : params.AppSecret) || this.AppSecret;
    this.urlText = (params === null || params === void 0 ? void 0 : params.urlText) || this.urlText;
    this.urlTts = (params === null || params === void 0 ? void 0 : params.urlTts) || this.urlTts;
  }
  // chat 流式
  _createClass(openApiRequest, [{
    key: "chatStream",
    value: function chatStream(obj) {
      var url = this.urlText + '/api/chat/v2/chat';
      var timestamp = Date.parse(new Date()) / 1000;
      var data = obj.data,
        other = _objectWithoutProperties(obj, _excluded);
      var options = _objectSpread2({
        data: _objectSpread2({
          appkey: this.AppKey,
          timestamp: timestamp + '',
          signature: md5(this.AppKey + '+' + this.AppSecret + '+' + timestamp),
          model: 'uclai-large',
          stream: true
        }, data)
      }, other);
      return chatStreamApi(url, options);

      // fetchStream(url, data, cb)

      // fetchStream2(url, {
      //     data,
      //     onmessage(res){
      //         console.log(res, 111);
      //     },
      //     onclose(){

      //     }
      // })
    }
    // chat 异步阻塞
  }, {
    key: "chatSync",
    value: function chatSync$1(obj) {
      var url = this.urlText + '/api/chat/v2/chat';
      var timestamp = Date.parse(new Date()) / 1000;
      var data = obj.data,
        other = _objectWithoutProperties(obj, _excluded2);
      var options = _objectSpread2({
        data: _objectSpread2({
          appkey: this.AppKey,
          timestamp: timestamp + '',
          signature: md5(this.AppKey + '+' + this.AppSecret + '+' + timestamp),
          model: 'uclai-large',
          stream: false
        }, data)
      }, other);
      return chatSync(url, options);
    }

    // tts流式请求
  }, {
    key: "ttsStream",
    value: function ttsStream$1(obj) {
      var url = this.urlTts + '/api/tts/v1';
      var timestamp = Date.parse(new Date()) / 1000;
      var data = obj.data,
        other = _objectWithoutProperties(obj, _excluded3);
      var options = _objectSpread2({
        data: _objectSpread2({
          appkey: this.AppKey,
          timestamp: timestamp,
          signature: md5(this.AppKey + '+' + this.AppSecret + '+' + timestamp),
          streaming: true
        }, data)
      }, other);
      return ttsStream(url, options);
    }
    // tts异步阻塞
  }, {
    key: "ttsSync",
    value: function ttsSync$1(obj) {
      var url = this.urlTts + '/api/tts/v1';
      var timestamp = Date.parse(new Date()) / 1000;
      var data = obj.data,
        other = _objectWithoutProperties(obj, _excluded4);
      var options = _objectSpread2({
        data: _objectSpread2({
          appkey: this.AppKey,
          timestamp: timestamp,
          signature: md5(this.AppKey + '+' + this.AppSecret + '+' + timestamp),
          streaming: false
        }, data)
      }, other);
      return ttsSync(url, options);
    }
  }], [{
    key: "changeOptionsToParams",
    value: function changeOptionsToParams(paras) {}
  }]);
  return openApiRequest;
}();

export { openApiRequest as default };
