# docker安装

## Ubuntu16.04 

### 添加APT镜像源

​	**虽然 Ubuntu 系统软件源中有 Docker，名为 docker.io ，但是不应该使用系统源中的这个版本，它的版本太旧。我们需要使用 Docker 官方提供的软件源，因此，我们需要添加 APT 软件源。**
​	**由于官方源使用 HTTPS 以确保软件下载过程中不被篡改。因此，我们首先需要添加使用 HTTPS 传输的软件包以及 CA 证书。 **

​	**国内的一些软件源镜像（比如阿里云） 不是太在意系统安全上的细节，可能依旧使用不安全的 HTTP，对于这些源可以不执行这一步**

~~~bash
$ sudo apt-get update
$ sudo apt-get install apt-transport-https ca-certificates
~~~

**为了确认所下载软件包的合法性，需要添加 Docker 官方软件源的 GPG 密钥。 **

~~~bash
$ sudo apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
~~~

**然后，我们需要向 source.list 中添加 Docker 软件源，下表列出了不同的Ubuntu 和 Debian 版本对应的 APT 源。 **

| 操作系统版本        | REPO                                                       |
| ------------------- | ---------------------------------------------------------- |
| Precise 12.04 (LTS) | deb https://apt.dockerproject.org/repo ubuntu-precise main |
| Trusty 14.04 (LTS)  | deb https://apt.dockerproject.org/repo ubuntu-trusty main  |
| Xenial 16.04 (LTS)  | deb https://apt.dockerproject.org/repo ubuntu-xenial main  |
| Debian 7 Wheezy     | deb https://apt.dockerproject.org/repo debian-wheezy main  |
| Debian 8 Jessie     | deb https://apt.dockerproject.org/repo debian-jessie main  |
| Debian Stretch/Sid  | deb https://apt.dockerproject.org/repo debian-stretch main |

**用下面的命令将 APT 源添加到 source.list （将其中的 <REPO> 替换为上表的值） ： **

~~~bash
$ echo "<REPO>" | sudo tee /etc/apt/sources.list.d/docker.list
~~~

***将<REPO>替换成deb https://apt.dockerproject.org/repo ubuntu-xenial main***

~~~bash
$ echo "deb https://apt.dockerproject.org/repo ubuntu-xenial main" | sudo tee /etc/apt/sources.list.d/docker.list
~~~

**添加成功后，更新 apt 软件包缓存。 **

~~~bash
$ sudo apt-get update
~~~

### 安装Docker

~~~bash
sudo apt-get install docker-engine
~~~

### 启动 Docker 引擎 

~~~bash
$ sudo systemctl enable docker
$ sudo systemctl start docker
$ sudo systemctl stop docker
~~~

### 建立docker用户组

**建立 docker 组： **

~~~bash
$ sudo groupadd docker
~~~

**将当前用户加入 docker 组： **

~~~bash
$ sudo usermod -aG docker $USER
~~~



# docker常用命令

|              命令               |                             解释                             |
| :-----------------------------: | :----------------------------------------------------------: |
|          docker images          |                       列表本地所有镜像                       |
|      docker search 关键词       |                    在Docker Hub中搜索镜像                    |
|      docker pull 镜像名称       |                        下载Docker镜像                        |
|        docker rmi 镜像ID        |           删除Docker镜像。 加参数-f表示强制删除。            |
|       docker run 镜像名称       |                        启动Docker镜像                        |
|            docker ps            | 列表所有运⾏中的Docker容器。 该命令参数⽐较多， -a： 列表所有容器； -f： 过滤； -q 只列表容器的id。 |
|         docker version          |                      查看Docker版本信息                      |
|           docker info           |    查看Docker系统信息， 例如： CPU、 内存、 容器个数等等     |
|       docker kill 容器ID        |                                                              |
| docker start/stop/restart容器ID |                  启动、 停⽌、 重启指定容器                  |
|           docker tag            |                         为镜像打标签                         |
|  docker build -t 标签名称 目录  |             构建Docker镜像， -t 表示指定⼀个标签             |
**更多命令， 请输⼊ --help 参数查询； 如果想看docker命令可输⼊ docker--help ； 如果想查询 docker run 命令的⽤法， 可输⼊ docker run --help 。 **



# Dockerfile常用指令

`指令的一般格式为： 指令名称 参数`

## FROM

⽀持三种格式：
FROM <image>
FROM <image>:<tag>
FROM <image>@<digest>
FROM指令必须指定且需要在Dockerfile其他指令的前⾯， 指定的基础image可以是官⽅远程仓库中的， 也可以位于本地仓库。 后续的指令都依赖于该指令指定的image。 当在同⼀个Dockerfile中建⽴多个镜像时， 可以使⽤多个FROM指令。 

## MAINTAINER

格式为：
MAINTAINER <name>
⽤于指定维护者的信息。 

