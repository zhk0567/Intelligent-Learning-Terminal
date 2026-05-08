# 古韵薪传 · Web 版

仓库 Android 工程的 Web 镜像，使用 React + Vite + TypeScript + Tailwind 实现。

## 启动

仅需 Python（用于一键启动器）和 Node.js（首次运行会自动 `npm install`）：

```powershell
python web/start.py
```

可选参数：

- `--port 5173` 指定端口（默认 5173）
- `--no-open` 不自动打开浏览器
- `--install` 强制重新安装依赖

启动后会在 `http://127.0.0.1:5173` 打开手机视图（≥768px 的桌面浏览器中央显示 480px 宽手机壳）。
