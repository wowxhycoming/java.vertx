1. Entity Bean : Todo  实体类  
@DataObject ， 是用于生成 JSON 转换类的注解。  
被 @DataObject 注解的实体类需要满足以下条件：拥有一个拷贝构造函数以及一个接受一个JsonObject 对象的构造函数。  
利用Vert.x Codegen来自动生成JSON转换类。我们需要在build.gradle中添加依赖。同时也需要 package-info.java 文件。
利用 maven 在编译期间生成转换类。



