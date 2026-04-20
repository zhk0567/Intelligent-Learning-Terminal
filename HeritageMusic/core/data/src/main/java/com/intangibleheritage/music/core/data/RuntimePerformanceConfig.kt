package com.intangibleheritage.music.core.data

/**
 * 运行期开关：用于控制演示环境中的额外开销（如模拟 delay）。
 */
object RuntimePerformanceConfig {
    @Volatile
    var enableFakeDelay: Boolean = false
}
