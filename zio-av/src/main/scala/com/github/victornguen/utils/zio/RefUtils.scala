package com.github.victornguen.utils.zio

import zio.{Ref, UIO}

private[victornguen] object RefUtils {

  def makeEmptyRef[A]: UIO[Ref[Option[A]]] = Ref.make(Option.empty[A])

}
