# AVMTest Profile Demo

在 `D:\work\SL2403_APP\AVMTest` 空项目上做的验证：**一套 Activity + 三份 profile + 两套 layout**，只保留“影像辅助”和“回放”两个页面。

## 一、三个渠道包

| flavor | 包名 | profile | 页面形态 | 相机 | 回放条数 |
|---|---|---|---|---|---|
| c12 | `com.example.avmtest.c12` | `c12.json` | 紧凑上下布局 | 4 路 / 2 列 | 3 |
| d15_tf | `com.example.avmtest.d15tf` | `d15_tf.json` | 大屏左右布局 | 6 路 / 3 列 | 5 |
| q12 | `com.example.avmtest.q12` | `q12.json` | 紧凑上下布局 | 5 路 / 3 列 | 4 |

三个包共用同一套 `MainActivity`、`AssistPreviewView`、`ProfileStore`、`FeatureBinder`、`ViewCatalog`，没有复制 Activity。

## 二、代码结构

```text
app/src/main/java/com/example/avmtest/
├─ MainActivity.java        // 唯一页面入口，读 profile 后渲染
├─ ProfileStore.java        // 读取 assets/profiles/*.json
├─ DeviceProfile.java       // features/cameras/layout/gridColumns/playbackCount
├─ ViewCatalog.java         // 语义视角 -> 平台 ViewId
├─ FeatureBinder.java       // 按 features 动态生成功能标签
└─ AssistPreviewView.java   // 自绘 AVM 预览，按控件宽高缩放

app/src/main/res/layout/
├─ activity_main_compact.xml    // c12/q12：上下布局
└─ activity_main_expanded.xml   // d15_tf：左右分栏

app/src/c12/assets/profiles/c12.json
app/src/d15_tf/assets/profiles/d15_tf.json
app/src/q12/assets/profiles/q12.json
```

`app/build.gradle` 增加：

- `flavorDimensions "vehicle"`
- 三个 `productFlavors`
- 每个 flavor 的 `applicationId`
- 每个 flavor 的 `BuildConfig.PROFILE_ASSET`

## 三、构建

```powershell
cd D:\work\SL2403_APP\AVMTest
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleC12Debug :app:assembleD15_tfDebug :app:assembleQ12Debug --offline
```

APK：

```text
app/build/outputs/apk/c12/debug/app-c12-debug.apk
app/build/outputs/apk/d15_tf/debug/app-d15_tf-debug.apk
app/build/outputs/apk/q12/debug/app-q12-debug.apk
```

## 四、设备验证

设备：`rk3576m_u_faw_d2000_15`，1920×1080，Android 14。

三个 APK 都安装并启动成功，截图在 `docs/screenshots/`。

| 渠道包 | 启动命令 |
|---|---|
| c12 | `adb shell am start -n com.example.avmtest.c12/com.example.avmtest.MainActivity` |
| d15_tf | `adb shell am start -n com.example.avmtest.d15tf/com.example.avmtest.MainActivity` |
| q12 | `adb shell am start -n com.example.avmtest.q12/com.example.avmtest.MainActivity` |

设备带有车厂安装校验服务 `com.autoxt.service.base`，直接 `adb install` 会报 `INSTALL_FAILED_VERIFICATION_FAILURE`。验证时临时停用该服务，安装完成后已恢复：

```powershell
adb shell pm disable-user --user 0 com.autoxt.service.base
adb install -r app-c12-debug.apk
adb shell pm enable --user 0 com.autoxt.service.base
```

这是设备安装策略，不是应用代码绕过。

## 五、验证结果

- c12：4 路相机、2 列、3 条回放，紧凑布局；
- d15_tf：6 路相机、3 列、5 条回放，左右分栏；
- q12：5 路相机、3 列、4 条回放，紧凑布局；
- 三个包共用同一套 Activity 代码；
- 影像辅助页和回放页都能切换；
- 无 FATAL EXCEPTION。

## 六、回放页：可运行，但功能不等价

源码仓库：

https://github.com/3171988184/AVMTest

不能把 Demo 的 3 个 Java 文件 / 157 行，当成 X24 的同功能替代。Demo 只是回放页的简化壳。

| 功能项 | X24Z01 回放页 | Demo 回放页 |
|---|---|---|
| USB/TF 插拔 | UsbUtil、UsbStateChangeListener | 无 |
| 目录/文件扫描 | PlaybackPresenterImpl、多级目录 | 无，固定 mock 列表 |
| 真实视频播放 | PlaybackVideoActivity | 无，模拟计时画面 |
| 滚动条 | CustomScrollBar | 无 |
| 无文件/无 TF 状态 | 有 | 无 |
| 格式化/开关设置 | 真实 Fragment | 占位演示页 |
| 退出/广播 | CloseAssistReceiver | 无 |

所以下面只是“代码规模参考”，不是同功能代码减少：

| 项目 | X24Z01 | Demo | 说明 |
|---|---:|---:|---|
| Java 文件 | 23 个 | 3 个 | 功能不等价 |
| Java 行数 | 4,427 行 | 157 行 | 仅页面壳 |
| layout 文件 | 29 个 | 3 个 | 仅页面壳 |
| layout 行数 | 1,445 行 | 119 行 | 仅页面壳 |

Demo 另外新增了模拟播放器、格式化、开关设置页，但这些不在上面的 3 个 Java/3 个 layout 里；即使加上，也不等于 X24 的完整回放逻辑。

影像辅助页不参与对比：X24 画面来自 `CameraService`/native，Demo 只是占位。

## 七、回放页交互说明

- 点击录像卡片：进入模拟播放器，有播放/暂停、进度条、时间、返回；
- 顶部 `影像回放`：显示录像列表；
- `格式化`：显示格式化演示页；
- `开关设置`：按 profile.features 显示 AVM/BSD 等开关；
- 每个 profile 只生成 3/5/4 条录像，不再把一条录像按相机拆成多条。

注意：Demo 没有真实录像文件，播放器是计时模拟，不是真正的 H.264/H.265 解码播放；真实播放需要接入录像文件 + MediaPlayer/ExoPlayer。

对比表只统计回放列表主体（PlaybackActivity/PlaybackListFragment/PlaybackAdapter + 三个 layout），不含新增的模拟播放器、格式化、开关设置演示页。

## 八、Android Studio 安装报错

Android Studio 直接 Run 报：

```text
INSTALL_FAILED_VERIFICATION_FAILURE: Verification timed out
```

这不是 APK 问题，是设备上的车厂安装校验服务 `com.autoxt.service.base` 超时。测试机可以这样安装：

```powershell
adb shell pm disable-user --user 0 com.autoxt.service.base
adb install -r app/build/outputs/apk/c12/debug/app-c12-debug.apk
adb shell pm enable --user 0 com.autoxt.service.base
```

项目里有脚本：

```powershell
.\tools\install_demo.ps1 -Variant c12
.\tools\install_demo.ps1 -Variant d15_tf
.\tools\install_demo.ps1 -Variant q12
```

脚本会临时停用校验服务、安装、启动 APP，最后恢复服务。只建议在测试机上这样做。

## 九、边界

这是 UI/配置验证，不含真实 AVM 拼接、CAN、录像文件、native 标定；回放列表是模拟数据。  
目标只是验证：**渠道包只换 profile/资源，不复制 Activity；同一 Activity 可以通过 layout/profile 呈现不同页面。**
