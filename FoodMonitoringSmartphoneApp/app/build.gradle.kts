plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.tpw.foodmonitoringapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tpw.foodmonitoringapp"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    val fragment_version = "1.6.2"

    implementation ("com.google.android.gms:play-services-code-scanner:16.1.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.jjoe64:graphview:4.2.2")

    // https://mvnrepository.com/artifact/com.mailjet/mailjet-client
    implementation ("com.mailjet:mailjet-client:4.2.0")

    // https://mvnrepository.com/artifact/javax.mail/mail
    implementation ("javax.mail:mail:1.4.7")






    implementation("androidx.fragment:fragment:$fragment_version")

    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.firebaseui:firebase-ui-database")
    implementation("com.firebaseui:firebase-ui-database:8.0.2")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}