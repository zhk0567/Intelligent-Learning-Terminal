# 部署说明

> **提醒（维护人）：** 每次在仓库里改了 **Web 静态资源路径**、**`web/public` 下的图片/音频/视频**、或 **Android `STATIC_ASSET_ORIGIN` / 网络明文域名** 后，都需要在 ECS 上 **`git pull` → `npm run build`（`web/`）→ 校验 Nginx `root` 指向的 `dist`**，否则 **Android 端封面、在线音频（每日热门/精选/猜你喜欢）、基础学习视频** 会加载失败。换公网 IP 时除改配置外也要同步 **`network_security_config.xml`**。

## 实例与仓库

| 项 | 值 |
|----|-----|
| 公网 IP | 39.106.117.118 |
| 私网 IP | 172.30.87.77 |
| SSH | root |
| 系统 | Alibaba Cloud Linux 3.2104 LTS |
| 仓库 | `https://gitee.com/zhk567/intelligent-learning-terminal.git` |

**重要：** Gitee 默认分支是 **`main`**（旧目录，无 `web/`）。含 `web/`、`package-lock.json` 的代码在 **`master`**。部署时必须 **`clone -b master`** 或 clone 后执行 **`git checkout master`**，否则会出现 `cd .../web: No such file`。

**仓库内已配置（push 后服务器 `git pull` 即用，一般无需再改文件）：**

- `web/.env.production`：`VITE_STATIC_ORIGIN=http://39.106.117.118`
- `app/gradle.properties`：`STATIC_ASSET_ORIGIN=http://39.106.117.118`
- `app/app/src/main/res/xml/network_security_config.xml`：已放行 `39.106.117.118` 明文 HTTP
- `miniprogram/config/staticOrigin.ts`：`STATIC_ORIGIN=""`（无域名时小程序正式版走包内资源）
- `deploy/nginx-git-server.conf`：`root` → `/opt/intelligent-learning-terminal/web/dist`

**换公网 IP：** 改上述三处与 `network_security_config.xml` 内 `<domain>`，`npm run build` 后 `git push`。

### Android 在线音频与视频（与 Web 同源）

- 音频路径与 Web `public/audio/` 一致，例如：`/audio/daily_hot/daily_hot_01.mp3`、`/audio/daily_select/…`、`/audio/daily_guess/…`。构建产物需在 **`web/dist/audio/`**（或 Nginx 对应该 URL）下存在文件，否则 App 内播放器无法出声。
- 基础学习视频：`/video/basic_lesson/basic_lesson_01.mp4` 等，需在 **`web/dist/video/`** 下存在。
- 仓库根若未提交大体积 `mp3/mp4`，需在服务器或构建流水线中 **单独拷贝 `data/音频` 等素材到 `web/public`** 后再 `npm run build`，否则仅有封面无声音。

**私有仓库：** `git clone` 改为带私人令牌的 HTTPS 或 `git@gitee.com:...`。

---

## 1. ECS 上安装依赖并拉代码、构建 Web

```bash
sudo yum install -y git nginx
curl -fsSL https://rpm.nodesource.com/setup_20.x | sudo bash -
sudo yum install -y nodejs
sudo mkdir -p /opt && cd /opt
sudo rm -rf intelligent-learning-terminal
sudo git clone -b master https://gitee.com/zhk567/intelligent-learning-terminal.git intelligent-learning-terminal
cd /opt/intelligent-learning-terminal/web
npm ci
npm run build
```

若已按旧文档 clone 过、没有 `web` 目录，在仓库根目录执行：`git fetch origin && git checkout master`，再 `cd web`。

若 **`npm ci` 报没有 package-lock.json**：先 `npm install`，再 `npm run build`。

若 `npm ci` 内存不足：

```bash
sudo dd if=/dev/zero of=/swapfile bs=1M count=2048
sudo chmod 600 /swapfile && sudo mkswap /swapfile && sudo swapon /swapfile
```

---

## 2. Nginx

**若出现 `conflicting server name "_"`：** 与自带的 `default.conf` 冲突。先停用默认站点再拷配置：

```bash
sudo test -f /etc/nginx/conf.d/default.conf && sudo mv /etc/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf.bak
sudo cp /opt/intelligent-learning-terminal/deploy/nginx-git-server.conf /etc/nginx/conf.d/ilt-web.conf
sudo nginx -t
sudo systemctl enable nginx
sudo systemctl start nginx
```

若服务已在跑、只改了配置：`sudo systemctl restart nginx`。

**若 `nginx.service is not active, cannot reload`：** 说明还没启动过，用上面的 **`systemctl start nginx`**，不要用 `reload`。

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
git checkout master
git pull
cd web && npm ci && npm run build
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
