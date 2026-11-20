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

## 六、Demo 验证范围（不是同页面代码对比）

源码仓库：

https://github.com/3171988184/AVMTest

必须说明：Demo 的影像辅助页和回放页是为了验证结构重新画的简化原型，**不是从 X24Z01 复制的实际页面**。

- Demo 影像辅助页：自绘 AVM 占位 + 动态功能标签 + 相机网格；
- Demo 回放页：模拟录像列表；
- X24Z01 的对应页面实际是 `MainActivity` + `Fragment`（如 `RecordListFragment`）+ `RecyclerView`/`CustomScrollBar`，页面结构和资源都不同。

因此，不能拿 Demo 的 6 个 Java 文件 / 408 行去和 X24Z01 的 `MainActivity`/`RecordMainActivity` 直接算“同功能代码减少 88.7%”。原始对比表已撤回，避免把原型页面当成实际页面。

Demo 只证明一件事：**3 个渠道包可以共用 1 套 Activity，通过 profile + 2 套 layout 呈现不同相机数、列数、回放条数和布局形态。**

如果要做真正的同页面代码对比，需要先把 X24Z01 的 `activity_main`、`activity_record_main`、`fragment_record_list`、`item_playback` 及其依赖资源迁入 Demo，再逐文件统计。

## 七、边界

这是 UI/配置验证，不含真实 AVM 拼接、CAN、录像文件、native 标定；回放列表是模拟数据。  
目标只是验证：**渠道包只换 profile/资源，不复制 Activity；同一 Activity 可以通过 layout/profile 呈现不同页面。**
