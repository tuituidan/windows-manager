# windows服务在线管理
<img src="https://img.shields.io/badge/springboot-2.7.4-brightgreen" alt="springboot"/>   <img src="https://img.shields.io/badge/jdk-1.8-blue" alt="java"/>   <img src="https://img.shields.io/badge/vue-2.6.12-blueviolet" alt="vue"/>   <img src="https://img.shields.io/badge/elementui-2.15.14-brown" alt="element-ui"/>   

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
- 从IIS获取的网站列表，可点击地址直接进行跳转访问，默认获取服务器本地IP，但可能受一些代理软件影响，获取到的地址可能不是用户想要的，~~这种情况可以通过配置`app.local-ip`来配置服务器IP~~，从`v1.1.0`版本开始可以在配置页面进行配置。
- 实现了IIS网站的在线启停，应用程序池的在线启停和回收，网站部署文件的上传删除预览等。
- 实现了windows服务的在线启停及部署文件管理。
- 网站和服务的文件管理通过`highlight.js`实现常用文本，如`js`，`css`，`sql`，`html`，`xml`的高亮显示，对于图片、PDF、视频、音频等文件也支持在线预览，具体参考配置页面。

### license

100%开源，MIT协议，可自由修改

### 部署说明

- 直接从[Release](https://github.com/tuituidan/windows-manager/releases)中下载`windows-manager.zip`压缩包，解压后执行`java -jar windows-manager.jar`即可，也可通过执行压缩包中提供的`install.bat`将本服务部署到windows服务中，通过windows服务进行管理。

  > 注意：因为需要执行windows命令获取IIS及windows服务，所以启动命令需要Windows管理员权限，idea进行开发调试时也需要使用管理员权限启动。

- 源码部署：下载源码，执行源码根目录下`bin`下的`package.bat`，该命令打包出一个包含将本服务安装成windows服务的压缩包，将该压缩包拷贝到服务器上解压后执行`install.bat`（管理员权限）后即可像管理windows服务一样管理本服务了

  > 如果不希望通过package.bat执行打包，也要注意本服务是前后端合并部署，需要手动拷贝前端编译文件到后端static文件夹中，再打包后端，具体可参考package.bat中的命令

#### 演示图

IIS在线管理

![IIS在线管理](https://gitee.com/tuituidan/pic/raw/master/2025/20250605210649729.png)

![IIS在线管理-文件查看](https://gitee.com/tuituidan/pic/raw/master/2025/20250605210823225.png)

Windows服务在线管理

![Image](https://gitee.com/tuituidan/pic/raw/master/2025/20250605211048837.png)

![Image](https://gitee.com/tuituidan/pic/raw/master/2025/20250605211137156.png)

![Image](https://gitee.com/tuituidan/pic/raw/master/2025/20250605211254735.png)

![](https://gitee.com/tuituidan/pic/raw/master/2025/20250605211336605.png)
