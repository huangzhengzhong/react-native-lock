
# react-native-lock

## Getting started

`$ npm install react-native-lock --save`

### Mostly automatic installation

`$ react-native link react-native-lock`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.ht.lock.RNLockPackage;` to the imports at the top of the file
  - Add `new RNLockPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-lock'
  	project(':react-native-lock').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-lock/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-lock')
  	```


## Usage
```javascript
import RNLock from 'react-native-lock';

// TODO: What to do with the module?
RNLock;
```
  