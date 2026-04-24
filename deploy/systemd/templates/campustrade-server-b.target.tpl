[Unit]
Description=CampusTrade Server B Service Group
Wants=campustrade-favorite.service campustrade-message.service
After=network-online.target

[Install]
WantedBy=multi-user.target

