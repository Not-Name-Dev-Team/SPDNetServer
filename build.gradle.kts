plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "me.catand"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/spring/")
    maven("https://jitpack.io")
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:3.2.2")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Shiro
    implementation("com.mikuac:shiro:2.1.7")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.2")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.3.1.Final")

    // Fastjson
    implementation("com.alibaba:fastjson:2.0.48")

    // Socket.io
    implementation("com.corundumstudio.socketio:netty-socketio:2.0.8")

    // email
    implementation("javax.mail:mail:1.4.7")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
