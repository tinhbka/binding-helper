## Get Started

Set up thư viện bên Android Native

### 1. Thêm thư viện vào `app/build.gradle`

```
implementation("com.github.tinhbka:binding-helper:0.0.7")
```

### 2. Sử dụng

#### Ở Android

- Tạo MainApplication có nội dung như sau

```
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

```
<application android:name=".MainApplication" ...
```

- Tạo MethodChannel ở MainActivity

```
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

        else -> {
          result.notImplemented()
        }

      }
    }
  )
```

#### Ở Flutter

Tạo MethodChannel

```

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

```
fullAdCallback: (isFullAd) {
  Global.instance.isFullAds = isFullAd;
  if (Global.instance.isFullAds) {
    NativeChannel.enableNotification();
  }
},
```

- Hàm `openNotificationSettings`: Nếu người dùng đã từ chối quyền thông báo 2 lần, lần sau yêu cầu
  quyền thông báo sẽ không hiện được popup thông báo của hệ thống, gọi hàm
  `openNotificationSettings` để mở setting thông báo của app

- Hàm `setNotificationContent`: setup nội dung của thông báo, gọi ở màn splash và khi thay đổi ngôn
  ngữ