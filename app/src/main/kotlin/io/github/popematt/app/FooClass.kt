package io.github.popematt.app

import io.github.popematt.annotation.FooAnnotation

interface Bar<T: Bar<T>>

class FooClass(@FooAnnotation val bar: Bar<*>)
