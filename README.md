# Intelligent Learning Terminal

基于 Android + Kotlin + Jetpack Compose 构建的“非遗音乐学习终端”示例项目，聚焦“内容浏览 + 互动 + 学习 + 商城 + 个人中心”的一体化体验，并持续进行性能与稳定性优化。

---

## 1. 项目定位

本项目面向移动端非遗音乐内容场景，核心目标是：

- 以现代化 UI 呈现传统音乐内容（音乐馆、故事、社区）。
- 提供基础互动能力（点赞、评论、分享、收藏、标签跳转）。
- 提供学习与消费闭环（内容推荐、详情页、商城详情态管理）。
- 保持可演进的模块化工程结构，便于后续接入真实后端服务。

---

## 2. 核心特性

- **模块化架构**：`app` + `core` + `feature` 分层，职责清晰。
- **Compose UI**：全局采用 Jetpack Compose + Material3。
- **导航体系**：主 Tab 与二级页面分层导航，支持单次入栈策略。
- **性能优化**：
  - 列表与详情页统一懒加载（`LazyColumn` / 网格分页优化）。
  - 高频更新区域（播放器进度等）局部状态化，降低无效重组。
  - 搜索输入防抖、复杂背景绘制降本、页签预加载控制。
- **状态健壮性**：详情页完善 `Loading / Empty / Error / Content` 态。
- **资源体系**：音乐馆“每日热门 / 每日精选”已改为透明 PNG 资源方案。
- **NFC 深链基础能力**：支持通过 NFC 标签触发 App 打开入口。

---

## 3. 技术栈

- **语言**：Kotlin
- **UI**：Jetpack Compose、Material3
- **导航**：Navigation Compose
- **播放器**：ExoPlayer
- **图片加载**：Coil
- **异步**：Kotlin Coroutines + Flow
- **数据持久化**：DataStore
- **网络**：Retrofit / OkHttp
- **构建**：Gradle（KTS）、Android Gradle Plugin 8.x

---

## 4. 目录结构

```text
Intelligent_learning_terminal/
├─ HeritageMusic/                  # Android 主工程
│  ├─ app/                         # Application、导航、主入口、通用页面承接
│  ├─ core/
│  │  ├─ resources/                # 资源与字符串
│  │  ├─ ui/                       # 主题、通用 UI 组件、布局常量
│  │  ├─ data/                     # 仓库、模型、运行时配置
│  │  └─ network/                  # 网络模块与接口装配
│  ├─ feature/
│  │  ├─ music-hall/               # 音乐馆首页与承接页
│  │  ├─ stories/                  # 故事流、故事详情
│  │  ├─ community/                # 社区流、帖子详情
│  │  ├─ mall/                     # 商城列表、商品详情
│  │  ├─ profile/                  # 我的、设置、隐私、通知等
│  │  └─ player/                   # 播放器页面与播放控制
│  └─ settings.gradle.kts          # 模块声明
└─ docs/
   └─ 子页面后续开发计划单.md       # 计划与阶段验收记录
```

---

## 5. 模块清单（来自 `settings.gradle.kts`）

- `:app`
- `:core:resources`
- `:core:ui`
- `:core:data`
- `:core:network`
- `:feature:music-hall`
- `:feature:stories`
- `:feature:community`
- `:feature:mall`
- `:feature:profile`
- `:feature:player`

---

## 6. 业务功能说明

### 6.1 音乐馆（Music Hall）

- Banner 推荐与快捷入口。
- 每日热门、每日精选、猜你喜欢标签区。
- “查看更多 / 标签承接页”导航链路。
- 搜索防抖与局部重组优化。
- 交互体验入口（智能编曲 / AI 点评）与日志统计看板。

### 6.2 故事（Stories）

- 故事流分页加载与瀑布流展示。
- 故事详情页懒加载化改造。
- 互动操作区（点赞 / 分享 / 评论输入）。

### 6.3 社区（Community）

- 帖子流与帖子详情。
- 详情页交互操作区统一样式。

### 6.4 商城（Mall）

- 商品列表与商品详情。
- 详情状态管理：加载态、空态、错误态、重试。

### 6.5 我的（Profile）

- 个人中心主页面。
- 通知设置、隐私设置、关于、图片许可页。
- DataStore 配置读取与生命周期安全收集。

### 6.6 播放器（Player）

- 播放、暂停、拖动进度等基础能力。
- 高频进度更新区域独立状态，优化帧率与交互延迟。

---

## 7. 开发环境要求

- Windows 10/11（推荐）
- JDK 17
- Android Studio（近期稳定版）
- Android SDK（按 `compileSdk` 要求安装）
- Gradle Wrapper（项目内置，无需手动全局安装）

