/**
 * 与 ECS/Nginx 上静态目录一致的服务根地址，勿尾斜杠。
 * 例：https://assets.example.com（须在微信后台配置合法域名）。
 * 仅公网 IP、无域名：微信正式版无法把 http(s)://IP 配进合法域名，请保持 `""`，资源走包内；
 * 真机调试用开发者工具「不校验合法域名」仅开发有效。
 */
export const STATIC_ORIGIN = "";
