
# Fidel React Native bridge SDK

This SDK is a bridge between React Native and Fidel's native iOS and Android SDKs. It helps you to add card linking technology to your React Native apps in minutes. It captures credit/debit card numbers securely and links them to your programs.

![Demo GIF](https://cl.ly/a47b1852f029/Screen%252520Recording%2525202018-09-18%252520at%25252004.34%252520PM.gif)

## Getting started

`$ npm install fidel-react-native --save`

## (Almost) Automatic linking (for RN >= 0.60)

`$ react-native link fidel-react-native`

### iOS

**1.** Go to the `ios` folder. Please check that in your `Podfile` you have the following line:

`pod 'fidel-react-native', :path => '../node_modules/fidel-react-native'`

**2.** Please make sure that the minimum platform is set to 9.1 in your Podfile: `platform :ios, '9.1'`.

**3.** Run `pod install`.

### Android

**1.** Append Jitpack to `android/build.gradle`:

```java
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
```

**2.** Make sure that the `minSdkVersion` is the same or higher than the `minSdkVersion` of our native Android SDK:

```java
buildscript {
  ext {
    ...
    minSdkVersion = 19
    ...
  }
}
```

## Manual linking

### iOS manual linking

#### Step 1: Add the Fidel React Native iOS project as a dependency

**1.** In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`

**2.** Go to `node_modules` ➜ `fidel-react-native` and add `Fidel.xcodeproj`

**3.** In XCode, in the project navigator, select your project. Add `libFidel.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`

**4.** Under your target's `Build Settings`, make sure to set `YES` for `Always Embed Swift Standard Libraries`. That's because, by default, your project might not need it. It needs to be `YES` because otherwise the project will not find the Swift libraries to be able to run our native iOS SDK.

#### Step 2: Add the Native iOS SDK as a dependency

You can use Cocoapods or install the library as a dynamic library.

**5.** Add a `Podfile` in your `ios/` folder of your React Native project. It should include the following dependency:

```ruby
use_frameworks!
pod 'Fidel'
```

Here is the simplest `Podfile` you need to link with our native iOS SDK:

```ruby
platform :ios, '9.1'

workspace 'example'

target 'example' do
    use_frameworks!
    pod 'Fidel'
end
```

**6.** If you use an older Swift version, you should install a different `Fidel` pod version. If your project uses Swift `4.2.1`, for example, your Podfile should include `pod Fidel, '~>1.4'`, *not* `pod 'Fidel'` (which installs the latest version of the latest Swift supported version). Please check our [iOS SDK README (step 1)](https://github.com/FidelLimited/fidel-ios#step-1). You'll find a suitable version you should set for our Fidel iOS SDK.

**7.** Run `pod install` in your terminal.

**8.** Make sure to use the new `.xcworkspace` created by Cocoapods when you run your iOS app. React Native should use it by default.

**9.** In order to allow scanning cards with the camera, make sure to add the key `NSCameraUsageDescription` to your iOS app `Info.plist` and set the value to a string describing why your app needs to use the camera (e.g. "To scan credit cards."). This string will be displayed when the app initially requests permission to access the camera.

### Android manual linking

**1.** Append the following lines to `android/settings.gradle`:

```java
include ':fidel-react-native'
project(':fidel-react-native').projectDir = new File(rootProject.projectDir, '../node_modules/fidel-react-native/android')
```

**2.** Insert the following lines inside the dependencies block in `android/app/build.gradle`:

```java
implementation project(':fidel-react-native')
```

**3.** Append Jitpack to `android/build.gradle`:

```java
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
```

**4.** Make sure that the `minSdkVersion` is the same or higher than the `minSdkVersion` of our native Android SDK:

```java
buildscript {
  ext {
    ...
    minSdkVersion = 19
    ...
  }
}
```

**5.** Only for projects initialized with **RN <= 0.59**: Open up `android/app/src/main/java/[...]/MainApplication.java`

- Add `import com.fidelreactlibrary.FidelPackage;` to the imports at the top of the file
- Add `new FidelPackage()` to the list returned by the `getPackages()` method:

```java
protected List <ReactPackage> getPackages() {
  return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          new FidelPackage(),
          //you might have other Packages here as well.
      );
}
```

**6.** Ensure that you have *Google Play Services* installed.

For a physical device you need to search on Google for *Google Play Services*. There will be a link that takes you to the Play Store and from there you will see a button to update it (*do not* search within the Play Store).

## How to use Fidel's React Native SDK

Import Fidel in your RN project:

```javascript
import Fidel from 'fidel-react-native';
```

Set up Fidel with your API key and your program ID. Without them you can't open the Fidel card linking UI:

```javascript
Fidel.setup({
  apiKey:'your api key',
  programId: 'your program id'
})
```

Set the options that create the experience of linking a card with Fidel:

```javascript
const myImage = require('./images/fdl_test_banner.png');
const resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');
const resolvedImage = resolveAssetSource(myImage);

