# 部署说明

## 实例与仓库

| 项 | 值 |
|----|-----|
| 公网 IP | 39.106.117.118 |
| 私网 IP | 172.30.87.77 |
| SSH | root |
| 系统 | Alibaba Cloud Linux 3.2104 LTS |
| 仓库 | `https://gitee.com/zhk567/intelligent-learning-terminal.git` |

**仓库内已配置（push 后服务器 `git pull` 即用，一般无需再改文件）：**

- `web/.env.production`：`VITE_STATIC_ORIGIN=http://39.106.117.118`
- `app/gradle.properties`：`STATIC_ASSET_ORIGIN=http://39.106.117.118`
- `app/app/src/main/res/xml/network_security_config.xml`：已放行 `39.106.117.118` 明文 HTTP
- `miniprogram/config/staticOrigin.ts`：`STATIC_ORIGIN=""`（无域名时小程序正式版走包内资源）
- `deploy/nginx-git-server.conf`：`root` → `/opt/intelligent-learning-terminal/web/dist`

**换公网 IP：** 改上述三处与 `network_security_config.xml` 内 `<domain>`，`npm run build` 后 `git push`。

**私有仓库：** `git clone` 改为带私人令牌的 HTTPS 或 `git@gitee.com:...`。

---

## 1. ECS 上安装依赖并拉代码、构建 Web

```bash
sudo yum install -y git nginx
curl -fsSL https://rpm.nodesource.com/setup_20.x | sudo bash -
sudo yum install -y nodejs
sudo mkdir -p /opt && cd /opt
sudo rm -rf intelligent-learning-terminal
sudo git clone https://gitee.com/zhk567/intelligent-learning-terminal.git intelligent-learning-terminal
cd /opt/intelligent-learning-terminal/web
sudo npm ci
sudo npm run build
```

若 `npm ci` 内存不足：

```bash
sudo dd if=/dev/zero of=/swapfile bs=1M count=2048
sudo chmod 600 /swapfile && sudo mkswap /swapfile && sudo swapon /swapfile
```

---

## 2. Nginx

```bash
sudo cp /opt/intelligent-learning-terminal/deploy/nginx-git-server.conf /etc/nginx/conf.d/ilt-web.conf
sudo nginx -t && sudo systemctl enable nginx && sudo systemctl restart nginx
```

---

## 3. 安全组

阿里云入方向放行 TCP：**22**、**80**（仅 HTTP 时不必开 443；上 HTTPS 后再开 443）。

---

## 4. 验证

浏览器打开：`http://39.106.117.118/`

---

## 5. 日后更新

```bash
cd /opt/intelligent-learning-terminal
sudo git pull
cd web && sudo npm ci && sudo npm run build
sudo systemctl reload nginx
```

---

## 6. `web/public` 大图/音视频

若 Gitee 未包含 `web/public` 下大文件：在有数据的机器上运行 `tools/sync_*.py` 生成后 **commit + push**，再在服务器执行第 5 节；或把本机 `web/public` **scp** 到服务器同路径后重新 `npm run build`。

---

## 附录 A：无域名、仅公网 IP

- **Web / Android**：可用 `http://IP`（与仓库当前配置一致）。
- **微信小程序**：正式版**不能**把纯 IP 配进合法域名；保持 `STATIC_ORIGIN=""`，资源打安装包，或以后用 **https 域名** 再在后台配置 **downloadFile**。
- **Android 明文 HTTP**：仅 IP 时必须保留 `network_security_config.xml` 中对该 IP 的 `cleartextTrafficPermitted`；改 HTTPS 后可删。

## 附录 B：仅静态目录（不构建整站）

若只把 `images`/`audio`/`video` 放到 Nginx、不跑 `npm run build`：用 `deploy/nginx-static.example.conf`，`root` 指向静态目录；本机可用 `tools/deploy-static-to-ecs.ps1` 上传。

## 附录 C：本地开发

不设 `web/.env.production` 或清空 `VITE_STATIC_ORIGIN` 时，Vite 用相对路径，依赖本机 `web/public`。
