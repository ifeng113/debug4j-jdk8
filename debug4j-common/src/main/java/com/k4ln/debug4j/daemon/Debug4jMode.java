package com.k4ln.debug4j.daemon;

public enum Debug4jMode {

    process, // 进程模式

    thread,  // 线程模式（不支持jdwp）

    mix,     // 混合模式（仅jdwp模式下，新开临时进程接管通信）

    mix_jdwp;     // 混合模式（jdwp代理节点）
}
