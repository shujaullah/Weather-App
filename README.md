#weatherApp

Download and build the project in andriod studio, Open up the emulator (Pixel 4a is recommended), Allow location while use. Enter your preferred location in the search bar.

Enjoy!

gradle settings: plugins { id 'com.android.application' }

android { compileSdk 31 defaultConfig { applicationId "com.example.weatheapp" minSdk 21 targetSdk 31 versionCode 1 versionName "1.0" testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner" } buildTypes { release { minifyEnabled false proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro' } } compileOptions { sourceCompatibility JavaVersion.VERSION_1_8 targetCompatibility JavaVersion.VERSION_1_8 } }

dependencies { implementation 'androidx.appcompat:appcompat:1.2.0' implementation 'com.squareup.picasso:picasso:2.71828' implementation 'com.android.volley:volley:1.1.1' implementation 'com.google.android.gms:play-services-location:17.0.0' implementation 'com.google.android.material:material:1.3.0' implementation 'androidx.constraintlayout:constraintlayout:2.1.1' testImplementation 'junit:junit:4.+' androidTestImplementation 'androidx.test.ext:junit:1.1.2' androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0' }
