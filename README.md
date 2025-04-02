# windows-manager
windows本地服务在线管理，包括IIS和Windows服务

代码地址：

gitee：[https://gitee.com/tuituidan/windows-manager](https://gitee.com/tuituidan/windows-manager)

github：[https://github.com/tuituidan/windows-manager](https://github.com/tuituidan/windows-manager)

### 简介

扩展自【在线IIS管理】一开始只做了IIS在线管理，但考虑很多windows服务也是本地管理，缺少在线管理，所以一并做了，由于命名问题，所以新开了一个仓库。

IIS管理说明见[github](https://github.com/tuituidan/iis-manager)、[gitee](https://gitee.com/tuituidan/iis-manager)

windows服务管理，通过windows命令获取所有windows服务，通过勾选来添加自己需要默认展示的服务，在线操作服务的启停及文件管理

### 功能说明

- 无数据库，IIS管理直接读取IIS所有网站列表进行管理，windows服务由于自带太多，而实际只需要管理自己创建的一些服务，所以将要管理的服务存入一个win-service.ini的配置文件中，配置文件保存后自动生成的。
- 可通过`spring.security.enabled=true`开启简单的登录控制，在`springboot`的`yml`配置中配置账号密码（默认账号密码：admin  / admin123）。
- 从IIS获取的网站列表，可点击地址直接进行跳转访问，默认获取服务器本地IP，但可能受一些代理软件影响，获取到的地址可能不是用户想要的，这种情况可以通过配置`app.local-ip`来配置服务器IP。
- 实现了IIS网站的在线启停，应用程序池的在线启停和回收，网站部署文件的上传删除预览等。
- 实现了windows服务的在线启停及部署文件管理。
- 网站和服务的文件管理通过`codemirror`实现常用文本，如`js`，`css`，`sql`，`html`，`xml`的高亮显示，后续也会考虑实现在线编辑。

### license

100%开源，MIT协议，可自由修改

#### 演示图

IIS在线管理

![IIS在线管理](https://github.com/user-attachments/assets/798cf769-6036-44d5-97f9-71b70315a890)

![IIS在线管理-文件查看](https://github.com/user-attachments/assets/ad1208e1-2eec-44a2-af31-b2c0f8231844)

Windows服务在线管理

![Image](https://github.com/user-attachments/assets/fd0b6757-dee2-4ca7-8ab4-f727d436b6d3)

![Image](https://github.com/user-attachments/assets/8c2cba1f-eeb5-4584-858e-3bd708e77250)

![Image](https://github.com/user-attachments/assets/3619695e-7058-4f76-9d05-523f17cd66c7)
