package com.itcv.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
     public static void main(String[] args) {
		logger.debug("开始启动服务");
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // 
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					 ch.pipeline().addLast(new DiscardServerHandler());
				}
				
			}).option(ChannelOption.SO_BACKLOG, 128)          // (5)
			.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
			
      // Bind and start to accept incoming connections.
      ChannelFuture f = b.bind(8099).sync(); // (7)

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
      f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
		}
		
		
	}
     
    
}

class DiscardServerHandler extends ChannelHandlerAdapter{
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        // Discard the received data silently.
        ((ByteBuf) msg).release(); // (3)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        cause.printStackTrace();
        ctx.close();
    }
	 
}
