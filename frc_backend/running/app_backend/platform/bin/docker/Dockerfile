FROM registry.digiwincloud.com/dwsidecar/dwsidecar-1.0.0.0:1.0.0.24

ADD @folderName@.tar /
RUN chmod +x /@folderName@/platform/bin/run.sh
RUN chmod +x /@folderName@/platform/bin/runEncrypt.sh
RUN chmod +x /@folderName@/platform/bin/stop.sh
RUN chmod +x /@folderName@/platform/bin/docker/dockerEnv.sh
RUN chmod +x /@folderName@/platform/bin/docker/dockerEnvReplace.sh
RUN chmod +x /@folderName@/platform/bin/docker/dockerRun.sh

WORKDIR /@folderName@
EXPOSE 22620
ENTRYPOINT ["/@folderName@/platform/bin/docker/dockerRun.sh"]
