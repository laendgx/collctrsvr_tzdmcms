package com.boco.tzdm.constant;

/**
 * 常量类
 * @author dgx
 *
 */
public class DriverConst {
    /**
     * 数据帧的报文头
     */
    public static final byte Const_FrameHead = 0x02;
    /**
     * 数据帧的报文尾
     */
    public static final byte Const_FrameTail = 0x03;
    /**
     * 数据帧中的转义
     */
    public static final byte Const_FrameTrans = 0x1B;
    /**
     * 发送超时时间
     */
    public static final int Const_FrameSendTimeOut = 7;
    /**
     * 等待应答次数
     */
    public static final int Const_Timer_WaitAnswer = 3;
    /**
     * 定期检查物理通道是否连接的命令
     */
    public static final int Const_Cmd_Check = 1;
    /**
     * 播放列表
     */
    public static final int Const_Cmd_PlayLst = 39;
    /**
     * 显示指定播放表
     */
    public static final int Const_Cmd_DisPlayLst = 47;
    /**
     * 控制开关
     */
    public static final int Const_Cmd_ScrOnOff = 11;
    /**
     * 读取CMS当前播放表信息
     */
    public static final int Const_Cmd_ReadCms = 57;
    /**
     * 控制屏体亮度
     */
    public static final int Const_Cmd_Bright = 23;
    /**
     * 正应答标志'0'
     */
    public static final int Const_Answer_Success = 0x31;
    /**
     * 情报板重启命令
     */
    public static final int Const_Cmd_Restart = 19;
}
