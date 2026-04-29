# Intelligent Learning Terminal

非遗音乐学习终端（Android）项目，包含移动端应用与轻量服务端示例。

---

## 项目结构

```text
Intelligent_learning_terminal/
├─ HeritageMusic/                  # Android 主工程（Kotlin + Compose）
│  ├─ app/
│  ├─ core/
│  │  ├─ resources/
│  │  ├─ ui/
│  │  ├─ data/
│  │  └─ network/
│  └─ feature/
│     ├─ music-hall/
│     ├─ stories/
│     ├─ community/
│     ├─ mall/
│     ├─ profile/
│     └─ player/
└─ HeritageWeb/                    # FastAPI 服务端（API + 静态资源）
```

---

## 开发环境

- Windows 10/11
- JDK 17
- Android Studio（稳定版）
- Android SDK（按 `compileSdk`）

---

## 快速开始（Windows PowerShell）

```powershell
cd .\HeritageMusic
.\gradlew :app:assembleDebug
```

安装到已连接设备：

```powershell
.\gradlew :app:installDebug
```

常用命令：

```powershell
# 快速代码校验
.\gradlew :app:compileDebugKotlin

# 构建 Debug
.\gradlew :app:assembleDebug
```

---

## 服务器信息更新（重点）

### 1) 修改 App 指向的服务器

文件：`HeritageMusic/app/build.gradle.kts`

- `USE_REMOTE_API`：`true` 使用远程接口，`false` 使用本地假数据
- `API_BASE_URL`：必须以 `/` 结尾，例如：
  - `http://39.106.117.118:8000/`
  - `http://your-domain/`

若使用 `http://`，请确认 `AndroidManifest.xml` 保留 `android:usesCleartextTraffic="true"`。

### 2) 服务器更新代码并重启

```bash
cd /opt/heritageweb/intelligent-learning-terminal
git pull

pkill -f "gunicorn.*server:app" || true
nohup /opt/heritageweb/.venv/bin/gunicorn \
  --chdir /opt/heritageweb/intelligent-learning-terminal/HeritageWeb \
  server:app -k uvicorn.workers.UvicornWorker -w 2 -b 0.0.0.0:8000 \
  > /opt/heritageweb/heritageweb.log 2>&1 &

sleep 2
tail -n 30 /opt/heritageweb/heritageweb.log
```

### 3) 商城图片走服务器

- 图片目录：`HeritageWeb/static/mall/`
- App 已配置优先读取远程 URL，包内仅保留占位图

只要更新并重启后端，手机端商城图会自动更新。
