./gradlew :clean
./gradlew :generateComposeResClass
./gradlew :generateResourceAccessorsForComposeMain
./gradlew :generateExpectResourceCollectorsForCommonMain
./gradlew :generateActualResourceCollectorsForComposeJvmMain
./gradlew :assembleComposeJvmMainResources
./gradlew :composeJvmRun "-DmainClass=cn.yurin.minecraft_composable_launcher.MainKt" --quiet