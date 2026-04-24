[Unit]
Description=CampusTrade Server A Service Group
Wants=campustrade-user.service campustrade-product.service
After=network-online.target

[Install]
WantedBy=multi-user.target

