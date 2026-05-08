# 古韵薪传 · 微信小程序版（原生 + TypeScript）

本目录为「原生微信小程序」工程，与仓库 `web/` 保持 **设计 token / 5 Tab / mock 数据 / 资源** 对齐（不接真实后端）。

## 1. 打开项目（推荐）

在仓库根目录执行（Windows / macOS / Linux 均可；Windows 建议 PowerShell）：

```powershell
cd F:\commercial\guyunxinchuan
python .\miniprogram\start.py
```

脚本会尝试调用「微信开发者工具」CLI：

- `cli open --project <本目录绝对路径>`

### 前置条件

1. 已安装 **微信开发者工具**
2. 在开发者工具：**设置 → 安全设置** 开启 **服务端口 / CLI / HTTP 调用**（不同版本文案略有差异）
3. 若自动检测失败：设置环境变量 `WECHAT_DEVTOOLS_CLI` 指向 `cli.bat`（或 `cli.exe`）的完整路径，然后重试

## 2. 手动打开

1. 打开微信开发者工具
2. 导入项目，目录选择：`miniprogram/`（本 README 所在目录）

## 3. AppID 填写

编辑 `project.config.json`：

- 将 `appid` 从占位 `touristappid` 替换为你的小程序 AppID（测试可使用测试号/体验号策略，按微信官方流程）

> 说明：占位 AppID 仅用于本地预览/结构校验；真机预览/上传仍需有效 AppID 与权限配置。

## 4. 目录说明（精简）

- `app.json`：页面路由 + 自定义 tabBar
- `app.wxss`：全局 token + 工具类（对齐 web 视觉）
- `data/`：mock 数据（从 `web/src/data` 迁移）
- `images/`：位图资源（从 `web/public/images` 拷贝）
- `stores/`：`player/cart/session` 等轻量 store（`utils/store.ts`）
- `components/`：`cover` / `top-bar` / `sheet` / `icon` 等
- `custom-tab-bar/`：自定义 tabBar + MiniPlayer（仅 Tab 页常驻）

## 5. 已知取舍（与计划一致）

- 不接微信登录：沿用 `sessionStore` 的游客/体验账号语义
- 不接真实音频播放：进度由全局 ticker 模拟
- 不接埋点/国际化
