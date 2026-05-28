import org.gradle.api.JavaVersion

plugins {
	id("com.android.application")
	id("io.gitlab.arturbosch.detekt").version("1.23.8")
}

android {
	compileSdk = 37

	defaultConfig {
		applicationId = "com.machinerychorus.lifeprogresswallpaper"
		minSdk = 23
		targetSdk = 36
		versionCode = 18
		versionName = "2.7.0"
		multiDexEnabled = true

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	androidResources {
		@Suppress("UnstableApiUsage")
		localeFilters += "en"
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro",
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
		isCoreLibraryDesugaringEnabled = true
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	namespace = "com.machinerychorus.lifeprogresswallpaper"
}

dependencies {
	implementation("androidx.core:core-ktx:1.18.0")
	implementation("androidx.appcompat:appcompat:1.7.1")
	implementation("com.google.android.material:material:1.14.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
	implementation("androidx.preference:preference-ktx:1.2.1")
	implementation("com.github.skydoves:colorpickerpreference:2.0.6")

	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.3.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
}

detekt {
	buildUponDefaultConfig = true // preconfigure defaults
	allRules = true // activate all available (even unstable) rules.
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
	// Target version of the generated JVM bytecode. It is used for type resolution.
	jvmTarget = "11"
	reports {
		html.required.set(false)
		xml.required.set(false)
		txt.required.set(false)
		sarif.required.set(true)
	}
}
