FROM registry.cn-hangzhou.aliyuncs.com/fleyx/node:v1
COPY settings.xml /opt/settings.xml
RUN cd /opt && \
    wget https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_linux-x64_bin.tar.gz && \
    tar -xvf openjdk-11+28_linux-x64_bin.tar.gz && \
    mv jdk-11 jdk11 && \
    rm openjdk-11+28_linux-x64_bin.tar.gz && \
    wget http://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/3.6.1/binaries/apache-maven-3.6.1-bin.tar.gz && \
    tar -xvf apache-maven-3.6.1-bin.tar.gz && \
    mv apache-maven-3.6.1 maven && \
    rm apache-maven-3.6.1-bin.tar.gz && \ 
    mv maven/conf/settings.xml maven/conf/settings.xml.bak && \
    mv settings.xml maven/conf/settings.xml
ENV PATH=$PATH:/opt/jdk11/bin:/opt/maven/bin
