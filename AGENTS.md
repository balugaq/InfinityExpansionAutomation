# AGENTS.md — InfinityExpansionAutomation 操作宪章

> 本文件是 AI 编程助手与本仓库开发者的**操作契约**。所有改动（代码、构建、文档）均须遵守本文约定；
> 当本文与代码冲突时，按末尾《文档更新契约》处理。

## TL;DR（太长了不看）

1. 本仓库是 **Minecraft Paper + Slimefun4 附属插件**（Java 21 / Gradle Kotlin DSL），所有改动必须通过 `./gradlew clean build` 验收后才能提交，禁止提交未编译验证的代码。
2. 本插件极小（4 个类）：改完跑构建即可覆盖全部编译验收；运行时行为需在装齐三前置的服务器上验证。
3. 直接在 `master` 分支开发（个人仓库），提交信息遵循 Conventional Commits（`feat:`/`fix:`/`docs:`/`refactor:`/`chore:`）。

---

## 1. 项目身份与边界

| 维度 | 事实 |
| --- | --- |
| 定位 | Minecraft Paper 服务器插件，**Slimefun4（GuguProject fork）附属**，为 InfinityExpansion 补充自动化机器 |
| 入口类 | `me.ddggdd135.infinityexpansionautomation.InfinityExpansionAutomation`（`extends JavaPlugin implements SlimefunAddon`） |
| 版本目标 | Java 21 字节码（`options.release = 21`）、Paper API 1.16.5（旧坐标 `com.destroystokyo.paper:paper-api`）、Slimefun 2025.1、`api-version: 1.16` |
| 仓库策略 | 本地仓库即开发仓库（个人使用），直接在 `master` 开发；远端 `balugaq/InfinityExpansionAutomation` |
| AI 职责范围 | 编写/修改功能代码、Bug 修复、构建/CI 维护、文档维护 |
| AI 不负责 | 发布版本、推送远端（推送动作由开发者执行） |

### 1.1 包地图（改代码前先定位）

| 类 | 职责 |
| --- | --- |
| `...InfinityExpansionAutomation` | 主类：`onEnable` 里先确保 InfinityExpansion 被启用，再调用 `InfinityExpansionAutomationItems.onSetup(this)` 注册全部内容 |
| `...items.InfinityExpansionAutomationItemGroups` | 唯一 ItemGroup「无尽自动化」的定义 |
| `...items.InfinityExpansionAutomationItems` | 两台机器的 `SlimefunItemStack` 定义与注册入口；含 `mobDataInfuserType()` 反射适配（见 3.1） |
| `...slimefun.InfinityCrafter` | 核心机器实现：通用「按目标物品的配方自动合成」tick 机器（`TickingBlock` + `MachineProcessHolder<CraftingOperation>` + `EnergyNetComponent` 消费端） |

---

## 2. 技术栈与项目结构（事实核对）

- **构建**：Gradle Kotlin DSL（`build.gradle.kts`）+ Shadow 插件；`defaultTasks("clean", "build")`；产物 `build/libs/InfinityExpansionAutomation-<version>.jar`（无 relocation，没有需要 shade 的运行时依赖）。
- **依赖**（全部 `compileOnly`，version catalog 见 `gradle/libs.versions.toml`）：
  - `com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT` —— **注意**：1.16.5 的 paper-api 只存在于这个旧 group 坐标，`io.papermc.paper:paper-api` 没有 1.16.5；
  - `com.github.SlimefunGuguProject:Slimefun4:2025.1`（原 2024.3，2026-11-07 升级并做 API 适配）；
  - `com.github.SlimefunGuguProject:InfinityExpansion:3c5db3650a`（原 bebf0bd0f9）；
  - `com.github.TimetownDev:GuguSlimefunLib:45627c6f8e`（原 c5f1d405d4）。
- **仓库源**：`mavenCentral()`、Paper、JitPack、Sonatype。**无本地 lib/，不要新增私服配置。**
- **配置**：仅 `src/main/resources/plugin.yml`（`depend: [Slimefun, InfinityExpansion, GuguSlimefunLib]`）。无 config.yml、无命令、无语言文件。
- **机器设计**：`InfinityCrafter` 是通用引擎，两台机器只是传入了不同的 `MachineRecipeType`（无尽工作台 / 生物注入器），目标物品的配方类型必须匹配该 `MachineRecipeType` 才会被接受。
- **CI**：`build.yml`（JDK 21，push/PR 到 master 触发）、`release.yml`（`gradle.properties` 的 `version=` 变更时自动打 tag 发布）、`test-compatibility.yml`（矩阵 `1.20.1,26.2`，从鬼斩资源站下载 Slimefun4 / InfinityExpansion、从 JitPack 下载 GuguSlimefunLib 后真机启动验证 Enabling）。

