package com.itcv.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class HeartBeatServer {
	
  private int port;
  
  public HeartBeatServer(int port){
	  this.port = port;
  }
  
  public void start(){
	  EventLoopGroup bossGroup = new NioEventLoopGroup();
	  EventLoopGroup workerGroup = new NioEventLoopGroup();
	  try {
          ServerBootstrap sbs = new ServerBootstrap().group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).
        		  localAddress(new InetSocketAddress(port)).childHandler(
        				  new ChannelInitializer<SocketChannel>() {
                      
                      private static final int READ_IDEL_TIME_OUT = 5; // 读超时
                      private static final int WRITE_IDEL_TIME_OUT = 0;// 写超时
                      private static final int ALL_IDEL_TIME_OUT = 0; // 所有超时
                      
                      protected void initChannel(SocketChannel ch) throws Exception {
                          ch.pipeline().addLast(new IdleStateHandler(READ_IDEL_TIME_OUT,
                                  WRITE_IDEL_TIME_OUT, ALL_IDEL_TIME_OUT, TimeUnit.SECONDS));
                          ch.pipeline().addLast("decoder", new StringDecoder());
                          ch.pipeline().addLast("encoder", new StringEncoder());
                          ch.pipeline().addLast(new HeartBeatHandler());
                      };
                      
                  }).option(ChannelOption.SO_BACKLOG, 128)   
                  .childOption(ChannelOption.SO_KEEPALIVE, true);
           // 绑定端口，开始接收进来的连接
           ChannelFuture future = sbs.bind(port).sync();  
           
           System.out.println("Server start listen at " + port );
           future.channel().closeFuture().sync();
      } catch (Exception e) {
          bossGroup.shutdownGracefully();
          workerGroup.shutdownGracefully();
      }
  }
  
  public static void main(String[] args) {
	 
	  new HeartBeatServer(8090).start();
	  
}
	
}
