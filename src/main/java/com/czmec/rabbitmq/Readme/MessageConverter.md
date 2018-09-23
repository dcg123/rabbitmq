- MessageConverter消息转化器（能在执行委派着方法之前对消息进行转换）  
- 自定义转换器:MessageConverter,一般来讲都需要实现这个接口
- 从写toMessage和fromMessage两个方法
- Json转换器:Jackson2JsonMessageConverter(可以进行java对象的转换功能)
- DefaultJackson2JavaTypeMapper映射器：可以进行java对象的映射关心
- 自定义二进制转换器:比如图片类型，PDF,PPT，流媒体

json消息转换  
``` 
     在委派方法中设置转换器
     Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
     adapter.setMessageConverter(jackson2JsonMessageConverter);
``` 


[测试代码](/src/test/java/com/czmec/rabbitmq/RabbitmqApiApplicationTests) 


``` 
测试代码
testSendJsonMessage()
``` 

java对象转换

``` 
主要代码：
Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
``` 

``` 
测试代码
testSendJavaMessage()
//使用默认的java转换映射器需要设置key必须为__TypeId__（可以看下源码）通过这个key去找到需要映射的实体类
		messageProperties.getHeaders().put("__TypeId__", "com.czmec.rabbitmq.entity.Order");
``` 

java对象多映射转换
``` 
主要代码
DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
         Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
         //根据设置的key 去转换成对应的实体类对象
         idClassMapping.put("order", com.czmec.rabbitmq.entity.Order.class);
         idClassMapping.put("packaged", com.czmec.rabbitmq.entity.Packaged.class);
         javaTypeMapper.setIdClassMapping(idClassMapping);
``` 

``` 
测试代码
testSendMappingMessage()方法
主要代码
MessageProperties messageProperties1 = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties1.setContentType("application/json");
		//需要设置key和value(key为__TypeId__) 转换器会根据key的value去需要上面idClassMapping中对应的实体类
		messageProperties1.getHeaders().put("__TypeId__", "order");
``` 

全局转换器 支持多种类型(自定义转换器需要实现MessageConverter接口)
``` 
主要代码
//全局的转换器:
        ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textConvert = new TextMessageConverter();
        convert.addDelegate("text", textConvert);
        convert.addDelegate("html/text", textConvert);
        convert.addDelegate("xml/text", textConvert);
        convert.addDelegate("text/plain", textConvert);
        还有支持其他类型
        
        //实现MessageConverter接口  对消息进行操作后返回给委派方法
        @Override
        	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        		throw new MessageConversionException(" convert error ! ");
        	}
        
        	@Override
        	public Object fromMessage(Message message) throws MessageConversionException {
        		System.err.println("-----------Image MessageConverter----------");
        		
        		Object _extName = message.getMessageProperties().getHeaders().get("extName");
        		String extName = _extName == null ? "png" : _extName.toString();
        		
        		byte[] body = message.getBody();
        		String fileName = UUID.randomUUID().toString();
        		String path = "d:/010_test/" + fileName + "." + extName;
        		File f = new File(path);
        		try {
        			Files.copy(new ByteArrayInputStream(body), f.toPath());
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		return f;
        	}
``` 

``` 
测试代码
testSendExtConverterMessage()方法
``` 
