# EasyPreferences

Easy to use SharedPreferences

Use Annotations and Compile-time Processing to generate the code , so you can save value just with methods.

## Version

| Module         | easypreferences                                              | easypreferences-compiler                                     |
| -------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Latest Version | [ ![Download](https://api.bintray.com/packages/kkdandan/maven/easypreferences/images/download.svg) ](https://bintray.com/kkdandan/maven/easypreferences/_latestVersion) | [ ![Download](https://api.bintray.com/packages/kkdandan/maven/easypreferences-compiler/images/download.svg) ](https://bintray.com/kkdandan/maven/easypreferences-compiler/_latestVersion)|

## Download

depend via Maven:

```
<dependency>
  <groupId>com.kkdandan</groupId>
  <artifactId>easypreferences</artifactId>
  <version>*.*.*</version>
  <type>pom</type>
</dependency>

<dependency>
  <groupId>com.kkdandan</groupId>
  <artifactId>easypreferences-compiler</artifactId>
  <version>*.*.*</version>
  <type>pom</type>
</dependency>
```

or Gradle

```
compile 'com.kkdandan:easypreferences:*.*.*'
compile 'com.kkdandan:easypreferences-compiler:*.*.*'
```



## Sample

Define an interface , and then Use class EasyPreferences to get the instance of interface.

    @Preferences
    public interface SimplePreferences {
        void setCheckVersion(int version);
        int getCheckVersion();
    }
    
    public class MainActivity extends AppCompatActivity {
        private static final String TAG = "MainActivity";
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            SimplePreferences simplePreferences = EasyPreferences.getSharedPreferences(this, SimplePreferences.class);
            simplePreferences.setInt(120);
            Log.e(TAG, "int " + simplePreferences.getInt());
            simplePreferences.setString("Hello, World!");
            Log.e(TAG, "string " + simplePreferences.getString());
            simplePreferences.setBoolean(true);
            Log.e(TAG, "boolean " + String.valueOf(simplePreferences.getBoolean()));
            simplePreferences.setLong(222);
            Log.e(TAG, "long " + String.valueOf(simplePreferences.getLong()));
            simplePreferences.setFloat(0.234f);
            Log.e(TAG, "float " + String.valueOf(simplePreferences.getFloat()));
            Set<String> stringSet = new HashSet<>();
            stringSet.add("set1");
            stringSet.add("set2");
            simplePreferences.setSetString(stringSet);
            Log.e(TAG, "stringSet " + simplePreferences.getSetString());
            simplePreferences.clear();
            Log.e(TAG, "string " + simplePreferences.getString());
        }
    }

## Current Features

- get and set types of int long boolean float

## TODO

- [x] check the setter and getter's type
- [x] support String
- [x] remove key
- [x] clear all keys
- [x] define the name of SharedPreferences
- [x] support setting default value
- [x] support using method of Apply to submit
- [x] support  type of Set<String>
- [x] support all types
- [x] support compile
- [x] support boolean when commit the value
- [x] support customize key
- [x] buffer instance of Preference
- [ ] multi-thread concurrency

## Other

- **WeChat:**  helloworld12
- **Email:**   rjjdick@gmail.com