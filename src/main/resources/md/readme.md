### mark1
> Debug4jAttach

SocketServer发送Attach命令后，在core模块需要进行以下4步操作：
- SocketClient接受处理指令
- 获取用户进程的Instrumentation
- 执行Attach逻辑
- Attach后置操作

二实现以上步骤的有两种方式：一种是进程模式（process），一种是线程模式（thread）

- process：通过新开进程，创建一个完全独立的进行与用户进程进行交互，相对更少侵入、耦合性低、可操作灵活度高（支持自定义agent.jar），但复杂度高，需占用主机额外的进程及内存开销
- thread：通过新开线程，创建一个依赖于用户进程的线程，相对更多侵入、耦合性、同时会占用消耗用户进程的算力与存储，且不支持jdwp远程调试，但程序逻辑更加简单、纯粹

两者核心差异主要是：
- process模式需要通过虚拟机attach的方式进行agent加载，并在agent中获取用户进程的Instrumentation，然后再执行attach逻辑，而要实现这一套逻辑，需要packing+boot+agent三个额外模块的支持
- thread模式可以在启动时直接获取Instrumentation，当收到执行后可直接获取并执行attach逻辑，但由于jdwp会阻塞进程，因此线程模式不支持jdwp【注意】


__【混合模式】：仅使用jdwp远程调试功能时，新开进程接管jdwp代理__


---

### step1

`
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar .\debug4j-demo1-1.0-SNAPSHOT-all.jar
`

> **_IDEA 开启远程调试_**
![1.png](static/1.png)

### step2
> 调试：*Agent.premain()*

`
java -javaagent:E:\JavaSpace\ksiu\debug4j\debug4j-agent\build\libs\debug4j-agent-1.0-SNAPSHOT-all.jar -jar .\debug4j-demo1-1.0-SNAPSHOT-all.jar
`

---

> 调试：*Agent.agentmain()* 

方式一：
```text
本地启动Demo1Main，再启动AgentMain，在Demo1Main的控制台就能看到attach日志
```


方式二：
> _需放开Demo1Main中"VirtualMachine.attach"相关代码（此时是attach自身）_

![2.jpg](static/2.jpg)

方式三：
> _需放开Demo1Main中"VirtualMachine.attach"相关代码（此时是attach自身）_

IDEA Terminal启动失败（_错误: 找不到或无法加载主类 .attach.allowAttachSelf=true_），__使用【cmd】命令启动：__

`
java -Djdk.attach.allowAttachSelf=true -jar .\debug4j-demo1-1.0-SNAPSHOT-all.jar
`

### step3

__*rollback：*__ 无法通过Attach API动态加载jdwp，调整方案为：手动配置远程调试jvm启动参数，agent仅作agent端


