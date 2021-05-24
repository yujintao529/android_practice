## 手动编译指南

###  前沿
由于本项目很大，不适合进行打包实验，所以用个非常简单，不依赖任何库的项目
### 编译资源

```
aapt2 compile  --dir app/src/main/res -v -o manual/resource/all_flat.zip
```

> 中间产物，二进制flat格式String no

### 链接资源
> 遗留TODO：1. gradle执行compile资源的逻辑，是否全部解析aar

```
#定义lib变量
ANDROID_LIB=$ANDROID_HOME/platforms/android-30/android.jar
--emit-ids manual/build/tmp --stable-ids -A app/src/main/assets/
#gradle 的配置
aaptOptions {
      additionalParameters  "--allow-reserved-package-id", "--package-id"," 0x50"
}

# --output-to-dir 这个参数控制把apk的内容解压出来到一个文件夹下，否则会输出一个apk

# 如果在style中使用了其他appcompat或者第三方资源时，直接link会有错误，因为第三方的资源没有参与compile，所以为了方便测试，手动修改了所有和第三方的资源的使用
# 我估计gradle执行过程中，会解压所有的aar，并compile相对应的资源，通过对app/build/intermediates/res/merged/下的资源查看，可以看出来compile所有的资源
aapt2 link -o manual/build/apk/test.apk manual/resource/all_flat.zip -I $ANDROID_LIB  --manifest app/src/main/AndroidManifest.xml --package-id 0x8A  --java manual/build/compile -v
```

### 编译classes 和 dex
> 把java文件路径全部放到一个文件里进行编译，最方便，
```

find app/src/main/java  package/java -type  f  -name "*.java" > package/sources.txt

javac @package/sources.txt -d package/build/classes  -cp $ANDROID_LIB


# 为什么第一个通配符不好用？？？
# d8主要功能转化class变成dex，解析java8语法为android平台语法，r8（混淆）
# 同时，d8支持增量编译
# d8 package/build/classes/*/*.class --lib $ANDROID_LIB --output package/apk --debug
d8 package/build/classes/com/bytedance/frameworks/manualpackage/*.class --lib $ANDROID_LIB --output package/apk --debug

# 打包
cd apk
zip -r my.apk ./*

# 签名
apksigner  sign --ks ~/.android/debug.keystore my.apk

```


TODO：gradle task里哪个task、是最后aapt compile, link,d8，javac等任务。