# 多线程上传文件到ftp
　　使用多线程跑数据
#　使用方式

## 添加配置
`````
   在启动的地方　加上配置：
   @ComponentScan(value = {"org.dsg"})
`````

## application.properties  配置
`````````
   spring.client.fileName=
   spring.client.fileDataCount=300
   spring.client.dataSplit=\\001
   spring.client.statSplit=|
   spring.client.tmpdir=/tmp/test/tab_json001
   spring.client.fileType=java,jar
   spring.client.url=192.168.24.250
   spring.client.port=21
   spring.client.user=ftp1
   spring.client.password=ftp1
   spring.client.path=/dsg/tab0130
   spring.client.excludeFields=HIS_DATE, HIS_TIME, HIS_LOGIN, HIS_ORDER_ITEM_ID, HIS_TYPE
   spring.client.excludePartitionFeild=true
`````````

# 使用方式
````````````
@Service
public class PutToFtpRunnerTest {

    @Autowired
    private PutToFtpRunner putToFtpRunner;
}

````````````