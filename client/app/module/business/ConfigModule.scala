package module.business

object ConfigModule {
    
  def configAllDataTypes : List[String] = {
      ("""省份数据""" :: """城市数据""" :: """医院数据""" :: Nil)
  }  
  
  def configAllMarkets : List[String] = {
      ("""博路定市场""" :: """降压药市场""" :: """ACEI市场""" :: Nil)
  }
}