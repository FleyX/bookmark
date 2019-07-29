FROM openjdk:11
COPY settings.xml /opt/settings.xml
RUN cd /opt && \
    wget https://npm.taobao.org/mirrors/node/v10.16.0/node-v10.16.0-linux-x64.tar.xz && \
    xz -d node-v10.16.0-linux-x64.tar.xz && \
    tar -xf node-v10.16.0-linux-x64.tar && \
    mv node-v10.16.0-linux-x64 node10 && \
    export PATH=$PATH:/opt/node10/bin && \
    ./node10/bin/npm install -g cnpm --registry=https://registry.npm.taobao.org && \
    rm node-v10.16.0-linux-x64.tar && \
    wget http://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/3.6.1/binaries/apache-maven-3.6.1-bin.tar.gz && \
    tar -xf apache-maven-3.6.1-bin.tar.gz && \
    mv apache-maven-3.6.1 maven && \
    rm apache-maven-3.6.1-bin.tar.gz && \ 
    mv maven/conf/settings.xml maven/conf/settings.xml.bak && \
    mv settings.xml maven/conf/settings.xml
ENV PATH=$PATH:/opt/node10/bin:/opt/maven/bin
