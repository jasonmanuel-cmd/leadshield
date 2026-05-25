import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.leadshield.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.leadshield.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Default subscription tier
        buildConfigField("int", "SUBSCRIPTION_TIER", "0") // 0=Free, 1=Pro, 2=Master
        buildConfigField("String", "GEMINI_API_KEY", "\"${providers.gradleProperty("GEMINI_API_KEY").getOrElse("")}\"")
        buildConfigField("String", "NEON_DATA_API_URL", "\"${providers.gradleProperty("NEON_DATA_API_URL").getOrElse("")}\"")
        buildConfigField("String", "NEON_API_KEY", "\"${providers.gradleProperty("NEON_API_KEY").getOrElse("")}\"")
        buildConfigField("String", "MASTER_GOD_MODE_PASSWORD", "\"${providers.gradleProperty("MASTER_GOD_MODE_PASSWORD").getOrElse("")}\"")
        buildConfigField("String", "BILLING_VERIFY_URL", "\"${providers.gradleProperty("BILLING_VERIFY_URL").getOrElse("")}\"")
        buildConfigField("boolean", "ENABLE_STRICT_BILLING_VERIFICATION", "false")

        // Set version code suffix for each flavor
        flavorDimensions.add("subscription")
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }

                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = rootProject.file(keystoreProperties["storeFile"].toString())
                storePassword = keystoreProperties["storePassword"].toString()
            } else {
                // Fallback to gradle properties or empty
                keyAlias = "mctb-release"
                keyPassword = providers.gradleProperty("MCTB_RELEASE_KEY_PASSWORD").getOrElse("")
                storeFile = rootProject.file("mctb-release-key.jks")
                storePassword = providers.gradleProperty("MCTB_RELEASE_STORE_PASSWORD").getOrElse("")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Use release signing configuration
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "ENABLE_STRICT_BILLING_VERIFICATION", "true")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "LeadShield Debug")
        }
    }

    // Product flavors for different subscription tiers
    productFlavors {
        create("free") {
            dimension = "subscription"
            // Canonical Play Store package: com.leadshield.app
            versionNameSuffix = ""
            buildConfigField("int", "SUBSCRIPTION_TIER", "0")
            buildConfigField("int", "MAX_TEXTS", "10")   // 10 auto-replies / month
            buildConfigField("boolean", "SHOW_ADS", "true")
            resValue("string", "app_name", "LeadShield")
        }
        create("pro") {
            dimension = "subscription"
            applicationIdSuffix = ".pro"
            versionNameSuffix = "-pro"
            buildConfigField("int", "SUBSCRIPTION_TIER", "1")
            buildConfigField("int", "MAX_TEXTS", "999999")  // unlimited
            buildConfigField("boolean", "SHOW_ADS", "false")
            resValue("string", "app_name", "LeadShield Pro")
        }
        create("operator") {
            dimension = "subscription"
            applicationIdSuffix = ".operator"
            versionNameSuffix = "-operator"
            buildConfigField("int", "SUBSCRIPTION_TIER", "2")
            buildConfigField("int", "MAX_TEXTS", "999999")
            buildConfigField("boolean", "SHOW_ADS", "false")
            resValue("string", "app_name", "LeadShield Operator")
        }
        create("master") {
            dimension = "subscription"
            // Internal/developer build — no suffix
            buildConfigField("int", "SUBSCRIPTION_TIER", "3")
            buildConfigField("int", "MAX_TEXTS", "999999")
            buildConfigField("boolean", "SHOW_ADS", "false")
            resValue("string", "app_name", "LeadShield Master")
        }
        create("voice") {
            dimension = "subscription"
            applicationIdSuffix = ".voice"
            versionNameSuffix = "-voice"
            buildConfigField("int", "SUBSCRIPTION_TIER", "4")
            buildConfigField("int", "MAX_TEXTS", "999999")
            buildConfigField("boolean", "SHOW_ADS", "false")
            resValue("string", "app_name", "LeadShield Voice")
        }
        create("team") {
            dimension = "subscription"
            applicationIdSuffix = ".team"
            versionNameSuffix = "-team"
            buildConfigField("int", "SUBSCRIPTION_TIER", "5")
            buildConfigField("int", "MAX_TEXTS", "999999")
            buildConfigField("boolean", "SHOW_ADS", "false")
            resValue("string", "app_name", "LeadShield Team")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        abortOnError = false
        warningsAsErrors = false
        checkReleaseBuilds = true
        disable += setOf("MissingTranslation", "ExtraTranslation")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Accompanist (for permissions)
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // Billing
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    // Gemini AI
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // WorkManager
    val work_version = "2.9.1"
    implementation("androidx.work:work-runtime-ktx:$work_version")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Network Stack (for Cloud Sync)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
