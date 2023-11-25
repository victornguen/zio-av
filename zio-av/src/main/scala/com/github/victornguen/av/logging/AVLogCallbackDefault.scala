package com.github.victornguen.av.logging

trait AVLogCallbackDefault[+E] extends AVLogCallback[Any, E] {

  val runtime: zio.Runtime[Any] = zio.Runtime.default

}
