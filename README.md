
This project will fail to build because of a stack overflow in the `ksp-processor` (when applying it to the `app` subproject).
The stack overflow is caused by infinite recursion, ending with:

```
...
        at com.squareup.kotlinpoet.ksp.KsTypesKt.toTypeName(ksTypes.kt:162)
        at com.squareup.kotlinpoet.ksp.KsTypesKt.toTypeName(ksTypes.kt:66)
        at com.squareup.kotlinpoet.ksp.KsTypesKt.toTypeName(ksTypes.kt:182)
        at com.squareup.kotlinpoet.ksp.KsTypesKt.toTypeName(ksTypes.kt:162)
        at com.squareup.kotlinpoet.ksp.KsTypesKt.toTypeName(ksTypes.kt:66)
        at com.squareup.kotlinpoet.ksp.KsTypesKt.toTypeName(ksTypes.kt:182)
```

However, it will build successfully if you apply the following change to KotlinPoet, and make the
`ksp-processor` subproject depend on a locally built version of `com.squareup:kotlinpoet-ksp` that
includes this change.

```git
diff --git a/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt b/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt
index d3573153..90d1124b 100644
--- a/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt
+++ b/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt
@@ -159,12 +159,13 @@ public fun KSTypeParameter.toTypeVariableName(
 public fun KSTypeArgument.toTypeName(
   typeParamResolver: TypeParameterResolver = TypeParameterResolver.EMPTY
 ): TypeName {
-  val typeName = type?.toTypeName(typeParamResolver) ?: return STAR
+  val type = this.type
+  type ?: return STAR
   return when (variance) {
-    COVARIANT -> WildcardTypeName.producerOf(typeName)
-    CONTRAVARIANT -> WildcardTypeName.consumerOf(typeName)
+    COVARIANT -> WildcardTypeName.producerOf(type.toTypeName(typeParamResolver))
+    CONTRAVARIANT -> WildcardTypeName.consumerOf(type.toTypeName(typeParamResolver))
     Variance.STAR -> STAR
-    INVARIANT -> typeName
+    INVARIANT -> type.toTypeName(typeParamResolver)
   }
 }
 
~
```
