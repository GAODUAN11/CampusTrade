[Unit]
Description=CampusTrade User Service
After=network-online.target
Wants=network-online.target
PartOf=campustrade-server-a.target

[Service]
Type=simple
User=__RUN_USER__
WorkingDirectory=__APP_ROOT__
EnvironmentFile=__APP_ROOT__/deploy/env/server-a.env
ExecStart=/usr/bin/env bash -lc 'JAR=$(find "__APP_ROOT__/user-service/target" -maxdepth 1 -type f -name "user-service-*.jar" ! -name "original-*.jar" | head -n 1); [ -n "$JAR" ] || { echo "user-service jar not found"; exit 1; }; exec java ${JAVA_OPTS:-} -jar "$JAR"'
ExecStop=/bin/kill -TERM $MAINPID
SuccessExitStatus=143
Restart=always
RestartSec=5
TimeoutStartSec=180
TimeoutStopSec=30

[Install]
WantedBy=campustrade-server-a.target
