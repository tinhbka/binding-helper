
## Get Started
Set up thư viện bên Android Native
### 1. Thêm thư viện
- Thêm jitpack vào `android/build.gradle`
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url 'https://jitpack.io'
        }
    }
}
```
- Thêm thư viện vào `android/app/build.gradle`
```gradle
implementation("com.github.tinhbka:binding-helper:1.0.0")
```

### 2. Sử dụng
#### Ở Android
- Tạo MainApplication có nội dung như sau
```kotlin
class MainApplication : FlutterApplication() {
    override fun onCreate() {
        super.onCreate()
        BindingNotificationManager.init(
            this,
            MainActivity::class.java,
            R.mipmap.ic_launcher, // icon của thông báo
        )
    }
}
```

- Khai báo `MainApplication` ở thẻ `application` trong `AndroidManifest.xml`

```xml
<application android:name=".MainApplication" ...
```

- Thêm `BindingNotificationManager.onRestart(this)` vào trong hàm `onCreate` và `onNewIntent`
```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ...
    BindingNotificationManager.onRestart(this)
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    BindingNotificationManager.onRestart(this)
  }
```

- Tạo MethodChannel ở MainActivity


```kotlin
  val channel : String = "channel_name"

  ...
  MethodChannel(
      flutterEngine.dartExecutor.binaryMessenger, channel
  ).setMethodCallHandler(
    { call, result ->
      when (call.method) {
        "enableNotification" -> {
          val isEnable = call.argument<Boolean>("isEnable")
          BindingNotificationManager.setEnableNotifications(isEnable ?: false)
          result.success(null)
        }

        "openNotificationSettings" -> {
          openNotificationSettings(this)
          result.success(null)
        }

        "setNotificationContent" -> {
          val title = call.argument<String>("title")
          val message = call.argument<String>("message")
          if (title != null && message != null) {
            BindingNotificationManager.buildBackgroundNotification(
              title = title,
              message = message
            )
          }
        }
         "setTemporaryContent" -> {
            val title = call.argument<String>("title")
            val message = call.argument<String>("message")
            if (title != null && message != null) {
              BindingNotificationManager.setTemporaryContent(
                title = title,
                message = message
              )
            }
          }

          "clearTemporaryContent" -> {
            BindingNotificationManager.clearTemporaryContent()
          }


          "setDelayTime" -> {
            val delayInSecond = call.argument<Long>("delayInSecond")
            if (delayInSecond != null) {
              BindingNotificationManager.setDelayTime(
                delayInSecond = delayInSecond
              )
            }
          }

          "setupEventsName" -> {
            val exitApp = call.argument<String>("exitApp")
            val repeat5m = call.argument<String>("repeat5m")
            val exitAppInDay = call.argument<String>("exitAppInDay")
            val exitApp30m = call.argument<String>("exitApp30m")
            BindingNotificationManager.setupEventsName(
              exitApp = exitApp,
              repeat5m = repeat5m,
              exitAppInDay = exitAppInDay,
              exitApp30m = exitApp30m,
            )
          }

        else -> {
          result.notImplemented()
        }
      }
    }
  )
```


#### Ở Flutter
Tạo MethodChannel
```dart

class NativeChannel {
  NativeChannel._();

  static const _channel = MethodChannel('channel_name');

  static Future<void> enableNotification([bool isEnable = true]) async {
    if (!Global.instance.isFullAds) {
      return;
    }
    try {
      _channel.invokeMethod('enableNotification', {'isEnable': isEnable});
    } catch (e) {
      debugPrint(e.toString());
    }
  }

  static Future<void> openNotificationSettings() async {
    try {
      await _channel.invokeMethod('openNotificationSettings');
    } catch (e) {
      print('Error opening settings: $e');
    }
  }

  static Future<void> setNotificationContent() async {
    if (!Global.instance.isFullAds) {
      return;
    }
    await Future.delayed(const Duration(milliseconds: 500));
    try {
      await _channel.invokeMethod(
        'setNotificationContent',
        {
          'title': appContext.l10n.didYouLogExpenses,
          'message': appContext.l10n.logExpensesReminder,
        },
      );
    } catch (e) {
      logger.e(e);
    }
  }
  static Future<void> setTemporaryContent({
    String? title,
    String? message,
  }) async {
    if (!Global.instance.isFullAds) {
      return;
    }
    try {
      await _channel.invokeMethod(
        'setTemporaryContent',
        {
          'title': title,
          'message': message,
        },
      );
    } catch (e) {
      logger.e(e);
    }
  }

  static Future<void> clearTemporaryContent() async {
    try {
      await _channel.invokeMethod('clearTemporaryContent');
    } catch (e) {
      logger.e(e);
    }
  }

  static Future<void> setDelayTime(int second) async {
    try {
      await _channel.invokeMethod(
        'setDelayTime',
        {
          'delayInSecond': second,
        },
      );
    } catch (e) {
      logger.e(e);
    }
  }

  static Future<void> setupEventsName({
    String? exitApp,
    String? repeat5m,
    String? exitAppInDay,
    String? exitApp30m,
  }) async {
    try {
      await _channel.invokeMethod(
        'setupEventsName',
        {
          'exitApp': exitApp,
          'repeat5m': repeat5m,
          'exitAppInDay': exitAppInDay,
          'exitApp30m': exitApp30m,
        },
      );
    } catch (e) {
      logger.e(e);
    }
  }
  static void setMethodCallHandler() {
    _channel.setMethodCallHandler((call) async {
      switch (call.method) {
        default:
          break;
      }
    });
  }
}
```

- Hàm `enableNotification` gọi sau khi check full ads
  Chỉ hiện thông báo ở bản full ads
```dart
fullAdCallback: (isFullAd) {
  Global.instance.isFullAds = isFullAd;
  if (Global.instance.isFullAds) {
    NativeChannel.enableNotification();
  }
},  
```


- Hàm `openNotificationSettings`: Nếu người dùng đã từ chối quyền thông báo 2 lần, lần sau yêu cầu quyền thông báo sẽ không hiện được popup thông báo của hệ thống, gọi hàm `openNotificationSettings` để mở setting thông báo của app

- Hàm `setNotificationContent`: setup nội dung của thông báo, gọi ở màn splash và khi thay đổi ngôn ngữ