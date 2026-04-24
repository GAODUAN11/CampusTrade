# CampusTrade systemd Setup

This directory provides systemd templates and installer scripts.

## Files

- `templates/*.service.tpl`: service unit templates
- `templates/*.target.tpl`: grouped target templates
- `install-server-a.sh`: install Server A units
- `install-server-b.sh`: install Server B units

## 1. Prepare

On each server:

```bash
cd /path/to/CampusTrade
cp deploy/env/server-a.env.example deploy/env/server-a.env
cp deploy/env/server-b.env.example deploy/env/server-b.env
# then edit env file(s) you really use on that server
```

Important:

- Keep env files in `KEY=VALUE` format (no `export`).
- `java -version` must be 17 or above.

## 2. Install server A units

Server A runs:

- `campustrade-user.service`
- `campustrade-product.service`
- grouped by `campustrade-server-a.target`

Install:

```bash
sudo bash deploy/systemd/install-server-a.sh \
  --app-root /path/to/CampusTrade \
  --run-user your_linux_user
```

Optional immediate start:

```bash
sudo bash deploy/systemd/install-server-a.sh \
  --app-root /path/to/CampusTrade \
  --run-user your_linux_user \
  --start-now
```

## 3. Install server B units

Server B runs:

- `campustrade-favorite.service`
- `campustrade-message.service`
- grouped by `campustrade-server-b.target`

Install:

```bash
sudo bash deploy/systemd/install-server-b.sh \
  --app-root /path/to/CampusTrade \
  --run-user your_linux_user
```

Optional immediate start:

```bash
sudo bash deploy/systemd/install-server-b.sh \
  --app-root /path/to/CampusTrade \
  --run-user your_linux_user \
  --start-now
```

## 4. Manage services

Server A:

```bash
sudo systemctl start campustrade-server-a.target
sudo systemctl stop campustrade-server-a.target
sudo systemctl restart campustrade-server-a.target
sudo systemctl status campustrade-server-a.target
sudo systemctl status campustrade-user.service
sudo systemctl status campustrade-product.service
```

Server B:

```bash
sudo systemctl start campustrade-server-b.target
sudo systemctl stop campustrade-server-b.target
sudo systemctl restart campustrade-server-b.target
sudo systemctl status campustrade-server-b.target
sudo systemctl status campustrade-favorite.service
sudo systemctl status campustrade-message.service
```

Logs:

```bash
journalctl -u campustrade-user.service -f
journalctl -u campustrade-product.service -f
journalctl -u campustrade-favorite.service -f
journalctl -u campustrade-message.service -f
```

## 5. Notes

- Current units run with `java -jar` against executable Spring Boot jars.
- First-time deployment should run:
  - Server A: `./deploy/server-a/prepare.sh`
  - Server B: `./deploy/server-b/prepare.sh`
