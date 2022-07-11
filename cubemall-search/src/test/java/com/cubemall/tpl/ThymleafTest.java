package com.cubemall.tpl;

import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.FileWriter;

public class ThymleafTest {

    @Test
    public void testThymeleaf() throws Exception{
        //创建基于classpath的加载器
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        //设置加载器属性,前缀和后缀
        templateResolver.setPrefix("templates");
        templateResolver.setSuffix(".html");
        //创建模板引擎
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        //创建Context对象(一个map),向模板传递数据使用
        Context context = new Context();
        //向context对象中添加模板使用的变量
        context.setVariable("hello", "hello world!");
        context.setVariable("html", "<h2>this is a html</h2>");
        //渲染模板,模板所在的位置,使用的context对象,静态文件生成的路径
        FileWriter writer =  new FileWriter("D:/temp/hello.html");
        engine.process("你好!", context, writer);

    }
}
