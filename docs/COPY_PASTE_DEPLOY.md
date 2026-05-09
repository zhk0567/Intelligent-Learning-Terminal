# 无脑粘贴（本机 ECS 已填好）

| 项 | 值 |
|----|-----|
| 公网 IP | 39.106.117.118 |
| 私网 IP | 172.30.87.77 |
| SSH 用户 | root |
| 系统 | Alibaba Cloud Linux 3.2104 LTS 64 位 |
| 规格 | 2 核 2 GiB，ecs.e-c1m1.large |
| 公网带宽 | 3 Mbps（按固定带宽） |
| 系统盘 | 默认随实例（控制台「云盘」页可看容量） |

**以后若公网 IP 变更：** 全文搜 `39.106.117.118` 替换；并改 `app/.../network_security_config.xml` 里最后一行 `<domain>`。

---

## A. 登录 ECS 后整段粘贴（终端里）

```bash
sudo mkdir -p /var/www/ilt-static
sudo chown -R $USER:$USER /var/www/ilt-static
sudo yum install -y nginx
sudo tee /etc/nginx/conf.d/ilt-static.conf <<'NGXEOF'
server {
    listen 80;
    server_name _;
    root /var/www/ilt-static;
    location /images/ {
        add_header Access-Control-Allow-Origin *;
        add_header Cache-Control "public, max-age=31536000, immutable";
        try_files $uri =404;
    }
    location /audio/ {
        add_header Access-Control-Allow-Origin *;
        add_header Cache-Control "public, max-age=86400";
        try_files $uri =404;
    }
    location /video/ {
        add_header Access-Control-Allow-Origin *;
        add_header Cache-Control "public, max-age=86400";
        try_files $uri =404;
    }
    gzip on;
    gzip_types image/jpeg image/png image/webp audio/mpeg video/mp4 application/json;
}
NGXEOF
sudo nginx -t && sudo systemctl enable nginx && sudo systemctl restart nginx
```

---

## B. Windows PowerShell（项目根目录，用 root 传静态文件）

```powershell
Set-Location "f:\commercial\guyunxinchuan"
.\tools\deploy-static-to-ecs.ps1 -HostName 39.106.117.118 -User root -RemoteDir /var/www/ilt-static
```

---

## C. 新建文件 `web/.env.production`（整文件内容）

```env
VITE_STATIC_ORIGIN=http://39.106.117.118
```

---

## D. `app/gradle.properties`（文件末尾追加一行）

```properties
STATIC_ASSET_ORIGIN=http://39.106.117.118
```

---

## E. Android 明文 HTTP（本仓库已写好）

`app/app/src/main/res/xml/network_security_config.xml` 里已包含：

```xml
        <domain includeSubdomains="false">39.106.117.118</domain>
```

换 IP 时只改这一处。

---

## F. `miniprogram/config/staticOrigin.ts`（保持这样）

```ts
export const STATIC_ORIGIN = "";
```

---

## G. 阿里云安全组

入方向放行 TCP：**22**、**80**。
