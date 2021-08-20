# KcAssistant 教务系统助手
* 由于教务系统升级，本系统即将升级为2.0版本
你可以将此项目和 [BaiAssistant 百叶计划助手](https://github.com/Ketuer/BaiAssistant) 配合使用。</br>
本项目与BaiAssistant相同，同样是通过接口直接操作教务账号，包括成绩查询、学籍信息查询、课表查询、选修课报名等操作。</br>

## 简介 🍎
* 此项目基于Java开发，支持跨平台使用
* 电子科技大学成都学院教务系统助手，支持通过接口直接操作教务系统账号
* 本项目遵循GPLv2协议，仅供学习交流使用，严禁用于商业用途
* 如果喜欢本项目，请点个star⭐️

## 版本历史 ⚒
* ### 1.1.6 - Release
  * 教务外网接口调整至4567
  * 修复某些课程不按分数制导致无法获取的问题
* ### 1.1.5 - Release
  * 课程表查询现在更加智能
  * 可以仅查询必修科目的课程表
  * 可以查询包含选修和必修的课程表
  * 采用映射实现，会折损一些网络性能
* ### 1.1 - Release
    * 新增修改密码方法，可以直接修改密码了
    * 新增课程表数据查询
    * 课程表数据被转换为更加便于操作的实体类
* ### 1.0.5 - Release
    * 保留学期成绩获取，移除方案成绩获取
    * 现在获取学期成绩可以直接获取必修和选修、通过和挂科的成绩了
    * 成绩获取现在采用映射机制，没有评教依然可以查询学期成绩
* ### 1.0 - Release
    * 基本框架搭建完成
    * 支持账号的登陆、退出操作（破教务甚至连重置密码都有问题）
    * 支持查询学期成绩、方案成绩

## Java开发者
开发者wiki待完善。

### 已知问题
分为 系统问题 和 代码问题，系统问题是教务系统本身的问题，代码问题是本项目未完善的问题
* [🚦严重系统问题] 即使在未登录的情况下，直接调用教务重置密码接口能够任意更改其他用户的密码（包括教师和学生的账号），
  本项目提供的是正常使用接口，不提供相关非法操作接口，如有兴趣可以自行实现，通过此接口对学校造成任何损失，后果自负。
* [系统问题] 有时候会调用接口莫名其妙连接超时，重新调用即可，影响不大。
* [系统问题] 在某些网络情况下，无法获取选修课程表内容。

### 添加依赖 🔮
* 直接下载最新的 [KcAssistant-X.X-Release.jar](https://github.com/Ketuer/KcAssistant/releases/) 和 [FastJSON-1.2.76.jar](https://repo1.maven.org/maven2/com/alibaba/fastjson/1.2.76/fastjson-1.2.76.jar) 和 [Jsoup-1.3.1.jar](https://repo1.maven.org/maven2/org/jsoup/jsoup/1.13.1/jsoup-1.13.1.jar) 并导入jar文件作为依赖。
    * [IDEA添加依赖](https://jingyan.baidu.com/article/e2284b2bb82806e2e6118dbf.html)
    * [Eclipse添加依赖](https://jingyan.baidu.com/article/db55b609aa8b1e4ba20a2f4b.html) 
* 或者添加Maven项目依赖：
```html
<repositories>
  <repository>
    <id>crack-mvn-repo</id>
    <url>https://raw.githubusercontent.com/Ketuer/KcAssistant/main/repo</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>crack.cduestc</groupId>
    <artifactId>jw</artifactId>
    <version>1.1.6-Release</version>
  </dependency>
</dependencies>
```

### 快速开始 🔫
登陆账号并获取用户信息，查询学期成绩：
```java
public class Main {
    public static void main(String[] args)  {
        //登陆账号，请不要使用new，而是使用create方法创建
        KcAccount account = KcAccount.create("2014564546", "我是密码");

        //进行登陆并判断是否登陆成功
        if(account.login()){
            //获取个人学籍信息
            System.out.println(account.getUserDetail());

            //获取学期成绩列表（包含选项和必修成绩、也包括挂科和未挂科）
            account.getScore().forEach((k ,v) -> System.out.println(k+" -> ("+v.size()+")"+v));
        }else {
            System.out.println("登陆失败！");
        }
    }
}
```
获取用户的班级课程表信息
```java
public class Main {
  public static void main(String[] args) {
    //登陆账号
    KcAccount account = KcAccount.create("2014564546", "我是密码");
    if(account.login()){
      //获取用户的课程表信息
      account
              .getClassTable(4)  //获取第4个学期的课程表（本科一共8个学期，专科一共6个学期，范围1~8/1~6）
              .forEach((k, v) -> System.out.println("星期"+k+" -> "+v));   //将每天的课程打印到控制台
      //注意，一共7天，每天11节课（按45分钟小课计算），有可能出现第n节课有两门课程的情况，所以是以List方式存储
    }else {
      System.out.println("登陆失败！");
    }
  }
}
```
