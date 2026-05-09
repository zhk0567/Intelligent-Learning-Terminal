# 将 web/public 下 images、audio、video 同步到 ECS（需本机 OpenSSH 的 scp）
# 用法（在仓库根目录）:
#   .\tools\deploy-static-to-ecs.ps1 -HostName 39.106.117.118 -User root -RemoteDir /var/www/ilt-static
param(
  [Parameter(Mandatory = $true)][string]$HostName,
  [Parameter(Mandatory = $true)][string]$User,
  [string]$RemoteDir = "/var/www/ilt-static",
  [string]$LocalRoot = ""
)
$ErrorActionPreference = "Stop"
if (-not $LocalRoot) { $LocalRoot = Join-Path $PSScriptRoot "..\web\public" }
$LocalRoot = (Resolve-Path $LocalRoot).Path
foreach ($d in @("images", "audio", "video")) {
  $src = Join-Path $LocalRoot $d
  if (-not (Test-Path $src)) {
    Write-Warning "跳过（目录不存在）: $src"
    continue
  }
  Write-Host "上传 $d -> ${User}@${HostName}:${RemoteDir}/"
  scp -r "${src}" "${User}@${HostName}:${RemoteDir}/"
}
Write-Host "完成。请在服务器上 mkdir -p $RemoteDir 并配置 nginx root 指向该目录。"
