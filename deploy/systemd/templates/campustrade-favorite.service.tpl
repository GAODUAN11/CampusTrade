[Unit]
Description=CampusTrade Favorite Service
After=network-online.target
Wants=network-online.target
PartOf=campustrade-server-b.target

[Service]
Type=simple
User=__RUN_USER__
WorkingDirectory=__APP_ROOT__
EnvironmentFile=__APP_ROOT__/deploy/env/server-b.env
ExecStart=/usr/bin/env bash -lc 'JAR=$(find "__APP_ROOT__/favorite-service/target" -maxdepth 1 -type f -name "favorite-service-*.jar" ! -name "original-*.jar" | head -n 1); [ -n "$JAR" ] || { echo "favorite-service jar not found"; exit 1; }; exec java ${JAVA_OPTS:-} -jar "$JAR"'
ExecStop=/bin/kill -TERM $MAINPID
SuccessExitStatus=143
Restart=always
RestartSec=5
TimeoutStartSec=180
TimeoutStopSec=30

[Install]
WantedBy=campustrade-server-b.target
