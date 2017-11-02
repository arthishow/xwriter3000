package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComunicationServer {

    private int port;

    private List activeUsers = Collections.synchronizedList(new ArrayList<ActiveUser>());

    public ComunicationServer(int port){
        this.port = port;
    }


    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap writerServer = new ServerBootstrap();
            writerServer.group(bossGroup, workerGroup);
            writerServer.channel(writerServerChannel.class);

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
