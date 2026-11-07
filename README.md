# InfinityExpansionAutomation

[![Build](https://github.com/balugaq/InfinityExpansionAutomation/actions/workflows/build.yml/badge.svg)](https://github.com/balugaq/InfinityExpansionAutomation/actions/workflows/build.yml)

为 [InfinityExpansion（无尽扩张）](https://github.com/SlimefunGuguProject/InfinityExpansion) 补上自动化的一环：新增专属分类与 2 台高耗电自动化机器——自动合成台与自动注入器，即装即用，未改动原有机制。

## 机器一览

| 机器 | 用途 | 能耗 |
| --- | --- | --- |
| 无尽自动合成台 | 放入目标物品，自动消耗材料批量完成无尽工作台配方 | 1,000,000 SC/t（缓存 10,000,000 SC） |
| 无尽自动注入器 | 放入目标物品，自动消耗材料批量完成生物注入器配方 | 1,000,000 SC/t（缓存 10,000,000 SC） |

两台机器的合成配方相同：无尽锻炉 + 高级自动合成台 / 原版自动合成台 + 虚空电容组合。

## 前置

- Minecraft 服务器（Paper 及其下游）
- [Slimefun4](https://github.com/SlimefunGuguProject/Slimefun4)（2025.1 及以上）
- [InfinityExpansion](https://github.com/SlimefunGuguProject/InfinityExpansion)
- [GuguSlimefunLib](https://github.com/TimetownDev/GuguSlimefunLib)

## 安装

1. 装好上述前置插件；
2. 从 [Releases](https://github.com/balugaq/InfinityExpansionAutomation/releases) 或[鬼斩资源站](https://resources.guizhanss.com/plugin/InfinityExpansionAutomation/versions) 下载插件 jar；
3. 放入服务器 `plugins/` 目录，重启服务器。

## 命令与配置

本插件没有命令与配置文件，装上即用。

## 构建

需要 JDK 21+：

```bash
./gradlew clean build
```

产物在 `build/libs/`。

## 兼容性

CI 会在多个 MC 版本上自动启动 Paper 服务器验证插件能否正常启用，见
[兼容性测试 workflow](https://github.com/balugaq/InfinityExpansionAutomation/actions/workflows/test-compatibility.yml)。

## 反馈

遇到问题请到 [Issues](https://github.com/balugaq/InfinityExpansionAutomation/issues) 提交反馈。

## 许可

本项目基于 [MIT License](LICENSE) 开源。
