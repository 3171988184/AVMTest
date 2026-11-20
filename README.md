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

## 六、和 X24Z01 同功能范围对比

源码仓库：

https://github.com/3171988184/AVMTest

只对比“影像辅助页 + 回放列表页”的页面壳，不含录像播放、标定、BSD、CAN、CameraService。

| 对比项 | X24Z01（同功能页面壳） | AVMTest Demo | 变化 |
|---|---:|---:|---:|
| 页面入口 Java 文件 | 15 个（MainActivity 8 + RecordMainActivity 7） | 6 个（1 个 MainActivity + 5 个支撑类） | -60% |
| 页面入口 Java 行数 | 3,609 行 | 408 行 | -88.7% |
| 同功能 layout 文件 | 39 个（含 hdpi） | 2 个 | -94.9% |
| 同功能 layout 行数 | 1,615 行 | 249 行 | -84.6% |
| 渠道配置 | 8 个 flavor 复制页面 | 3 份 profile JSON（48 行） | 配置化 |
| 页面入口 Activity | 15 份（8+7） | 1 份 MainActivity | 1 套 |

注意：这是**页面壳/车型适配层**的对比，不是整个 X24Z01 工程的代码下降。Demo 的影像是自绘占位，回放是模拟列表，不含真实 AVM/CAN/录像/标定。

## 七、边界

这是 UI/配置验证，不含真实 AVM 拼接、CAN、录像文件、native 标定；回放列表是模拟数据。  
目标只是验证：**渠道包只换 profile/资源，不复制 Activity；同一 Activity 可以通过 layout/profile 呈现不同页面。**
