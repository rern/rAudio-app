App
---

### Setup
- Device Manager > Virtual > Create device
- File > Settings
- Appearance > Use custom font: Inconsolata, Size: 14
- Editor
  - Font: Inconsolata, Size: 14
  - Code style: Line separator: Unix and MacOS
- Select Hardware > Phone > Pixel 2
- System Image > Release Name > S (x86_64) > Download

### Project
- New Project > Phone and Tablet > Empty Activitiy
- Name: rAudio
- Package Name: com.raudio
- Language: Java
- Maximum SDK: Android 23

### Files
- `rAudio/app/src/main/AndroidManifest.xml`
- `rAudio/app/src/main/res/layout/activity_main.xml`
- `rAudio/app/src/main/java/com/raudio/MainActivity.java`
- `rAudio/app/build.gradle (Module: rAudio.app)` - `minifyEnabled true`

### Icons
- rAudio/app > New > Image Asset
- Source > 512 x 512 icon.png
- Delete all existing `*.wepp`s

### Install file
- Build > Generate Signed Bundle / APK...
    - Android App Bundle
    - APK
- Key store path > Create new > ... > Remember passwords
- Destination folder > release

### Upload `*.AAB`
- Set newer version in `rAudio/app/build.gradle (Module: rAudio.app)`
  - versionCode N
  - versionName "N.0"
- [Google Play Console](https://play.google.com/console/about/)
  - Production > Edit release > App bundles > Upload > Release notes > `Save`
  - `Review release` > `Start rollout to production`

### Tips
- Get IP from URL
```java
 public String getIP( String url ) {
    String ip = null;
    try {
        InetAddress host = InetAddress.getByName( url );
        ip = host.getHostAddress();
        System.out.println(host.getHostAddress());
    } catch (UnknownHostException ex) {
        ex.printStackTrace();
    }
    return ip;
}
```
