-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.example.medicalapp.**$$serializer { *; }
-keepclassmembers class com.example.medicalapp.* {
    *** Companion;
}
-keepclasseswithmembers class com.example.medicalapp.* {
    kotlinx.serialization.KSerializer serializer(...);
}