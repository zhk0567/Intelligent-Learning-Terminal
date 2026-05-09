/**
 * 与 ECS 静态站、Web `VITE_STATIC_ORIGIN`、Android `STATIC_ASSET_ORIGIN` 同源（勿尾斜杠）。
 * 图 / 音频 / 视频一律经此域名拉取，**不要**在小程序包内放大图或 mp3/mp4。
 *
 * - 换公网 IP / 上 HTTPS：改本常量，并在微信公众平台配置 **downloadFile / request 合法域名**。
 * - `project.config.json` 的 `packOptions.ignore` 已排除本仓库内 `miniprogram/audio`、`miniprogram/images`：**上传/预览包不含这些文件**，须依赖本常量指向的静态站；若改为 `""` 且无本地拷贝，图音会加载失败。
 */
export const STATIC_ORIGIN = "http://39.106.117.118";
