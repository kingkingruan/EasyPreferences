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



## API

### Annotations

There are eight annotations in the `easy preferences-annotations` artifact:

`@Preferences` - This annotation can be used on the interface which define methods to set , get ,remove or clear value. You can set the value to define the name of SharedPreferences, if not will use the name of interface.

`@Key` - This annotation can be used on the methods of setter, getter and remove to define the value of key.

`@Default` - This annotation can be used on the methods of getter and its value is the default value of getter.

`@All` - This annotation can be used on the method to get the all values of SharedPreferences. The return type of method must be Map<String,?> to match the result of origin method.

`@Apply` - The method of setter uses 'commit()' api to submit the value. This annotation is used to switch to 'apply()' api.

`@Clear` - This annotation can be used on the method which clears all values.

`@Remove` - This annotation can be used on the method which remove a key.

`@Converter` - This annotation can be used on the method which to save custom class. The parameter must implement the interface of IConvert.

### Interface

There is only one interface `IConvert` that support methods to transform between class and string so We can save all types of object. For example , we can define a class to transform json.

```java
public class UserConverter implements IConvert<User> {
    @Override
    public String convertToString(User value) {
        return new Gson().toJson(value);
    }

    @Override
    public User convertFromString(String value) {
        return new Gson().fromJson(value, User.class);
    }
}
```

then we can use this on setter method of User

```java
@Preferences
public interface SimplePreferences {

    @Converter(UserConverter.class)
    void setUser(User user);

    @Converter(UserConverter.class)
    User getUser();
}
```



## Sample

Define an interface , and then Use class EasyPreferences to get the instance of interface.

```java
@Preferences
public interface SimplePreferences {
    void setLong(long value);
    Long getLong();

    void setFloat(Float value);
    float getFloat();

    @Apply
    void setSetString(Set<String> stringSet);
    Set<String> getSetString();

    @Default("hello")
    String getWelcome();

    @Default("0")
    int getDafaultInt();
    
    @Default({"hh", "gg"})
    Set<String> getDefaultSetString();

    @Converter(UserConverter.class)
    void setUser(User user);

    @Converter(UserConverter.class)
    User getUser();

    @Remove("string")
    void removeString();

    @Clear
    void clear();

    @All
    Map<String,?> getAll();
}
```

```java
public class MainActivity extends AppCompatActivity {
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimplePreferences simplePreferences = EasyPreferences.getSharedPreferences(this, SimplePreferences.class);
        simplePreferences.setInt(120);
        simplePreferences.removeString();
        User user = simplePreferences.getUser();
        Map<String,?> allValues = simplePreferences.getAll();
        simplePreferences.clear();
    }
}
```



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