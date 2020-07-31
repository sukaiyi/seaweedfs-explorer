## 简介

Seaweedfs 是一个优秀的分布式文件存储服务程序，使用 fileId 唯一标志一个文件，这也是外部访问文件的唯一方法，但有时候文件上传之后 fileId 丢失了，而 Seaweedfs 出于安全考虑，并不支持查找 fileId ，这时便再也无法访问该文件，也无法释放该文件占用的磁盘空间，这对服务器磁盘空间造成了永久性的浪费。

为解决这个问题，我研究了下 Seaweedfs 的文件存储格式，并编写了 Seaweedfs Explorer，可以实现数据文件中 fileId以及其他相关信息的遍历。

## 安装 & 使用

```bash
git clone git@github.com:sukaiyi/seaweedfs-explorer.git
cd seaweedfs-explorer
mvn clean package -DskipTests

# 启动 方法一
cd target
java -jar seaweedfs-explorer-1.0-SNAPSHOT-shaded.jar <seaweedfs data dir>
# 方法二 将jar包拷贝到 seaweedfs 数据目录下（dat/idx 同级目录），可省略启动参数
cd <seaweedfs data dir>
java -jar seaweedfs-explorer-1.0-SNAPSHOT-shaded.jar

# 浏览器打开 http://127.0.0.1:35672/index.html
```

![image-20200731230815159](https://github.com/sukaiyi/seaweedfs-explorer/raw/master/README.assets/image-20200731230815159.png)

