package com.github.victornguen.av.storage

import zio.nio.file.{Files, Path}
import zio.{IO, Scope, ULayer, ZIO, ZLayer}

import java.io.IOException

final case class DefaultTempFileStorage() extends TempFileStorage {
  override def createTempFileScoped(suffix: String, prefix: Option[String]): ZIO[Scope, IOException, Path] =
    Files.createTempFileScoped(suffix, prefix)

  override def createTempFile(suffix: String, prefix: Option[String]): IO[IOException, Path] =
    Files.createTempFile(suffix, prefix, List.empty)
}

object DefaultTempFileStorage {
  def makeLayer: ULayer[TempFileStorage] = ZLayer.succeed(DefaultTempFileStorage())
}
