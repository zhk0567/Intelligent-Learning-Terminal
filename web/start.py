"""古韵薪传 Web 版唯一启动入口。

用法：
    python web/start.py [--port 5173] [--no-open] [--install]

行为：
    - 检查 node / npm 是否可用；
    - web/node_modules 不存在或显式 --install 时执行 npm install；
    - 拉起 Vite 开发服务（npm run dev），等待端口可连通；
    - 自动用系统默认浏览器打开 http://127.0.0.1:<port>；
    - Ctrl+C 时优雅终止 Vite 子进程。
"""

from __future__ import annotations

import argparse
import os
import shutil
import signal
import socket
import subprocess
import sys
import time
import webbrowser
from pathlib import Path

WEB_DIR = Path(__file__).resolve().parent
IS_WINDOWS = os.name == "nt"


def _has(cmd: str) -> bool:
    return shutil.which(cmd) is not None or shutil.which(cmd + ".cmd") is not None


def _ensure_node() -> tuple[str, str]:
    if not _has("node"):
        print("[启动失败] 未检测到 Node.js，请先安装 Node 18+ 后再运行：https://nodejs.org/")
        sys.exit(1)
    npm_exe = "npm.cmd" if IS_WINDOWS and _has("npm.cmd") else "npm"
    npx_exe = "npx.cmd" if IS_WINDOWS and _has("npx.cmd") else "npx"
    if not _has(npm_exe):
        print("[启动失败] 未检测到 npm，请确认 Node.js 安装完整。")
        sys.exit(1)
    return npm_exe, npx_exe


def _install(npm_exe: str) -> None:
    print("[依赖] 正在执行 npm install ...")
    proc = subprocess.run(
        [npm_exe, "install", "--no-audit", "--no-fund"],
        cwd=WEB_DIR,
        shell=False,
    )
    if proc.returncode != 0:
        print("[启动失败] npm install 失败，请按以上日志检查。")
        sys.exit(proc.returncode)


def _wait_port(host: str, port: int, timeout: float = 60.0) -> bool:
    end = time.time() + timeout
    while time.time() < end:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.settimeout(0.5)
            try:
                s.connect((host, port))
                return True
            except OSError:
                time.sleep(0.3)
    return False


def main() -> None:
    parser = argparse.ArgumentParser(description="古韵薪传 Web 版启动器")
    parser.add_argument("--port", type=int, default=5173)
    parser.add_argument("--no-open", action="store_true")
    parser.add_argument("--install", action="store_true", help="强制重新执行 npm install")
    args = parser.parse_args()

    npm_exe, _ = _ensure_node()

    node_modules = WEB_DIR / "node_modules"
    if args.install or not node_modules.exists():
        _install(npm_exe)
    else:
        print("[依赖] 已检测到 node_modules，跳过安装。如需强制重装可加 --install。")

    cmd = [npm_exe, "run", "dev", "--", "--host", "--port", str(args.port)]
    print(f"[启动] {' '.join(cmd)}")

    popen_kwargs: dict = {"cwd": str(WEB_DIR), "shell": False}
    if IS_WINDOWS:
        popen_kwargs["creationflags"] = subprocess.CREATE_NEW_PROCESS_GROUP
    else:
        popen_kwargs["preexec_fn"] = os.setsid  # type: ignore[arg-type]

    proc = subprocess.Popen(cmd, **popen_kwargs)

    try:
        if _wait_port("127.0.0.1", args.port, timeout=90):
            url = f"http://127.0.0.1:{args.port}"
            print(f"[就绪] 已监听 {url}")
            if not args.no_open:
                webbrowser.open(url)
        else:
            print("[警告] 等待 Vite 端口超时，但子进程仍在运行，可手动访问。")
        proc.wait()
    except KeyboardInterrupt:
        print("\n[退出] 正在关闭 Vite ...")
        try:
            if IS_WINDOWS:
                proc.send_signal(signal.CTRL_BREAK_EVENT)
            else:
                os.killpg(os.getpgid(proc.pid), signal.SIGTERM)  # type: ignore[arg-type]
            proc.wait(timeout=8)
        except Exception:
            proc.kill()


if __name__ == "__main__":
    main()
