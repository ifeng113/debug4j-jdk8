package com.k4ln.debug4j.core.client;

import com.alibaba.fastjson2.JSON;
import com.k4ln.debug4j.core.proxy.SocketTFProxyClient;
import com.k4ln.debug4j.protocol.command.Command;
import com.k4ln.debug4j.protocol.command.CommandTypeEnum;
import com.k4ln.debug4j.protocol.command.message.CommandInfoMessage;
import com.k4ln.debug4j.protocol.command.message.CommandLogMessage;
import com.k4ln.debug4j.protocol.command.message.CommandProxyMessage;
import com.k4ln.debug4j.protocol.socket.ProtocolTypeEnum;
import com.k4ln.debug4j.protocol.socket.SocketProtocol;
import com.k4ln.debug4j.protocol.socket.SocketProtocolDecoder;
import com.k4ln.debug4j.utils.SocketProtocolUtil;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.LoaderClassPath;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.k4ln.debug4j.core.Debugger.instrumentation;

@Slf4j
public class SocketClient {

    private final String key;

    private final String host;

    private final Integer port;

    private final CommandInfoMessage commandInfoMessage;

    public SocketClient(String key, String host, Integer port, CommandInfoMessage commandInfoMessage) {
        this.key = key;
        this.host = host;
        this.port = port;
        this.commandInfoMessage = commandInfoMessage;
    }

    @Getter
    static AioSession session;

    /**
     * realClientId -> targetClient
     */
    static final Map<Integer, SocketTFProxyClient> targetClients = new ConcurrentHashMap<>();

    /**
     * 是否还存活
     * @return
     */
    public boolean isAlive() {
        return session != null && !session.isInvalid();
    }

    public void shutdown(){
        if (session != null) {
            session.close();
        }
    }

    /**
     * 开启socket客户端
     * @throws IOException
     */
    public void start() throws IOException {

        targetClients.clear();

        MessageProcessor<SocketProtocol> processor =new AbstractMessageProcessor<>() {

            @Override
            public void process0(AioSession session, SocketProtocol socketProtocol) {
                switch (socketProtocol.getProtocolType()) {
                    case COMMAND -> handCommand(socketProtocol);
                    case PROXY -> {
                        Integer clientId = socketProtocol.getClientId();
                        if (targetClients.containsKey(clientId)){
                            targetClients.get(clientId).sendMessage(socketProtocol.getBody());
                        } else {
                            log.warn("socketClient proxy no clientId:{}", clientId);
                        }
                    }
                }
            }

            private static void handCommand(SocketProtocol socketProtocol) {
                Command command = JSON.parseObject(new String(socketProtocol.getBody()), Command.class);
                if (command.getCommand().equals(CommandTypeEnum.LOG)){
                    log.info(JSON.parseObject(JSON.toJSONString(command.getData()), CommandLogMessage.class).getContent());

                    // 延迟5秒
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    instrumentation.addTransformer(new ClassFileTransformer() {
                        @Override
                        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                                ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                            try {
                                ClassPool pool = ClassPool.getDefault();
                                pool.insertClassPath(new LoaderClassPath(loader));
                                if (!className.contains("com/k4ln/demo/Demo1Main")) {
                                    return classfileBuffer;
                                }
                                CtClass cc = pool.get(className.replace("/", "."));
                                CtBehavior[] methods = cc.getDeclaredBehaviors();
                                for (CtBehavior method : methods) {
                                    if (!method.isEmpty()){
                                        String methodName = method.getName();
                                        method.addLocalVariable("start", CtClass.longType);
                                        method.insertBefore("start = System.currentTimeMillis();");
                                        method.insertAfter( String.format("System.out.println(\"%s cost: \" + (System.currentTimeMillis() - start) + \"ms\");", methodName) );
                                    }
                                }
                                return cc.toBytecode();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return classfileBuffer;
                        }
                    }, true);
                    for (Class allLoadedClass : instrumentation.getAllLoadedClasses()) {
                        if (allLoadedClass.getName().startsWith("com.k4ln.demo")){
                            if (allLoadedClass.getName().equals("com.k4ln.demo.Demo1Main")){
                                try {
                                    instrumentation.retransformClasses(allLoadedClass);
                                } catch (UnmodifiableClassException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } else if (command.getCommand().equals(CommandTypeEnum.PROXY_OPEN)){
                    CommandProxyMessage proxyMessage = JSON.parseObject(JSON.toJSONString(command.getData()), CommandProxyMessage.class);
                    try {
                        SocketTFProxyClient tfProxyClient = new SocketTFProxyClient();
                        targetClients.put(socketProtocol.getClientId(), tfProxyClient);
                        tfProxyClient.start(socketProtocol.getClientId(), proxyMessage.getHost(), proxyMessage.getPort());
                        log.info("socketClient proxy started successfully clientId:{}", socketProtocol.getClientId());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (command.getCommand().equals(CommandTypeEnum.PROXY_CLOSE)){
                    if (targetClients.containsKey(socketProtocol.getClientId())){
                        SocketTFProxyClient tfProxyClient = targetClients.get(socketProtocol.getClientId());
                        if (tfProxyClient != null && !tfProxyClient.getSession().isInvalid()){
                            tfProxyClient.getSession().close();
                        }
                        targetClients.remove(socketProtocol.getClientId());
                        log.info("socketClient proxy closed successfully clientId:{}", socketProtocol.getClientId());
                    } else {
                        log.warn("socketClient command no clientId:{}", socketProtocol.getClientId());
                    }
                }
            }

            @Override
            public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
                if (stateMachineEnum.equals(StateMachineEnum.NEW_SESSION)) {
                    SocketProtocolUtil.sendMessage(session, 0, ProtocolTypeEnum.AUTH, key.getBytes());
                    SocketProtocolUtil.sendMessage(session, 0, ProtocolTypeEnum.COMMAND,
                            JSON.toJSONString(Command.builder().command(CommandTypeEnum.INFO).data(commandInfoMessage).build()).getBytes());
                    log.info("socket client connected");
                } else if (stateMachineEnum.equals(StateMachineEnum.SESSION_CLOSED)) {
                    log.info("socket client disConnected");
                } else if (throwable != null) {
                    throwable.printStackTrace();
                }
            }
        };
        AioQuickClient client = new AioQuickClient(host, port, new SocketProtocolDecoder(), processor);
        client.setReadBufferSize(SocketProtocolUtil.READ_BUFFER_SIZE);
        session = client.start();
    }

    public static void callbackMessage(Integer clientId, byte[] body){
        SocketProtocolUtil.sendMessage(session, clientId, ProtocolTypeEnum.PROXY, body);
    }

    public static void clientClose(Integer clientId){
        targetClients.remove(clientId);
        SocketProtocolUtil.sendMessage(session, clientId, ProtocolTypeEnum.COMMAND,
                CommandProxyMessage.buildCommandProxyMessage(CommandTypeEnum.PROXY_CLOSE, null, null));
        log.info("socketClient proxy target closed clientId:{}", clientId);
    }
}
