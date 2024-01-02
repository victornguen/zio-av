package com.github.victornguen.av.internal

import zio.nio.file.Path

import java.io.File

object Implicits {

  implicit def zioPathToJavaFile(path: Path): File = path.toFile

}
