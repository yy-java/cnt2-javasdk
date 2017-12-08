Java sdk for cnt2
----

- [x] 注册到etcd & ConfigCenter
- [x] 加载远端配置到本地副本
- [x] 监听配置节点变化 & 更新本地副本 & 通知业务
- [x] 断线重连（Watch & KeepAlive）
- [x] 启动失败自动加载本地文件
- [x] 支持从etcd发现ConfigServer service
- [x] jetcd支持jdk7(移除模块cnt2-jetcd)
- [x] 增加recipes(从之前的cnt-service中复制过来) 
- [ ] 优化NameResolver & LoadBalancer


模块说明
----

1. **cnt2-sdk** : `ConfigCenter V2版本SDK，和V1版本功能差不多但不兼容V1，只不过实现改为etcd + grpc`
1. **cnt2-demo**: `ConfigCenter V2版本SDK demo`
1. **cnt-api**: `ConfigCenter的API接口定义（本接口不兼容V1版本,从V2版本开始需要实现这个接口），将接口定义单独抽出来方便后面拓展`
1. **cnt-recipes**: `ConfigCenter一些常用工具，实现了连接池动态配置，redis动态配置以及spring注解配置等`

Quick Start
----
#### 引入maven依赖
		<dependency>
			<groupId>com.yy</groupId>
			<artifactId>cnt2-sdk</artifactId>
			<version>1.0-SNAPSHOT</version>
		</dependency>
		
		<!-- 使用spring注解或者其他工具才需要 -->
		<dependency>
			<groupId>com.yy</groupId>
			<artifactId>cnt-recipes</artifactId>
			<version>1.0-SNAPSHOT</version>
		</dependency>

#### 初始化配置
  	<bean id="cnt2Service" class="com.yy.cnt2.Cnt2Service" destroy-method="close">
  		<!-- 对应配置中心的业务标记 -->
		<constructor-arg index="0" value="${cnt2.appName}" />
		<!-- 对应配置中心的环境 -->
		<constructor-arg index="1" value="${cnt2.profile}" />
		<!-- 本地存储配置文件的目录，潜龙发布项一般使用/data/file/$潜龙项目$ -->
		<constructor-arg index="2" value="${cnt2.localFilePath}" />
		<!-- etcd集群地址  -->
		<constructor-arg index="3" value="${cnt2.etcdEndpoints}" />
		<constructor-arg index="4" value="${cnt2.etcdLeaseTtl}" />
	</bean>
	
#### 加载监听配置
    //配置中心的对应配置项
    private static final String MY_CONFIG_KEY = "my_config_1";

    @Autowired
    private Cnt2Service cnt2Service;

    //本地属性
    private int myConfig1;

    public int getMyConfig1() {
        return myConfig1;
    }

    public void setMyConfig1(int myConfig1) {
        this.myConfig1 = myConfig1;
    }

    @PostConstruct
    public void init() {
        //初始化时从本地文件加载配配置
        setMyConfig1(Integer.parseInt(cnt2Service.getValue(MY_CONFIG_KEY, "-1")));
        
        //注册监听配置修改事件处理器
        cnt2Service.registerEventHandler(new AbstractEventHandler() {
            @Override
            public String getKey() {
                return MY_CONFIG_KEY;
            }

            @Override
            //修改配置事件
            public void handlePutEvent(String key, String value) {
                System.out.println("Inner Key : " + key + " Value :" + value);
                //跟新Bean属性
                setMyConfig1(Integer.parseInt(value));
            }
            
            //删除配置事件
            public void handleDeleteEvent(String key, String value) {
                System.out.println("Inner Key : " + key + " Value :" + value);
                //跟新Bean属性为默认值
                setMyConfig1(-1);
            }
        });

    }

#### Spring注解方式加载监听配置
    //配置中心的对应配置项
    private static final String MY_CONFIG_KEY = "my_config_2";
    
    @Autowired
    private Cnt2Service cnt2Service;

    /**
     *绑定bean属性跟配置中心的对应配置项，监听配置中心对应配置项的发布事件
     *简单类型（不需要对配置值做二次处理的）采用{@link AutoValue}}注解绑定，复杂类型采用{@link RegisterEventHandler}注解绑定
     */
    @AutoValue(propertyKey = MY_CONFIG_KEY, controlCenterService = "cnt2Service", defaultValue = "default")
    private String myConfig2;

    /**
     * 
     * 监听配置中心对应配置项的发布事件
     * 当需要对配置值进行二次处理，比如分割字符串或者json反系列化时采用{@link RegisterEventHandler}注解绑定
     * 
     * @param key 配置中心配置项名称
     * @param value 配置中心配置项对应的值，修改后的，如果是null，表示该配置项被删除
     */
    @RegisterEventHandler(propertyKey = MY_CONFIG_KEY, controlCenterService = "cnt2Service", initializing = true)
    public void handleUpdate(String key, String value) {
        System.out.println("RegisterEventHandler Key : " + key + " Value :" + value);
    }


 