---

## 8. 快速开始（Windows PowerShell）

在仓库根目录执行：

```powershell
cd .\HeritageMusic
.\gradlew :app:assembleDebug
```

如需安装到设备（已连接 ADB）：

```powershell
.\gradlew :app:installDebug
```

---

## 9. 常用构建命令

```powershell
# 仅编译 Kotlin（快速校验）
.\gradlew :app:compileDebugKotlin

# 构建 Debug APK
.\gradlew :app:assembleDebug

# 清理后重建
.\gradlew clean
.\gradlew :app:assembleDebug
```

---

## 10. 关键运行配置（性能相关）

项目中引入了可开关的运行时配置以平衡“开发调试可观测性”与“流畅度”：

- `USE_REMOTE_API`：是否使用远端接口数据。
- `ENABLE_NETWORK_LOGGING`：是否启用网络日志拦截。
- `ENABLE_FAKE_DELAY`：是否启用模拟延迟。

建议：

- **联调定位问题**：可临时开启日志与延迟模拟。
- **日常开发/演示**：关闭不必要开销，保证交互顺滑。

---

## 11. NFC 打开应用说明

项目已配置 NFC 相关能力（权限、特性与 intent-filter），可通过 NFC 标签触发应用入口。

建议写入标签内容时使用应用可识别的深链格式，并确保：

- Android 设备开启 NFC；
- 标签写入内容与应用 manifest 中声明规则匹配；
- 已安装可处理该深链的当前应用版本。

---

## 12. 性能与稳定性优化摘要

近期已完成的一批优化方向包括：

- 主界面与详情页滚动容器懒加载化，降低长页面卡顿。
- 播放器高频状态更新局部化，减少整页重组。
- 主导航去重入栈（`launchSingleTop` 语义），减少返回栈膨胀。
- 绘制层优化（复杂背景缓存与条件绘制），降低 GPU/CPU 压力。
- Android Studio 构建兼容与 Gradle/JVM 参数调优，提升构建稳定性。

---

## 13. 常见问题（FAQ）

### Q1：改了资源但界面没变化？

- 执行 `.\gradlew clean` 后重装 `debug` 包。
- 必要时先卸载旧应用再安装，避免旧缓存影响。

### Q2：Android Studio 构建异常但命令行可过？

- 优先用命令行确认真实错误。
- 检查 Gradle 缓存路径、IDE 同步状态、JDK 版本是否一致。

### Q3：页面切换/滑动卡顿？

- 先确认 `ENABLE_NETWORK_LOGGING` 和 `ENABLE_FAKE_DELAY` 已关闭。
- 检查是否引入了新的高频重组或大图资源。

---

## 14. 文档与计划

- 开发计划与阶段记录：`docs/子页面后续开发计划单.md`
- 页面截图回归基线：`docs/页面截图回归基线清单.md`

## 14.1 Week 4 交互体验演示说明

- 入口：音乐馆搜索栏右侧“互动”按钮。
- 编曲链路：输入风格/情绪/BPM -> 生成片段 -> 试听/收藏按钮反馈。
- 点评链路：输入音频名/关注维度 -> 生成点评 -> 展示节奏/音准/表现力与建议。
- 统计看板：互动主页可查看最近事件（含时间戳）和核心计数（生成、试听、收藏、点评）。

## 14.2 演示检查流程（可直接执行）

在 Windows PowerShell 进入 `HeritageMusic` 目录后，建议按以下顺序执行：

1. **快速编译校验**
   - `.\gradlew :app:compileDebugKotlin`
2. **功能一：资料库链路**
   - 音乐馆点击“资料库” -> 执行筛选/搜索 -> 进入详情 -> 检查来源时间轴与版权标签 -> 点击关联故事/曲目
3. **功能二：课程链路**
   - 音乐馆点击“课程” -> 切换层级筛选 -> 进入详情 -> 打开课时目录/教材资料/师资主页 -> 检查关联跳转
4. **功能三：商城链路**
   - 商城进入任一分区 -> 使用关键词与排序 -> 进入商品详情 -> 从故事/课程详情验证“内容 -> 商品”联动
5. **功能四：交互体验链路**
   - 音乐馆点击“互动” -> 编曲生成并点击试听/收藏 -> AI 点评生成 -> 返回互动主页刷新统计，确认计数与最近事件变化

---

## 15. 许可证与说明

本项目用于学习与演示。若引入第三方素材/图标，请在发布前补充版权与许可证信息，并在应用内或仓库中保留必要声明。
