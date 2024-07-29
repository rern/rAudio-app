App
---

### Setup
- Device Manager > Virtual > Create device
- File > Settings
- Appearance > Use custom font: Inconsolata, Size: 16
- Editor
  - Font: Inconsolata, Size: 16
  - Code style: Line separator: Unix and MacOS
- Side menu > Device Manger > Create device > Pixel 2
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
- Set newer version in `rAudio/app/build.gradle (Module: rAudio.app)`
  - versionCode N
  - versionName "yyymmdd"
- Build > Generate Signed Bundle / APK...
  - Android App Bundle (for upload)
  - APK                (for local install)
- Key store path > Create new > ... > Remember passwords
- Destination folder > release

### Upload `*.aab`
- [Google Play](https://play.google.com/console/about/) > `Go to Play Console`
  - Select App
  - Production > Create new release > App bundles > Upload `app-release.aab` > Release notes - add > Save
  - Review release > Send to review / Start rollout to production

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
