# Plausible Android

This is an **unofficial** Android SDK to record events with a [Plausible](https://plausible.io) backend.

## Usage

Add the following dependency to your project's build to integrate the SDK.gradle file:

```xml
implementation 'com.github.OneBusAway:plausible-android-sdk:3.3'
```
### Configuration

Initialize the SDK by specifying your own `domainURL` for event transmission. If you're using a custom instance, you can also define the hostURL.

```Java
Plausible plausible = new Plausible(this, domainURL, hostURL);
```

By default, the SDK will be enabled at app startup, though you can prevent this to allow users to
opt-in or opt-out like so:

```xml
<string name="plausible_enable_startup">false</string>
```

You can then manually enable the sdk with the following:

```java
Plausible.enable(true)
```

### Sending Events

#### Page Views

```java
plausible.pageView("/settings")
```

#### Custom Events

```java
plausible.event("ctaClick")
```

## License

    Copyright 2022 William Patrick Brawner, 2025 Open Transit Software Foundation

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
