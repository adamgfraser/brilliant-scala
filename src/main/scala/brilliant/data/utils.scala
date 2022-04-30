package brilliant

package object data {
  type TODO

  def TODO: Nothing = throw new Error("Unimplemented")

  implicit class AnySyntax(any: AnyRef) {
    def todo: Nothing = TODO
  }
}
