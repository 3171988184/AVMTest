param(
    [ValidateSet("c12", "d15_tf", "q12")]
    [string]$Variant = "c12"
)

$ErrorActionPreference = "Stop"
$adb = "D:\Android\sdk\platform-tools\adb.exe"

$map = @{
    "c12"    = @{ Package = "com.example.avmtest.c12";   Apk = "c12\debug\app-c12-debug.apk" }
    "d15_tf" = @{ Package = "com.example.avmtest.d15tf"; Apk = "d15_tf\debug\app-d15_tf-debug.apk" }
    "q12"    = @{ Package = "com.example.avmtest.q12";   Apk = "q12\debug\app-q12-debug.apk" }
}

$root = Split-Path -Parent $PSScriptRoot
$apk = Join-Path $root "app\build\outputs\apk\$($map[$Variant].Apk)"
$package = $map[$Variant].Package

if (-not (Test-Path $apk)) {
    throw "APK not found: $apk`nRun: .\gradlew.bat :app:assemble$((Get-Culture).TextInfo.ToTitleCase($Variant))Debug"
}

Write-Host "1/4 disable car package verifier (test device only)"
& $adb shell pm disable-user --user 0 com.autoxt.service.base

try {
    Write-Host "2/4 install $apk"
    & $adb install -r $apk

    Write-Host "3/4 launch $package"
    & $adb shell am force-stop $package
    & $adb shell am start -n "$package/com.example.avmtest.MainActivity"
}
finally {
    Write-Host "4/4 restore car package verifier"
    & $adb shell pm enable --user 0 com.autoxt.service.base
}
