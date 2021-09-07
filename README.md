# KcAssistant 教务系统助手
现已对接到新教务，欢迎使用！
***
你可以将此项目和 [BaiAssistant 百叶计划助手](https://github.com/Ketuer/BaiAssistant) 配合使用。</br>
本项目与BaiAssistant相同，同样是通过接口直接操作教务账号，包括成绩查询、学籍信息查询、课表查询、选修课报名等操作。</br>

## 简介 🍎
* 此项目基于Java开发，支持跨平台使用
* 电子科技大学成都学院教务系统助手，支持通过接口直接操作教务系统账号
* 本项目遵循GPLv2协议，仅供学习交流使用，严禁用于商业用途
* 如果喜欢本项目，请点个star⭐️

## 版本历史 ⚒
* ### 2.0.5 - Release
  * 现在支持选修课程查询，以及快速进行报名（抢课）
  * 某些年级无法正常获取课程表 
  * 当成绩出现挂科时，无法正确获取成绩
* ### 2.0.0 - Release
  * 对接全新的教务系统。
  * 由于对接了新的教务系统，使用上可能会有一些不同。
  * 快速开始部分教程已更新~
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

_暂无问题发现_

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
    <version>2.0.0-Release</version>
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

        account.login();  //如果登陆失败会抛出异常！
        System.out.println(account.getInfo());  //默认以JSON形式打印用户数据
        account.logout();   //最好还是退出登陆！
    }
}
```
获取用户的班级课程表信息
```java
public class Main {
  public static void main(String[] args) {
    //登陆账号
    KcAccount account = KcAccount.create("2014564546", "我是密码");
    account.login();  //如果登陆失败会抛出异常！
    account
            .getClassTable(1)    //获取第一学期的课程表
            .getClassInOneDay(1)      //仅获取周一的课程
            .forEach(System.out::println);  //打印所有周一的课程
    account.logout();   //最好还是退出登陆！
  }
}
```
