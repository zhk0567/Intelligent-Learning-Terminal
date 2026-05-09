# 静态资源上云（减包体积）

## 0. 没有域名、只有公网 IP 时（必读）

**可以这样做的：** 用 **`http://公网IP`**（不要末尾 `/`）当静态资源根地址，配好 Nginx、传好文件后，**网页（Web）**和**安卓里「基础学习」远程视频**一般都能用。

**做不到的：** **微信小程序**后台的「服务器域名 / downloadFile 合法域名」**不能填纯 IP**，必须是有备案等环节的 **https 域名**。所以没有域名时：
- 要么小程序**继续把图片等资源打在安装包里**（不要改 `STATIC_ORIGIN`，保持空字符串）；
- 要么以后买了域名、上了 **HTTPS**，再把 `miniprogram/config/staticOrigin.ts` 改成 `https://你的域名`，并在微信后台配置该域名。

**安卓用 HTTP + 公网 IP 播视频：** 必须在 `app/app/src/main/res/xml/network_security_config.xml` 里为你的公网 IP 增加一段允许明文 HTTP 的配置（与文档下面「Android」节示例一致），否则系统会拦截 `http://` 请求。

---

## 1. 服务器目录

在 ECS（如 `39.106.117.118`）上：

```bash
sudo mkdir -p /var/www/ilt-static
sudo chown -R $USER:$USER /var/www/ilt-static
```

本地保证 `web/public/` 下已有 `images/`、`audio/`、`video/`（缺则先跑仓库内 `tools/sync_*.py` 从 `data/` 同步）。

## 2. 上传

仓库根目录 PowerShell：

```powershell
.\tools\deploy-static-to-ecs.ps1 -HostName 39.106.117.118 -User root -RemoteDir /var/www/ilt-static
```

或自行 `scp -r web\public\images web\public\audio web\public\video root@IP:/var/www/ilt-static/`。

## 3. Nginx

参考 `deploy/nginx-static.example.conf` 放到 `/etc/nginx/conf.d/`，`root` 指向 `/var/www/ilt-static`。  
`server_name` 可写你的公网 IP 或 `_`。只有 IP、无证书时：**只开 80 端口、用 HTTP** 即可。  
有域名并上小程序正式版时，再改为 **备案域名 + HTTPS（443）**。

## 4. Web

复制 `web/.env.example` 为 `web/.env.production`，设置：

有域名、HTTPS 时：

```env
VITE_STATIC_ORIGIN=https://你的静态域名
```

只有公网 IP 时（示例，换成你的 IP，无尾斜杠）：

```env
VITE_STATIC_ORIGIN=http://39.106.117.118
```

再 `npm run build`。未设置时仍用相对路径，适合本地开发。

## 5. 微信小程序

1. **有 https 域名时**：编辑 `miniprogram/config/staticOrigin.ts` 的 `STATIC_ORIGIN` 与 Web 一致（`https://...`，勿尾斜杠）；在微信公众平台把该域名加入 **downloadFile 合法域名**。
2. **只有公网 IP 时**：不要把 `http://IP` 写进小程序发布配置；保持 `STATIC_ORIGIN = ""`，资源走包内。开发者工具里可勾选「不校验合法域名」做本机调试，**正式上架无效**。

## 6. Android

- **基础学习视频**：在 `app/gradle.properties` 中设置（勿尾斜杠），与 Web 的 `VITE_STATIC_ORIGIN` 一致。仅公网 IP 示例：  
  `STATIC_ASSET_ORIGIN=http://39.106.117.118`  
  有域名后改为 `https://你的域名`。留空则仍用 `http://10.0.2.2:5173`（模拟器连本机 Vite）。
- **网络安全**：用 **`http://` + 公网 IP** 时，在 `app/app/src/main/res/xml/network_security_config.xml` 的 `</network-security-config>` 前增加（IP 换成你的）：

```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="false">39.106.117.118</domain>
</domain-config>
```

以后改为 **HTTPS 域名** 后，可删除该段，并把 `STATIC_ASSET_ORIGIN` 改为 `https://...`。
- **封面 / 其它音频**：仍在 `res/drawable` 等本地资源中；若要全部迁 URL，需扩展 Coil 与数据模型。

## 7. 安全组

阿里云安全组放行 **80/443**（及 SSH 22）。
