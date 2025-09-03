# ================= STAGE 1: 빌드(Build) 환경 =================
# JDK 이미지를 사용하여 애플리케이션을 빌드합니다.
FROM eclipse-temurin:21-jdk-jammy AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 관련 파일들을 먼저 복사하여 의존성 레이어를 캐싱합니다.
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle Wrapper를 실행 가능하게 만들고 의존성을 다운로드합니다.
# 소스 코드가 변경되지 않으면 이 레이어는 캐시되어 재사용됩니다.
RUN chmod +x ./gradlew && ./gradlew dependencies

# 소스 코드를 복사합니다.
COPY src ./src

# 애플리케이션을 빌드합니다. (테스트는 건너뜁니다)
RUN ./gradlew bootJar

# ================= STAGE 2: 실행(Runtime) 환경 =================
# JRE(Java 실행 환경) 이미지를 사용하여 최종 이미지 크기를 줄입니다.
FROM eclipse-temurin:21-jre-jammy

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지(builder)에서 생성된 JAR 파일만 복사해옵니다.
# JAR 파일 이름은 build.gradle의 설정에 따라 정확히 지정하는 것이 좋습니다.
# build/libs/app-0.0.1-SNAPSHOT.jar 와 같은 형태입니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행 포트 노출
EXPOSE 8080

# 컨테이너 시작 시 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]