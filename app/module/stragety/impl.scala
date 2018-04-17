package module.stragety

import scala.reflect.ClassTag

object impl {
    def apply[T : ClassTag](implicit tag : ClassTag[T]) = tag.runtimeClass.newInstance().asInstanceOf[T]
}