---

## 3. 决策树：当遇到 A 时，执行 B

### 3.1 新增/修改机器

- **当新增一台机器时**：在 `InfinityExpansionAutomationItems` 定义 `SlimefunItemStack`（能耗用 `LoreBuilder.powerPerSecond/powerBuffer`），复用 `InfinityCrafter` 传入对应 `MachineRecipeType` 注册；只有当机器逻辑与「放目标物品→自动合成」不同时才新建类（参照 `InfinityCrafter` 的 tick/processor/掉落三件套）。
- **当需要引用 InfinityExpansion 内部机器的 `MachineRecipeType` 时**：先用 `javap` 确认该字段的可见性——新版已把 `MobDataInfuser.TYPE` 改为包私有，此时用 `InfinityExpansionAutomationItems.mobDataInfuserType()` 的反射模式取出原实例（配方注册在原实例上，重新 new 一个拿不到配方），并保持注释说明原因。
- **当升级 Slimefun4 / InfinityExpansion / GuguSlimefunLib 版本时**：先在 JitPack 验证坐标可解析（JitPack 对不存在的版本会 404，注意区分「没构建过」和「构建失败」，看 build.log），再升级并跑 `./gradlew clean build` 逐个修编译错误；用 `javap` 核对被调用 API 的签名是否仍存在。
- **当访问 Slimefun 方块数据时**：统一走 `StorageCacheUtils` / `SlimefunBlockData`（参照 `InfinityCrafter` 的 `BlockBreakHandler` 用法），不要绕过 Slimefun 存储层。

### 3.2 能耗与平衡

- 机器能耗在注册处 `.setEnergyConsumption(...)` 一处设定，`InfinityCrafter` 的 `getCapacity/getEnergyConsumption` 复用同一字段（现状：容量与单次消耗同为 500000×2，物品 lore 上写的 1,000,000/t / 10,000,000 缓存与字段语义需注意一致）。
- 改能耗数值时同步更新 `InfinityExpansionAutomationItems` 里 lore 的 `powerPerSecond/powerBuffer`。

### 3.3 异常与日志

- 本插件没有语言文件与配置系统；日志用 `getLogger()`，禁止 `System.out.println` 与吞异常。
- 反射取 `MobDataInfuser.TYPE` 失败时抛 `IllegalStateException` 快速失败，不要静默降级。

---

## 4. 构建与验收

- 本地验收：`./gradlew clean build`，BUILD SUCCESSFUL 即编译验收通过；产物 major 必须是 65（Java 21）。
- 修改 workflow / 构建脚本后：
  - 确认 `plugin.yml` 的 `version: '${version}'` 占位符不被改回 `${project.version}`；
  - `gradlew` 的 git mode 必须保持 `100755`（`git ls-files -s gradlew` 核查）；三个 workflow 首次调用 `./gradlew` 前都有 `chmod +x gradlew`，不要删。
- 兼容性测试手动触发：Actions → Test Plugin Compatibility → 填逗号分隔的 MC 版本。

---

## 5. 红线

1. 禁止把编译目标降到 Java 21 以下（Slimefun4 2025.1 依赖链要求）。
2. 禁止改动物品 ID（`INFINITY_CRAFTER` / `INFINITY_INFUSER`），会导致玩家已放置方块与注册项失联。
3. 禁止引入 `implementation` 依赖而不做 shade/relocation 评估。
4. 禁止提交 `gradle.properties` 之外的本机绝对路径配置（如 `org.gradle.java.home`）。
5. 禁止 `git push -f`、改写历史；提交权归开发者。
6. 静态验收不得启动浏览器或任何渲染/截图工具。

---

## 6. 已知债务 / 注意点

- `api-version: 1.16` 与 Slimefun4 2025.1 的实际运行时要求（Java 21、较新 MC）存在跨度：编译目标是 1.16.5 API，但实际可用服务器范围以 Slimefun4 2025.1 为准，README 兼容性以 CI 实测为准。
- 资源站暂无 GuguSlimefunLib（`test-compatibility.yml` 从 JitPack 拉取兜底）；若资源站上架可改回资源站源。
- 资源站插件页 `https://resources.guizhanss.com/plugin/InfinityExpansionAutomation/versions` 尚未上架（404），README 中该链接待上架后生效。

---

## 7. 文档更新契约

- 改动机器数量、能耗、前置版本、构建方式时，同步更新 `README.md` 与本文对应小节。
- 本文与代码冲突时，以代码为准并立即修订本文，不要带着矛盾继续开发。
