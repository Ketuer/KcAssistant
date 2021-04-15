# KcAssistant 教务系统助手
你可以将此项目和 [BaiAssistant 百叶计划助手](https://github.com/Ketuer/BaiAssistant) 配合使用。</br>
本项目与BaiAssistant相同，同样是通过接口直接操作教务账号，包括成绩查询、学籍信息查询、课表查询、选修课报名等操作。</br>

## 简介 🍎
* 此项目基于Java开发，支持跨平台使用
* 电子科技大学成都学院教务系统助手，支持通过接口直接操作教务系统账号
* 本项目遵循GPLv2协议，仅供学习交流使用，严禁用于商业用途
* 如果喜欢本项目，请点个star⭐️

## 版本历史 ⚒
* ### 1.0 - Release
    * 基本框架搭建完成
    * 支持账号的登陆、退出操作（破教务甚至连重置密码都有问题）
    * 支持查询学期成绩、方案成绩
* ### 新版本正在开发中...

## Java开发者
开发者wiki待完善。

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
    <version>1.0-Release</version>
  </dependency>
</dependencies>
```

### 快速开始 🔫
登陆账号并获取用户信息，查询学期成绩和方案成绩：
```java
public class Main {
    public static void main(String[] args)  {
        //登陆账号，请不要使用new，而是使用create方法创建
        KcAccount account = KcAccount.create("2014564546", "我是密码");

        //进行登陆并判断是否登陆成功
        if(account.login()){
            //获取个人学籍信息
            System.out.println(account.getUserDetail());

            //获取学期成绩列表（包含学期成绩和）
            ScoreList scoreList = account.getScore();
            //将及格成绩分学期打印
            scoreList.forEach((k, v) -> System.out.println("学期："+k+" -> 成绩列表："+v));
            //不及格成绩单独存在另一个List中
            System.out.println("不及格成绩："+scoreList.getFailedScore());
        }else {
            System.out.println("登陆失败！");
        }
    }
}
```
