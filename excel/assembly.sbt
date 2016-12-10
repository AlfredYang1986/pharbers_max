jarName in assembly := "excel-reader_0.1.0.jar"

test in assembly := {}

mainClass in assembly := Some("com.pharbers.aqll.calc.stub.StubMain")

assemblyOption in packageDependency ~= { _.copy(appendContentHash = true) }