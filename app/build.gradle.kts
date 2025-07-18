import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.application")
	id("kotlin-android")
	id("io.gitlab.arturbosch.detekt").version("1.23.8")
}

android {
	compileSdk = 36

	defaultConfig {
		applicationId = "com.machinerychorus.lifeprogresswallpaper"
		minSdk = 21
		targetSdk = 36
		versionCode = 18
		versionName = "2.6.0"
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
	kotlin {
		compilerOptions {
			jvmTarget = JvmTarget.JVM_11
		}
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	namespace = "com.machinerychorus.lifeprogresswallpaper"
}

dependencies {
	implementation("androidx.core:core-ktx:1.16.0")
	implementation("androidx.appcompat:appcompat:1.7.1")
	implementation("com.google.android.material:material:1.12.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
	implementation("androidx.preference:preference-ktx:1.2.1")
	implementation("com.github.skydoves:colorpickerpreference:2.0.6")

	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.2.1")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

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
