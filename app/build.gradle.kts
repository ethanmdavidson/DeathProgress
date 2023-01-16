plugins {
	id("com.android.application")
	id("kotlin-android")
	id("io.gitlab.arturbosch.detekt").version("1.22.0")
}

android {
	compileSdk = 33

	defaultConfig {
		applicationId = "com.machinerychorus.lifeprogresswallpaper"
		minSdk = 16
		targetSdk = 33
		versionCode = 13
		versionName = "2.3.1"
		multiDexEnabled = true

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}

		resourceConfigurations.addAll(listOf("en"))
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
		isCoreLibraryDesugaringEnabled = true
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	packagingOptions {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	namespace = "com.machinerychorus.lifeprogresswallpaper"
}

dependencies {
	implementation("androidx.core:core-ktx:1.9.0")
	implementation("androidx.appcompat:appcompat:1.6.0")
	implementation("com.google.android.material:material:1.7.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
	implementation("androidx.preference:preference-ktx:1.2.0")
	implementation("com.github.skydoves:colorpickerpreference:2.0.6")

	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.0")
}

detekt {
	buildUponDefaultConfig = true // preconfigure defaults
	allRules = true // activate all available (even unstable) rules.

	reports {
		html.enabled = false
		xml.enabled = false
		txt.enabled = false
		sarif.enabled = true
	}
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
	// Target version of the generated JVM bytecode. It is used for type resolution.
	jvmTarget = "11"
}
