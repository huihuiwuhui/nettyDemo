package com.itcv.nio.client;

public class TimeClient {
  public static void main(String[] args) {
	  new Thread(new TimeClientHandler("127.0.0.1",8080),"TimeClient-001").start();
}
}
