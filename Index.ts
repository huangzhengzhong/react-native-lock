import { NativeModules } from 'react-native';

const { RNLock } = NativeModules;

/**
 * 开启锁控串口
 *
 * @param pathName 串口地址
 * @param baudRate 波特率
 * @return 成功返回 success
 */
const openSerialPort = async (pathName:string,baudRate:number) : Promise<string> => {
  return await RNLock.openSerialPort(pathName,baudRate);
}

/**
 * 启动串口服务 用于处理串口的指令收发
 * @param port 本地端口
 * @return 成功返回 success
 */
const startSerialPortServer = async (port:number) : Promise<string> => {
  return await RNLock.startSerialPortServer(port);
};

/**
 * 启动串口响应数据推送服务 用于将收到的串口响应数据推送给服务端
 *
 * @param serverIP   服务端地址
 * @param serverPort 服务端端口
 * @return 成功返回 success
 */
const startSendServer = async (serverIP:string,serverPort:number) : Promise<string> => {
  return await RNLock.startSendServer(serverIP,serverPort);
};

/**
 * 获取串口地址列表
 */
const list = async ():Promise<Array<string>> =>{
  return await RNLock.list();
};

export {
  openSerialPort,
  list,
  startSerialPortServer,
  startSendServer
}
