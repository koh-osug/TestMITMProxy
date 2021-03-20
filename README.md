# Summary

This is a test project connecting over a HTTPS proxy to [LittleProxy-mitm](https://github.com/koh-osug/LittleProxy-mitm)
or [PCAPdroid](https://github.com/emanuele-f/PCAPdroid) providing a VPN service.

# Patching 3rd party APKs

Since Android 7 3rd party applications are no longer using user defined CAs defined in the Android settings.
The [Network Security Configuration](https://developer.android.com/training/articles/security-config) has to be patched to make it work.
[Apktool](https://ibotpeaches.github.io/Apktool/) is necessary to do this.

## Steps (Example Opera):

 * Unpack APK: `./apktool d opera.apk`
 * Store the CA certificate of the MITM proxy under `res/raw/`: `cp ~/projects/LittleProxy-mitm/littleproxy-mitm.pem opera/res/raw/littleproxy` 
 * Edit the network security configuration XML `network_security_config_official_build.xml` under `res/xml/`:
 
~~~xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="@raw/littleproxy"/>
        </trust-anchors>
    </base-config>
</network-security-config>
~~~
 * Repack `./apktool b --use-aapt2 opera -o opera-patched.apk`
 * [zipalign](https://developer.android.com/studio/command-line/zipalign) `~/Android/Sdk/build-tools/30.0.3/zipalign -f -p 4 opera-patched.apk opera-patched2.apk`
 * Generate key store: `keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 10000`
 * Sign the APK: `~/Android/Sdk/build-tools/30.0.3/apksigner sign --ks my-release-key.keystore opera-patched2.apk`
 * Install `~/Android/Sdk/platform-tools/adb install opera-patched2.apk`

## Steps (Example FF)

 * Unpack APK: `./apktool d ff.apk`
 * Store the CA certificate of the MITM proxy under `res/raw/`: `cp ~/projects/LittleProxy-mitm/littleproxy-mitm.pem ff/res/raw/littleproxy`
 * Create the network security configuration XML `network_security_config.xml` under `res/xml/`
 * Patch the `AndroidManifest.xml` with the location of the network security configuration XML in the `application` element.
 * Repack `./apktool b --use-aapt2 ff -o ff-patched.apk`
 * Create key store (see above)
 * Sign the APK: `jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key.keystore ff-patched.apk alias_name`
 * Install `~/Android/Sdk/platform-tools/adb install ff-patched.apk`


