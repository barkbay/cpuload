FROM openjdk:14
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp/src
RUN javac CpuLoad.java
CMD ["java", "CpuLoad"]
