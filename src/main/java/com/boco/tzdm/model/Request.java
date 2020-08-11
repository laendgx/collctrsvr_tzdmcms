package com.boco.tzdm.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Request {
	/**
	 * 设备变量的索引
	 */
	private String index;
	/**
	 * 目的地址
	 */
	private int targetAddr;
	/**
	 * 源地址
	 */
	private int orginAddr;

	/**
	 * 帧类型
	 */
	private int frameType;

	/**
	 * 数据内容
	 */
	private byte[] data;

	/**
	 * @return the 设备变量的索引
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * @param //设备变量的索引
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * @return the 目的地址
	 */
	public int getTargetAddr() {
		return targetAddr;
	}

	/**
	 * @param //目的地址 the targetAddr to set
	 */
	public void setTargetAddr(int targetAddr) {
		this.targetAddr = targetAddr;
	}

	/**
	 * @return the 源地址
	 */
	public int getOrginAddr() {
		return orginAddr;
	}

	/**
	 * @param //源地址 the orginAddr to set
	 */
	public void setOrginAddr(int orginAddr) {
		this.orginAddr = orginAddr;
	}

	/**
	 * @return the 帧类型
	 */
	public int getFrameType() {
		return frameType;
	}

	/**
	 * @param //帧类型 the frameType to set
	 */
	public void setFrameType(int frameType) {
		this.frameType = frameType;
	}

	/**
	 * @return the 数据内容
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param //数据内容 the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	//构造方法
	public Request(int targetAddr, int orginAddr, int frameType, byte[] data) {
		super();
		this.targetAddr = targetAddr;
		this.orginAddr = orginAddr;
		this.frameType = frameType;
		this.data = data;
	}

	//无参构造方法
	public Request() {
		super();
	}

	/**
	 * 获取目的地址、源地址、帧类型、发送数据三方编码后的数据
	 * @return
	 */
	public byte[] getEncoderData(){
		String target = String.format("%02d", this.targetAddr);
		String orgin = String.format("%02d", this.orginAddr);
		String frameType = String.format("%02d", this.frameType);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(target.getBytes("GBK"));
			baos.write(orgin.getBytes("GBK"));
			baos.write(frameType.getBytes("GBK"));
			baos.write(data);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return baos.toByteArray();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return "Request [targetAddr=" + targetAddr + ", orginAddr="
					+ this.orginAddr + ", frameType=" + frameType + ", data="
					+ new String(data, "GBK") + "]";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
