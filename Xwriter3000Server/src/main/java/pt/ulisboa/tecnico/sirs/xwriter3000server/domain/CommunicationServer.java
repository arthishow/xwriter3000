package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunicationServer {

    private int port;

    private List<ActiveUser> activeUsers = Collections.synchronizedList(new ArrayList<ActiveUser>());

    public CommunicationServer(int port){
        this.port = port;
    }


    public void run() throws InterruptedException{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap writerServer = new ServerBootstrap();
            writerServer.group(bossGroup, workerGroup);
            writerServer.channel(NioServerSocketChannel.class);
            writerServer.childHandler(new xwriterServerChannel());

            ChannelFuture future = writerServer.bind(port).sync();

            future.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void receiveBookChanges(String book, String sessionID){

    }

    public String sendBook(String bookID, String sessionID){
        for (ActiveUser activeUser : activeUsers){
            if(sessionID == activeUser.getSessionID()){
                return "";
            }
        }
        return "Fail";
    }

    public void authentificateUser(){

    }

    public void forwardSymKey(){

    }
}
