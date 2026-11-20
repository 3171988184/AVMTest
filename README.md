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

## 六、同功能回放页代码量对比

X24Z01 回放页在 8 个渠道里有 23 个 Java 副本、4,427 行。按每个类最大副本行数估算，一套参数化实现的保守基线是 3 个 Java 文件、595 行：

| 同功能回放页 | X24Z01（8 渠道副本） | 单套实现基线 | 减少 |
|---|---:|---:|---:|
| Java 文件 | 23 个 | 3 个 | 87.0% |
| Java 行数 | 4,427 行 | 595 行 | 86.6% |
| layout 文件 | 29 个 | 3 个 | 89.7% |
| layout 行数 | 1,445 行 | 151 行 | 89.6% |

595/151 的口径：取每类副本中的最大行数（RecordMainActivity 224、RecordListFragment 286、PlaybackListAdapter 85；layout 61、66、24）。这是保守基线，实际重构还要加少量 profile 分支，但不会回到 4,427 行。

新增一个渠道的成本：

- X24：复制约 3 个 Java（595 行）+ 3 个 layout（151 行），约 6 个文件、746 行；
- 单套实现：只新增 1 份 profile（约 48 行），页面代码不改。

Demo 的 157 行只用于验证一套 Activity/Fragment 能跑 3 个 profile，不作为同功能减少比例。

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
