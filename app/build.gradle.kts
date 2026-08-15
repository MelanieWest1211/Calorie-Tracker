plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	id ("com.google.devtools.ksp")
	id ("com.google.gms.google-services")
}

android {
	namespace = "com.example.caltrackcoursework"
	compileSdk = 36

	defaultConfig {
		applicationId = "com.example.caltrackcoursework"
		minSdk = 24
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	ksp("androidx.room:room-compiler:2.8.4")
	androidTestImplementation("androidx.test.ext:junit:1.3.0")
	androidTestImplementation("androidx.test:core:1.7.0")
	androidTestImplementation("androidx.test:runner:1.7.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
	implementation("androidx.room:room-runtime:2.8.4")
	implementation("androidx.room:room-ktx:2.8.4")
	implementation("androidx.datastore:datastore-preferences:1.2.1")
	implementation(platform("com.google.firebase:firebase-bom:34.13.0"))
	implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation("androidx.navigation:navigation-compose:2.9.8")
	implementation("androidx.compose.runtime:runtime-livedata:1.11.1")
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}