//this is the default value for supported card schemes,
//but you can remove the support for some of the card schemes if you want to
const cardSchemes = new Set([
  Fidel.CardScheme.visa,
  Fidel.CardScheme.mastercard,
  Fidel.CardScheme.americanExpress
]);

Fidel.setOptions({
  bannerImage: resolvedImage,
  country: Fidel.Country.sweden,
  supportedCardSchemes: Array.from(cardSchemes),
  autoScan: false,
  metaData: {'meta-data-1': 'value1'}, // additional data to pass with the card
  companyName: 'My RN Company', // the company name displayed in the checkbox text
  deleteInstructions: 'Your custom delete instructions!',
  privacyUrl: 'https://fidel.uk',
  termsConditionsUrl: 'https://fidel.uk/privacy', // mandatory when you set the default country to USA/Canada or when the user can select USA/Canada
  programName: 'My program name', // optional, is used when you set the default country to USA/Canada or when the user can select USA/Canada
});
```

Open the card linking view by calling:

```javascript
Fidel.openForm((error, result) => {
  if (error) {
    console.error(error);
  } else {
    console.info(result);
  }
});
```

Both `result` and `error` are objects that look like in the following examples:

### Result

```javascript
{
  accountId: "the-account-id"
  countryCode: "GBR" // the country selected by the user, in the Fidel SDK form
  created: "2019-04-22T05:26:45.611Z"
  expDate: "2023-12-31T23:59:59.999Z" // the card expiration date
  expMonth: 12 // for your convenience, this is the card expiration month
  expYear: 2023 // for your convenience, this is the card expiration year
  id: "card-id" // the card ID as registered on the Fidel platform
  lastNumbers: "4001" //last numbers of the card
  live: false
  mapped: false
  metaData: {meta-data-1: "value1"} //the meta data that you specified for the Fidel SDK
  programId: "your program ID, as specified for the Fidel SDK"
  scheme: "visa"
  type: "visa"
  updated: "2019-04-22T05:26:45.611Z"
}
```

### Error

```javascript
{
  code: "item-save" // the code of the error
  date: "2019-04-22T05:34:04.621Z" // the date of the card link request
  message: "Item already exists" // the message of the error
}
```

## Options documentation

### bannerImage

Use this option to customize the topmost banner image with the Fidel UI. Your custom asset needs to be resolved in order to be passed to our native module:

```javascript
const myImage = require('./images/fdl_test_banner.png');
const resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');
const resolvedImage = resolveAssetSource(myImage);
Fidel.setOptions({
  bannerImage: resolvedImage
}
```

### country

Set a default country the SDK should use with:

```javascript
Fidel.setOptions({
  country: Fidel.Country.unitedKingdom
});
```

When you set a default country, the card linking screen will not show the country picker UI. The other options, for now, are: `.unitedStates`, `.ireland`, `.sweden`, `.japan`, `.canada`.

### supportedCardSchemes

We currently support _Visa_, _Mastercard_ and _AmericanExpress_, but you can choose to support only one, two or all three. By default the SDK is configured to support all three.

If you set this option to an empty array or to `null`, of course, you will not be able to open the Fidel UI. You must support at least one our supported card schemes.

Please check the example below:

```javascript
//this is the default value for supported card schemes,
//but you can remove the support for some of the card schemes if you want to
const cardSchemes = new Set([
  Fidel.CardScheme.visa,
  Fidel.CardScheme.mastercard,
  Fidel.CardScheme.americanExpress
]);
Fidel.setOptions({
  supportedCardSchemes: Array.from(cardSchemes)
});
```

### autoScan

Set this property to `true`, if you want to open the card scanning UI immediately after executing `Fidel.openForm`. The default value is `false`.

```javascript
Fidel.setOptions({
  autoScan: true
});
```

### metaData

Use this option to pass any other data with the card data:

```javascript
Fidel.setOptions({
  metaData: {'meta-data-key-1': 'value1'}
});
```

### companyName

Set your company name as it will appear in our consent checkbox text. Please set it to a maximum of 60 characters.

```javascript
Fidel.setOptions({
  companyName: 'Your Company Name'
});
```

### deleteInstructions

Write your custom opt-out instructions for your users. They will be displayed in the consent checkbox text as well.

```javascript
Fidel.setOptions({
  deleteInstructions: 'Your custom card delete instructions!'
});
```

### privacyUrl

This is the privacy policy URL that you can set for the consent checkbox text.

```javascript
Fidel.setOptions({
  privacyUrl: 'https://fidel.uk',
});
```

### programName (applied to the consent text only for USA and Canada)

Set your program name as it will appear in the consent text. Note that **this parameter is optional** and used when you set United States or Canada as the default country or don't set a default country (meaning that the user is free to select United States or Canada as their country). Please set it to a maximum of 60 characters.

```javascript
Fidel.setOptions({
  programName: 'Your Program Name'
});
```

### termsConditionsUrl (applied to the consent text only for USA and Canada)

This is the terms & conditions URL that you can set for the consent text. Note that **this parameter is mandatory** when you set United States or Canada as the default country or don't set a default country (meaning that the user is free to select United States or Canada as their country).

```javascript
Fidel.setOptions({
  termsConditionsUrl: 'https://fidel.uk',
});
```

## Customizing the consent text

In order to properly set the consent text, please follow these steps:

1. Set the company name (please explain how it affects all consent texts, that it's optional and what's the default value)
2. Set the privacy policy URL (please explain how it affects all consent texts, that it's optional and what's the default value)
3. Set the delete instructions (please explain how it affects all consent texts, that it's optional and what's the default value)

Note that the consent text has a different form depending on the country you set or the country the user can select.

### Consent text for United States and Canada

When you set United States or Canada as the default country or don't set a default country (meaning that the user is free to select United States or Canada as their country), a specific consent text will be applied.

For USA & Canada, the following would be an example Terms & Conditions text for ```Cashback Inc``` (an example company) that uses ```Awesome Bonus``` as their program name:

*By submitting your card information and checking this box, you authorize ```card_scheme_name``` to monitor and share transaction data with Fidel (our service provider) to participate in ```Awesome Bonus``` program. You also acknowledge and agree that Fidel may share certain details of your qualifying transactions with ```Cashback Inc``` to enable your participation in ```Awesome Bonus``` program and for other purposes in accordance with the ```Cashback Inc``` Terms and Conditions, ```Cashback Inc``` privacy policy and Fidel’s Privacy Policy. You may opt-out of transaction monitoring on the linked card at any time by ```deleteInstructions```.*

There are two specific parameters that you can set for this consent text:

#### 1. termsConditionsUrl
This parameter is mandatory when you set United States or Canada as the default country or don't set a default country. When you set this parameter, the ```Terms and Conditions``` from the consent text will get a hyperlink with the URL you set.

```javascript
Fidel.setOptions({
  termsConditionsUrl: 'https://fidel.uk',
});
```

If you don't set this parameter, you'll get an error when trying to open the card linking interface: ```You have set a North American default country or you allow the user to select a North American country. For North American countries it is mandatory for you to provide the Terms and Conditions URL.```

#### 2. programName
This parameter is optional when you set United States or Canada as the default country or don't set a default country. If you don't set a program name, we'll use ```our``` as the default value (for example, in the text above, you would see *...to monitor and share transaction data with Fidel (our service provider) to participate in ```our``` program...*)

```javascript
Fidel.setOptions({
  programName: 'Your Program Name',
});
```

#### Consent text behaviour for card scheme name

If you don't set a card scheme (meaning the user can input either Visa, Mastercard or American Express cards) *OR* set 2 or 3 card scheme names, the default value used will be ```your payment card network``` (e.g. _you authorize ```your payment card network``` to monitor and share transaction data with Fidel (our service provider)_). When the user starts typing in a card number, ```your payment card network``` will be replaced with the scheme name of the card that they typed in (e.g. Visa).

If you set one card scheme name, it will be displayed in the consent text (e.g. for Mastercard it would be _you authorize ```Mastercard``` to monitor and share transaction data with Fidel (our service provider)_) This value - ```Mastercard``` - will not change when the user starts typing in a card number.

#### Consent text behaviour for privacy policy

Notice the following excerpt from the consent text above: _in accordance with the ```Cashback Inc``` Terms and Conditions, ```Cashback Inc``` privacy policy and Fidel’s Privacy Policy._ If you set a ```privacyUrl```, this is the text that will be displayed, along with a hyperlink set on *privacy policy*.

If you do not set a ```privacyUrl```, the text will become _in accordance with the ```Cashback Inc``` Terms and Conditions and Fidel’s Privacy Policy._


### Consent text for the rest of the world

When you set United Kingdom, Ireland, Japan or Sweden as the default country or the user selects one of these countries from the list, a consent text specific for these countries will be applied.

The following would be an example Terms & Conditions text for ```Cashback Inc``` (an example company):

*I authorise ```card_scheme_name``` to monitor my payment card to identify transactions that qualify for a reward and for ```card_scheme_name``` to share such information with ```Cashback Inc```, to enable my card linked offers and target offers that may be of interest to me. For information about ```Cashback Inc``` privacy practices, please see the privacy policy. You may opt-out of transaction monitoring on the payment card you entered at any time by ```deleteInstructions```.*

#### Consent text behaviour for card scheme name

If you don't set a card scheme (meaning the user can input either Visa, Mastercard or American Express cards) *OR* set 2 or 3 card scheme names, the default value used will be ```my card network``` (e.g. _I authorise ```my card network``` to monitor my payment card_). When the user starts typing in a card number, ```my card network``` will be replaced with the scheme name of the card that they typed in (e.g. Visa).

If you set one card scheme name, it will be displayed in the consent text (e.g. for Mastercard it would be _I authorise ```Mastercard``` to monitor my payment card_) This value - ```Mastercard``` - will not change when the user starts typing in a card number.

#### Consent text behaviour for privacy policy

If you do not set a privacy policy URL, the privacy policy related phrase will be removed from the text.

Notice the following excerpt from the consent text above: _...may be of interest to me. For information about ```Cashback Inc``` privacy practices, please see the privacy policy. You may opt-out of..._ If you set a ```privacyUrl```, this is the text that will be displayed, along with a hyperlink set on *privacy policy*.

If you do not set a ```privacyUrl```, the text will become _...may be of interest to me. You may opt-out of..._

## Localisation

The SDK's default language is English, but it's also localised for French and Swedish languages. When the device has either `Français (Canada)` or `Svenska (Sverige)` as its language, the appropriate texts will be displayed. Please note that developer error messages are in English only and they will not be displayed to the user.

Please make sure that your project also supports localisation for the languages that you want to support.

## Documentation

In the test environment please use our VISA, Mastercard or American Express test card numbers. You must use a test API Key for them to work.

VISA: _4444000000004***_ (the last 3 numbers can be anything)

Mastercard: _5555000000005***_ (the last 3 numbers can be anything)

American Express: _3400000000003**_ or _3700000000003**_ (the last 2 numbers can be anything)

## Feedback

The Fidel SDK is in active development, we welcome your feedback!

Get in touch:

GitHub Issues - For SDK issues and feedback
Fidel Developers Forum - [https://community.fidel.uk](https://community.fidel.uk) - for personal support at any phase of integration
