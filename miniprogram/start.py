"""古韵薪传 微信小程序版启动入口。

用法（在仓库根目录）：
    python miniprogram/start.py

行为：
    - 自动定位「微信开发者工具」CLI（cli.bat / cli）；
    - 调用 `cli open --project <miniprogram 绝对路径>` 打开本目录；
    - 失败时打印手动打开指引。

前置条件：
    1) 已安装「微信开发者工具」；
    2) 在开发者工具：设置 → 安全设置 → 开启「服务端口 / CLI / HTTP 调用」（不同版本文案略有差异）。
"""

from __future__ import annotations

import argparse
import os
import shutil
import subprocess
import sys
from pathlib import Path

MINIPROGRAM_DIR = Path(__file__).resolve().parent
IS_WINDOWS = os.name == "nt"


def _try_registry_cli() -> Path | None:
    if not IS_WINDOWS:
        return None
    try:
        import winreg  # type: ignore
    except Exception:
        return None

    keys = [
        (winreg.HKEY_CURRENT_USER, r"Software\Tencent\微信web开发者工具"),
        (winreg.HKEY_LOCAL_MACHINE, r"Software\Tencent\微信web开发者工具"),
        (winreg.HKEY_LOCAL_MACHINE, r"Software\WOW6432Node\Tencent\微信web开发者工具"),
    ]
    for root, sub in keys:
        try:
            with winreg.OpenKey(root, sub) as k:
                install_path, _ = winreg.QueryValueEx(k, "InstallPath")
        except OSError:
            continue
        base = Path(install_path)
        for name in ("cli.bat", "cli.exe", "cli"):
            p = base / name
            if p.exists():
                return p
    return None


def _find_wechat_devtools_cli() -> str | None:
    candidates: list[Path] = []

    env = os.environ.get("WECHAT_DEVTOOLS_CLI")
    if env:
        candidates.append(Path(env))

    if IS_WINDOWS:
        candidates.extend(
            [
                Path(r"C:\Program Files (x86)\Tencent\微信web开发者工具\cli.bat"),
                Path(r"C:\Program Files (x86)\Tencent\微信web开发者工具\cli.exe"),
                Path(r"C:\Program Files\Tencent\微信web开发者工具\cli.bat"),
                Path(r"C:\Program Files\Tencent\微信web开发者工具\cli.exe"),
            ]
        )

    reg = _try_registry_cli()
    if reg:
        candidates.append(reg)

    which = shutil.which("cli") or shutil.which("cli.bat")
    if which:
        candidates.append(Path(which))

    for p in candidates:
        try:
            if p and p.exists():
                return str(p)
        except OSError:
            continue
    return None


def main() -> None:
    parser = argparse.ArgumentParser(description="古韵薪传 微信小程序版启动器")
    parser.parse_args()

    if not MINIPROGRAM_DIR.exists():
        print(f"[启动失败] 未找到小程序目录：{MINIPROGRAM_DIR}")
        sys.exit(1)

    cli_exe = _find_wechat_devtools_cli()
    if not cli_exe:
        print("\n[启动失败] 未找到微信开发者工具 CLI。")
        print("请确认：")
        print("  1) 已安装「微信开发者工具」；")
        print("  2) 已在「设置 → 安全设置」开启 CLI/HTTP 调用；")
        print("  3) 或将 CLI 路径写入环境变量 WECHAT_DEVTOOLS_CLI（例如 cli.bat 的完整路径）。")
        print(f"\n然后手动打开：{MINIPROGRAM_DIR}")
        sys.exit(1)

    cmd = [cli_exe, "open", "--project", str(MINIPROGRAM_DIR)]
    print(f"[启动] {' '.join(cmd)}")
    try:
        subprocess.run(cmd, check=True, cwd=str(MINIPROGRAM_DIR), shell=False)
    except subprocess.CalledProcessError as e:
        print(f"\n[启动失败] CLI 返回码：{e.returncode}")
        print("请检查微信开发者工具是否已安装/可启动，以及是否已开启 CLI 调用。")
        print(f"\n可手动打开目录：{MINIPROGRAM_DIR}")
        sys.exit(e.returncode)

    print(f"\n[就绪] 已请求微信开发者工具打开项目：{MINIPROGRAM_DIR}")


if __name__ == "__main__":
    main()
