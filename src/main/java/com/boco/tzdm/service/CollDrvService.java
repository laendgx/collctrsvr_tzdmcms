package com.boco.tzdm.service;

import com.boco.tzdm.commconfig.devvarinfoconfig.DevVarCollInfo;
import com.boco.tzdm.commconfig.devvarinfoconfig.DevVarCollInfoDataServiceImpl;
import com.boco.tzdm.commconfig.devcommconfig.DevcommInfo;
import com.boco.tzdm.commconfig.devcommconfig.DevcommInfoDataServiceImpl;
import com.boco.tzdm.commrabbitmq.CommonSendQueueListener;
import com.boco.tzdm.constant.DriverConst;
import com.boco.tzdm.driver.TCommPortInfo;
import com.boco.tzdm.driver.TDriver;
import com.boco.tzdm.driver.TDriverList;
import com.boco.tzdm.driver.TDriverVarInfo;
import com.boco.tzdm.model.Request;
import com.boco.tzdm.tcpcomm.IMessageListener;
import com.boco.tzdm.tcpcomm.IPortStatusListener;
import com.boco.tzdm.tcpcomm.TTCPComm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务启动
 * @author dgx
 *
 */
@Service("CollDrvService")
public class CollDrvService implements InitializingBean,DisposableBean {
    private static final Logger logger= LoggerFactory.getLogger(CommonSendQueueListener.class);

    @Autowired
    private Environment env;
    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    //设备通讯端口devCommConfig.xml接口信息
    @Resource(name="devcommInfoDataServiceImpl")
    private DevcommInfoDataServiceImpl devcommInfoDataServiceImpl;

    //设备通讯端口devVarListConfig.xml接口信息
    @Resource(name="devVarCollInfoDataServiceImpl")
    private DevVarCollInfoDataServiceImpl devVarCollInfoDataServiceImpl;

    /**
     * 盛放通讯端口对象的线程池
     */
    private ExecutorService tcpThreadPool;
    /**
     * 通信对象
     */
    public TTCPComm ftcpComm;

    /**
     * 通讯端口状态监听器
     */
    public IPortStatusListener portStatusListener;
    /**
     * 数据上传监听器
     */
    public IMessageListener messageListener;

    /**
     * 定时器线程
     */
    private ScheduledExecutorService scheduledService;

    /**
     * 初始化线程池 测试tcp通讯
     */
    private void initExecutorService() throws Exception {
        //获取设备通讯信息
        DevcommInfo DevcommInfotemp=devcommInfoDataServiceImpl.getCurDevcommInfo("21210001");
        if(!DevcommInfotemp.getDevId().equals(null)) {
            String devip = DevcommInfotemp.getDevIp();
            String devport = DevcommInfotemp.getDevPort();

            ftcpComm = new TTCPComm(devip, Integer.valueOf(devport), portStatusListener, messageListener);
        }

        //初始化线程池
        tcpThreadPool = Executors.newFixedThreadPool(10);
        tcpThreadPool.execute(ftcpComm);

        //初始化定时器线程
        scheduledService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * 初始化设备通讯信息
     */
    private void initDriver(){
        try {
            //List<TDriverVarInfo> devDriverVarInfo= new ArrayList<>();//Arrays.asList(TDriverList.getDriverVariantInfo());
            List<DevcommInfo> DevcommInfotempList = devcommInfoDataServiceImpl.getDevcommInfoList();

            for (int i=0;i<DevcommInfotempList.size();i++){
                String DevTypeid=DevcommInfotempList.get(i).getDevId().substring(0,4);
                TCommPortInfo tCommPortInfo=new TCommPortInfo();
                tCommPortInfo.setDwDriverId(Integer.valueOf(DevcommInfotempList.get(i).getCommId()));
                tCommPortInfo.setSzDevId(DevcommInfotempList.get(i).getDevId());
                tCommPortInfo.setSzDevIp(DevcommInfotempList.get(i).getDevIp());
                tCommPortInfo.setDwDevPort(Integer.valueOf(DevcommInfotempList.get(i).getDevPort()));
                tCommPortInfo.setSzAddressParam(DevcommInfotempList.get(i).getDevAddr());
                tCommPortInfo.setExchangeName(env.getProperty("exchangeName").toString());
                tCommPortInfo.setSendQueueroutingkey(env.getProperty("sendQueueroutingkey"));

                List<TDriverVarInfo> devDriverVarInfo= new ArrayList<>();
                List<DevVarCollInfo> DevVarCollInfotempList = devVarCollInfoDataServiceImpl.getCurDevVarCollInfo(DevTypeid);
                for (int j=0;j<DevVarCollInfotempList.size();j++) {
                    TDriverVarInfo TDriverVarInfoTemp=new TDriverVarInfo();
                    TDriverVarInfoTemp.setDwdevTypeid(DevVarCollInfotempList.get(j).getDwdevTypeId());
                    TDriverVarInfoTemp.setDwVariantId(DevVarCollInfotempList.get(j).getDwVariantId());
                    TDriverVarInfoTemp.setSzVariantDesc(DevVarCollInfotempList.get(j).getSzVariantDesc());
                    TDriverVarInfoTemp.setDwSendCmd(DevVarCollInfotempList.get(j).getDwSendCmd());
                    TDriverVarInfoTemp.setDwRecvCmd(DevVarCollInfotempList.get(j).getDwRecvCmd());
                    TDriverVarInfoTemp.setSzFuncAbbr(DevVarCollInfotempList.get(j).getSzFuncAbbr());
                    devDriverVarInfo.add(TDriverVarInfoTemp);
                }
                TDriver TDriverDev=new TDriver(tCommPortInfo,rabbitTemplate,devDriverVarInfo); //设备驱动初始化及TDriverList添加（类构造函数中）
            }

        }catch(Exception ex)
        {
            logger.error("数据转发异常"+ex.toString());
        }
    }

    /**
     * 实例初始化完成后触发
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        initDriver();
        TDriverList.getInstance().startDrivers();


//        for (Map.Entry<Integer, TDriver> entry : TDriverList.getInstance().gDriverMap.entrySet()) {
//            TDriver driver = entry.getValue();  //获取设备驱动信息
//            //driver.sendData(1,"999");
//            Integer sdf=driver.FStatus;
//            String sdfasd=entry.getKey().toString();
//            logger.error(sdfasd+"--->"+sdf.toString());
//        }

//        initExecutorService();//tcp通讯测试
      }

    /**
     * 容器关闭时触发
     */
    @Override
    public void destroy() throws Exception {
        try{
            TDriverList.getInstance().stopDrivers();
        }catch(Exception ex){}

        try{
            ftcpComm.shutdown();
        }catch(Exception ex){}

        try{
            tcpThreadPool.shutdownNow();
        }catch(Exception ex){}

        try{
            scheduledService.shutdownNow();
        }catch(Exception ex){}
    }

    /**
     * 超时检查任务
     */
    private Runnable waitTaskRunnable = new Runnable() {
        @Override
        public void run() {
            String str="123";
            Request sdf=new Request(0,0,10,str.getBytes());
           ftcpComm.sendMsg(sdf);
        }
    };
}
