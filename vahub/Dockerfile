FROM docker.io/openjdk:8u282-jdk

ARG WAR_NAME

ENV WAR_NAME $WAR_NAME
ENV ENV_TYPE_PROFILE dev
ENV AUTH_PROFILE local-no-security
ENV CONFIG_PROFILE local-config
ENV OTHER_PROFILES NoScheduledJobs
ENV CFG_SRV_LOGIN acuity
ENV CFG_SRV_PASSWORD ac3tbasic

ENV JAVA_OPTIONS=

WORKDIR /usr/root
COPY docker-resources/wait-for-it.sh wait-for-it.sh
COPY docker-resources/application.yml /usr/root/local-configs/application.yml
COPY /target/$WAR_NAME /usr/root/$WAR_NAME
RUN chmod +x wait-for-it.sh

EXPOSE 8000
CMD if [ "$CONFIG_PROFILE" != "local-config" ] ; \
        then ./wait-for-it.sh config-server:8888 --timeout=300 --strict -- echo "Starting $WAR_NAME..." \
                && spring_config_details='-Dspring.cloud.config.username='$CFG_SRV_LOGIN' \
                -Dspring.cloud.config.password='$CFG_SRV_PASSWORD' \
                -Dspring.cloud.config.uri=http://config-server:8888/acuity-spring-configs'; \
        else spring_config_details='-Dspring.config.location=./local-configs/'; \
    fi \
    && mkdir -p /var/log/gc \
    && mkdir -p /var/log/heap_dump \
    && java -Dspring.profiles.active=$ENV_TYPE_PROFILE,$AUTH_PROFILE,$CONFIG_PROFILE,$OTHER_PROFILES \
            -Dserver.port=8000 \
            $spring_config_details \
            $JAVA_OPTIONS \
            -jar /usr/root/$WAR_NAME
