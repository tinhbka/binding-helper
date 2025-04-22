
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
implementation("com.github.tinhbka:binding-helper:1.0.1")
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
    BindingNotificationManager.onRestart(this, intent)
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    BindingNotificationManager.onRestart(this, intent)
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
          BindingNotificationManager.openNotificationSettings(this)
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

  static const _channel = MethodChannel('com.prank.call/flutter');

  static Future<void> enableNotification([bool isEnable = true]) async {
    if (!Global.instance.isFullAds) {
      return;
    }
    try {
      _channel.invokeMethod('enableNotification', {'isEnable': isEnable});
    } catch (e) {
      logger.e(e);
    }
  }

  static Future<void> openNotificationSettings() async {
    try {
      await _channel.invokeMethod('openNotificationSettings');
    } catch (e) {
      logger.e(e);
    }
  }

  /// Đặt nội dung cho thông báo
  static Future<void> setNotificationContent() async {
    if (!Global.instance.isFullAds) {
      return;
    }
    await Future.delayed(const Duration(milliseconds: 500));
    try {
      await _channel.invokeMethod(
        'setNotificationContent',
        {
          'title': appContext.l10n.aPrankIsWaiting,
          'message': appContext.l10n.openAppAndTryIt,
        },
      );
    } catch (e) {
      logger.e(e);
    }
  }

  /// Đặt nội dung tạm thời cho thông báo
  /// nếu muốn đặt lại nội dung mặc định thì gọi hàm [clearTemporaryContent]
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

  /// Xóa nội dung tạm thời
  static Future<void> clearTemporaryContent() async {
    try {
      await _channel.invokeMethod('clearTemporaryContent');
    } catch (e) {
      logger.e(e);
    }
  }

  /// Đặt thời gian delay cho thông báo khi ẩn app
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

  /// Custom tên cho các event
  /// THêm 1 key remote cho phép custom tên cho các event, value là
  /// {
  ///   "exitApp": "recent_app",
  ///   "repeat5m": "repeat_5m",
  ///   "exitAppInDay": "exit_app",
  ///   "exitApp30m": "recent_app_30m"
  /// }
  static Future<void> setupEventsName() async {
    final data = RemoteConfigManager.instance.notificationEventName;
    final Map<String, dynamic> json = jsonDecode(data) as Map<String, dynamic>;
    try {
      await _channel.invokeMethod(
        'setupEventsName',
        json,
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
  NativeChannel.enableNotification();
},  
```


- Hàm `openNotificationSettings`: Nếu người dùng đã từ chối quyền thông báo 2 lần, lần sau yêu cầu quyền thông báo sẽ không hiện được popup thông báo của hệ thống, gọi hàm `openNotificationSettings` để mở setting thông báo của app

- Hàm `setNotificationContent`: setup nội dung của thông báo, gọi ở màn splash và khi thay đổi ngôn ngữ