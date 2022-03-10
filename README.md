App
---

### Setup
- Device Manager > Virtual > Create device
- File > Settings
- Appearance > Use custom font: Inconsolata, Size: 14
- Editor > Font: Inconsolata, Size: 14
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
- Set newer version in `rAudio/app/build.gradlee (Module: rAudio.app)`
  - versionCode N
  - versionName "N.0"
- Build > Generate Signed Bubled / APK
	- Android App Bundle: for upload
	- APK: for sideload
- Signing key
	- New app:
		- Create new > ... > Remember passwords
		- Important! - Must be backup/kept the same for app signing for every version
			- Key store: file + password
			- Key: alias + password
	- Upgrade version:
		- Existing (or upload will be rejected)
			- Key store: file + password
			- Key: alias + password

### Upload
- Google Play Console
- New app:
	- Optional - Testing > `Create new release` > Upload
	- Production > `Create new release` > Upload
	- Wait for review and approval by Google
- Upgrade version:
	- Production > `Create new release` > Upload
