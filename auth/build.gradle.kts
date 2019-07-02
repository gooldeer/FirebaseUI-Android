import Config.Plugins.android
import com.android.build.gradle.internal.dsl.TestOptions

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(Config.SdkVersions.compile)
    buildTypes {
        named("release").configure {
            postprocessing {
                consumerProguardFiles("auth-proguard.pro")
            }
        }
    }

    lintOptions {
        disable("UnusedQuantity")
        disable("UnknownNullness")  // TODO fix in future PR
        disable("TypographyQuotes") // Straight versus directional quotes
    }

    testOptions {
        unitTests(closureOf<TestOptions.UnitTestOptions> {
            isIncludeAndroidResources = true
        })
    }
}

dependencies {
    implementation(Config.Libs.Support.design)
    implementation(Config.Libs.Support.customTabs)
    implementation(Config.Libs.Support.constraint)
    implementation(Config.Libs.Misc.materialProgress)

    implementation(Config.Libs.Arch.extensions)
    annotationProcessor(Config.Libs.Arch.compiler)

    api(Config.Libs.Firebase.auth)
    api(Config.Libs.PlayServices.auth)

    compileOnly(Config.Libs.Provider.facebook)
    implementation(project(Config.Libs.Provider.appAuth))
    implementation(Config.Libs.Support.v4) // Needed to override deps
    implementation(Config.Libs.Support.cardView) // Needed to override Facebook
    compileOnly(Config.Libs.Provider.twitter) { isTransitive = true }

    testImplementation(Config.Libs.Test.junit)
    testImplementation(Config.Libs.Test.truth)
    testImplementation(Config.Libs.Test.mockito)
    testImplementation(Config.Libs.Test.robolectric)
    testImplementation(Config.Libs.Provider.facebook)
    testImplementation(Config.Libs.Provider.twitter) { isTransitive = true }

//    debugImplementation(project(":internal:lintchecks"))
}
