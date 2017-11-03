package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;


import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class xwriterServerChannel extends ChannelInitializer {

    public void initChannel(Channel channel){
        channel.pipeline().addLast("serverHandler", new xwriterServerHandler());
    }

}
